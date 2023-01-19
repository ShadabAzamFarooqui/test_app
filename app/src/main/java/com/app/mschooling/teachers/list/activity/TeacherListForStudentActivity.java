package com.app.mschooling.teachers.list.activity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.app.mschooling.teachers.list.adapter.TeacherListForStudentAdapter;
import com.app.mschooling.base.activity.BaseActivity;
import com.app.mschooling.com.R;
import com.mschooling.transaction.common.api.Status;
import com.mschooling.transaction.response.student.GetMyTeacherResponse;
import com.mschooling.transaction.response.student.MyTeacherResponse;

import org.greenrobot.eventbus.Subscribe;

import java.util.List;

public class TeacherListForStudentActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener {


    LinearLayout noRecord;
    EditText search;
    RecyclerView recycler_view;
    TeacherListForStudentAdapter adapter;
    List<MyTeacherResponse> detailList;
    SwipeRefreshLayout swipeRefreshLayout;
    boolean isScrolled;

    int pastVisiblesItems, visibleItemCount, totalItemCount;
    private boolean loading = true;
    int pageNo;
    GridLayoutManager layoutManager;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_list_for_student);

        pageNo = 0;
        loading = true;
        search = findViewById(R.id.search);
        noRecord = findViewById(R.id.noRecord);
        recycler_view = findViewById(R.id.recycler_view);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(this);
        toolbarClick(getString(R.string.teachers));

        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    TeacherListForStudentAdapter.filteredList = detailList;
                    TeacherListForStudentAdapter.responseList = detailList;
                    adapter.getFilter().filter(s.toString());
                    isScrolled = true;
                } catch (Exception e) {

                }

            }
        });

        recycler_view.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) {
                    isScrolled = false;
                    visibleItemCount = layoutManager.getChildCount();
                    totalItemCount = layoutManager.getItemCount();
                    pastVisiblesItems = layoutManager.findFirstCompletelyVisibleItemPosition();
                    pageNo++;
                    if (loading) {
                        if ((visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                            loading = false;
//                                ApiCallService.action(TeacherListForStudentActivity.this, "" + pageNo, ApiCallService.Action.ACTION_GET_TEACHER_LIST);
                        }

                    }
                }
            }
        });





    }


    @Subscribe
    public void getMyTeacherList(GetMyTeacherResponse response) {
        if (response.getStatus().value().equals(Status.SUCCESS.value())) {
            swipeRefreshLayout.setRefreshing(false);
            if (pageNo == 0) {
                detailList = response.getMyTeacherResponses();
                recycler_view.setHasFixedSize(true);
                recycler_view.setFocusable(false);
                recycler_view.setNestedScrollingEnabled(false);
                layoutManager = new GridLayoutManager(TeacherListForStudentActivity.this, 1);
                recycler_view.setLayoutManager(layoutManager);
                adapter = new TeacherListForStudentAdapter(TeacherListForStudentActivity.this, detailList);
                recycler_view.setAdapter(adapter);
                if (detailList.size() == 0) {
                    noRecord.setVisibility(View.VISIBLE);
                    swipeRefreshLayout.setVisibility(View.GONE);
                } else {
                    noRecord.setVisibility(View.GONE);
                    swipeRefreshLayout.setVisibility(View.VISIBLE);
                }
            } else {
                detailList.addAll(response.getMyTeacherResponses());
                adapter.notifyDataSetChanged();
                if (response.getMyTeacherResponses().size() > 0) {
                    loading = true;
                    isScrolled = false;

                } else {
                    loading = false;
                    isScrolled = true;
                }
            }
        } else {
            dialogFailed(response.getMessage().getMessage());
        }
    }

    @Override
    public void onRefresh() {
        pageNo = 0;
        loading = true;
        apiCallBack(getApiCommonController().getMyTeacherList());
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    protected void onResume() {
        apiCallBack(getApiCommonController().getMyTeacherList());
        super.onResume();
    }


}
