package com.app.mschooling.birthday.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.app.mschooling.attendance.student.adapter.MarkStudentAttendanceAdapter;
import com.app.mschooling.com.R;
import com.app.mschooling.com.databinding.RowAttendanceBinding;
import com.app.mschooling.com.databinding.RowBirthdayBinding;
import com.app.mschooling.other.activity.PrintIdCardActivity;
import com.app.mschooling.utils.BuzzTextView;
import com.app.mschooling.utils.Helper;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.mschooling.transaction.common.api.Common;
import com.mschooling.transaction.response.birthday.BirthdayResponse;
import com.mschooling.transaction.response.icard.ICardResponse;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class BirthdayListAdapter extends RecyclerView.Adapter<BirthdayListAdapter.ViewHolder> {

    private Activity context;

    List<BirthdayResponse> responseList;

    public BirthdayListAdapter(Activity context, List<BirthdayResponse> responseList) {
        this.context = context;
        this.responseList = responseList;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

        RowBirthdayBinding binding = RowBirthdayBinding.inflate(LayoutInflater.from(viewGroup.getContext()));
        return new ViewHolder(binding.getRoot());

    }

    @SuppressLint("SetTextI18n")
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {

        BirthdayResponse birthdayResponse = responseList.get(position);
        viewHolder.binding.role.setText(birthdayResponse.getRole().value());
        viewHolder.binding.name.setText(Helper.getName(birthdayResponse.getfName(), birthdayResponse.getlName()));
        viewHolder.binding.classSection.setText(birthdayResponse.getClassName() + " (" + birthdayResponse.getSectionName() + ")");
        viewHolder.binding.dob.setText(birthdayResponse.getDate());
        if (birthdayResponse.getRole().value().equals(Common.Role.STUDENT.value())) {
            viewHolder.binding.classSection.setVisibility(View.VISIBLE);
            viewHolder.binding.role.setVisibility(View.GONE);
        } else {
            viewHolder.binding.classSection.setVisibility(View.GONE);
            viewHolder.binding.role.setVisibility(View.VISIBLE);
        }

        viewHolder.binding.classSection.setText(responseList.get(position).getClassName() + " (" + responseList.get(position).getSectionName() + ")");

        Helper.getProfileImageStringUrl(context,birthdayResponse.getUrl(),viewHolder.binding.image,viewHolder.binding.shimmerFrameLayout);

    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return responseList.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {


        RowBirthdayBinding binding;

        public ViewHolder(View view) {
            super(view);
            binding = DataBindingUtil.findBinding(itemView.getRootView());
        }
    }


}