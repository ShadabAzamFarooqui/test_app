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

import androidx.annotation.RequiresApi;

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

public class ResultActivityActivity extends BaseActivity {


    @BindView(R.id.noRecord)
    LinearLayout noRecord;
    @BindView(R.id.download)
    LinearLayout download;
    @BindView(R.id.webView)
    WebView webView;

    String result = null;

    String fileName;

    File myExternalFile;


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

        fileName=getIntent().getStringExtra("name") + ".pdf";

        myExternalFile = new File(getExternalFilesDir("mSchooling"), "");

        new Thread(new Runnable() {
            @Override
            public void run() {
                apiCall();
            }
        }).start();


        download.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.R)
            @Override
            public void onClick(View v) {
                PrintManager printManager = (PrintManager) getSystemService(
                        Context.PRINT_SERVICE);
                printManager.print(fileName, webView.createPrintDocumentAdapter(fileName), null);
//                createWebPrintJob(webView);
            }
        });


    }

    public void apiCall() {
        try {
            HttpClient httpclient = new DefaultHttpClient();
            String url;
            if (Preferences.getInstance(getApplicationContext()).getBuildType().equals("Staging")) {
                url = BASE_URL_DEVELOPMENT + COMMON_CONTROLLER + "report/examination/student/result/get?id=" + getIntent().getStringExtra("id") + "&enrollmentId=" + getIntent().getStringExtra("enrollmentId");
            } else {
                url = BASE_URL_PRODUCTION + COMMON_CONTROLLER + "report/examination/student/result/get?id=" + getIntent().getStringExtra("id") + "&enrollmentId=" + getIntent().getStringExtra("enrollmentId");
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
            System.out.println("http 5 :" + ((HttpResponseException) e).getStatusCode());

        }
    }


    private void createWebPrintJob(WebView webView) {
        String jobName = getString(R.string.app_name) + " Document";
        PrintAttributes attributes = new PrintAttributes.Builder()
                .setMediaSize(PrintAttributes.MediaSize.ISO_A4)
                .setResolution(new PrintAttributes.Resolution("pdf", "pdf", 500, 500))
                .setMinMargins(PrintAttributes.Margins.NO_MARGINS).build();

//        final File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
        PdfPrint pdfPrint = new PdfPrint(attributes);
        PrintDocumentAdapter adapter;
        adapter = webView.createPrintDocumentAdapter(jobName);
        pdfPrint.print(adapter, myExternalFile, fileName);
        MyProgressDialog.getInstance(this).show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                MyProgressDialog.setDismiss();
                dialogSuccess(myExternalFile.getPath() + "/" + fileName);

            }
        }, 1000);
    }


}
