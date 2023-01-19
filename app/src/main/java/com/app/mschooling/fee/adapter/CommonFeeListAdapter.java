package com.app.mschooling.fee.adapter;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.app.mschooling.fee.activity.CommonAddFeeActivity;
import com.app.mschooling.com.R;
import com.app.mschooling.utils.BuzzTextView;
import com.mschooling.transaction.response.fee.FeeSetupResponse;

import java.util.List;


public class CommonFeeListAdapter extends RecyclerView.Adapter<CommonFeeListAdapter.ViewHolder> {


    private Activity context;
    public static List<FeeSetupResponse> responseList;
    public static List<FeeSetupResponse> filteredList;
    String intent;


    public CommonFeeListAdapter(Activity context, String intent, List<FeeSetupResponse> responsesList) {

        this.context = context;
        this.intent = intent;
        this.filteredList = responsesList;
        this.responseList = responsesList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_fee_list, viewGroup, false);
        return new ViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {

        viewHolder.name.setText(responseList.get(position).getFeeType());
        viewHolder.amount.setText("₹"+responseList.get(position).getFee());

        viewHolder.action.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context, CommonAddFeeActivity.class);
                intent.putExtra("id",responseList.get(position).getId());
                intent.putExtra("name",responseList.get(position).getFeeType());
                intent.putExtra("amount",""+responseList.get(position).getFee());
                context.startActivityForResult(intent,1);
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

        BuzzTextView name;
        BuzzTextView amount;
        LinearLayout action;


        public ViewHolder(View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.name);
            amount = itemView.findViewById(R.id.amount);
            action = itemView.findViewById(R.id.action);


        }

    }






}