package com.app.mschooling.quiz.adapter;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.app.mschooling.com.R;
import com.mschooling.transaction.common.api.Common;
import com.mschooling.transaction.response.quiz.ChoiceResponse;

import java.util.List;

public class AdminOptionAdapter extends RecyclerView.Adapter<AdminOptionAdapter.ViewHolder> {
    Context context;
    List<ChoiceResponse> responseList;

    public AdminOptionAdapter(Context context, List<ChoiceResponse> responseList) {
        this.context = context;
        this.responseList = responseList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_admin_option, viewGroup, false);
        return new ViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        viewHolder.tvName.setText(responseList.get(position).getName());
        if (responseList.get(position).getYesNo().value().equalsIgnoreCase(Common.YesNo.YES.value()))  {
            viewHolder.radioButtonCorrect.setChecked(true);
        }
        viewHolder.radioButtonCorrect.setEnabled(false);
    }


    @Override
    public int getItemCount() {
        return responseList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        RadioButton radioButtonCorrect;
        TextView tvName;
        public ViewHolder(View itemView) {
            super(itemView);
            radioButtonCorrect = itemView.findViewById(R.id.radioButtonCorrect);
            tvName = itemView.findViewById(R.id.tvName);
        }
    }

}
