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
import com.app.mschooling.class_section_subject.activity.ClassListActivity;
import com.app.mschooling.class_section_subject.activity.ClassSectionSelectionActivity;
import com.app.mschooling.paid.activity.PaidActivity;
import com.app.mschooling.other.activity.QRCodeActivity;
import com.app.mschooling.class_section_subject.activity.ClassSectionListActivity;
import com.app.mschooling.class_section_subject.activity.ClassSubjectListActivity;
import com.app.mschooling.syllabus.activity.SelectClassSubjectActivity;
import com.app.mschooling.com.BuildConfig;
import com.app.mschooling.com.R;
import com.app.mschooling.utils.AdmobHelper;
import com.app.mschooling.utils.Helper;
import com.app.mschooling.utils.ParameterConstant;
import com.app.mschooling.utils.Preferences;
import com.mschooling.transaction.common.Menu;

import java.util.List;

public class AdminSetUpHomeAdapter extends RecyclerView.Adapter<AdminSetUpHomeAdapter.ViewHolder> {

    private Activity context;
    private List<Menu>  data;


    public AdminSetUpHomeAdapter(Activity context,List<Menu> menuList) {

        this.context = context;
        this.data = menuList;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_home_fragment, viewGroup, false);
        return new ViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, @SuppressLint("RecyclerView") int position) {
        if (Preferences.getInstance(context).getLanguage().equals("en")){
            viewHolder.tittle.setText(data.get(position).getDisplayNameEn());
        }else {
            viewHolder.tittle.setText(data.get(position).getDisplayNameHi());
        }


        if (data.get(position).getName().equals("Class")) {
            viewHolder.icon.setImageDrawable(context.getResources().getDrawable(R.drawable.clas));
        } else if (data.get(position).getName().equals("Subject")) {
            viewHolder.icon.setImageDrawable(context.getResources().getDrawable(R.drawable.subject));
        } else if (data.get(position).getName().equals("QR Code")) {
            viewHolder.icon.setImageDrawable(context.getResources().getDrawable(R.drawable.qr_code));
        }else if (data.get(position).getName().contains("Time")) {
            viewHolder.icon.setImageDrawable(context.getResources().getDrawable(R.drawable.timetable));
        }else if (data.get(position).getName().equals("Syllabus")) {
            viewHolder.icon.setImageDrawable(context.getResources().getDrawable(R.drawable.syllabus));
        }else if (data.get(position).getName().equals("Fee")) {
            viewHolder.icon.setImageDrawable(context.getResources().getDrawable(R.drawable.fee));
        }

//        Helper.setTextGradient(context,viewHolder.tittle);

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

    void intent(int position){
        if (data.get(position).getName().equals("Class")) {
            context.startActivity(new Intent(context, ClassSectionListActivity.class));
        } else if (data.get(position).getName().equals("Subject")) {
            context.startActivity(new Intent(context, ClassSubjectListActivity.class));
        }  else if (data.get(position).getName().contains("QR")) {
            context.startActivity(new Intent(context, QRCodeActivity.class));
        }else if (data.get(position).getName().contains("Time")) {
            Intent intent=new Intent(context, ClassSectionSelectionActivity.class);
            intent.putExtra("intent","AddTimeTableActivity");
            context.startActivity(intent);
        }else if (data.get(position).getName().equals("Syllabus")) {
            Intent intent = new Intent(context, SelectClassSubjectActivity.class);
            context.startActivity(intent);
        }else if (data.get(position).getName().equals("Fee")) {
            Intent intent=new Intent(context, ClassListActivity.class);
            intent.putExtra("intent","FeeListActivity");
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