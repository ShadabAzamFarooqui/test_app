package com.app.mschooling.add_student;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.app.mschooling.base.activity.BaseActivity;
import com.app.mschooling.com.R;
import com.mschooling.transaction.common.api.Status;
import com.mschooling.transaction.common.student.request.StudentAddressRequest;
import com.mschooling.transaction.request.student.UpdateStudentRequest;
import com.mschooling.transaction.response.student.UpdateStudentResponse;

import org.greenrobot.eventbus.Subscribe;

public class AddStudent3 extends BaseActivity {

//    views

    EditText altFlat;
    EditText altStreet;
    EditText altLandmark;
    EditText altPin;
    EditText altCity;
    Spinner altState;
    EditText perFlat;
    EditText perStreet;
    EditText perLandmark;
    EditText perPin;
    EditText perCity;
    Spinner perState;
    Button submit;
    CheckBox markAs;
    LinearLayout skipLayout;

    UpdateStudentRequest mRequest;
    public static AddStudent3 context;


    void init() {
        submit=findViewById(R.id.submit);
        altFlat=findViewById(R.id.altFlat);
        altStreet=findViewById(R.id.altStreet);
        altLandmark=findViewById(R.id.altLandmark);
        altPin=findViewById(R.id.altPin);
        altCity=findViewById(R.id.altCity);
        altState=findViewById(R.id.altState);
        perFlat=findViewById(R.id.perFlat);
        perStreet=findViewById(R.id.perStreet);
        perLandmark=findViewById(R.id.perLandmark);
        perPin=findViewById(R.id.perPin);
        perCity=findViewById(R.id.perCity);
        perState=findViewById(R.id.perState);
        markAs=findViewById(R.id.markAs);
        skipLayout=findViewById(R.id.skipLayout);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_student3);
        toolbarClick(getString(R.string.tool_address_detail));
        init();
        context=this;
        mRequest=new UpdateStudentRequest();
        mRequest.getStudentBasicRequest().setEnrollmentId(getIntent().getStringExtra("id"));

        skipLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(),AddStudent5.class);
                intent.putExtra("id", getIntent().getStringExtra("id"));
                startActivity(intent);
            }
        });

        markAs.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    perFlat.setText(altFlat.getText().toString());
                    perStreet.setText(altStreet.getText().toString());
                    perLandmark.setText(altLandmark.getText().toString());
                    perPin.setText(altPin.getText().toString());
                    perCity.setText(altCity.getText().toString());
                    perState.setSelection(altState.getSelectedItemPosition());


                    perFlat.setError(null);
                    perStreet.setError(null);
                    perLandmark.setError(null);
                    perPin.setError(null);
                    perCity.setError(null);
                }else {
                    perFlat.setText("");
                    perStreet.setText("");
                    perLandmark.setText("");
                    perPin.setText("");
                    perCity.setText("");
                    perState.setSelection(0);




                }
            }
        });

        textWatcher(altFlat,perFlat);
        textWatcher(altStreet,perStreet);
        textWatcher(altLandmark,perLandmark);
        textWatcher(altPin,perPin);
        textWatcher(altCity,perCity);

        altState.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (markAs.isChecked()){
                    perState.setSelection(position);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

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

                StudentAddressRequest address=new StudentAddressRequest();
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



    }



    void textWatcher(EditText alt,EditText per){
        alt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (markAs.isChecked()){
                    per.setText(alt.getText().toString());
                    per.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }


    @Subscribe
    public void updateStudent(UpdateStudentResponse response) {
        if (response.getStatus().value().equals(Status.SUCCESS.value())) {
            Intent intent = new Intent(getApplicationContext(), AddStudent5.class);
            intent.putExtra("id", getIntent().getStringExtra("id"));
            intent.putExtra("mobile", getIntent().getStringExtra("mobile"));
            startActivity(intent);
        }else {
            dialogError(response.getMessage().getMessage());
        }
    }
}

