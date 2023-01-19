package com.app.mschooling.home.admin.adapter;

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
import com.app.mschooling.setting.activity.AddSettingActivity;
import com.app.mschooling.class_section_subject.activity.ClassListActivity;
import com.app.mschooling.class_section_subject.activity.ClassSectionSelectionActivity;
import com.app.mschooling.class_section_subject.activity.ClassSectionSubjectSelectionActivity;
import com.app.mschooling.paid.activity.PaidActivity;
import com.app.mschooling.students.filter.activity.StudentFilterActivity;
import com.app.mschooling.add_student.AddStudent1;
import com.app.mschooling.syllabus.activity.SelectClassSubjectActivity;
import com.app.mschooling.com.BuildConfig;
import com.app.mschooling.com.R;
import com.app.mschooling.utils.AdmobHelper;
import com.app.mschooling.utils.Helper;
import com.app.mschooling.utils.ParameterConstant;
import com.app.mschooling.utils.Preferences;
import com.mschooling.transaction.common.Menu;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AdminSetUpStudentAdapter extends RecyclerView.Adapter<AdminSetUpStudentAdapter.ViewHolder> {

    private Activity context;
    private List<Menu> data;


    public AdminSetUpStudentAdapter(Activity context, List<Menu> data) {

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
        viewHolder.icon.setImageDrawable(context.getResources().getDrawable(R.drawable.administration));
//        Random rnd = new Random();
//        int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));

        String first = String.valueOf(Helper.getAlphabet().get(position));
        ColorGenerator generator = ColorGenerator.MATERIAL;
        int color = generator.getColor(first);
        viewHolder.icon.setColorFilter(color, android.graphics.PorterDuff.Mode.SRC_IN);

//        Helper.setTextGradient(context,viewHolder.tittle);

        if (data.get(position).getName().equals("Add Student")) {
            viewHolder.icon.setImageDrawable(context.getResources().getDrawable(R.drawable.add_student));
        } else if (data.get(position).getName().equals("Student List")) {
            viewHolder.icon.setImageDrawable(context.getResources().getDrawable(R.drawable.student_list));
        } else if (data.get(position).getName().equals("Attendance")) {
            viewHolder.icon.setImageDrawable(context.getResources().getDrawable(R.drawable.attendance));
        } else if (data.get(position).getName().equals("Homework")) {
            viewHolder.icon.setImageDrawable(context.getResources().getDrawable(R.drawable.home_work));
        } else if (data.get(position).getName().equals("Fee")) {
            viewHolder.icon.setImageDrawable(context.getResources().getDrawable(R.drawable.fee));
        } else if (data.get(position).getName().equals("Complaint/Sugg.")) {
            viewHolder.icon.setImageDrawable(context.getResources().getDrawable(R.drawable.suggession));
        } else if (data.get(position).getName().contains("ICard")) {
            viewHolder.icon.setImageDrawable(context.getResources().getDrawable(R.drawable.id_card));
        }

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
                            Intent intent = new Intent(context, PaidActivity.class);
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
        }
        if (data.get(position).getName().equals("Student List")) {
            context.startActivity(new Intent(context, StudentFilterActivity.class));
        }
        if (data.get(position).getName().equals("Attendance")) {
            if (Preferences.getInstance(context).getAttendanceMode().equals("")) {
                Intent intent=new Intent(context, AddSettingActivity.class);
                intent.putExtra("displayName", "ATTENDANCE MODE");
                intent.putExtra("name", "ATTENDANCE_MODE");
                intent.putExtra("value", "Class Wise");
                intent.putStringArrayListExtra("data", new ArrayList(Arrays.asList("Class Wise", "Subject Wise")));
                context.startActivity(intent);
            } else if (ParameterConstant.isAttendanceModeSubjectWise(context)) {
                Intent intent = new Intent(context, ClassSectionSubjectSelectionActivity.class);
                intent.putExtra("intent", "ShowAttendanceActivity");
                context.startActivity(intent);
            } else {

                Intent intent = new Intent(context, ClassSectionSelectionActivity.class);
                intent.putExtra("intent", "ShowStudentAttendanceActivity");
                context.startActivity(intent);
            }
        }
        if (data.get(position).getName().equals("Homework")) {
            Intent intent = new Intent(context, ClassSectionSubjectSelectionActivity.class);
            intent.putExtra("intent", "HomeWorkListActivity");
            context.startActivity(intent);
        }
        if (data.get(position).getName().equals("Fee")) {
            Intent intent = new Intent(context, ClassSectionSelectionActivity.class);
            intent.putExtra("intent", "StudentFeeListActivity");
            context.startActivity(intent);
        }
        if (data.get(position).getName().equals("Syllabus")) {
            Intent intent = new Intent(context, SelectClassSubjectActivity.class);
            context.startActivity(intent);
        }
        if (data.get(position).getName().contains("ICard")) {
            Intent intent = new Intent(context, ClassSectionSelectionActivity.class);
            intent.putExtra("intent","IdCardListActivity");
            context.startActivity(intent);
//                    Intent intent=new Intent(context, ClassListActivity.class);
//                    intent.putExtra("intent","IdCardListActivity");
//                    context.startActivity(intent);
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