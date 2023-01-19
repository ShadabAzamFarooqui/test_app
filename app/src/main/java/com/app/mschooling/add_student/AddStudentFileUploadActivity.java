package com.app.mschooling.add_student;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.app.mschooling.base.activity.BaseActivity;
import com.app.mschooling.com.R;
import com.app.mschooling.network.response.ServerResponse;
import com.app.mschooling.utils.FilePath;
import com.app.mschooling.utils.Helper;

import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;


public class AddStudentFileUploadActivity extends BaseActivity {


    LinearLayout mainLayout;
    LinearLayout excel;
    LinearLayout csv;
    LinearLayout attachExcel;
    Button submit;
    TextView attachedFile;
    ProgressDialog progressDialog;
    String filePath;
    boolean isExcel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_fetch_student);
        toolbarClick(getString(R.string.tool_add_students));
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        submit = findViewById(R.id.submit);
        mainLayout = findViewById(R.id.mainLayout);
        excel = findViewById(R.id.excel);
        csv = findViewById(R.id.csv);
        attachExcel = findViewById(R.id.attachExcel);
        attachedFile = findViewById(R.id.attachedFile);

        progressDialog = new ProgressDialog(getApplicationContext());
        progressDialog.setMessage(getString(R.string.uploading));

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (filePath == null) {
                    dialogError(getString(R.string.please_select_file));
                    return;
                } else {
                    File file = new File(filePath);
                    MultipartBody.Part filePart = MultipartBody.Part.createFormData("file", file.getName(),
                            RequestBody.create(MediaType.parse("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"), file));
                    apiCallBack(getApiCommonController().uploadFile( "xlsx",filePart));
                }
            }
        });

        attachExcel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String[] mimeTypes = {"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"};
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    intent.setType(mimeTypes.length == 1 ? mimeTypes[0] : "*/*");
                    if (mimeTypes.length > 0) {
                        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
                    }
                } else {
                    String mimeTypesStr = "";
                    for (String mimeType : mimeTypes) {
                        mimeTypesStr += mimeType + "|";
                    }
                    intent.setType(mimeTypesStr.substring(0, mimeTypesStr.length() - 1));
                }
//                startActivityForResult(Intent.createChooser(intent, "ChooseFile"), 1);
                startActivityForResult(Intent.createChooser(intent, "Open CSV"), 1);
            }
        });
        excel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isExcel = true;
                apiCallBack(getApiCommonController().downloadExcel("add", "xlsx"));
            }
        });

        csv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isExcel = false;
                apiCallBack(getApiCommonController().downloadExcel("add", "csv"));
            }
        });
    }

    @Nullable
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fetch_student, container, false);


        return view;
    }


    @Subscribe
    public void timeOut(String msg) {
        Toast.makeText(getApplicationContext(), "msg: " + msg, Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            super.onActivityResult(requestCode, resultCode, data);
            if (requestCode == 1 && resultCode == RESULT_OK) {
                filePath = FilePath.getPath(getApplicationContext(), data.getData());
                attachedFile.setText(Helper.getFileName(getApplicationContext(), data.getData()));
            }
        } catch (Exception ex) {
        }

    }

    @Subscribe
    public void downloadFormat(ResponseBody body) {
        FileOutputStream fileOutputStream = null;
        File file;
        if (isExcel) {
            file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), " Add_Students.xlsx");
        } else {
            file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), " Add_Students.csv");
        }
        try {
            byte[] ar = body.bytes();
            fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(ar);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (isExcel) {
                            Toast.makeText(getApplicationContext(), R.string.file_success_fully_downloaded, Toast.LENGTH_SHORT).show();
                            Uri uri = Uri.fromFile(file);
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.setDataAndType(uri, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        } else {
                            Toast.makeText(getApplicationContext(), R.string.file_success_fully_downloaded, Toast.LENGTH_SHORT).show();
                            Uri uri = Uri.fromFile(file);
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.addCategory(Intent.CATEGORY_OPENABLE);
                            intent.setDataAndType(uri, "text/csv");
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        }
                    } catch (ActivityNotFoundException e) {
                        Toast.makeText(getApplicationContext(), R.string.no_application_found_open_the_file, Toast.LENGTH_SHORT).show();
                    }
                }
            }, 1000);
        } catch (IOException e) {
//            Toast.makeText(getApplicationContext()(), "" + ERROR, Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        } finally {
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Subscribe
    public void uploadFile(ServerResponse response) {
        attachedFile.setText(getApplicationContext().getResources().getString(R.string.attach_excel_file));
        filePath = null;
        dialogSuccess(getString(R.string.file_successfully_uploaded));
    }


}





