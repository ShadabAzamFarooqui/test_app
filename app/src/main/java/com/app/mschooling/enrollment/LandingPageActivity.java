package com.app.mschooling.enrollment;


import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Interpolator;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RadioGroup;
import android.widget.Scroller;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.app.mschooling.enrollment.login.activity.LoginActivity;
import com.app.mschooling.enrollment.signup.activity.SignUpBaseActivity;
import com.app.mschooling.enrollment.guide.activity.SignupGuideActivity;
import com.app.mschooling.help_support_report_feedback.activity.ExternalSupportActivity;
import com.app.mschooling.other.activity.LanguageSelectionActivity;
import com.app.mschooling.other.activity.TncActivity;
import com.app.mschooling.barcodescan.MaterialBarcodeScanner;
import com.app.mschooling.barcodescan.MaterialBarcodeScannerBuilder;
import com.app.mschooling.base.activity.BaseActivity;
import com.app.mschooling.com.R;
import com.app.mschooling.utils.AppUser;
import com.app.mschooling.utils.IBarcode;
import com.app.mschooling.utils.ParameterConstant;
import com.app.mschooling.utils.progress_dialog.MyProgressDialog;
import com.google.android.gms.vision.barcode.Barcode;
import com.mschooling.transaction.common.api.Status;
import com.mschooling.transaction.response.qrcode.GetQRCodeResponse;

import org.greenrobot.eventbus.Subscribe;

import java.lang.reflect.Field;
import java.util.Timer;
import java.util.TimerTask;

public class LandingPageActivity extends BaseActivity {

    LinearLayout login;
    LinearLayout signUp;
    LinearLayout register;
    TextView itsFree;
    ViewPager mPager;
    RadioGroup mRadioGroup;
    TextView terms;
    MyPagerAdapter adapter;
    int currentPage = 0;
    int NUM_PAGES = 0;
    Timer timer;
    long DELAY_MS = 0;
    long PERIOD_MS = 0;
    Handler handler;
    Runnable Update;
    String QR_CODE_VALUE = "";

    public String barcodeScan(int code_type) {
        String bool = "";
        if (code_type == IBarcode.QR_CODE) {
            bool = "QR-CODE";
        }
        return bool;
    }



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing_page);

        login = findViewById(R.id.login);
        signUp = findViewById(R.id.signUp);
        register = findViewById(R.id.register);
        mPager = findViewById(R.id.viewpager);
        mRadioGroup = findViewById(R.id.radiogroup);
        terms = findViewById(R.id.terms);
        itsFree = findViewById(R.id.itsFree);

        Animation anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setDuration(200); //You can manage the blinking time with this parameter
        anim.setStartOffset(20);
        anim.setRepeatMode(Animation.REVERSE);
        anim.setRepeatCount(Animation.INFINITE);
        itsFree.startAnimation(anim);

        currentPage = 0;
        NUM_PAGES = 9;
        DELAY_MS = 500;
        PERIOD_MS = 3000;
        terms.setClickable(true);
        terms.setMovementMethod(LinkMovementMethod.getInstance());
        String text = getString(R.string.login_privacy_poicy);
        terms.setText(Html.fromHtml(text));

        SpannableString ss = new SpannableString(text);
        ClickableSpan span1 = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
                Intent intent = new Intent(LandingPageActivity.this, TncActivity.class);
                intent.putExtra("from", "tnc");
                startActivity(intent);

            }
        };

        ClickableSpan span2 = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
                Intent intent = new Intent(LandingPageActivity.this, TncActivity.class);
                intent.putExtra("from", "");
                startActivity(intent);
            }
        };

        ss.setSpan(span1, text.indexOf("Te"), text.lastIndexOf("e") + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ss.setSpan(span2, text.indexOf("Pr"), text.lastIndexOf("y") + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        terms.setText(ss);
        terms.setMovementMethod(LinkMovementMethod.getInstance());


        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            }
        });
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startScan();
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), ValidateRegisterSchoolActivity.class));
            }
        });


        adapter = new MyPagerAdapter();
        mPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        mRadioGroup.check(R.id.radioButton);
                        break;
                    case 1:
                        mRadioGroup.check(R.id.radioButton2);
                        break;
                    case 2:
                        mRadioGroup.check(R.id.radioButton3);
                        break;
                    case 3:
                        mRadioGroup.check(R.id.radioButton4);
                        break;
                    case 4:
                        mRadioGroup.check(R.id.radioButton5);
                        break;
                    case 5:
                        mRadioGroup.check(R.id.radioButton6);
                        break;
                    case 6:
                        mRadioGroup.check(R.id.radioButton7);
                        break;
                    case 7:
                        mRadioGroup.check(R.id.radioButton8);

                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        mPager.setAdapter(adapter);
        changePagerScroller();
        // mPager.setCurrentItem(0);
        timer = new Timer();
        handler = new Handler();
        Update = new Runnable() {
            public void run() {
                if (currentPage == NUM_PAGES - 1) {
                    currentPage = 0;
                    changePagerScroller();
                }
                mPager.setCurrentItem(currentPage++, true);

            }
        };

        // This will create a new Thread
        timer.schedule(new TimerTask() { // task to be scheduled
            @Override
            public void run() {
                handler.post(Update);
            }
        }, DELAY_MS, PERIOD_MS);

        mPager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                timer.cancel();
                return false;
            }
        });
    }


    public void changePagerScroller() {
        try {
            Field mScroller = null;
            mScroller = ViewPager.class.getDeclaredField("mScroller");
            mScroller.setAccessible(true);
            ViewPagerScroller scroller = new ViewPagerScroller(mPager.getContext());
            mScroller.set(mPager, scroller);
        } catch (Exception e) {
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        handler.removeCallbacks(Update);
        timer.cancel();
    }

    private void startScan() {
        MaterialBarcodeScanner materialBarcodeScanner = new MaterialBarcodeScannerBuilder()
                .withActivity(this)
                .withEnableAutoFocus(true)
                .withBleepEnabled(false)
                .withBackfacingCamera()
                .withCenterTracker()
                .withText("Scanning...")
                .withResultListener(new MaterialBarcodeScanner.OnResultListener() {
                    @Override
                    public void onResult(Barcode barcode) {
                        MyProgressDialog.getInstance(LandingPageActivity.this).show();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                MyProgressDialog.setDismiss();
                                if (barcodeScan(barcode.format).equals("QR-CODE")) {

                                    QR_CODE_VALUE = barcode.rawValue;
                                    int code_type = barcode.format;
                                    String qr_value[] = QR_CODE_VALUE.split("#");
                                    if (qr_value[0].equals(ParameterConstant.getMSchoolingText())) {
                                        apiCallBack(getApiCommonController().getQrCodeDetail(qr_value[1]));
                                    } else {
                                        apiCallBack(getApiCommonController().getQrCodeDetail(QR_CODE_VALUE));
                                    }
                                } else {
                                    dialogError(getString(R.string.please_scan_only_qr_code));
                                }
                            }
                        }, 3 * 1000);
                    }
                })
                .build();
        materialBarcodeScanner.startScan();

    }

    class MyPagerAdapter extends PagerAdapter {

        public int getCount() {
            return 8;
        }

        public Object instantiateItem(View collection, int position) {

            LayoutInflater inflater = (LayoutInflater) collection.getContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            int resId = 0;
            switch (position) {
                case 0:
                    resId = R.layout.layout_fragment_page1;
//                    currentPage=0;
                    break;
                case 1:
                    resId = R.layout.layout_fragment_page2;
//                    currentPage=1;
                    break;
                case 2:
                    resId = R.layout.layout_fragment_page3;
//                    currentPage=2;
                    break;
                case 3:
                    resId = R.layout.layout_fragment_page4;
                    break;
                case 4:
                    resId = R.layout.layout_fragment_page5;
                    break;
                case 5:
                    resId = R.layout.layout_fragment_page6;
                    break;
                case 6:
                    resId = R.layout.layout_fragment_page7;
                    break;
                case 7:
                    resId = R.layout.layout_fragment_page8;
//                    currentPage=3;
                    break;
            }

            View view = inflater.inflate(resId, null);

            ((ViewPager) collection).addView(view, 0);

            return view;
        }

        @Override
        public void destroyItem(View arg0, int arg1, Object arg2) {
            ((ViewPager) arg0).removeView((View) arg2);

        }


        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == ((View) arg1);

        }

        @Override
        public Parcelable saveState() {
            return null;
        }

    }

    class ViewPagerScroller extends Scroller {
        private int mScrollDuration = 1500;

        public ViewPagerScroller(Context context) {
            super(context);
        }

        public ViewPagerScroller(Context context, Interpolator interpolator) {
            super(context, interpolator);
        }

        @Override
        public void startScroll(int startX, int startY, int dx, int dy, int duration) {
            super.startScroll(startX, startY, dx, dy, mScrollDuration);
        }

        @Override
        public void startScroll(int startX, int startY, int dx, int dy) {
            super.startScroll(startX, startY, dx, dy, mScrollDuration);
        }
    }


    @Subscribe
    public void getQrCodeDetail(GetQRCodeResponse response) {
        if (response.getStatus().value().equals(Status.SUCCESS.value())) {
            AppUser.getInstance().setQrCodeDetailResponse(response);
            startActivity(new Intent(getApplicationContext(), SignUpBaseActivity.class));
        } else {
            getDialogHelp(response.getMessage().getMessage());
        }
    }

    public void getDialogHelp(String msg) {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.help_dialog);
        dialog.setCancelable(false);
        TextView message = (TextView) dialog.findViewById(R.id.message);
        TextView help = (TextView) dialog.findViewById(R.id.help);
        TextView ok = (TextView) dialog.findViewById(R.id.ok);
        message.setText(msg);
        help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(getApplicationContext(), SignupGuideActivity.class));
                dialog.dismiss();
            }
        });
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    public void support(View view) {
        PopupMenu popup = new PopupMenu(LandingPageActivity.this, view);
        //inflating menu from xml resource
        popup.inflate(R.menu.support);
        //adding click listener
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.support:
                        startActivity( new Intent(getApplicationContext(), ExternalSupportActivity.class));
                        return true;
                    case R.id.change:
                        Intent intent = new Intent(getApplicationContext(), LanguageSelectionActivity.class);
                        intent.putExtra("intent","");
                        startActivity(intent);
                        return true;
                    default:
                        return false;
                }
            }
        });
        //displaying the popup
        popup.show();
    }
}

