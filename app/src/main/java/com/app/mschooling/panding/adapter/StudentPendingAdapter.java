package com.app.mschooling.panding.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.app.mschooling.base.fragment.BaseFragment;
import com.app.mschooling.com.R;
import com.mschooling.transaction.common.api.Common;
import com.mschooling.transaction.response.pending.PendingTaskResponse;

import java.util.ArrayList;
import java.util.List;

public class StudentPendingAdapter extends RecyclerView.Adapter<StudentPendingAdapter.ViewHolder> implements Filterable {

    Activity context;
    BaseFragment instance;
    public static List<PendingTaskResponse> responseList ;
    public static List<PendingTaskResponse>  filteredList;

    public StudentPendingAdapter(Activity context, BaseFragment instance, List<PendingTaskResponse> responseList) {
        this.context = context;
        this.responseList = responseList;
        this.filteredList = responseList;
        this.instance = instance;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_pending_student, viewGroup, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, @SuppressLint("RecyclerView") int position) {
        viewHolder.count.setText(""+(position+1)+". ");
        viewHolder.fullName.setText(responseList.get(position).getFname()+" "+responseList.get(position).getLname());
        viewHolder.firstName.setText(responseList.get(position).getFname());
        viewHolder.lastName.setText(responseList.get(position).getLname());
        viewHolder.mobile.setText(responseList.get(position).getMobile());
        viewHolder.fatherName.setText(responseList.get(position).getFather());
        viewHolder.date.setText(responseList.get(position).getTime());
        viewHolder.gender.setText(responseList.get(position).getGender().value());
        viewHolder.dob.setText(responseList.get(position).getDob());
        if (!responseList.get(position).getEmail().trim().isEmpty()){
            viewHolder.email.setText(responseList.get(position).getEmail());
        }else {
            viewHolder.email.setText("N/A");
        }

        viewHolder.clazz.setText(responseList.get(position).getPendingTaskStudentResponse().getClassName());
        viewHolder.rollNumber.setText(responseList.get(position).getPendingTaskStudentResponse().getRollNumber());
        viewHolder.section.setText(responseList.get(position).getPendingTaskStudentResponse().getSectionName());

        viewHolder.accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BaseFragment.context.dialogAcceptDecline(context.getString(R.string.accept),BaseFragment.context.getApiCommonController().approveDeclineStudent(Common.Actions.APPROVED.value(), responseList.get(position).getId()));

            }
        });


        viewHolder.decline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BaseFragment.context.dialogAcceptDecline(context.getString(R.string.decline),BaseFragment.context.getApiCommonController().approveDeclineStudent(Common.Actions.REJECTED.value(), responseList.get(position).getId()));
            }
        });

    }


    @Override
    public int getItemCount() {
        return responseList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView count;
        TextView fullName,section,rollNumber,gender,dob,email,motherName;
        TextView firstName;
        TextView lastName;
        TextView mobile;
        TextView clazz;
        TextView fatherName;
        TextView date;
        LinearLayout accept;
        LinearLayout decline;

        public ViewHolder(View itemView) {
            super(itemView);
            email=itemView.findViewById(R.id.email);
            count=itemView.findViewById(R.id.count);
            fullName=itemView.findViewById(R.id.fullName);
            firstName=itemView.findViewById(R.id.firstName);
            lastName=itemView.findViewById(R.id.lastName);
            mobile=itemView.findViewById(R.id.mobile);
            clazz=itemView.findViewById(R.id.clazz);
            fatherName=itemView.findViewById(R.id.fatherName);
            date=itemView.findViewById(R.id.date);
            accept=itemView.findViewById(R.id.accept);
            decline=itemView.findViewById(R.id.decline);
            motherName=itemView.findViewById(R.id.motherName);
            gender=itemView.findViewById(R.id.gender);
            section=itemView.findViewById(R.id.section);
            dob=itemView.findViewById(R.id.dob);
            rollNumber=itemView.findViewById(R.id.rollNumber);

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
                    ArrayList<PendingTaskResponse> filteredList = new ArrayList<>();
                    for (PendingTaskResponse noticeResponse : responseList) {
                        if (noticeResponse.getFname().toLowerCase().startsWith(charString) /*|| androidVersion.getMobile().startsWith(charString)*/) {
                            filteredList.add(noticeResponse);
                        } else if (noticeResponse.getLname().toLowerCase().contains(charString) /*|| androidVersion.getMobile().contains(charString)*/) {
                            filteredList.add(noticeResponse);
                        } else if (noticeResponse.getMobile().contains(charString) /*|| androidVersion.getMobile().contains(charString)*/) {
                            filteredList.add(noticeResponse);
                        }
                    }
                    StudentPendingAdapter.filteredList = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = filteredList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                filteredList = (ArrayList<PendingTaskResponse>) filterResults.values;
                responseList = filteredList;
                notifyDataSetChanged();
            }
        };
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }


    /*void apiHit(String action, String id) {
        MyProgressDialog.getInstance(context).show();
        Api api = ThisApp.getApi(context, Preferences.getInstance(context).getBaseUrl());
        api.approveDeclineStudent(action, id).enqueue(new Callback<ApproveDeclineStudentResponse>() {
            @Override
            public void onResponse(Call<ApproveDeclineStudentResponse> call, Response<ApproveDeclineStudentResponse> r) {
                MyProgressDialog.setDismiss();
                ApproveDeclineStudentResponse response=r.body();
                if (response.getStatus().value().equals(Status.SUCCESS.value())) {
                    Preferences.getInstance(context).setPageNo2(0);
                    MyProgressDialog.getInstance(context).show();
                    ApiCallService.action(context, 0, ApiCallService.Action.ACTION_TEACHER_PENDING_LIST);
                } else {
                    instance.getDialog("Sorry", response.getMessage().getMessage(), true);
                }
            }

            @Override
            public void onFailure(Call<ApproveDeclineStudentResponse> call, Throwable t) {
                MyProgressDialog.setDismiss();
            }
        });

    }*/
}