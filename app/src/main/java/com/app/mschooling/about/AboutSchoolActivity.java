package com.app.mschooling.about;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import com.app.mschooling.base.activity.BaseActivity;
import com.app.mschooling.com.R;
import com.app.mschooling.com.databinding.ActivityAboutSchoolBinding;
import com.app.mschooling.other.activity.ShowImageActivity;
import com.app.mschooling.utils.Preferences;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.mschooling.transaction.common.SchoolInfo;
import com.mschooling.transaction.common.api.Common;
import com.mschooling.transaction.common.api.Status;
import com.mschooling.transaction.response.school.info.GetSchoolInfoResponse;

import org.greenrobot.eventbus.Subscribe;

import butterknife.ButterKnife;

public class AboutSchoolActivity extends BaseActivity {


    ActivityAboutSchoolBinding binding;
    GetSchoolInfoResponse response;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_about_school);
        ButterKnife.bind(this);
        toolbarClick(getString(R.string.school_details));
        binding.mainLayout.setVisibility(View.GONE);
        binding.shimmerFrameLayout.setVisibility(View.VISIBLE);
        binding.shimmerFrameLayout.startShimmer();
        binding.editLayout.edit.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), UpdateSchoolActivity.class)));
        if (Preferences.getInstance(getApplicationContext()).getROLE().equals(Common.Role.ADMIN.value())) {
            binding.editLayout.edit.setVisibility(View.VISIBLE);
        } else {
            binding.editLayout.edit.setVisibility(View.INVISIBLE);
        }

        binding.image.setOnClickListener(v -> {
            if (response != null) {
                if (response.getSchoolInfo().getUrl() != null) {
                    Intent intent = new Intent(getApplicationContext(), ShowImageActivity.class);
                    intent.putExtra("url", response.getSchoolInfo().getUrl());
                    intent.putExtra("name", response.getSchoolInfo().getName());
                    startActivity(intent);
                }
            }

        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        apiCallBack(getApiCommonController().getSchoolInfo());
    }

    @SuppressLint("SetTextI18n")
    @Subscribe
    public void getResponse(GetSchoolInfoResponse response) {
        if (Status.SUCCESS.value().equals(response.getStatus().value())) {
            this.response = response;
            binding.mainLayout.setVisibility(View.VISIBLE);
            SchoolInfo schoolInfo = response.getSchoolInfo();
            binding.schoolName.setText(schoolInfo.getName());
            binding.openTime.setText(schoolInfo.getOpenTime());
            binding.closeTime.setText(schoolInfo.getCloseTime());
            binding.qrCode.setText(schoolInfo.getQrCode());
            binding.schoolCode.setText("MS4U0" + schoolInfo.getCode());
            binding.address.setText(schoolInfo.getHouse() + " " + schoolInfo.getStreet() + " " + schoolInfo.getCity() /*+ " " + add.getLandmark()*/ + "\n" + schoolInfo.getState() + " " + schoolInfo.getCountry() + "\n" + schoolInfo.getPinCode());
            binding.mobile.setText(schoolInfo.getSchoolContactInfo().getMobile());
            binding.email.setText(schoolInfo.getSchoolContactInfo().getEmail());
            binding.aboutSchool.setText(schoolInfo.getAbout());

            binding.schoolMobile.setText(schoolInfo.getSchoolContactInfo().getMobile());
            binding.schoolEmail.setText(schoolInfo.getSchoolContactInfo().getEmail());

            binding.adminName.setText(schoolInfo.getAdminContactInfo().getName());
            binding.adminEmail.setText(schoolInfo.getAdminContactInfo().getEmail());
            binding.adminMobile.setText(schoolInfo.getAdminContactInfo().getMobile());


            binding.principalName.setText(schoolInfo.getPrincipalContactInfo().getName());
            binding.principalEmail.setText(schoolInfo.getPrincipalContactInfo().getEmail());
            binding.principalMobile.setText(schoolInfo.getPrincipalContactInfo().getMobile());


            Glide.with(this)
                    .load(response.getSchoolInfo().getUrl())
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            binding.shimmerFrameLayout.setVisibility(View.GONE);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            binding.shimmerFrameLayout.setVisibility(View.GONE);
                            return false;
                        }
                    })
                    .into(binding.image);


            if (schoolInfo.getFacebookSocialMedia() != null) {
                if (schoolInfo.getFacebookSocialMedia().getUrl() != null) {
                    if (!schoolInfo.getFacebookSocialMedia().getUrl().trim().isEmpty()) {
                        binding.facebook.setText(schoolInfo.getFacebookSocialMedia().getUrl());
                    } else {
                        binding.facebook.setText("-");
                    }
                } else {
                    binding.facebook.setText("-");
                }
            } else {
                binding.facebook.setText("-");
            }


            if (schoolInfo.getTwitterSocialMedia() != null) {
                if (schoolInfo.getTwitterSocialMedia().getUrl() != null) {
                    if (!schoolInfo.getTwitterSocialMedia().getUrl().trim().isEmpty()) {
                        binding.twitter.setText(schoolInfo.getTwitterSocialMedia().getUrl());
                    } else {
                        binding.twitter.setText("-");
                    }
                } else {
                    binding.twitter.setText("-");
                }
            } else {
                binding.twitter.setText("-");
            }


            if (schoolInfo.getLinkedInSocialMedia() != null) {
                if (schoolInfo.getLinkedInSocialMedia().getUrl() != null) {
                    if (!schoolInfo.getLinkedInSocialMedia().getUrl().trim().isEmpty()) {
                        binding.linkedIn.setText(schoolInfo.getLinkedInSocialMedia().getUrl());
                    } else {
                        binding.linkedIn.setText("-");
                    }
                } else {
                    binding.linkedIn.setText("-");
                }
            } else {
                binding.linkedIn.setText("-");
            }


        } else {
            dialogFailed(response.getMessage().getMessage());
        }

    }


    public void call(View view) {
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, 1);
        } else {
            Intent callIntent = new Intent(Intent.ACTION_CALL);
            callIntent.setData(Uri.parse("tel:" + response.getSchoolInfo().getSchoolContactInfo().getMobile()));
            startActivity(callIntent);
        }

    }

    public void email(View view) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        String[] recipients = {response.getSchoolInfo().getSchoolContactInfo().getEmail()};
        intent.putExtra(Intent.EXTRA_EMAIL, recipients);
        intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.email_subject_message));
        intent.putExtra(Intent.EXTRA_TEXT, "");
        intent.setType("text/html");
        intent.setPackage("com.google.android.gm");
        startActivity(Intent.createChooser(intent, "Send mail"));
    }


}

