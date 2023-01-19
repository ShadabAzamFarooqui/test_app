package com.app.mschooling.home.student.adapter;

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
import com.app.mschooling.timetable.activity.AddTimeTableActivity;
import com.app.mschooling.application.activity.ApplicationListActivity;
import com.app.mschooling.broadcast.activity.BroadCastListActivity;
import com.app.mschooling.other.activity.ComingSoonActivity;
import com.app.mschooling.complaint.activity.ComplaintListActivity;
import com.app.mschooling.event.activity.EventsActivity;
import com.app.mschooling.gallery.activity.GalleryActivity;
import com.app.mschooling.notice.activity.NoticeListActivity;
import com.app.mschooling.paid.activity.Paid2Activity;
import com.app.mschooling.prayer.activity.PrayerListActivity;
import com.app.mschooling.other.activity.PrintIdCardActivity;
import com.app.mschooling.class_section_subject.activity.SubjectListActivity;
import com.app.mschooling.examination.activity.ExaminationListActivity;
import com.app.mschooling.fee.activity.student.StudentFeeDetailActivity;
import com.app.mschooling.attendance.student.activity.StudentAttendanceDetailActivity;
import com.app.mschooling.teachers.list.activity.TeacherListForStudentActivity;
import com.app.mschooling.study_material.activity.StudyMaterialCategoryListActivity;
import com.app.mschooling.utils.AdmobHelper;
import com.app.mschooling.video.activity.VideoCategoryListActivity;
import com.app.mschooling.com.BuildConfig;
import com.app.mschooling.com.R;
import com.app.mschooling.meeting.activity.DiscussionListActivity;
import com.app.mschooling.utils.Helper;
import com.app.mschooling.utils.ParameterConstant;
import com.app.mschooling.utils.Preferences;
//import com.app.mschooling.zoomsdk.activity.onlineclasses.student.StudentLiveClassListActivity;
import com.mschooling.transaction.common.Menu;
import com.mschooling.transaction.common.user.StudentSetup;
import com.mschooling.transaction.response.configuration.ConfigurationResponse;

import java.util.HashMap;
import java.util.List;

public class StudentHomeAdapter extends RecyclerView.Adapter<StudentHomeAdapter.ViewHolder> {

    private Activity context;
    private List<Menu> menuList;

    public StudentHomeAdapter(Activity context, List<Menu> menuList) {
        this.context = context;
        this.menuList = menuList;
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
            viewHolder.tittle.setText(menuList.get(position).getDisplayNameEn());
        } else {
            viewHolder.tittle.setText(menuList.get(position).getDisplayNameHi());
        }

        if (menuList.get(position).getName().equals("Report")) {
            viewHolder.icon.setImageResource(R.drawable.report);
        } else if (menuList.get(position).getName().equals("Examination")) {
            viewHolder.icon.setImageResource(R.drawable.examination);
        } else if (menuList.get(position).getName().equals("Gallery")) {
            viewHolder.icon.setImageResource(R.drawable.gallary);
        } else if (menuList.get(position).getName().equals("Complaint")) {
            viewHolder.icon.setImageResource(R.drawable.complaint);
        } else if (menuList.get(position).getName().equals("Notice")) {
            viewHolder.icon.setImageResource(R.drawable.notice);
        } else if (menuList.get(position).getName().equals("Application")) {
            viewHolder.icon.setImageResource(R.drawable.inbox);
        } else if (menuList.get(position).getName().equals("Attendance")) {
            viewHolder.icon.setImageResource(R.drawable.attendance);
        } else if (menuList.get(position).getName().equals("Broadcast")) {
            viewHolder.icon.setImageResource(R.drawable.broad_cast);
        } else if (menuList.get(position).getName().equals("Teachers")) {
            viewHolder.icon.setImageResource(R.drawable.teacher);
        } else if (menuList.get(position).getName().equals("Syllabus")) {
            viewHolder.icon.setImageResource(R.drawable.syllabus);
        } else if (menuList.get(position).getName().equals("Homework")) {
            viewHolder.icon.setImageResource(R.drawable.home_work);
        } else if (menuList.get(position).getName().equals("Events")) {
            viewHolder.icon.setImageResource(R.drawable.events);
        } else if (menuList.get(position).getName().equals("Quiz")) {
            viewHolder.icon.setImageResource(R.drawable.quiz);
        } else if (menuList.get(position).getName().equals("Timetable")) {
            viewHolder.icon.setImageResource(R.drawable.timetable);
        } else if (menuList.get(position).getName().equals("Live")) {
            viewHolder.icon.setImageResource(R.drawable.meeting);
        } else if (menuList.get(position).getName().equals("Fee")) {
            viewHolder.icon.setImageResource(R.drawable.fee);
        } else if (menuList.get(position).getName().equals("Discussion")) {
            viewHolder.icon.setImageResource(R.drawable.meeting);
        } else if (menuList.get(position).getName().equals("ICard")) {
            viewHolder.icon.setImageResource(R.drawable.id_card);
        } else if (menuList.get(position).getName().equals("Std. Material")) {
            viewHolder.icon.setImageResource(R.drawable.e_doc);
        } else if (menuList.get(position).getName().equals("Video")) {
            viewHolder.icon.setImageDrawable(context.getResources().getDrawable(R.drawable.youtube));
        }else if (menuList.get(position).getName().equals("Prayer")) {
            viewHolder.icon.setImageDrawable(context.getResources().getDrawable(R.drawable.prayer));
        }else if (menuList.get(position).getName().equals("Birthday")) {
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
                }else {
                    if (menuList.get(position).isPaid()) {
                        if (Preferences.getInstance(context).isPaid()) {
                            intent(position);
                        } else {
                            Intent intent = new Intent(context, Paid2Activity.class);
                            intent.putExtra("toolbar", menuList.get(position).getName());
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
        if (menuList.get(position).getName().equals("Gallery")) {
            context.startActivity(new Intent(context, GalleryActivity.class));
        } else if (menuList.get(position).getName().equals("Complaint")) {
            context.startActivity(new Intent(context, ComplaintListActivity.class));
        } else if (menuList.get(position).getName().equals("Notice")) {
            context.startActivity(new Intent(context, NoticeListActivity.class));
        } else if (menuList.get(position).getName().equals("Application")) {
            context.startActivity(new Intent(context, ApplicationListActivity.class));
        } else if (menuList.get(position).getName().equals("Teachers")) {
            context.startActivity(new Intent(context, TeacherListForStudentActivity.class));
        } else if (menuList.get(position).getName().equals("Attendance")) {
            if (ParameterConstant.isAttendanceModeSubjectWise(context)){
                Intent intent=new Intent(context, SubjectListActivity.class);
                intent.putExtra("intent","StudentAttendanceDetailActivity");
                context.startActivity(intent);
            }else {
                context.startActivity(new Intent(context, StudentAttendanceDetailActivity.class));
            }

        } else if (menuList.get(position).getName().equals("Events")) {
            context.startActivity(new Intent(context, EventsActivity.class));
        } else if (menuList.get(position).getName().equals("Syllabus")) {
            Intent intent = new Intent(context, SubjectListActivity.class);
            intent.putExtra("intent", "SyllabusListActivity");
            context.startActivity(intent);
        } else if (menuList.get(position).getName().equals("Timetable")) {
            ConfigurationResponse configuration=Preferences.getInstance(context).getConfiguration();
            Intent intent = new Intent(context, AddTimeTableActivity.class);
            intent.putExtra("classId", configuration.getStudentSetup().getClassId());
            intent.putExtra("className", configuration.getStudentSetup().getClassName());
            intent.putExtra("sectionId", configuration.getStudentSetup().getSectionId());
            intent.putExtra("sectionName", configuration.getStudentSetup().getSectionName());
            intent.putExtra("student", "false");
            context.startActivity(intent);

        } else if (menuList.get(position).getName().equals("Live")) {
//            context.startActivity(new Intent(context, StudentLiveClassListActivity.class));
        } else if (menuList.get(position).getName().equals("Fee")) {
            Intent intent = new Intent(context, StudentFeeDetailActivity.class);
            context.startActivity(intent);
        } else if (menuList.get(position).getName().equals("Broadcast")) {
            context.startActivity(new Intent(context, BroadCastListActivity.class));
        } else if (menuList.get(position).getName().equals("Quiz")) {
            Intent intent = new Intent(context, SubjectListActivity.class);
            StudentSetup studentSetup = Preferences.getInstance(context).getConfiguration().getStudentSetup();
            intent.putExtra("classId", studentSetup.getClassId());
            intent.putExtra("className", studentSetup.getClassName());
            intent.putExtra("sectionId", studentSetup.getSectionId());
            intent.putExtra("sectionName", studentSetup.getSectionName());
            intent.putExtra("intent", "StudentQuizListActivity");
            context.startActivity(intent);
        } else if (menuList.get(position).getName().equals("Discussion")) {
            context.startActivity(new Intent(context, DiscussionListActivity.class));
        } else if (menuList.get(position).getName().equals("ICard")) {
            context.startActivity(new Intent(context, PrintIdCardActivity.class));
        } else if (menuList.get(position).getName().equals("Video")) {
            context.startActivity(new Intent(context, VideoCategoryListActivity.class));
        } else if (menuList.get(position).getName().equals("Std. Material")) {
            context.startActivity(new Intent(context, StudyMaterialCategoryListActivity.class));
        } else if (menuList.get(position).getName().equals("Examination")) {
            context.startActivity(new Intent(context, ExaminationListActivity.class));
        } else if (menuList.get(position).getName().equals("Prayer")) {
            context.startActivity(new Intent(context, PrayerListActivity.class));
        }else if (menuList.get(position).getName().equals("Birthday")) {
            context.startActivity(new Intent(context, BirthdayListActivity.class));
        } else {
            Intent intent = new Intent(context, ComingSoonActivity.class);
            intent.putExtra("toolbar", menuList.get(position).getName());
            context.startActivity(intent);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return menuList.size();
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
}