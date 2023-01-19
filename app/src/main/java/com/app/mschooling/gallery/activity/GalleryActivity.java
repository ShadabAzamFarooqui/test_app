package com.app.mschooling.gallery.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.mschooling.gallery.adadpter.GalleryFragmentAdapter;
import com.app.mschooling.base.activity.BaseActivity;
import com.app.mschooling.com.R;
import com.app.mschooling.event_handler.EventDeleteChild;
import com.app.mschooling.gallery.fragment.GalleryRecyFragment;
import com.app.mschooling.utils.Preferences;
import com.mschooling.transaction.common.api.Common;
import com.mschooling.transaction.common.api.Status;
import com.mschooling.transaction.response.gallery.DeleteGalleryResponse;
import com.mschooling.transaction.response.gallery.GalleryResponse;
import com.mschooling.transaction.response.gallery.GetGalleryResponse;
import com.mschooling.multimediapicker.GalleryPickerActivity;
import com.mschooling.multimediapicker.MultimediaPicker;
import com.mschooling.multimediapicker.model.Image;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

public class GalleryActivity extends BaseActivity {

    RecyclerView recyclerView;
    LinearLayout add;
    LinearLayout noRecord;
    ProgressBar progressBar;
    List<GalleryResponse> responseList;
    int pastVisibleItems, visibleItemCount, totalItemCount;
    private boolean loading = true;
    int pageNo;
    LinearLayoutManager layoutManager;
    GalleryFragmentAdapter adapter;

    private ArrayList<Image> imageList = new ArrayList<>();

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery_student_teacher);
        toolbarClick(getString(R.string.gallery));


        progressBar = findViewById(R.id.progressBar);
        recyclerView = findViewById(R.id.recycler_view);
        noRecord = findViewById(R.id.noRecord);
        pageNo = 0;
        loading = true;
        apiCallBack(getApiCommonController().galleryList(pageNo));

        add = findViewById(R.id.add);
        if (Preferences.getInstance(getApplicationContext()).getROLE().equals(Common.Role.ADMIN.value())) {
            add.setVisibility(View.VISIBLE);
        } else {
            add.setVisibility(View.GONE);
        }

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                MultimediaPicker.create(GalleryActivity.this)
                        .folderMode(true) // set folder mode (false by default)
                        .folderTitle(getString(R.string.album)) // folder selection title
                        .imageTitle(getString(R.string.tap_to_select)) // image selection title
                        .single() // single mode
                        .setOnlyImages(true)
                        .multi() // multi mode (default mode)
                        .limit(10) // max images can be selected (999 by default)
                        .showCamera(false) // show camera or not (true by default)
                        .imageDirectory("Camera")   // captured image directory name ("Camera" folder by default)
                        .origin(imageList) // original selected images, used in multi mode
                        .start(REQUEST_GALLERY);
            }
        });


        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) {
                    visibleItemCount = layoutManager.getChildCount();
                    totalItemCount = layoutManager.getItemCount();
                    pastVisibleItems = layoutManager.findFirstCompletelyVisibleItemPosition();
                    if (loading) {
                        if ((visibleItemCount + pastVisibleItems) >= totalItemCount) {
                            pageNo++;
                            loading = false;
                            progressBar.setVisibility(View.VISIBLE);
                            apiCallBackWithout(getApiCommonController().galleryList(pageNo));
                        }

                    }
                }
            }
        });
    }
    @SuppressLint("NotifyDataSetChanged")
    @Subscribe
    public void getResponse(GetGalleryResponse response) {
        progressBar.setVisibility(View.GONE);
        if (response.getStatus().value() == Status.SUCCESS.value()) {
            if (pageNo == 0) {
                if (response.getGalleryResponses().size() > 0) {
                    noRecord.setVisibility(View.GONE);
                } else {
                    noRecord.setVisibility(View.VISIBLE);
                }
                responseList = response.getGalleryResponses();
                recyclerView.setHasFixedSize(true);
                recyclerView.setFocusable(false);
                recyclerView.setNestedScrollingEnabled(false);
                layoutManager = new GridLayoutManager(getApplicationContext(), 2);
                recyclerView.setLayoutManager(layoutManager);
                adapter = new GalleryFragmentAdapter(this,  responseList);

                recyclerView.setAdapter(adapter);
                recyclerView.scrollToPosition(GalleryRecyFragment.position);
            } else {
                responseList.addAll(response.getGalleryResponses());
                adapter.notifyDataSetChanged();
                if (response.getGalleryResponses().size() > 0) {
                    loading = true;
                } else {
                    loading = false;
                }
            }
        } else {
            dialogError(response.getMessage().getMessage());
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    @Subscribe
    public void deleteResponse(DeleteGalleryResponse response) {
        if (response.getStatus().value() == Status.SUCCESS.value()) {
            responseList.remove(eventDelete.getGroupPosition());
            adapter.notifyDataSetChanged();

            if (responseList.size() > 0) {
                noRecord.setVisibility(View.GONE);
            } else {
                noRecord.setVisibility(View.VISIBLE);
            }
        } else {
            dialogError(response.getMessage().getMessage());
        }
    }

    EventDeleteChild eventDelete;

    @Subscribe
    public void deleteEvent(EventDeleteChild eventDelete) {
        this.eventDelete = eventDelete;
        apiCallBack(getApiCommonController().deleteGallery(responseList.get(eventDelete.getGroupPosition()).getId()));
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_GALLERY && resultCode == RESULT_OK && data != null) {
            imageList = data.getParcelableArrayListExtra(GalleryPickerActivity.INTENT_EXTRA_SELECTED_IMAGES);
            Intent intent = new Intent(getApplicationContext(), ImageUploadActivity.class);
            intent.putParcelableArrayListExtra(GalleryPickerActivity.INTENT_EXTRA_SELECTED_IMAGES, imageList);
            startActivity(intent);
        }
    }
}
