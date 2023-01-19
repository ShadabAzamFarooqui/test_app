package com.app.mschooling.icard.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.mschooling.icard.adapter.IdCardListAdapter;
import com.app.mschooling.base.activity.BaseActivity;
import com.app.mschooling.com.R;
import com.mschooling.transaction.common.api.Status;
import com.mschooling.transaction.filter.ICardCriteria;
import com.mschooling.transaction.response.icard.GetICardResponse;

import org.greenrobot.eventbus.Subscribe;

import butterknife.BindView;
import butterknife.ButterKnife;

public class IdCardListActivity extends BaseActivity {

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.noRecord)
    LinearLayout noRecord;
    GetICardResponse response;
    IdCardListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_id_card_list);
        ButterKnife.bind(this);
        toolbarClick(getString(R.string.tool_i_cards));
        ICardCriteria criteria = new ICardCriteria();
        criteria.setClassId(getIntent().getStringExtra("classId"));
        criteria.setSectionId(getIntent().getStringExtra("sectionId"));
        apiCallBack(getApiCommonController().getIdCard(criteria));
    }


    @Subscribe
    public void getFeeList(GetICardResponse response) {
        if (response.getStatus().value().equals(Status.SUCCESS.value())) {
            this.response = response;
            recyclerView.setHasFixedSize(true);
            recyclerView.setFocusable(false);
            recyclerView.setNestedScrollingEnabled(false);
            recyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(), 2));
            adapter = new IdCardListAdapter(this, response.getiCardResponses());
            recyclerView.setAdapter(adapter);
            if (response.getiCardResponses().size() == 0) {
                noRecord.setVisibility(View.VISIBLE);
            } else {
                noRecord.setVisibility(View.GONE);
            }

        } else {
            dialogError(response.getMessage().getMessage());
        }
    }

}
