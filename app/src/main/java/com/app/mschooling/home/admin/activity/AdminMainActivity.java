package com.app.mschooling.home.admin.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.app.mschooling.about.AboutAppActivity;
import com.app.mschooling.about.AboutSchoolActivity;
import com.app.mschooling.about.AboutUsActivity;
import com.app.mschooling.application.activity.AddApplicationReasonActivity;
import com.app.mschooling.class_section_subject.activity.AddClassActivity;
import com.app.mschooling.complaint.activity.AddComplaintReasonActivity;
import com.app.mschooling.class_section_subject.activity.AddSubjectActivity;
import com.app.mschooling.class_section_subject.fragment.ClassSubjectSelectionFragment;
import com.app.mschooling.admin.activity.AdminProfileActivity;
import com.app.mschooling.enrollment.ChangePassCodeActivity;
import com.app.mschooling.help_support_report_feedback.activity.HelpActivity;
import com.app.mschooling.birthday.activity.BirthdayActivity;
import com.app.mschooling.other.activity.QRCodeActivity;
import com.app.mschooling.other.activity.ShowImageActivity;
import com.app.mschooling.other.activity.TncActivity;
import com.app.mschooling.base.activity.BaseActivity;
import com.app.mschooling.com.BuildConfig;
import com.app.mschooling.com.R;
import com.app.mschooling.home.admin.fragment.AdminHomeFragment;
import com.app.mschooling.menu.AdminMenuFragment;
import com.app.mschooling.teachers.profile.fragment.TeacherTimeTableAllocationFragment;
import com.app.mschooling.panding.fragment.PendingRequestFragment;
import com.app.mschooling.utils.AdmobHelper;
import com.app.mschooling.utils.AppUser;
import com.app.mschooling.utils.Helper;
import com.app.mschooling.utils.ParameterConstant;
import com.app.mschooling.utils.Preferences;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.mschooling.transaction.common.api.Status;
import com.mschooling.transaction.common.user.UserSetup;
import com.mschooling.transaction.response.version.GetVersionResponse;

import org.greenrobot.eventbus.Subscribe;

public class AdminMainActivity extends BaseActivity {

    /*
    OCclhRt7PP0
    <#> mSchooling: Your verification code is 1234 OCclhRt7PP0
     */


    @SuppressLint("StaticFieldLeak")
    public static AdminMainActivity context;
    boolean foregroundChecker;
    public BottomNavigationView navigation;


    private final BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        Fragment fragment = null;


        @SuppressLint("NonConstantResourceId")
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {

                case R.id.navigation_home:
                    fragment = new AdminHomeFragment();
                    break;
                case R.id.navigation_timetable:
                    fragment = new TeacherTimeTableAllocationFragment();
                    break;
                case R.id.navigation_pending:
                    fragment = new PendingRequestFragment();
                    break;

                case R.id.navigation_menu:
                    fragment = new AdminMenuFragment();
                    break;

            }
            return AdminMainActivity.this.loadFragment(fragment);
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        updateToken();
        setContentView(R.layout.activity_main_navigation);
        if (ParameterConstant.isMSchooling(getApplicationContext())){
            findViewById(R.id.logo).setVisibility(View.VISIBLE);
        }else if (ParameterConstant.isLittleSteps(getApplicationContext())) {
            findViewById(R.id.logo).setVisibility(View.GONE);
        }

        if (Preferences.getInstance(getApplicationContext()).getYoutubeApiKey().isEmpty()){
            apiCallBackWithout(getApiCommonController().getGoogleApiKey());
        }
//        birthdayDialog();
//        Preferences.getInstance(context).setAdsCycle(2);
        /*
        'AdsService.startService(this);
        AppSignatureHashHelper appSignatureHashHelper = new AppSignatureHashHelper(this);
        Log.i("http ", "HashKey: " + appSignatureHashHelper.getAppSignatures().get(0));
        */

//        startActivity(new Intent(getApplicationContext(), BirthdayActivity.class));

        context = this;
        try {
            navigation = (BottomNavigationView) findViewById(R.id.navigation);
            navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
            loadFragment(new AdminHomeFragment());
            if (Preferences.getInstance(getApplicationContext()).getFirstTimeLogin() == 0) {
                Preferences.getInstance(getApplicationContext()).setFirstTimeLogin(1);
                Dialog dialog = new Dialog(this);
                dialog.getWindow().requestFeature(1);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
                dialog.setContentView(R.layout.dialog_alert);
                dialog.setCancelable(false);
                TextView message = dialog.findViewById(R.id.message);
                TextView submit = dialog.findViewById(R.id.submit);
                TextView cancel = dialog.findViewById(R.id.cancel);
                submit.setText(getString(R.string.ok));
                message.setText(R.string.admin_login_message);
                cancel.setVisibility(View.GONE);
                submit.setOnClickListener(v -> dialog.dismiss());
                cancel.setOnClickListener(v -> dialog.dismiss());
                dialog.show();
                Helper.welcomeVoice(this);
            }
            apiCallBackWithout(getApiCommonController().version(Helper.getVersion(this)));
            Preferences.getInstance(getApplicationContext()).setLogin(true);
        } catch (Exception e) {
            dialogLogout();
        }

        getDailyConfiguration();

    }


    private boolean loadFragment(Fragment fragment) {
        if (fragment == null) {
            return false;
        }
//        getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        Preferences.getInstance(getApplicationContext()).setPageNo1(0);
        Preferences.getInstance(getApplicationContext()).setPageNo2(0);
        getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, fragment).commit();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        if (fragmentManager.getBackStackEntryCount() > 0) {
            fragmentManager.popBackStackImmediate();
        } else {
            super.onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        foregroundChecker = false;
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        context = this;
        try {
            foregroundChecker = true;
            navigationDrawer();
        } catch (Exception e) {
            dialogLogout();
        }

    }


    @Override
    protected void onRestart() {
        super.onRestart();
        if (!BuildConfig.DEBUG) {
            AdmobHelper.showInterstitialAds(this);
        }
        AdmobHelper.showInterstitialAds(this);

    }

    boolean isExit = false;

    @Override
    public void onBackPressed() {
        if (navigation.getMenu().findItem(R.id.navigation_home).isChecked()) {
            if (isExit) {
                super.onBackPressed();
            } else {
                Toast.makeText(this, R.string.backpress_message, Toast.LENGTH_SHORT).show();
            }
            isExit = true;
            new Handler().postDelayed(() -> isExit = false, 2000);
        } else {
            navigation.setSelectedItemId(R.id.navigation_home);
        }

    }

    public boolean isForeground() {
        return context.foregroundChecker;
    }


    @Subscribe
    public void version(GetVersionResponse response) {
        if (response.getStatus().value().equals(Status.SUCCESS.value())) {
            AppUser.getInstance().setVersionResponse(response);
            if (response.getVersionResponse().isDialogVisible()) {
                dialogUpdate(response.getVersionResponse().getDialogMessage(), response.getVersionResponse().isForceUpdate());
            }
            if (response.getVersionResponse().isConfigUpdate()) {
                apiCallBackWithout(getApiCommonController().configuration());
            }
        } else {
            dialogFailed(response.getMessage().getMessage());
        }
    }

    public void comingSoon(View view) {
        Toast.makeText(this, R.string.coming_soon, Toast.LENGTH_SHORT).show();
    }


    @SuppressLint({"UseCompatLoadingForDrawables", "SetTextI18n"})
    void navigationDrawer() {
        NavigationView navigationView = findViewById(R.id.nav_view);
        View view = navigationView.getHeaderView(0);
        UserSetup user = Preferences.getInstance(getApplicationContext()).getConfiguration().getUserSetup();

        TextView name = view.findViewById(R.id.name);
        TextView enrollmentId = view.findViewById(R.id.enrollmentId);
        TextView mobile = view.findViewById(R.id.mobile);
        TextView role = view.findViewById(R.id.role);
        ImageView image = view.findViewById(R.id.image);
        ProgressBar progressBar = view.findViewById(R.id.progressBar);

        if (user.getUrl() == null) {
            image.setImageDrawable(getResources().getDrawable(R.drawable.user));
        } else {
            Glide.with(this)
                    .load(user.getUrl())
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            progressBar.setVisibility(View.GONE);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            progressBar.setVisibility(View.GONE);
                            return false;
                        }
                    })
                    .skipMemoryCache(true)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .into(image);
        }

        name.setText(user.getName());
        enrollmentId.setText(user.getEnrollmentId());
        mobile.setText(user.getMobile());
        role.setText(Preferences.getInstance(getApplicationContext()).getROLE());

        image.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), ShowImageActivity.class);
            intent.putExtra("url", user.getUrl());
            intent.putExtra("name", user.getName());
            startActivity(intent);
        });

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        LinearLayout hamburger = findViewById(R.id.hamburger);
        hamburger.setOnClickListener(v -> drawer.openDrawer(GravityCompat.START));


        LinearLayout timeTable;

        LinearLayout logout;
        LinearLayout share;
        LinearLayout myProfile;
        LinearLayout tnc;
        LinearLayout privacy;
        LinearLayout schoolInfo;
        LinearLayout aboutUs;
        LinearLayout aboutApp, reportSupport;

        LinearLayout generateQRCode;
        LinearLayout addSubject;
        LinearLayout addClass;
        LinearLayout addComplaintReason;
        LinearLayout addApplicationReason;
        LinearLayout theme;
        LinearLayout changePassCode;


        timeTable = findViewById(R.id.timeTable);
        logout = findViewById(R.id.logout);
        schoolInfo = findViewById(R.id.schoolInfo);
        share = findViewById(R.id.share);
        myProfile = findViewById(R.id.myProfile);
        aboutUs = findViewById(R.id.aboutUs);
        aboutApp = findViewById(R.id.aboutApp);
        reportSupport = findViewById(R.id.reportSupport);
        tnc = findViewById(R.id.tnc);
        privacy = findViewById(R.id.privacy);
        generateQRCode = findViewById(R.id.generate_qr_code);
        addSubject = findViewById(R.id.addSubject);
        addClass = findViewById(R.id.addClass);
        addComplaintReason = findViewById(R.id.addComplaintReason);
        addApplicationReason = findViewById(R.id.addApplicationReason);
        theme = findViewById(R.id.theme);
        changePassCode = findViewById(R.id.changePassCode);


        TextView version = findViewById(R.id.version);
        version.setText("Version " + Helper.getVersion(this));
        myProfile.setOnClickListener(v -> {
            drawer.closeDrawer(GravityCompat.START);
            Intent intent = new Intent(getApplicationContext(), AdminProfileActivity.class);
            intent.putExtra("position", 0);
            intent.putExtra("update", false);
            startActivity(intent);
        });
        timeTable.setOnClickListener(v -> {
            drawer.closeDrawer(GravityCompat.START);
            Intent intent = new Intent(getApplicationContext(), ClassSubjectSelectionFragment.class);
            intent.putExtra("edit", false);
            startActivity(intent);
        });


        aboutUs.setOnClickListener(v -> {
            drawer.closeDrawer(GravityCompat.START);
            startActivity(new Intent(getApplicationContext(), AboutUsActivity.class));
        });
        aboutApp.setOnClickListener(v -> {
            drawer.closeDrawer(GravityCompat.START);
            startActivity(new Intent(getApplicationContext(), AboutAppActivity.class));
        });

        tnc.setOnClickListener(v -> {
            drawer.closeDrawer(GravityCompat.START);
            Intent intent = new Intent(getApplicationContext(), TncActivity.class);
            intent.putExtra("from", "tnc");
            startActivity(intent);
        });
        privacy.setOnClickListener(v -> {
            drawer.closeDrawer(GravityCompat.START);
            Intent intent = new Intent(getApplicationContext(), TncActivity.class);
            intent.putExtra("from", "");
            startActivity(intent);
        });
        reportSupport.setOnClickListener(v -> {
            drawer.closeDrawer(GravityCompat.START);
            Intent intent = new Intent(getApplicationContext(), HelpActivity.class);
            startActivity(intent);
        });

        share.setOnClickListener(v -> {
            drawer.closeDrawer(GravityCompat.START);
            Helper.share(AdminMainActivity.this);
        });

        logout.setOnClickListener(v -> {
            drawer.closeDrawer(GravityCompat.START);
            logoutEvent();
        });


        schoolInfo.setOnClickListener(v -> {
            drawer.closeDrawer(GravityCompat.START);
            startActivity(new Intent(getApplicationContext(), AboutSchoolActivity.class));
        });


        generateQRCode.setOnClickListener(v -> {
            drawer.closeDrawer(GravityCompat.START);
            startActivity(new Intent(getApplicationContext(), QRCodeActivity.class));
        });

        addSubject.setOnClickListener(v -> {
            drawer.closeDrawer(GravityCompat.START);
            Intent intent = new Intent(getApplicationContext(), AddSubjectActivity.class);
            intent.putExtra("id", "");
            startActivity(intent);
        });

        addClass.setOnClickListener(v -> {
            drawer.closeDrawer(GravityCompat.START);
            startActivity(new Intent(getApplicationContext(), AddClassActivity.class));
        });

        addComplaintReason.setOnClickListener(v -> {
            drawer.closeDrawer(GravityCompat.START);
            startActivity(new Intent(getApplicationContext(), AddComplaintReasonActivity.class));
        });
        addApplicationReason.setOnClickListener(v -> {
            drawer.closeDrawer(GravityCompat.START);
            startActivity(new Intent(getApplicationContext(), AddApplicationReasonActivity.class));
        });

        theme.setOnClickListener(v -> drawer.closeDrawer(GravityCompat.START));


        changePassCode.setOnClickListener(v -> {
            drawer.closeDrawer(GravityCompat.START);
            startActivity(new Intent(getApplicationContext(), ChangePassCodeActivity.class));
        });
    }




}
