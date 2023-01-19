package com.app.mschooling.fee.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.app.mschooling.fee.activity.PayFeeActivity;
import com.app.mschooling.fee.activity.student.FeeHistoryListActivity;
import com.app.mschooling.fee.activity.student.StudentFeeDetailListActivity;
import com.app.mschooling.com.R;
import com.app.mschooling.event_handler.EventFee;
import com.app.mschooling.utils.BuzzTextView;
import com.mschooling.transaction.response.fee.StudentFeesResponse;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class CommonStudentFeeListAdapter extends RecyclerView.Adapter<CommonStudentFeeListAdapter.ViewHolder> {


    private Activity context;
     public  List<StudentFeesResponse> responseList;
     public List<StudentFeesResponse> filteredList;


    public CommonStudentFeeListAdapter(Activity context, List<StudentFeesResponse> responsesList) {

        this.context = context;
        this.filteredList = responsesList;
        this.responseList = responsesList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_fee, viewGroup, false);
        return new ViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, @SuppressLint("RecyclerView") int position) {

        StudentFeesResponse response = responseList.get(position);
        viewHolder.name.setText(response.getStudentBasicResponse().getfName() + " " + response.getStudentBasicResponse().getlName());
        viewHolder.enrolmentId.setText(response.getStudentBasicResponse().getEnrollmentId());
        viewHolder.mobile.setText(response.getStudentBasicResponse().getMobile());
        viewHolder.gender.setText(response.getStudentBasicResponse().getGender().value());
        viewHolder.classSection.setText(response.getStudentBasicResponse().getClassName() + " (" + response.getStudentBasicResponse().getSectionName() + ")");

//        Helper.setFireBase(context,response.getFirebase(),viewHolder.image);

//        if (response.getStudentFeeResponse().getSumPaidFee() == response.getStudentFeeResponse().getSumTotalFee()) {
//            viewHolder.totalDue.setText("Paid");
//            viewHolder.totalDue.setTextColor(context.getResources().getColor(R.color.green));
//        } else if (response.getStudentFeeResponse().getSumPaidFee() > response.getStudentFeeResponse().getSumTotalFee()) {
//            double amt = (response.getStudentFeeResponse().getSumTotalFee() - response.getStudentFeeResponse().getSumPaidFee());
//            if (amt < 0) {
//                amt = -1 * amt;
//            }
//            viewHolder.totalDue.setText("Advance ₹" + amt);
//            viewHolder.totalDue.setTextColor(context.getResources().getColor(R.color.green));
//        } else {
//            viewHolder.totalDue.setText("Total Due ₹" + (response.getStudentFeeResponse().getSumTotalFee() - response.getStudentFeeResponse().getSumPaidFee()));
//            viewHolder.totalDue.setTextColor(context.getResources().getColor(R.color.red));
//        }

        viewHolder.totalDue.setText("₹" + (response.getStudentFeeResponse().getSumTotalFee() - response.getStudentFeeResponse().getSumPaidFee()));
        viewHolder.totalDue.setTextColor(context.getResources().getColor(R.color.red));

        viewHolder.totalPaid.setText("₹" + (response.getStudentFeeResponse().getSumPaidFee()));
        viewHolder.totalPaid.setTextColor(context.getResources().getColor(R.color.green));
        viewHolder.pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().post(new EventFee(position));
            }
        });

        viewHolder.viewDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, StudentFeeDetailListActivity.class);
                intent.putExtra("name", response.getStudentBasicResponse().getfName()+" "+response.getStudentBasicResponse().getlName());
                intent.putExtra("enrollmentId", response.getStudentBasicResponse().getEnrollmentId());
                intent.putExtra("intent", "");
                context.startActivityForResult(intent,1);
            }
        });

        viewHolder.history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, FeeHistoryListActivity.class);
                intent.putExtra("name", response.getStudentBasicResponse().getfName()+" "+response.getStudentBasicResponse().getlName());
                intent.putExtra("enrollmentId", response.getStudentBasicResponse().getEnrollmentId());
                context.startActivity(intent);
            }
        }); viewHolder.pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, PayFeeActivity.class);
                intent.putExtra("name", response.getStudentBasicResponse().getfName()+" "+response.getStudentBasicResponse().getlName());
                intent.putExtra("enrollmentId", response.getStudentBasicResponse().getEnrollmentId());
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
        ProgressBar progressBar;
        CircleImageView image;
        BuzzTextView name;
        BuzzTextView totalDue;
        BuzzTextView totalPaid;
        BuzzTextView mobile;
        BuzzTextView classSection;
        BuzzTextView enrolmentId;
        BuzzTextView gender;
        LinearLayout layout;
        LinearLayout pay;
        LinearLayout viewDetail;
        LinearLayout history;
        LinearLayout mainLayout;

        public ViewHolder(View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.name);
            pay = itemView.findViewById(R.id.pay);
            mobile = itemView.findViewById(R.id.mobile);
            gender = itemView.findViewById(R.id.gender);
            enrolmentId = itemView.findViewById(R.id.enrolmentId);
            classSection = itemView.findViewById(R.id.classSection);
            classSection = itemView.findViewById(R.id.classSection);
            progressBar = itemView.findViewById(R.id.progressBar);
            image = itemView.findViewById(R.id.image);
            mainLayout = itemView.findViewById(R.id.mainLayout);
            totalDue = itemView.findViewById(R.id.totalDue);
            totalPaid = itemView.findViewById(R.id.totalPaid);
            history = itemView.findViewById(R.id.history);
            viewDetail = itemView.findViewById(R.id.viewDetail);

        }

    }





}