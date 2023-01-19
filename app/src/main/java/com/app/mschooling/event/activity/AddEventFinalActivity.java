package com.app.mschooling.event.activity;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

import com.app.mschooling.base.activity.BaseActivity;
import com.app.mschooling.com.R;
import com.app.mschooling.utils.ParameterConstant;
import com.mschooling.transaction.common.api.Status;
import com.mschooling.transaction.request.event.AddEventRequest;
import com.mschooling.transaction.response.event.AddEventResponse;

import org.greenrobot.eventbus.Subscribe;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AddEventFinalActivity extends BaseActivity {
    TextView date;
    Button submit;
    TextView name;
    TextView description;
    String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_event_dialog);

        date = (TextView) findViewById(R.id.date);
        submit = (Button) findViewById(R.id.submit);
        name = (TextView) findViewById(R.id.name);
        description = (TextView) findViewById(R.id.description);
        date.setText(getIntent().getStringExtra("date"));

        id = getIntent().getStringExtra("id");

        if (id == null) {
            toolbarClick(getString(R.string.add_event));
        } else {
            toolbarClick(getString(R.string.update_event));
            name.setText(getIntent().getStringExtra("name"));
            description.setText(getIntent().getStringExtra("description"));
        }

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (name.getText().toString().trim().isEmpty()) {
                    dialogError(getString(R.string.please_enter_event_name));
                    return;
                }
                if (description.getText().toString().trim().isEmpty()) {
                    dialogError(getString(R.string.please_enter_event_description));
                    return;
                }
                AddEventRequest request = new AddEventRequest();
                request.setId(id);
                request.setDate(date.getText().toString());
                request.setName(name.getText().toString().trim());
                request.setDescription(description.getText().toString().trim());
                apiCallBack(getApiCommonController().addEvent(request));
            }
        });

    }


    @Subscribe
    public void addEvent(AddEventResponse response) {
        if (response.getStatus().value() == Status.SUCCESS.value()) {
            dialogSuccessFinish(response.getMessage().getMessage());
        } else {
            dialogFailed(response.getMessage().getMessage());
        }
    }

    public void date(View view) {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(AddEventFinalActivity.this, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(year, monthOfYear, dayOfMonth);
                String d = new SimpleDateFormat(ParameterConstant.getDateFormat() , Locale.US).format(calendar.getTime());
                date.setText(d);
            }

        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        datePickerDialog.show();
    }

}
