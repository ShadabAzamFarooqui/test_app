package com.app.mschooling.students.list.activity;


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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.app.mschooling.base.activity.BaseActivity;
import com.app.mschooling.com.R;
import com.app.mschooling.students.list.adapter.StudentListAdapter;
import com.app.mschooling.utils.ParameterConstant;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.mschooling.transaction.common.api.Common;
import com.mschooling.transaction.common.api.Status;
import com.mschooling.transaction.filter.ListCriteria;
import com.mschooling.transaction.request.student.DeleteStudentRequest;
import com.mschooling.transaction.request.student.DeleteStudentRequestList;
import com.mschooling.transaction.request.student.DisableStudentRequest;
import com.mschooling.transaction.request.student.EnableStudentRequest;
import com.mschooling.transaction.response.student.DeleteStudentResponse;
import com.mschooling.transaction.response.student.DisableStudentResponse;
import com.mschooling.transaction.response.student.EnableStudentResponse;
import com.mschooling.transaction.response.student.GetStudentResponse;
import com.mschooling.transaction.response.student.StudentResponse;

import org.greenrobot.eventbus.Subscribe;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;


public class StudentsListActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener {

    LinearLayout noRecord;
    EditText search;
    RecyclerView recyclerView;
    StudentListAdapter adapter;
    List<StudentResponse> detailList;
    SwipeRefreshLayout swipeRefreshLayout;
    boolean isScrolled;

    int pastVisibleItems, visibleItemCount, totalItemCount;
    boolean loading = true;
    int pageNo;
    GridLayoutManager layoutManager;
    Button disableButton;
    Button deleteButton;
    Button enableButton;
    FloatingActionButton fab, fab1, fab2;
    LinearLayout fabLayout1, fabLayout2;
    boolean isFABOpen = false;
    String intentString = "";
    Boolean boolForSortingName = false, boolForSortingEnrollment = false;

    ListCriteria criteria;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_student_list);
        toolbarClick(getString(R.string.students));
        Intent intent = getIntent();
        intentString = intent.getStringExtra("intent");
        boolForSortingName = false;
        boolForSortingEnrollment = false;
        loading = true;
        noRecord = findViewById(R.id.noRecord);
        search = findViewById(R.id.search);
        recyclerView = findViewById(R.id.recycler_view);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(this);
        disableButton = findViewById(R.id.disableButton);
        deleteButton = findViewById(R.id.deleteButton);
        enableButton = findViewById(R.id.enableButton);
        fab();
        criteria = new Gson().fromJson(getIntent().getStringExtra("criteria"), ListCriteria.class);
        if (criteria==null){
            criteria=new ListCriteria();
        } if (criteria.getState()==null){
            criteria.setState(Common.State.ACTIVATED);
        }
        closeFABMenu();
        hideActionView();
        pageNo = 0;

        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (adapter == null) {
                    return;
                }
                adapter.filteredList = detailList;
                adapter.responseList = detailList;
                adapter.getFilter().filter(s.toString());
                isScrolled = true;
            }
        });

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
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
                        }

                    }
                }
            }
        });




        enableButton.setOnClickListener(v -> {
            EnableStudentRequest request = new EnableStudentRequest();
            request.getEnrollmentIdList().addAll(StudentListAdapter.map.keySet());
            dialogEnableDisable(getString(R.string.enable), getApiCommonController().enableStudent(request));

        });


        disableButton.setOnClickListener(v -> {
            DisableStudentRequest request = new DisableStudentRequest();
            request.getEnrollmentIdList().addAll(StudentListAdapter.map.keySet());
            dialogEnableDisable(getString(R.string.disable), getApiCommonController().disableStudent(request));

        });


        deleteButton.setOnClickListener(v -> getDialogDelete());
        apiCall();
    }

    void fab() {
        fabLayout1 = findViewById(R.id.fabLayout1);
        fabLayout2 = findViewById(R.id.fabLayout2);
        fab = findViewById(R.id.fab);
        fab1 = findViewById(R.id.fab1);
        fab2 = findViewById(R.id.fab2);

        fab.setOnClickListener(view -> {
            if (!isFABOpen) {
                showFABMenu();
            } else {
                closeFABMenu();
            }
        });
    }

    public void shortByEnrollment(View view) {
        closeFABMenu();
        if (detailList == null) {
            return;
        }
        if (detailList.size() == 0) {
            return;
        }
        if (boolForSortingEnrollment) {
            boolForSortingEnrollment = false;
            Collections.sort(detailList, new Comparator<StudentResponse>() {
                public int compare(StudentResponse v1, StudentResponse v2) {
                    return v1.getStudentBasicResponse().getEnrollmentId().compareTo(v2.getStudentBasicResponse().getEnrollmentId());
                }
            });
        } else {
            boolForSortingEnrollment = true;
            Collections.sort(detailList, new Comparator<StudentResponse>() {
                public int compare(StudentResponse v1, StudentResponse v2) {
                    return v2.getStudentBasicResponse().getEnrollmentId().compareTo(v1.getStudentBasicResponse().getEnrollmentId());
                }
            });
        }
        adapter.notifyDataSetChanged();
    }

    public void sortByName(View view) {
        closeFABMenu();
        if (detailList == null) {
            return;
        }
        if (detailList.size() == 0) {
            return;
        }
        if (boolForSortingName) {
            boolForSortingName = false;
            Collections.sort(detailList, new Comparator<StudentResponse>() {
                public int compare(StudentResponse v1, StudentResponse v2) {
                    return v1.getStudentBasicResponse().getfName().compareTo(v2.getStudentBasicResponse().getfName());
                }
            });
        } else {
            boolForSortingName = true;
            Collections.sort(detailList, new Comparator<StudentResponse>() {
                public int compare(StudentResponse v1, StudentResponse v2) {
                    return v2.getStudentBasicResponse().getfName().compareTo(v1.getStudentBasicResponse().getfName());
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
    public void getStudentList(GetStudentResponse response) {
        if (response.getStatus().value().equals(Status.SUCCESS.value())) {
            swipeRefreshLayout.setRefreshing(false);
            if (pageNo == 0) {
                detailList = response.getStudentResponses();
                recyclerView.setHasFixedSize(true);
                recyclerView.setFocusable(false);
                recyclerView.setNestedScrollingEnabled(false);
                layoutManager = new GridLayoutManager(StudentsListActivity.this, 2);
                recyclerView.setLayoutManager(layoutManager);
                adapter = new StudentListAdapter(StudentsListActivity.this, detailList, enableButton, disableButton, deleteButton, intentString,criteria);
                recyclerView.setAdapter(adapter);

                if (detailList.size() == 0) {
                    hideActionView();
                    noRecord.setVisibility(View.VISIBLE);
                    swipeRefreshLayout.setVisibility(View.GONE);
                    fab.setVisibility(View.GONE);
                } else {
                    noRecord.setVisibility(View.GONE);
                    swipeRefreshLayout.setVisibility(View.VISIBLE);
                    fab.setVisibility(View.VISIBLE);
                }
            } else {
                detailList.addAll(response.getStudentResponses());
                adapter.notifyDataSetChanged();
                if (response.getStudentResponses().size() > 0) {
                    loading = true;
                    isScrolled = false;
                } else {
                    loading = false;
                    isScrolled = true;
                }
            }
        } else {
            dialogError(response.getMessage().getMessage());
        }
    }

    @Override
    public void onRefresh() {
        hideActionView();
        pageNo = 0;
        loading = true;
        swipeRefreshLayout.setRefreshing(false);
        apiCall();
    }


    @Subscribe
    public void deleteStudent(DeleteStudentResponse response) {
        if (response.getStatus().value().equals(Status.SUCCESS.value())) {
            hideActionView();
            pageNo = 0;
            dialogSuccess(response.getMessage().getMessage());
            apiCall();
        } else {
            dialogFailed(response.getMessage().getMessage());
        }
    }

    @Subscribe
    public void disableStudent(DisableStudentResponse response) {
        if (response.getStatus().value().equals(Status.SUCCESS.value())) {
            hideActionView();
            pageNo = 0;
            dialogSuccess(response.getMessage().getMessage());
            apiCall();
        } else {
            dialogFailed(response.getMessage().getMessage());
        }
    }


    @Subscribe
    public void enableStudent(EnableStudentResponse response) {
        if (response.getStatus().value().equals(Status.SUCCESS.value())) {
            hideActionView();
            pageNo = 0;
            dialogSuccess(response.getMessage().getMessage());
            apiCall();
        } else {
            dialogFailed(response.getMessage().getMessage());
        }
    }

    void hideActionView() {
        enableButton.setVisibility(View.GONE);
        disableButton.setVisibility(View.GONE);
        deleteButton.setVisibility(View.GONE);
    }


    public void getDialogDelete() {
        Dialog dialog = new Dialog(StudentsListActivity.this);
        dialog.getWindow().requestFeature(1);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        dialog.setContentView(R.layout.delete_dialog);
        dialog.setCancelable(false);
        TextView delete = dialog.findViewById(R.id.delete);
        TextView cancel = dialog.findViewById(R.id.cancel);
        TextView description = dialog.findViewById(R.id.description);
        cancel.setOnClickListener(view -> dialog.dismiss());
        delete.setOnClickListener(v -> {
            DeleteStudentRequestList request = new DeleteStudentRequestList();

            Set<String> set = StudentListAdapter.map.keySet();
            for (String enrolmentId : set) {
                DeleteStudentRequest obj = new DeleteStudentRequest();
                obj.setEnrollmentId(enrolmentId);
                obj.setDescription(description.getText().toString());
                request.getDeleteStudentRequestList().add(obj);
            }


            apiCallBack(getApiCommonController().deleteStudent(request));
            dialog.dismiss();

        });
        dialog.show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        closeFABMenu();
        hideActionView();
        pageNo = 0;
        apiCall();
    }

    void apiCall() {
        apiCallBack(getApiCommonController().getStudentList(
                ParameterConstant.getRole(getApplicationContext()),
                criteria.getName(),
                criteria.getEnrollmentId(),
                criteria.getGender(),
                criteria.getState(),
                criteria.getJoiningStartDate(),
                criteria.getJoiningEndDate(),
                criteria.getClassIds(),
                criteria.getSectionIds()));
    }
}





