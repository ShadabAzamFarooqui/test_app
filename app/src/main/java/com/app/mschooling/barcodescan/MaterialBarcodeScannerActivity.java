package com.app.mschooling.barcodescan;

import android.app.Dialog;
import android.content.Intent;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.mschooling.enrollment.RegisterSchoolActivity;
import com.app.mschooling.enrollment.ValidateRegisterSchoolActivity;
import com.app.mschooling.enrollment.signup.activity.SignUpBaseActivity;
import com.app.mschooling.enrollment.guide.activity.SignupGuideActivity;
import com.app.mschooling.base.activity.BaseActivity;
import com.app.mschooling.com.R;
import com.app.mschooling.utils.AppUser;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.mschooling.transaction.common.api.Status;
import com.mschooling.transaction.response.qrcode.GetQRCodeResponse;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;

public class MaterialBarcodeScannerActivity extends BaseActivity {

    private static final int RC_HANDLE_GMS = 9001;
    private static final String TAG = "MaterialBarcodeScanner";
    private MaterialBarcodeScanner mMaterialBarcodeScanner;
    private MaterialBarcodeScannerBuilder mMaterialBarcodeScannerBuilder;
    private BarcodeDetector barcodeDetector;
    private CameraSourcePreview mCameraSourcePreview;
    private GraphicOverlay<BarcodeGraphic> mGraphicOverlay;
    private SoundPoolPlayer mSoundPoolPlayer;
    private boolean mDetectionConsumed = false;
    private boolean mFlashOn = false;
    private EditText etQrCode;
    LinearLayout guide;
    LinearLayout setup;
    LinearLayout submit;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.barcode_capture);
        try {
            toolbarClick(getString(R.string.scan_enter_qr_code));
            etQrCode = findViewById(R.id.etQrCode);
            guide = findViewById(R.id.guide);
            setup = findViewById(R.id.setup);
            submit = findViewById(R.id.submit);
            guide.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(getApplicationContext(), SignupGuideActivity.class));
                }
            });
            setup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(getApplicationContext(), ValidateRegisterSchoolActivity.class));
                }
            });

            submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (etQrCode.getText().toString().trim().length()==6||etQrCode.getText().toString().trim().length()==10) {
                        apiCallBack(getApiCommonController().getQrCodeDetail(etQrCode.getText().toString()));
                    }else {
                        dialogError(getString(R.string.enter_valid_qr_code));
                    }
                }
            });


        } catch (Exception e) {
            dialogReport("LINE :" + Thread.currentThread().getStackTrace()[2].getLineNumber() + " " + "\nERROR:\n" + e.getMessage());
        }
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onMaterialBarcodeScanner(MaterialBarcodeScanner materialBarcodeScanner) {
        this.mMaterialBarcodeScanner = materialBarcodeScanner;
        mMaterialBarcodeScannerBuilder = mMaterialBarcodeScanner.getMaterialBarcodeScannerBuilder();
        barcodeDetector = mMaterialBarcodeScanner.getMaterialBarcodeScannerBuilder().getBarcodeDetector();
        startCameraSource();
        setupLayout();
    }

    private void setupLayout() {
        final TextView topTextView = (TextView) findViewById(R.id.topText);
        //  assertNotNull(topTextView);
        String topText = mMaterialBarcodeScannerBuilder.getText();
        if (!mMaterialBarcodeScannerBuilder.getText().equals("")) {
            topTextView.setText(topText);
        }
        setupButtons();
        setupCenterTracker();
    }

    private void setupCenterTracker() {
        if (mMaterialBarcodeScannerBuilder.getScannerMode() == MaterialBarcodeScanner.SCANNER_MODE_CENTER) {
            final ImageView centerTracker = (ImageView) findViewById(R.id.barcode_square);
            centerTracker.setImageResource(mMaterialBarcodeScannerBuilder.getTrackerResourceID());
            mGraphicOverlay.setVisibility(View.INVISIBLE);
        }
    }

    private void updateCenterTrackerForDetectedState() {
        if (mMaterialBarcodeScannerBuilder.getScannerMode() == MaterialBarcodeScanner.SCANNER_MODE_CENTER) {
            final ImageView centerTracker = (ImageView) findViewById(R.id.barcode_square);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    centerTracker.setImageResource(mMaterialBarcodeScannerBuilder.getTrackerDetectedResourceID());
                }
            });
        }
    }

    private void setupButtons() {
        final LinearLayout flashOnButton = (LinearLayout) findViewById(R.id.flashIconButton);
        final ImageView flashToggleIcon = (ImageView) findViewById(R.id.flashIcon);
        //assertNotNull(flashOnButton);
        flashOnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mFlashOn) {
                    flashToggleIcon.setBackgroundResource(R.drawable.ic_flash_on_white_24dp);
                    disableTorch();
                } else {
                    flashToggleIcon.setBackgroundResource(R.drawable.ic_flash_off_white_24dp);
                    enableTorch();
                }
                mFlashOn ^= true;
            }
        });
        if (mMaterialBarcodeScannerBuilder.isFlashEnabledByDefault()) {
            flashToggleIcon.setBackgroundResource(R.drawable.ic_call);
        }
    }

    /**
     * Starts or restarts the camera source, if it exists.  If the camera source doesn't exist yet
     * (e.g., because onResume was called before the camera source was created), this will be called
     * again when the camera source is created.
     */
    private void startCameraSource() throws SecurityException {
        // check that the device has play services available.
        mSoundPoolPlayer = new SoundPoolPlayer(this);
        int code = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(
                getApplicationContext());
        if (code != ConnectionResult.SUCCESS) {
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(this, code, RC_HANDLE_GMS);
            dialog.show();
        }
        mGraphicOverlay = (GraphicOverlay<BarcodeGraphic>) findViewById(R.id.graphicOverlay);
        BarcodeGraphicTracker.NewDetectionListener listener = new BarcodeGraphicTracker.NewDetectionListener() {
            @Override
            public void onNewDetection(Barcode barcode) {
                if (!mDetectionConsumed) {
                    mDetectionConsumed = true;
                    Log.d(TAG, "Barcode detected! - " + barcode.displayValue);
                    EventBus.getDefault().postSticky(barcode);
                    updateCenterTrackerForDetectedState();
                    if (mMaterialBarcodeScannerBuilder.isBleepEnabled()) {
                        mSoundPoolPlayer.playShortResource(R.raw.bleep);
                    }
                    mGraphicOverlay.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            finish();
                        }
                    }, 50);
                }
            }
        };
        BarcodeTrackerFactory barcodeFactory = new BarcodeTrackerFactory(mGraphicOverlay, listener, mMaterialBarcodeScannerBuilder.getTrackerColor());
        barcodeDetector.setProcessor(new MultiProcessor.Builder<>(barcodeFactory).build());
        CameraSource mCameraSource = mMaterialBarcodeScannerBuilder.getCameraSource();
        if (mCameraSource != null) {
            try {
                mCameraSourcePreview = (CameraSourcePreview) findViewById(R.id.preview);
                mCameraSourcePreview.start(mCameraSource, mGraphicOverlay);
            } catch (IOException e) {
                Log.e(TAG, "Unable to start camera source.", e);
                mCameraSource.release();
                mCameraSource = null;
            }
        }
    }

    private void enableTorch() throws SecurityException {
        mMaterialBarcodeScannerBuilder.getCameraSource().setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
        try {
            mMaterialBarcodeScannerBuilder.getCameraSource().start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void disableTorch() throws SecurityException {
        mMaterialBarcodeScannerBuilder.getCameraSource().setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
        try {
            mMaterialBarcodeScannerBuilder.getCameraSource().start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        //  EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        // EventBus.getDefault().unregister(this);
        super.onStop();
    }

    /**
     * Stops the camera.
     */
    @Override
    protected void onPause() {
        super.onPause();
        if (mCameraSourcePreview != null) {
            mCameraSourcePreview.stop();
        }
    }

    /**
     * Releases the resources associated with the camera source, the associated detectors, and the
     * rest of the processing pipeline.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isFinishing()) {
            clean();
        }
    }

    private void clean() {
        try {
            EventBus.getDefault().removeStickyEvent(MaterialBarcodeScanner.class);
            if (mCameraSourcePreview != null) {
                mCameraSourcePreview.release();
                mCameraSourcePreview = null;
            }
            if (mSoundPoolPlayer != null) {
                mSoundPoolPlayer.release();
                mSoundPoolPlayer = null;
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Subscribe
    public void getQrCodeDetail(GetQRCodeResponse response) {
        try {
            if (response.getStatus().value().equals(Status.SUCCESS.value())) {
                AppUser.getInstance().setQrCodeDetailResponse(response);
                startActivity(new Intent(getApplicationContext(), SignUpBaseActivity.class));
                finish();
            } else {
                getDialogHelp(response.getMessage().getMessage());
            }
        } catch (Exception e) {
            dialogReport("LINE :" + Thread.currentThread().getStackTrace()[2].getLineNumber() + "\n" + e.getMessage());
        }

    }


    public void getDialogHelp(String msg) {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.help_dialog);
        dialog.setCancelable(false);
        TextView message = (TextView) dialog.findViewById(R.id.message);
        TextView help = (TextView) dialog.findViewById(R.id.help);
        TextView ok = (TextView) dialog.findViewById(R.id.ok);
        message.setText(msg);
        help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), SignupGuideActivity.class));
                dialog.dismiss();
            }
        });
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }


}