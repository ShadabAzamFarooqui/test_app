package com.app.mschooling.homework.activity;

import static com.app.mschooling.base.fragment.BaseFragment.REQUEST_CODE_PICKER;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.mschooling.homework.adapter.UploadHomeWorkAdapter;
import com.app.mschooling.base.activity.BaseActivity;
import com.app.mschooling.com.R;
import com.app.mschooling.utils.Helper;
import com.app.mschooling.utils.ParameterConstant;
import com.app.mschooling.utils.progress_dialog.MyProgressDialogUploadMultiple;
import com.mschooling.transaction.common.Firebase;
import com.mschooling.transaction.common.api.Common;
import com.mschooling.transaction.common.api.Status;
import com.mschooling.transaction.request.homework.AddWorksheetRequest;
import com.mschooling.transaction.response.homework.AddWorksheetResponse;
import com.mschooling.transaction.response.resource.AddResourceResponse;
import com.mschooling.multimediapicker.GalleryPickerActivity;
import com.mschooling.multimediapicker.MultimediaPicker;
import com.mschooling.multimediapicker.model.Image;

import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class AddHomeWorkActivity extends BaseActivity {
    @BindView(R.id.classSubjectName)
    EditText classSubjectName;
    @BindView(R.id.tittle)
    EditText tittle;
    @BindView(R.id.content)
    EditText content;
    @BindView(R.id.dateLayout)
    RelativeLayout dateLayout;
    @BindView(R.id.date)
    TextView date;
    @BindView(R.id.insertImage)
    LinearLayout insertImage;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.submit)
    Button submit;

    List<Firebase> firebaseList;
    UploadHomeWorkAdapter adapter;
    int uploading;
    int size;
    int limit = 5;

    private ArrayList<Image> imageList = new ArrayList<>();



    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_home_work);
        ButterKnife.bind(this);
        toolbarClick(getString(R.string.homework));
        classSubjectName.setText(getIntent().getStringExtra("className") + "  (" + getIntent().getStringExtra("sectionName") + ")" + "  - (" + getIntent().getStringExtra("subjectName") + ")");
        firebaseList = new ArrayList<>();

        insertImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MultimediaPicker.create(AddHomeWorkActivity.this)
                        .folderMode(true) // set folder mode (false by default)
                        .folderTitle(getString(R.string.album)) // folder selection title
                        .imageTitle(getString(R.string.tap_to_select)) // image selection title
                        .single() // single mode
                        .setOnlyImages(true)
                        .multi() // multi mode (default mode)
                        .limit(limit - imageList.size()) // max images can be selected (999 by default)
                        .showCamera(true) // show camera or not (true by default)
                        .imageDirectory("Camera")   // captured image directory name ("Camera" folder by default)
                        .origin(imageList) // original selected images, used in multi mode
                        .start(REQUEST_CODE_PICKER);
            }
        });

        date.setText(Helper.getCurrentDate());
        datePicker(date);
        datePicker(dateLayout);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tittle.getText().toString().trim().isEmpty()) {
                    dialogError(getString(R.string.enter_title));
                    return;
                } if (content.getText().toString().trim().isEmpty()) {
                    dialogError(getString(R.string.enter_content));
                    return;
                }
                if (imageList.size() != 0) {
                    uploading = 0;
                    size = imageList.size();
                    MyProgressDialogUploadMultiple.getInstance(AddHomeWorkActivity.this).show();
                    uploadImageUri("application/image", imageList.get(0).getPath());
                } else {
                    addHomeWorkApi();
                }
            }
        });

    }

    void addHomeWorkApi(){
        AddWorksheetRequest request = new AddWorksheetRequest();
        request.setClassId(getIntent().getStringExtra("classId"));
        request.setSectionId(getIntent().getStringExtra("sectionId"));
        request.setSubjectId(getIntent().getStringExtra("subjectId"));
        request.setDate(date.getText().toString());
        request.setTitle(tittle.getText().toString());
        request.setContent(content.getText().toString());
        request.setFirebases(firebaseList);
        MyProgressDialogUploadMultiple.setDismiss();
        apiCallBack(getApiCommonController().addHomeWork(ParameterConstant.getRole(getApplicationContext()),request));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            imageList.addAll(data.getParcelableArrayListExtra(GalleryPickerActivity.INTENT_EXTRA_SELECTED_IMAGES));
            if (imageList.size() > limit) {
                imageList.subList(limit, imageList.size()).clear();
            }

            if (imageList.size() == limit) {
                insertImage.setVisibility(View.GONE);
            }


            recyclerView.setVisibility(View.VISIBLE);
            recyclerView.setHasFixedSize(true);
            recyclerView.setFocusable(false);
            recyclerView.setNestedScrollingEnabled(false);
            recyclerView.setLayoutManager(new GridLayoutManager(AddHomeWorkActivity.this, 3));
            adapter = new UploadHomeWorkAdapter(AddHomeWorkActivity.this, imageList, insertImage);
            recyclerView.setAdapter(adapter);

        } catch (Exception e) {
//            dialogFailed(getString(R.string.error_msg_something_went_wrong));
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    @Subscribe
    public void addHomeWork(AddWorksheetResponse response) {
        if (response.getStatus().value().equals(Status.SUCCESS.value())) {
            firebaseList.clear();
            tittle.setText("");
            content.setText("");
            dialogSuccessFinish(response.getMessage().getMessage());
            adapter.notifyDataSetChanged();
        } else {
            dialogError(response.getMessage().getMessage());
        }
    }


    @Subscribe
    public void getResourceResponse(AddResourceResponse r) {
        if (Status.SUCCESS.value() == r.getStatus().value()) {
            Firebase firebase = new Firebase();
            firebase.setUrl(r.getUrl().toString());
            firebase.setFirebaseType(Common.FirebaseType.IMAGE);
            firebaseList.add(firebase);
            imageList.remove(0);
            if (imageList.size() > 0) {
                uploadImageUri("application/image", imageList.get(0).getPath());
            } else {
                addHomeWorkApi();
            }
        } else {
            dialogError(r.getMessage().getMessage());
        }
    }


    private void uploadImageUri(String contentType, String imagePath) {
        uploading++;
        Uri compressUri;
        try {
            compressUri = compressImage(imagePath);
        } catch (Exception e) {
            compressUri = Uri.parse(imagePath);
        }

        File file = new File(compressUri.toString());
        MultipartBody.Part filePart = MultipartBody.Part.createFormData("file", file.getName(), RequestBody.create(MediaType.parse(contentType), file));
        apiCallBackWithout(getApiCommonController().uploadResourceAdmin(Common.DocType.HOMEWORK, null, filePart));
        MyProgressDialogUploadMultiple.setCount("" + uploading, "" + size);
    }


    @Subscribe
    public void timeOut(String msg) {
        MyProgressDialogUploadMultiple.setDismiss();
        uploading = 0;
        dialogFailed(msg);
    }

    void datePicker(View view) {
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                DatePickerDialog datePickerDialog = new DatePickerDialog(AddHomeWorkActivity.this, new DatePickerDialog.OnDateSetListener() {

                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(year, monthOfYear, dayOfMonth);
                        String d = new SimpleDateFormat(ParameterConstant.getDateFormat() , Locale.US).format(calendar.getTime());
                        date.setText(d);
                    }

                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
                datePickerDialog.show();
            }
        });

    }


}
