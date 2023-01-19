package com.app.mschooling.students.list.adapter;

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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.app.mschooling.com.R;
import com.app.mschooling.other.activity.ShowImageActivity;
import com.app.mschooling.students.detail.activity.StudentBasicDetailActivity;
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
import com.mschooling.transaction.response.student.StudentResponse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class StudentListAdapter extends RecyclerView.Adapter<StudentListAdapter.ViewHolder> implements Filterable {

    private final Activity context;
   public List<StudentResponse> responseList;
    public static HashMap<String, String> map;
   public List<StudentResponse> filteredList;
    boolean isDeleted ;
    Button disableButton;
    Button deleteButton;
    Button enableButton;
    String intentString;
    ListCriteria criteria;


    public StudentListAdapter(Activity context, List<StudentResponse> data, Button enableButton, Button disableButton, Button deleteButton, String intentString, ListCriteria criteria) {
        this.context = context;
        responseList = data;
        map = new HashMap<>();
        this.enableButton = enableButton;
        this.disableButton = disableButton;
        this.deleteButton = deleteButton;
        this.intentString = intentString;
        this.isDeleted = criteria.getState() == Common.State.DELETED;
        this.criteria = criteria;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_admin_student_list_new, viewGroup, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, @SuppressLint("RecyclerView") int position) {

        viewHolder.enrolmentId.setText(responseList.get(position).getStudentBasicResponse().getEnrollmentId());
        viewHolder.fname.setText("" + responseList.get(position).getStudentBasicResponse().getfName().toUpperCase());
        viewHolder.lname.setText("" + responseList.get(position).getStudentBasicResponse().getlName().toUpperCase());
        viewHolder.mobile.setText("" + responseList.get(position).getStudentBasicResponse().getMobile());
        if (responseList.get(position).getStudentBasicResponse().getGender() != null) {
            viewHolder.gender.setText("" + responseList.get(position).getStudentBasicResponse().getGender());
        } else {
            viewHolder.gender.setText("N/A");
        }
        viewHolder.clas.setText("" + responseList.get(position).getStudentBasicResponse().getClassName() + " (" + responseList.get(position).getStudentBasicResponse().getSectionName() + ")");
        viewHolder.shimmerFrameLayout.setVisibility(View.VISIBLE);
        viewHolder.shimmerFrameLayout.startShimmer();

        if (responseList.get(position).getStudentProfileResponse().getProfileFirebase() == null) {
            Firebase firebase = new Firebase();
            firebase.setUrl(ParameterConstant.getDefaultUserUrl());
            responseList.get(position).getStudentProfileResponse().setProfileFirebase(firebase);
        } else if (responseList.get(position).getStudentProfileResponse().getProfileFirebase().getUrl() == null) {
            responseList.get(position).getStudentProfileResponse().getProfileFirebase().setUrl(ParameterConstant.getDefaultUserUrl());
        }

        Glide.with(context)
                .load(responseList.get(position).getStudentProfileResponse().getProfileFirebase().getUrl())
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .listener(new RequestListener<Drawable>() {
                    @SuppressLint("UseCompatLoadingForDrawables")
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        viewHolder.shimmerFrameLayout.setVisibility(View.GONE);
                        viewHolder.image.setImageDrawable(context.getResources().getDrawable(R.drawable.user));
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        viewHolder.shimmerFrameLayout.setVisibility(View.GONE);
                        return false;
                    }
                })
                .into(viewHolder.image);
        if (map.containsValue(responseList.get(position).getStudentBasicResponse().getEnrollmentId())) {
            selected(viewHolder);
        } else {
            unSelect(viewHolder);
        }

        viewHolder.main_layout.setOnClickListener(v -> {
            if (intentString == null) {
                if (map.size() > 0) {
                    click(viewHolder, position);
                    return;
                }
                Intent intent = new Intent(context, StudentBasicDetailActivity.class);
                intent.putExtra("studentId", responseList.get(position).getStudentBasicResponse().getEnrollmentId());
                intent.putExtra("id", responseList.get(position).getStudentBasicResponse().getEnrollmentId());
                context.startActivityForResult(intent, 1);
            } else if ("HomeWorkListActivity".equals(intentString)) {
                if (map.size() > 0) {
                    click(viewHolder, position);
                    return;
                }
                Intent intent = new Intent(context, StudentBasicDetailActivity.class);
                intent.putExtra("id", responseList.get(position).getStudentBasicResponse().getEnrollmentId());
                context.startActivityForResult(intent, 1);
            } else {
                Intent intent = new Intent();
                intent.putExtra("id", responseList.get(position).getStudentBasicResponse().getEnrollmentId());
                intent.putExtra("name", responseList.get(position).getStudentBasicResponse().getfName() + " " + responseList.get(position).getStudentBasicResponse().getlName());
                context.setResult(RESULT_OK, intent);
                context.finish();
            }
        });

        viewHolder.multipleSelectLayout.setOnClickListener(v -> {
            if (Preferences.getInstance(context).getROLE().equals(Common.Role.ADMIN.value())) {
                if (intentString == null) {
                    click(viewHolder, position);
                } else {
                    Intent intent = new Intent();
                    intent.putExtra("id", responseList.get(position).getStudentBasicResponse().getEnrollmentId());
                    intent.putExtra("name", responseList.get(position).getStudentBasicResponse().getfName() + " " + responseList.get(position).getStudentBasicResponse().getlName());
                    context.setResult(RESULT_OK, intent);
                    context.finish();
                }
            }
        });


       /* viewHolder.main_layout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (Preferences.getInstance(context).getROLE().equals(Common.Role.ADMIN.value())) {
                    if (intentString == null) {
                        click(viewHolder, position);
                    } else {
                        Intent intent = new Intent();
                        intent.putExtra("id", responseList.get(position).getEnrollmentId());
                        intent.putExtra("name", responseList.get(position).getfName() + " " + responseList.get(position).getlName());
                        context.setResult(RESULT_OK, intent);
                        context.finish();
                    }
                }
                return true;
            }
        });*/

        /*viewHolder.multipleSelectLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (Preferences.getInstance(context).getROLE().equals(Common.Role.ADMIN.value())) {
                    if (intentString == null) {
                        click(viewHolder, position);
                    } else {
                        Intent intent = new Intent();
                        intent.putExtra("id", responseList.get(position).getEnrollmentId());
                        intent.putExtra("name", responseList.get(position).getfName() + " " + responseList.get(position).getlName());
                        context.setResult(RESULT_OK, intent);
                        context.finish();
                    }
                }
                return true;
            }
        });*/

        /* viewHolder.image.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (intentString == null) {
                    click(viewHolder, position);
                } else {
                    Intent intent = new Intent();
                    intent.putExtra("id", responseList.get(position).getEnrollmentId());
                    intent.putExtra("name", responseList.get(position).getfName() + " " + responseList.get(position).getlName());
                    context.setResult(RESULT_OK, intent);
                    context.finish();
                }
                return true;
            }
        });*/

        viewHolder.delete.setOnClickListener(v -> {
            if (map.size() > 0) {
                click(viewHolder, position);
                return;
            }
            Toast.makeText(context, "Delete popup will be shown for confirmation", Toast.LENGTH_SHORT).show();
        });

        viewHolder.image.setOnClickListener(v -> {
            if (intentString == null) {
                try {
                    if (map.size() > 0) {
                        click(viewHolder, position);
                        return;
                    }
                    if (responseList.get(position).getStudentProfileResponse().getProfileFirebase().getUrl() != null) {
                        Intent intent = new Intent(context, ShowImageActivity.class);
                        intent.putExtra("url", responseList.get(position).getStudentProfileResponse().getProfileFirebase().getUrl());
                        intent.putExtra("name", responseList.get(position).getStudentBasicResponse().getlName());
                        context.startActivity(intent);
                    } else {
                        Intent intent = new Intent(context, StudentBasicDetailActivity.class);
                        intent.putExtra("id", responseList.get(position).getStudentBasicResponse().getEnrollmentId());
                        context.startActivity(intent);
                    }
                } catch (Exception e) {
                    Intent intent = new Intent(context, StudentBasicDetailActivity.class);
                    intent.putExtra("id", responseList.get(position).getStudentBasicResponse().getEnrollmentId());
                    context.startActivity(intent);
                }
            } else {
                Intent intent = new Intent();
                intent.putExtra("name", responseList.get(position).getStudentBasicResponse().getfName() + " " + responseList.get(position).getStudentBasicResponse().getlName());
                intent.putExtra("id", responseList.get(position).getStudentBasicResponse().getEnrollmentId());
                context.setResult(RESULT_OK, intent);
                context.finish();
            }
        });


    }

    @SuppressLint({"SetTextI18n", "UseCompatLoadingForDrawables"})
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

    @SuppressLint({"SetTextI18n", "UseCompatLoadingForDrawables"})
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
                    ArrayList<StudentResponse> filteredList = new ArrayList<>();
                    for (StudentResponse studentResponse : responseList) {
                        if (studentResponse.getStudentBasicResponse().getEnrollmentId().startsWith(charString) || studentResponse.getStudentBasicResponse().getEnrollmentId().contains(charString)) {
                            filteredList.add(studentResponse);
                        } else if (studentResponse.getStudentBasicResponse().getfName().toLowerCase().startsWith(charString) || studentResponse.getStudentBasicResponse().getfName().toLowerCase().contains(charString)) {
                            filteredList.add(studentResponse);
                        } else if (studentResponse.getStudentBasicResponse().getlName().toLowerCase().startsWith(charString) || studentResponse.getStudentBasicResponse().getlName().toLowerCase().contains(charString)) {
                            filteredList.add(studentResponse);
                        } else if (studentResponse.getStudentBasicResponse().getMobile().startsWith(charString) || studentResponse.getStudentBasicResponse().getMobile().contains(charString)) {
                            filteredList.add(studentResponse);
                        }
                    }
                    StudentListAdapter.this.filteredList = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = filteredList;
                return filterResults;
            }

            @SuppressLint("NotifyDataSetChanged")
            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                filteredList = (ArrayList<StudentResponse>) filterResults.values;
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

        if (Preferences.getInstance(context).getROLE().equals(Common.Role.ADMIN.value())) {
            if (isDeleted) {
                return;
            }
            if (responseList.get(position).getStudentBasicResponse().getEnrollmentId().equals("0000003")) {
                Toast.makeText(context, "You can't perform any operation on this record", Toast.LENGTH_SHORT).show();
                return;
            }
            if (map.containsValue(responseList.get(position).getStudentBasicResponse().getEnrollmentId())) {
                map.values().remove(responseList.get(position).getStudentBasicResponse().getEnrollmentId());
                unSelect(viewHolder);
            } else {
                map.put(responseList.get(position).getStudentBasicResponse().getEnrollmentId(), responseList.get(position).getStudentBasicResponse().getEnrollmentId());
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
        }
    }
}