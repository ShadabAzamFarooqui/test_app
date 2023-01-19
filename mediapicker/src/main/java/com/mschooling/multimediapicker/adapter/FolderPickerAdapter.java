package com.mschooling.multimediapicker.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.mschooling.multimediapicker.R;
import com.mschooling.multimediapicker.listeners.OnFolderClickListener;
import com.mschooling.multimediapicker.model.Folder;

import java.util.List;

/**
 * Created by admin on 03/02/2017.
 */

public class FolderPickerAdapter extends RecyclerView.Adapter<FolderPickerAdapter.FolderViewHolder> {

    private Context context;
    private LayoutInflater inflater;
    private final OnFolderClickListener folderClickListener;

    private List<Folder> folders;

    public FolderPickerAdapter(Context context, OnFolderClickListener folderClickListener) {
        this.context = context;
        this.folderClickListener = folderClickListener;
        inflater = LayoutInflater.from(this.context);
    }

    @Override
    public FolderViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.folder_item, parent, false);
        return new FolderViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final FolderViewHolder holder, int position) {

        final Folder folder = folders.get(position);


        try {
            Glide.with(context)
                    .load(folder.getImages().get(0).getPath())
                    .placeholder(R.drawable.folder_placeholder)
                    .error(R.drawable.folder_placeholder)
                    //.animate( animationObject )
//                    .crossFade()
                    .into(holder.image);
        } catch (NoSuchMethodError e) {
            e.printStackTrace();
            System.out.println("http: error "+e.getMessage());
            new AlertDialog.Builder(context)
                    .setTitle("Sorry")
                    .setMessage(e.getMessage())
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    })

                    .setNegativeButton(android.R.string.no, null)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();

        }

        holder.name.setText(folders.get(position).

                getFolderName());
        holder.number.setText(String.valueOf(folders.get(position).

                getImages().

                size()));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (folderClickListener != null)
                    folderClickListener.onFolderClick(folder);
            }
        });
    }

    public void setData(List<Folder> folders) {
        this.folders = folders;

        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return folders.size();
    }

    public static class FolderViewHolder extends RecyclerView.ViewHolder {

        private ImageView image;
        private TextView name;
        private TextView number;

        public FolderViewHolder(View itemView) {
            super(itemView);

            image = (ImageView) itemView.findViewById(R.id.image);
            name = (TextView) itemView.findViewById(R.id.tv_name);
            number = (TextView) itemView.findViewById(R.id.tv_number);
        }
    }
}

