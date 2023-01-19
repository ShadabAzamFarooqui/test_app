package com.app.mschooling.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.mschooling.com.R;

/**
 * Created by pc patidar on 09/04/19.
 */

public class BottomDialogAdapter extends BaseAdapter {
    Context appCompatActivity;
    int count;

    public BottomDialogAdapter() {
    }

    public BottomDialogAdapter(Context appCompatActivity, int count) {
        try {
            this.appCompatActivity = appCompatActivity;
            this.count = count;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getCount() {
        return count;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {// inflate the layout for each list row
        ViewHolder viewHolder;
        try {
            if (convertView == null) {
                convertView = LayoutInflater.from(appCompatActivity).
                        inflate(R.layout.bottom_dialogitem, parent, false);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            // get the TextView for item name and item description

            if (position == 0) {
                viewHolder.itemName.setText("Gallery");
                viewHolder.imageView.setBackgroundResource(R.drawable.camera_167);
            }
            if (position == 1) {
                viewHolder.itemName.setText("Camera");
                viewHolder.imageView.setBackgroundResource(R.drawable.gallery_white);
            }
            if (position == 2) {
                viewHolder.itemName.setText("Remove");
                viewHolder.imageView.setBackgroundResource(R.drawable.gallery_white);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return convertView;
    }

    private class ViewHolder {
        TextView itemName;
        ImageView imageView;

        public ViewHolder(View view) {
            itemName = (TextView) view.findViewById(R.id.text_view_bottom);
            imageView = (ImageView) view.findViewById(R.id.image_view_bottom);
        }
    }
}
