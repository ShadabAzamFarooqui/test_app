package com.app.mschooling.about;

import static com.app.mschooling.base.fragment.BaseFragment.REQUEST_CODE_PICKER;

import android.annotation.SuppressLint;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
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
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.app.mschooling.other.activity.AddressPickerActivity;
import com.app.mschooling.base.activity.BaseActivity;
import com.app.mschooling.com.R;
import com.app.mschooling.utils.Validation;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.mschooling.transaction.common.ContactInfo;
import com.mschooling.transaction.common.SchoolInfo;
import com.mschooling.transaction.common.SocialMedia;
import com.mschooling.transaction.common.api.Common;
import com.mschooling.transaction.common.api.Status;
import com.mschooling.transaction.request.school.info.UpdateSchoolInfoRequest;
import com.mschooling.transaction.response.resource.AddResourceResponse;
import com.mschooling.transaction.response.school.info.GetSchoolInfoResponse;
import com.mschooling.transaction.response.school.info.UpdateSchoolInfoResponse;
import com.mschooling.multimediapicker.GalleryPickerActivity;
import com.mschooling.multimediapicker.MultimediaPicker;
import com.mschooling.multimediapicker.model.Image;

import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class UpdateSchoolActivity extends BaseActivity {

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
    @BindView(R.id.openTimeLayout)
    LinearLayout openTimeLayout;
    @BindView(R.id.closeTime)
    TextView closeTime;
    @BindView(R.id.closeTimeLayout)
    LinearLayout closeTimeLayout;

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
    @BindView(R.id.schoolContact)
    EditText schoolContact;
    @BindView(R.id.schoolEmail)
    EditText schoolEmail;
    @BindView(R.id.adminName)
    EditText adminName;
    @BindView(R.id.adminMobile)
    EditText adminMobile;
    @BindView(R.id.adminEmail)
    EditText adminEmail;
    @BindView(R.id.principalName)
    EditText principalName;
    @BindView(R.id.principalMobile)
    EditText principalMobile;
    @BindView(R.id.principalEmail)
    EditText principalEmail;
    @BindView(R.id.facebook)
    EditText facebook;
    @BindView(R.id.twitter)
    EditText twitter;

    @BindView(R.id.linkedIn)
    EditText linkedIn;

    @BindView(R.id.shimmerFrameLayout)
    ShimmerFrameLayout shimmerFrameLayout;

    @BindView(R.id.submit)
    Button submit;

    private static int REQUEST_ADDRESS = 1;
    String[] latLong = new String[2];
    ArrayList<Image> imageList = new ArrayList<>();

    String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_school);
        ButterKnife.bind(this);
        toolbarClick(getString(R.string.school_details));
        apiCallBackWithout(getApiCommonController().getGoogleApiKey());
        apiCallBack(getApiCommonController().getSchoolInfo());

        markLocation.setVisibility(View.GONE);

        locationIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(UpdateSchoolActivity.this, AddressPickerActivity.class), REQUEST_ADDRESS);
            }
        });
        markLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(UpdateSchoolActivity.this, AddressPickerActivity.class), REQUEST_ADDRESS);
            }
        });

        captureImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                dialogProfile();

                MultimediaPicker.create(UpdateSchoolActivity.this)
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
            }
        });

        timePicker(openTimeLayout,openTime);
        timePicker(closeTimeLayout,closeTime);


        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (schoolName.getText().toString().trim().trim().isEmpty()) {
                    dialogError(getString(R.string.enter_school_name));
                    return;
                } else if (description.getText().toString().trim().trim().isEmpty()) {
                    dialogError(getString(R.string.enter_school_description));
                    return;
                } /*else if (openTime.getText().toString().trim().trim().isEmpty()) {
                    dialogError(getString(R.string.select_opening_time));
                    return;
                } else if (closeTime.getText().toString().trim().trim().isEmpty()) {
                    dialogError(getString(R.string.select_closing_time));
                    return;
                }*/ else if (buildingNumber.getText().toString().trim().trim().isEmpty()) {
                    dialogError(getString(R.string.enter_building_number));
                    return;
                } else if (street.getText().toString().trim().trim().isEmpty()) {
                    dialogError(getString(R.string.enter_street_locality_number));
                    return;
                } else if (city.getText().toString().trim().trim().isEmpty()) {
                    dialogError(getString(R.string.enter_city_number));
                    return;
                } else if (pinCode.getText().toString().trim().trim().isEmpty()) {
                    dialogError(getString(R.string.enter_pin_code));
                    return;
                } else if (pinCode.getText().toString().trim().length() != 6) {
                    dialogError(getString(R.string.enter_valid_pin_code));
                    return;
                } else if (state.getSelectedItemPosition() == 0) {
                    dialogError(getString(R.string.select_state));
                    return;
                } /*else if (address.getText().toString().trim().isEmpty()) {
                    dialogError(getString(R.string.select_geo_location));
                    return;
                } */ else if (schoolContact.getText().toString().trim().isEmpty()) {
                    dialogError(getString(R.string.enter_school_contact_number));
                    return;
                } else if (schoolEmail.getText().toString().trim().isEmpty()) {
                    dialogError(getString(R.string.enter_school_email));
                    return;
                } else if (!Validation.isEmailValid(schoolEmail.getText().toString().trim())) {
                    dialogError(getString(R.string.enter_valid_school_email));
                    return;
                } else if (adminName.getText().toString().trim().isEmpty()) {
                    dialogError(getString(R.string.enter_admin_name));
                    return;
                } else if (adminMobile.getText().toString().trim().isEmpty()) {
                    dialogError(getString(R.string.enter_admin_mobile));
                    return;
                } else if (adminMobile.getText().toString().trim().length() != 10) {
                    dialogError(getString(R.string.enter_valid_admin_mobile));
                    return;
                } /*else if (principalName.getText().toString().trim().isEmpty()) {
                    dialogError(getString(R.string.enter_principle_name));
                    return;
                } else if (principalMobile.getText().toString().trim().isEmpty()) {
                    dialogError(getString(R.string.enter_principle_mobile));
                    return;
                } else if (principalMobile.getText().toString().trim().length() != 10) {
                    dialogError(getString(R.string.enter_valid_principle_mobile));
                    return;
                }*/

                if (!adminEmail.getText().toString().trim().trim().isEmpty()) {
                    if (!Validation.isEmailValid(adminEmail.getText().toString().trim())) {
                        dialogError(getString(R.string.enter_valid_admin_email));
                        return;
                    }
                }

                UpdateSchoolInfoRequest request = new UpdateSchoolInfoRequest();
                request.setName(schoolName.getText().toString());
                request.setAbout(description.getText().toString());
                request.setOpenTime(openTime.getText().toString());
                request.setCloseTime(closeTime.getText().toString());
                request.setHouse(buildingNumber.getText().toString());
                request.setStreet(street.getText().toString());
                request.setCity(city.getText().toString());
                request.setPinCode(pinCode.getText().toString());
                request.setState(state.getSelectedItem().toString());
                request.setCountry(country.getSelectedItem().toString());
                request.setLatitude(latLong[0]);
                request.setLongitude(latLong[1]);

                ContactInfo schoolContact = new ContactInfo();
                schoolContact.setContactType(Common.ContactType.SCHOOL);
                schoolContact.setName(schoolName.getText().toString());
                schoolContact.setMobile(UpdateSchoolActivity.this.schoolContact.getText().toString());
                schoolContact.setEmail(schoolEmail.getText().toString());
                request.setSchoolContactInfo(schoolContact);

                ContactInfo adminContact = new ContactInfo();
                adminContact.setContactType(Common.ContactType.ADMIN);
                adminContact.setName(adminName.getText().toString());
                adminContact.setEmail(adminEmail.getText().toString());
                adminContact.setMobile(adminMobile.getText().toString());
                request.setAdminContactInfo(adminContact);

                ContactInfo principalContact = new ContactInfo();
                principalContact.setContactType(Common.ContactType.PRINCIPAL);
                principalContact.setName(principalName.getText().toString());
                principalContact.setEmail(principalEmail.getText().toString());
                principalContact.setMobile(principalMobile.getText().toString());
                request.setPrincipalContactInfo(principalContact);

                SocialMedia facebookSocial = new SocialMedia();
                facebookSocial.setSocialMediaType(Common.SocialMediaType.FACEBOOK);
                facebookSocial.setUrl(facebook.getText().toString());
                request.setFacebookSocialMedia(facebookSocial);


                SocialMedia twiterSocial = new SocialMedia();
                twiterSocial.setSocialMediaType(Common.SocialMediaType.TWITTER);
                twiterSocial.setUrl(twitter.getText().toString());
                request.setTwitterSocialMedia(twiterSocial);


                SocialMedia linkedInSocial = new SocialMedia();
                linkedInSocial.setSocialMediaType(Common.SocialMediaType.LINKEDIN);
                linkedInSocial.setUrl(linkedIn.getText().toString());
                request.setLinkedInSocialMedia(linkedInSocial);

                request.setUrl(url);


                apiCallBack(getApiCommonController().updateSchool(request));
            }
        });
    }


    @Subscribe
    public void getResponse(GetSchoolInfoResponse response) {
        if (Status.SUCCESS.value().equals(response.getStatus().value())) {

            SchoolInfo schoolInfo = response.getSchoolInfo();
            schoolName.setText(schoolInfo.getName());
            openTime.setText(schoolInfo.getOpenTime());
            closeTime.setText(schoolInfo.getCloseTime());
//            qrCode.setText(schoolInfo.getQrCode());
//            schoolCode.setText("MS4U0" + schoolInfo.getCode());
//            address.setText(schoolInfo.getHouse() + " " + schoolInfo.getStreet() + " " + schoolInfo.getCity() /*+ " " + add.getLandmark()*/ + "\n" + schoolInfo.getState() + " " + schoolInfo.getCountry() + "\n" + schoolInfo.getPinCode());
            schoolContact.setText(schoolInfo.getSchoolContactInfo().getMobile());
            schoolEmail.setText(schoolInfo.getSchoolContactInfo().getEmail());
            description.setText(schoolInfo.getAbout());

            buildingNumber.setText(schoolInfo.getHouse());
            street.setText(schoolInfo.getStreet());
            city.setText(schoolInfo.getCity());
            pinCode.setText(schoolInfo.getPinCode());


            adminName.setText(schoolInfo.getAdminContactInfo().getName());
            adminEmail.setText(schoolInfo.getAdminContactInfo().getEmail());
            adminMobile.setText(schoolInfo.getAdminContactInfo().getMobile());


            principalName.setText(schoolInfo.getPrincipalContactInfo().getName());
            principalEmail.setText(schoolInfo.getPrincipalContactInfo().getEmail());
            principalMobile.setText(schoolInfo.getPrincipalContactInfo().getMobile());
            facebook.setText(schoolInfo.getFacebookSocialMedia().getUrl());
            twitter.setText(schoolInfo.getTwitterSocialMedia().getUrl());
            linkedIn.setText(schoolInfo.getLinkedInSocialMedia().getUrl());


            state.setSelection(getPosition(R.array.state, schoolInfo.getState()));

            if (schoolInfo.getLatitude() != null) {
                this.address.setText(R.string.location_marked_on_map);
                tick.setImageDrawable(getResources().getDrawable(R.drawable.tick_location));
            }


            url = response.getSchoolInfo().getUrl();
            Glide.with(this)
                    .load(response.getSchoolInfo().getUrl())
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            shimmerFrameLayout.setVisibility(View.GONE);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            shimmerFrameLayout.setVisibility(View.GONE);
                            return false;
                        }
                    })
                    .into(image);

            latLong[0] = schoolInfo.getLatitude();
            latLong[1] = schoolInfo.getLongitude();

        } else {
            dialogFailed(response.getMessage().getMessage());
        }

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
                    tick.setImageDrawable(getResources().getDrawable(R.drawable.location));
                } else if (requestCode == REQUEST_CODE_PICKER) {
                    image.setVisibility(View.VISIBLE);
                    if (resultCode == RESULT_OK) {
                        image.setVisibility(View.VISIBLE);
                        if (requestCode == REQUEST_CODE_PICKER) {
                            imageList = data.getParcelableArrayListExtra(GalleryPickerActivity.INTENT_EXTRA_SELECTED_IMAGES);
                            image.setImageURI(Uri.parse(imageList.get(0).getPath()));
                            multipart(Common.DocType.LOGO,imageList.get(0).getPath());
                        }
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    @Subscribe
    public void updateResponse(@NonNull UpdateSchoolInfoResponse response) {
        if (Status.SUCCESS.value() == response.getStatus().value()) {
            dialogSuccessFinish(response.getMessage().getMessage());
        } else {
            dialogError(response.getMessage().getMessage());
        }
    }


    @Subscribe
    public void getResourceResponse(@NonNull AddResourceResponse r) {
        if (Status.SUCCESS.value() == r.getStatus().value()) {
            url=r.getUrl();
        } else {
            dialogError(r.getMessage().getMessage());
        }
    }




    void timePicker(@NonNull View linearLayout, TextView textView) {
        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendar = Calendar.getInstance();
                int mHour = calendar.get(Calendar.HOUR_OF_DAY);
                int mMinute = calendar.get(Calendar.MINUTE);
                TimePickerDialog timePickerDialog = new TimePickerDialog(UpdateSchoolActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {

                            @SuppressLint("SetTextI18n")
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                String h = "" + hourOfDay;
                                String m = "" + minute;
                                if (hourOfDay < 10) {
                                    h = "0" + hourOfDay;
                                }
                                if (minute < 10) {
                                    m = "0" + minute;
                                }

                                textView.setText(" " + h + ":" + m);
                            }
                        }, mHour, mMinute, true);
                timePickerDialog.show();
            }
        });

    }

    public  void multipart(Common.DocType docType, String path) {
        Uri compressUri = compressImage(path);
        File file = new File(compressUri.toString());
        MultipartBody.Part filePart = MultipartBody.Part.createFormData("file", file.getName(),
                RequestBody.create(MediaType.parse("application/image"), file));
        apiCallBack(getApiCommonController().uploadResource(docType, filePart));
    }

}