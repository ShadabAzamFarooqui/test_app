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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.mschooling.base.fragment.BaseFragment;
import com.app.mschooling.com.R;
import com.mschooling.transaction.common.api.Common;
import com.mschooling.transaction.response.pending.PendingTaskResponse;

import java.util.ArrayList;
import java.util.List;

public class TeacherPendingAdapter extends RecyclerView.Adapter<TeacherPendingAdapter.ViewHolder> implements Filterable {

    Activity context;
    BaseFragment instance;
    public static List<PendingTaskResponse> responseList;
    public static List<PendingTaskResponse> filteredList;

    public TeacherPendingAdapter(Activity context, BaseFragment instance, List<PendingTaskResponse> responseList) {
        this.context = context;
        this.responseList = responseList;
        this.filteredList = responseList;
        this.instance = instance;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_teacher_pending, viewGroup, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, @SuppressLint("RecyclerView") int position) {
        viewHolder.count.setText("" + (position + 1) + ". ");
        viewHolder.fullName.setText(responseList.get(position).getFname() + " " + responseList.get(position).getLname());
        viewHolder.firstName.setText(responseList.get(position).getFname());
        viewHolder.fatherName.setText(responseList.get(position).getFather());
        viewHolder.lastName.setText(responseList.get(position).getLname());
        viewHolder.mobile.setText(responseList.get(position).getMobile());
        viewHolder.date.setText(responseList.get(position).getTime());
        viewHolder.gender.setText(responseList.get(position).getGender().value());
        viewHolder.clazzLayout.setVisibility(View.GONE);
        viewHolder.motherName.setVisibility(View.GONE);
//        viewHolder.gender.setVisibility(View.GONE);


        if (!responseList.get(position).getEmail().trim().isEmpty()) {
            viewHolder.email.setText(responseList.get(position).getEmail());
        } else {
            viewHolder.email.setText("N/A");
        }
        if (responseList.get(position).getDob() == null) {
            viewHolder.dob.setText("N/A");
        } else if (responseList.get(position).getDob().trim().isEmpty()) {
            viewHolder.dob.setText("N/A");
        } else {
            viewHolder.dob.setText(responseList.get(position).getDob());
        }

        viewHolder.recyclerView.setHasFixedSize(true);
        viewHolder.recyclerView.setFocusable(false);
        viewHolder.recyclerView.setNestedScrollingEnabled(false);
        viewHolder.recyclerView.setLayoutManager(new LinearLayoutManager(context));
//        viewHolder.recyclerView.setAdapter(new PendingTeacherPendingClassAdapter(context,responseList.get(position).getClassIds()));




        viewHolder.accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BaseFragment.context.dialogAcceptDecline(context.getString(R.string.accept),BaseFragment.context.getApiCommonController().approveDeclineTeacher(Common.Actions.APPROVED.value(), responseList.get(position).getId()));

            }
        });


        viewHolder.decline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BaseFragment.context.dialogAcceptDecline(context.getString(R.string.decline),BaseFragment.context.getApiCommonController().approveDeclineTeacher(Common.Actions.REJECTED.value(), responseList.get(position).getId()));
            }
        });



    }


    @Override
    public int getItemCount() {
        return responseList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView count;
        TextView fullName, gender, motherName;
        TextView firstName;
        TextView lastName;
        TextView mobile;
        LinearLayout clazzLayout;
        TextView fatherName;
        TextView date, dob, email;
        LinearLayout accept;
        LinearLayout decline;
        RecyclerView recyclerView;

        public ViewHolder(View itemView) {
            super(itemView);
            count = itemView.findViewById(R.id.count);
            fullName = itemView.findViewById(R.id.fullName);
            firstName = itemView.findViewById(R.id.firstName);
            lastName = itemView.findViewById(R.id.lastName);
            mobile = itemView.findViewById(R.id.mobile);
            clazzLayout = itemView.findViewById(R.id.clazzLayout);
            fatherName = itemView.findViewById(R.id.father);
            dob = itemView.findViewById(R.id.dob);
            email = itemView.findViewById(R.id.email);
            date = itemView.findViewById(R.id.date);
            accept = itemView.findViewById(R.id.accept);
            decline = itemView.findViewById(R.id.decline);
            motherName = itemView.findViewById(R.id.motherName);
            gender = itemView.findViewById(R.id.gender);
            recyclerView = itemView.findViewById(R.id.recyclerView);
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
                    TeacherPendingAdapter.filteredList = filteredList;
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
        api.approveDeclineTeacher(action, id).enqueue(new Callback<ApproveDeclineTeacherResponse>() {
            @Override
            public void onResponse(Call<ApproveDeclineTeacherResponse> call, Response<ApproveDeclineTeacherResponse> r) {
                MyProgressDialog.setDismiss();
                ApproveDeclineTeacherResponse response=r.body();
                if (response.getStatus().value().equals(Status.SUCCESS.value())) {
                    Preferences.getInstance(context).setPageNo2(0);
                    MyProgressDialog.getInstance(context).show();
                    ApiCallService.action(context, 0, ApiCallService.Action.ACTION_TEACHER_PENDING_LIST);
                } else {
                    instance.getDialog("Sorry", response.getMessage().getMessage(), true);
                }
            }

            @Override
            public void onFailure(Call<ApproveDeclineTeacherResponse> call, Throwable t) {
                MyProgressDialog.setDismiss();
            }
        });

    }
*/
}