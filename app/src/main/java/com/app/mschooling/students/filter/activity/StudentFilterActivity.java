package com.app.mschooling.students.filter.activity;


import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.mschooling.base.activity.BaseActivity;
import com.app.mschooling.com.R;
import com.app.mschooling.students.filter.adapter.StudentFilterListAdapter;
import com.app.mschooling.students.list.activity.StudentsListActivity;
import com.app.mschooling.utils.AppUser;
import com.app.mschooling.utils.Helper;
import com.app.mschooling.utils.ParameterConstant;
import com.google.gson.Gson;
import com.mschooling.transaction.common.api.Common;
import com.mschooling.transaction.common.api.Status;
import com.mschooling.transaction.filter.ListCriteria;
import com.mschooling.transaction.response.section.GetStandaloneClassResponse;
import com.mschooling.transaction.response.student.ClassResponse;
import com.mschooling.transaction.response.student.GetClassResponse;

import org.greenrobot.eventbus.Subscribe;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;


public class StudentFilterActivity extends BaseActivity {

    EditText search;
    RecyclerView recycler_view;
    Button submit;
    StudentFilterListAdapter adapter;
    List<String> dataList;
    List<String> classIdList;
    LinearLayout noFilter;
    LinearLayout classWiseLayout;
    CheckBox nameWise;
    CheckBox enrollWise;
    CheckBox admissionDateWise;
    CheckBox male;
    CheckBox female;
    CheckBox active;
    CheckBox disabled;
    CheckBox deleted;
    EditText name;
    ImageView icon;
    EditText enrolmentId;

    public Dialog dialog;
    private DatePickerDialog DatePickerDialog;
    private SimpleDateFormat dateFormatter;
    String startDate;
    String endDate;
    ListCriteria criteria = new ListCriteria();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_student_filter_list);

        toolbarClick(getString(R.string.tool_filter));
        init();

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (nameWise.isChecked()) {
                    criteria.setName(name.getText().toString());
                }
                if (enrollWise.isChecked()) {
                    criteria.setEnrollmentId(enrolmentId.getText().toString());
                }
                if (admissionDateWise.isChecked()) {
                    criteria.setJoiningStartDate(startDate);
                    criteria.setJoiningEndDate(endDate);
                }
                if (male.isChecked()) {
                    criteria.setGender(Common.Gender.Male);
                }
                if (female.isChecked()) {
                    criteria.setGender(Common.Gender.Female);
                }
                if (active.isChecked()) {
                    criteria.setState(Common.State.ACTIVATED);
                }
                if (disabled.isChecked()) {
                    criteria.setState(Common.State.DISABLED);
                }
                if (deleted.isChecked()) {
                    criteria.setState(Common.State.DELETED);
                }
                Intent intent = new Intent(getApplicationContext(), StudentsListActivity.class);
                intent.putExtra("criteria", new Gson().toJson(criteria));
                startActivity(intent);
            }
        });

        noFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                criteria.setState(Common.State.ACTIVATED);
                startActivity(new Intent(getApplicationContext(), StudentsListActivity.class));
            }
        });

        nameWise.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    name.setVisibility(View.VISIBLE);
                } else {
                    name.setVisibility(View.GONE);
                    name.setText("");
                }
            }
        });


        enrollWise.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    enrolmentId.setVisibility(View.VISIBLE);
                } else {
                    enrolmentId.setVisibility(View.GONE);
                    enrolmentId.setText("");
                }
            }
        });

        admissionDateWise.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    showDateDialog(admissionDateWise);
                }
            }
        });

        male.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    female.setChecked(false);
                }
            }
        });

        female.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    male.setChecked(false);
                }
            }
        });

        active.setChecked(true);
        active.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    disabled.setChecked(false);
                    deleted.setChecked(false);
                } else {
                    if (!disabled.isChecked() && !deleted.isChecked()) {
                        active.setChecked(true);
                    }
                }
            }
        });

        disabled.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    active.setChecked(false);
                    deleted.setChecked(false);
                } else {
                    if (!disabled.isChecked() && !deleted.isChecked()) {
                        active.setChecked(true);
                    }
                }
            }
        });


        deleted.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    disabled.setChecked(false);
                    active.setChecked(false);
                } else {
                    if (!disabled.isChecked() && !deleted.isChecked()) {
                        active.setChecked(true);
                    }
                }
            }
        });


        classWiseLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (recycler_view.getVisibility() == View.VISIBLE) {
                    recycler_view.setVisibility(View.GONE);
                    icon.setRotation(90);
                } else {
                    recycler_view.setVisibility(View.VISIBLE);
                    icon.setRotation(270);
                }
            }
        });
    }


    void init() {
        submit = findViewById(R.id.submit);
        search = findViewById(R.id.search);
        noFilter = findViewById(R.id.noFilter);
        classWiseLayout = findViewById(R.id.classWiseLayout);
        nameWise = findViewById(R.id.nameWise);
        enrollWise = findViewById(R.id.enrollWise);
        admissionDateWise = findViewById(R.id.admissionDateWise);
        male = findViewById(R.id.male);
        female = findViewById(R.id.female);
        recycler_view = findViewById(R.id.recycler_view);
        name = findViewById(R.id.name);
        enrolmentId = findViewById(R.id.enrolmentId);
        icon = findViewById(R.id.icon);
        active = findViewById(R.id.active);
        disabled = findViewById(R.id.disabled);
        deleted = findViewById(R.id.deleted);
        name.setVisibility(View.GONE);
        enrolmentId.setVisibility(View.GONE);
    }

    void setAdapter() {
        dataList = new ArrayList<>();
        classIdList = new ArrayList<>();
        List<ClassResponse> classResponseList = AppUser.getInstance().getClassResponseList();
        for (int i = 0; i < classResponseList.size(); i++) {
            dataList.add(classResponseList.get(i).getName());
            classIdList.add(classResponseList.get(i).getId());
        }


        recycler_view.setHasFixedSize(true);
        recycler_view.setFocusable(false);
        recycler_view.setNestedScrollingEnabled(false);
        recycler_view.setLayoutManager(new GridLayoutManager(StudentFilterActivity.this, 1));
        adapter = new StudentFilterListAdapter(StudentFilterActivity.this, dataList, classIdList, criteria);
        recycler_view.setAdapter(adapter);
    }


    @Subscribe
    public void timeout(String msg) {

    }


    @Subscribe
    public void getClassList(GetStandaloneClassResponse response) {
        if (response.getStatus().value().equals(Status.SUCCESS.value())) {
            AppUser.getInstance().setClassResponseList(response.getClassResponses());
            setAdapter();
        } else {
            dialogFailed(response.getMessage().getMessage());
        }
    }


    public void showDateDialog(CheckBox checkBox) {
        dateFormatter = new SimpleDateFormat(ParameterConstant.getDateFormat() , Locale.US);
        Dialog dialog = new Dialog(StudentFilterActivity.this);
        dialog.getWindow().requestFeature(1);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        dialog.setContentView(R.layout.date_pick_dialog);
        dialog.setCancelable(false);
        TextView date1 = (TextView) dialog.findViewById(R.id.date1);
        TextView date2 = (TextView) dialog.findViewById(R.id.date2);
        Button submit = (Button) dialog.findViewById(R.id.submit);
        LinearLayout close = dialog.findViewById(R.id.close);
        date1.setText(dateFormatter.format(System.currentTimeMillis()));
        date2.setText(dateFormatter.format(System.currentTimeMillis()));

        dateDialog(date1, Calendar.getInstance());
        dateDialog(date2, Calendar.getInstance());

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkBox.setChecked(false);
                dialog.dismiss();
            }
        });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String start = ((TextView) dialog.findViewById(R.id.date1)).getText().toString();
                String end = ((TextView) dialog.findViewById(R.id.date2)).getText().toString();
                if (Helper.dateValidation(start, end) == -1) {
                    dialogError(getString(R.string.end_date_should_be_greater));
                } else {
                    dialog.dismiss();
                    startDate = date1.getText().toString();
                    endDate = date2.getText().toString();
                }
            }
        });

        dialog.show();
    }

    void dateDialog(TextView textView, Calendar calendar) {
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog = new DatePickerDialog(StudentFilterActivity.this, new DatePickerDialog.OnDateSetListener() {

                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(year, monthOfYear, dayOfMonth);
                        String date = dateFormatter.format(calendar.getTime());
                        textView.setText(date);
                    }

                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

                DatePickerDialog.show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        criteria = new ListCriteria();
        if (AppUser.getInstance().getClassResponseList().size() == 0) {
            apiCallBack(getApiCommonController().getClassList(ParameterConstant.getRole(this)));
        } else {
            setAdapter();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        nameWise.setChecked(false);
        enrollWise.setChecked(false);
        admissionDateWise.setChecked(false);
        male.setChecked(false);
        female.setChecked(false);
        name.setVisibility(View.GONE);
        enrolmentId.setVisibility(View.GONE);
        name.setText("");
        enrolmentId.setText("");
        active.setChecked(true);
    }
}





