package com.app.mschooling.add_student;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;

import com.app.mschooling.base.activity.BaseActivity;
import com.app.mschooling.class_section_subject.activity.ClassSectionSelectionActivity;
import com.app.mschooling.class_section_subject.adapter.OptionalSubjectAdapter;
import com.app.mschooling.com.R;
import com.app.mschooling.com.databinding.ActivityAddStudentBasicDetailBinding;
import com.app.mschooling.event_handler.EventFileUrl;
import com.app.mschooling.utils.Helper;
import com.app.mschooling.utils.ParameterConstant;
import com.mschooling.transaction.common.Firebase;
import com.mschooling.transaction.common.api.Common;
import com.mschooling.transaction.common.api.Status;
import com.mschooling.transaction.request.student.AddStudentRequest;
import com.mschooling.transaction.response.student.AddStudentResponse;
import com.mschooling.transaction.response.subject.GetStandaloneSubjectResponse;
import com.mschooling.transaction.response.subject.StandaloneSubject;

import org.greenrobot.eventbus.Subscribe;

import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import butterknife.ButterKnife;

public class AddStudent1 extends BaseActivity {


    AddStudentRequest mRequest;
    String classId;
    String sectionId;
    String mob;
    ActivityAddStudentBasicDetailBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_student_basic_detail);
        ButterKnife.bind(this);
        toolbarClick(getString(R.string.add_student));
        binding.sectionLayout.setVisibility(View.GONE);
        binding.optionalHeading.setVisibility(View.GONE);
        binding.sectionLayout.setVisibility(View.GONE);
        mRequest = new AddStudentRequest();
        imageName = Helper.getRandom();


//        removeError(binding.firstNameLayout, binding.firstName);
//        removeError(binding.firstNameLayout, binding.firstName);
//        removeError(binding.mobileLayout, binding.mobile);

        binding.uploadLayout.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), AddStudentFileUploadActivity.class)));
        binding.classLayout.setOnClickListener(view -> startActivityForResult(new Intent(getApplicationContext(), ClassSectionSelectionActivity.class), 100));


        binding.dateLayout.setOnClickListener(view -> {
            final Calendar myCalendar = Calendar.getInstance();
            myCalendar.set(Integer.parseInt(Helper.getCurrentYear()) - 2, Helper.getCurrentMonth() - 1, Helper.getCurrentDay());

            DatePickerDialog datePickerDialog = new DatePickerDialog(AddStudent1.this, (view1, year, monthOfYear, dayOfMonth) -> {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                SimpleDateFormat sdf1 = new SimpleDateFormat(ParameterConstant.getDateFormat() , Locale.getDefault());
                binding.dob.setText(sdf1.format(myCalendar.getTime()));

            }, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH));
//                datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
            datePickerDialog.getDatePicker().setMaxDate(myCalendar.getTimeInMillis());
            datePickerDialog.show();
        });


        binding.submit.setOnClickListener(v -> {
            boolean isValid = true;
            if (binding.firstName.getText().toString().isEmpty()) {
                isValid = setError(binding.firstNameLayout, binding.firstName, getString(R.string.please_enter_name));
            }
            if (binding.mobile.getText().toString().isEmpty()) {
                isValid = setError(binding.mobileLayout, binding.mobile, getString(R.string.please_mobile_number));
            }
            if (binding.mobile.getText().toString().length() != 10 && isValid) {
                isValid = setError(binding.mobileLayout, binding.mobile, getString(R.string.please_valid_mobile_number));
            }
            if (binding.dob.getText().toString().isEmpty()) {
                isValid = setError(binding.dateLayout, binding.dob, getString(R.string.please_select_dob));
            }
            if (binding.clazz.getText().toString().isEmpty()) {
                isValid = setError(binding.classLayout, binding.clazz, getString(R.string.select_class));
            }

            if (isValid) {
                mRequest.setfName(binding.firstName.getText().toString());
                mRequest.setlName(binding.lastName.getText().toString());
                mRequest.setMobile(binding.mobile.getText().toString());
                mRequest.setEmail(binding.email.getText().toString());
                mRequest.setRollNumber(binding.rollNumber.getText().toString());
                mRequest.setGender(Common.Gender.valueOf(binding.gender.getSelectedItem().toString()));
                mRequest.setDob(binding.dob.getText().toString());
                mRequest.setClassId(classId);
                mRequest.setSectionId(sectionId);
                List<String> list = new ArrayList<>();
                Map<Integer, String> map = OptionalSubjectAdapter.mapId;
                for (Integer key : map.keySet()) {
                    list.add(map.get(key));
                }
                mRequest.setSubjects(list);
                mob = binding.mobile.getText().toString();
                apiCallBack(getApiCommonController().addStudent(mRequest));
            } else {
                dialogError(getString(R.string.please_check_form));
            }

        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            super.onActivityResult(requestCode, resultCode, data);
            if (resultCode == RESULT_OK) {
                if (requestCode == 100) {
                    classId = data.getStringExtra("classId");
                    binding.clazz.setText(data.getStringExtra("className"));
                    sectionId = data.getStringExtra("sectionId");
                    binding.section.setText(data.getStringExtra("sectionName"));
                    binding.sectionLayout.setVisibility(View.VISIBLE);
                    apiCallBack(getApiCommonController().getSubjectList(
                            ParameterConstant.getRole(this), classId, null));
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }


    @Subscribe
    public void addStudent(AddStudentResponse response) {
        if (response.getStatus().value().equals(Status.SUCCESS.value())) {
            binding.firstName.setText("");
            binding.lastName.setText("");
            binding.mobile.setText("");
            binding.email.setText("");
            binding.dob.setText("");
            binding.rollNumber.setText("");
            binding.gender.setSelection(0);
            binding.clazz.setText("");
            binding.gender.setSelection(0);
            binding.sectionLayout.setVisibility(View.GONE);
            binding.recyclerView.setVisibility(View.GONE);
            binding.optionalHeading.setVisibility(View.GONE);

            Dialog dialog = new Dialog(this);
            dialog.getWindow().requestFeature(1);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            dialog.setContentView(R.layout.dialog_add_more);
            dialog.setCancelable(false);
            TextView message = dialog.findViewById(R.id.message);
            TextView whatsApp = dialog.findViewById(R.id.whatsApp);
            TextView addMore = dialog.findViewById(R.id.addMore);
            TextView cancel = dialog.findViewById(R.id.cancel);
            message.setText(response.getMessage().getMessage());
            whatsApp.setOnClickListener(v -> whatsApp(response.getName(), response.getMobile(), response.getEnrollmentId(), response.getPasscode()));
            addMore.setOnClickListener(v -> {
                dialog.dismiss();
                Intent intent = new Intent(getApplicationContext(), AddStudentProfileImageActivity.class);
                intent.putExtra("id", response.getEnrollmentId());
                intent.putExtra("mobile", mob);
                startActivity(intent);
            });
            cancel.setOnClickListener(v -> dialog.dismiss());
            dialog.show();

        } else {
            dialogError(response.getMessage().getMessage());
        }
//
    }

    @Subscribe
    public void getSubject(GetStandaloneSubjectResponse response) {
        if (response.getStatus().value().equals(Status.SUCCESS.value())) {
            setListAndAdapter(response);
        } else {
            dialogFailed(response.getMessage().getMessage());
        }
    }


    @Subscribe
    public void getUrl(EventFileUrl uri) {
        Firebase firebase = new Firebase();
        firebase.setUrl(uri.getUrl());
        mRequest.setFirebase(firebase);
    }


    void setListAndAdapter(GetStandaloneSubjectResponse response) {
        binding.recyclerView.setVisibility(View.VISIBLE);
        Map<String, List<StandaloneSubject>> optionalSubjectMap = Helper.getOptionalPaper(response);
        binding.recyclerView.setHasFixedSize(true);
        binding.recyclerView.setFocusable(false);
        binding.recyclerView.setNestedScrollingEnabled(false);
        binding.recyclerView.setLayoutManager(new GridLayoutManager(this, 1));
        binding.recyclerView.setAdapter(new OptionalSubjectAdapter(this, true, optionalSubjectMap));

        try {
            if (optionalSubjectMap.size() > 0) {
                binding.optionalHeading.setVisibility(View.VISIBLE);
            } else {
                binding.optionalHeading.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    String getMessage(String name, String enrollmentId, String passcode) {

        return "Hi " + name +
                ",\n" +
                "Welcome to mSchooling!" +
                "\n" +
                "Please see below details -" +
                "\n" +
                "Enrollment Id = " + enrollmentId +
                "\n" +
                "Passcode = " + passcode +
                "\n" +
                "Get app-https://play.google.com/store/apps/details?id=com.app.mschooling" +
                "\n" +
                "\n" +
                "Regards," +
                "\n" +
                "mSchooling";
    }


    @SuppressLint("QueryPermissionsNeeded")
    public void whatsApp(String name, String mobile, String enrollmentId, String passcode) {
        PackageManager packageManager = getPackageManager();
        Intent i = new Intent(Intent.ACTION_VIEW);
        try {
            String url = "https://api.whatsapp.com/send?phone=91" + mobile + "&text=" + URLEncoder.encode(getMessage(name, enrollmentId, passcode), "UTF-8");
            i.setPackage("com.whatsapp");
            i.setData(Uri.parse(url));
            if (i.resolveActivity(packageManager) != null) {
                startActivity(i);
            } else {
                Toast.makeText(getApplicationContext(), getString(R.string.whatsapp_not_exist), Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), getString(R.string.whatsapp_not_exist), Toast.LENGTH_SHORT).show();
        }
    }


}
