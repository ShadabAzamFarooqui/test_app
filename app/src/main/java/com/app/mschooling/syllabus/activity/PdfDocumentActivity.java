package com.app.mschooling.syllabus.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.app.mschooling.base.activity.BaseActivity;
import com.app.mschooling.com.R;
import com.app.mschooling.utils.ParameterConstant;
import com.app.mschooling.utils.Preferences;
import com.app.mschooling.utils.progress_dialog.MyProgressDialog;
import com.mschooling.transaction.common.api.Common;
import com.mschooling.transaction.common.api.Status;
import com.mschooling.transaction.response.syllabus.DeleteSyllabusResponse;
import com.thirdparty.textlocal.SuccessResponse;

import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import com.mschooling.pdfviewpager.library.RemotePDFViewPager;
import com.mschooling.pdfviewpager.library.adapter.PDFPagerAdapter;
import com.mschooling.pdfviewpager.library.remote.DownloadFile;
import com.mschooling.pdfviewpager.library.util.FileUtil;


public class PdfDocumentActivity extends BaseActivity implements DownloadFile.Listener {

    LinearLayout noRecord;
    LinearLayout download;


    LinearLayout root;
    RemotePDFViewPager remotePDFViewPager;
    ProgressBar progressBar;
    PDFPagerAdapter adapter;

    ImageView imageIndicator;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_syllabus_pdf);
        noRecord = (LinearLayout) findViewById(R.id.noRecord);
        download = (LinearLayout) findViewById(R.id.download);
        root = findViewById(R.id.remote_pdf_root);
        progressBar = findViewById(R.id.progressBar);
        remotePDFViewPager = findViewById(R.id.pdfViewPager);
        imageIndicator = findViewById(R.id.imageIndicator);

        toolbarClick(getIntent().getStringExtra("name"));

        setDownloadButtonListener(getIntent().getStringExtra("url"));
     

        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyProgressDialog.getInstance(PdfDocumentActivity.this).show();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        downloadFile(PdfDocumentActivity.this, getIntent().getStringExtra("url"), getIntent().getStringExtra("name")+".pdf");
                    }
                }).start();
            }
        });


    }





    @SuppressLint("RestrictedApi")
    @Subscribe
    public void delete(DeleteSyllabusResponse response) {
        if (response.getStatus().value().equals(Status.SUCCESS.value())) {
            dialogSuccessFinish(response.getMessage().getMessage());
        } else {
            dialogFailed(response.getMessage().getMessage());
        }
    }

    @Subscribe
    public void download(SuccessResponse response) {
        Toast.makeText(this, "downloaded", Toast.LENGTH_SHORT).show();
    }


    protected void setDownloadButtonListener(String url) {
        final Context ctx = this;
        final DownloadFile.Listener listener = this;
        remotePDFViewPager = new RemotePDFViewPager(ctx, url, listener);
        remotePDFViewPager.setId(R.id.pdfViewPager);
    }

    @Override
    public void onSuccess(String url, String destinationPath) {
        try {
            adapter = new PDFPagerAdapter(this, FileUtil.extractFileNameFromURL(url));
            remotePDFViewPager.setAdapter(adapter);
            remotePDFViewPager.canScrollVertically(1);
            updateLayout();
            Toast.makeText(this, getString(R.string.scroll), Toast.LENGTH_SHORT).show();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    imageIndicator.setVisibility(View.GONE);
                }
            },2000);
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    public void onFailure(Exception e) {
        progressBar.setVisibility(View.GONE);
        e.printStackTrace();
        Toast.makeText(this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onProgressUpdate(int progress, int total) {

    }


    public void updateLayout() {
        root.addView(remotePDFViewPager, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        progressBar.setVisibility(View.GONE);
    }


    public void downloadFile(BaseActivity context, String fileUrl, String name){
        try {
            final int  MEGABYTE = 1024 * 1024;
            URL url = new URL(fileUrl);
            HttpURLConnection urlConnection = (HttpURLConnection)url.openConnection();
            urlConnection.setRequestMethod("GET");

            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setRequestProperty("X-APP_NAME", context.getResources().getString(R.string.app_name));
            urlConnection.setRequestProperty("Authorization", "Bearer " + Preferences.getInstance(context).getToken());
            //urlConnection.setDoOutput(true);
            urlConnection.connect();


            File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), ParameterConstant.getFolderName());
            File directory=new File(file,name);
            InputStream inputStream = urlConnection.getInputStream();
            FileOutputStream fileOutputStream = new FileOutputStream(directory);
            int totalSize = urlConnection.getContentLength();

            byte[] buffer = new byte[MEGABYTE];
            int bufferLength = 0;
            while((bufferLength = inputStream.read(buffer))>0 ){
                fileOutputStream.write(buffer, 0, bufferLength);
            }
            fileOutputStream.close();


            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    MyProgressDialog.setDismiss();
                    dialogSuccess(directory.getAbsolutePath());
                }
            });
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

}

