package com.app.mschooling.home.student.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;

import com.app.mschooling.notification.activity.NotificationListActivity;
import com.app.mschooling.home.student.adapter.StudentHomeAdapter;
import com.app.mschooling.base.fragment.BaseFragment;
import com.app.mschooling.com.BuildConfig;
import com.app.mschooling.com.R;
import com.app.mschooling.utils.Preferences;
import com.mschooling.slider.library.Animations.DescriptionAnimation;
import com.mschooling.slider.library.SliderLayout;
import com.mschooling.slider.library.SliderTypes.BaseSliderView;
import com.mschooling.slider.library.SliderTypes.TextSliderView;
import com.mschooling.slider.library.Tricks.ViewPagerEx;
import com.mlsdev.animatedrv.AnimatedRecyclerView;
import com.mschooling.transaction.common.Menu;
import com.mschooling.transaction.response.menu.MenuResponse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StudentHomeFragment extends BaseFragment implements BaseSliderView.OnSliderClickListener, ViewPagerEx.OnPageChangeListener {

    private SliderLayout imageSlider;
    AnimatedRecyclerView mRecyclerView;
    HashMap<String,String> mapLinks;
    LinearLayout notification;
    TextView academicSession;




    @Nullable
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_student, container, false);

        try {
            imageSlider = (SliderLayout) view.findViewById(R.id.slider);
            mRecyclerView = view.findViewById(R.id.recycler_view);
            notification = (LinearLayout) view.findViewById(R.id.notification);
            mapLinks=new HashMap<>();
            academicSession = view.findViewById(R.id.academicSession);
            mapLinks = new HashMap<>();
            ArrayList<String> tittle = new ArrayList();

            List<MenuResponse> menuResponseList=Preferences.getInstance(getContext()).getConfiguration().getDashboard().getGetMenuResponse().getMenuResponses();
            setAdapter(menuResponseList.get(0).getMenus());
            setImageSlider(imageSlider,Preferences.getInstance(getContext()).getConfiguration().getDashboard().getUrlMap());

            setHeaderImage(view);

            notification.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(getActivity(), NotificationListActivity.class));
                }
            });


        }catch (Exception e){
            dialogLogout();
        }


        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        academicSession.setText(Preferences.getInstance(getContext()).getAcademicSession());
    }

    void setImageSlider(SliderLayout imageSlider, Map<String, String> url_maps ) {

        for (String url : url_maps.keySet()) {
            TextSliderView textSliderView = new TextSliderView(getActivity());

            textSliderView
                    .description(url_maps.get(url))
                    .image(url)
                    .setScaleType(BaseSliderView.ScaleType.Fit)
                    .setOnSliderClickListener(this);

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
    public void onDestroy() {
        super.onDestroy();
        imageSlider.stopAutoCycle();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onSliderClick(BaseSliderView slider) {
//        Toast.makeText(getActivity(), slider.getBundle().get("extra") + "", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
        Log.e("Slider Demo", "Page Changed: " + position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    void setAdapter(List<Menu> menuList) {
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setFocusable(false);
        mRecyclerView.setNestedScrollingEnabled(false);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        mRecyclerView.setAdapter(new StudentHomeAdapter(getActivity(), menuList));
        mRecyclerView.scheduleLayoutAnimation();
    }

}

