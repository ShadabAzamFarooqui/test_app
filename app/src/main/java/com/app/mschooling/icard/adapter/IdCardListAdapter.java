package com.app.mschooling.icard.adapter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.app.mschooling.other.activity.PrintIdCardActivity;
import com.app.mschooling.com.R;
import com.app.mschooling.utils.BuzzTextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.mschooling.transaction.response.icard.ICardResponse;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class IdCardListAdapter extends RecyclerView.Adapter<IdCardListAdapter.ViewHolder> implements Filterable {

    private Activity context;
    public static List<ICardResponse> filteredList;
    public static List<ICardResponse> responseList;

    public IdCardListAdapter(Activity context, List<ICardResponse> responseList) {

        this.context = context;
        this.responseList = responseList;
        this.filteredList = responseList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_id_card, viewGroup, false);
        return new ViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        ICardResponse response = responseList.get(position);
        viewHolder.name.setText(response.getfName() + " " + response.getlName());
        if (response.getBloodGroup()==null){
            viewHolder.bloodGroup.setText("N/A");
        }else {
            if (response.getBloodGroup().equals("")){
                viewHolder.bloodGroup.setText("N/A");
            }else {
                viewHolder.bloodGroup.setText(response.getBloodGroup());
            }
        }

        viewHolder.enrolmentId.setText(response.getEnrollmentId());
        viewHolder.mobile.setText(response.getMobile());
        viewHolder.gender.setText(response.getGender().value());
        viewHolder.classSection.setText(response.getClassName() + " (" + response.getSectionName() + ")");

        viewHolder.print.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context,PrintIdCardActivity.class);
                intent.putExtra("id",response.getEnrollmentId());
                intent.putExtra("name",response.getfName());
                context.startActivity(intent);

//                context.startActivity(new Intent(context, PrintIdCardActivity.class));
            }
        });
        try {
            if (response.getFirebase().getUrl() != null) {
                Glide.with(context)
                        .load(responseList.get(position).getFirebase().getUrl())
                        .listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                viewHolder.progressBar.setVisibility(View.GONE);
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                viewHolder.progressBar.setVisibility(View.GONE);
                                return false;
                            }
                        })
//                    .apply(RequestOptions.skipMemoryCacheOf(true))
//                    .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE))
                        .into(viewHolder.image);
            } else {
                viewHolder.image.setImageDrawable(context.getResources().getDrawable(R.drawable.user));
            }
        } catch (Exception e) {
            viewHolder.image.setImageDrawable(context.getResources().getDrawable(R.drawable.user));
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
        ProgressBar progressBar;
        CircleImageView image;
        BuzzTextView name;
        BuzzTextView mobile;
        BuzzTextView bloodGroup;
        BuzzTextView classSection;
        BuzzTextView enrolmentId;
        BuzzTextView gender;
        RecyclerView recyclerView;
        LinearLayout print;

        public ViewHolder(View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.name);
            print = itemView.findViewById(R.id.print);
            mobile = itemView.findViewById(R.id.mobile);
            gender = itemView.findViewById(R.id.gender);
            enrolmentId = itemView.findViewById(R.id.enrolmentId);
            classSection = itemView.findViewById(R.id.classSection);
            bloodGroup = itemView.findViewById(R.id.bloodGroup);
            progressBar = itemView.findViewById(R.id.progressBar);
            recyclerView = itemView.findViewById(R.id.recyclerView);
            image = itemView.findViewById(R.id.image);

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
                    ArrayList<ICardResponse> filteredList = new ArrayList<>();
//                    for (ICardResponse wrapper : responseList) {
//                        StudentAttendance attendance=wrapper.getStudentAttendances().get(0);
//                        if (wrapper.getfName().toLowerCase().startsWith(charString) || wrapper.getlName().startsWith(charString)) {
//                            filteredList.get(0).getStudentAttendances().add(attendance);
//                        } else if (wrapper.getRollNumber().toLowerCase().contains(charString) /*|| androidVersion.getMobile().contains(charString)*/) {
//                            filteredList.get(0).getStudentAttendances().add(attendance);
//                        } else if (wrapper.getEnrollmentId().contains(charString) /*|| androidVersion.getMobile().contains(charString)*/) {
//                            filteredList.get(0).getStudentAttendances().add(attendance);
//                        }
//                    }
                    IdCardListAdapter.filteredList = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = filteredList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                filteredList = (ArrayList<ICardResponse>) filterResults.values;
                responseList = filteredList;
                notifyDataSetChanged();
            }
        };
    }
}