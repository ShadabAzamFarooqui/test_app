package com.app.mschooling.teachers.profile.fragment;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;

import androidx.annotation.Nullable;

import com.app.mschooling.base.fragment.BaseFragment;
import com.app.mschooling.com.R;
import com.app.mschooling.event_handler.EventToolbar;
import com.app.mschooling.utils.Helper;
import com.mschooling.transaction.common.api.Status;
import com.mschooling.transaction.common.teacher.request.TeacherAddressRequest;
import com.mschooling.transaction.common.teacher.response.TeacherAddressResponse;
import com.mschooling.transaction.request.profile.UpdateTeacherProfileRequest;
import com.mschooling.transaction.response.profile.GetTeacherProfileResponse;
import com.mschooling.transaction.response.profile.UpdateTeacherProfileResponse;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TeacherProfileAddressFragment extends BaseFragment {

    GetTeacherProfileResponse response;
    @BindView(R.id.altFlat)
    EditText altFlat;
    @BindView(R.id.altStreet)
    EditText altStreet;
    @BindView(R.id.altLandmark)
    EditText altLandmark;
    @BindView(R.id.altPin)
    EditText altPin;
    @BindView(R.id.altCity)
    EditText altCity;
    @BindView(R.id.altState)
    Spinner altState;
    @BindView(R.id.markAs)
    CheckBox markAs;
    @BindView(R.id.perFlat)
    EditText perFlat;
    @BindView(R.id.perStreet)
    EditText perStreet;
    @BindView(R.id.perLandmark)
    EditText perLandmark;
    @BindView(R.id.perPin)
    EditText perPin;
    @BindView(R.id.perCity)
    EditText perCity;
    @BindView(R.id.perState)
    Spinner perState;
    @BindView(R.id.submit)
    Button submit;
    @BindView(R.id.mainLayout)
    LinearLayout mainLayout;
    @BindView(R.id.skipLayout)
    LinearLayout skipLayout;
    boolean update;


    public TeacherProfileAddressFragment(GetTeacherProfileResponse response, boolean update) {
        if (response.getTeacherAddressResponse() == null) {
            response.setTeacherAddressResponse(new TeacherAddressResponse());
        }
        this.update = update;
        this.response = response;
    }

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.profile_address, container, false);
        ButterKnife.bind(this, view);
        Helper.enableDisableView(mainLayout, submit, update);
        if (!update) {
            skipLayout.setVisibility(View.GONE);
        }
        TeacherAddressResponse teacherAddressResponse = response.getTeacherAddressResponse();
        altFlat.setText(teacherAddressResponse.gettHouse());
        altStreet.setText(teacherAddressResponse.gettStreet());
        altLandmark.setText(teacherAddressResponse.gettLandmark());
        altPin.setText(teacherAddressResponse.gettPinCode());
        altCity.setText(teacherAddressResponse.gettCity());
        altState.setSelection(getPosition(R.array.states, teacherAddressResponse.gettState()));

        perFlat.setText(teacherAddressResponse.getHouse());
        perStreet.setText(teacherAddressResponse.getStreet());
        perLandmark.setText(teacherAddressResponse.getLandmark());
        perPin.setText(teacherAddressResponse.getPinCode());
        perCity.setText(teacherAddressResponse.getCity());
        perState.setSelection(getPosition(R.array.states, teacherAddressResponse.getState()));
        if (!altFlat.getText().toString().isEmpty()
                && !altStreet.getText().toString().isEmpty()
                && !altLandmark.getText().toString().isEmpty()
                && !altPin.getText().toString().isEmpty()
                && !altCity.getText().toString().isEmpty()) {
            if (altFlat.getText().toString().equals(perFlat.getText().toString())
                    && altStreet.getText().toString().equals(perStreet.getText().toString())
                    && altLandmark.getText().toString().equals(perLandmark.getText().toString())
                    && altPin.getText().toString().equals(perPin.getText().toString())
                    && altCity.getText().toString().equals(perCity.getText().toString())
                    && altState.getSelectedItem().toString().equals(perState.getSelectedItem().toString())) {
                markAs.setChecked(true);
            }
        }

        textChangeListener(altFlat,perFlat,markAs);
        textChangeListener(altStreet,perStreet,markAs);
        textChangeListener(altLandmark,perLandmark,markAs);
        textChangeListener(altPin,perPin,markAs);
        textChangeListener(altCity,perCity,markAs);
        spinnerListener(altState,perState,markAs);


        markAs.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    perFlat.setText(altFlat.getText().toString());
                    perStreet.setText(altStreet.getText().toString());
                    perLandmark.setText(altLandmark.getText().toString());
                    perPin.setText(altPin.getText().toString());
                    perCity.setText(altCity.getText().toString());
                    perState.setSelection(altState.getSelectedItemPosition());
                } else {
//                    perFlat.setText("");
//                    perStreet.setText("");
//                    perLandmark.setText("");
//                    perPin.setText("");
//                    perCity.setText("");
//                    perState.setSelection(0);
                }
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (altFlat.getText().toString().trim().isEmpty()) {
                    dialogError(getString(R.string.enter_flat_number));
                    return;
                }
                if (altStreet.getText().toString().isEmpty()) {
                    dialogError(getString(R.string.enter_street));
                    return;
                }
                if (altPin.getText().toString().trim().isEmpty()) {
                    dialogError(getString(R.string.enter_pin_code));
                    return;
                }
                if (altPin.getText().toString().length() != 6) {
                    dialogError(getString(R.string.enter_valid_pin_code));
                    return;
                }
                if (altCity.getText().toString().isEmpty()) {
                    dialogError(getString(R.string.enter_city_name));
                    return;
                }

                if (perFlat.getText().toString().trim().isEmpty()) {
                    dialogError(getString(R.string.enter_flat_number));
                    return;
                }
                if (perStreet.getText().toString().trim().isEmpty()) {
                    dialogError(getString(R.string.enter_street));
                    return;
                }
                if (perPin.getText().toString().isEmpty()) {
                    dialogError(getString(R.string.enter_pin_code));
                    return;
                }
                if (perPin.getText().toString().length() != 6) {
                    dialogError(getString(R.string.enter_valid_pin_code));
                    return;
                }
                if (perCity.getText().toString().trim().isEmpty()) {
                    dialogError(getString(R.string.enter_city_name));
                    return;
                }

                TeacherAddressRequest teacherAddressResponse = new TeacherAddressRequest();
                teacherAddressResponse.settHouse(altFlat.getText().toString());
                teacherAddressResponse.settStreet(altStreet.getText().toString());
                teacherAddressResponse.settLandmark(altLandmark.getText().toString());
                teacherAddressResponse.settPinCode(altPin.getText().toString());
                teacherAddressResponse.settCity(altCity.getText().toString());
                teacherAddressResponse.settState(altState.getSelectedItem().toString());
                teacherAddressResponse.setCountry("India");
                teacherAddressResponse.setHouse(perFlat.getText().toString());
                teacherAddressResponse.setStreet(perStreet.getText().toString());
                teacherAddressResponse.setLandmark(perLandmark.getText().toString());
                teacherAddressResponse.setPinCode(perPin.getText().toString());
                teacherAddressResponse.setCity(perCity.getText().toString());
                teacherAddressResponse.setState(perState.getSelectedItem().toString());
                teacherAddressResponse.settCountry("India");
                UpdateTeacherProfileRequest request = new UpdateTeacherProfileRequest();
                request.setTeacherAddressRequest(teacherAddressResponse);
                apiCallBack(getApiCommonController().updateTeacherProfile(request));
            }
        });

        skipLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().post(new EventToolbar("Identification"));
                fragmentSwitching(new TeacherProfileIdentificationFragment(response, update));
            }
        });

        return view;
    }


    @Subscribe
    public void uploadResponse(UpdateTeacherProfileResponse updateStudentProfileResponse) {
        if (Status.SUCCESS.value() == updateStudentProfileResponse.getStatus().value()) {
            EventBus.getDefault().post(new EventToolbar("Identification"));
            fragmentSwitching(new TeacherProfileIdentificationFragment(response, update));
        } else {
            dialogError(updateStudentProfileResponse.getMessage().getMessage());
        }
    }




}

