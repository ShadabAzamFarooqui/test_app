package com.app.mschooling.unused.ocr.activity;


import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.media.CameraProfile;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.StatFs;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuItem;
import android.view.OrientationEventListener;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.app.mschooling.base.activity.BaseActivity;
import com.app.mschooling.com.R;
import com.app.mschooling.utils.Preferences;
import com.app.mschooling.utils.progress_dialog.MyProgressDialog;
import com.app.mschooling.utils.progress_dialog.MyProgressDialogScan;
import com.app.mschooling.utils.progress_dialog.MyProgressDialogUploadMultiple;
import com.mschooling.transaction.common.api.Common;
import com.mschooling.transaction.common.api.Status;
import com.mschooling.transaction.response.resource.AddResourceResponse;

import org.greenrobot.eventbus.Subscribe;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class DigitalCameraActivity extends BaseActivity implements SurfaceHolder.Callback, View.OnClickListener {


    String urlFront;
    String urlBack;

    List<Uri> uriList;
    private SurfaceHolder surfaceHolder;
    private Camera mCamera;
    private Handler customHandler = new Handler();
    int flag = 0;
    private File tempFile = null;
    private File file_first;
    private Camera.PictureCallback jpegCallback;
    int MAX_VIDEO_SIZE_UPLOAD = 25;
    SharedPreferences mSharedPreferences;
    SharedPreferences.Editor mEditor;
    ProgressDialog dialog;
    MyProgressDialog mProgressDialog;
    private ImageView imgPreview;
    SurfaceView surfaceView;
    RelativeLayout progressB;
    RelativeLayout previewRelativeMain;
    ImageView uploadpan;
    private Uri filePath_adhar_f;
    private Uri filePath_adhar_b;
    private Uri filePath_pan;
    private Uri filePath_cheque;
    private Uri filePath_coi;
    FrameLayout cameraFrameLayoutMain;
    ImageView close_box;
    TextView docType;
    TextView review_msg;
    RelativeLayout firstRelative;
    MyProgressDialogScan forecasterDialogue;
    TextView imageView_front;
    TextView imageView_back;
    TextView imageView_cancel;
    TextView imageView_Pancard;
    TextView imageView_cheque;
    TextView imageView_cio;
    private static byte[] imageBytePic;
    private static byte[] imageBytePic1;
    private final int PICK_IMAGE_ADHAR_BACK_REQUEST = 71;
    private final int PICK_IMAGE_PANCARD_REQUEST = 72;
    private final int PICK_IMAGE_ADHAR_FRONT_REQUEST = 73;
    private final int PICK_IMAGE_CHEQUE_REQUEST = 74;
    private final int PICK_IMAGE_COI_REQUEST = 75;
    private final int IMAGE_SIZE = 1024;
    private File folder = null;
    private SavePicTask savePicTask;
    private int mPhotoAngle = 90;
    private SurfaceView imgSurface;
    private ImageView imgCapture;
    private ImageView imgFlashOnOff;
    private ImageView imgSwipeCamera;
    private RunTimePermission runTimePermission;
    private TextView textCounter;
    private TextView hintTextView;
    private TextView hintTextViewAbove;
    int flashType = 1;
    private long timeInMilliseconds = 0L;
    private long startTime = SystemClock.uptimeMillis();
    private long updatedTime = 0L;
    private long timeSwapBuff = 0L;
    private Runnable updateTimerThread = new Runnable() {
        public void run() {
            timeInMilliseconds = SystemClock.uptimeMillis() - startTime;
            updatedTime = timeSwapBuff + timeInMilliseconds;
            int secs = (int) (updatedTime / 1000L);
            int mins = secs / 60;
            int hrs = mins / 60;
            secs %= 60;
            textCounter.setText(String.format("%02d", mins) + ":" + String.format("%02d", secs));
            customHandler.postDelayed(this, 0L);
        }
    };
    OrientationEventListener myOrientationEventListener;
    int iOrientation = 0;
    int mOrientation = 90;

    public DigitalCameraActivity() {
    }

    protected void onResume() {
        super.onResume();

        try {
            if (this.myOrientationEventListener != null) {
                this.myOrientationEventListener.enable();
            }
        } catch (Exception var2) {
            var2.printStackTrace();
        }

    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (this.runTimePermission != null) {
            this.runTimePermission.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(1);
        this.getWindow().setFlags(1024, 1024);
        this.setContentView(R.layout.innodigital_camera_activity);
        this.InitActivity();
        uriList = new ArrayList<>();
    }

    public void InitActivity() {
        this.forecasterDialogue = new MyProgressDialogScan(this);
        this.forecasterDialogue.setCancelable(false);
        this.mSharedPreferences = this.getSharedPreferences("demandForeCasting", 0);
        this.uploadpan = (ImageView) this.findViewById(R.id.upload_tv);
        this.progressB = (RelativeLayout) this.findViewById(R.id.loadingPanel);
        this.cameraFrameLayoutMain = (FrameLayout) this.findViewById(R.id.cameraframe_main);
        this.previewRelativeMain = (RelativeLayout) this.findViewById(R.id.image_preview_pan);
        this.cameraFrameLayoutMain.setVisibility(View.VISIBLE);
        this.previewRelativeMain.setVisibility(View.GONE);
        this.imgPreview = (ImageView) this.findViewById(R.id.imgShow);
        this.imageView_front = (TextView) this.findViewById(R.id.front_click_ok);
        this.imageView_back = (TextView) this.findViewById(R.id.final_click_ok);
        this.imageView_Pancard = (TextView) this.findViewById(R.id.pancard_click);
        this.imageView_cheque = (TextView) this.findViewById(R.id.cheque_click);
        this.imageView_cio = (TextView) this.findViewById(R.id.cio_click);
        this.close_box = (ImageView) this.findViewById(R.id.close_box);
        this.review_msg = (TextView) this.findViewById(R.id.review_msg);
        Log.d("AType", this.getIntent().getStringExtra("docType"));
        this.imageView_cancel = (TextView) this.findViewById(R.id.undo_click);
        this.runTimePermission = new RunTimePermission(this);
        this.runTimePermission.requestPermission(new String[]{"android.permission.CAMERA", "android.permission.READ_EXTERNAL_STORAGE", "android.permission.WRITE_EXTERNAL_STORAGE"}, new RunTimePermission.RunTimePermissionListener() {
            public void permissionGranted() {
                initControls();
                folder = new File(Environment.getExternalStorageDirectory() + "/mSchooling");
                if (!folder.exists()) {
                    folder.mkdirs();
                }

                captureImageCallback();
                if (mCamera != null) {
                    Camera.CameraInfo info = new Camera.CameraInfo();
                    if (info.facing == 1) {
                        imgFlashOnOff.setVisibility(View.GONE);
                    }
                }

            }

            public void permissionDenied() {
            }
        });
        this.uploadpan.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (getIntent().getStringExtra("docType").equalsIgnoreCase("AadhaarFront")) {
                    chooseAdhaarFron();
                } else if (getIntent().getStringExtra("docType").equalsIgnoreCase("AadhaarBack")) {
                    chooseAdharBack();
                } else if (getIntent().getStringExtra("docType").equalsIgnoreCase("Pancard")) {
                    choosePancard();
                } else if (getIntent().getStringExtra("docType").equalsIgnoreCase("Cheque")) {
                    chooseCheque();
                } else if (getIntent().getStringExtra("docType").equalsIgnoreCase("COI")) {
                    chooseCOI();
                }

            }
        });
    }

    private void cancelSavePicTaskIfNeed() {
        if (this.savePicTask != null && this.savePicTask.getStatus() == AsyncTask.Status.RUNNING) {
            this.savePicTask.cancel(true);
        }

    }

    public String saveToSDCard(byte[] data, int rotation) throws IOException {
        String imagePath = "";

        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeByteArray(data, 0, data.length, options);
            DisplayMetrics metrics = new DisplayMetrics();
            this.getWindowManager().getDefaultDisplay().getMetrics(metrics);
            int reqHeight = metrics.heightPixels;
            int reqWidth = metrics.widthPixels;
            options.inSampleSize = this.calculateInSampleSize(options, reqWidth, reqHeight);
            options.inJustDecodeBounds = false;
            Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length, options);
            if (rotation != 0) {
                Matrix mat = new Matrix();
                mat.postRotate((float) rotation);
                Bitmap bitmap1 = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), mat, true);
                if (bitmap != bitmap1) {
                    bitmap.recycle();
                }

                imagePath = this.getSavePhotoLocal(bitmap1);
                if (bitmap1 != null) {
                    bitmap1.recycle();
                }
            } else {
                imagePath = this.getSavePhotoLocal(bitmap);
                if (bitmap != null) {
                    bitmap.recycle();
                }
            }
        } catch (Exception var11) {
            var11.printStackTrace();
        }

        return imagePath;
    }

    public int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        int height = options.outHeight;
        int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            if (width > height) {
                inSampleSize = Math.round((float) height / (float) reqHeight);
            } else {
                inSampleSize = Math.round((float) width / (float) reqWidth);
            }
        }

        return inSampleSize;
    }

    private String getSavePhotoLocal(Bitmap bitmap) {
        String path = "";

        try {
            File file = new File(this.folder.getAbsolutePath(), "wc" + System.currentTimeMillis() + ".jpg");
            path = file.getAbsolutePath();

            try {
                OutputStream output = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, output);
                output.flush();
                output.close();
                path = file.getAbsolutePath();
            } catch (Exception var6) {
                var6.printStackTrace();
            }
        } catch (Exception var7) {
            var7.printStackTrace();
        }

        return path;
    }

    private void captureImageCallback() {
        this.surfaceHolder = this.imgSurface.getHolder();
        this.surfaceHolder.addCallback(this);
        this.surfaceHolder.setType(3);
        this.jpegCallback = new Camera.PictureCallback() {
            public void onPictureTaken(byte[] data, Camera camera) {
                refreshCamera();
                cancelSavePicTaskIfNeed();
                savePicTask = new SavePicTask(data, 90);
                savePicTask.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, new Void[0]);
            }
        };
    }

    private void initControls() {
        this.imgSurface = (SurfaceView) this.findViewById(R.id.imgSurface);
        this.imgCapture = (ImageView) this.findViewById(R.id.imgCapture);
        this.imgFlashOnOff = (ImageView) this.findViewById(R.id.imgFlashOnOff);
        this.hintTextView = (TextView) this.findViewById(R.id.hintTextView);
        this.hintTextViewAbove = (TextView) this.findViewById(R.id.texthintabove);
        if (this.getIntent().getStringExtra("docType").equalsIgnoreCase("Pancard")) {
            Log.d("docTypeContent", this.getIntent().getStringExtra("docType"));
            this.pancradCameraCapture();
        } else if (this.getIntent().getStringExtra("docType").equalsIgnoreCase("AadhaarFront")) {
            this.activeCameraCapture();
        } else if (this.getIntent().getStringExtra("docType").equalsIgnoreCase("AadhaarBack")) {
            this.activeCameraCapture2();
        } else if (this.getIntent().getStringExtra("docType").equalsIgnoreCase("Cheque")) {
            this.chequeCameraCapture();
        } else if (this.getIntent().getStringExtra("docType").equalsIgnoreCase("COI")) {
            this.coiCameraCapture();
        }

//        this.imgFlashOnOff.setOnClickListener(this);
        this.imgFlashOnOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flashToggle();
            }
        });

    }

    public void onBackPressed() {
        super.onBackPressed();
        this.cancelSavePicTaskIfNeed();
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case 1:
                this.flashToggle();
            default:
        }
    }

    private void flashToggle() {
        if (this.flashType == 1) {
            this.flashType = 2;
        } else if (this.flashType == 2) {
            this.flashType = 3;
        } else if (this.flashType == 3) {
            this.flashType = 1;
        }

        this.refreshCamera();
    }

    private void captureImage() {
        this.progressB.setVisibility(View.VISIBLE);
        this.mCamera.autoFocus(new Camera.AutoFocusCallback() {
            public void onAutoFocus(boolean success, Camera camera) {
                if (success) {
                    try {
                        camera.takePicture((Camera.ShutterCallback) null, (Camera.PictureCallback) null, jpegCallback);
                        progressB.setVisibility(View.GONE);
                    } catch (Exception e) {
                        progressB.setVisibility(View.GONE);
                    }

                }

            }
        });
        this.inActiveCameraCapture();
    }

    public void refreshCamera() {
        if (this.surfaceHolder.getSurface() != null) {
            try {
                try {
                    this.mCamera.stopPreview();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Camera.Parameters param = this.mCamera.getParameters();
                param.setFocusMode("auto");
                if (this.flag == 0) {
                    if (this.flashType == 1) {
                        param.setFlashMode("auto");
                        this.imgFlashOnOff.setImageResource(R.drawable.ic_flash_auto);
                    } else if (this.flashType == 2) {
                        param.setFlashMode("on");
                        Camera.Parameters params = null;
                        if (this.mCamera != null) {
                            params = this.mCamera.getParameters();
                            if (params != null) {
                                List<String> supportedFlashModes = params.getSupportedFlashModes();
                                if (supportedFlashModes != null) {
                                    if (supportedFlashModes.contains("torch")) {
                                        param.setFlashMode("torch");
                                    } else if (supportedFlashModes.contains("on")) {
                                        param.setFlashMode("on");
                                    }
                                }
                            }
                        }

                        this.imgFlashOnOff.setImageResource(R.drawable.ic_flash_on);
                    } else if (this.flashType == 3) {
                        param.setFlashMode("off");
                        this.imgFlashOnOff.setImageResource(R.drawable.ic_flash_off);
                    }
                }

                this.refrechCameraPriview(param);
            } catch (Exception var4) {
                var4.printStackTrace();
            }
        }
    }

    private boolean safeCameraOpen(int id) {
        boolean qOpened = false;

        try {
            this.releaseCameraAndPreview();
            this.mCamera = Camera.open(id);
            qOpened = this.mCamera != null;
        } catch (Exception var4) {
            Log.e(this.getString(R.string.app_name), "failed to open Camera");
            var4.printStackTrace();
        }

        return qOpened;
    }

    private void releaseCameraAndPreview() {
        if (this.mCamera != null) {
            this.mCamera.release();
            this.mCamera = null;
        }

    }

    private void refrechCameraPriview(Camera.Parameters param) {
        try {
            Camera.Parameters params = this.mCamera.getParameters();
            params.setFocusMode("auto");
            this.mCamera.setParameters(params);
            this.mCamera.setParameters(param);
            setCameraDisplayOrientation(this, 0, this.mCamera);
            this.mCamera.setPreviewDisplay(this.surfaceHolder);
            this.mCamera.startPreview();
        } catch (Exception var3) {
            var3.printStackTrace();
        }

    }

    public static void setCameraDisplayOrientation(Activity activity, int cameraId, Camera camera) {
        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(cameraId, info);
        int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
        int degrees = 0;
        switch (rotation) {
            case 0:
                degrees = 0;
                break;
            case 1:
                degrees = 90;
                break;
            case 2:
                degrees = 180;
                break;
            case 3:
                degrees = 270;
        }

        int result;
        if (info.facing == 1) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;
        } else {
            result = (info.orientation - degrees + 360) % 360;
        }

        camera.setDisplayOrientation(result);
    }

    private void setCameraParams(Camera.Parameters params) {
        params.set("contrast", 100);
        params.set("brightness", 50);
        params.set("saturation", 100);
        params.set("sharpness", 100);
        params.setPictureFormat(256);
        params.set("jpeg-quality", 100);
        params.setJpegQuality(CameraProfile.getJpegEncodingQualityParameter(0, 2));
        if (params.isZoomSupported()) {
            params.setZoom(0);
        }

        this.setPictureSize(params);
        this.mCamera.setParameters(params);
    }

    private void setPictureSize(Camera.Parameters param) {
        Camera.Size previewSize = (Camera.Size) param.getSupportedPreviewSizes().get(0);
        Iterator var3 = param.getSupportedPreviewSizes().iterator();

        while (var3.hasNext()) {
            Camera.Size size = (Camera.Size) var3.next();
            if (size.width >= 1024 && size.height >= 1024) {
                previewSize = size;
                break;
            }
        }

        param.setPreviewSize(previewSize.width, previewSize.height);
        Camera.Size pictureSize = (Camera.Size) param.getSupportedPictureSizes().get(0);
        Iterator var7 = param.getSupportedPictureSizes().iterator();

        while (var7.hasNext()) {
            Camera.Size size = (Camera.Size) var7.next();
            if (size.width == previewSize.width && size.height == previewSize.height) {
                pictureSize = size;
                break;
            }
        }

        param.setPictureSize(pictureSize.width, pictureSize.height);
    }

    private Camera.Size getOptimalPreviewSize(List<Camera.Size> sizes, int w, int h) {
        double ASPECT_TOLERANCE = 0.1D;
        double targetRatio = (double) h / (double) w;
        if (sizes == null) {
            return null;
        } else {
            Camera.Size optimalSize = null;
            double minDiff = 1.7976931348623157E308D;
            int targetHeight = h;
            Iterator var12 = sizes.iterator();

            Camera.Size size;
            while (var12.hasNext()) {
                size = (Camera.Size) var12.next();
                double ratio = (double) size.height / (double) size.width;
                if (Math.abs(ratio - targetRatio) <= 0.1D && (double) Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = (double) Math.abs(size.height - targetHeight);
                }
            }

            if (optimalSize == null) {
                minDiff = 1.7976931348623157E308D;
                var12 = sizes.iterator();

                while (var12.hasNext()) {
                    size = (Camera.Size) var12.next();
                    if ((double) Math.abs(size.height - targetHeight) < minDiff) {
                        optimalSize = size;
                        minDiff = (double) Math.abs(size.height - targetHeight);
                    }
                }
            }

            return optimalSize;
        }
    }

    public void surfaceCreated(SurfaceHolder arg0) {
        try {
            releaseCameraAndPreview();
            if (this.flag == 0) {
                this.mCamera = Camera.open(0);
            } else {
                this.mCamera = Camera.open(1);
            }
        } catch (RuntimeException var6) {
            var6.printStackTrace();
            return;
        }

        try {
            Camera.Parameters param = this.mCamera.getParameters();
            param.setFocusMode("auto");
            this.setCameraParams(this.mCamera.getParameters());
            setCameraDisplayOrientation(this, 0, this.mCamera);
            this.mCamera.setPreviewDisplay(this.surfaceHolder);
            this.mCamera.startPreview();
            if (this.flashType == 1) {
                param.setFlashMode("auto");
                this.imgFlashOnOff.setImageResource(R.drawable.ic_flash_auto);
            } else if (this.flashType == 2) {
                param.setFlashMode("on");
                Camera.Parameters params = null;
                if (this.mCamera != null) {
                    params = this.mCamera.getParameters();
                    if (params != null) {
                        List<String> supportedFlashModes = params.getSupportedFlashModes();
                        if (supportedFlashModes != null) {
                            if (supportedFlashModes.contains("torch")) {
                                param.setFlashMode("torch");
                            } else if (supportedFlashModes.contains("on")) {
                                param.setFlashMode("on");
                            }
                        }
                    }
                }

                this.imgFlashOnOff.setImageResource(R.drawable.ic_flash_on);
            } else if (this.flashType == 3) {
                param.setFlashMode("off");
                this.imgFlashOnOff.setImageResource(R.drawable.ic_flash_off);
            }

        } catch (Exception var5) {
            var5.printStackTrace();
        }
    }

    public void surfaceDestroyed(SurfaceHolder arg0) {
        try {
            try {
                this.mCamera.stopPreview();
                this.mCamera.release();
                this.mCamera = null;
            } catch (Exception e) {
                e.printStackTrace();
            }

        } catch (Exception var3) {
            var3.printStackTrace();
        }

    }

    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
        this.refreshCamera();
    }

    private void scaleUpAnimation() {
        ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(this.imgCapture, "scaleX", new float[]{2.0F});
        ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(this.imgCapture, "scaleY", new float[]{2.0F});
        scaleDownX.setDuration(100L);
        scaleDownY.setDuration(100L);
        AnimatorSet scaleDown = new AnimatorSet();
        scaleDown.play(scaleDownX).with(scaleDownY);
        scaleDownX.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                View p = (View) imgCapture.getParent();
                p.invalidate();
            }
        });
        scaleDown.start();
    }

    private void scaleDownAnimation() {
        ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(this.imgCapture, "scaleX", new float[]{1.0F});
        ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(this.imgCapture, "scaleY", new float[]{1.0F});
        scaleDownX.setDuration(100L);
        scaleDownY.setDuration(100L);
        AnimatorSet scaleDown = new AnimatorSet();
        scaleDown.play(scaleDownX).with(scaleDownY);
        scaleDownX.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                View p = (View) imgCapture.getParent();
                p.invalidate();
            }
        });
        scaleDown.start();
    }

    protected void onPause() {
        super.onPause();

        try {
            if (this.customHandler != null) {
                this.customHandler.removeCallbacksAndMessages((Object) null);
            }

            if (this.myOrientationEventListener != null) {
                this.myOrientationEventListener.enable();
            }
        } catch (Exception var2) {
            var2.printStackTrace();
        }

    }

    private void activeCameraCapture() {
        this.hintTextViewAbove.setText(R.string.aadhaar_first);
        this.hintTextView.setText(R.string.place_adhaar_card_inside_the_box);
        if (this.imgCapture != null) {
            this.imgCapture.setAlpha(1.0F);
            this.imgCapture.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                   /* if (isSpaceAvailable()) {
                        captureImage();
                    } else {
                        getDialog("Memory is not available");
                    }*/

                    captureImage();

                }
            });
        }

    }

    private void pancradCameraCapture() {
        this.hintTextViewAbove.setText(R.string.pan_info);
        this.hintTextView.setText(R.string.place_pancard_inside_the_box);
        if (this.imgCapture != null) {
            this.imgCapture.setAlpha(1.0F);
            this.imgCapture.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                   /* if (isSpaceAvailable()) {
                        captureImage();
                    } else {
                        getDialog("Memory is not available");
                    }*/

                    captureImage();

                }
            });
        }

    }

    private void chequeCameraCapture() {
        this.hintTextViewAbove.setText(R.string.cheque_info);
        this.hintTextView.setText(R.string.focuse_camera_account_number_for_better_result);
        if (this.imgCapture != null) {
            this.imgCapture.setAlpha(1.0F);
            this.imgCapture.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                   /* if (isSpaceAvailable()) {
                        captureImage();
                    } else {
                        getDialog("Memory is not available");
                    }*/


                    captureImage();

                }
            });
        }

    }

    private void coiCameraCapture() {
        this.hintTextViewAbove.setText(R.string.cio_info);
        this.hintTextView.setText(R.string.focuse_camera_on_cin_for_better_result);
        if (this.imgCapture != null) {
            this.imgCapture.setAlpha(1.0F);
            this.imgCapture.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    /*if (isSpaceAvailable()) {
                        captureImage();
                    } else {
                        getDialog("Memory is not available");
                    }*/


                    captureImage();

                }
            });
        }

    }

    private void activeCameraCapture2() {
        if (this.imgCapture != null) {
            this.hintTextViewAbove.setText(R.string.aadhaar_back);
            this.hintTextView.setText(R.string.place_adhaar_card_backside_the_box);
            this.imgCapture.setAlpha(1.0F);
            this.imgCapture.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    /*if (isSpaceAvailable()) {
                        captureImage();
                    } else {
                        getDialog("Memory is not available");
                    }*/


                    captureImage();

                }
            });
        }

    }

    private void inActiveCameraCapture() {
        if (this.imgCapture != null) {
            this.imgCapture.setAlpha(0.5F);
            this.imgCapture.setOnClickListener((View.OnClickListener) null);
        }

    }

    public int getFreeSpacePercantage() {
        int percantage = (int) (this.freeMemory() * 100.0D / this.totalMemory());
        int modValue = percantage % 5;
        return percantage - modValue;
    }

    public double totalMemory() {
        StatFs stat = new StatFs(Environment.getExternalStorageDirectory().getPath());
        double sdAvailSize = (double) stat.getBlockCount() * (double) stat.getBlockSize();
        return sdAvailSize / 1.073741824E9D;
    }

    public double freeMemory() {
        StatFs stat = new StatFs(Environment.getExternalStorageDirectory().getPath());
        double sdAvailSize = (double) stat.getAvailableBlocks() * (double) stat.getBlockSize();
        return sdAvailSize / 1.073741824E9D;
    }

    public boolean isSpaceAvailable() {
        return this.getFreeSpacePercantage() >= 1;
    }

    private int normalize(int degrees) {
        if (degrees <= 315 && degrees > 45) {
            if (degrees > 45 && degrees <= 135) {
                return 90;
            } else if (degrees > 135 && degrees <= 225) {
                return 180;
            } else if (degrees > 225 && degrees <= 315) {
                return 270;
            } else {
                throw new RuntimeException("Error....");
            }
        } else {
            return 0;
        }
    }

    private int getPhotoRotation() {
        int orientation = this.mPhotoAngle;
        Camera.CameraInfo info = new Camera.CameraInfo();
        if (this.flag == 0) {
            Camera.getCameraInfo(0, info);
        } else {
            Camera.getCameraInfo(1, info);
        }

        int rotation;
        if (info.facing == 1) {
            rotation = (info.orientation - orientation + 360) % 360;
        } else {
            rotation = (info.orientation + orientation) % 360;
        }

        return rotation;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 16908332:
                this.onBackPressed();
                return true;
            default:
                return false;
        }
    }

    private void choosePancard() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction("android.intent.action.GET_CONTENT");
        this.startActivityForResult(Intent.createChooser(intent, "Select Pancard"), 72);
    }

    private void chooseCheque() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction("android.intent.action.GET_CONTENT");
        this.startActivityForResult(Intent.createChooser(intent, "Select Cheque"), 74);
    }

    private void chooseCOI() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction("android.intent.action.GET_CONTENT");
        this.startActivityForResult(Intent.createChooser(intent, "Select COI"), 75);
    }

    Bitmap bitmap;

    @SuppressLint("WrongConstant")
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("RequestCodeMain", String.valueOf(requestCode));

        if (requestCode == 73 && resultCode == -1 && data != null && data.getData() != null) {
            this.filePath_adhar_f = data.getData();
            this.cameraFrameLayoutMain.setVisibility(View.GONE);
            this.review_msg.setText(R.string.aadhaar_front_review);
            this.previewRelativeMain.setVisibility(View.VISIBLE);

            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), this.filePath_adhar_f);
                Log.d("BitMapImage", String.valueOf(bitmap));
                this.imgPreview.setImageBitmap(bitmap);
            } catch (IOException var12) {
                var12.printStackTrace();
            }

            this.imageView_front.setVisibility(View.VISIBLE);
            this.imageView_back.setVisibility(View.GONE);
            this.imageView_Pancard.setVisibility(View.GONE);
            this.imageView_cheque.setVisibility(View.GONE);
            this.imageView_cio.setVisibility(View.GONE);
            this.imageView_cancel.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    InitActivity();
                }
            });
            this.close_box.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    InitActivity();
                }
            });
            this.imageView_front.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    mEditor = mSharedPreferences.edit();
                    mEditor.putString("frontAadharUri", String.valueOf(filePath_adhar_f));
                    mEditor.apply();
                    getIntent().putExtra("docType", "AadhaarBack");
                    InitActivity();
                }
            });
        }

        if (requestCode == 71 && resultCode == -1 && data != null && data.getData() != null) {
            this.filePath_adhar_b = data.getData();
            this.cameraFrameLayoutMain.setVisibility(View.GONE);
            this.review_msg.setText(R.string.aadhaar_back_review);
            this.previewRelativeMain.setVisibility(View.VISIBLE);
            final Uri fileUri_b = this.filePath_adhar_b;
            String mImageUri = this.mSharedPreferences.getString("frontAadharUri", (String) null);
            final Uri fileUri_f = Uri.parse(mImageUri);
            Log.d("FirstUriTest", String.valueOf(fileUri_f));

            try {
                Bitmap bitmap2 = MediaStore.Images.Media.getBitmap(this.getContentResolver(), this.filePath_adhar_b);
                this.imgPreview.setImageBitmap(bitmap2);
            } catch (IOException var11) {
                var11.printStackTrace();
            }

            this.imageView_front.setVisibility(View.GONE);
            this.imageView_back.setVisibility(View.VISIBLE);
            this.imageView_Pancard.setVisibility(View.GONE);
            this.imageView_cheque.setVisibility(View.GONE);
            this.imageView_cio.setVisibility(View.GONE);
            this.imageView_cancel.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    InitActivity();
                }
            });
            this.close_box.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    InitActivity();
                }
            });
            this.imageView_back.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    Bitmap bitmapb = null;
                    Bitmap bitmapf = null;

                    try {
                        bitmapb = scaleBitmapDown(MediaStore.Images.Media.getBitmap(getContentResolver(), filePath_adhar_b), 800);
                        bitmapf = scaleBitmapDown(MediaStore.Images.Media.getBitmap(getContentResolver(), fileUri_f), 800);
                    } catch (IOException var5) {
                        var5.printStackTrace();
                    }


                    if (fileUri_f != null && fileUri_b != null) {
                        uriList.clear();
                        uriList.add(fileUri_f);
                        uriList.add(fileUri_b);
                        uploadImageAdhaar();
                    } else if (fileUri_f != null && fileUri_b == null) {
                        uploadImageAdhaarModified(fileUri_f, fileUri_b, "G", "C");
                    } else if (fileUri_f == null && fileUri_b != null) {
                        uploadImageAdhaarModified(fileUri_f, fileUri_b, "C", "G");
                    }


                }
            });
        }

        if (requestCode == 72 && resultCode == -1 && data != null && data.getData() != null) {
            this.filePath_pan = data.getData();
            this.cameraFrameLayoutMain.setVisibility(View.GONE);
            this.review_msg.setText(R.string.pancard_review);
            this.previewRelativeMain.setVisibility(View.VISIBLE);

            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), this.filePath_pan);
                Log.d("BitMapImage", String.valueOf(bitmap));
                this.imgPreview.setImageBitmap(bitmap);
            } catch (IOException var10) {
                var10.printStackTrace();
            }

            this.imageView_front.setVisibility(View.GONE);
            this.imageView_back.setVisibility(View.GONE);
            this.imageView_Pancard.setVisibility(View.VISIBLE);
            this.imageView_cheque.setVisibility(View.GONE);
            this.imageView_cio.setVisibility(View.GONE);
            this.imageView_cancel.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    InitActivity();
                }
            });
            this.close_box.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    InitActivity();
                }
            });
            this.imageView_Pancard.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inSampleSize = 8;
                    Bitmap bitmap = null;

                    try {
                        bitmap = scaleBitmapDown(MediaStore.Images.Media.getBitmap(getContentResolver(), filePath_pan), 800);
                    } catch (IOException var5) {
                        var5.printStackTrace();
                    }

                    uploadImageUri(filePath_pan);
                }
            });
        }

        if (requestCode == 74 && resultCode == -1 && data != null && data.getData() != null) {
            this.filePath_cheque = data.getData();
            this.cameraFrameLayoutMain.setVisibility(View.GONE);
            this.review_msg.setText(R.string.cheque_review);
            this.previewRelativeMain.setVisibility(View.VISIBLE);
            bitmap = null;

            try {
                bitmap = this.scaleBitmapDown(MediaStore.Images.Media.getBitmap(this.getContentResolver(), this.filePath_cheque), 800);
                Log.d("BitMapImage", String.valueOf(bitmap));
                this.imgPreview.setImageBitmap(bitmap);
            } catch (IOException var9) {
                var9.printStackTrace();
            }

            this.imageView_front.setVisibility(View.GONE);
            this.imageView_back.setVisibility(View.GONE);
            this.imageView_Pancard.setVisibility(View.GONE);
            this.imageView_cheque.setVisibility(View.VISIBLE);
            this.imageView_cio.setVisibility(View.GONE);
            this.imageView_cancel.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    InitActivity();
                }
            });
            this.close_box.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    InitActivity();
                }
            });
            this.imageView_cheque.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    uploadImageUriCheque(bitmap);
                }
            });
        }

        if (requestCode == 75 && resultCode == -1 && data != null && data.getData() != null) {
            this.filePath_coi = data.getData();
            this.cameraFrameLayoutMain.setVisibility(View.GONE);
            this.review_msg.setText(R.string.coi_review);
            this.previewRelativeMain.setVisibility(View.VISIBLE);
            bitmap = null;

            try {
                bitmap = this.scaleBitmapDown(MediaStore.Images.Media.getBitmap(this.getContentResolver(), this.filePath_coi), 800);
                Log.d("BitMapImage", String.valueOf(bitmap));
                this.imgPreview.setImageBitmap(bitmap);
            } catch (IOException var8) {
                var8.printStackTrace();
            }

            this.imageView_front.setVisibility(View.GONE);
            this.imageView_back.setVisibility(View.GONE);
            this.imageView_Pancard.setVisibility(View.GONE);
            this.imageView_cheque.setVisibility(View.GONE);
            this.imageView_cio.setVisibility(View.VISIBLE);
            this.imageView_cancel.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    InitActivity();
                }
            });
            this.close_box.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    InitActivity();
                }
            });
            this.imageView_cio.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    uploadImageUriCOI(bitmap);
                }
            });
        }

    }

    private void chooseAdhaarFron() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction("android.intent.action.GET_CONTENT");
        this.startActivityForResult(Intent.createChooser(intent, getString(R.string.select_aadhaar_front)), 73);
    }

    private void chooseAdharBack() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction("android.intent.action.GET_CONTENT");
        this.startActivityForResult(Intent.createChooser(intent, getString(R.string.select_adhaar_back)), 71);
    }

    private void uploadImageUri(Uri uri) {
        File file = new File(uri.toString());
        MultipartBody.Part filePart = MultipartBody.Part.createFormData("file", file.getName(),
                RequestBody.create(MediaType.parse("application/image"), file));

        if (Preferences.getInstance(getApplicationContext()).getROLE().equals(Common.Role.ADMIN.value())) {
            if ("admin".equals(getIntent().getStringExtra("type"))){
                apiCallBackWithout(getApiCommonController().uploadResource(Common.DocType.A_PAN,  filePart));
            }else {
                apiCallBackWithout(getApiCommonController().uploadResourceAdmin(Common.DocType.F_PAN, getIntent().getStringExtra("id"), filePart));
            }
        } else {
            apiCallBackWithout(getApiCommonController().uploadResource(Common.DocType.F_PAN, filePart));
        }
        MyProgressDialogUploadMultiple.getInstance(this).show();
        MyProgressDialogUploadMultiple.setCount("1", "1");
    }

    private void uploadImageUriCheque(Bitmap bitmap) {
        this.forecasterDialogue.show();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 30, byteArrayOutputStream);
        imageBytePic = byteArrayOutputStream.toByteArray();
        String image = Base64.encodeToString(imageBytePic, 0);
        String urlStr = "https://ocrapi-100.appspot.com/api/cheque/extraction/android/";
        JSONObject jsonObject = new JSONObject();


    }

    private void uploadImageUriCOI(Bitmap bitmap) {
        this.forecasterDialogue.show();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 30, byteArrayOutputStream);
        imageBytePic = byteArrayOutputStream.toByteArray();
        String image = Base64.encodeToString(imageBytePic, 0);
        String urlStr = "https://ocrapi-100.appspot.com/api/coi/extraction/android/";
        JSONObject jsonObject = new JSONObject();


    }

    private Bitmap scaleBitmapDown(Bitmap bitmap, int maxDimension) {
        int originalWidth = bitmap.getWidth();
        int originalHeight = bitmap.getHeight();
        int resizedWidth = maxDimension;
        int resizedHeight = maxDimension;
        if (originalHeight > originalWidth) {
            resizedHeight = maxDimension;
            resizedWidth = (int) ((float) maxDimension * (float) originalWidth / (float) originalHeight);
        } else if (originalWidth > originalHeight) {
            resizedWidth = maxDimension;
            resizedHeight = (int) ((float) maxDimension * (float) originalHeight / (float) originalWidth);
        } else if (originalHeight == originalWidth) {
            resizedHeight = maxDimension;
            resizedWidth = maxDimension;
        }

        return Bitmap.createScaledBitmap(bitmap, resizedWidth, resizedHeight, false);
    }

    private void uploadImageAdhaarModified(Uri filePath_f, Uri fileUri_b, String Ftype, String Stype) {
        Log.d("fileUri_f", String.valueOf(filePath_f));
        Log.d("fileUri_b", String.valueOf(fileUri_b));
        this.forecasterDialogue.show();
        BitmapFactory.Options options1 = new BitmapFactory.Options();
        BitmapFactory.Options options2 = new BitmapFactory.Options();
        Bitmap bitmap = null;
        if (Ftype.equals("G")) {
            try {
                bitmap = this.scaleBitmapDown(MediaStore.Images.Media.getBitmap(this.getContentResolver(), filePath_f), 800);
            } catch (IOException var18) {
                var18.printStackTrace();
            }
        } else {
            bitmap = this.scaleBitmapDown(BitmapFactory.decodeFile(filePath_f.getPath(), options1), 800);
        }

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 40, byteArrayOutputStream);
        imageBytePic = byteArrayOutputStream.toByteArray();
        String image = Base64.encodeToString(imageBytePic, 0);
        Bitmap bitmap1 = null;
        if (Stype.equals("G")) {
            try {
                bitmap1 = this.scaleBitmapDown(MediaStore.Images.Media.getBitmap(this.getContentResolver(), fileUri_b), 800);
            } catch (IOException var17) {
                var17.printStackTrace();
            }
        } else {
            bitmap1 = this.scaleBitmapDown(BitmapFactory.decodeFile(fileUri_b.getPath(), options2), 800);
        }

        ByteArrayOutputStream byteArrayOutputStream1 = new ByteArrayOutputStream();
        bitmap1.compress(Bitmap.CompressFormat.JPEG, 40, byteArrayOutputStream1);
        imageBytePic1 = byteArrayOutputStream1.toByteArray();
        String image1 = Base64.encodeToString(imageBytePic1, 0);
        String urlStr = "https://ocrapi-100.appspot.com/api/aadhar/extraction/android/";
        JSONObject jsonObject = new JSONObject();


    }

    private void uploadImageAdhaar() {
        File file = new File(uriList.get(0).toString());
        MultipartBody.Part filePart = MultipartBody.Part.createFormData("file", file.getName(),
                RequestBody.create(MediaType.parse("application/image"), file));

        if (uriList.size() == 2) {
            if (Preferences.getInstance(getApplicationContext()).getROLE().equals(Common.Role.ADMIN.value())) {
                if ("admin".equals(getIntent().getStringExtra("type"))) {
                    apiCallBackWithout(getApiCommonController().uploadResource(Common.DocType.ADHAAR_FRONT, filePart));
                }
                if ("student".equals(getIntent().getStringExtra("type"))) {
                    apiCallBackWithout(getApiCommonController().uploadResourceAdmin(Common.DocType.ADHAAR_FRONT, getIntent().getStringExtra("id"), filePart));
                }
                if ("teacher".equals(getIntent().getStringExtra("type"))) {
                    apiCallBackWithout(getApiCommonController().uploadResourceAdmin(Common.DocType.ADHAAR_FRONT, getIntent().getStringExtra("id"), filePart));
                }
                if ("father".equals(getIntent().getStringExtra("type"))) {
                    apiCallBackWithout(getApiCommonController().uploadResourceAdmin(Common.DocType.F_ADHAAR_FRONT, getIntent().getStringExtra("id"), filePart));
                }
            } else {
                apiCallBackWithout(getApiCommonController().uploadResource(Common.DocType.ADHAAR_FRONT, filePart));
            }
            MyProgressDialogUploadMultiple.getInstance(this).show();
            MyProgressDialogUploadMultiple.setCount("1", "2");
        } else {
            if (Preferences.getInstance(getApplicationContext()).getROLE().equals(Common.Role.ADMIN.value())) {
                if ("admin".equals(getIntent().getStringExtra("type"))) {
                    apiCallBackWithout(getApiCommonController().uploadResource(Common.DocType.ADHAAR_FRONT, filePart));
                }
                if ("student".equals(getIntent().getStringExtra("type"))) {
                    apiCallBackWithout(getApiCommonController().uploadResourceAdmin(Common.DocType.ADHAAR_BACK, getIntent().getStringExtra("id"), filePart));
                }
                if ("teacher".equals(getIntent().getStringExtra("type"))) {
                    apiCallBackWithout(getApiCommonController().uploadResourceAdmin(Common.DocType.ADHAAR_BACK, getIntent().getStringExtra("id"), filePart));
                }
                if ("father".equals(getIntent().getStringExtra("type"))) {
                    apiCallBackWithout(getApiCommonController().uploadResourceAdmin(Common.DocType.F_ADHAAR_BACK, getIntent().getStringExtra("id"), filePart));
                }
            } else {
                apiCallBackWithout(getApiCommonController().uploadResource(Common.DocType.ADHAAR_BACK, filePart));
            }
            MyProgressDialogUploadMultiple.setCount("2", "2");
        }

    }

    private void sendDataToParentActivty(JSONObject JsonResponse, String image) {
        Intent intent = new Intent();
        intent.putExtra("OCRResponse", String.valueOf(JsonResponse));
        intent.putExtra("image", image);
        this.setResult(-1, intent);
        this.finish();
    }

    private void sendDataToParentActivty(JSONObject JsonResponse, String image1, String image2) {
        Intent intent = new Intent();
        intent.putExtra("OCRResponse", String.valueOf(JsonResponse));
        intent.putExtra("image1", image1);
        intent.putExtra("image2", image2);
        this.setResult(-1, intent);
        this.finish();
    }

    private class SavePicTask extends AsyncTask<Void, Void, String> {
        private byte[] data;
        private int rotation = 0;

        public SavePicTask(byte[] data, int rotation) {
            this.data = data;
            this.rotation = rotation;
        }

        protected void onPreExecute() {
        }

        protected String doInBackground(Void... params) {
            try {
                return saveToSDCard(this.data, this.rotation);
            } catch (Exception var3) {
                var3.printStackTrace();
                return null;
            }
        }

        protected void onPostExecute(String result) {
            activeCameraCapture();
            Log.d("ImageWho", getIntent().getStringExtra("docType"));
            tempFile = new File(result);
            Log.d("FilePathWhatsup", String.valueOf(tempFile));
            (new Handler()).postDelayed(new Runnable() {
                @SuppressLint("WrongConstant")
                public void run() {
                    BitmapFactory.Options options;
                    final Bitmap bitmap;
                    if (getIntent().getStringExtra("docType").equalsIgnoreCase("AadhaarFront")) {
                        mEditor = mSharedPreferences.edit();
                        mEditor.putString("Frontfile", tempFile.toString());
                        mEditor.apply();
                        cameraFrameLayoutMain.setVisibility(View.GONE);
                        review_msg.setText(R.string.aadhaar_front_review);
                        previewRelativeMain.setVisibility(View.VISIBLE);
                        options = new BitmapFactory.Options();
                        bitmap = scaleBitmapDown(BitmapFactory.decodeFile(tempFile.getPath(), options), 800);
                        imgPreview.setImageBitmap(bitmap);
                        imageView_front.setVisibility(View.VISIBLE);
                        imageView_back.setVisibility(View.GONE);
                        imageView_Pancard.setVisibility(View.GONE);
                        imageView_cheque.setVisibility(View.GONE);
                        imageView_cio.setVisibility(View.GONE);
                        imageView_cancel.setOnClickListener(new View.OnClickListener() {
                            public void onClick(View view) {
                                InitActivity();
                            }
                        });
                        close_box.setOnClickListener(new View.OnClickListener() {
                            public void onClick(View view) {
                                InitActivity();
                            }
                        });
                        imageView_front.setOnClickListener(new View.OnClickListener() {
                            public void onClick(View view) {
                                getIntent().putExtra("docType", "AadhaarBack");
                                InitActivity();
                            }
                        });
                    } else if (getIntent().getStringExtra("docType").equalsIgnoreCase("AadhaarBack")) {
                        cameraFrameLayoutMain.setVisibility(View.GONE);
                        review_msg.setText(R.string.aadhaar_back_review);
                        previewRelativeMain.setVisibility(View.VISIBLE);
                        options = new BitmapFactory.Options();
                        final Uri fileUri_b = Uri.parse(tempFile.getPath());
                        final Uri fileUri_f = Uri.parse(mSharedPreferences.getString("Frontfile", ""));
                        final Bitmap bitmapb = scaleBitmapDown(BitmapFactory.decodeFile(tempFile.getPath(), options), 800);
                        final Bitmap bitmapf = scaleBitmapDown(BitmapFactory.decodeFile(fileUri_f.getPath(), options), 800);
                        imgPreview.setImageBitmap(bitmapb);
                        imageView_front.setVisibility(View.GONE);
                        imageView_back.setVisibility(View.VISIBLE);
                        imageView_Pancard.setVisibility(View.GONE);
                        imageView_cheque.setVisibility(View.GONE);
                        imageView_cio.setVisibility(View.GONE);
                        imageView_cancel.setOnClickListener(new View.OnClickListener() {
                            public void onClick(View view) {
                                InitActivity();
                            }
                        });
                        close_box.setOnClickListener(new View.OnClickListener() {
                            public void onClick(View view) {
                                InitActivity();
                            }
                        });
                        imageView_back.setOnClickListener(new View.OnClickListener() {
                            public void onClick(View view) {
                                if (fileUri_f != null && fileUri_b != null) {
                                    uriList.clear();
                                    uriList.add(fileUri_f);
                                    uriList.add(fileUri_b);
                                    uploadImageAdhaar();
                                } else if (fileUri_f != null && fileUri_b == null) {
                                    uploadImageAdhaarModified(fileUri_f, fileUri_b, "G", "C");
                                } else if (fileUri_f == null && fileUri_b != null) {
                                    uploadImageAdhaarModified(fileUri_f, fileUri_b, "C", "G");
                                }

                            }
                        });
                    } else if (getIntent().getStringExtra("docType").equalsIgnoreCase("Pancard")) {
                        cameraFrameLayoutMain.setVisibility(View.GONE);
                        review_msg.setText(R.string.pancard_review);
                        previewRelativeMain.setVisibility(View.VISIBLE);
                        options = new BitmapFactory.Options();
                        filePath_pan = Uri.parse(tempFile.getPath());
                        bitmap = scaleBitmapDown(BitmapFactory.decodeFile(tempFile.getPath(), options), 800);
                        imgPreview.setImageBitmap(bitmap);
                        imageView_front.setVisibility(View.GONE);
                        imageView_back.setVisibility(View.GONE);
                        imageView_Pancard.setVisibility(View.VISIBLE);
                        imageView_cheque.setVisibility(View.GONE);
                        imageView_cio.setVisibility(View.GONE);
                        imageView_cancel.setOnClickListener(new View.OnClickListener() {
                            public void onClick(View view) {
                                InitActivity();
                            }
                        });
                        close_box.setOnClickListener(new View.OnClickListener() {
                            public void onClick(View view) {
                                InitActivity();
                            }
                        });
                        imageView_Pancard.setOnClickListener(new View.OnClickListener() {
                            public void onClick(View view) {
                                uploadImageUri(filePath_pan);
                            }
                        });
                    } else if (getIntent().getStringExtra("docType").equalsIgnoreCase("Cheque")) {
                        cameraFrameLayoutMain.setVisibility(View.GONE);
                        review_msg.setText(R.string.cheque_review);
                        previewRelativeMain.setVisibility(View.VISIBLE);
                        options = new BitmapFactory.Options();
                        bitmap = scaleBitmapDown(BitmapFactory.decodeFile(tempFile.getPath(), options), 800);
                        imgPreview.setImageBitmap(bitmap);
                        imageView_front.setVisibility(View.GONE);
                        imageView_back.setVisibility(View.GONE);
                        imageView_Pancard.setVisibility(View.GONE);
                        imageView_cheque.setVisibility(View.VISIBLE);
                        imageView_cio.setVisibility(View.GONE);
                        imageView_cancel.setOnClickListener(new View.OnClickListener() {
                            public void onClick(View view) {
                                InitActivity();
                            }
                        });
                        close_box.setOnClickListener(new View.OnClickListener() {
                            public void onClick(View view) {
                                InitActivity();
                            }
                        });
                        imageView_cheque.setOnClickListener(new View.OnClickListener() {
                            public void onClick(View view) {
                                uploadImageUriCheque(bitmap);
                            }
                        });
                    } else if (getIntent().getStringExtra("docType").equalsIgnoreCase("COI")) {
                        cameraFrameLayoutMain.setVisibility(View.GONE);
                        review_msg.setText(R.string.coi_review);
                        previewRelativeMain.setVisibility(View.VISIBLE);
                        options = new BitmapFactory.Options();
                        bitmap = scaleBitmapDown(BitmapFactory.decodeFile(tempFile.getPath(), options), 800);
                        imgPreview.setImageBitmap(bitmap);
                        imageView_front.setVisibility(View.GONE);
                        imageView_back.setVisibility(View.GONE);
                        imageView_Pancard.setVisibility(View.GONE);
                        imageView_cheque.setVisibility(View.GONE);
                        imageView_cio.setVisibility(View.VISIBLE);
                        imageView_cancel.setOnClickListener(new View.OnClickListener() {
                            public void onClick(View view) {
                                InitActivity();
                            }
                        });
                        close_box.setOnClickListener(new View.OnClickListener() {
                            public void onClick(View view) {
                                InitActivity();
                            }
                        });
                        imageView_cio.setOnClickListener(new View.OnClickListener() {
                            public void onClick(View view) {
                                uploadImageUriCOI(bitmap);
                            }
                        });
                    }

                }
            }, 10L);
        }
    }


    private Uri getImageUri(Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }


    @Subscribe
    public void getResourceResponse(AddResourceResponse response) {

        if ("Pancard".equals(getIntent().getStringExtra("docType"))) {
            MyProgressDialogUploadMultiple.setDismiss();
            Intent intent = new Intent();
            intent.putExtra("url", response.getUrl());
            setResult(RESULT_OK, intent);
            finish();
        } else {
            MyProgressDialogUploadMultiple.setCount("2", "2");
            if (Status.SUCCESS.value() == response.getStatus().value()) {
                if (uriList.size() == 2) {
                    urlFront = response.getUrl();
                } else if (uriList.size() == 1) {
                    urlBack = response.getUrl();
                    Intent intent = new Intent();
                    intent.putExtra("urlFront", urlFront);
                    intent.putExtra("urlBack", urlBack);
                    setResult(RESULT_OK, intent);

                    finish();
                }
                uriList.remove(0);
                if (uriList.size() > 0) {
                    uploadImageAdhaar();
                } else {
                    MyProgressDialogUploadMultiple.setDismiss();
                }

            } else {
                dialogError(response.getMessage().getMessage());
            }
        }
    }


    @Subscribe
    public void timeOut(String message) {
        dialogError(message);
        MyProgressDialogUploadMultiple.setDismiss();
    }


}


