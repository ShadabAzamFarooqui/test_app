package com.app.mschooling.examination.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Build;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.app.mschooling.com.R;
import com.mschooling.transaction.common.StudentExamination;

import java.util.List;
import java.util.Map;

public class AddStudentMarksAdapter extends RecyclerView.Adapter<AddStudentMarksAdapter.ViewHolder> {

    Activity context;
    List<StudentExamination> responseList;
    Integer totalMark;
    RecyclerView recyclerView;

    public AddStudentMarksAdapter(Activity context, String totalMark, List<StudentExamination> data, RecyclerView recyclerView) {
        this.context = context;
        this.responseList = data;
        this.totalMark = Integer.valueOf(totalMark);
        this.recyclerView = recyclerView;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_ad_examination_list, viewGroup, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        responseList.get(position).setMaximumMarks(totalMark);
        viewHolder.name.setText(responseList.get(position).getfName() + " " + responseList.get(position).getlName());
        viewHolder.enrollmentId.setText(responseList.get(position).getEnrollmentId());
        viewHolder.rollNumber.setText(responseList.get(position).getRollNumber());

        if (responseList.get(position).getObtainTheoryMarks() > -1) {
            viewHolder.theory.setText("" + responseList.get(position).getObtainTheoryMarks());
        } else {
            viewHolder.theory.setText("");
        }

        if (responseList.get(position).getObtainPracticalMarks() > -1) {
            viewHolder.practical.setText("" + responseList.get(position).getObtainPracticalMarks());
        } else {
            viewHolder.practical.setText("");
        }


        editTextListener(viewHolder.theory, position, "t");
        editTextListener(viewHolder.practical, position, "p");


    }

    @SuppressLint("NotifyDataSetChanged")
    void editTextListener(EditText editText, int position, String type) {
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (type.equals("t")) {
                    try {
                        responseList.get(position).setObtainTheoryMarks(Double.valueOf(s.toString()));
                        double obtainedTotal =( responseList.get(position).getObtainPracticalMarks() + responseList.get(position).getObtainTheoryMarks());
                        if (obtainedTotal > totalMark) {
                            Toast.makeText(context, context.getString(R.string.obtained_mark_cant_be), Toast.LENGTH_SHORT).show();
                            responseList.get(position).setObtainTheoryMarks(-1);
                            notifyListener();
                        } else {
                            responseList.get(position).setObtainTheoryMarks(Double.valueOf(s.toString()));
                        }
                    } catch (Exception e) {
                        responseList.get(position).setObtainTheoryMarks(-1);
                    }


                } else {
                    try {
                        responseList.get(position).setObtainPracticalMarks(Double.valueOf(s.toString()));
                        double obtainedTotal =( responseList.get(position).getObtainPracticalMarks() + responseList.get(position).getObtainTheoryMarks());
                        if (obtainedTotal > totalMark) {
                            Toast.makeText(context, context.getString(R.string.obtained_mark_cant_be), Toast.LENGTH_SHORT).show();
                            responseList.get(position).setObtainPracticalMarks(-1);
                            notifyListener();
                        } else {
                            responseList.get(position).setObtainPracticalMarks(Double.valueOf(s.toString()));
                        }
                    } catch (Exception e) {
                        responseList.get(position).setObtainPracticalMarks(-1);
                    }

                }
            }

            @Override
            public void afterTextChanged(Editable s) {


            }
        });
    }


    @Override
    public int getItemCount() {
        return responseList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, enrollmentId, rollNumber;
        EditText theory, practical;
        LinearLayout mainLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            enrollmentId = itemView.findViewById(R.id.enrollmentId);
            rollNumber = itemView.findViewById(R.id.rollNumber);
            mainLayout = itemView.findViewById(R.id.mainLayout);
            theory = itemView.findViewById(R.id.theory);
            practical = itemView.findViewById(R.id.practical);
        }
    }


    @Override
    public int getItemViewType(int position) {
        return position;
    }

    void  notifyListener(){
        context.runOnUiThread(new Runnable() {
            @SuppressLint("NotifyDataSetChanged")
            public void run() {
                if (!recyclerView.isComputingLayout()) {
                    notifyDataSetChanged();
                }
            }
        });
    }


}