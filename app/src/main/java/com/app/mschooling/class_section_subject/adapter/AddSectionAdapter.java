package com.app.mschooling.class_section_subject.adapter;

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

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.app.mschooling.com.R;
import com.mschooling.transaction.request.student.AddSectionRequest;

import java.util.List;

public class AddSectionAdapter extends RecyclerView.Adapter<AddSectionAdapter.ViewHolder> {
    Activity context;
    List<AddSectionRequest> responseList;

    public AddSectionAdapter(Activity context, List<AddSectionRequest> addSectionRequestList) {
        this.context = context;
        this.responseList = addSectionRequestList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_add_section, viewGroup, false);
        return new ViewHolder(view);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, @SuppressLint("RecyclerView") int position) {

        if (position == 0) {
            viewHolder.delete.setVisibility(View.INVISIBLE);
            viewHolder.add.setVisibility(View.INVISIBLE);
        } else {
            if (responseList.get(position).isEnable()) {
                viewHolder.delete.setVisibility(View.VISIBLE);
                viewHolder.add.setVisibility(View.VISIBLE);
            } else {
                viewHolder.delete.setVisibility(View.INVISIBLE);
                viewHolder.add.setVisibility(View.INVISIBLE);
            }
        }

        if (responseList.get(position).isError()) {
            viewHolder.mainLayout.setBackground(context.getDrawable(R.drawable.error_border));
            viewHolder.name.setHint(context.getString(R.string.enter_section_name));
        } else {
            viewHolder.mainLayout.setBackground(context.getDrawable(R.drawable.gradient_border));
            viewHolder.name.setHint(null);

        }
        viewHolder.add.setVisibility(View.GONE);
        viewHolder.name.setText(responseList.get(position).getName());
        viewHolder.priority.setText(String.valueOf(responseList.get(position).getPriority()));


        viewHolder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                responseList.remove(position);
                notifyDataSetChanged();
            }
        });
        viewHolder.name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }


            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                AddSectionRequest demo = responseList.get(position);
                demo.setName(viewHolder.name.getText().toString());
                demo.setDescription(viewHolder.name.getText().toString());
                demo.setEnable(responseList.get(position).isEnable());
                try {
                    demo.setPriority(Integer.parseInt(viewHolder.priority.getText().toString()));
                } catch (Exception e) {
                    demo.setPriority(0);
                }
                responseList.set(position, demo);

                if (!viewHolder.name.getText().toString().isEmpty()) {
                    responseList.get(position).setError(false);
                    viewHolder.mainLayout.setBackground(context.getDrawable(R.drawable.gradient_border));
                    viewHolder.name.setHint(null);
                    viewHolder.name.setError(null);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        viewHolder.priority.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                AddSectionRequest demo = responseList.get(position);
                demo.setName(viewHolder.name.getText().toString());
                demo.setDescription(viewHolder.name.getText().toString());
                demo.setEnable(responseList.get(position).isEnable());
                try {
                    demo.setPriority(Integer.parseInt(viewHolder.priority.getText().toString()));
                } catch (Exception e) {
                    demo.setPriority(0);
                }
                responseList.set(position, demo);
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


    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        LinearLayout add;
        LinearLayout delete;
        LinearLayout mainLayout;
        EditText name, priority;

        public ViewHolder(View itemView) {
            super(itemView);
            add = itemView.findViewById(R.id.add);
            delete = itemView.findViewById(R.id.delete);
            name = itemView.findViewById(R.id.name);
            priority = itemView.findViewById(R.id.priority);
            mainLayout = itemView.findViewById(R.id.mainLayout);
        }
    }

}
