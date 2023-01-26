package com.app.mschooling.other.activity;

import static android.graphics.Color.BLACK;
import static android.graphics.Color.WHITE;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.content.FileProvider;

import com.app.mschooling.base.activity.BaseActivity;
import com.app.mschooling.com.R;
import com.app.mschooling.utils.Helper;
import com.app.mschooling.utils.ParameterConstant;
import com.app.mschooling.utils.progress_dialog.MyProgressDialog;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.mschooling.transaction.common.api.Status;
import com.mschooling.transaction.response.school.info.GetSchoolInfoResponse;

import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.EnumMap;
import java.util.Map;


public class QRCodeActivity extends BaseActivity {
    String input_data;
    ImageView mImageView;
    TextView description;
    TextView codeNameTxt;
    LinearLayout mainLayout;
    LinearLayout copy;
    String schoolName;
    CardView share;

    Bitmap bitmap;


    File myExternalFile;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_generate_qrcode);
        toolbarClick(getString(R.string.qr_code));
        mainLayout = findViewById(R.id.mainLayout);
        mImageView = findViewById(R.id.image_view);
        copy = findViewById(R.id.copy);
        description = findViewById(R.id.description);
        codeNameTxt = findViewById(R.id.codeNameTxt);
        share = findViewById(R.id.share);
        mainLayout.setVisibility(View.GONE);
        apiCallBack(getApiCommonController().getSchoolInfo());

        myExternalFile = new File(getExternalFilesDir("mSchooling"), "QrCode.pdf");
        share.setOnClickListener((View.OnClickListener) v -> createPdf(Helper.getBitmapFromView(mainLayout)));

        copy.setOnClickListener((View.OnClickListener) v -> {
            ClipboardManager myClipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
            ClipData myClip = ClipData.newPlainText("text", codeNameTxt.getText().toString());
            myClipboard.setPrimaryClip(myClip);
            Toast.makeText(getApplicationContext(), getString(R.string.copied),
                    Toast.LENGTH_SHORT).show();
        });

    }

    public Bitmap barcodeBitmap(String input_data, int width, int height) {


        return bitmap;
    }

    private static String guessAppropriateEncoding(CharSequence contents) {
        // Very crude at the moment
        for (int i = 0; i < contents.length(); i++) {
            if (contents.charAt(i) > 0xFF) {
                return "UTF-8";
            }
        }
        return null;
    }

    public static Bitmap encodeAsBitmap(String contents, BarcodeFormat format, int img_width, int img_height) throws WriterException {

        if (contents == null) {
            return null;
        }
        Map<EncodeHintType, Object> hints = null;
        String encoding = guessAppropriateEncoding(contents);
        if (encoding != null) {
            hints = new EnumMap<EncodeHintType, Object>(EncodeHintType.class);
            hints.put(EncodeHintType.CHARACTER_SET, encoding);
        }
        MultiFormatWriter writer = new MultiFormatWriter();
        BitMatrix result;
        try {
            result = writer.encode(contents, format, img_width, img_height, hints);
        } catch (IllegalArgumentException iae) {
            // Unsupported format
            return null;
        }
        int width = result.getWidth();
        int height = result.getHeight();
        int[] pixels = new int[width * height];
        for (int y = 0; y < height; y++) {
            int offset = y * width;
            for (int x = 0; x < width; x++) {
                pixels[offset + x] = result.get(x, y) ? BLACK : WHITE;
            }
        }

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        return bitmap;
    }


    private void createPdf(Bitmap bitmap) {
        MyProgressDialog.getInstance(QRCodeActivity.this).show();
        WindowManager wm = (WindowManager) QRCodeActivity.this.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        DisplayMetrics displaymetrics = new DisplayMetrics();
        QRCodeActivity.this.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        PdfDocument document = new PdfDocument();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(bitmap.getWidth(), bitmap.getHeight(), 1).create();
        PdfDocument.Page page = document.startPage(pageInfo);

        Canvas canvas = page.getCanvas();


        Paint paint = new Paint();
        paint.setColor(Color.parseColor("#ffffff"));
        canvas.drawPaint(paint);

        bitmap = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth(), bitmap.getHeight(), true);

        paint.setColor(Color.BLACK);
        canvas.drawBitmap(bitmap, 0, 0, null);
        document.finishPage(page);
        try {
            document.writeTo(new FileOutputStream(myExternalFile));
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Something wrong: " + e.toString(), Toast.LENGTH_LONG).show();
        }

        // close the document
        document.close();


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                MyProgressDialog.setDismiss();
//                Helper.preview(getApplicationContext(), Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS + "/mSchooling/QrCode.pdf"));
//                previewPdf(filePath);
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_STREAM, FileProvider.getUriForFile(QRCodeActivity.this, QRCodeActivity.this.getPackageName() + ".provider", myExternalFile));
                intent.setType("application/pdf");
                intent.putExtra(Intent.EXTRA_SUBJECT, "Link");
                intent.putExtra(Intent.EXTRA_TEXT, "https://play.google.com/store/apps/details?id=com.app.mschooling");
                startActivity(intent);
            }
        }, 3 * 1000);

    }


    @Subscribe
    public void getResponse(GetSchoolInfoResponse response) throws WriterException {
        if (Status.SUCCESS.value().equals(response.getStatus().value())) {
            input_data = ParameterConstant.getMSchoolingText() + "#" + response.getSchoolInfo().getQrCode();
            schoolName = response.getSchoolInfo().getName().replace(" ", "_");
            description.setText(response.getSchoolInfo().getName());
            codeNameTxt.setText(response.getSchoolInfo().getQrCode());
            Bitmap bitmap = encodeAsBitmap(input_data, BarcodeFormat.QR_CODE, 1200, 1000);
            if (bitmap != null) {
                MyProgressDialog.setDismiss();
                mImageView.setImageBitmap(bitmap);
                mainLayout.setVisibility(View.VISIBLE);
            } else {
                Toast.makeText(getApplicationContext(), R.string.qr_code_not_generated, Toast.LENGTH_SHORT).show();
            }
        } else {
            dialogFailed(response.getMessage().getMessage());
        }
    }

}
