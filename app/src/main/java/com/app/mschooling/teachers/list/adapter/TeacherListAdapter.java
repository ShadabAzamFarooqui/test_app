package com.app.mschooling.teachers.list.adapter;

import static android.app.Activity.RESULT_OK;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.app.mschooling.other.activity.ShowImageActivity;
import com.app.mschooling.teachers.detail.activity.TeacherDetailActivity;
import com.app.mschooling.com.R;
import com.app.mschooling.utils.AppUser;
import com.app.mschooling.utils.ParameterConstant;
import com.app.mschooling.utils.Preferences;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.mschooling.transaction.common.Firebase;
import com.mschooling.transaction.common.api.Common;
import com.mschooling.transaction.filter.ListCriteria;
import com.mschooling.transaction.response.teacher.TeacherResponse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TeacherListAdapter extends RecyclerView.Adapter<TeacherListAdapter.ViewHolder> implements Filterable {

    private Activity context;
    public static List<TeacherResponse> responseList;
    public static HashMap<String, String> map;
    public static List<TeacherResponse> filteredList;
    boolean isDeleted;

    Button disableButton;
    Button deleteButton;
    Button enableButton;
    String whereFrom;
    ListCriteria criteria;

    public TeacherListAdapter(Activity context, List<TeacherResponse> data, Button enableButton, Button disableButton, Button deleteButton, String whereFrom, ListCriteria criteria) {
        this.context = context;
        this.responseList = data;
        map = new HashMap<>();
        isDeleted = criteria.getState()==Common.State.DELETED;
        this.enableButton = enableButton;
        this.disableButton = disableButton;
        this.deleteButton = deleteButton;
        this.whereFrom = whereFrom;
        this.criteria = criteria;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_admin_student_list_new, viewGroup, false);
        return new ViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, @SuppressLint("RecyclerView") int position) {

        viewHolder.enrolmentId.setText(responseList.get(position).getTeacherBasicResponse().getEnrollmentId());
        viewHolder.fname.setText("" + responseList.get(position).getTeacherBasicResponse().getfName().toUpperCase());
        viewHolder.lname.setText("" + responseList.get(position).getTeacherBasicResponse().getlName().toUpperCase());
        viewHolder.mobile.setText("" + responseList.get(position).getTeacherBasicResponse().getMobile());
        if (responseList.get(position).getTeacherBasicResponse().getGender() != null) {
            viewHolder.gender.setText("" + responseList.get(position).getTeacherBasicResponse().getGender());
        } else {
            viewHolder.gender.setText("N/A");
        }
        viewHolder.clas.setVisibility(View.GONE);

        viewHolder.shimmerFrameLayout.setVisibility(View.VISIBLE);
        viewHolder.shimmerFrameLayout.startShimmer();

        if (responseList.get(position).getTeacherProfileResponse().getProfileFirebase()==null){
            Firebase firebase=new Firebase();
            firebase.setUrl(ParameterConstant.getDefaultUserUrl());
            responseList.get(position).getTeacherProfileResponse().setProfileFirebase(firebase);
        }else if (responseList.get(position).getTeacherProfileResponse().getProfileFirebase().getUrl()==null){
            responseList.get(position).getTeacherProfileResponse().getProfileFirebase().setUrl(ParameterConstant.getDefaultUserUrl());
        }

        Glide.with(context)
                .load(responseList.get(position).getTeacherProfileResponse().getProfileFirebase().getUrl())
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        viewHolder.image.setImageDrawable(context.getResources().getDrawable(R.drawable.user));
                        viewHolder.shimmerFrameLayout.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        viewHolder.shimmerFrameLayout.setVisibility(View.GONE);
                        return false;
                    }
                })
//                    .apply(RequestOptions.skipMemoryCacheOf(true))
//                    .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE))
                .into(viewHolder.image);


        if (map.containsValue(responseList.get(position).getTeacherBasicResponse().getEnrollmentId())) {
            selected(viewHolder);
        } else {
            unSelect(viewHolder);
        }

        viewHolder.main_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (whereFrom == null) {
                    if (Preferences.getInstance(context).getROLE().equals("ADMIN")) {
                        if (map.size() > 0) {
                            click(viewHolder, position);
                            return;
                        }
                    }
                    Intent intent = new Intent(context, TeacherDetailActivity.class);
                    intent.putExtra("id", responseList.get(position).getTeacherBasicResponse().getEnrollmentId());
                    context.startActivityForResult(intent,1);
                } else {
                    Intent intent = new Intent();
                    intent.putExtra("id", responseList.get(position).getTeacherBasicResponse().getEnrollmentId());
                    intent.putExtra("name", responseList.get(position).getTeacherBasicResponse().getfName() + " " + responseList.get(position).getTeacherBasicResponse().getlName());
                    context.setResult(RESULT_OK, intent);
                    context.finish();
                }
            }
        });

        viewHolder.multipleSelectLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (whereFrom == null) {
                    if (Preferences.getInstance(context).getROLE().equals("ADMIN")) {
                        click(viewHolder, position);
                    }
                }
            }
        });


        /*viewHolder.main_layout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (whereFrom == null) {
                    if (Preferences.getInstance(context).getROLE().equals("ADMIN")) {
                        click(viewHolder, position);
                    }
                }
                return true;
            }
        });*/

       /* viewHolder.multipleSelectLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (whereFrom == null) {
                    if (Preferences.getInstance(context).getROLE().equals("ADMIN")) {
                        click(viewHolder, position);
                    }
                }
                return true;
            }
        });*/

        viewHolder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (map.size() > 0) {
                    click(viewHolder, position);
                    return;
                }
                Toast.makeText(context, "Delete popup will be shown for confirmation", Toast.LENGTH_SHORT).show();
            }
        });

        viewHolder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (map.size() > 0) {
                        click(viewHolder, position);
                        return;
                    }
                    if (responseList.get(position).getTeacherProfileResponse().getProfileFirebase().getUrl() != null) {
                        Intent intent = new Intent(context, ShowImageActivity.class);
                        intent.putExtra("url", responseList.get(position).getTeacherProfileResponse().getProfileFirebase().getUrl());
                        intent.putExtra("name", responseList.get(position).getTeacherBasicResponse().getfName() + " " + responseList.get(position).getTeacherBasicResponse().getlName());
                        context.startActivity(intent);
                    } else {
                        Intent intent = new Intent(context, TeacherDetailActivity.class);
                        intent.putExtra("id", responseList.get(position).getTeacherBasicResponse().getEnrollmentId());
                        context.startActivityForResult(intent,1);
                    }
                } catch (Exception e) {
                    Intent intent = new Intent(context, TeacherDetailActivity.class);
                    intent.putExtra("id", responseList.get(position).getTeacherBasicResponse().getEnrollmentId());
                    context.startActivityForResult(intent,1);
                }
            }
        });

       /* viewHolder.image.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                click(viewHolder, position);
                return true;
            }
        });*/


    }

    void unSelect(ViewHolder viewHolder) {
        viewHolder.layout.setBackgroundColor(context.getResources().getColor(R.color.white));
        viewHolder.multipleSelectLayout.setBackgroundColor(context.getResources().getColor(R.color.white));
        viewHolder.enrolmentId.setTextColor(context.getResources().getColor(R.color.blue));
        viewHolder.selectTxt.setTextColor(context.getResources().getColor(R.color.military_blue));
        viewHolder.fname.setTextColor(context.getResources().getColor(R.color.military_blue));
        viewHolder.lname.setTextColor(context.getResources().getColor(R.color.military_blue));
        viewHolder.mobile.setTextColor(context.getResources().getColor(R.color.military_blue));
        viewHolder.gender.setTextColor(context.getResources().getColor(R.color.military_blue));
        viewHolder.clas.setTextColor(context.getResources().getColor(R.color.military_blue));
        if (isDeleted) {
            viewHolder.selectTxt.setText("Deleted");
            viewHolder.selectTxt.setTextColor(context.getResources().getColor(R.color.divider_menu));
        } else {
            viewHolder.selectTxt.setText("Select");
        }
        viewHolder.deleteIcon.setImageDrawable(context.getResources().getDrawable(R.drawable.delete_black));

    }

    void selected(ViewHolder viewHolder) {
        viewHolder.layout.setBackgroundColor(context.getResources().getColor(R.color.gallery_tittle));
        viewHolder.multipleSelectLayout.setBackgroundColor(context.getResources().getColor(R.color.gallery_tittle));
        viewHolder.enrolmentId.setTextColor(context.getResources().getColor(R.color.white));
        viewHolder.selectTxt.setTextColor(context.getResources().getColor(R.color.white));
        viewHolder.fname.setTextColor(context.getResources().getColor(R.color.white));
        viewHolder.lname.setTextColor(context.getResources().getColor(R.color.white));
        viewHolder.mobile.setTextColor(context.getResources().getColor(R.color.white));
        viewHolder.gender.setTextColor(context.getResources().getColor(R.color.white));
        viewHolder.clas.setTextColor(context.getResources().getColor(R.color.white));
        viewHolder.selectTxt.setTextColor(context.getResources().getColor(R.color.white));
        viewHolder.selectTxt.setText("Selected");
        viewHolder.deleteIcon.setImageDrawable(context.getResources().getDrawable(R.drawable.delete_white));
    }

    @Override
    public int getItemCount() {
        return responseList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        RelativeLayout main_layout;
        LinearLayout layout;
        RelativeLayout multipleSelectLayout;
        TextView fname;
        TextView lname;
        TextView mobile;
        TextView selectTxt;
        ImageView deleteIcon;
        ImageView image;
        LinearLayout delete;
        TextView gender;
        TextView clas;
        TextView enrolmentId;
        ShimmerFrameLayout shimmerFrameLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            fname = itemView.findViewById(R.id.fname);
            lname = itemView.findViewById(R.id.lname);
            mobile = itemView.findViewById(R.id.mobile);
            selectTxt = itemView.findViewById(R.id.selectTxt);
            main_layout = itemView.findViewById(R.id.main_layout);
            multipleSelectLayout = itemView.findViewById(R.id.multipleSelectLayout);
            deleteIcon = itemView.findViewById(R.id.deleteIcon);
            delete = itemView.findViewById(R.id.delete);
            layout = itemView.findViewById(R.id.layout);
            gender = itemView.findViewById(R.id.gender);
            clas = itemView.findViewById(R.id.clas);
            enrolmentId = itemView.findViewById(R.id.enrolmentId);
            image = itemView.findViewById(R.id.image);
            shimmerFrameLayout = itemView.findViewById(R.id.shimmerFrameLayout);

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
                    ArrayList<TeacherResponse> filteredList = new ArrayList<>();
                    for (TeacherResponse teacherResponse : responseList) {
                        if (teacherResponse.getTeacherBasicResponse().getEnrollmentId().startsWith(charString) || teacherResponse.getTeacherBasicResponse().getEnrollmentId().contains(charString)) {
                            filteredList.add(teacherResponse);
                        } else if (teacherResponse.getTeacherBasicResponse().getfName().toLowerCase().startsWith(charString) || teacherResponse.getTeacherBasicResponse().getfName().toLowerCase().contains(charString)) {
                            filteredList.add(teacherResponse);
                        } else if (teacherResponse.getTeacherBasicResponse().getlName().toLowerCase().startsWith(charString) || teacherResponse.getTeacherBasicResponse().getlName().toLowerCase().contains(charString)) {
                            filteredList.add(teacherResponse);
                        } else if (teacherResponse.getTeacherBasicResponse().getMobile().startsWith(charString) || teacherResponse.getTeacherBasicResponse().getMobile().contains(charString)) {
                            filteredList.add(teacherResponse);
                        }
                    }
                    TeacherListAdapter.filteredList = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = filteredList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                filteredList = (ArrayList<TeacherResponse>) filterResults.values;
                responseList = filteredList;
                notifyDataSetChanged();
            }
        };
    }

//    @Override
//    public int getItemViewType(int position) {
//        return position;
//    }

    void click(ViewHolder viewHolder, int position) {
        if (isDeleted) {
            return;
        }
        if (responseList.get(position).getTeacherBasicResponse().getEnrollmentId().equals("0000002")) {
            Toast.makeText(context, "You can't perform any operation on this record", Toast.LENGTH_SHORT).show();
            return;
        }

//        this line have to remove , in order to support multiple selection.
        if (map.containsValue(responseList.get(position).getTeacherBasicResponse().getEnrollmentId())) {
            map.values().remove(responseList.get(position).getTeacherBasicResponse().getEnrollmentId());
            unSelect(viewHolder);
        } else {
            map.clear();
            map.put(responseList.get(position).getTeacherBasicResponse().getEnrollmentId(), responseList.get(position).getTeacherBasicResponse().getEnrollmentId());
            selected(viewHolder);
        }
        if (map.size() > 0) {
            if (criteria.getState().value().equals(Common.State.ACTIVATED.value())) {
                enableButton.setVisibility(View.GONE);
                disableButton.setVisibility(View.VISIBLE);
                deleteButton.setVisibility(View.VISIBLE);
            } else if (criteria.getState().value().equals(Common.State.DISABLED.value())) {
                enableButton.setVisibility(View.VISIBLE);
                disableButton.setVisibility(View.GONE);
                deleteButton.setVisibility(View.VISIBLE);
            }
        } else {
            enableButton.setVisibility(View.GONE);
            disableButton.setVisibility(View.GONE);
            deleteButton.setVisibility(View.GONE);
        }
        notifyDataSetChanged();
    }
}