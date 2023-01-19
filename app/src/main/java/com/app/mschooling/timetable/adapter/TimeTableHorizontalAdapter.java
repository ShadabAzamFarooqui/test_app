package com.app.mschooling.timetable.adapter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.mschooling.base.activity.BaseActivity;
import com.app.mschooling.class_section_subject.adapter.SubjectListDialogPlusAdapter;
import com.app.mschooling.com.R;
import com.app.mschooling.teachers.list.adapter.TeacherListDialogAdapter;
import com.app.mschooling.teachers.list.adapter.TeacherListDialogPlusAdapter;
import com.app.mschooling.timetable.activity.AddTimeTableActivity;
import com.app.mschooling.utils.AppUser;
import com.app.mschooling.utils.ParameterConstant;
import com.mschooling.transaction.common.Allocation;
import com.mschooling.transaction.common.TimeTableRow;
import com.mschooling.transaction.filter.SubjectCriteria;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.DialogPlusBuilder;
import com.orhanobut.dialogplus.GridHolder;
import com.orhanobut.dialogplus.OnClickListener;
import com.orhanobut.dialogplus.OnItemClickListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class TimeTableHorizontalAdapter extends RecyclerView.Adapter<TimeTableHorizontalAdapter.ViewHolder> {

    AddTimeTableActivity context;
    List<TimeTableRow> response;
    public static List<TimeTableRow> deletedRowList;
    Dialog dialog;
    public String day;
    public Map<Integer, String> map;
    public int mainPosition;
    Integer hour;
    Integer minute;

    public static int groupPosition;
    public static int childPosition;
    boolean isStudent;


    public TimeTableHorizontalAdapter(AddTimeTableActivity context, boolean isStudent, List<TimeTableRow> response, int position) {
        this.context = context;
        this.response = response;
        map = new HashMap<>();
        this.mainPosition = position;
        this.isStudent = isStudent;
        deletedRowList = new ArrayList<>();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_time_table3, viewGroup, false);
        return new ViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, @SuppressLint("RecyclerView") int position) {


        if (isStudent == true) {
            viewHolder.remove.setVisibility(View.GONE);
        } else {
           /* if (mainPosition != 0) {
                if (position == 6) {
                    viewHolder.remove.setVisibility(View.VISIBLE);
                } else {
                    viewHolder.remove.setVisibility(View.GONE);
                }
            } else {
                viewHolder.remove.setVisibility(View.GONE);
            }*/

            if (position == 6) {
                viewHolder.remove.setVisibility(View.VISIBLE);
            } else {
                viewHolder.remove.setVisibility(View.GONE);
            }
        }

        viewHolder.subject.setText(response.get(mainPosition).getTimeTables().get(position).getSubjectName());
        viewHolder.teacher.setText(response.get(mainPosition).getTimeTables().get(position).getTeacherName());

        viewHolder.remove.setOnClickListener(v -> {
            if (!isStudent) {
                deleteDialog();
            }
        });

        viewHolder.subject.setOnClickListener(v -> {
            if (!isStudent) {
                showSubjectDialog(mainPosition, position);
            }
        });

        viewHolder.teacher.setOnClickListener(v -> {
            if (!isStudent) {
                if (response.get(mainPosition).getTimeslot().getStart() == null ||
                        response.get(mainPosition).getTimeslot().getEnd() == null) {
                    context.dialogError("Please select the time of this period");
                    return;
                } else if (response.get(mainPosition).getTimeslot().getStart().equals("From") ||
                        response.get(mainPosition).getTimeslot().getEnd().equals("To")) {
                    context.dialogError("Please select the time of this period");
                    return;
                }
                if (viewHolder.subject.getText().toString().trim().isEmpty()) {
                    context.dialogError("Please select the subject");
                    return;
                }
                showTeacherDialog(mainPosition, position);
            }
        });

    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return response.get(mainPosition).getTimeTables().size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        LinearLayout remove;
        TextView subject;
        TextView teacher;

        public ViewHolder(View itemView) {
            super(itemView);
            remove = itemView.findViewById(R.id.remove);
            subject = itemView.findViewById(R.id.subject);
            teacher = itemView.findViewById(R.id.teacher);
        }

    }

    public void deleteDialog() {

        Dialog dialog = new Dialog(context);
        dialog.getWindow().requestFeature(1);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        dialog.setContentView(R.layout.dialog_delete_notice);
        dialog.setCancelable(false);
        TextView delete =  dialog.findViewById(R.id.delete);
        TextView cancel =  dialog.findViewById(R.id.cancel);
        cancel.setOnClickListener(view -> dialog.dismiss());
        delete.setOnClickListener(v -> {
            deletedRowList.add(response.get(mainPosition));
            AddTimeTableActivity.deletedRowList.add(response.get(mainPosition));
            response.remove(mainPosition);
            AddTimeTableActivity.adapter.notifyDataSetChanged();
            Toast.makeText(context, "Deleted", Toast.LENGTH_SHORT).show();
            updateTeacherList();
            dialog.dismiss();
        });
        dialog.show();
    }


    public void showSubjectDialog(int groupPosition, int childPosition) {

        if (AppUser.getInstance().getGetSubjectResponse() == null) {
            TimeTableHorizontalAdapter.groupPosition = groupPosition;
            TimeTableHorizontalAdapter.childPosition = childPosition;

            SubjectCriteria criteria = new SubjectCriteria();
            criteria.setClassId(AddTimeTableActivity.classId);

            BaseActivity.context.apiCallBack(BaseActivity.context.getApiCommonController().getSubjectList(ParameterConstant.getRole(context),
                    AddTimeTableActivity.classId, null));
        } else {

            DialogPlus dialogPlus = null;
            DialogPlusBuilder dialogPlusBuilder = DialogPlus.newDialog(context);
            dialogPlusBuilder.setHeader(R.layout.subject_header);
            dialogPlusBuilder.setContentHolder(new GridHolder(1));
            dialogPlusBuilder.setGravity(Gravity.BOTTOM);
            dialogPlusBuilder.setCancelable(true);

            dialogPlusBuilder.setMargin(5, 300, 5, 10);
            dialogPlusBuilder.setPadding(0, 0, 0, 10);
            dialogPlusBuilder.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(DialogPlus dialog, View view) {
                    Log.e(" inside", " dialog is dismiss issssssssssssssss");
                    view.findViewById(R.id.footer_close_button);
                    dialog.dismiss();
                }
            });

            dialogPlusBuilder.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(DialogPlus dialog, Object item, View view, int position) {

                }
            });
            dialogPlusBuilder.setFooter(R.layout.footer);
            dialogPlusBuilder.setExpanded(false); // This will enable the expand feature, (similar to android L share dialog)
            dialogPlusBuilder.setAdapter(new SubjectListDialogPlusAdapter(context, AppUser.getInstance().getGetSubjectResponse().getStandaloneSubjectResponses(), groupPosition, childPosition));
            dialogPlus = dialogPlusBuilder.create();
            dialogPlus.show();
            AddTimeTableActivity.dialogPlus = dialogPlus;
        }

    }


    public void showTeacherDialog(int groupPosition, int childPosition) {
        if (AppUser.getInstance().getAllocationResponseList() == null) {
            TimeTableHorizontalAdapter.groupPosition = groupPosition;
            TimeTableHorizontalAdapter.childPosition = childPosition;
            BaseActivity.context.apiCallBack(BaseActivity.context.getApiCommonController().getAllocation(null));
        } else {
            dialog = new Dialog(context, android.R.style.Theme_Light);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.subject_selection_dialog);
            dialog.setCancelable(true);
            RecyclerView recyclerView = dialog.findViewById(R.id.recyclerView);
            LinearLayout close = dialog.findViewById(R.id.close);
            TextView title = dialog.findViewById(R.id.title);
            title.setText(context.getString(R.string.select_teacher));
            recyclerView.setHasFixedSize(true);
            recyclerView.setFocusable(false);
            recyclerView.setNestedScrollingEnabled(false);
            recyclerView.setLayoutManager(new GridLayoutManager(context, 1));
            recyclerView.setAdapter(new TeacherListDialogAdapter(context,
                    AppUser.getInstance().getAllocationResponseList().getAllocationResponses(),
                    groupPosition, childPosition, dialog));
            close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });
            dialog.show();





//            DialogPlus dialogPlus = null;
//            DialogPlusBuilder dialogPlusBuilder = DialogPlus.newDialog(context);
//            dialogPlusBuilder.setHeader(R.layout.teacher_timetable_header);
//            dialogPlusBuilder.setContentHolder(new GridHolder(1));
//            dialogPlusBuilder.setGravity(Gravity.BOTTOM);
//            dialogPlusBuilder.setCancelable(true);
//
//            dialogPlusBuilder.setMargin(5, 300, 5, 10);
//            dialogPlusBuilder.setPadding(0, 0, 0, 10);
//            dialogPlusBuilder.setOnClickListener(new OnClickListener() {
//                @Override
//                public void onClick(DialogPlus dialog, View view) {
//                    Log.e(" inside", " dialog is dismiss issssssssssssssss");
//                    view.findViewById(R.id.footer_close_button);
//                    dialog.dismiss();
//                }
//            });
//
//            dialogPlusBuilder.setOnItemClickListener(new OnItemClickListener() {
//                @Override
//                public void onItemClick(DialogPlus dialog, Object item, View view, int position) {
//
//                }
//            });
//            dialogPlusBuilder.setFooter(R.layout.footer);
//            dialogPlusBuilder.setExpanded(false); // This will enable the expand feature, (similar to android L share dialog)
//            dialogPlusBuilder.setAdapter(new TeacherListDialogPlusAdapter(context, AppUser.getInstance().getAllocationResponseList().getAllocationResponses(), groupPosition, childPosition));
//            dialogPlus = dialogPlusBuilder.create();
//            dialogPlus.show();
//            AddTimeTableActivity.dialogPlus = dialogPlus;
        }
    }


    void updateTeacherList() {

        for (int a = 0; a < deletedRowList.size(); a++) {
            try {
                for (int i = 0; i < TeacherListDialogAdapter.responseList.size(); i++) {
                    List<Allocation> allocationList = TeacherListDialogAdapter.responseList.get(i).getAllocations();
                    for (int j = 0; j < allocationList.size(); j++) {
                        for (int k = 0; k < deletedRowList.get(a).getTimeTables().size(); k++) {
                            if (deletedRowList.get(a).getTimeTables().get(k).getDay() != null && deletedRowList.get(a).getTimeslot() != null) {
                                try {
                                    if (allocationList.get(j).getDay().value().equals(deletedRowList.get(a).getTimeTables().get(k).getDay().value())
                                            && allocationList.get(j).getTimeslot().getStart().equals(deletedRowList.get(a).getTimeslot().getStart())
                                            && allocationList.get(j).getTimeslot().getEnd().equals(deletedRowList.get(a).getTimeslot().getEnd())
//                        && allocationList.get(j).getClassId().equals(deletedRowList.get(a).getTimeTables().get(j).getClassId())
//                        && allocationList.get(j).getSectionId().equals(deletedRowList.get(a).getTimeTables().get(j).getSectionId())
                                            && allocationList.get(j).getSubjectId().equals(deletedRowList.get(a).getTimeTables().get(k).getSubjectId())
                                            && TeacherListDialogAdapter.responseList.get(i).getEnrollmentId().equals(deletedRowList.get(a).getTimeTables().get(k).getTeacherId())) {

                                        allocationList.remove(j);

                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                }
            } catch (Exception e) {

            }

        }
    }


    /*void updateTeacherList(String subjectName,String subjectId, String day) {
        try {
            for (int i = 0; i < TeacherListDialogAdapter.responseList.size(); i++) {
                List<Allocation> allocationList=TeacherListDialogAdapter.responseList.get(i).getAllocations();
                for (int j = 0; j < allocationList.size(); j++) {
                    if (allocationList.get(j).getDay().value().equals(day)
                            && allocationList.get(j).getTimeslot().getStart().
                            equals(AddTimeTableAdapter.timeTableList.get(TimeTableHorizontalAdapter.groupPosition).getTimeslot().getStart())
                            && allocationList.get(j).getTimeslot().getEnd().
                            equals(AddTimeTableAdapter.timeTableList.get(TimeTableHorizontalAdapter.groupPosition).getTimeslot().getEnd())) {
                        Allocation allocation=allocationList.get(j);
                        allocation.setSubjectName(subjectName);
                        allocation.setSubjectId(subjectId);
                    }
                }
            }
        }catch (Exception e){

        }

    }
*/

}