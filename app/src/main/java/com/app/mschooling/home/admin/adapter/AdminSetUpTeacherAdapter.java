package com.app.mschooling.home.admin.adapter;

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
import com.app.mschooling.other.activity.ComingSoonActivity;
import com.app.mschooling.paid.activity.PaidActivity;
import com.app.mschooling.other.activity.SalaryListActivity;
import com.app.mschooling.attendance.teacher.activity.TeacherAbsenceListActivity;
import com.app.mschooling.teachers.filter.activity.TeacherFilterListActivity;
import com.app.mschooling.add_teacher.AddTeacher1;
import com.app.mschooling.attendance.teacher.activity.MarkTeachersAttendanceActivity;
import com.app.mschooling.com.BuildConfig;
import com.app.mschooling.com.R;
import com.app.mschooling.utils.AdmobHelper;
import com.app.mschooling.utils.Helper;
import com.app.mschooling.utils.ParameterConstant;
import com.app.mschooling.utils.Preferences;
import com.mschooling.transaction.common.Menu;

import java.util.List;

public class AdminSetUpTeacherAdapter extends RecyclerView.Adapter<AdminSetUpTeacherAdapter.ViewHolder> {

    private Activity context;
    private List<Menu>  data;


    public AdminSetUpTeacherAdapter(Activity context, List<Menu> data) {

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
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        if (Preferences.getInstance(context).getLanguage().equals("en")){
            viewHolder.tittle.setText(data.get(position).getDisplayNameEn());
        }else {
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

        if (data.get(position).getName().equals("Add Teacher")){
            viewHolder.icon.setImageDrawable(context.getResources().getDrawable(R.drawable.teacher));
        }else if (data.get(position).getName().equals("Teacher List")){
            viewHolder.icon.setImageDrawable(context.getResources().getDrawable(R.drawable.teacher_list));
        }else if (data.get(position).getName().equals("Salary")){
            viewHolder.icon.setImageDrawable(context.getResources().getDrawable(R.drawable.wallet));
        } else if (data.get(position).getName().equals("Attendance")) {
            viewHolder.icon.setImageDrawable(context.getResources().getDrawable(R.drawable.attendance));
        }else if (data.get(position).getName().equals("Absence")) {
            viewHolder.icon.setImageDrawable(context.getResources().getDrawable(R.drawable.absent));
        }else if (data.get(position).getName().contains("ICard")) {
            viewHolder.icon.setImageDrawable(context.getResources().getDrawable(R.drawable.id_card));
        }

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

    void  intent(int position){
        if (data.get(position).getName().equals("Add Teacher")){
            context.startActivity(new Intent(context, AddTeacher1.class));
        }else if (data.get(position).getName().equals("Teacher List")){
            context.startActivity(new Intent(context, TeacherFilterListActivity.class));
        }else if (data.get(position).getName().equals("Attendance")){
            context.startActivity(new Intent(context, MarkTeachersAttendanceActivity.class));
        } else if (data.get(position).getName().equals("Salary")) {
            Intent intent = new Intent(context, SalaryListActivity.class);
            intent.putExtra("toolbar", "Salary");
            context.startActivity(intent);
        }else if (data.get(position).getName().equals("Absence")) {
            Intent intent = new Intent(context, TeacherAbsenceListActivity.class);
            context.startActivity(intent);
        }else {
            Intent intent=new Intent(context, ComingSoonActivity.class);
            intent.putExtra("toolbar",data.get(position).getName());
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