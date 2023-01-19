package com.app.mschooling.fee.adapter;

import android.app.Activity;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.app.mschooling.com.R;
import com.mschooling.transaction.response.fee.StudentFeeHistoryResponse;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class FeeHistoryListAdapter extends RecyclerView.Adapter<FeeHistoryListAdapter.ViewHolder> {


    private Activity context;
    List<StudentFeeHistoryResponse> responseList;
    List<StudentFeeHistoryResponse> filteredList;
    String intent;


    public FeeHistoryListAdapter(Activity context, String intent, List<StudentFeeHistoryResponse> responsesList) {

        this.context = context;
        this.intent = intent;
        this.filteredList = responsesList;
        this.responseList = responsesList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_fee_history, viewGroup, false);
        return new ViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {

        viewHolder.date.setText(responseList.get(position).getDate().split(" ")[0]);
        viewHolder.time.setText(responseList.get(position).getDate().split(" ")[1]);
        viewHolder.amount.setText("₹" + (responseList.get(position).getAmount() + responseList.get(position).getLateAmount()));
        viewHolder.lateFee.setText("Late Fee: " + responseList.get(position).getLateAmount());
        viewHolder.paidBy.setText("" + responseList.get(position).getCreator());
        viewHolder.feeType.setText("" + responseList.get(position).getFeeType());
        viewHolder.note.setText("" + responseList.get(position).getNote());

        if (responseList.get(position).getNote().trim().equals("")){
            viewHolder.note.setVisibility(View.GONE);
        }else {
            viewHolder.note.setVisibility(View.VISIBLE);
        }

        viewHolder.action.setVisibility(View.GONE);

        viewHolder.action.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(context, v);
                //inflating menu from xml resource
                popup.inflate(R.menu.edit_delete);
                //adding click listener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.edit:
                               /* Intent intent = new Intent(context, PayFeeActivity.class);
                                intent.putExtra("id", responseList.get(position).getFeeType());
                                intent.putExtra("name", responseList.get(position).getAmount());
                                context.startActivity(intent);*/
                                return true;
                            case R.id.delete:
//                                EventBus.getDefault().post(new EventDelete(responseList.get(position).getId(), position));
                                return true;
                            default:
                                return false;
                        }
                    }
                });
                //displaying the popup
                popup.show();
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

        @BindView(R.id.date)
        TextView date;
        @BindView(R.id.time)
        TextView time;
        @BindView(R.id.amount)
        TextView amount;
        @BindView(R.id.lateFee)
        TextView lateFee;
        @BindView(R.id.paidBy)
        TextView paidBy;
        @BindView(R.id.feeType)
        TextView feeType;
        @BindView(R.id.note)
        TextView note;
        @BindView(R.id.action)
        LinearLayout action;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

        }
    }


}