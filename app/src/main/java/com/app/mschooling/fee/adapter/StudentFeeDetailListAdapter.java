package com.app.mschooling.fee.adapter;

import static android.app.Activity.RESULT_OK;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.app.mschooling.com.R;
import com.app.mschooling.utils.BuzzTextView;
import com.mschooling.transaction.common.student.response.StudentFee;

import java.util.List;


public class StudentFeeDetailListAdapter extends RecyclerView.Adapter<StudentFeeDetailListAdapter.ViewHolder> {


    private Activity context;
    public static List<StudentFee> responseList;
    public static List<StudentFee> filteredList;
    String intent;



    public StudentFeeDetailListAdapter(Activity context, String intent, List<StudentFee> responsesList) {

        this.context = context;
        this.intent = intent;
        this.filteredList = responsesList;
        this.responseList = responsesList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_fee_detail, viewGroup, false);
        return new ViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {

        viewHolder.feeType.setText(responseList.get(position).getFeeType());
        viewHolder.totalFee.setText("₹"+responseList.get(position).getTotalAmount());
        viewHolder.amount.setText(""+responseList.get(position).getTotalAmount());
        viewHolder.paidFee.setText("₹"+responseList.get(position).getPaidAmount());
        viewHolder.lateFee.setText("₹"+responseList.get(position).getLateAmount());
        viewHolder.dueAmount.setText("Due: ₹"+(responseList.get(position).getTotalAmount()-responseList.get(position).getPaidAmount()));


        if (intent.equals("edit")){
            viewHolder.enterTotalFeeLayout.setVisibility(View.VISIBLE);
            viewHolder.totalFeeLayout.setVisibility(View.GONE);
        }else {
            viewHolder.enterTotalFeeLayout.setVisibility(View.GONE);
            viewHolder.totalFeeLayout.setVisibility(View.VISIBLE);
        }


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




        viewHolder.mainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (intent.equals("selection")){
                    if ((responseList.get(position).getTotalAmount()-responseList.get(position).getPaidAmount())==0){
                        Toast.makeText(context, "Sorry, There is no due amount in "+responseList.get(position).getFeeType()+" fee", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    Intent intent = new Intent();
                    intent.putExtra("id", responseList.get(position).getId());
                    intent.putExtra("name", responseList.get(position).getFeeType());
                    context.setResult(RESULT_OK, intent);
                    context.finish();
                }
            }
        });


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