package com.app.mschooling.home.teacher.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.app.mschooling.birthday.activity.BirthdayListActivity;
import com.app.mschooling.class_section_subject.activity.AppendedClassListActivity;
import com.app.mschooling.application.activity.ApplicationListActivity;
import com.app.mschooling.broadcast.activity.BroadCastListActivity;
import com.app.mschooling.other.activity.ComingSoonActivity;
import com.app.mschooling.complaint.activity.ComplaintListActivity;
import com.app.mschooling.event.activity.EventsActivity;
import com.app.mschooling.gallery.activity.GalleryActivity;
import com.app.mschooling.notice.activity.NoticeListActivity;
import com.app.mschooling.paid.activity.Paid2Activity;
import com.app.mschooling.prayer.activity.PrayerListActivity;
import com.app.mschooling.teachers.filter.activity.TeacherFilterListActivity;
import com.app.mschooling.examination.activity.ExaminationTeacherDashboardActivity;
import com.app.mschooling.quiz.activity.TeacherQuizDashboardActivity;
import com.app.mschooling.com.BuildConfig;
import com.app.mschooling.com.R;
import com.app.mschooling.meeting.activity.DiscussionListActivity;
import com.app.mschooling.utils.AdmobHelper;
import com.app.mschooling.utils.Helper;
import com.app.mschooling.utils.ParameterConstant;
import com.app.mschooling.utils.Preferences;
import com.mschooling.transaction.common.Menu;

import java.util.HashMap;
import java.util.List;

public class TeacherHomeAdapter extends RecyclerView.Adapter<TeacherHomeAdapter.ViewHolder> {

    private Activity context;
    private List<Menu> data;
    HashMap<Integer, Boolean> map;

    public TeacherHomeAdapter(Activity context, List<Menu> data) {

        this.context = context;
        this.data = data;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_home_fragment, viewGroup, false);
        return new ViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, @SuppressLint("RecyclerView") int position) {

        if (Preferences.getInstance(context).getLanguage().equals("en")) {
            viewHolder.tittle.setText(data.get(position).getDisplayNameEn());
        } else {
            viewHolder.tittle.setText(data.get(position).getDisplayNameHi());
        }

        if (data.get(position).getName().equals("DashBoard")) {
            viewHolder.icon.setImageResource(R.drawable.dashboard);
        } else if (data.get(position).getName().equals("Report")) {
            viewHolder.icon.setImageResource(R.drawable.report);
        } else if (data.get(position).getName().equals("Examination")) {
            viewHolder.icon.setImageResource(R.drawable.examination);
        } else if (data.get(position).getName().equals("Gallery")) {
            viewHolder.icon.setImageResource(R.drawable.gallary);
        } else if (data.get(position).getName().equals("Complaint")) {
            viewHolder.icon.setImageResource(R.drawable.complaint);
        } else if (data.get(position).getName().equals("Notice")) {
            viewHolder.icon.setImageResource(R.drawable.notice);
        } else if (data.get(position).getName().equals("Application")) {
            viewHolder.icon.setImageResource(R.drawable.inbox);
        } else if (data.get(position).getName().equals("Attendance")) {
            viewHolder.icon.setImageResource(R.drawable.attendance);
        } else if (data.get(position).getName().equals("Broadcast")) {
            viewHolder.icon.setImageResource(R.drawable.broad_cast);
        } else if (data.get(position).getName().equals("Events")) {
            viewHolder.icon.setImageResource(R.drawable.events);
        } else if (data.get(position).getName().equals("Quiz") || data.get(position).getName().equals("Assign Quiz")) {
            viewHolder.icon.setImageResource(R.drawable.quiz);
        } else if (data.get(position).getName().equals("Time Table")) {
            viewHolder.icon.setImageResource(R.drawable.timetable);
        } else if (data.get(position).getName().equals("Std. Material")) {
            viewHolder.icon.setImageResource(R.drawable.e_doc);
        } else if (data.get(position).getName().equals("Discussion")) {
            viewHolder.icon.setImageResource(R.drawable.meeting);
        } else if (data.get(position).getName().equals("Video")) {
            viewHolder.icon.setImageDrawable(context.getResources().getDrawable(R.drawable.youtube));
        } else if (data.get(position).getName().contains("ICard")) {
            viewHolder.icon.setImageDrawable(context.getResources().getDrawable(R.drawable.id_card));
        } else if (data.get(position).getName().equals("Prayer")) {
            viewHolder.icon.setImageDrawable(context.getResources().getDrawable(R.drawable.prayer));
        } else if (data.get(position).getName().equals("Birthday")) {
            viewHolder.icon.setImageDrawable(context.getResources().getDrawable(R.drawable.birthday));
        }
        String first = String.valueOf(Helper.getAlphabet().get(position));
        ColorGenerator generator = ColorGenerator.MATERIAL;
        int color = generator.getColor(first);
        viewHolder.icon.setColorFilter(color, android.graphics.PorterDuff.Mode.SRC_IN);

        viewHolder.main_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AdmobHelper.updateCount(context);
                if (BuildConfig.DEBUG) {
                    intent(position);
                } else {
                    if (data.get(position).isPaid()) {
                        if (Preferences.getInstance(context).isPaid()) {
                            intent(position);
                        } else {
                            Intent intent = new Intent(context, Paid2Activity.class);
                            intent.putExtra("toolbar", data.get(position).getName());
                            context.startActivity(intent);
                        }
                    } else {
                        intent(position);
                    }
                }


            }
        });
    }

    void intent(int position) {
        if (data.get(position).getName().equals("Gallery")) {
            context.startActivity(new Intent(context, GalleryActivity.class));
        } else if (data.get(position).getName().equals("Complaint")) {
            context.startActivity(new Intent(context, ComplaintListActivity.class));
        } else if (data.get(position).getName().equals("Notice")) {
            context.startActivity(new Intent(context, NoticeListActivity.class));
        } else if (data.get(position).getName().equals("Application")) {
            context.startActivity(new Intent(context, ApplicationListActivity.class));
        } else if (data.get(position).getName().equals("Teacher")) {
            context.startActivity(new Intent(context, TeacherFilterListActivity.class));
        } else if (data.get(position).getName().equals("Attendance")) {
            context.startActivity(new Intent(context, TeacherFilterListActivity.class));
        } else if (data.get(position).getName().equals("Events")) {
            context.startActivity(new Intent(context, EventsActivity.class));
        } else if (data.get(position).getName().equals("Broadcast")) {
            context.startActivity(new Intent(context, BroadCastListActivity.class));
        } else if (data.get(position).getName().equals("Discussion")) {
            context.startActivity(new Intent(context, DiscussionListActivity.class));
        } else if (data.get(position).getName().equals("Quiz")) {
            Intent intent = new Intent(context, TeacherQuizDashboardActivity.class);
            context.startActivity(intent);
        } else if (data.get(position).getName().equals("Assign Quiz")) {
            Intent intent = new Intent(context, TeacherQuizDashboardActivity.class);
            context.startActivity(intent);
        } else if (data.get(position).getName().equals("Video")) {
            Intent intent = new Intent(context, AppendedClassListActivity.class);
            intent.putExtra("intent", "VideoCategoryListActivity");
            context.startActivity(intent);
        } else if (data.get(position).getName().equals("Examination")) {
            context.startActivity(new Intent(context, ExaminationTeacherDashboardActivity.class));
        } else if (data.get(position).getName().equals("Std. Material")) {
            Intent intent = new Intent(context, AppendedClassListActivity.class);
            intent.putExtra("intent", "StudyMaterialCategoryListActivity");
            context.startActivity(intent);
        } else if (data.get(position).getName().equals("Prayer")) {
            context.startActivity(new Intent(context, PrayerListActivity.class));
        } else if (data.get(position).getName().equals("Birthday")) {
            context.startActivity(new Intent(context, BirthdayListActivity.class));
        } else {
            Intent intent = new Intent(context, ComingSoonActivity.class);
            intent.putExtra("toolbar", data.get(position).getName());
            context.startActivity(intent);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return data.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout main_layout;
        TextView tittle;
        ImageView icon;

        public ViewHolder(View itemView) {
            super(itemView);
            tittle = itemView.findViewById(R.id.tittle);
            icon = itemView.findViewById(R.id.icon);
            main_layout = itemView.findViewById(R.id.main_layout);

        }
    }


//

//
}