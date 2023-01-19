package com.app.mschooling.menu;


import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.app.mschooling.about.AboutAppActivity;
import com.app.mschooling.about.AboutSchoolActivity;
import com.app.mschooling.about.AboutUsActivity;
import com.app.mschooling.session.activity.AcademicSessionListActivity;
import com.app.mschooling.enrollment.ChangePassCodeActivity;
import com.app.mschooling.help_support_report_feedback.activity.ExternalSupportActivity;
import com.app.mschooling.help_support_report_feedback.activity.FeedBackActivity;
import com.app.mschooling.other.activity.LanguageSelectionActivity;
import com.app.mschooling.notification.activity.NotificationListActivity;
import com.app.mschooling.other.activity.PrintIdCardActivity;
import com.app.mschooling.help_support_report_feedback.activity.ReportActivityActivityIssue;
import com.app.mschooling.other.activity.TncActivity;
import com.app.mschooling.students.profile.activity.StudentProfileActivity;
import com.app.mschooling.base.fragment.BaseFragment;
import com.app.mschooling.com.R;
import com.app.mschooling.utils.Helper;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.mschooling.transaction.common.api.Common;
import com.mschooling.transaction.common.api.Status;
import com.mschooling.transaction.request.feedback.AddFeedbackRequest;
import com.mschooling.transaction.response.feedback.AddFeedbackResponse;

import org.greenrobot.eventbus.Subscribe;

public class StudentMenuFragment extends BaseFragment {

    LinearLayout menu1;
    LinearLayout menu2;
    LinearLayout logout;
    LinearLayout feedback;
    LinearLayout share;
    LinearLayout myProfile;
    LinearLayout tnc;
    LinearLayout privacy;
    LinearLayout schoolInfo;
    LinearLayout aboutUs;
    LinearLayout rateUs;
    LinearLayout language;
    LinearLayout aboutApp, idCard, notification;
    LinearLayout layoutReportIssue, layoutFeedback, layoutSuggestNewFeature, contactUs;
    BottomSheetDialog dialog;
    LinearLayout changePassCode;
    LinearLayout academicSession;
    @Nullable
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_menu_student, container, false);
        toolbarClick(view, getString(R.string.title_menu));
        menu1 = view.findViewById(R.id.menu1);
        menu2 = view.findViewById(R.id.menu2);
        logout = view.findViewById(R.id.logout);
        schoolInfo = view.findViewById(R.id.schoolInfo);
        feedback = view.findViewById(R.id.feedback);
        share = view.findViewById(R.id.share);
        myProfile = view.findViewById(R.id.myProfile);
        aboutUs = view.findViewById(R.id.aboutUs);
        aboutApp = view.findViewById(R.id.aboutApp);
        rateUs = view.findViewById(R.id.rateUs);
        idCard = view.findViewById(R.id.idCard);
        language = view.findViewById(R.id.language);
        tnc = view.findViewById(R.id.tnc);
        privacy = view.findViewById(R.id.privacy);
        layoutReportIssue = view.findViewById(R.id.layoutReportIssue);
        layoutFeedback = view.findViewById(R.id.layoutFeedback);
        layoutSuggestNewFeature = view.findViewById(R.id.layoutSuggestNewFeature);
        contactUs = view.findViewById(R.id.contactUs);
        changePassCode = view.findViewById(R.id.changePassCode);
        notification = view.findViewById(R.id.notification);
        academicSession = view.findViewById(R.id.academicSession);

        academicSession.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), AcademicSessionListActivity.class));
            }
        });

        notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), NotificationListActivity.class);
                startActivity(intent);
            }
        });
        language.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), LanguageSelectionActivity.class);
                startActivity(intent);
            }
        });


        feedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), FeedBackActivity.class));
            }
        });


        aboutUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), AboutUsActivity.class));
            }
        });
        aboutApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), AboutAppActivity.class));
            }
        });

        tnc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), TncActivity.class);
                intent.putExtra("from", "tnc");
                startActivity(intent);
            }
        });
        privacy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), TncActivity.class);
                intent.putExtra("from", "");
                startActivity(intent);
            }
        });

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Helper.share(getActivity());
            }
        });

        rateUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("market://details?id=" + getActivity().getPackageName());
                Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
                // To count with Play market backstack, After pressing back button,
                // to taken back to our application, we need to add following flags to intent.
                goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                        Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                        Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                try {
                    startActivity(goToMarket);
                } catch (ActivityNotFoundException e) {
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("http://play.google.com/store/apps/details?id=" + getActivity().getPackageName())));
                }
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logoutEvent();
            }
        });


        myProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), StudentProfileActivity.class));
//                startActivity(new Intent(getActivity(), MyProfileActivity.class));
            }
        });


        schoolInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), AboutSchoolActivity.class));
            }
        });
        idCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), PrintIdCardActivity.class));
            }
        });


        changePassCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(getActivity(), ChangePassCodeActivity.class));
            }
        });
        layoutReportIssue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ReportActivityActivityIssue.class);
                startActivity(intent);
            }
        });
        layoutFeedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), FeedBackActivity.class);
                startActivity(intent);
            }
        });
        contactUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ExternalSupportActivity.class);
                startActivity(intent);
            }
        });
        layoutSuggestNewFeature.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View view = getLayoutInflater().inflate(R.layout.botto_sheet_dialog_layout, null);
                TextView textName = view.findViewById(R.id.textName);
                EditText etEditText = view.findViewById(R.id.etEditText);
                TextView cancel = view.findViewById(R.id.cancel);
                TextView send = view.findViewById(R.id.send);
                TextView attachment = view.findViewById(R.id.attachment);
                attachment.setVisibility(View.GONE);
                dialog = new BottomSheetDialog(getActivity());
                dialog.setContentView(view);
                dialog.setCancelable(false);
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                send.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (etEditText.getText().toString().equals("")) {
                            dialogError(getString(R.string.please_suggest_new_features));
                        } else {
                            AddFeedbackRequest request = new AddFeedbackRequest();
                            request.setContent(etEditText.getText().toString());
                            request.setFeedbackType(Common.FeedbackType.NEW_FEATURE);
                            apiCallBack(getApiCommonController().feedback(request));
                        }
                    }
                });

                dialog.show();
            }
        });


        return view;
    }


    public void comingSoon(View view) {
        Toast.makeText(getContext(), getString(R.string.coming_soon), Toast.LENGTH_SHORT).show();
    }


    @Subscribe
    public void feedback(AddFeedbackResponse response) {
        if (response.getStatus().value() == Status.SUCCESS.value()) {
            dialogSuccess(response.getMessage().getMessage());
            dialog.dismiss();
        } else {
            dialogFailed(response.getMessage().getMessage());
        }
    }
}

