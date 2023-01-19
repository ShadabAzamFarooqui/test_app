package com.app.mschooling.enrollment;

import static com.app.mschooling.base.fragment.BaseFragment.REQUEST_CODE_PICKER;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.app.mschooling.base.activity.BaseActivity;
import com.app.mschooling.com.R;
import com.app.mschooling.enrollment.otp.activity.OtpRegisterActivity;
import com.app.mschooling.other.activity.AddressPickerActivity;
import com.app.mschooling.utils.Preferences;
import com.app.mschooling.utils.Validation;
import com.mschooling.transaction.common.api.Status;
import com.mschooling.transaction.request.register.AddRegisterRequest;
import com.mschooling.transaction.response.registration.AddRegistrationResponse;
import com.mschooling.multimediapicker.GalleryPickerActivity;
import com.mschooling.multimediapicker.MultimediaPicker;
import com.mschooling.multimediapicker.model.Image;
import com.mschooling.transaction.response.registration.SendOTPResponse;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class RegisterSchoolActivity extends BaseActivity {


    @BindView(R.id.image)
    CircleImageView image;
    @BindView(R.id.captureImage)
    RelativeLayout captureImage;
    @BindView(R.id.schoolName)
    EditText schoolName;
    @BindView(R.id.description)
    EditText description;
    @BindView(R.id.openTime)
    TextView openTime;
    @BindView(R.id.closeTime)
    TextView closeTime;
    @BindView(R.id.openTimeLayout)
    LinearLayout openTimeLayout;
    @BindView(R.id.closeTimeLayout)
    LinearLayout closeTimeLayout;
    @BindView(R.id.gst)
    EditText gst;
    @BindView(R.id.buildingNumber)
    EditText buildingNumber;
    @BindView(R.id.street)
    EditText street;
    @BindView(R.id.city)
    EditText city;
    @BindView(R.id.pinCode)
    EditText pinCode;
    @BindView(R.id.state)
    Spinner state;
    @BindView(R.id.country)
    Spinner country;
    @BindView(R.id.tick)
    ImageView tick;
    @BindView(R.id.address)
    TextView address;
    @BindView(R.id.location_icon)
    ImageView locationIcon;
    @BindView(R.id.markLocation)
    RelativeLayout markLocation;



    @BindView(R.id.principalName)
    EditText principalName;
    @BindView(R.id.principalMobile)
    EditText principalMobile;
    @BindView(R.id.principalEmail)
    EditText principalEmail;
    @BindView(R.id.tvFaceBook)
    TextView tvFaceBook;
    @BindView(R.id.facebook)
    EditText facebook;
    @BindView(R.id.tvTwitter)
    TextView tvTwitter;
    @BindView(R.id.twitter)
    EditText twitter;
    @BindView(R.id.tvInstagram)
    TextView tvInstagram;
    @BindView(R.id.instagram)
    EditText instagram;
    @BindView(R.id.tvLinkedIn)
    TextView tvLinkedIn;
    @BindView(R.id.linkedIn)
    EditText linkedIn;
    @BindView(R.id.tvQuora)
    TextView tvQuora;
    @BindView(R.id.quora)
    EditText quora;
    @BindView(R.id.submit)
    Button submit;

    ArrayList<Image> imageList = new ArrayList<>();
    private static int REQUEST_ADDRESS = 1;

    String[] latLong;

    String mobile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_school);
        ButterKnife.bind(this);
        toolbarClick(getString(R.string.setup));
        apiCallBackWithout(getApiCommonController().getGoogleApiKey());

//        if (BuildConfig.DEBUG){
//            schoolName.setText("Ghulam Akram Siddiqui Memorial Public School");
//            description.setText("Hindi/English/Urdu Medium");
//            adminName.setText("Salman Athar");
//            adminEmail.setText("gasmps@gmail.com");
//            adminMobile.setText("8268400046");
//            state.setSelection(35);
//            buildingNumber.setText("102");
//            street.setText("5");
//            city.setText("Sipah Ibrahimabad District Mau");
//            pinCode.setText("221603");
//        }

        mobile=getIntent("mobile");

        locationIcon.setOnClickListener(view -> startActivityForResult(new Intent(RegisterSchoolActivity.this, AddressPickerActivity.class), REQUEST_ADDRESS));
        markLocation.setOnClickListener(view -> startActivityForResult(new Intent(RegisterSchoolActivity.this, AddressPickerActivity.class), REQUEST_ADDRESS));

        timePicker(openTimeLayout, openTime);
        timePicker(openTime, openTime);
        timePicker(closeTimeLayout, closeTime);
        timePicker(closeTime, closeTime);

        captureImage.setOnClickListener(v -> {
//                dialogProfile();

            MultimediaPicker.create(RegisterSchoolActivity.this)
                    .folderMode(true) // set folder mode (false by default)
                    .folderTitle(getString(R.string.album)) // folder selection title
                    .imageTitle(getString(R.string.tap_to_select)) // image selection title
                    .single() // single mode
                    .setOnlyImages(true)
                    .multi() // multi mode (default mode)
                    .limit(1) // max images can be selected (999 by default)
                    .showCamera(true) // show camera or not (true by default)
                    .imageDirectory("Camera")   // captured image directory name ("Camera" folder by default)
                    .origin(imageList) // original selected images, used in multi mode
                    .start(REQUEST_CODE_PICKER);
        });


        submit.setOnClickListener(v -> {
            if (schoolName.getText().toString().trim().isEmpty()) {
                dialogError(getString(R.string.enter_school_name));
                return;
            } else if (buildingNumber.getText().toString().trim().isEmpty()) {
                dialogError(getString(R.string.enter_building_number));
                return;
            } else if (street.getText().toString().trim().isEmpty()) {
                dialogError(getString(R.string.enter_street_locality_number));
                return;
            } else if (city.getText().toString().trim().isEmpty()) {
                dialogError(getString(R.string.enter_city_number));
                return;
            } else if (pinCode.getText().toString().trim().isEmpty()) {
                dialogError(getString(R.string.enter_pin_code));
                return;
            } else if (pinCode.getText().toString().trim().length() != 6) {
                dialogError(getString(R.string.enter_valid_pin_code));
                return;
            } else if (state.getSelectedItemPosition() == 0) {
                dialogError(getString(R.string.select_state));
                return;
            }

            AddRegisterRequest request = new AddRegisterRequest();
            request.setName(schoolName.getText().toString().trim());
            request.setAbout(description.getText().toString().trim());
            request.setHouse(buildingNumber.getText().toString().trim());
            request.setStreet(street.getText().toString().trim());
            request.setCity(city.getText().toString().trim());
            request.setPinCode(pinCode.getText().toString().trim());
            request.setState(state.getSelectedItem().toString());
            request.setCountry(country.getSelectedItem().toString());
            try {
                request.setLatitude(latLong[0]);
                request.setLongitude(latLong[1]);
            } catch (Exception e) {
                e.printStackTrace();
            }


            request.setAdminName(getIntent("name"));
            request.setAdminMobile(mobile);
//            Preferences.getInstance(getApplicationContext()).setRegisterRequest(request);
            apiCallBack(getApiCommonController().sendOTP(mobile));


        });

    }


    void timePicker(View linearLayout, TextView textView) {
        linearLayout.setOnClickListener(v -> {
            final Calendar c = Calendar.getInstance();
            int mHour = c.get(Calendar.HOUR_OF_DAY);
            int mMinute = c.get(Calendar.MINUTE);
            TimePickerDialog timePickerDialog = new TimePickerDialog(RegisterSchoolActivity.this,
                    (view, hourOfDay, minute) -> {
                        String h = "" + hourOfDay;
                        String m = "" + minute;
                        if (hourOfDay < 10) {
                            h = "0" + hourOfDay;
                        }
                        if (minute < 10) {
                            m = "0" + minute;
                        }

                        textView.setText(" " + h + ":" + m);
                    }, mHour, mMinute, true);
            timePickerDialog.show();
        });

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            super.onActivityResult(requestCode, resultCode, data);
            if (resultCode == RESULT_OK) {
                if (requestCode == REQUEST_ADDRESS) {
                    String address = data.getStringExtra("address");
                    latLong = data.getStringExtra("latLong").split(",");
                    this.address.setText(R.string.location_marked_on_map);
                    tick.setImageDrawable(getResources().getDrawable(R.drawable.tick_location));
                } else if (requestCode == REQUEST_CODE_PICKER) {
                    image.setVisibility(View.VISIBLE);
                    if (resultCode == RESULT_OK) {
                        image.setVisibility(View.VISIBLE);
                        if (requestCode == REQUEST_CODE_PICKER) {
                            imageList = data.getParcelableArrayListExtra(GalleryPickerActivity.INTENT_EXTRA_SELECTED_IMAGES);
                            image.setImageURI(Uri.parse(imageList.get(0).getPath()));
                        }
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }


    @Subscribe
    public void registerResponse(SendOTPResponse response) {
        if (response.getStatus().value().equals(Status.SUCCESS.value())) {
            Intent intent=new Intent(getApplicationContext(), OtpRegisterActivity.class);
            intent.putExtra("mobile",mobile);
            startActivity(intent);
        } else {
            dialogFailed(response.getMessage().getMessage());
        }
    }


}