package com.app.mschooling.homework.activity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.DatePicker;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.mschooling.com.databinding.ActivityHomeWorkListBinding;
import com.app.mschooling.event_handler.CommonEvent;
import com.app.mschooling.homework.adapter.HomeWorkListAdapter;
import com.app.mschooling.base.activity.BaseActivity;
import com.app.mschooling.com.R;
import com.app.mschooling.event_handler.EventDelete;
import com.app.mschooling.utils.Helper;
import com.app.mschooling.utils.ParameterConstant;
import com.app.mschooling.utils.PojoUtils;
import com.app.mschooling.utils.Preferences;
import com.mschooling.transaction.common.api.Common;
import com.mschooling.transaction.common.api.Status;
import com.mschooling.transaction.response.homework.DeleteWorksheetResponse;
import com.mschooling.transaction.response.homework.GetWorksheetResponse;
import com.mschooling.transaction.response.homework.WorksheetResponse;

import org.greenrobot.eventbus.Subscribe;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import butterknife.ButterKnife;

public class HomeWorkListActivity extends BaseActivity {

    HomeWorkListAdapter adapter;
    List<WorksheetResponse> responseList;

    PojoUtils pojoUtils = PojoUtils.getObject();
    int pastVisibleItems, visibleItemCount, totalItemCount;
    LinearLayoutManager layoutManager;

    ActivityHomeWorkListBinding binding;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_home_work_list);
        ButterKnife.bind(this);
        toolbarClick(getString(R.string.homework));

        binding.date.setText(Helper.getCurrentDate());
        datePicker(binding.date);
        datePicker(binding.dateLayout);
        layoutManager = new LinearLayoutManager(this);

        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (adapter == null) {
                    return;
                }
                adapter.filteredList = responseList;
                adapter.responseList = responseList;
                adapter.getFilter().filter(s.toString());
            }
        });

        if (Preferences.getInstance(this).getROLE().equals(Common.Role.STUDENT.value())) {
            binding.add.add.setVisibility(View.GONE);
        } else {
            binding.add.add.setVisibility(View.VISIBLE);
        }


        pojoUtils.setClassId(getIntent().getStringExtra("classId"));
        pojoUtils.setClassName(getIntent().getStringExtra("className"));
        pojoUtils.setSectionId(getIntent().getStringExtra("sectionId"));
        pojoUtils.setSectionName(getIntent().getStringExtra("sectionName"));
        pojoUtils.setSubjectId(getIntent().getStringExtra("subjectId"));
        pojoUtils.setSubjectName(getIntent().getStringExtra("subjectName"));
        pojoUtils.setDate(binding.date.getText().toString());


        binding.add.add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AddHomeWorkActivity.class);
                intent.putExtra("className", pojoUtils.getClassName());
                intent.putExtra("classId", pojoUtils.getClassId());
                intent.putExtra("sectionName", pojoUtils.getSectionName());
                intent.putExtra("sectionId", pojoUtils.getSectionId());
                intent.putExtra("subjectName", pojoUtils.getSubjectName());
                intent.putExtra("subjectId", pojoUtils.getSubjectId());
                startActivityForResult(intent, REQUEST_CREATE);
            }
        });


        apiCall();


        binding.recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) {
                    visibleItemCount = layoutManager.getChildCount();
                    totalItemCount = layoutManager.getItemCount();
                    pastVisibleItems = layoutManager.findFirstCompletelyVisibleItemPosition();

                    if (pojoUtils.isLoading()) {
                        if ((visibleItemCount + pastVisibleItems) >= totalItemCount) {
                            pojoUtils.setIndex(pojoUtils.getIndex() + 1);
                            pojoUtils.setLoading(false);
                            apiCall();
                        }

                    }
                }
            }
        });
    }


    void apiCall() {
        apiCallBack(getApiCommonController().getHomeWork(ParameterConstant.getRole(getApplicationContext()),
                pojoUtils.getIndex(),
                binding.date.getText().toString(),
                pojoUtils.getClassId(),
                pojoUtils.getSectionId(),
                pojoUtils.getSubjectId()
        ));
    }


    @SuppressLint("NotifyDataSetChanged")
    @Subscribe
    public void getResponse(GetWorksheetResponse response) {
        if (response.getStatus().value().equals(Status.SUCCESS.value())) {
            if (pojoUtils.getIndex() == 0) {
                responseList = response.getWorksheetResponses();
                binding.recyclerView.setHasFixedSize(true);
                binding.recyclerView.setFocusable(false);
                binding.recyclerView.setNestedScrollingEnabled(false);
                binding.recyclerView.setLayoutManager(layoutManager);
                adapter = new HomeWorkListAdapter(this, response.getWorksheetResponses());
                binding.recyclerView.setAdapter(adapter);

                if (response.getWorksheetResponses().size() == 0) {
                    binding.noRecord.noRecord.setVisibility(View.VISIBLE);
                } else {
                    binding.noRecord.noRecord.setVisibility(View.GONE);
                }
            } else {
                responseList.addAll(response.getWorksheetResponses());
                adapter.notifyDataSetChanged();
                if (response.getWorksheetResponses().size() > 0) {
                    pojoUtils.setLoading(true);
                } else {
                    pojoUtils.setLoading(false);
                }
            }

        } else {
            dialogError(response.getMessage().getMessage());
        }
    }

    @SuppressLint("RestrictedApi")
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            super.onActivityResult(requestCode, resultCode, data);
            if (resultCode == RESULT_OK) {
                if (requestCode == REQUEST_CREATE) {
                    apiCall();
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    void datePicker(View view) {
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                DatePickerDialog datePickerDialog = new DatePickerDialog(HomeWorkListActivity.this, new DatePickerDialog.OnDateSetListener() {
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(year, monthOfYear, dayOfMonth);
                        String d = new SimpleDateFormat(ParameterConstant.getDateFormat() , Locale.US).format(calendar.getTime());
                        binding.date.setText(d);
                        apiCall();
                    }

                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
//                datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
                datePickerDialog.show();
            }
        });

    }


    @Subscribe
    public void deleteEvent(EventDelete event) {
        this.event = event;
        dialogDelete(getApiCommonController().deleteHomeWork(ParameterConstant.getRole(this),event.getId()));
    }

    @SuppressLint("NotifyDataSetChanged")
    @Subscribe
    public void deleteHomeWorkResponse(DeleteWorksheetResponse response) {
        if (response.getStatus().value().equals(Status.SUCCESS.value())) {
            responseList.remove(event.getPosition());
            adapter.notifyDataSetChanged();
            if (responseList.size() == 0) {
                binding.noRecord.noRecord.setVisibility(View.VISIBLE);
            } else {
                binding.noRecord.noRecord.setVisibility(View.GONE);
            }
            dialogSuccess(response.getMessage().getMessage());
        } else {
            dialogFailed(response.getMessage().getMessage());
        }
    }


    @Subscribe
    public void clickHandler(CommonEvent event) {
        Intent intent = new Intent();
        if (Preferences.getInstance(getApplicationContext()).getROLE().equals(Common.Role.STUDENT.value())) {
            intent = new Intent(getApplicationContext(), StudentHomeworkUploadAndListActivity.class);
        } else {
            intent = new Intent(getApplicationContext(), HomeWorkStatusListActivity.class);
        }
        intent.putExtra("homeworkId", responseList.get(event.getPosition()).getId());
        intent.putExtra("classId", pojoUtils.getClassId());
        intent.putExtra("sectionId", pojoUtils.getSectionId());
        intent.putExtra("subjectId", pojoUtils.getSubjectId());
        startActivity(intent);
    }


}
