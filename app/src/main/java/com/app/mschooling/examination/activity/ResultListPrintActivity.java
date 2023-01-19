package com.app.mschooling.examination.activity;

import static com.app.mschooling.network.Api.BASE_URL_DEVELOPMENT;
import static com.app.mschooling.network.Api.BASE_URL_PRODUCTION;
import static com.app.mschooling.network.Api.COMMON_CONTROLLER;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.print.PdfPrint;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintManager;
import android.provider.Settings;
import android.view.View;
import android.webkit.WebView;
import android.widget.LinearLayout;

import com.app.mschooling.base.activity.BaseActivity;
import com.app.mschooling.com.R;
import com.app.mschooling.utils.Helper;
import com.app.mschooling.utils.Preferences;
import com.app.mschooling.utils.progress_dialog.MyProgressDialog;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.File;
import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ResultListPrintActivity extends BaseActivity {


    @BindView(R.id.noRecord)
    LinearLayout noRecord;
    @BindView(R.id.download)
    LinearLayout download;
    @BindView(R.id.webView)
    WebView webView;

    String result = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        ButterKnife.bind(this);
        toolbarClick(getString(R.string.result));
//        apiCallBack(getApiCommonController().result(getIntent().getStringExtra("id")));
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setLoadWithOverviewMode(true);

        new Thread(new Runnable() {
            @Override
            public void run() {
                apiCall();
            }
        }).start();


        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                createWebPrintJob(webView);

//                PrintManager printManager = (PrintManager) getSystemService(
//                        Context.PRINT_SERVICE);
//                printManager.print(getIntent().getStringExtra("className") +
//                                "_" +
//                                getIntent().getStringExtra("examName") +
//                                ".pdf"
//                        , webView.createPrintDocumentAdapter(), null);

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
                url = BASE_URL_DEVELOPMENT + COMMON_CONTROLLER + "report/examination/result/get?id=" + getIntent().getStringExtra("examId") + "&classId=" + getIntent().getStringExtra("classId") + "&sectionId=" + getIntent().getStringExtra("sectionId");
            } else {
                url = BASE_URL_PRODUCTION + COMMON_CONTROLLER + "report/examination/result/get?id=" + getIntent().getStringExtra("examId") + "&classId=" + getIntent().getStringExtra("classId") + "&sectionId=" + getIntent().getStringExtra("sectionId");
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

            } catch (ClientProtocolException e) {
                e.printStackTrace();
                System.out.println("http 1 : " + ((HttpResponseException) e).getStatusCode());
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

        final File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS );
        PdfPrint pdfPrint = new PdfPrint(attributes);
        PrintDocumentAdapter adapter;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            adapter = webView.createPrintDocumentAdapter(jobName);
        } else {
            adapter = webView.createPrintDocumentAdapter();
        }
        pdfPrint.print(adapter, path, getIntent().getStringExtra("className") + "_" + getIntent().getStringExtra("examName") + ".pdf");
        MyProgressDialog.getInstance(this).show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                MyProgressDialog.setDismiss();
                dialogSuccess(path.toString() + "/" + getIntent().getStringExtra("className") + "_" + getIntent().getStringExtra("examName") + ".pdf");

            }
        }, 1 * 1000);
    }


}
