package com.app.mschooling.homework.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.app.mschooling.base.fragment.BaseFragment;
import com.app.mschooling.com.R;
import com.app.mschooling.other.activity.ShowImageActivity;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.mschooling.transaction.common.Firebase;
import com.mschooling.transaction.response.homework.StudentUploadedWorksheetResponse;

import java.util.List;

public class HomeworkStudentSubmittedAdapter extends RecyclerView.Adapter<HomeworkStudentSubmittedAdapter.ViewHolder> {

    private Activity context;
    List<StudentUploadedWorksheetResponse> firebaseList;
    BaseFragment instance;

    public HomeworkStudentSubmittedAdapter(Activity context, List<StudentUploadedWorksheetResponse> firebaseList) {

        this.context = context;
        this.firebaseList = firebaseList;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_home_work_image, viewGroup, false);
        return new ViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, @SuppressLint("RecyclerView") int position) {

        showShimmer(viewHolder);
        Glide.with(context)
                .load(firebaseList.get(position).getUrl())
                .apply(RequestOptions.skipMemoryCacheOf(true))
                .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.ALL))
                .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE))
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        hideShimmer(viewHolder);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, com.bumptech.glide.load.DataSource dataSource, boolean isFirstResource) {
                        hideShimmer(viewHolder);
                        return false;
                    }
                })
                .into(viewHolder.image);


        viewHolder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ShowImageActivity.class);
                intent.putExtra("url", firebaseList.get(position).getUrl());
                intent.putExtra("name", "Attachment");
                context.startActivity(intent);


            }
        });

    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return firebaseList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView image;
        ShimmerFrameLayout shimmerLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            shimmerLayout = itemView.findViewById(R.id.shimmerLayout);
        }
    }


    private void hideShimmer(ViewHolder viewHolder) {
        viewHolder.shimmerLayout.stopShimmer();
        viewHolder.shimmerLayout.setVisibility(View.GONE);
    }

    private void showShimmer(ViewHolder viewHolder) {
        viewHolder.shimmerLayout.startShimmer();
        viewHolder.shimmerLayout.setVisibility(View.VISIBLE);
    }
//

//
}