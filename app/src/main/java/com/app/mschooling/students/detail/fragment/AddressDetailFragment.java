package com.app.mschooling.students.detail.fragment;


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
import com.app.mschooling.utils.AppUser;
import com.app.mschooling.utils.Helper;
import com.mschooling.transaction.common.api.Status;
import com.mschooling.transaction.common.student.request.StudentAddressRequest;
import com.mschooling.transaction.common.student.response.StudentAddressResponse;
import com.mschooling.transaction.request.student.UpdateStudentRequest;
import com.mschooling.transaction.response.student.GetStudentDetailResponse;
import com.mschooling.transaction.response.student.UpdateStudentResponse;

import org.greenrobot.eventbus.Subscribe;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AddressDetailFragment extends BaseFragment {

    UpdateStudentRequest mRequest = new UpdateStudentRequest();
    StudentAddressResponse response;
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

    String enrollmentId;
    boolean update ;
    public AddressDetailFragment(GetStudentDetailResponse response,boolean update) {
        this.response = response.getStudentDetailResponse().getStudentAddressResponse();
        this.enrollmentId = response.getStudentDetailResponse().getStudentBasicResponse().getEnrollmentId();
        this.update=update;
    }

    @Nullable
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.address_details, container, false);
        ButterKnife.bind(this, view);
        Helper.enableDisableView(mainLayout, submit, update);
        mRequest.getStudentBasicRequest().setEnrollmentId(enrollmentId);


        altFlat.setText(response.gettHouse());
        altStreet.setText(response.gettStreet());
        altLandmark.setText(response.gettLandmark());
        altPin.setText(response.gettPinCode());
        altCity.setText(response.gettCity());
        altState.setSelection(getPosition(R.array.states, response.gettState()));

        perFlat.setText(response.getHouse());
        perStreet.setText(response.getStreet());
        perLandmark.setText(response.getLandmark());
        perPin.setText(response.getPinCode());
        perCity.setText(response.getCity());
        perState.setSelection(getPosition(R.array.states, response.getState()));
//        for (int i=0;i<)
//        markAs.setChecked();
//        altState.setText(response.gettLandmark());


        textChangeListener(altFlat,perFlat,markAs);
        textChangeListener(altStreet,perStreet,markAs);
        textChangeListener(altLandmark,perLandmark,markAs);
        textChangeListener(altPin,perPin,markAs);
        textChangeListener(altCity,perCity,markAs);
        spinnerListener(altState,perState,markAs);

        try {
            if (response.getHouse().equals(response.gettHouse())
                    && response.getStreet().equals(response.gettStreet())
                    && response.getLandmark().equals(response.gettLandmark())
                    && response.getPinCode().equals(response.gettPinCode())
                    && response.gettCity().equals(response.getCity())
                    && response.gettState().equals(response.getState())) {
                markAs.setChecked(true);
            }
        }catch (Exception e){
            e.printStackTrace();
        }



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
                /*boolean validator=true;
                if (altFlat.getText().toString().trim().isEmpty()){
                    validator=false;
                    altFlat.setError("Enter Flat Number");
                }if (altStreet.getText().toString().isEmpty()){
                    validator=false;
                    altStreet.setError("Enter Street");
                }if (altPin.getText().toString().trim().isEmpty()){
                    validator=false;
                    altPin.setError("Enter pin code");
                }if (altPin.getText().toString().length()!=6){
                    validator=false;
                    altPin.setError("Enter valid pin code");
                }
                if (altCity.getText().toString().isEmpty()) {
                    validator = false;
                    altCity.setError("Enter city name");
                }

                if (perFlat.getText().toString().trim().isEmpty()){
                    validator=false;
                    perFlat.setError("Enter Flat Number");
                }if (perStreet.getText().toString().trim().isEmpty()){
                    validator=false;
                    perStreet.setError("Enter Street");
                }if (perPin.getText().toString().isEmpty()){
                    validator=false;
                    perPin.setError("Enter pin code");
                }if (perPin.getText().toString().length()!=6){
                    validator=false;
                    perPin.setError("Enter valid pin code");
                }
                if (perCity.getText().toString().trim().isEmpty()) {
                    validator = false;
                    perCity.setError("Enter city name");
                }
                if (!validator){
                    alerter();
                    return;
                }*/

                StudentAddressRequest address = new StudentAddressRequest();
                address.settHouse(altFlat.getText().toString());
                address.settStreet(altStreet.getText().toString());
                address.settLandmark(altLandmark.getText().toString());
                address.settPinCode(altPin.getText().toString());
                address.settCity(altCity.getText().toString());
                address.settState(altState.getSelectedItem().toString());
                address.setCountry("India");
                address.setHouse(perFlat.getText().toString());
                address.setStreet(perStreet.getText().toString());
                address.setLandmark(perLandmark.getText().toString());
                address.setPinCode(perPin.getText().toString());
                address.setCity(perCity.getText().toString());
                address.setState(perState.getSelectedItem().toString());
                address.settCountry("India");
                mRequest.setStudentAddressRequest(address);
                apiCallBack(getApiCommonController().updateStudent(mRequest));
            }
        });


        return view;
    }

    @Subscribe
    public void updateStudent(UpdateStudentResponse response) {
        if (response.getStatus().value() == Status.SUCCESS.value()) {
            dialogSuccess(response.getMessage().getMessage());
        } else {
            dialogFailed(response.getMessage().getMessage());
        }
    }





}

