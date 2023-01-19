package com.app.mschooling.fee.adapter;

import android.app.Activity;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.app.mschooling.com.R;
import com.app.mschooling.utils.BuzzTextView;
import com.mschooling.transaction.common.student.response.StudentFee;

import java.util.List;


public class StudentFeeDetailAdapter extends RecyclerView.Adapter<StudentFeeDetailAdapter.ViewHolder> {


    private Activity context;
    List<StudentFee> responseList;




    public StudentFeeDetailAdapter(Activity context, List<StudentFee> responsesList) {

        this.context = context;
        this.responseList = responsesList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_student_fee_detail, viewGroup, false);
        return new ViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {

        viewHolder.feeType.setText(responseList.get(position).getFeeType());
        viewHolder.totalFee.setText("₹"+responseList.get(position).getTotalAmount());
        viewHolder.paidFee.setText("₹"+responseList.get(position).getPaidAmount());
        viewHolder.lateFee.setText("₹"+responseList.get(position).getLateAmount());
        viewHolder.dueAmount.setText("Due: ₹"+(responseList.get(position).getTotalAmount()-responseList.get(position).getPaidAmount()));




        if (position == responseList.size() - 1) {
            viewHolder.marginLayout.setVisibility(View.VISIBLE);
        } else {
            viewHolder.marginLayout.setVisibility(View.GONE);
        }






    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return responseList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        BuzzTextView feeType;
        BuzzTextView totalFee;
        BuzzTextView paidFee;
        BuzzTextView lateFee;
        BuzzTextView dueAmount;
        LinearLayout mainLayout;
        LinearLayout totalFeeLayout;
        View marginLayout;

        public ViewHolder(View itemView) {
            super(itemView);

            feeType = itemView.findViewById(R.id.feeType);
            totalFee = itemView.findViewById(R.id.totalFee);
            paidFee = itemView.findViewById(R.id.paidFee);
            lateFee = itemView.findViewById(R.id.lateFee);
            dueAmount = itemView.findViewById(R.id.dueAmount);
            mainLayout = itemView.findViewById(R.id.mainLayout);
            totalFeeLayout = itemView.findViewById(R.id.totalFeeLayout);
            marginLayout = itemView.findViewById(R.id.marginLayout);


        }

    }






}