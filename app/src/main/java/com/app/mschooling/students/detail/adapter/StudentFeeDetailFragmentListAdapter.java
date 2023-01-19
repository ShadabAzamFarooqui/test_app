package com.app.mschooling.students.detail.adapter;

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

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.app.mschooling.com.R;
import com.app.mschooling.utils.AppUser;
import com.app.mschooling.utils.BuzzTextView;
import com.mschooling.transaction.common.student.response.StudentFee;

import java.util.List;


public class StudentFeeDetailFragmentListAdapter extends RecyclerView.Adapter<StudentFeeDetailFragmentListAdapter.ViewHolder> {


    private Activity context;
    public static List<StudentFee> responseList;
    public static List<StudentFee> filteredList;
    String intent;
    boolean update ;


    public StudentFeeDetailFragmentListAdapter(Activity context, String intent, List<StudentFee> responsesList, boolean update ) {

        this.context = context;
        this.intent = intent;
        this.filteredList = responsesList;
        this.responseList = responsesList;
        this.update = update;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_fee_detail, viewGroup, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, @SuppressLint("RecyclerView") int position) {

        viewHolder.feeType.setText(responseList.get(position).getFeeType());
        viewHolder.totalFee.setText("₹"+responseList.get(position).getTotalAmount());
        viewHolder.amount.setText(""+responseList.get(position).getTotalAmount());
        viewHolder.paidFee.setText("₹"+responseList.get(position).getPaidAmount());
        viewHolder.lateFee.setText("₹"+responseList.get(position).getLateAmount());
        viewHolder.dueAmount.setText("Due: ₹"+(responseList.get(position).getTotalAmount()-responseList.get(position).getPaidAmount()));

        viewHolder.amount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!viewHolder.amount.getText().toString().trim().isEmpty()){
                    responseList.get(position).setTotalAmount(Double.parseDouble(viewHolder.amount.getText().toString()));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        if ( update){
            viewHolder.enterTotalFeeLayout.setVisibility(View.VISIBLE);
            viewHolder.totalFeeLayout.setVisibility(View.GONE);
        }else {
            viewHolder.enterTotalFeeLayout.setVisibility(View.GONE);
            viewHolder.totalFeeLayout.setVisibility(View.VISIBLE);
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
        EditText amount;
        BuzzTextView paidFee;
        BuzzTextView lateFee;
        BuzzTextView dueAmount;
        LinearLayout mainLayout;
        LinearLayout enterTotalFeeLayout;
        LinearLayout totalFeeLayout;


        public ViewHolder(View itemView) {
            super(itemView);

            feeType = itemView.findViewById(R.id.feeType);
            totalFee = itemView.findViewById(R.id.totalFee);
            amount = itemView.findViewById(R.id.amount);
            paidFee = itemView.findViewById(R.id.paidFee);
            lateFee = itemView.findViewById(R.id.lateFee);
            dueAmount = itemView.findViewById(R.id.dueAmount);
            mainLayout = itemView.findViewById(R.id.mainLayout);
            enterTotalFeeLayout = itemView.findViewById(R.id.enterTotalFeeLayout);
            totalFeeLayout = itemView.findViewById(R.id.totalFeeLayout);


        }

    }






}