package com.app.mschooling.teachers.detail.activity;


import android.Manifest;
import android.animation.Animator;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.telephony.PhoneNumberUtils;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.app.mschooling.other.activity.ShowImageActivity;
import com.app.mschooling.base.activity.BaseActivity;
import com.app.mschooling.com.R;
import com.app.mschooling.utils.AppUser;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mschooling.transaction.common.api.Common;
import com.mschooling.transaction.common.api.Status;
import com.mschooling.transaction.request.teacher.DeleteTeacherRequest;
import com.mschooling.transaction.request.teacher.DeleteTeacherRequestList;
import com.mschooling.transaction.request.teacher.DisableTeacherRequest;
import com.mschooling.transaction.request.teacher.EnableTeacherRequest;
import com.mschooling.transaction.response.teacher.DeleteTeacherResponse;
import com.mschooling.transaction.response.teacher.DisableTeacherResponse;
import com.mschooling.transaction.response.teacher.EnableTeacherResponse;
import com.mschooling.transaction.response.teacher.GetTeacherDetailResponse;

import org.greenrobot.eventbus.Subscribe;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;


public class TeacherDetailActivity extends BaseActivity {

    @BindView(R.id.name)
    TextView name;
    @BindView(R.id.enrolmentId)
    TextView enrolmentId;
    @BindView(R.id.mobile)
    TextView mobile;
    @BindView(R.id.email)
    TextView email;
    @BindView(R.id.address)
    TextView address;


    @BindView(R.id.dob)
    TextView dob;
    @BindView(R.id.fatherName)
    TextView fatherName;
    @BindView(R.id.gender)
    TextView gender;
    @BindView(R.id.passcode)
    TextView passcode;
    @BindView(R.id.status)
    TextView status;
    @BindView(R.id.deleteButton)
    LinearLayout deleteButton;
    @BindView(R.id.disable)
    LinearLayout disable;
    @BindView(R.id.enable)
    LinearLayout enable;
    @BindView(R.id.details)
    RelativeLayout details;
    @BindView(R.id.image)
    CircleImageView image;
    @BindView(R.id.mainLayout)
    RelativeLayout mainLayout;
    @BindView(R.id.rootLayout)
    RelativeLayout rootLayout;
    @BindView(R.id.submit)
    Button submit;
    @BindView(R.id.disableDivider)
    View disableDivider;
    @BindView(R.id.deleteDivider)
    View deleteDivider;
   /* @BindView(R.id.coordinatorLayout)
    CoordinatorLayout coordinatorLayout;*/

    private String id;

    GetTeacherDetailResponse response;


    FloatingActionButton fab, fab1, fab2, fab3, fab4, fab5;
    LinearLayout fabLayout1, fabLayout2, fabLayout3, fabLayout4, fabLayout5;
    boolean isFABOpen = false;


    void fab() {
        fabLayout1 = (LinearLayout) findViewById(R.id.fabLayout1);
        fabLayout2 = (LinearLayout) findViewById(R.id.fabLayout2);
        fabLayout3 = (LinearLayout) findViewById(R.id.fabLayout3);
        fabLayout4 = (LinearLayout) findViewById(R.id.fabLayout4);
        fabLayout5 = (LinearLayout) findViewById(R.id.fabLayout5);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab1 = (FloatingActionButton) findViewById(R.id.fab1);
        fab2 = (FloatingActionButton) findViewById(R.id.fab2);
        fab3 = (FloatingActionButton) findViewById(R.id.fab3);
        fab4 = (FloatingActionButton) findViewById(R.id.fab4);
        fab5 = (FloatingActionButton) findViewById(R.id.fab5);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isFABOpen) {
                    showFABMenu();
                } else {
                    closeFABMenu();
                }
            }
        });

       /* coordinatorLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeFABMenu();
            }
        });*/


    }

    private void showFABMenu() {
        isFABOpen = true;
        fabLayout1.setVisibility(View.VISIBLE);
        fabLayout2.setVisibility(View.VISIBLE);
        fabLayout3.setVisibility(View.VISIBLE);
        fabLayout4.setVisibility(View.VISIBLE);
        fabLayout5.setVisibility(View.VISIBLE);
        fab.animate().rotationBy(180);
        fabLayout1.animate().translationY(-getResources().getDimension(R.dimen.dp_55));
        fabLayout2.animate().translationY(-getResources().getDimension(R.dimen.dp_100));
        fabLayout3.animate().translationY(-getResources().getDimension(R.dimen.dp_145));
        fabLayout4.animate().translationY(-getResources().getDimension(R.dimen.dp_190));
        fabLayout5.animate().translationY(-getResources().getDimension(R.dimen.dp_235));
    }

    private void closeFABMenu() {
        isFABOpen = false;
        fab.animate().rotation(0);
        fabLayout1.animate().translationY(0);
        fabLayout2.animate().translationY(0);
        fabLayout3.animate().translationY(0);
        fabLayout4.animate().translationY(0);
        fabLayout5.animate().translationY(0).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                if (!isFABOpen) {
                    fabLayout1.setVisibility(View.GONE);
                    fabLayout2.setVisibility(View.GONE);
                    fabLayout3.setVisibility(View.GONE);
                    fabLayout4.setVisibility(View.GONE);
                    fabLayout5.setVisibility(View.GONE);
                }
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_techer_detail);
        ButterKnife.bind(this);
        mainLayout.setVisibility(View.INVISIBLE);
        id = getIntent().getStringExtra("id");
        toolbarClick(getString(R.string.tool_teacher_details));

        rootLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeFABMenu();
            }
        });

        mainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeFABMenu();
            }
        });
        fab();
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeFABMenu();
                AppUser.getInstance().setUpdate(false);
                AppUser.getInstance().setPosition(0);
                Intent intent = new Intent(getApplicationContext(), ShowTeacherDetailsActivity.class);
                intent.putExtra("id", id);
                startActivity(intent);
            }
        });


        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (response.getTeacherDetailResponse().getTeacherProfileResponse().getProfileFirebase() != null) {
                            if (response.getTeacherDetailResponse().getTeacherProfileResponse().getProfileFirebase().getUrl() != null) {
                                closeFABMenu();
                                Intent intent = new Intent(getApplicationContext(), ShowImageActivity.class);
                                intent.putExtra("url", response.getTeacherDetailResponse().getTeacherProfileResponse().getProfileFirebase().getUrl());
                                intent.putExtra("name", response.getTeacherDetailResponse().getTeacherBasicResponse().getfName());
                                startActivity(intent);
                            }
                        }
                    }
                });
            }
        });


        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeFABMenu();
                if (id.equals("0000002")) {
                    Toast.makeText(TeacherDetailActivity.this, R.string.you_cant_delete_this_record, Toast.LENGTH_SHORT).show();
                    return;
                }
                getDialogDelete(id);
            }
        });


        enable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeFABMenu();
                EnableTeacherRequest request = new EnableTeacherRequest();
                request.getEnrollmentIdList().add(id);
                dialogEnableDisable(getString(R.string.enable), getApiCommonController().enableTeacher(request));
            }
        });

        disable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeFABMenu();
                if (id.equals("0000002")) {
                    Toast.makeText(TeacherDetailActivity.this, R.string.you_cant_disable_this_record, Toast.LENGTH_SHORT).show();
                    return;
                }
                DisableTeacherRequest request = new DisableTeacherRequest();
                request.getEnrollmentIdList().add(id);
                dialogEnableDisable(getString(R.string.disable), getApiCommonController().disableTeacher(request));
            }
        });




    }

    @Subscribe
    public void timeout(String msg) {

    }

    @Override
    public void onResume() {
        super.onResume();
        apiCallBack(getApiCommonController().getTeacherDetails(id));

    }

    @SuppressLint("SetTextI18n")
    @Subscribe
    public void getTeacherDetails(GetTeacherDetailResponse response) {
        if (response.getStatus().value().equals(Status.SUCCESS.value())) {
            this.response = response;
            mainLayout.setVisibility(View.VISIBLE);
            name.setText(response.getTeacherDetailResponse().getTeacherBasicResponse().getfName() + " " + response.getTeacherDetailResponse().getTeacherBasicResponse().getlName());
            enrolmentId.setText(response.getTeacherDetailResponse().getTeacherBasicResponse().getEnrollmentId());
            try {
                if (response.getTeacherDetailResponse().getTeacherBasicResponse().getEmail().equals("") || response.getTeacherDetailResponse().getTeacherBasicResponse().getEmail() == null) {
                    email.setText("N/A");
                } else {
                    email.setText(response.getTeacherDetailResponse().getTeacherBasicResponse().getEmail());
                }
            } catch (Exception e) {
                email.setText("N/A");
            }
//            passcode.setText(response.getTeacherDetailResponse().getTeacherBasicResponse().getPasscode());

            mobile.setText(response.getTeacherDetailResponse().getTeacherBasicResponse().getMobile());
//            clazz.setText(response.getTeacherDetailResponse().getTeacherBasicResponse().getClazz());
//            section.setText(response.getTeacherDetailResponse().getTeacherBasicResponse().getSection());
            gender.setText(response.getTeacherDetailResponse().getTeacherBasicResponse().getGender().value());
            //  fatherName.setText(response.getTeacherResponseList()().get(0).getFather());
            //   motherName.setText(response.getTeacherResponseList()().get(0).getMother());
            dob.setText(response.getTeacherDetailResponse().getTeacherBasicResponse().getDob());
            fatherName.setText(response.getTeacherDetailResponse().getTeacherBasicResponse().getFather());
            //   address.setText(response.getTeacherResponseList()().get(0).getAddress());

            if (response.getTeacherDetailResponse().getTeacherProfileResponse().getProfileFirebase() != null) {
                if (response.getTeacherDetailResponse().getTeacherProfileResponse().getProfileFirebase().getUrl() != null) {
                    Glide.with(this)
                            .load(response.getTeacherDetailResponse().getTeacherProfileResponse().getProfileFirebase().getUrl())
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .skipMemoryCache(true)
                            .into(image);
                } else {
                    image.setImageDrawable(getResources().getDrawable(R.drawable.user));
                }
            } else {
                image.setImageDrawable(getResources().getDrawable(R.drawable.user));
            }
            status.setText(response.getTeacherDetailResponse().getTeacherMiscellaneousResponse().getState().value());
            if (response.getTeacherDetailResponse().getTeacherMiscellaneousResponse().getState().value().equals(Common.State.DELETED.value())) {
                enable.setVisibility(View.GONE);
                disable.setVisibility(View.GONE);
                deleteButton.setVisibility(View.GONE);
                deleteDivider.setVisibility(View.GONE);
                disableDivider.setVisibility(View.GONE);
            } else if (response.getTeacherDetailResponse().getTeacherMiscellaneousResponse().getState().value().equals(Common.State.DISABLED.value())) {
                enable.setVisibility(View.VISIBLE);
                disable.setVisibility(View.GONE);
                deleteButton.setVisibility(View.VISIBLE);
                deleteDivider.setVisibility(View.VISIBLE);
                disableDivider.setVisibility(View.GONE);
            } else {
                enable.setVisibility(View.GONE);
                disable.setVisibility(View.VISIBLE);
                deleteButton.setVisibility(View.VISIBLE);
                deleteDivider.setVisibility(View.VISIBLE);
                disableDivider.setVisibility(View.GONE);
            }
        } else {
            dialogFailed(response.getMessage().getMessage());
        }
    }


    @Subscribe
    public void deleteTeacher(DeleteTeacherResponse response) {
        if (response.getStatus().value().equals(Status.SUCCESS.value())) {
            apiCallBack(getApiCommonController().getTeacherDetails(id));
        } else {
            dialogFailed(response.getMessage().getMessage());
        }
    }


    public void whatsApp(View view) {

        closeFABMenu();
        String mobileNumber = "91" + mobile.getText().toString();
//        boolean isWhatsappInstalled = whatsappInstalledOrNot("com.whatsapp");
        boolean isWhatsappInstalled = true;
        if (isWhatsappInstalled) {

            Intent sendIntent = new Intent("android.intent.action.MAIN");
            sendIntent.setComponent(new ComponentName("com.whatsapp", "com.whatsapp.Conversation"));
            sendIntent.putExtra("jid", PhoneNumberUtils.stripSeparators(mobileNumber) + "@s.whatsapp.net");  //phone number without "+" prefix

            startActivity(sendIntent);
        } else {

            Uri uri = Uri.parse("market://details?id=com.whatsapp");
            Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
            Toast.makeText(getApplicationContext(), "WhatsApp not Installed",
                    Toast.LENGTH_SHORT).show();
            startActivity(goToMarket);
        }


    }

    boolean whatsappInstalledOrNot(String uri) {
        PackageManager pm = getApplicationContext().getPackageManager();
        boolean app_installed = false;
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            app_installed = true;
        } catch (PackageManager.NameNotFoundException e) {
            app_installed = false;
        }
        return app_installed;
    }

    @Subscribe
    public void deleteStudent(DeleteTeacherResponse response) {
        if (response.getStatus().value().equals(Status.SUCCESS.value())) {
            dialogSuccess(response.getMessage().getMessage());
            apiCallBack(getApiCommonController().getTeacherDetails(id));
        } else {
            dialogFailed(response.getMessage().getMessage());
        }
    }


    @Subscribe
    public void disableStudent(DisableTeacherResponse response) {
        if (response.getStatus().value().equals(Status.SUCCESS.value())) {
            dialogSuccess(response.getMessage().getMessage());
            apiCallBack(getApiCommonController().getTeacherDetails(id));
        } else {
            dialogFailed(response.getMessage().getMessage());
        }
    }


    @Subscribe
    public void enableStudent(EnableTeacherResponse response) {
        if (response.getStatus().value().equals(Status.SUCCESS.value())) {
            dialogSuccess(response.getMessage().getMessage());
            apiCallBack(getApiCommonController().getTeacherDetails(id));
        } else {
            dialogFailed(response.getMessage().getMessage());
        }
    }


    public void call(View view) {
        closeFABMenu();
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:0" + mobile.getText().toString()));
        if (ActivityCompat.checkSelfPermission(TeacherDetailActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
        }
        startActivity(callIntent);

    }

    public void addContact(View view) {
        closeFABMenu();
        Intent contactIntent = new Intent(ContactsContract.Intents.Insert.ACTION);
        contactIntent.setType(ContactsContract.RawContacts.CONTENT_TYPE);
        contactIntent
                .putExtra(ContactsContract.Intents.Insert.NAME, name.getText().toString())
                .putExtra(ContactsContract.Intents.Insert.PHONE, mobile.getText().toString());
        startActivityForResult(contactIntent, 1);

    }

    public void sms(View view) {
        closeFABMenu();
        Intent smsIntent = new Intent(Intent.ACTION_SENDTO,Uri.parse("smsto:"+mobile.getText().toString()));
        smsIntent.putExtra("sms_body", "");
        smsIntent.putExtra("address", mobile.getText().toString());
        startActivity(smsIntent);
    }

    public void email(View view) {
        if (email.getText().toString().equals("N/A")) {
            Toast.makeText(TeacherDetailActivity.this, R.string.email_not_found, Toast.LENGTH_SHORT).show();
            return;
        }
        closeFABMenu();
        String[] TO = {email.getText().toString()};
//                String[] CC = {"school_email@gmail.com"};
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.setType("text/plain");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
//                emailIntent.putExtra(Intent.EXTRA_CC, CC);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "");
        emailIntent.putExtra(Intent.EXTRA_TEXT, "");

        try {
            startActivity(Intent.createChooser(emailIntent, getString(R.string.mail_intent_chooser_text)));

        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(TeacherDetailActivity.this,
                    R.string.there_is_no_email_client_installed, Toast.LENGTH_SHORT).show();
        }
    }

    public void getDialogDelete(String enrolmentId) {
        Dialog dialog = new Dialog(TeacherDetailActivity.this);
        dialog.getWindow().requestFeature(1);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        dialog.setContentView(R.layout.delete_dialog);
        dialog.setCancelable(false);
        TextView delete = (TextView) dialog.findViewById(R.id.delete);
        TextView cancel = (TextView) dialog.findViewById(R.id.cancel);
        TextView description = (TextView) dialog.findViewById(R.id.description);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DeleteTeacherRequestList request = new DeleteTeacherRequestList();
                DeleteTeacherRequest request1 = new DeleteTeacherRequest();
                request1.setDescription(description.getText().toString());
                request1.setEnrollmentId(enrolmentId);
                request.getDeleteTeacherRequestList().add(request1);
                apiCallBack(getApiCommonController().deleteTeacher(request));
                dialog.dismiss();

            }
        });
        dialog.show();
    }


}





