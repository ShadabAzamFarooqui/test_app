package com.app.mschooling.gallery.adadpter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.app.mschooling.other.activity.ShowImageActivity;
import com.app.mschooling.base.fragment.BaseFragment;
import com.app.mschooling.com.R;
import com.app.mschooling.event_handler.EventDeleteChild;
import com.app.mschooling.utils.Preferences;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.mschooling.transaction.common.api.Common;
import com.mschooling.transaction.response.gallery.GalleryResponse;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

public class GalleryFragmentAdapter extends RecyclerView.Adapter<GalleryFragmentAdapter.ViewHolder> {

    private Activity context;
    List<GalleryResponse> responseList;
    private BaseFragment instance;


    public GalleryFragmentAdapter(Activity context, BaseFragment instance,  List<GalleryResponse> responseList) {

        this.context = context;
        this.responseList = responseList;
        this.instance=instance;
    }

    public GalleryFragmentAdapter(Activity context, List<GalleryResponse> responseList) {

        this.context = context;
        this.responseList = responseList;
        this.instance=instance;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_gallery, viewGroup, false);
        return new ViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        viewHolder.tittle.setText(responseList.get(position).getId());

        if (position==responseList.size()-1&&position>2){
            viewHolder.marginLayout.setVisibility(View.VISIBLE);
        }else {
            viewHolder.marginLayout.setVisibility(View.GONE);
        }

        Glide.with(context)
                .load(responseList.get(position).getUrl())
                .transition(DrawableTransitionOptions.withCrossFade())
                .apply(RequestOptions.skipMemoryCacheOf(true))
                .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.ALL))
                .apply(new RequestOptions().placeholder(R.drawable.default_image))
                .into(viewHolder.image);


        viewHolder.viewImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ShowImageActivity.class);
                intent.putExtra("url", responseList.get(position).getUrl());
                intent.putExtra("name", responseList.get(position).getId());
                context.startActivity(intent);
            }
        });

        if (Preferences.getInstance(context).getROLE().equals(Common.Role.ADMIN.value())){
            viewHolder.delete.setVisibility(View.VISIBLE);
        }else {
            viewHolder.delete.setVisibility(View.GONE);
        }

        viewHolder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dialog = new Dialog(context);
                dialog.getWindow().requestFeature(1);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
                dialog.setContentView(R.layout.dialog_delete_notice);
                dialog.setCancelable(false);
                TextView delete =  dialog.findViewById(R.id.delete);
                TextView cancel =  dialog.findViewById(R.id.cancel);
                delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        EventBus.getDefault().post(new EventDeleteChild(position));
                    }
                });
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();

                    }
                });
                dialog.show();
            }
        });

    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return responseList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tittle;
        ImageView image;
        LinearLayout viewImage;
        LinearLayout delete;
        View marginLayout;
       // ProgressBar progressBar;

        public ViewHolder(View itemView) {
            super(itemView);
            marginLayout = itemView.findViewById(R.id.marginLayout);
            tittle = itemView.findViewById(R.id.description);
            image = itemView.findViewById(R.id.image);
            viewImage = itemView.findViewById(R.id.viewImage);
            delete = itemView.findViewById(R.id.delete);
          //  progressBar = itemView.findViewById(R.id.progressBar);

        }
    }


//

//
}