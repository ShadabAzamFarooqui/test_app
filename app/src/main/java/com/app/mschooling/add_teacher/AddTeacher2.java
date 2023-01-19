package com.app.mschooling.add_teacher;

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
import com.app.mschooling.unused.AddTeacher3;
import com.mschooling.transaction.common.api.Status;
import com.mschooling.transaction.common.teacher.request.TeacherAddressRequest;
import com.mschooling.transaction.request.teacher.UpdateTeacherRequest;
import com.mschooling.transaction.response.teacher.UpdateTeacherResponse;

import org.greenrobot.eventbus.Subscribe;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AddTeacher2 extends BaseActivity {

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
    @BindView(R.id.skipLayout)
    LinearLayout skipLayout;
    @BindView(R.id.submit)
    Button submit;

    UpdateTeacherRequest mRequest;
    public static AddTeacher2 context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_teacher2);
        ButterKnife.bind(this);
        context=this;
        toolbarClick(getString(R.string.tool_address_detail));

        mRequest=new UpdateTeacherRequest();
        mRequest.getTeacherBasicRequest().setEnrollmentId(getIntent().getStringExtra("id"));


        skipLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AddTeacher4.class);
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

                TeacherAddressRequest address=new TeacherAddressRequest();
                address.settHouse(altFlat.getText().toString());
                address.settStreet(altStreet.getText().toString());
                address.settLandmark(altLandmark.getText().toString());
                address.settPinCode(altPin.getText().toString());
                address.settCity(altCity.getText().toString());
                address.settState(altState.getSelectedItem().toString());
                address.settCountry("India");

                address.setHouse(perFlat.getText().toString());
                address.setStreet(perStreet.getText().toString());
                address.setLandmark(perLandmark.getText().toString());
                address.setPinCode(perPin.getText().toString());
                address.setCity(perCity.getText().toString());
                address.setState(perState.getSelectedItem().toString());
                address.setCountry("India");
                mRequest.setTeacherAddressRequest(address);
                apiCallBack(getApiCommonController().updateTeacher(mRequest));
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
    public void updateTeacher(UpdateTeacherResponse response) {
        if (response.getStatus().value()== Status.SUCCESS.value()) {
            Intent intent = new Intent(getApplicationContext(), AddTeacher3.class);
            intent.putExtra("id", getIntent().getStringExtra("id"));
            intent.putExtra("mobile", getIntent().getStringExtra("mobile"));
            startActivity(intent);
        }else {
            dialogError(response.getMessage().getMessage());
        }
    }

}
