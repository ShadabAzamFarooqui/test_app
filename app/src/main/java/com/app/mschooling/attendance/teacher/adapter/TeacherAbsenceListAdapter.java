package com.app.mschooling.attendance.teacher.adapter;

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
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.app.mschooling.timetable.activity.TeacherTimeTableAllocationActivity;
import com.app.mschooling.teachers.detail.activity.TeacherDetailActivity;
import com.app.mschooling.com.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.mschooling.transaction.common.api.Common;
import com.mschooling.transaction.response.attendance.TeacherAttendanceResponse;

import java.util.ArrayList;
import java.util.List;

public class TeacherAbsenceListAdapter extends RecyclerView.Adapter<TeacherAbsenceListAdapter.ViewHolder> implements Filterable {

    private Activity context;
    public static List<TeacherAttendanceResponse> responseList;

    public static List<TeacherAttendanceResponse> filteredList;


    public TeacherAbsenceListAdapter(Activity context, List<TeacherAttendanceResponse> data) {
        this.context = context;
        this.responseList = data;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_absence, viewGroup, false);
        return new ViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, @SuppressLint("RecyclerView") int position) {

        viewHolder.enrolmentId.setText(responseList.get(position).getEnrollmentId());
        viewHolder.fname.setText("" + responseList.get(position).getfName());
        viewHolder.lname.setText("" + responseList.get(position).getlName());
        if (Common.Attendance.A.equals(responseList.get(position).getAttendanceResponse().getAttendance())){
            viewHolder.attendance.setText("Absent");
        }else if(Common.Attendance.L.equals(responseList.get(position).getAttendanceResponse().getAttendance())){
            viewHolder.attendance.setText("Leave");
        }else if (Common.Attendance.P.equals(responseList.get(position).getAttendanceResponse().getAttendance())){
            viewHolder.attendance.setText("Present");
        }else {
            viewHolder.attendance.setText("N/A");
        }

        if (responseList.get(position).getUrl()==null){
            viewHolder.shimmerFrameLayout.setVisibility(View.GONE);
            viewHolder.image.setImageDrawable(context.getResources().getDrawable(R.drawable.user));
        }else if (responseList.get(position).getUrl().equals("")){
            viewHolder.shimmerFrameLayout.setVisibility(View.GONE);
            viewHolder.image.setImageDrawable(context.getResources().getDrawable(R.drawable.user));
        }else {
            viewHolder.shimmerFrameLayout.setVisibility(View.VISIBLE);
            Glide.with(context)
                    .load(responseList.get(position).getUrl())
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            viewHolder.shimmerFrameLayout.setVisibility(View.GONE);
                            viewHolder.image.setImageDrawable(context.getResources().getDrawable(R.drawable.user));
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            viewHolder.shimmerFrameLayout.setVisibility(View.GONE);
                            return false;
                        }
                    })
                    .into(viewHolder.image);
        }


        viewHolder.details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, TeacherDetailActivity.class);
                intent.putExtra("id", responseList.get(position).getEnrollmentId());
                context.startActivity(intent);
            }
        });

        viewHolder.main_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context, TeacherTimeTableAllocationActivity.class);
                intent.putExtra("enrollmentId",responseList.get(position).getEnrollmentId());
                context.startActivity(intent);
            }
        });


    }


    @Override
    public int getItemCount() {
        return responseList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        RelativeLayout main_layout;

        TextView fname;
        TextView lname;
        TextView attendance;

        ImageView image;

        TextView enrolmentId;
        TextView details;
        ShimmerFrameLayout shimmerFrameLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            fname = itemView.findViewById(R.id.fname);
            lname = itemView.findViewById(R.id.lname);
            attendance = itemView.findViewById(R.id.attendance);
            main_layout = itemView.findViewById(R.id.main_layout);
            enrolmentId = itemView.findViewById(R.id.enrolmentId);
            image = itemView.findViewById(R.id.image);
            details = itemView.findViewById(R.id.details);
            shimmerFrameLayout = itemView.findViewById(R.id.shimmerFrameLayout);

        }
    }


    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    filteredList = responseList;
                } else {
                    ArrayList<TeacherAttendanceResponse> filteredList = new ArrayList<>();
                    for (TeacherAttendanceResponse teacherResponse : responseList) {
                        if (teacherResponse.getEnrollmentId().startsWith(charString) || teacherResponse.getEnrollmentId().contains(charString)) {
                            filteredList.add(teacherResponse);
                        } else if (teacherResponse.getfName().toLowerCase().startsWith(charString) || teacherResponse.getfName().toLowerCase().contains(charString)) {
                            filteredList.add(teacherResponse);
                        } else if (teacherResponse.getlName().toLowerCase().startsWith(charString) || teacherResponse.getlName().toLowerCase().contains(charString)) {
                            filteredList.add(teacherResponse);
                        }
                    }
                    TeacherAbsenceListAdapter.filteredList = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = filteredList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                filteredList = (ArrayList<TeacherAttendanceResponse>) filterResults.values;
                responseList = filteredList;
                notifyDataSetChanged();
            }
        };
    }

//    @Override
//    public int getItemViewType(int position) {
//        return position;
//    }


}