package com.app.mschooling.other.activity;

import static android.graphics.Color.BLACK;
import static android.graphics.Color.WHITE;

import android.Manifest;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintJob;
import android.print.PrintManager;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
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


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_generate_qrcode);
        toolbarClick(getString(R.string.qr_code));
        mainLayout = (LinearLayout) findViewById(R.id.mainLayout);
        mImageView = (ImageView) findViewById(R.id.image_view);
        copy = findViewById(R.id.copy);
        description = (TextView) findViewById(R.id.description);
        codeNameTxt = (TextView) findViewById(R.id.codeNameTxt);
        share = (CardView) findViewById(R.id.share);
        mainLayout.setVisibility(View.GONE);
        apiCallBack(getApiCommonController().getSchoolInfo());
        share.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.R)
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(QRCodeActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(QRCodeActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
                } else {
//                    if (!Environment.isExternalStorageManager()) {
//                        Intent permissionIntent = new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
//                        startActivity(permissionIntent);
//                    } else {
                        createPdf(Helper.getBitmapFromView(mainLayout));
//                    }

                }
            }
        });

        copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager myClipboard = myClipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                ClipData myClip = ClipData.newPlainText("text", codeNameTxt.getText().toString());
                myClipboard.setPrimaryClip(myClip);
                Toast.makeText(getApplicationContext(), getString(R.string.copied),
                        Toast.LENGTH_SHORT).show();
            }
        });

    }

    public Bitmap barcodeBitmap(String input_data, int width, int height) {

        try {
            bitmap = encodeAsBitmap(input_data, BarcodeFormat.QR_CODE, width, height);
        } catch (WriterException e) {
            e.printStackTrace();
        }
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
        String contentsToEncode = contents;
        if (contentsToEncode == null) {
            return null;
        }
        Map<EncodeHintType, Object> hints = null;
        String encoding = guessAppropriateEncoding(contentsToEncode);
        if (encoding != null) {
            hints = new EnumMap<EncodeHintType, Object>(EncodeHintType.class);
            hints.put(EncodeHintType.CHARACTER_SET, encoding);
        }
        MultiFormatWriter writer = new MultiFormatWriter();
        BitMatrix result;
        try {
            result = writer.encode(contentsToEncode, format, img_width, img_height, hints);
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

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
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

        File filePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS + "/QrCode.pdf");

        try {
            document.writeTo(new FileOutputStream(filePath));
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
                intent.putExtra(Intent.EXTRA_STREAM, FileProvider.getUriForFile(QRCodeActivity.this, QRCodeActivity.this.getPackageName() + ".provider", filePath));
                intent.setType("application/pdf");
                intent.putExtra(Intent.EXTRA_SUBJECT, "Link");
                intent.putExtra(Intent.EXTRA_TEXT, "https://play.google.com/store/apps/details?id=com.app.mschooling");
                startActivity(intent);
            }
        }, 3 * 1000);

    }

    private void previewPdf(File path) {

        Intent intent = new Intent(Intent.ACTION_VIEW);
        try {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                intent.setDataAndType(Uri.parse(path.toString()), "application/pdf");
            } else {
                Uri uri;
                if (path.exists()) {
                    uri = FileProvider.getUriForFile(QRCodeActivity.this, QRCodeActivity.this.getPackageName() + ".provider", path);
                    intent.setDataAndType(uri, "application/pdf");
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                }
            }

            intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void createWebPrintJob(WebView webView) {

        // Get a PrintManager instance
        PrintManager printManager = (PrintManager) QRCodeActivity.this
                .getSystemService(Context.PRINT_SERVICE);

        // Get a print adapter instance
        PrintDocumentAdapter printAdapter = webView.createPrintDocumentAdapter();

        // Create a print job with name and adapter instance
        String jobName = getString(R.string.app_name) + " Document";
        PrintJob printJob = printManager.print(jobName, printAdapter,
                new PrintAttributes.Builder().build());
    }


    @Subscribe
    public void getResponse(GetSchoolInfoResponse response) {
        if (Status.SUCCESS.value().equals(response.getStatus().value())) {
            input_data = ParameterConstant.getMSchoolingText() + "#" + response.getSchoolInfo().getQrCode();
            schoolName = response.getSchoolInfo().getName().replace(" ", "_");
            description.setText(response.getSchoolInfo().getName());
            codeNameTxt.setText(response.getSchoolInfo().getQrCode());
            Bitmap bitmap = barcodeBitmap(input_data, 1200, 1000);
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
