package com.app.mschooling.piechart;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import com.app.mschooling.base.activity.BaseActivity;
import com.app.mschooling.com.R;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.mschooling.transaction.common.api.Status;
import com.mschooling.transaction.response.dashboard.GetStudentCountResponse;
import com.mschooling.transaction.response.dashboard.StudentCountResponse;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;


public class ChartActivity extends BaseActivity {
    String role;
    PieChart pieChart;

    @Override
    protected void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setContentView(R.layout.chart_activity);
        Intent intent = getIntent();
        role = intent.getStringExtra("role");
        String enrolled = intent.getStringExtra("enrolled");
        toolbarClick(enrolled);
        pieChart = findViewById(R.id.pieChart);
        apiCallBack(getApiCommonController().getDashboardCount());
    }

    public void setPieChart(List<StudentCountResponse> data) {
        pieChart.setUsePercentValues(true);
        pieChart.getDescription().setEnabled(true);
        pieChart.setExtraOffsets(5, 10, 5, 5);
        pieChart.setDragDecelerationFrictionCoef(0.9f);
        pieChart.setTransparentCircleRadius(61f);
        pieChart.setHoleColor(Color.WHITE);
        pieChart.setCenterText("mSchooling");
        pieChart.animateY(1000, Easing.EasingOption.EaseInOutCubic);
        ArrayList<PieEntry> yValues = new ArrayList<>();
        Float total = 0f;
        for (int i = 0; i < data.size(); i++) {
            yValues.add(new PieEntry(Float.valueOf(data.get(i).getCount()), data.get(i).getClassName()));
            total += Float.valueOf(data.get(i).getCount());
        }
//        yValues.add(new PieEntry(20f,"B"));
//        yValues.add(new PieEntry(30f,"C"));
//        yValues.add(new PieEntry(10f,"D"));


        PieDataSet dataSet = new PieDataSet(yValues, R.string.total  + role + getString(R.string.count) + total);
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);
        dataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        PieData pieData = new PieData((dataSet));
        pieData.setValueTextSize(10f);
        pieData.setValueTextColor(Color.YELLOW);
        pieChart.setData(pieData);
        //PieChart Ends Here
    }

    @Subscribe
    public void notifyLiveClass(GetStudentCountResponse response) {
        if (response.getStatus().value() == Status.SUCCESS.value()) {
            setPieChart(response.getStudentCountResponses());
        } else {
            dialogFailed(response.getMessage().getMessage());
        }
    }
}
