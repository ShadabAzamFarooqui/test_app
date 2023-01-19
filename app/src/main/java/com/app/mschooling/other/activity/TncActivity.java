package com.app.mschooling.other.activity;

import android.os.Bundle;
import android.view.MenuItem;
import android.webkit.WebView;

import com.app.mschooling.base.activity.BaseActivity;
import com.app.mschooling.com.R;
import com.mschooling.transaction.common.api.Status;
import com.mschooling.transaction.response.tnc.GetTNCResponse;

import org.greenrobot.eventbus.Subscribe;

public class TncActivity extends BaseActivity {



    WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tnc);
        webView = findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        if (getIntent().getStringExtra("from").equals("tnc")) {
            toolbarClick(getString(R.string.terms_condition));
        } else {
            toolbarClick(getString(R.string.privacy_policy));
        }
        apiCallBack(getApiCommonController().tnc());
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
    }

    @Subscribe
    public void getResponse(GetTNCResponse response) {
        if (Status.SUCCESS.value().equals(response.getStatus().value())) {
            webView.loadDataWithBaseURL("", response.getTnc(), "text/html", "UTF-8", null);
        } else {
            dialogFailed(response.getMessage().getMessage());
        }
    }
}
