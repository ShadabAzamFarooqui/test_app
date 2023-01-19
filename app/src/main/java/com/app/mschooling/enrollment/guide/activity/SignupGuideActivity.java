package com.app.mschooling.enrollment.guide.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.app.mschooling.help_support_report_feedback.activity.ExternalSupportActivity;
import com.app.mschooling.base.activity.BaseActivity;
import com.app.mschooling.com.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.mschooling.transaction.common.api.Status;
import com.mschooling.transaction.response.help.GetHelpResponse;

import org.greenrobot.eventbus.Subscribe;

import java.util.Iterator;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SignupGuideActivity extends BaseActivity {

    @BindView(R.id.image1)
    ImageView image1;
    @BindView(R.id.image2)
    ImageView image2;
    @BindView(R.id.image3)
    ImageView image3;
    @BindView(R.id.image4)
    ImageView image4;
    @BindView(R.id.image5)
    ImageView image5;
    @BindView(R.id.image6)
    ImageView image6;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_guide);
        ButterKnife.bind(this);
        toolbarClick(getString(R.string.tool_guide));
        apiCallBack(getApiCommonController().help());
    }

    public void help(View view) {
        startActivity(new Intent(getApplicationContext(), ExternalSupportActivity.class));
    }


    @Subscribe
    public void getResponse(GetHelpResponse response){
        if (response.getStatus().value().equals(Status.SUCCESS.value())){
           Iterator<String> iterator= response.getUrls().iterator();
            glide(image1,iterator.next());
            glide(image2,iterator.next());
            glide(image3,iterator.next());
            glide(image4,iterator.next());
            glide(image5,iterator.next());
            try {
                glide(image6,iterator.next());
            }catch (Exception e){
                e.printStackTrace();
                glide(image6,"http://ec2-13-233-238-18.ap-south-1.compute.amazonaws.com:8080/app-war/common/resource/get/QUF7Xnt0cVB8b0hSNmduUn1ZflF7PFdpfVpLZndKNl56SUhRfDxXXTRKS156Z3FmdHw2ZnpsNl1YNTZQNVZyZjVOW2h6WVtmemw2UA==/");
            }
        }else {
            dialogError(response.getMessage().getMessage());
        }
    }

    void glide(ImageView image,String url){
        Glide.with(this)
                .load(url)
                .transition(DrawableTransitionOptions.withCrossFade())
                .apply(RequestOptions.skipMemoryCacheOf(true))
                .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.ALL))
                .apply(new RequestOptions().placeholder(R.drawable.default_image))
                .into(image);

    }
}
