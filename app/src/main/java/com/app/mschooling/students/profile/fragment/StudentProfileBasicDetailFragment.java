package com.app.mschooling.students.profile.fragment;


import static android.app.Activity.RESULT_OK;
import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.mschooling.class_section_subject.activity.ClassListActivity;
import com.app.mschooling.class_section_subject.adapter.OptionalSubjectAdapter;
import com.app.mschooling.base.fragment.BaseFragment;
import com.app.mschooling.com.R;
import com.app.mschooling.event_handler.EventFileUrl;
import com.app.mschooling.utils.AppUser;
import com.app.mschooling.utils.Helper;
import com.app.mschooling.utils.ParameterConstant;
import com.mschooling.transaction.common.Firebase;
import com.mschooling.transaction.common.api.Common;
import com.mschooling.transaction.common.api.Status;
import com.mschooling.transaction.common.student.response.StudentBasicResponse;
import com.mschooling.transaction.request.student.UpdateStudentRequest;
import com.mschooling.transaction.response.profile.GetStudentProfileResponse;
import com.mschooling.transaction.response.student.GetClassResponse;
import com.mschooling.transaction.response.student.UpdateStudentResponse;
import com.mschooling.transaction.response.subject.GetStandaloneSubjectResponse;
import com.mschooling.transaction.response.subject.StandaloneSubject;

import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class StudentProfileBasicDetailFragment extends BaseFragment {

    GetStudentProfileResponse response;
    GetClassResponse classResponse;

    @BindView(R.id.image)
    CircleImageView image;
    @BindView(R.id.captureImage)
    RelativeLayout captureImage;
    @BindView(R.id.firstName)
    EditText firstName;

    @BindView(R.id.lastName)
    EditText lastName;

    @BindView(R.id.mobile)
    EditText mobile;
    @BindView(R.id.email)
    EditText email;
    @BindView(R.id.rollNumber)
    EditText rollNumber;
    @BindView(R.id.section)
    TextView section;
    @BindView(R.id.gender)
    Spinner gender;
    @BindView(R.id.dob)
    TextView dob;
    @BindView(R.id.dateLayout)
    RelativeLayout dateLayout;
    @BindView(R.id.submit)
    Button submit;
    @BindView(R.id.clazz)
    TextView clazz;
    @BindView(R.id.classLayout)
    LinearLayout classLayout;
    @BindView(R.id.sectionLayout)
    LinearLayout sectionLayout;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.optionalHeading)
    TextView optionalHeading;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.mainLayout)
    LinearLayout mainLayout;

    UpdateStudentRequest mRequest = new UpdateStudentRequest();
    String classId, sectionId;

    public void init(GetStudentProfileResponse response, boolean update) {
        this.response = response;
    }

    StudentBasicResponse responseStudentBasicResponse;

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.basic_detail_fragment, container, false);
        ButterKnife.bind(this, view);
        Helper.enableDisableView(mainLayout, submit, AppUser.getInstance().isUpdate());

        responseStudentBasicResponse = response.getStudentBasicResponse();
        imageName = Helper.getRandom();
        firstName.setText(responseStudentBasicResponse.getfName());
        lastName.setText(responseStudentBasicResponse.getlName());
        mobile.setText(responseStudentBasicResponse.getMobile());
        rollNumber.setText(responseStudentBasicResponse.getRollNumber());
        email.setText(responseStudentBasicResponse.getEmail());
        if (responseStudentBasicResponse.getGender().value().equals(Common.Gender.Male.value())) {
            gender.setSelection(0);
        } else if (responseStudentBasicResponse.getGender().value().equals(Common.Gender.Female.value())) {
            gender.setSelection(1);
        } else {
            gender.setSelection(1);
        }
        dob.setText(responseStudentBasicResponse.getDob());
        clazz.setText(responseStudentBasicResponse.getClassName());
        section.setText(responseStudentBasicResponse.getSectionName());
        classId = responseStudentBasicResponse.getClassId();
        sectionId = responseStudentBasicResponse.getSectionId();


        optionalHeading.setVisibility(View.GONE);
        recyclerView.setVisibility(View.GONE);


        classLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(getActivity(), ClassListActivity.class), 100);
            }
        });

        dateLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar myCalendar = Calendar.getInstance();
                myCalendar.set(Integer.valueOf(Helper.getCurrentYear()) - 2, Integer.valueOf(Helper.getCurrentMonth()) - 1, Integer.valueOf(Helper.getCurrentDay()));

                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        // TODO Auto-generated method stub
                        myCalendar.set(Calendar.YEAR, year);
                        myCalendar.set(Calendar.MONTH, monthOfYear);
                        myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        SimpleDateFormat sdf1 = new SimpleDateFormat(ParameterConstant.getDateFormat() , Locale.getDefault());
                        dob.setText(sdf1.format(myCalendar.getTime()));
                        dob.setError(null);

                    }
                }, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH));
//                datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
                datePickerDialog.getDatePicker().setMaxDate(myCalendar.getTimeInMillis());
                datePickerDialog.show();
            }
        });


        captureImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogPlus();
            }
        });


        subjectApi();
        return view;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            super.onActivityResult(requestCode, resultCode, data);
            if (resultCode == RESULT_OK) {
                if (requestCode == REQUEST_CAMERA) {
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inSampleSize = 8;
//                    image.setImageBitmap(BitmapFactory.decodeFile(fileUri.getPath(), options));
                    fileUri = Uri.fromFile(Helper.getOutputMediaFile(MEDIA_TYPE_IMAGE, imageName));
                    String str = compressImage(this.fileUri);
                    Uri uri = Uri.fromFile(new File(str));
                    image.setImageURI(fileUri);
                }
                if (requestCode == REQUEST_GALLERY) {
                    try {
                        fileUri = data.getData();
                        String str = compressImage(fileUri);
                        Uri uri = Uri.fromFile(new File(str));
                        image.setImageURI(fileUri);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }


    void setListAndAdapter(GetStandaloneSubjectResponse response) {
        recyclerView.setVisibility(View.VISIBLE);
        OptionalSubjectAdapter.mapPosition = new Hashtable<>();
        Map<String, List<StandaloneSubject>> optionalSubjectMap = Helper.getOptionalPaper(response);
        recyclerView.setHasFixedSize(true);
        recyclerView.setFocusable(false);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 1));
        for (int i = 0; i < this.responseStudentBasicResponse.getSubjects().size(); i++) {
            for (int j = 0; j < response.getStandaloneSubjectResponses().size(); j++) {
                for (int k = 0; k < response.getStandaloneSubjectResponses().get(j).getStandaloneSubjects().size(); k++) {
                    if (this.responseStudentBasicResponse.getSubjects().get(i).equals(response.getStandaloneSubjectResponses().get(j).getStandaloneSubjects().get(k).getId())) {
                        OptionalSubjectAdapter.mapPosition.put(i, k);
                    }
                }
            }
        }
        recyclerView.setAdapter(new OptionalSubjectAdapter(getActivity(), AppUser.getInstance().isUpdate(), optionalSubjectMap));
        if (optionalSubjectMap.size() > 0) {
            optionalHeading.setVisibility(View.VISIBLE);
        } else {
            optionalHeading.setVisibility(View.GONE);
        }
    }


    @Subscribe
    public void updateStudent(UpdateStudentResponse response) {
        if (response.getStatus().value() == Status.SUCCESS.value()) {
            dialogSuccess(response.getMessage().getMessage());
        } else {
            dialogFailed(response.getMessage().getMessage());
        }
    }

    void subjectApi() {
        apiCallBack(getApiCommonController().getSubjectList(ParameterConstant.getRole(getActivity()), classId, sectionId));
    }

    @Subscribe
    public void getUrl(EventFileUrl uri) {
        Firebase firebase = new Firebase();
        firebase.setUrl(uri.getUrl());
        mRequest.getStudentProfileRequest().setProfileFirebase(firebase);
    }

    @Subscribe
    public void getSubject(GetStandaloneSubjectResponse response) {
        if (response.getStatus().value().equals(Status.SUCCESS.value())) {
            setListAndAdapter(response);
        } else {
            dialogFailed(response.getMessage().getMessage());
        }
    }
}

