package com.app.mschooling.birthday.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;

import com.app.mschooling.base.activity.BaseActivity;
import com.app.mschooling.birthday.adapter.BirthdayListAdapter;
import com.app.mschooling.com.R;
import com.app.mschooling.com.databinding.ActivityBirthdayListBinding;
import com.app.mschooling.com.databinding.ActivityNormalListBinding;
import com.app.mschooling.com.databinding.NoRecord2Binding;
import com.app.mschooling.com.databinding.NoRecordBinding;
import com.app.mschooling.notice.activity.NoticeListActivity;
import com.app.mschooling.notice.adapter.NoticeListAdapter;
import com.mschooling.transaction.common.api.Common;
import com.mschooling.transaction.common.api.Status;
import com.mschooling.transaction.response.birthday.BirthdayResponse;
import com.mschooling.transaction.response.birthday.GetBirthdayResponse;
import com.mschooling.transaction.response.notice.GetNoticeResponse;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

public class BirthdayListActivity extends BaseActivity {

    ActivityBirthdayListBinding binding;
    NoRecord2Binding noRecordBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_birthday_list);
        toolbarClick(getString(R.string.birthday));
        noRecordBinding = binding.noRecord;
        apiCallBack(getApiCommonController().getBirthdayResponse());
    }


    @Subscribe
    public void get(GetBirthdayResponse response) {
        if (response.getStatus().value() == Status.SUCCESS.value()) {
            List<BirthdayResponse> birthdayResponses = new ArrayList<>();
            birthdayResponses.addAll(response.getStudentBirthdayResponses());
            birthdayResponses.addAll(response.getTeacherBirthdayResponses());
            binding.recyclerView.setHasFixedSize(true);
            binding.recyclerView.setFocusable(false);
            binding.recyclerView.setNestedScrollingEnabled(false);
            binding.recyclerView.setLayoutManager(new GridLayoutManager(this,2));
            binding.recyclerView.setAdapter(new BirthdayListAdapter(this, birthdayResponses));
            if (birthdayResponses.size() == 0) {
                noRecordBinding.noRecord.setVisibility(View.VISIBLE);
            } else {
                noRecordBinding.noRecord.setVisibility(View.GONE);
            }
        } else {
            dialogFailed(response.getMessage().getMessage());
        }
    }

}