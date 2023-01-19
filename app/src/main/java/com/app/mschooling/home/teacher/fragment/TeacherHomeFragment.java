package com.app.mschooling.home.teacher.fragment;


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
import com.app.mschooling.home.teacher.adapter.TeacherHomeAdapter;
import com.app.mschooling.home.teacher.adapter.TeacherHomeStudentSectionAdapter;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TeacherHomeFragment extends BaseFragment implements BaseSliderView.OnSliderClickListener, ViewPagerEx.OnPageChangeListener {

    private SliderLayout imageSlider;
    AnimatedRecyclerView mRecyclerView;
    AnimatedRecyclerView studentRecyclerView;
    HashMap<String, String> mapLinks;
    TextView studentHeading;
    TextView otherHeading;
    LinearLayout notification;
    TextView academicSession;
//    LinearLayout profile;




    @Nullable
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_teacher, container, false);

        try {
            imageSlider = (SliderLayout) view.findViewById(R.id.slider);
            notification = (LinearLayout) view.findViewById(R.id.notification);
            mRecyclerView = view.findViewById(R.id.recycler_view);
            studentRecyclerView = view.findViewById(R.id.studentRecyclerView);
            studentHeading = view.findViewById(R.id.studentHeading);
            otherHeading = view.findViewById(R.id.otherHeading);
            academicSession = view.findViewById(R.id.academicSession);
            mapLinks = new HashMap<>();

            setHeaderImage(view);


            notification.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(getActivity(), NotificationListActivity.class));
                }
            });

//        setImageSlider(new LinkedHashMap<>());


            List<MenuResponse> menuResponseList=Preferences.getInstance(getContext()).getConfiguration().getDashboard().getGetMenuResponse().getMenuResponses();
            for (int i=0;i<menuResponseList.size();i++){
                if (menuResponseList.get(i).getName().equals("Others")){
                    if ("en".equals(Preferences.getInstance(getContext()).getLanguage())){
                        otherHeading.setText(menuResponseList.get(i).getDisplayNameEn());
                    }else {
                        otherHeading.setText(menuResponseList.get(i).getDisplayNameHi());
                    }
                    setOtherAdapter(menuResponseList.get(i).getMenus());
                }else if (menuResponseList.get(i).getName().equals("Students")){
                    if ("en".equals(Preferences.getInstance(getContext()).getLanguage())){
                        studentHeading.setText(menuResponseList.get(i).getDisplayNameEn());
                    }else {
                        studentHeading.setText(menuResponseList.get(i).getDisplayNameHi());
                    }
                    setStudentAdapter(menuResponseList.get(i).getMenus());
                }
            }

            setImageSlider(imageSlider,Preferences.getInstance(getContext()).getConfiguration().getDashboard().getUrlMap());

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

       /*  HashMap<String, String> url_maps = new LinkedHashMap<String, String>();
        Iterator<String> it = set.iterator();
        String key="mSchooling";
        while(it.hasNext()){
            url_maps.put(key, it.next());
            key=key+" ";
        } */

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
    public void onDestroy() {
        super.onDestroy();
        imageSlider.stopAutoCycle();
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

    void setOtherAdapter(List<Menu> menuList) {
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setFocusable(false);
        mRecyclerView.setNestedScrollingEnabled(false);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        mRecyclerView.setAdapter(new TeacherHomeAdapter(getActivity(), menuList));
    }

    void setStudentAdapter(List<Menu> menuList) {
        studentRecyclerView.setHasFixedSize(true);
        studentRecyclerView.setFocusable(false);
        studentRecyclerView.setNestedScrollingEnabled(false);
        studentRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        studentRecyclerView.setAdapter(new TeacherHomeStudentSectionAdapter(getActivity(), menuList));
    }

}

