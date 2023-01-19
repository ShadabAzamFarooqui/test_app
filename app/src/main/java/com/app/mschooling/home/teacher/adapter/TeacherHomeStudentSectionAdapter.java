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
import com.app.mschooling.class_section_subject.activity.ClassListActivity;
import com.app.mschooling.class_section_subject.activity.ClassSectionSelectionActivity;
import com.app.mschooling.class_section_subject.activity.ClassSectionSubjectSelectionActivity;
import com.app.mschooling.complaint.activity.ComplaintListActivity;
import com.app.mschooling.paid.activity.Paid2Activity;
import com.app.mschooling.add_student.AddStudent1;
import com.app.mschooling.com.BuildConfig;
import com.app.mschooling.com.R;
import com.app.mschooling.utils.AdmobHelper;
import com.app.mschooling.utils.Helper;
import com.app.mschooling.utils.ParameterConstant;
import com.app.mschooling.utils.Preferences;
//import com.app.mschooling.zoomsdk.activity.onlineclasses.SetupZoomActivity;
//import com.app.mschooling.zoomsdk.activity.onlineclasses.teacher.LiveClassListActivity;
import com.mschooling.transaction.common.Menu;

import java.util.List;

public class TeacherHomeStudentSectionAdapter extends RecyclerView.Adapter<TeacherHomeStudentSectionAdapter.ViewHolder> {

    private Activity context;
    private List<Menu> data;

    public TeacherHomeStudentSectionAdapter(Activity context, List<Menu> data) {
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

        if (data.get(position).getName().equals("Add Student")) {
            viewHolder.icon.setImageResource(R.drawable.add_student);
        } else if (data.get(position).getName().equals("Student List")) {
            viewHolder.icon.setImageResource(R.drawable.student_list);
        } else if (data.get(position).getName().equals("Fee")) {
            viewHolder.icon.setImageResource(R.drawable.fee);
        } else if (data.get(position).getName().equals("Attendance")) {
            viewHolder.icon.setImageResource(R.drawable.attendance);
        } else if (data.get(position).getName().contains("Comp")) {
            viewHolder.icon.setImageResource(R.drawable.suggession);
        } else if (data.get(position).getName().equals("Homework")) {
            viewHolder.icon.setImageResource(R.drawable.home_work);
        } else if (data.get(position).getName().equals("Live")) {
            viewHolder.icon.setImageResource(R.drawable.meeting);
        } else if (data.get(position).getName().contains("ICard")) {
            viewHolder.icon.setImageDrawable(context.getResources().getDrawable(R.drawable.id_card));
        } else if (data.get(position).getName().equals("Syllabus")) {
            viewHolder.icon.setImageDrawable(context.getResources().getDrawable(R.drawable.syllabus));
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
        if (data.get(position).getName().equals("Add Student")) {
            context.startActivity(new Intent(context, AddStudent1.class));
        } else if (data.get(position).getName().equals("Student List")) {
            Intent intent=new Intent(context, ClassSectionSelectionActivity.class);
            intent.putExtra("intent","StudentsListActivity");
            context.startActivity(intent);
        } else if (data.get(position).getName().equals("Fee")) {
            Intent intent = new Intent(context, ClassSectionSelectionActivity.class);
            intent.putExtra("intent", "StudentFeeListActivity");
            context.startActivity(intent);
        } else if (data.get(position).getName().equals("Attendance")) {

            if (Preferences.getInstance(context).getAttendanceMode().equalsIgnoreCase("")){
                Intent intent=new Intent(context, Paid2Activity.class);
                intent.putExtra("intent","Attendance");
                context.startActivity(intent);
            }else if (ParameterConstant.isAttendanceModeSubjectWise(context)){
                Intent intent=new Intent(context, ClassSectionSubjectSelectionActivity.class);
                intent.putExtra("intent","MarkStudentAttendanceActivity");
                context.startActivity(intent);
            }else {
                Intent intent=new Intent(context, ClassSectionSelectionActivity.class);
                intent.putExtra("intent","MarkStudentAttendanceActivity");
                context.startActivity(intent);
            }

        } else if (data.get(position).getName().contains("Comp")) {
            context.startActivity(new Intent(context, ComplaintListActivity.class));
        } else if (data.get(position).getName().equals("Homework")) {
            Intent intent=new Intent(context, ClassSectionSubjectSelectionActivity.class);
            intent.putExtra("intent","HomeWorkListActivity");
            context.startActivity(intent);
        } else if (data.get(position).getName().contains("ICard")) {
            Intent intent = new Intent(context, ClassSectionSelectionActivity.class);
            intent.putExtra("intent","IdCardListActivity");
            context.startActivity(intent);
        } else if (data.get(position).getName().equals("Syllabus")) {
            Intent intent = new Intent(context, ClassSectionSubjectSelectionActivity.class);
            intent.putExtra("intent","SyllabusListActivity");
            context.startActivity(intent);
        } else if (data.get(position).getName().equals("Live")) {
            if (Preferences.getInstance(context).isZoomSetup()) {
//                context.startActivity(new Intent(context, LiveClassListActivity.class));
            } else {
//                context.startActivity(new Intent(context, SetupZoomActivity.class));
            }
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