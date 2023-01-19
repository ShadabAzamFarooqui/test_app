package com.app.mschooling.students.detail.activity;

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

import androidx.core.app.ActivityCompat;

import com.app.mschooling.other.activity.ShowImageActivity;
import com.app.mschooling.base.activity.BaseActivity;
import com.app.mschooling.com.R;
import com.app.mschooling.utils.AppUser;
import com.app.mschooling.utils.Preferences;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mschooling.transaction.common.api.Common;
import com.mschooling.transaction.common.api.Status;
import com.mschooling.transaction.request.student.DeleteStudentRequest;
import com.mschooling.transaction.request.student.DeleteStudentRequestList;
import com.mschooling.transaction.request.student.DisableStudentRequest;
import com.mschooling.transaction.request.student.EnableStudentRequest;
import com.mschooling.transaction.response.student.DeleteStudentResponse;
import com.mschooling.transaction.response.student.DisableStudentResponse;
import com.mschooling.transaction.response.student.EnableStudentResponse;
import com.mschooling.transaction.response.student.GetStudentDetailResponse;

import org.greenrobot.eventbus.Subscribe;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;


public class StudentBasicDetailActivity extends BaseActivity {
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
    @BindView(R.id.clazz)
    TextView clazz;
    @BindView(R.id.section)
    TextView section;
    @BindView(R.id.rollNumber)
    TextView rollNumber;
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
    @BindView(R.id.deletedMessage)
    TextView deletedMessage;
  /*  @BindView(R.id.coordinatorLayout)
    CoordinatorLayout coordinatorLayout;*/


    private String id;
    GetStudentDetailResponse response;
    boolean isDeleted;


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


    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_detail);
        ButterKnife.bind(this);
        toolbarClick(getString(R.string.tool_student_details));
        id = getIntent().getStringExtra("id");

        fab();

        mainLayout.setVisibility(View.GONE);
        fab.setVisibility(View.GONE);
        submit.setVisibility(View.GONE);

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
        enable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeFABMenu();
                EnableStudentRequest request = new EnableStudentRequest();
                request.getEnrollmentIdList().add(id);
                dialogEnableDisable(getString(R.string.enable), getApiCommonController().enableStudent(request));
            }
        });


        disable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeFABMenu();
                if (id.equals("0000003")) {
                    Toast.makeText(StudentBasicDetailActivity.this, "You can't disable this record", Toast.LENGTH_SHORT).show();
                    return;
                }
                DisableStudentRequest request = new DisableStudentRequest();
                request.getEnrollmentIdList().add(id);
                dialogEnableDisable(getString(R.string.disable), getApiCommonController().disableStudent(request));

            }
        });


        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeFABMenu();
                if (id.equals("0000003")) {
                    Toast.makeText(StudentBasicDetailActivity.this, "You can't delete this record", Toast.LENGTH_SHORT).show();
                    return;
                }
                getDialogDelete();
            }
        });


        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeFABMenu();
                AppUser.getInstance().setUpdate(false);
                AppUser.getInstance().setPosition(0);
                Intent intent = new Intent(getApplicationContext(), StudentCompleteDetailActivity.class);
                intent.putExtra("id", id);
                intent.putExtra("isDeleted", isDeleted);
                intent.putExtra("classId", response.getStudentDetailResponse().getStudentBasicResponse().getClassId());
                intent.putExtra("sectionId", response.getStudentDetailResponse().getStudentBasicResponse().getSectionId());
                intent.putExtra("enrollmentId", response.getStudentDetailResponse().getStudentBasicResponse().getEnrollmentId());
                startActivity(intent);
            }
        });

    }


    public void call(View view) {
        closeFABMenu();
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:0" + mobile.getText().toString()));
        if (ActivityCompat.checkSelfPermission(StudentBasicDetailActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
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
        Intent smsIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:" + mobile.getText().toString()));
        smsIntent.putExtra("sms_body", "");
        smsIntent.putExtra("address", mobile.getText().toString());
        startActivity(smsIntent);

    }

    public void email(View view) {
        closeFABMenu();
        if (email.getText().toString().equals("N/A")) {
            Toast.makeText(StudentBasicDetailActivity.this, "Email not found", Toast.LENGTH_SHORT).show();
            return;
        }
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
            startActivity(Intent.createChooser(emailIntent, "Send mail..."));

        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(StudentBasicDetailActivity.this,
                    "There is no email client installed.", Toast.LENGTH_SHORT).show();
        }
    }


    @Subscribe
    public void timeout(String msg) {

    }

    @Override
    public void onResume() {
        super.onResume();
        apiCallBack(getApiCommonController().getStudentDetails(getIntent().getStringExtra("id")));
    }

    @SuppressLint("RestrictedApi")
    @Subscribe
    public void getStudentDetails(GetStudentDetailResponse response) {
        if (response.getStatus().value().equals(Status.SUCCESS.value())) {
            this.response = response;
            mainLayout.setVisibility(View.VISIBLE);
            fab.setVisibility(View.VISIBLE);
            submit.setVisibility(View.VISIBLE);
            name.setText(response.getStudentDetailResponse().getStudentBasicResponse().getfName() + " " + response.getStudentDetailResponse().getStudentBasicResponse().getlName());
            enrolmentId.setText(response.getStudentDetailResponse().getStudentBasicResponse().getEnrollmentId());
            try {
                if (response.getStudentDetailResponse().getStudentBasicResponse().getEmail().equals("") || response.getStudentDetailResponse().getStudentBasicResponse().getEmail() == null) {
                    email.setText("N/A");
                } else {
                    email.setText(response.getStudentDetailResponse().getStudentBasicResponse().getEmail());
                }
            } catch (Exception e) {
                email.setText("N/A");
            }
            if (Preferences.getInstance(getApplicationContext()).getROLE().equals(Common.Role.TEACHER.value())) {
//                passcode.setText("****");
            } else {
//                passcode.setText(response.getStudentDetailResponse().getStudentBasicResponse().getPasscode());
            }

            mobile.setText(response.getStudentDetailResponse().getStudentBasicResponse().getMobile());
            clazz.setText(response.getStudentDetailResponse().getStudentBasicResponse().getClassName());
            section.setText(response.getStudentDetailResponse().getStudentBasicResponse().getSectionName());
            gender.setText(response.getStudentDetailResponse().getStudentBasicResponse().getGender().value());
            fatherName.setText("(C/o " + response.getStudentDetailResponse().getStudentPersonalResponse().getfName() + ")");
            rollNumber.setText(response.getStudentDetailResponse().getStudentBasicResponse().getRollNumber());

            if (response.getStudentDetailResponse().getStudentProfileResponse().getProfileFirebase() != null) {
                if (response.getStudentDetailResponse().getStudentProfileResponse().getProfileFirebase().getUrl() != null) {
                    Glide.with(StudentBasicDetailActivity.this)
                            .load(response.getStudentDetailResponse().getStudentProfileResponse().getProfileFirebase().getUrl())
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .skipMemoryCache(true)
                            .into(image);


                } else {
                    image.setImageDrawable(getResources().getDrawable(R.drawable.user));
                }
            } else {
                image.setImageDrawable(getResources().getDrawable(R.drawable.user));
            }

            image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    closeFABMenu();
                    if (response.getStudentDetailResponse().getStudentProfileResponse().getProfileFirebase() != null) {
                        if (response.getStudentDetailResponse().getStudentProfileResponse().getProfileFirebase().getUrl() != null) {
                            Intent intent = new Intent(getApplicationContext(), ShowImageActivity.class);
                            intent.putExtra("url", response.getStudentDetailResponse().getStudentProfileResponse().getProfileFirebase().getUrl());
                            intent.putExtra("name", response.getStudentDetailResponse().getStudentBasicResponse().getfName());
                            startActivity(intent);
                        }
                    }
                }
            });
            status.setText(response.getStudentDetailResponse().getStudentMiscellaneousResponse().getState().value());
            deletedMessage.setText(response.getStudentDetailResponse().getStudentMiscellaneousResponse().getDescription());
            if (response.getStudentDetailResponse().getStudentMiscellaneousResponse().getState().value().equals(Common.State.DELETED.value())) {
                isDeleted = true;
                enable.setVisibility(View.GONE);
                disable.setVisibility(View.GONE);
                deleteButton.setVisibility(View.GONE);
                deleteDivider.setVisibility(View.GONE);
                disableDivider.setVisibility(View.GONE);
                deletedMessage.setVisibility(View.VISIBLE);
            } else if (response.getStudentDetailResponse().getStudentMiscellaneousResponse().getState().value().equals(Common.State.DISABLED.value())) {
                enable.setVisibility(View.VISIBLE);
                disable.setVisibility(View.GONE);
                deleteButton.setVisibility(View.VISIBLE);
                deleteDivider.setVisibility(View.VISIBLE);
                disableDivider.setVisibility(View.GONE);
                deletedMessage.setVisibility(View.GONE);
            } else {
                enable.setVisibility(View.GONE);
                disable.setVisibility(View.VISIBLE);
                deleteButton.setVisibility(View.VISIBLE);
                deleteDivider.setVisibility(View.VISIBLE);
                disableDivider.setVisibility(View.GONE);
                deletedMessage.setVisibility(View.GONE);
            }


            if (!Preferences.getInstance(getApplicationContext()).getROLE().equals(Common.Role.ADMIN.value())) {
                enable.setVisibility(View.GONE);
                disable.setVisibility(View.GONE);
                deleteButton.setVisibility(View.GONE);
                deleteDivider.setVisibility(View.GONE);
                disableDivider.setVisibility(View.GONE);
            }

        } else {
            dialogFailed(response.getMessage().getMessage());
        }
    }


    @Subscribe
    public void deleteStudent(DeleteStudentResponse response) {
        if (response.getStatus().value().equals(Status.SUCCESS.value())) {
//            dialogSuccess(response.getMessage().getMessage());
            showToast(response.getMessage().getMessage());
            apiCallBack(getApiCommonController().getStudentDetails(getIntent().getStringExtra("id")));
        } else {
            dialogFailed(response.getMessage().getMessage());
        }
    }


    @Subscribe
    public void disableStudent(DisableStudentResponse response) {
        if (response.getStatus().value().equals(Status.SUCCESS.value())) {
            dialogSuccess(response.getMessage().getMessage());
            apiCallBack(getApiCommonController().getStudentDetails(getIntent().getStringExtra("id")));
        } else {
            dialogFailed(response.getMessage().getMessage());
        }
    }


    @Subscribe
    public void enableStudent(EnableStudentResponse response) {
        if (response.getStatus().value().equals(Status.SUCCESS.value())) {
            dialogSuccess(response.getMessage().getMessage());
            apiCallBack(getApiCommonController().getStudentDetails(getIntent().getStringExtra("id")));
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
            Toast.makeText(StudentBasicDetailActivity.this, "WhatsApp not Installed",
                    Toast.LENGTH_SHORT).show();
            startActivity(goToMarket);
        }


    }

    void attachment(View view) {
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(StudentBasicDetailActivity.this, "No Attachment Found", Toast.LENGTH_SHORT).show();
            }
        });
    }

    boolean whatsappInstalledOrNot(String uri) {
        PackageManager pm = StudentBasicDetailActivity.this.getPackageManager();
        boolean app_installed = false;
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            app_installed = true;
        } catch (PackageManager.NameNotFoundException e) {
            app_installed = false;
        }
        return app_installed;
    }


    public void getDialogDelete() {
        Dialog dialog = new Dialog(StudentBasicDetailActivity.this);
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
                DeleteStudentRequestList request = new DeleteStudentRequestList();
                DeleteStudentRequest obj = new DeleteStudentRequest();
                obj.setEnrollmentId(id);
                obj.setDescription(description.getText().toString());
                request.getDeleteStudentRequestList().add(obj);
                apiCallBack(getApiCommonController().deleteStudent(request));
                dialog.dismiss();
            }
        });
        dialog.show();
    }
}






