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
import com.app.mschooling.about.AboutUsActivity;
import com.app.mschooling.session.activity.AcademicSessionListActivity;
import com.app.mschooling.enrollment.ChangePassCodeActivity;
import com.app.mschooling.help_support_report_feedback.activity.ExternalSupportActivity;
import com.app.mschooling.help_support_report_feedback.activity.FeedBackActivity;
import com.app.mschooling.other.activity.LanguageSelectionActivity;
import com.app.mschooling.notification.activity.NotificationListActivity;
import com.app.mschooling.other.activity.QRCodeActivity;
import com.app.mschooling.help_support_report_feedback.activity.ReportActivityActivityIssue;
import com.app.mschooling.about.AboutSchoolActivity;
import com.app.mschooling.setting.activity.SettingListActivity;
import com.app.mschooling.timetable.activity.ClassTimeTableActivity;
import com.app.mschooling.other.activity.TncActivity;
import com.app.mschooling.base.fragment.BaseFragment;
import com.app.mschooling.com.R;
import com.app.mschooling.utils.Helper;
//import com.app.mschooling.zoomsdk.activity.onlineclasses.UpdateZoomCredentialActivity;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.mschooling.transaction.common.api.Common;
import com.mschooling.transaction.common.api.Status;
import com.mschooling.transaction.request.feedback.AddFeedbackRequest;
import com.mschooling.transaction.response.feedback.AddFeedbackResponse;

import org.greenrobot.eventbus.Subscribe;

public class AdminMenuFragment extends BaseFragment {

    LinearLayout showTimeTable;
    LinearLayout logout;
    LinearLayout share;
    LinearLayout myProfile;
    LinearLayout tnc;
    LinearLayout privacy;
    LinearLayout schoolInfo;
    LinearLayout aboutUs;
    LinearLayout rateUs;
    LinearLayout aboutApp, language, notification;
    LinearLayout generateQRCode;
    LinearLayout theme;
    LinearLayout changePassCode;
    LinearLayout setup;
    LinearLayout layoutReportIssue, layoutFeedback, layoutSuggestNewFeature, contactUs;
    BottomSheetDialog dialog;

    public static String customFontString = "";

    LinearLayout academicSession;

    TextView general, setting, help, support, other;


    @Nullable
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_menu_admin, container, false);
        toolbarClick(view, getString(R.string.title_menu));


        showTimeTable = view.findViewById(R.id.showTimeTable);
        logout = view.findViewById(R.id.logout);
        schoolInfo = view.findViewById(R.id.schoolInfo);
        share = view.findViewById(R.id.share);
        myProfile = view.findViewById(R.id.myProfile);
        aboutUs = view.findViewById(R.id.aboutUs);
        aboutApp = view.findViewById(R.id.aboutApp);
        language = view.findViewById(R.id.language);
        rateUs = view.findViewById(R.id.rateUs);
        notification = view.findViewById(R.id.notification);
        tnc = view.findViewById(R.id.tnc);
        privacy = view.findViewById(R.id.privacy);
        layoutReportIssue = view.findViewById(R.id.layoutReportIssue);
        layoutFeedback = view.findViewById(R.id.layoutFeedback);
        layoutSuggestNewFeature = view.findViewById(R.id.layoutSuggestNewFeature);
        contactUs = view.findViewById(R.id.contactUs);
        generateQRCode = view.findViewById(R.id.generate_qr_code);
        theme = view.findViewById(R.id.theme);
        changePassCode = view.findViewById(R.id.changePassCode);
        setup = view.findViewById(R.id.setup);
        academicSession = view.findViewById(R.id.academicSession);
        general = view.findViewById(R.id.general);
        setting = view.findViewById(R.id.setting);
        help = view.findViewById(R.id.help);
        support = view.findViewById(R.id.support);
        other = view.findViewById(R.id.other);

        Helper.setTextGradient(getContext(), general);
        Helper.setTextGradient(getContext(), setting);
        Helper.setTextGradient(getContext(), help);
        Helper.setTextGradient(getContext(), support);
        Helper.setTextGradient(getContext(), other);

//        setup.setVisibility(View.GONE);

        academicSession.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), AcademicSessionListActivity.class));
            }
        });


        setup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SettingListActivity.class);
                startActivity(intent);
            }
        });
        layoutReportIssue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ReportActivityActivityIssue.class);
                startActivity(intent);
            }
        });
        notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), NotificationListActivity.class);
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


        showTimeTable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), ClassTimeTableActivity.class));
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

        language.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), LanguageSelectionActivity.class);
                startActivity(intent);
            }
        });

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Helper.share(getActivity());
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logoutEvent();
            }
        });

        myProfile.setVisibility(View.GONE);


        schoolInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), AboutSchoolActivity.class));
            }
        });


        generateQRCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), QRCodeActivity.class));
            }
        });


        theme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


        changePassCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), ChangePassCodeActivity.class));
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

