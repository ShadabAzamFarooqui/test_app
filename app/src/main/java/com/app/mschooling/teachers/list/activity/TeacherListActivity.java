package com.app.mschooling.teachers.list.activity;


import android.animation.Animator;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.app.mschooling.teachers.list.adapter.TeacherListAdapter;
import com.app.mschooling.base.activity.BaseActivity;
import com.app.mschooling.com.R;
import com.app.mschooling.utils.AppUser;
import com.app.mschooling.utils.Preferences;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.mschooling.transaction.common.api.Common;
import com.mschooling.transaction.common.api.Status;
import com.mschooling.transaction.filter.ListCriteria;
import com.mschooling.transaction.request.teacher.DeleteTeacherRequest;
import com.mschooling.transaction.request.teacher.DeleteTeacherRequestList;
import com.mschooling.transaction.request.teacher.DisableTeacherRequest;
import com.mschooling.transaction.request.teacher.EnableTeacherRequest;
import com.mschooling.transaction.response.teacher.DeleteTeacherResponse;
import com.mschooling.transaction.response.teacher.DisableTeacherResponse;
import com.mschooling.transaction.response.teacher.EnableTeacherResponse;
import com.mschooling.transaction.response.teacher.GetTeacherResponse;
import com.mschooling.transaction.response.teacher.TeacherResponse;

import org.greenrobot.eventbus.Subscribe;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;


public class TeacherListActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener {

    LinearLayout noRecord, actionLayout;
    EditText search;
    RecyclerView recyclerView;
    TeacherListAdapter adapter;
    List<TeacherResponse> detailList;
    SwipeRefreshLayout swipeRefreshLayout;
    boolean isScrolled;

    int pastVisibleItems, visibleItemCount, totalItemCount;
    private boolean loading = true;
    int pageNo;
    GridLayoutManager layoutManager;
    Button disableButton;
    Button deleteButton;
    Button enableButton;
    String whereFrom = "";
    FloatingActionButton fab, fab1, fab2;
    LinearLayout fabLayout1, fabLayout2;
    boolean isFABOpen = false;
    Boolean boolForSortingName = false, boolForSortingEnrollment = false;
    ListCriteria criteria;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_student_list);
        Intent intent = getIntent();
        whereFrom = intent.getStringExtra("whereFrom");
        criteria = new Gson().fromJson(getIntent().getStringExtra("criteria"), ListCriteria.class);
        if (criteria==null){
            criteria=new ListCriteria();
        } if (criteria.getState()==null){
            criteria.setState(Common.State.ACTIVATED);
        };
        boolForSortingName = false;
        boolForSortingEnrollment = false;
        pageNo = 0;
        loading = true;
        disableButton = findViewById(R.id.disableButton);
        deleteButton = findViewById(R.id.deleteButton);
        enableButton = findViewById(R.id.enableButton);

        search = findViewById(R.id.search);
        noRecord = findViewById(R.id.noRecord);
        recyclerView = findViewById(R.id.recycler_view);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        actionLayout = findViewById(R.id.actionLayout);
        swipeRefreshLayout.setOnRefreshListener(this);
        toolbarClick(getString(R.string.teachers));
        if (Preferences.getInstance(TeacherListActivity.this).getROLE().equals("ADMIN")) {
            if (whereFrom != null) {
                actionLayout.setVisibility(View.GONE);
            } else {
                actionLayout.setVisibility(View.VISIBLE);
            }
        } else {
            actionLayout.setVisibility(View.GONE);
        }
        fab();

        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (adapter==null){
                    return;
                }
                TeacherListAdapter.filteredList = detailList;
                TeacherListAdapter.responseList = detailList;
                adapter.getFilter().filter(s.toString());
                isScrolled = true;

            }
        });

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) {
                    isScrolled = false;
                    visibleItemCount = layoutManager.getChildCount();
                    totalItemCount = layoutManager.getItemCount();
                    pastVisibleItems = layoutManager.findFirstCompletelyVisibleItemPosition();
                    pageNo++;
                    if (loading) {
                        if ((visibleItemCount + pastVisibleItems) >= totalItemCount) {
                            loading = false;
//                                apiCallBack( getApi().getTeacherList(AppUser.getInstance().getCriteria()));
                        }

                    }
                }
            }
        });




        enableButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EnableTeacherRequest request = new EnableTeacherRequest();
                request.getEnrollmentIdList().addAll(TeacherListAdapter.map.keySet());
                dialogEnableDisable(getString(R.string.enable), getApiCommonController().enableTeacher(request));
            }
        });

        disableButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DisableTeacherRequest request = new DisableTeacherRequest();
                request.getEnrollmentIdList().addAll(TeacherListAdapter.map.keySet());
                dialogEnableDisable(getString(R.string.disable), getApiCommonController().disableTeacher(request));
            }
        });


        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialogDelete();
            }
        });


        closeFABMenu();
        hideActionView();
        pageNo = 0;

        apiCall();

    }

    void apiCall(){

        apiCallBack(getApiCommonController().getTeacherList(criteria.getName(),
                criteria.getEnrollmentId(),
                criteria.getGender(),
                criteria.getState(),
                criteria.getJoiningStartDate(),
                criteria.getJoiningEndDate()
        ));
    }
    void fab() {
        fabLayout1 = (LinearLayout) findViewById(R.id.fabLayout1);
        fabLayout2 = (LinearLayout) findViewById(R.id.fabLayout2);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab1 = (FloatingActionButton) findViewById(R.id.fab1);
        fab2 = (FloatingActionButton) findViewById(R.id.fab2);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isFABOpen) {
                    showFABMenu();
                } else {
                    closeFABMenu();
                }
            }
        });
    }

    public void shortByEnrollment(View view) {
        closeFABMenu();
        if (detailList==null){
            return;
        }
        if (detailList.size()==0){
            return;
        }
        if (boolForSortingEnrollment) {
            boolForSortingEnrollment = false;
            Collections.sort(detailList, new Comparator<TeacherResponse>() {
                public int compare(TeacherResponse v1, TeacherResponse v2) {
                    return v1.getTeacherBasicResponse().getEnrollmentId().compareTo(v2.getTeacherBasicResponse().getEnrollmentId());
                }
            });
        } else {
            boolForSortingEnrollment = true;
            Collections.sort(detailList, new Comparator<TeacherResponse>() {
                public int compare(TeacherResponse v1, TeacherResponse v2) {
                    return v2.getTeacherBasicResponse().getEnrollmentId().compareTo(v1.getTeacherBasicResponse().getEnrollmentId());
                }
            });
        }
        adapter.notifyDataSetChanged();
    }

    public void sortByName(View view) {
        closeFABMenu();
        if (detailList==null){
            return;
        }
        if (detailList.size()==0){
            return;
        }
        if (boolForSortingName) {
            boolForSortingName = false;
            Collections.sort(detailList, new Comparator<TeacherResponse>() {
                public int compare(TeacherResponse v1, TeacherResponse v2) {
                    return v1.getTeacherBasicResponse().getfName().compareTo(v2.getTeacherBasicResponse().getfName());
                }
            });
        } else {
            boolForSortingName = true;
            Collections.sort(detailList, new Comparator<TeacherResponse>() {
                public int compare(TeacherResponse v1, TeacherResponse v2) {
                    return v2.getTeacherBasicResponse().getfName().compareTo(v1.getTeacherBasicResponse().getfName());
                }
            });
        }
        adapter.notifyDataSetChanged();
    }

    private void showFABMenu() {
        isFABOpen = true;
        fabLayout1.setVisibility(View.VISIBLE);
        fabLayout2.setVisibility(View.VISIBLE);
        fab.animate().rotationBy(180);
        fabLayout1.animate().translationY(-getResources().getDimension(R.dimen.dp_55));
        fabLayout2.animate().translationY(-getResources().getDimension(R.dimen.dp_100));
    }

    private void closeFABMenu() {
        isFABOpen = false;
        fab.animate().rotation(0);
        fabLayout1.animate().translationY(0);
        fabLayout2.animate().translationY(0);
        fabLayout2.animate().translationY(0).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                if (!isFABOpen) {
                    fabLayout1.setVisibility(View.GONE);
                    fabLayout2.setVisibility(View.GONE);
                }
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
    }

    @Subscribe
    public void timeout(String msg) {

    }


    @Subscribe
    public void getTeacherList(GetTeacherResponse response) {
        if (response.getStatus().value().equals(Status.SUCCESS.value())) {
            swipeRefreshLayout.setRefreshing(false);
            hideActionView();
            if (pageNo == 0) {
                detailList = response.getTeacherResponses();
                recyclerView.setHasFixedSize(true);
                recyclerView.setFocusable(false);
                recyclerView.setNestedScrollingEnabled(false);
                layoutManager = new GridLayoutManager(TeacherListActivity.this, 2);
                recyclerView.setLayoutManager(layoutManager);
                adapter = new TeacherListAdapter(TeacherListActivity.this, detailList, enableButton, disableButton, deleteButton, whereFrom,criteria);
                recyclerView.setAdapter(adapter);
                if (detailList.size() == 0) {
                    noRecord.setVisibility(View.VISIBLE);
                    swipeRefreshLayout.setVisibility(View.GONE);
                } else {
                    noRecord.setVisibility(View.GONE);
                    swipeRefreshLayout.setVisibility(View.VISIBLE);
                }
            } else {
                detailList.addAll(response.getTeacherResponses());
                adapter.notifyDataSetChanged();
                if (response.getTeacherResponses().size() > 0) {
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
        hideActionView();
        pageNo = 0;
        loading = true;
        apiCall();
        swipeRefreshLayout.setRefreshing(false);
    }


    @Subscribe
    public void deleteTeacher(DeleteTeacherResponse response) {
        if (response.getStatus().value().equals(Status.SUCCESS.value())) {
            dialogSuccess(response.getMessage().getMessage());
            pageNo = 0;
            apiCall();
        } else {
            dialogFailed(response.getMessage().getMessage());
        }
    }


    @Subscribe
    public void enableTeacher(EnableTeacherResponse response) {
        if (response.getStatus().value().equals(Status.SUCCESS.value())) {
            pageNo = 0;
            dialogSuccess(response.getMessage().getMessage());
            apiCall();
        } else {
            dialogFailed(response.getMessage().getMessage());
        }
    }

    @Subscribe
    public void disableTeacher(DisableTeacherResponse response) {
        if (response.getStatus().value().equals(Status.SUCCESS.value())) {
            dialogSuccess(response.getMessage().getMessage());
            pageNo = 0;
            apiCall();
        } else {
            dialogFailed(response.getMessage().getMessage());
        }
    }


    public void getDialogDelete() {
        Dialog dialog = new Dialog(this);
        dialog.getWindow().requestFeature(1);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        dialog.setContentView(R.layout.delete_dialog);
        dialog.setCancelable(false);
        TextView delete = (TextView) dialog.findViewById(R.id.delete);
        TextView cancel = (TextView) dialog.findViewById(R.id.cancel);
        TextView description = (TextView) dialog.findViewById(R.id.description);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DeleteTeacherRequestList request = new DeleteTeacherRequestList();
                Set<String> set = TeacherListAdapter.map.keySet();
                for (String enrolmentId : set) {
                    DeleteTeacherRequest obj = new DeleteTeacherRequest();
                    obj.setEnrollmentId(enrolmentId);
                    obj.setDescription(description.getText().toString());
                    request.getDeleteTeacherRequestList().add(obj);
                }
                apiCallBack(getApiCommonController().deleteTeacher(request));
                dialog.dismiss();

            }
        });
        dialog.show();
    }

    void hideActionView(){
        enableButton.setVisibility(View.GONE);
        disableButton.setVisibility(View.GONE);
        deleteButton.setVisibility(View.GONE);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        closeFABMenu();
        hideActionView();
        pageNo = 0;
        apiCall();
    }
}





