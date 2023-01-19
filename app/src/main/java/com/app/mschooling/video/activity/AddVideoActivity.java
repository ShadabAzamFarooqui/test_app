package com.app.mschooling.video.activity;


import static com.app.mschooling.com.R.layout.add_video_activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.app.mschooling.base.activity.BaseActivity;
import com.app.mschooling.com.R;
import com.mschooling.transaction.common.api.Status;
import com.mschooling.transaction.request.video.AddVideoRequest;
import com.mschooling.transaction.response.video.AddVideoResponse;

import org.greenrobot.eventbus.Subscribe;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AddVideoActivity extends BaseActivity {


    @BindView(R.id.name)
    EditText name;
    @BindView(R.id.url)
    EditText url;
    @BindView(R.id.submit)
    Button submit;
    @BindView(R.id.main_layout)
    LinearLayout mainLayout;
    @BindView(R.id.categoryLayout)
    LinearLayout categoryLayout;
    @BindView((R.id.category))
    TextView category;

    String id;
    String categoryId;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(add_video_activity);
        ButterKnife.bind(this);
        if (getIntent()!=null){
            id=getIntent().getStringExtra("id");
        }
        if (id!=null){
            name.setText(getIntent().getStringExtra("name"));
            url.setText(getIntent().getStringExtra("url"));
            toolbarClick(getString(R.string.tool_update_video));
        }else {
            toolbarClick(getString(R.string.tool_add_video));
        }
       categoryId= getIntent().getStringExtra("categoryId");
        onSharedIntent();

//        name.setText("Twinkle Twinkle Little Star + More Nursery Rhymes & Kids Songs");
//        url.setText("https://www.youtube.com/watch?v=RciE68Q7PCA");

        categoryLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), VideoCategoryListActivity.class);
                intent.putExtra("intent","select");
                startActivityForResult(intent, 100);
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (categoryLayout.getVisibility()==View.VISIBLE){
                    if(category.getText().toString().isEmpty()){
                        dialogError(getString(R.string.select_category));
                        return;
                    }
                }
                if (name.getText().toString().trim().isEmpty()){
                    dialogError(getString(R.string.enter_name));
                    return;
                }else if (url.getText().toString().trim().isEmpty()){
                    dialogError(getString(R.string.enter_url_w_star));
                    return;
                }else if (!url.getText().toString().trim().contains("http")){
                    dialogError(getString(R.string.enter_valid_url));
                    return;
                }

                AddVideoRequest request=new AddVideoRequest();
                request.setCategoryId(categoryId);
                request.setId(id);
                request.setUrl(url.getText().toString().trim());
                request.setName(name.getText().toString().trim());
                apiCallBack(getApiCommonController().addVideo(request));
            }
        });
    }


    @Subscribe
    public void notice(AddVideoResponse response) {
        if (response.getStatus().value() == Status.SUCCESS.value()) {
            name.setText("");
            url.setText("");
            if (id==null){
                dialogSuccess(response.getMessage().getMessage());
            }else {
                dialogSuccessFinish(response.getMessage().getMessage());
            }

        } else {
            dialogError(response.getMessage().getMessage());
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            super.onActivityResult(requestCode, resultCode, data);
            if (resultCode == RESULT_OK) {
                if (requestCode == 100) {
                    toolbarClick(getString(R.string.tool_add_video));
                    categoryId = data.getStringExtra("id");
                    category.setText(data.getStringExtra("name"));
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void onSharedIntent() {
        Intent receiverdIntent = getIntent();
        String receivedAction = receiverdIntent.getAction();
        String receivedType = receiverdIntent.getType();

        if (receivedAction!=null && receivedAction.equals(Intent.ACTION_SEND)) {
            // check mime type
            if (receivedType.startsWith("text/")) {
                categoryLayout.setVisibility(View.VISIBLE);
                String receivedText = receiverdIntent
                        .getStringExtra(Intent.EXTRA_TEXT);
                if (receivedText != null) {
                    //do your stuff
                    url.setText(receivedText);
                }
            }

//            else if (receivedType.startsWith("image/")) {
//
//                Uri receiveUri = (Uri) receiverdIntent
//                        .getParcelableExtra(Intent.EXTRA_STREAM);
//
//                if (receiveUri != null) {
//                    //do your stuff
//                    fileUri = receiveUri;// save to your own Uri object
//
//                    Log.e("Share content add video",receiveUri.toString());
//                }
//            }
//
//        } else if (receivedAction.equals(Intent.ACTION_MAIN)) {
//
//            Log.e("Share content add video", "onSharedIntent: nothing shared" );
       }
    }

}

