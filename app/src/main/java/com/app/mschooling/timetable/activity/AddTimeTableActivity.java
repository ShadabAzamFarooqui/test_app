package com.app.mschooling.timetable.activity;

import android.app.Dialog;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.mschooling.timetable.adapter.AddTimeTableAdapter;
import com.app.mschooling.class_section_subject.adapter.SubjectListDialogPlusAdapter;
import com.app.mschooling.teachers.list.adapter.TeacherListDialogPlusAdapter;
import com.app.mschooling.timetable.adapter.TimeTableHorizontalAdapter;
import com.app.mschooling.base.activity.BaseActivity;
import com.app.mschooling.com.R;
import com.app.mschooling.utils.AppUser;
import com.mschooling.transaction.common.TimeTable;
import com.mschooling.transaction.common.TimeTableRow;
import com.mschooling.transaction.common.Timeslot;
import com.mschooling.transaction.common.api.Common;
import com.mschooling.transaction.common.api.Status;
import com.mschooling.transaction.request.timetable.AddTimeTableRequestList;
import com.mschooling.transaction.request.timetable.TimetableCriteria;
import com.mschooling.transaction.response.subject.GetStandaloneSubjectResponse;
import com.mschooling.transaction.response.teacher.GetAllocationResponse;
import com.mschooling.transaction.response.timetable.AddTimeTableResponse;
import com.mschooling.transaction.response.timetable.GetTimeTableResponseList;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.DialogPlusBuilder;
import com.orhanobut.dialogplus.GridHolder;
import com.orhanobut.dialogplus.OnClickListener;
import com.orhanobut.dialogplus.OnItemClickListener;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;


public class AddTimeTableActivity extends BaseActivity {


    RecyclerView recyclerView;
    TextView addPeriod;
    TextView clazz;
    Button submit;
    public static AddTimeTableAdapter adapter;
    TextView space;
    TextView copyMonday, copyTuesday, copyWednesday, copyThursday, copyFriday, copySaturday;
    public static String classId;
    public static String className;
    public static String sectionId;
    public static String sectionName;
    List<TimeTableRow> timeTableRequestList;
    boolean isStudent;
    LinearLayout pasteLayout;

    public static List<TimeTableRow> deletedRowList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_time_table);
        context = this;
        AppUser.getInstance().setGetSubjectResponse(null);
        AppUser.getInstance().setAllocationResponseList(null);
        deletedRowList.clear();
        recyclerView = findViewById(R.id.recyclerView);
        pasteLayout = findViewById(R.id.pasteLayout);
        clazz = findViewById(R.id.clazz);
        addPeriod = findViewById(R.id.addPeriod);
        space = findViewById(R.id.space);
        submit = findViewById(R.id.submit);
        copyMonday = findViewById(R.id.copyMonday);
        copyTuesday = findViewById(R.id.copyTuesday);
        copyWednesday = findViewById(R.id.copyWednesday);
        copyThursday = findViewById(R.id.copyThursday);
        copyFriday = findViewById(R.id.copyFriday);
        copySaturday = findViewById(R.id.copySaturday);
        space.setVisibility(View.GONE);

        classId = getIntent().getStringExtra("classId");
        className = getIntent().getStringExtra("className");
        sectionName = getIntent().getStringExtra("sectionName");
        sectionId = getIntent().getStringExtra("sectionId");
        if (getIntent().getStringExtra("student") == null) {
            isStudent = false;
            pasteLayout.setVisibility(View.VISIBLE);
            addPeriod.setVisibility(View.VISIBLE);
            submit.setVisibility(View.VISIBLE);
            TimetableCriteria request = new TimetableCriteria();
            request.setClassId(classId);
            request.setSectionId(sectionId);
            request.setSubjectId("");
            request.setTeacherId("");
            apiCallBack(getApiCommonController().getTimeTable(request));
        } else {
            isStudent = true;
            pasteLayout.setVisibility(View.GONE);
            addPeriod.setVisibility(View.GONE);
            submit.setVisibility(View.GONE);
            apiCallBack(getApiCommonController().getTimeTable(new TimetableCriteria()));

        }

        clazz.setText(className + " (" + sectionName + ")");
        toolbarClick(className + " (" + sectionName + ")");

        copyMonday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < timeTableRequestList.size(); i++) {
                    List<TimeTable> timeTableList = timeTableRequestList.get(i).getTimeTables();
                    TimeTable timeTable = new TimeTable(timeTableList.get(1).getId(),
                            timeTableList.get(0),
                            Common.Day.TUESDAY);
                    timeTableRequestList.get(i).getTimeTables().set(1, timeTable);
                    adapter.notifyDataSetChanged();
                }
            }
        });
        copyTuesday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < timeTableRequestList.size(); i++) {
                    List<TimeTable> timeTableList = timeTableRequestList.get(i).getTimeTables();
                    TimeTable timeTable = new TimeTable(timeTableList.get(2).getId(),
                            timeTableList.get(1),
                            Common.Day.WEDNESDAY);
                    timeTableRequestList.get(i).getTimeTables().set(2, timeTable);
                    adapter.notifyDataSetChanged();
                }

            }
        });
        copyWednesday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < timeTableRequestList.size(); i++) {
                    List<TimeTable> timeTableList = timeTableRequestList.get(i).getTimeTables();
                    TimeTable timeTable = new TimeTable(timeTableList.get(3).getId(),
                            timeTableList.get(2),
                            Common.Day.THURSDAY);
                    timeTableRequestList.get(i).getTimeTables().set(3, timeTable);
                    adapter.notifyDataSetChanged();
                }

            }
        });
        copyThursday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < timeTableRequestList.size(); i++) {
                    List<TimeTable> timeTableList = timeTableRequestList.get(i).getTimeTables();
                    TimeTable timeTable = new TimeTable(timeTableList.get(4).getId(),
                            timeTableList.get(3),
                            Common.Day.FRIDAY);

                    timeTableRequestList.get(i).getTimeTables().set(4, timeTable);
                    adapter.notifyDataSetChanged();
                }

            }
        });
        copyFriday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < timeTableRequestList.size(); i++) {
                    List<TimeTable> timeTableList = timeTableRequestList.get(i).getTimeTables();
                    TimeTable timeTable = new TimeTable(timeTableList.get(5).getId(),
                            timeTableList.get(4),
                            Common.Day.SATURDAY);

                    timeTableRequestList.get(i).getTimeTables().set(5, timeTable);
                    adapter.notifyDataSetChanged();
                }

            }
        });
        copySaturday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < timeTableRequestList.size(); i++) {
                    List<TimeTable> timeTableList = timeTableRequestList.get(i).getTimeTables();
                    TimeTable timeTable = new TimeTable(timeTableList.get(6).getId(),
                            timeTableList.get(5),
                            Common.Day.SUNDAY);
                    timeTableRequestList.get(i).getTimeTables().set(6, timeTable);
                    adapter.notifyDataSetChanged();
                }

            }
        });


        findViewById(R.id.submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddTimeTableRequestList request = new AddTimeTableRequestList();
                request.setDeletedTableRows(deletedRowList);
                request.setClassId(classId);
                try {
                    request.setSectionId(sectionId);
                } catch (Exception e) {
                    request.setSectionId("");
                }
                request.setTimeTableRows(timeTableRequestList);
                apiCallBack(getApiCommonController().saveTimeTable(request));

            }
        });


        addPeriod.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    timeTableRequestList.add(new TimeTableRow(new Timeslot()));
                    adapter.notifyDataSetChanged();
                } catch (Exception E) {
                    dialogError(getString(R.string.please_select_class));
                }

            }
        });
    }


    @Subscribe
    public void saveTimeTable(AddTimeTableResponse response) {
        if (response.getStatus().value().equals(Status.SUCCESS.value())) {
            dialogSuccessFinish(response.getMessage().getMessage());
        } else {
            dialogError(response.getMessage().getMessage());
        }
    }


    @Subscribe
    public void getTimeTable(GetTimeTableResponseList response) {
        if (response.getStatus().value().equals(Status.SUCCESS.value())) {
            if (response.getGetTimeTableResponses().size() > 0) {
                timeTableRequestList = response.getGetTimeTableResponses().get(0).getTimeTableRows();
                recyclerView.setHasFixedSize(true);
                recyclerView.setFocusable(false);
                recyclerView.setNestedScrollingEnabled(false);
                recyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(), 1));
                for (int i = response.getGetTimeTableResponses().get(0).getTimeTableRows().size(); i < 8; i++) {
                    response.getGetTimeTableResponses().get(0).getTimeTableRows().add(new TimeTableRow(new Timeslot()));
                }
                adapter = new AddTimeTableAdapter(this, isStudent, response.getGetTimeTableResponses().get(0).getTimeTableRows(), space);
                recyclerView.setAdapter(adapter);
            } else {
                timeTableRequestList = new ArrayList<>();
                for (int i = 0; i < 8; i++) {
                    timeTableRequestList.add(new TimeTableRow(new Timeslot()));
                }
                recyclerView.setHasFixedSize(true);
                recyclerView.setFocusable(false);
                recyclerView.setNestedScrollingEnabled(false);
                recyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(), 1));
                adapter = new AddTimeTableAdapter(this, isStudent, timeTableRequestList, space);
                recyclerView.setAdapter(adapter);
            }
        } else {

        }
    }

    @Subscribe
    public void getTeacherList(GetAllocationResponse response) {
        if (response.getStatus().value().equals(Status.SUCCESS.value())) {
            AppUser.getInstance().setAllocationResponseList(response);
            DialogPlusBuilder dialogPlusBuilder = DialogPlus.newDialog(this);
            dialogPlusBuilder.setHeader(R.layout.teacher_timetable_header);
            dialogPlusBuilder.setContentHolder(new GridHolder(1));
            dialogPlusBuilder.setGravity(Gravity.BOTTOM);
            dialogPlusBuilder.setCancelable(true);

            dialogPlusBuilder.setMargin(5, 300, 5, 10);
            dialogPlusBuilder.setPadding(0, 0, 0, 10);
            dialogPlusBuilder.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(DialogPlus dialog, View view) {
                    view.findViewById(R.id.footer_close_button);
                    dialog.dismiss();
                }
            });

            dialogPlusBuilder.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(DialogPlus dialog, Object item, View view, int position) {

                }

            });
            dialogPlusBuilder.setFooter(R.layout.footer);
            dialogPlusBuilder.setExpanded(false); // This will enable the expand feature, (similar to android L share dialog)
            dialogPlusBuilder.setAdapter(new TeacherListDialogPlusAdapter(this,
                    response.getAllocationResponses(),
                    TimeTableHorizontalAdapter.groupPosition, TimeTableHorizontalAdapter.childPosition));
            dialogPlus = dialogPlusBuilder.create();
            dialogPlus.show();

        } else {
            dialogError(response.getMessage().getMessage());
        }
    }


    public static DialogPlus dialogPlus;

    @Subscribe
    public void getSubject(GetStandaloneSubjectResponse response) {
        if (response.getStatus().value().equals(Status.SUCCESS.value())) {
            AppUser.getInstance().setGetSubjectResponse(response);
            DialogPlusBuilder dialogPlusBuilder = DialogPlus.newDialog(this);
            dialogPlusBuilder.setHeader(R.layout.subject_header);
            dialogPlusBuilder.setContentHolder(new GridHolder(1));
            dialogPlusBuilder.setGravity(Gravity.BOTTOM);
            dialogPlusBuilder.setCancelable(true);

            dialogPlusBuilder.setMargin(5, 300, 5, 10);
            dialogPlusBuilder.setPadding(0, 0, 0, 10);
            dialogPlusBuilder.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(DialogPlus dialog, View view) {
                    view.findViewById(R.id.footer_close_button);
                    dialog.dismiss();
                }
            });

            dialogPlusBuilder.setOnItemClickListener((dialog, item, view, position) -> {

            });
            dialogPlusBuilder.setFooter(R.layout.footer);
            dialogPlusBuilder.setExpanded(false); // This will enable the expand feature, (similar to android L share dialog)
            dialogPlusBuilder.setAdapter(new SubjectListDialogPlusAdapter(this,
                    response.getStandaloneSubjectResponses(),
                    TimeTableHorizontalAdapter.groupPosition, TimeTableHorizontalAdapter.childPosition));
            dialogPlus = dialogPlusBuilder.create();
            dialogPlus.show();
        } else {
            dialogError(response.getMessage().getMessage());
        }
    }
}
