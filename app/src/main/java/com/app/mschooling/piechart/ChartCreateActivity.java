package com.app.mschooling.piechart;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;

import androidx.databinding.DataBindingUtil;

import com.app.mschooling.base.activity.BaseActivity;
import com.app.mschooling.com.R;
import com.app.mschooling.com.databinding.ActivityChartCreateBinding;

public class ChartCreateActivity extends BaseActivity {

    ActivityChartCreateBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= DataBindingUtil. setContentView(this,R.layout.activity_chart_create);
        toolbarClick(getString(R.string.dashboard));

        binding.submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                if (spinnerRole.getSelectedItemPosition()==0){
//                    dialogError(getString(R.string.select_role));
//                        return;
//                }
//                if (spinnerEnrolled.getSelectedItemPosition()==0){
//                    dialogError(getString(R.string.select_enrolled_w_star));
//                        return;
//                }

                Intent intent = new Intent(ChartCreateActivity.this, ChartActivity.class);
                intent.putExtra("role",binding.spinnerRole.getSelectedItem().toString());
                intent.putExtra("enrolled",binding.spinnerEnrolled.getSelectedItem().toString());
                startActivity(intent);
            }
        });

    }
}
