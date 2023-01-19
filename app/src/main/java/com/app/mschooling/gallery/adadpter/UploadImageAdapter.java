package com.app.mschooling.gallery.adadpter;

import android.app.Activity;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.app.mschooling.com.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.mschooling.multimediapicker.model.Image;

import java.util.List;


public class UploadImageAdapter extends RecyclerView.Adapter<UploadImageAdapter.ViewHolder> {

    private Activity context;
    private List<Image> uri;


    public UploadImageAdapter(Activity context, List<Image> uri) {

        this.context = context;
        this.uri = uri;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_image_upload, viewGroup, false);
        return new ViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {

        Glide.with(context)
                .load(uri.get(position).getPath())
                .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.ALL))
                .into(viewHolder.image);

        viewHolder.remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uri.remove(position);
                if (uri.size() == 0) {
                    context.finish();
                }
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return uri.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        LinearLayout remove;
        RelativeLayout mainLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            remove = itemView.findViewById(R.id.remove);
            mainLayout = itemView.findViewById(R.id.mainLayout);

        }

    }
}