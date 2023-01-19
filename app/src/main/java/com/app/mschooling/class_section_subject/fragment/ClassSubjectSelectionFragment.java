package com.app.mschooling.class_section_subject.fragment;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.app.mschooling.class_section_subject.activity.ClassListActivity;
import com.app.mschooling.timetable.activity.AddTimeTableActivity;
import com.app.mschooling.base.fragment.BaseFragment;
import com.app.mschooling.com.R;
import com.app.mschooling.utils.AppUser;
import com.mschooling.transaction.request.timetable.TimetableCriteria;
import com.mschooling.transaction.response.student.SectionResponse;

import java.util.ArrayList;
import java.util.List;

public class ClassSubjectSelectionFragment extends BaseFragment {

//    views
    TextView clazz;
    Spinner section;
    LinearLayout classLayout;
    LinearLayout sectionLayout;
    Button submit;

//    variables
    List<String> listSection;
    List<String> listSectionId;
    String classId;
    String className;
    String sectionId;
    String sectionName;

    List<SectionResponse> sectionResponses;




    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.activity_class_subject, container, false);



        toolbarClick(view,getString(R.string.select_class));

//        setAnimation(view);

//        bind views
        clazz =view. findViewById(R.id.clazz);
        classLayout = view. findViewById(R.id.classLayout);
        sectionLayout = view. findViewById(R.id.sectionLayout);
        section = view. findViewById(R.id.section);
        submit = view. findViewById(R.id.submit);

        sectionLayout.setVisibility(View.GONE);



        classLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(getActivity(), ClassListActivity.class), 100);
            }
        });


        section.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                TimetableCriteria request = new TimetableCriteria();
                request.setClassId(classId);
                try {
                    request.setSectionId(listSectionId.get(position));
                    sectionId = listSectionId.get(position);
                    sectionName = listSection.get(position);
                } catch (Exception e) {
                    request.setSectionId("");
                }
                request.setSubjectId("");
                request.setTeacherId("");

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clazz.getText().toString().isEmpty()){
                    dialogError(getString(R.string.please_select_the_class));
                    return;
                }
                Intent intent=new Intent(getActivity(), AddTimeTableActivity.class);
                intent.putExtra("classId",classId);
                intent.putExtra("className",className);
                intent.putExtra("sectionId",sectionId);
                intent.putExtra("sectionName",sectionName);
                startActivity(intent);
            }
        });

        return view;

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            super.onActivityResult(requestCode, resultCode, data);
            if (resultCode == RESULT_OK) {
                if (requestCode == 100) {
                    sectionId = null;
                    sectionName = null;
                    classId = data.getStringExtra("id");
                    className = data.getStringExtra("name");
                    clazz.setText(className);
                    /*timeTableRequestList = new ArrayList<>();
                    for (int i = 0; i < 8; i++) {
                        timeTableRequestList.add(new TimeTableRow(new Timeslot()));
                    }
                    recyclerView.setHasFixedSize(true);
                    recyclerView.setFocusable(false);
                    recyclerView.setNestedScrollingEnabled(false);
                    recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 1));
                    adapter = new AddTimeTableAdapter(this, timeTableRequestList, space);
                    recyclerView.setAdapter(adapter);*/
                    listSection = new ArrayList<>();
                    listSectionId = new ArrayList<>();
                    sectionResponses= AppUser.getInstance().getSectionResponse();
                    for (int i = 0; i < sectionResponses.size(); i++) {
                        listSection.add(sectionResponses.get(i).getName());
                        listSectionId.add(sectionResponses.get(i).getId());
                    }
                    if (sectionResponses.size() == 0) {
                        sectionLayout.setVisibility(View.GONE);
                    } else {
                        sectionLayout.setVisibility(View.VISIBLE);
                    }

                    ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, listSection);
                    spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    section.setAdapter(spinnerAdapter);
                }
            }
        } catch (Exception ex) {
        }
    }




}
