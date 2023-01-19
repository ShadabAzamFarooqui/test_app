package com.app.mschooling.teachers.list.adapter;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.telephony.PhoneNumberUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.app.mschooling.com.R;
import com.app.mschooling.students.detail.activity.StudentBasicDetailActivity;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.mschooling.transaction.response.student.MyTeacherResponse;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class TeacherListForStudentAdapter extends RecyclerView.Adapter<TeacherListForStudentAdapter.ViewHolder> implements Filterable {

    private Activity context;
    public static List<MyTeacherResponse> responseList;
    public static List<MyTeacherResponse> filteredList;


    public TeacherListForStudentAdapter(Activity context, List<MyTeacherResponse> data) {
        this.context = context;
        this.responseList = data;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_teacher_for_student, viewGroup, false);
        return new ViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, @SuppressLint("RecyclerView") int position) {

        viewHolder.name.setText("" + responseList.get(position).getTeacherBasicResponse().getfName() + " " + responseList.get(position).getTeacherBasicResponse().getlName());
        if (responseList.get(position).getTeacherBasicResponse().getEmail() == null) {
            viewHolder.email.setText("N/A");
        } else if (responseList.get(position).getTeacherBasicResponse().getEmail().trim().isEmpty()) {
            viewHolder.email.setText("N/A");
        } else {
            viewHolder.email.setText("" + responseList.get(position).getTeacherBasicResponse().getEmail());

        }
        viewHolder.mobile.setText("" + responseList.get(position).getTeacherBasicResponse().getMobile());

        viewHolder.call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                call(position);
            }
        });


        viewHolder.gmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (responseList.get(position).getTeacherBasicResponse().getEmail() == null) {
                    Toast.makeText(context, "Email not found", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (responseList.get(position).getTeacherBasicResponse().getEmail().isEmpty()) {
                    Toast.makeText(context, "Email not found", Toast.LENGTH_SHORT).show();
                    return;
                }
                gmail(position);
            }
        });


        viewHolder.whatsApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                whatsApp(position);
            }
        });

        try {
            if (responseList.get(position).getTeacherProfileResponse().getProfileFirebase().getUrl() != null) {
                Glide.with(context)
                        .load(responseList.get(position).getTeacherProfileResponse().getProfileFirebase().getUrl())
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
                viewHolder.progressBar.setVisibility(View.GONE);
                viewHolder.image.setImageDrawable(context.getResources().getDrawable(R.drawable.user));
            }
        } catch (Exception e) {
            viewHolder.progressBar.setVisibility(View.GONE);
            viewHolder.image.setImageDrawable(context.getResources().getDrawable(R.drawable.user));
        }


    }


    @Override
    public int getItemCount() {
        return responseList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, email, mobile;
        LinearLayout call, gmail, whatsApp;
        ImageView image;
        ProgressBar progressBar;

        public ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            email = itemView.findViewById(R.id.email);
            mobile = itemView.findViewById(R.id.mobile);
            call = itemView.findViewById(R.id.call);
            gmail = itemView.findViewById(R.id.gmail);
            whatsApp = itemView.findViewById(R.id.whatsApp);
            image = itemView.findViewById(R.id.image);
            progressBar = itemView.findViewById(R.id.progressBar);


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
                    ArrayList<MyTeacherResponse> filteredList = new ArrayList<>();
                    for (MyTeacherResponse teacherResponse : responseList) {
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
                    TeacherListForStudentAdapter.filteredList = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = filteredList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                filteredList = (ArrayList<MyTeacherResponse>) filterResults.values;
                responseList = filteredList;
                notifyDataSetChanged();
            }
        };
    }


    void call(int position) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(context, new String[]{Manifest.permission.CALL_PHONE}, 1);
        } else {

            Intent callIntent = new Intent(Intent.ACTION_CALL);
            callIntent.setData(Uri.parse("tel:" + responseList.get(position).getTeacherBasicResponse().getMobile()));
            context.startActivity(callIntent);
        }

    }

    void gmail(int position) {

        Intent intent = new Intent(Intent.ACTION_SEND);
        String[] recipients = {responseList.get(position).getTeacherBasicResponse().getEmail()};
        intent.putExtra(Intent.EXTRA_EMAIL, recipients);
        intent.putExtra(Intent.EXTRA_SUBJECT, "from " + context.getResources().getString(R.string.app_name) + " App:");
        intent.putExtra(Intent.EXTRA_TEXT, "");
//        intent.putExtra(Intent.EXTRA_CC,"mailcc@gmail.com");
        intent.setType("text/html");
        intent.setPackage("com.google.android.gm");
        context.startActivity(Intent.createChooser(intent, "Send mail"));

    }


//    void whatsApp(int position) {
//        PackageManager packageManager = context.getPackageManager();
//        Intent i = new Intent(Intent.ACTION_VIEW);
//        try {
//            String url = "https://api.whatsapp.com/send?phone=91" + responseList.get(position).getTeacherBasicResponse().getMobile() + "&text=" + URLEncoder.encode("from " + context.getResources().getString(R.string.app_name) + " App:\n", "UTF-8");
//            i.setPackage("com.whatsapp");
//            i.setData(Uri.parse(url));
//            if (i.resolveActivity(packageManager) != null) {
//                context.startActivity(i);
//            } else {
//                Toast.makeText(context, "WhatsApp not exist", Toast.LENGTH_SHORT).show();
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            Toast.makeText(context, "WhatsApp not exist", Toast.LENGTH_SHORT).show();
//        }
//
//    }


    public void whatsApp(int position) {
        Intent intent = new Intent("android.intent.action.MAIN");
        intent.setComponent(new ComponentName("com.whatsapp", "com.whatsapp.Conversation"));
        intent.putExtra("jid", PhoneNumberUtils.stripSeparators("91" + responseList.get(position).getTeacherBasicResponse().getMobile()) + "@s.whatsapp.net");  //phone number without "+" prefix
        context.startActivity(intent);
    }

}