package com.app.mschooling.home.admin.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.viewpager.widget.ViewPager;

import com.app.mschooling.base.fragment.BaseFragment;
import com.app.mschooling.com.BuildConfig;
import com.app.mschooling.com.R;
import com.app.mschooling.home.admin.adapter.AdminOtherHomeAdapter;
import com.app.mschooling.home.admin.adapter.AdminSetUpHomeAdapter;
import com.app.mschooling.home.admin.adapter.AdminSetUpStudentAdapter;
import com.app.mschooling.home.admin.adapter.AdminSetUpTeacherAdapter;
import com.app.mschooling.notification.activity.NotificationListActivity;
import com.app.mschooling.utils.Helper;
import com.app.mschooling.utils.Preferences;
import com.mschooling.slider.library.Animations.DescriptionAnimation;
import com.mschooling.slider.library.SliderLayout;
import com.mschooling.slider.library.SliderTypes.BaseSliderView;
import com.mschooling.slider.library.SliderTypes.TextSliderView;
import com.mschooling.slider.library.Tricks.ViewPagerEx;
import com.mlsdev.animatedrv.AnimatedRecyclerView;
import com.mschooling.transaction.common.Menu;
import com.mschooling.transaction.response.menu.MenuResponse;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.relex.circleindicator.CircleIndicator;

public class AdminHomeFragment extends BaseFragment implements BaseSliderView.OnSliderClickListener, ViewPagerEx.OnPageChangeListener {

    private SliderLayout imageSlider;
    AnimatedRecyclerView mRecyclerView;
    AnimatedRecyclerView adminRecyclerView;
    AnimatedRecyclerView studentRecyclerView;
    AnimatedRecyclerView teacherRecyclerView;
    HashMap<String, String> mapLinks;
    ViewPager mPager_top;
    CircleIndicator indicator;
    LinearLayout notification;

    TextView adminHeading;
    TextView studentHeading;
    TextView teacherHeading;
    TextView otherHeading;
    TextView academicSession;

    LinearLayout m1, m2, indicatorView;





    @Nullable
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        try {
            notification =  view.findViewById(R.id.notification);
            imageSlider =  view.findViewById(R.id.slider);
            mPager_top =  view.findViewById(R.id.pager_top);
            indicator =  view.findViewById(R.id.indicator_top);
            mRecyclerView = view.findViewById(R.id.recycler_view);
            adminRecyclerView = view.findViewById(R.id.adminRecyclerView);
            studentRecyclerView = view.findViewById(R.id.studentRecyclerView);
            teacherRecyclerView = view.findViewById(R.id.teacherRecyclerView);
            adminHeading = view.findViewById(R.id.adminHeading);
            studentHeading = view.findViewById(R.id.studentHeading);
            teacherHeading = view.findViewById(R.id.teacherHeading);
            otherHeading = view.findViewById(R.id.otherHeading);
            academicSession = view.findViewById(R.id.academicSession);
            m1 = view.findViewById(R.id.m1);
            m2 = view.findViewById(R.id.m2);
            indicatorView = view.findViewById(R.id.indicatorView);
            mapLinks = new HashMap<>();

            setHeaderImage(view);


            notification.setOnClickListener(v -> startActivity(new Intent(getActivity(), NotificationListActivity.class)));

            List<MenuResponse> menuResponseList = Preferences.getInstance(getContext()).getConfiguration().getDashboard().getGetMenuResponse().getMenuResponses();
            for (int i = 0; i < menuResponseList.size(); i++) {
                switch (menuResponseList.get(i).getName()) {
                    case "Administration Setup":
                        if ("en".equals(Preferences.getInstance(getContext()).getLanguage())) {
                            adminHeading.setText(menuResponseList.get(i).getDisplayNameEn());
                        } else {
                            adminHeading.setText(menuResponseList.get(i).getDisplayNameHi());
                        }
                        setAdminRecyclerView(menuResponseList.get(i).getMenus());
                        break;
                    case "Teachers":
                        if ("en".equals(Preferences.getInstance(getContext()).getLanguage())) {
                            teacherHeading.setText(menuResponseList.get(i).getDisplayNameEn());
                        } else {
                            teacherHeading.setText(menuResponseList.get(i).getDisplayNameHi());
                        }
                        setTeacherRecyclerView(menuResponseList.get(i).getMenus());
                        break;
                    case "Students":
                        if ("en".equals(Preferences.getInstance(getContext()).getLanguage())) {
                            studentHeading.setText(menuResponseList.get(i).getDisplayNameEn());
                        } else {
                            studentHeading.setText(menuResponseList.get(i).getDisplayNameHi());
                        }
                        setStudentRecyclerView(menuResponseList.get(i).getMenus());
                        break;
                    case "Others":
                        if ("en".equals(Preferences.getInstance(getContext()).getLanguage())) {
                            otherHeading.setText(menuResponseList.get(i).getDisplayNameEn());
                        } else {
                            otherHeading.setText(menuResponseList.get(i).getDisplayNameHi());
                        }
                        setOtherRecyclerView(menuResponseList.get(i).getMenus());
                        break;
                }
            }

            Helper.setTextGradient(getContext(), adminHeading);
            Helper.setTextGradient(getContext(), studentHeading);
            Helper.setTextGradient(getContext(), teacherHeading);
            Helper.setTextGradient(getContext(), otherHeading);
            setImageSlider(imageSlider, Preferences.getInstance(getContext()).getConfiguration().getDashboard().getUrlMap());



        } catch (Exception e) {
            dialogLogout();
        }

        return view;
    }


    void setAdminRecyclerView(List<Menu> menuList) {
        adminRecyclerView.setHasFixedSize(true);
        adminRecyclerView.setFocusable(false);
        adminRecyclerView.setNestedScrollingEnabled(false);
        adminRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        adminRecyclerView.setAdapter(new AdminSetUpHomeAdapter(getActivity(), menuList));
    }

    void setTeacherRecyclerView(List<Menu> menuList) {
        teacherRecyclerView.setHasFixedSize(true);
        teacherRecyclerView.setFocusable(false);
        teacherRecyclerView.setNestedScrollingEnabled(false);
        teacherRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        if (BuildConfig.DEBUG) {
            Menu Dashboard = new Menu();
            Dashboard.setName("Salary");
            Dashboard.setDisplayNameEn("Salary");
            Dashboard.setDisplayNameHi("Salary");
            menuList.add(Dashboard);

            Menu ICard = new Menu();
            ICard.setName("ICard");
            ICard.setDisplayNameEn("ICard");
            ICard.setDisplayNameHi("ICard");
            menuList.add(ICard);
        }
        teacherRecyclerView.setAdapter(new AdminSetUpTeacherAdapter(getActivity(), menuList));
    }

    void setStudentRecyclerView(List<Menu> menuList) {
        studentRecyclerView.setHasFixedSize(true);
        studentRecyclerView.setFocusable(false);
        studentRecyclerView.setNestedScrollingEnabled(false);
        studentRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        studentRecyclerView.setAdapter(new AdminSetUpStudentAdapter(getActivity(), menuList));
    }

    void setOtherRecyclerView(List<Menu> menuList) {
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setFocusable(false);
        mRecyclerView.setNestedScrollingEnabled(false);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        if (BuildConfig.DEBUG) {
            Menu Dashboard = new Menu();
            Dashboard.setName("Dashboard");
            Dashboard.setDisplayNameEn("Dashboard");
            Dashboard.setDisplayNameHi("Dashboard");
            menuList.add(Dashboard);

            Menu Report = new Menu();
            Report.setName("Report");
            Report.setDisplayNameEn("Report");
            Report.setDisplayNameHi("Report");
            menuList.add(Report);
        }
        mRecyclerView.setAdapter(new AdminOtherHomeAdapter(getActivity(), menuList));
    }


    void setImageSlider(SliderLayout imageSlider, Map<String, String> url_maps) {

        for (String url : url_maps.keySet()) {
            TextSliderView textSliderView = new TextSliderView(getActivity());
            // initialize a SliderLayout
            textSliderView
                    .description(url_maps.get(url))
                    .image(url)
                    .setScaleType(BaseSliderView.ScaleType.Fit)
                    .setOnSliderClickListener(this);

            //add your extra information
            textSliderView.bundle(new Bundle());
            textSliderView.getBundle()
                    .putString("extra", url);

            imageSlider.addSlider(textSliderView);
        }
        imageSlider.setPresetTransformer(SliderLayout.Transformer.Accordion);
        imageSlider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
        imageSlider.setCustomAnimation(new DescriptionAnimation());
        imageSlider.setDuration(5000);
        imageSlider.addOnPageChangeListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        academicSession.setText(Preferences.getInstance(getContext()).getAcademicSession());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        imageSlider.stopAutoCycle();
    }


    @Override
    public void onSliderClick(BaseSliderView slider) {
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }


}

