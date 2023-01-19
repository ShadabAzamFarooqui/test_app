package com.app.mschooling.admin.fragment;


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
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.app.mschooling.base.fragment.BaseFragment;
import com.app.mschooling.com.R;
import com.app.mschooling.utils.Helper;
import com.mschooling.transaction.common.admin.request.AdminAddressRequest;
import com.mschooling.transaction.common.admin.response.AdminAddressResponse;
import com.mschooling.transaction.common.api.Status;
import com.mschooling.transaction.request.profile.UpdateAdminProfileRequest;
import com.mschooling.transaction.response.profile.GetAdminProfileResponse;
import com.mschooling.transaction.response.profile.UpdateAdminProfileResponse;

import org.greenrobot.eventbus.Subscribe;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AdminProfileAddressFragment extends BaseFragment {


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
    @BindView(R.id.alternateAddress)
    TextView alternateAddress;
    @BindView(R.id.permanentAddress)
    TextView permanentAddress;
    boolean update;

    GetAdminProfileResponse response;

    public AdminProfileAddressFragment(GetAdminProfileResponse response, boolean update) {
        if (response.getAdminAddressResponse() == null) {
            response.setAdminAddressResponse(new AdminAddressResponse());
        }
        this.update = update;
        this.response = response;
    }

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.admin_profile_address, container, false);
        ButterKnife.bind(this, view);
        Helper.enableDisableView(mainLayout, submit, update);
        AdminAddressResponse studentAddressResponse = response.getAdminAddressResponse();
        altFlat.setText(studentAddressResponse.gettHouse());
        altStreet.setText(studentAddressResponse.gettStreet());
        altLandmark.setText(studentAddressResponse.gettLandmark());
        altPin.setText(studentAddressResponse.gettPinCode());
        altCity.setText(studentAddressResponse.gettCity());
        altState.setSelection(getPosition(R.array.states, studentAddressResponse.gettState()));

        perFlat.setText(studentAddressResponse.getHouse());
        perStreet.setText(studentAddressResponse.getStreet());
        perLandmark.setText(studentAddressResponse.getLandmark());
        perPin.setText(studentAddressResponse.getPinCode());
        perCity.setText(studentAddressResponse.getCity());
        perState.setSelection(getPosition(R.array.states, studentAddressResponse.getState()));

        Helper.setTextGradient(getContext(),alternateAddress);
        Helper.setTextGradient(getContext(),permanentAddress);

        textChangeListener(altFlat, perFlat, markAs);
        textChangeListener(altStreet, perStreet, markAs);
        textChangeListener(altLandmark, perLandmark, markAs);
        textChangeListener(altPin, perPin, markAs);
        textChangeListener(altCity, perCity, markAs);
        spinnerListener(altState, perState, markAs);

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


//                if (altFlat.getText().toString().trim().isEmpty()) {
//                    dialogError("Enter Flat Number");
//                    return;
//                }
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

//                if (perFlat.getText().toString().trim().isEmpty()) {
//                    dialogError("Enter Flat Number");
//                    return;
//                }
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

                AdminAddressRequest studentAddressResponse = new AdminAddressRequest();
                studentAddressResponse.settHouse(altFlat.getText().toString());
                studentAddressResponse.settStreet(altStreet.getText().toString());
                studentAddressResponse.settLandmark(altLandmark.getText().toString());
                studentAddressResponse.settPinCode(altPin.getText().toString());
                studentAddressResponse.settCity(altCity.getText().toString());
                studentAddressResponse.settState(altState.getSelectedItem().toString());
                studentAddressResponse.setCountry("India");
                studentAddressResponse.setHouse(perFlat.getText().toString());
                studentAddressResponse.setStreet(perStreet.getText().toString());
                studentAddressResponse.setLandmark(perLandmark.getText().toString());
                studentAddressResponse.setPinCode(perPin.getText().toString());
                studentAddressResponse.setCity(perCity.getText().toString());
                studentAddressResponse.setState(perState.getSelectedItem().toString());
                studentAddressResponse.settCountry("India");
                UpdateAdminProfileRequest request = new UpdateAdminProfileRequest();
                request.setAdminAddressRequest(studentAddressResponse);
                apiCallBack(getApiCommonController().updateAdminProfile(request));
            }
        });

        return view;
    }


    @Subscribe
    public void update(UpdateAdminProfileResponse response) {
        if (Status.SUCCESS.value() == response.getStatus().value()) {
            dialogSuccess(response.getMessage().getMessage());
        } else {
            dialogError(response.getMessage().getMessage());
        }
    }


}

