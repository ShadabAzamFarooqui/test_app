package com.app.mschooling.panding.fragment;

import static com.app.mschooling.panding.fragment.PendingRequestFragment.searchEdit;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.mschooling.panding.adapter.TeacherPendingAdapter;
import com.app.mschooling.base.fragment.BaseFragment;
import com.app.mschooling.com.R;
import com.app.mschooling.network.response.ApproveDeclineTeacherResponse;
import com.app.mschooling.utils.Helper;
import com.app.mschooling.utils.Preferences;
import com.app.mschooling.utils.progress_dialog.MyProgressDialog;
import com.mschooling.transaction.common.api.Status;
import com.mschooling.transaction.response.pending.GetPendingTaskTeacherResponse;
import com.mschooling.transaction.response.pending.PendingTaskResponse;

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener;

import org.greenrobot.eventbus.Subscribe;

import java.util.List;

@SuppressLint("ValidFragment")
public class PendingTeachersFragment extends BaseFragment {

    LinearLayout noRecord;
    RecyclerView recyclerView;
    TeacherPendingAdapter adapter;
    List<PendingTaskResponse> response;
    int pastVisibleItems, visibleItemCount, totalItemCount;
    private boolean loading = true;
    GridLayoutManager layoutManager;
    boolean isKeyPadOpen;
    MyProgressDialog myProgressDialog;;

    @Nullable
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_student_pending_request, container, false);
        Preferences.getInstance(getContext()).setPageNo2(0);
        loading = true;
        recyclerView = view.findViewById(R.id.recyclerView);
        search = view.findViewById(R.id.search);
        noRecord = view.findViewById(R.id.noRecord);
        myProgressDialog=new MyProgressDialog(getActivity());
        if (Helper.isNetworkAvailable(getContext())){
            myProgressDialog.show();
        }


        apiCallBackWithout(getApiCommonController().getTeacherPending(String.valueOf(Preferences.getInstance(getContext()).getPageNo2())));

        try {
            searchEdit.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    try {
                        TeacherPendingAdapter.filteredList = response;
                        TeacherPendingAdapter.responseList = response;
                        adapter.getFilter().filter(s.toString());
                    } catch (Exception e) {

                    }

                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) {
                    visibleItemCount = layoutManager.getChildCount();
                    totalItemCount = layoutManager.getItemCount();
                    pastVisibleItems = layoutManager.findFirstCompletelyVisibleItemPosition();
                    if (loading) {
                        if ((visibleItemCount + pastVisibleItems) >= totalItemCount) {
                            loading = false;
                            Preferences.getInstance(getContext()).setPageNo2(Preferences.getInstance(getContext()).getPageNo2() + 1);
                            apiCallBack(getApiCommonController().getTeacherPending(String.valueOf(Preferences.getInstance(getContext()).getPageNo2())));
                        }

                    }
                }

            }
        });

        KeyboardVisibilityEvent.setEventListener(getActivity(), new KeyboardVisibilityEventListener() {
            @Override
            public void onVisibilityChanged(boolean isOpen) {
                isKeyPadOpen = isOpen;
            }
        });


        return view;
    }


    @Subscribe
    public void getPendingList(GetPendingTaskTeacherResponse response) {
        myProgressDialog.dismiss();
        if (response.getStatus().value().equals(Status.SUCCESS.value())) {
            if (Preferences.getInstance(getContext()).getPageNo2() == 0) {
                this.response = response.getPendingTaskResponses();
                recyclerView.setHasFixedSize(true);
                recyclerView.setFocusable(false);
                recyclerView.setNestedScrollingEnabled(false);
                layoutManager = new GridLayoutManager(getActivity(), 1);
                recyclerView.setLayoutManager(layoutManager);
                adapter = new TeacherPendingAdapter(getActivity(), this, this.response);
                recyclerView.setAdapter(adapter);
                if (this.response.size() == 0) {
                    recyclerView.setVisibility(View.GONE);
                    noRecord.setVisibility(View.VISIBLE);
                } else {
                    recyclerView.setVisibility(View.VISIBLE);
                    noRecord.setVisibility(View.GONE);
                }
            } else {
                this.response.addAll(response.getPendingTaskResponses());
                adapter.notifyDataSetChanged();
                if (response.getPendingTaskResponses().size() > 0) {
                    loading = true;
                } else {
                    loading = false;
                }
            }
        } else {
            dialogFailed(response.getMessage().getMessage());
        }
    }

    @Subscribe
    public void approveDecline(ApproveDeclineTeacherResponse response) {
        if (response.getStatus().value().equals(Status.SUCCESS.value())) {
            dialogSuccess(response.getMessage().getMessage());
            loading = true;
            Preferences.getInstance(getContext()).setPageNo2(0);
            apiCallBack(getApiCommonController().getTeacherPending(String.valueOf(Preferences.getInstance(getContext()).getPageNo2())));
        } else {
            dialogFailed(response.getMessage().getMessage());
        }
    }


}





