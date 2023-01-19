package com.app.mschooling.home.student.activity;

import android.app.FragmentManager;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
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

import com.app.mschooling.about.AboutSchoolActivity;
import com.app.mschooling.about.AboutUsActivity;
import com.app.mschooling.birthday.activity.BirthdayActivity;
import com.app.mschooling.students.profile.activity.StudentProfileActivity;
import com.app.mschooling.enrollment.ChangePassCodeActivity;
import com.app.mschooling.help_support_report_feedback.activity.HelpActivity;
import com.app.mschooling.other.activity.QRCodeActivity;
import com.app.mschooling.other.activity.ShowImageActivity;
import com.app.mschooling.other.activity.TncActivity;
import com.app.mschooling.base.activity.BaseActivity;
import com.app.mschooling.com.BuildConfig;
import com.app.mschooling.com.R;
import com.app.mschooling.panding.fragment.PendingRequestFragment;
import com.app.mschooling.home.student.fragment.StudentHomeFragment;
import com.app.mschooling.homework.fragment.StudentHomeWorkFragment;
import com.app.mschooling.menu.StudentMenuFragment;
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
import com.mschooling.transaction.common.user.StudentSetup;
import com.mschooling.transaction.common.user.UserSetup;
import com.mschooling.transaction.response.birthday.GetBirthdayResponse;
import com.mschooling.transaction.response.version.GetVersionResponse;

import org.greenrobot.eventbus.Subscribe;

public class StudentMainActivity extends BaseActivity {

    public static StudentMainActivity context;
    boolean foregroundChecker;
    public BottomNavigationView navigation;


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        Fragment fragment = null;

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    fragment = new StudentHomeFragment();
                    break;
                case R.id.navigation_notice:
                    fragment = new StudentHomeWorkFragment();
                    break;
                case R.id.navigation_complaint:
                    fragment = new PendingRequestFragment();
                    break;

                case R.id.navigation_menu:
                    fragment = new StudentMenuFragment();

            }
            return StudentMainActivity.this.loadFragment(fragment);
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        updateToken();
        setContentView(R.layout.activity_main_student_navigation);
        context = this;

        if (ParameterConstant.isMSchooling(getApplicationContext())){
            findViewById(R.id.logo).setVisibility(View.VISIBLE);
        }else if (ParameterConstant.isLittleSteps(getApplicationContext())) {
            findViewById(R.id.logo).setVisibility(View.GONE);
        }

        if (Preferences.getInstance(getApplicationContext()).getYoutubeApiKey().isEmpty()){
            apiCallBackWithout(getApiCommonController().getGoogleApiKey());
        }
        try {
            navigation = (BottomNavigationView) findViewById(R.id.navigation);
            navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
            loadFragment(new StudentHomeFragment());

            apiCallBackWithout(getApiCommonController().version(Helper.getVersion(this)));
            if (Preferences.getInstance(getApplicationContext()).getFirstTimeLogin() == 0) {
                Preferences.getInstance(getApplicationContext()).setFirstTimeLogin(1);
                Helper.welcomeVoice(this);
            }
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
        getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, fragment).commit();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        androidx.fragment.app.FragmentManager fragmentManager = getSupportFragmentManager();
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
        try {
            context = this;
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
//        AdmobHelper.showInterstitialAds(this);

    }

    boolean isExit = false;

    @Override
    public void onBackPressed() {
        if (navigation.getMenu().findItem(R.id.navigation_home).isChecked()) {
            if (isExit) {
                super.onBackPressed();
            } else {
                Toast.makeText(this, getString(R.string.backpress_message), Toast.LENGTH_SHORT).show();
            }
            isExit = true;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    isExit = false;
                }
            }, 2000);
        } else {
            navigation.setSelectedItemId(R.id.navigation_home);
        }

    }

    public boolean isForeground() {
        return context.foregroundChecker;
    }

    @Subscribe
    public void timeOut(String msg) {

    }


    @Subscribe
    public void version(GetVersionResponse response) {
        if (response.getStatus().value() == Status.SUCCESS.value()) {
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
        Toast.makeText(this, getString(R.string.coming_soon), Toast.LENGTH_SHORT).show();
    }


    void navigationDrawer() {
        NavigationView navigationView = findViewById(R.id.nav_view);
        View view = navigationView.getHeaderView(0);
        UserSetup user = Preferences.getInstance(getApplicationContext()).getConfiguration().getUserSetup();

        TextView name = view.findViewById(R.id.name);
        TextView enrollmentId = view.findViewById(R.id.enrollmentId);
        TextView mobile = view.findViewById(R.id.mobile);
        TextView clazz = view.findViewById(R.id.clazz);
        TextView role = view.findViewById(R.id.role);
        ImageView image = view.findViewById(R.id.image);
        ProgressBar progressBar = view.findViewById(R.id.progressBar);

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ShowImageActivity.class);
                intent.putExtra("url", user.getUrl());
                intent.putExtra("name", user.getName());
                startActivity(intent);
            }
        });

        StudentSetup studentSetup = Preferences.getInstance(getApplicationContext()).getConfiguration().getStudentSetup();
        clazz.setText(studentSetup.getClassName() + " (" + studentSetup.getSectionName() + ")");
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

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        LinearLayout hamburger = findViewById(R.id.hamburger);
        hamburger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.openDrawer(GravityCompat.START);
            }
        });


        LinearLayout logout;
        LinearLayout share;
        LinearLayout myProfile;
        LinearLayout privacy;
        LinearLayout schoolInfo;
        LinearLayout aboutUs;
        LinearLayout reportSupport;
        LinearLayout generateQRCode;
        LinearLayout changePassCode;


        logout = findViewById(R.id.logout);
        schoolInfo = findViewById(R.id.schoolInfo);
        share = findViewById(R.id.share);
        myProfile = findViewById(R.id.myProfile);
        aboutUs = findViewById(R.id.aboutUs);
        reportSupport = findViewById(R.id.reportSupport);
        privacy = findViewById(R.id.privacy);
        generateQRCode = findViewById(R.id.generate_qr_code);

        changePassCode = findViewById(R.id.changePassCode);


        TextView version = findViewById(R.id.version);

        try {
            PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            version.setText("Version " + packageInfo.versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }


        aboutUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.closeDrawer(GravityCompat.START);
                startActivity(new Intent(getApplicationContext(), AboutUsActivity.class));
            }
        });


        privacy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.closeDrawer(GravityCompat.START);
                Intent intent = new Intent(getApplicationContext(), TncActivity.class);
                intent.putExtra("from", "");
                startActivity(intent);
            }
        });
        reportSupport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.closeDrawer(GravityCompat.START);
                Intent intent = new Intent(getApplicationContext(), HelpActivity.class);
                startActivity(intent);
            }
        });

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.closeDrawer(GravityCompat.START);
                Helper.share(StudentMainActivity.this);
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.closeDrawer(GravityCompat.START);
                logoutEvent();
            }
        });


        myProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.closeDrawer(GravityCompat.START);
//                startActivity(new Intent(getApplicationContext(), MyProfileActivity.class));
                startActivity(new Intent(getApplicationContext(), StudentProfileActivity.class));
            }
        });


        schoolInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.closeDrawer(GravityCompat.START);
                startActivity(new Intent(getApplicationContext(), AboutSchoolActivity.class));
            }
        });


        generateQRCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.closeDrawer(GravityCompat.START);
                startActivity(new Intent(getApplicationContext(), QRCodeActivity.class));
            }
        });


        changePassCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.closeDrawer(GravityCompat.START);
                startActivity(new Intent(getApplicationContext(), ChangePassCodeActivity.class));
            }
        });


    }

    @Subscribe
    void getBirthday(GetBirthdayResponse response) {
        if (Status.SUCCESS.value().equals(response.getMessage())) {
            if (response.getStudentBirthdayResponses() != null) {
                Intent intent = new Intent(getApplicationContext(), BirthdayActivity.class);
                intent.putExtra("name", Helper.getName(response.getStudentBirthdayResponses().get(0).getfName(), response.getStudentBirthdayResponses().get(0).getlName()));
                intent.putExtra("image", response.getStudentBirthdayResponses().get(0).getUrl());
                intent.putExtra("birthDate", response.getStudentBirthdayResponses().get(0).getDate());
                intent.putExtra("classSection", response.getStudentBirthdayResponses().get(0).getClassName() + " (" + response.getStudentBirthdayResponses().get(0).getSectionName() + ")");
                startActivity(new Intent(getApplicationContext(), BirthdayActivity.class));
            } else {
                Intent intent = new Intent(getApplicationContext(), BirthdayActivity.class);
                intent.putExtra("name", Helper.getName(response.getTeacherBirthdayResponses().get(0).getfName(), response.getStudentBirthdayResponses().get(0).getlName()));
                intent.putExtra("image", response.getTeacherBirthdayResponses().get(0).getUrl());
                intent.putExtra("image", Helper.getName(response.getTeacherBirthdayResponses().get(0).getfName(), response.getStudentBirthdayResponses().get(0).getlName()));
                intent.putExtra("birthDate", response.getTeacherBirthdayResponses().get(0).getDate());
                intent.putExtra("classSection", response.getTeacherBirthdayResponses().get(0).getClassName() + " (" + response.getStudentBirthdayResponses().get(0).getSectionName() + ")");
                startActivity(new Intent(getApplicationContext(), BirthdayActivity.class));
            }

        } else {
            dialogError(response.getMessage().getMessage());
        }
    }

}
