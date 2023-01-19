package com.app.mschooling.gallery.fragment;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import com.app.mschooling.gallery.adadpter.GalleryRecyFragmentAdapter;
import com.app.mschooling.base.fragment.BaseFragment;
import com.app.mschooling.com.R;
import com.mschooling.transaction.common.api.Status;
import com.mschooling.transaction.response.gallery.GalleryResponse;
import com.mschooling.transaction.response.gallery.GetGalleryResponse;

import org.greenrobot.eventbus.Subscribe;

import java.util.List;

public class GalleryRecyFragment extends BaseFragment {

    RecyclerView recyclerView;
    List<GalleryResponse> responseList;
    int pastVisiblesItems, visibleItemCount, totalItemCount;
    private boolean loading = true;
    int pageNo;
    LinearLayoutManager layoutManager;
    GalleryRecyFragmentAdapter adapter;
    public static Integer position = 0;

    @Nullable
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_gallery_recy, container, false);
        toolbarClick(view, getString(R.string.gallery));
        recyclerView = view.findViewById(R.id.recycler_view);
        pageNo=0;
        loading=true;
        apiCallBack(getApiCommonController().galleryList(pageNo));
        /*recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) {
                    visibleItemCount = layoutManager.getChildCount();
                    totalItemCount = layoutManager.getItemCount();
                    pastVisiblesItems = layoutManager.findFirstCompletelyVisibleItemPosition();
                    pageNo++;
                    if (loading) {
                        if ((visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                            loading = false;
                            apiCallBack(getApi().galleryList(pageNo));

                        }

                    }
                }
            }
        });*/

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (!recyclerView.canScrollHorizontally(1)) {
                    if (loading) {
                        pageNo++;
                        apiCallBack(getApiCommonController().galleryList(pageNo));
                    }
                }
            }
        });
//        setAdapter();
        return view;
    }


    void setAdapter() {
        recyclerView.setHasFixedSize(true);
        recyclerView.setFocusable(false);
        recyclerView.setNestedScrollingEnabled(false);
        layoutManager=new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);
        adapter=new GalleryRecyFragmentAdapter(this, getActivity(), responseList);
        recyclerView.setAdapter(adapter);
        recyclerView.scrollToPosition(position);
        SnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(recyclerView);
    }


    @Subscribe
    public void getResponse(GetGalleryResponse response) {
        if (response.getStatus().value() == Status.SUCCESS.value()) {
            if (pageNo == 0) {
                responseList=response.getGalleryResponses();
                setAdapter();
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


}

