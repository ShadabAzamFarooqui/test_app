package com.app.mschooling.video.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.mschooling.video.adapter.VideoListAdapter;
import com.app.mschooling.com.R;
import com.app.mschooling.utils.AppUser;
import com.app.mschooling.utils.ParameterConstant;
import com.app.mschooling.utils.Preferences;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.mschooling.transaction.common.user.SettingSetup;
import com.mschooling.transaction.response.video.VideoResponse;

import java.util.ArrayList;
import java.util.List;

public class WatchVideoActivity extends YouTubeBaseActivity implements YouTubePlayer.OnInitializedListener {
    String fileName = "";
    private static final int RECOVERY_DIALOG_REQUEST = 1;
    // YouTube player view
    private YouTubePlayerView youTubeView;
    RecyclerView recyclerView;
    LinearLayout back;
    LinearLayout noRecord;
    TextView title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watch_video);
        Intent intent = getIntent();
        fileName = intent.getStringExtra("filename").replace("https://www.youtube.com/watch?v=", "");
        fileName = fileName.replace("https://youtu.be/", "");
//      requestWindowFeature(Window.FEATURE_NO_TITLE);
//      getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//      WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // https://www.youtube.com/watch?v=
        youTubeView = (YouTubePlayerView) findViewById(R.id.youtube_view);
        back = (LinearLayout) findViewById(R.id.back);
        title = (TextView) findViewById(R.id.title);
        recyclerView = findViewById(R.id.recyclerView);
        noRecord = findViewById(R.id.noRecord);
        // Initializing video player with developer key
        youTubeView.initialize(Preferences.getInstance(getApplicationContext()).getYoutubeApiKey(), this);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        title.setText(getIntent().getStringExtra("name"));

//        List<VideoResponse> videoResponses = new ArrayList<>();
//        try {
//            for (int i = 0; i < AppUser.getInstance().getVideoResponse().size(); i++) {
//                if (!AppUser.getInstance().getVideoResponse().get(i).getId().equals(getIntent().getStringExtra("id"))) {
//                    videoResponses.add(AppUser.getInstance().getVideoResponse().get(i));
//                }
//            }
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//
//        setAdapter(videoResponses);

    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider,
                                        YouTubeInitializationResult errorReason) {
        if (errorReason.isUserRecoverableError()) {
            errorReason.getErrorDialog(this, RECOVERY_DIALOG_REQUEST).show();
        } else {
            String errorMessage = String.format(
                    getString(R.string.error_player), errorReason.toString());
            Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider,
                                        YouTubePlayer player, boolean wasRestored) {
        if (!wasRestored) {

            // loadVideo() will auto play video
            // Use cueVideo() method, if you don't want to play it automatically
            player.loadVideo(fileName);

            SettingSetup settingSetup= Preferences.getInstance(getApplicationContext()).getConfiguration().getCommonSetup().getSettingSetup();
            // Hiding player controls
            if (settingSetup.getVideoPrivacy().trim().equals("ENABLED")) {
                player.setPlayerStyle(YouTubePlayer.PlayerStyle.MINIMAL);

            } else {
                player.setPlayerStyle(YouTubePlayer.PlayerStyle.DEFAULT);

            }

            if (settingSetup.getVideoOrientation()!=null){
                if (settingSetup.getVideoOrientation().equals("LANDSCAPE")){
                    player.setFullscreen(true);
                }else {
                    player.setFullscreen(false);
                }
            }else {
                player.setFullscreen(true);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RECOVERY_DIALOG_REQUEST) {
            // Retry initialization if user performed a recovery action
            getYouTubePlayerProvider().initialize(Preferences.getInstance(getApplicationContext()).getYoutubeApiKey(), this);
        }
    }

    private YouTubePlayer.Provider getYouTubePlayerProvider() {
        return (YouTubePlayerView) findViewById(R.id.youtube_view);
    }


    private void setAdapter(List<VideoResponse> videoResponseList) {
        if (videoResponseList.size() > 0) {
            noRecord.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            recyclerView.setHasFixedSize(true);
            recyclerView.setFocusable(false);
            recyclerView.setNestedScrollingEnabled(false);
            recyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(), 1));
            recyclerView.setAdapter(new VideoListAdapter(this, videoResponseList, getIntent().getStringExtra("categoryId"), true));
        } else {
            noRecord.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }
    }
}