package com.app.mschooling.other.activity;

import static com.app.mschooling.network.Api.BASE_URL_DEVELOPMENT;
import static com.app.mschooling.network.Api.BASE_URL_PRODUCTION;
import static com.app.mschooling.network.Api.COMMON_CONTROLLER;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.print.PdfPrint;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintManager;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.core.content.FileProvider;

import com.app.mschooling.base.activity.BaseActivity;
import com.app.mschooling.com.R;
import com.app.mschooling.utils.Preferences;
import com.app.mschooling.utils.progress_dialog.MyProgressDialog;
import com.mschooling.transaction.common.api.Common;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.File;
import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PrintIdCardActivity extends BaseActivity {

    @BindView(R.id.webView)
    WebView webView;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.printer)
    LinearLayout printer;
    String result = null;
    String enrollmentId;
    String fileName;
    File myExternalFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_print_id_card);
        ButterKnife.bind(this);
        toolbarClick(getString(R.string.tool_i_cards));

        if (Preferences.getInstance(getApplicationContext()).getROLE().equals(Common.Role.STUDENT.value())) {
            enrollmentId = Preferences.getInstance(getApplicationContext()).getEnrollmentId();
        } else {
            enrollmentId = getIntent().getStringExtra("id");
        }
        if (getIntent().getStringExtra("name") == null) {
            fileName = "student.pdf";
        } else {
            fileName = getIntent().getStringExtra("name") + ".pdf";
        }


        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setLoadWithOverviewMode(true);

        myExternalFile = new File(getExternalFilesDir("mSchooling"), "");

        new Thread(new Runnable() {
            @Override
            public void run() {
                apiCall();
            }
        }).start();


        printer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                createWebPrintJob(webView);
                PrintManager printManager = (PrintManager) getSystemService(
                        Context.PRINT_SERVICE);
                printManager.print(fileName, webView.createPrintDocumentAdapter(), null);

//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//                    if (!Environment.isExternalStorageManager()) {
//                        Intent permissionIntent = new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
//                        startActivity(permissionIntent);
//                    } else {
//                        createWebPrintJob(webView);
//                    }
//                }else {
//                    createWebPrintJob(webView);
//                }
            }
        });
    }


    public void apiCall() {
        try {
            HttpClient httpclient = new DefaultHttpClient();
            String url;
            if (Preferences.getInstance(getApplicationContext()).getBuildType().equals("Staging")) {
                url = BASE_URL_DEVELOPMENT + COMMON_CONTROLLER + "report/student/icard/detail/get/" + enrollmentId + "/";
            } else {
                url = BASE_URL_PRODUCTION + COMMON_CONTROLLER + "report/student/icard/detail/get/" + enrollmentId + "/";
            }
            System.out.println("http url : " + url);
            HttpGet request = new HttpGet(url);
            request.addHeader("Content-Type", "application/json");
            request.addHeader("X-APP_NAME", getResources().getString(R.string.app_name));
            request.addHeader("Authorization", "Bearer " + Preferences.getInstance(this).getToken());
            ResponseHandler<String> handler = new BasicResponseHandler();

            try {
                result = httpclient.execute(request, handler);
                System.out.println("http result : " + result);
                progressBar.setVisibility(View.GONE);

            } catch (ClientProtocolException e) {
                e.printStackTrace();
                System.out.println("http 1 : " + e.getMessage());
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("http 2 :" + e.getMessage());
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("http 3 :" + e.getMessage());
            } catch (Error e) {
                e.printStackTrace();
                System.out.println("http 4 :" + e.getMessage());
            }
            httpclient.getConnectionManager().shutdown();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    webView.loadDataWithBaseURL(null, result, "text/html", "utf-8", null);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("http 5 :" + e.getMessage());

        }
    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void createWebPrintJob(WebView webView) {


        String jobName = getString(R.string.app_name) + " Document";
        PrintAttributes attributes = new PrintAttributes.Builder()
                .setMediaSize(PrintAttributes.MediaSize.ISO_A4)
                .setResolution(new PrintAttributes.Resolution("pdf", "pdf", 500, 500))
                .setMinMargins(PrintAttributes.Margins.NO_MARGINS).build();

        PdfPrint pdfPrint = new PdfPrint(attributes);
        PrintDocumentAdapter adapter;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            adapter = webView.createPrintDocumentAdapter(jobName);
        } else {
            adapter = webView.createPrintDocumentAdapter();
        }
        pdfPrint.print(adapter, myExternalFile, fileName);
        MyProgressDialog.getInstance(this).show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                MyProgressDialog.setDismiss();
                File pdf = new File(myExternalFile, fileName);
                if (pdf.exists()) {
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_SEND);
                    intent.putExtra(Intent.EXTRA_STREAM, FileProvider.getUriForFile(PrintIdCardActivity.this, PrintIdCardActivity.this.getPackageName() + ".provider", pdf));
                    intent.setType("application/pdf");
                    startActivity(intent);
                } else {
                    Log.i("DEBUG", "File doesn't exist");
                }

            }
        }, 1 * 1000);
    }


}
