package com.app.mschooling.examination.adapter;

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
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.app.mschooling.examination.activity.ResultActivityActivity;
import com.app.mschooling.com.R;
import com.app.mschooling.utils.Helper;
import com.app.mschooling.utils.ParameterConstant;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.mschooling.transaction.common.Firebase;
import com.mschooling.transaction.response.examination.StudentExaminationClassResultResponse;

import java.util.ArrayList;
import java.util.List;

public class StudentResultListAdapter extends RecyclerView.Adapter<StudentResultListAdapter.ViewHolder> implements Filterable {

    public  List<StudentExaminationClassResultResponse> responseList;
    public  List<StudentExaminationClassResultResponse> filteredList;
    Activity context;
    String examId;

    public StudentResultListAdapter(Activity context, String examId, List<StudentExaminationClassResultResponse> data) {
        this.context = context;
        this.examId = examId;
        this.responseList = data;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_student_exam_list, viewGroup, false);
        return new ViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        StudentExaminationClassResultResponse response=responseList.get(position);
        viewHolder.name.setText(Helper.getName(response.getStudentBasicResponse().getfName(),response.getStudentBasicResponse().getlName()));
        viewHolder.enrollmentId.setText("" + response.getStudentBasicResponse().getEnrollmentId());
        viewHolder.totalMarks.setText("" + response.getTotalMarks());
        viewHolder.obtainedMarks.setText("" + response.getObtainMarks());
        viewHolder.percentage.setText("" + Helper.roundOff(response.getObtainPercentage()) + "%");

        if (response.getStudentProfileResponse().getProfileFirebase() == null) {
            Firebase firebase = new Firebase();
            firebase.setUrl(ParameterConstant.getDefaultUserUrl());
            response.getStudentProfileResponse().setProfileFirebase(firebase);
        } else if (response.getStudentProfileResponse().getProfileFirebase().getUrl() == null) {
            response.getStudentProfileResponse().getProfileFirebase().setUrl(ParameterConstant.getDefaultUserUrl());
        }

        Glide.with(context)
                .load(response.getStudentProfileResponse().getProfileFirebase().getUrl())
//                .diskCacheStrategy(DiskCacheStrategy.NONE)
//                .skipMemoryCache(true)
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

        viewHolder.mainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ResultActivityActivity.class);
                intent.putExtra("id", examId);
                intent.putExtra("name", Helper.getName(response.getStudentBasicResponse().getfName(),response.getStudentBasicResponse().getlName()));
                intent.putExtra("enrollmentId", response.getStudentBasicResponse().getEnrollmentId());
                context.startActivity(intent);
            }
        });
    }


    @Override
    public int getItemCount() {
        return responseList.size();
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
                    ArrayList<StudentExaminationClassResultResponse> filteredList = new ArrayList<>();
                    for (StudentExaminationClassResultResponse noticeResponse : responseList) {
                        if (noticeResponse.getStudentBasicResponse().getEnrollmentId().toLowerCase().startsWith(charString) /*|| androidVersion.getMobile().startsWith(charString)*/) {
                            filteredList.add(noticeResponse);
                        } else if (noticeResponse.getStudentBasicResponse().getfName().toLowerCase().contains(charString) /*|| androidVersion.getMobile().contains(charString)*/) {
                            filteredList.add(noticeResponse);
                        } else if (noticeResponse.getStudentBasicResponse().getlName().contains(charString) /*|| androidVersion.getMobile().contains(charString)*/) {
                            filteredList.add(noticeResponse);
                        }
                    }
                   StudentResultListAdapter. this.filteredList = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = filteredList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                filteredList = (ArrayList<StudentExaminationClassResultResponse>) filterResults.values;
                responseList = filteredList;
                notifyDataSetChanged();
            }
        };
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView name, enrollmentId, totalMarks, obtainedMarks, percentage;
        LinearLayout mainLayout;
        ShimmerFrameLayout shimmerFrameLayout;
        ImageView image;

        public ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            enrollmentId = itemView.findViewById(R.id.enrollmentId);
            totalMarks = itemView.findViewById(R.id.totalMarks);
            obtainedMarks = itemView.findViewById(R.id.obtainedMarks);
            percentage = itemView.findViewById(R.id.percentage);
            mainLayout = itemView.findViewById(R.id.mainLayout);
            shimmerFrameLayout = itemView.findViewById(R.id.shimmerFrameLayout);
            image = itemView.findViewById(R.id.image);

        }
    }

}