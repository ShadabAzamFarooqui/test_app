package com.app.mschooling.paid.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.app.mschooling.base.activity.BaseActivity;
import com.app.mschooling.com.R;
import com.app.mschooling.utils.Preferences;
import com.mschooling.transaction.common.api.Common;
import com.mschooling.transaction.common.api.Status;
import com.mschooling.transaction.common.user.Subscription;
import com.mschooling.transaction.response.configuration.ConfigurationResponse;
import com.mschooling.transaction.response.subscription.AddSubscriptionResponse;

import org.greenrobot.eventbus.Subscribe;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PaidActivity extends BaseActivity {


    @BindView(R.id.submit)
    LinearLayout submit;
    @BindView(R.id.applied)
    LinearLayout applied;
    Subscription subscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paid);
        ButterKnife.bind(this);
        toolbarClick(getString(R.string.plan));
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                apiCallBack(getApiCommonController().applySubscription());
            }
        });
        subscription= Preferences.getInstance(getApplicationContext()).getConfiguration().getCommonSetup().getSubscription();
        if (subscription.getSubscriptionType().equals(Common.SubscriptionType.PENDING)) {
            applied.setVisibility(View.VISIBLE);
            submit.setVisibility(View.GONE);
        }else {
            applied.setVisibility(View.GONE);
            submit.setVisibility(View.VISIBLE);
        }

    }

    @Subscribe
    public void getResponse(AddSubscriptionResponse response) {
        if (response.getStatus().value().equals(Status.SUCCESS.value())) {
            dialogSuccess(response.getMessage().getMessage());
            applied.setVisibility(View.VISIBLE);
            submit.setVisibility(View.GONE);
            subscription.setSubscriptionType(Common.SubscriptionType.PENDING);
            ConfigurationResponse configuration= Preferences.getInstance(getApplicationContext()).getConfiguration();
            configuration.getCommonSetup().setSubscription(subscription);
            Preferences.getInstance(getApplicationContext()).setConfiguration(configuration);

        } else {
            dialogError(response.getMessage().getMessage());
        }
    }


}
