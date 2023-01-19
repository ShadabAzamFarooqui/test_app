package com.app.mschooling.utils;

import android.content.Context;

import com.app.mschooling.network.pojo.Student;
import com.app.mschooling.network.pojo.Teacher;
import com.mschooling.transaction.filter.ListCriteria;
import com.mschooling.transaction.request.student.UpdateStudentRequest;
import com.mschooling.transaction.response.qrcode.GetQRCodeResponse;
import com.mschooling.transaction.response.student.ClassResponse;
import com.mschooling.transaction.response.student.SectionResponse;
import com.mschooling.transaction.response.subject.GetStandaloneSubjectResponse;
import com.mschooling.transaction.response.teacher.GetAllocationResponse;
import com.mschooling.transaction.response.version.GetVersionResponse;
import com.mschooling.transaction.response.video.VideoResponse;

import java.util.ArrayList;
import java.util.List;

public class AppUser {

    static AppUser appUser;
    UpdateStudentRequest updateStudentRequest;
    List<ClassResponse> classResponseList = new ArrayList<>();
    List<Student> students;
    List<Teacher> teacher;
    GetAllocationResponse allocationResponseList;
    GetVersionResponse versionResponse;
    GetQRCodeResponse qrCodeDetailResponse;
    GetStandaloneSubjectResponse GetSubjectResponse;
    List<SectionResponse> sectionResponse;
    ClassResponse classResponse;
    boolean update;
    int position;
    List<VideoResponse> VideoResponse;

    public static AppUser getInstance() {
        if (AppUser.appUser == null) {
            appUser = new AppUser();
        }
        return appUser;
    }

    public static void clearInstance(Context context) {
        AppUser.appUser = null;
        LocalRepositories.saveAppUser(context, new AppUser());
    }


    public List<VideoResponse> getVideoResponse() {
        return VideoResponse;
    }

    public void setVideoResponse(List<VideoResponse> videoResponse) {
        VideoResponse = videoResponse;
    }

    public GetAllocationResponse getAllocationResponseList() {
        return allocationResponseList;
    }

    public void setAllocationResponseList(GetAllocationResponse allocationResponseList) {
        this.allocationResponseList = allocationResponseList;
    }

    public ClassResponse getClassResponse() {
        return classResponse;
    }

    public void setClassResponse(ClassResponse classResponse) {
        this.classResponse = classResponse;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public boolean isUpdate() {
        return update;
    }

    public void setUpdate(boolean update) {
        this.update = update;
    }

    public List<SectionResponse> getSectionResponse() {
        return sectionResponse;
    }

    public void setSectionResponse(List<SectionResponse> sectionResponse) {
        this.sectionResponse = sectionResponse;
    }


    public GetStandaloneSubjectResponse getGetSubjectResponse() {
        return GetSubjectResponse;
    }

    public void setGetSubjectResponse(GetStandaloneSubjectResponse GetSubjectResponse) {
        this.GetSubjectResponse = GetSubjectResponse;
    }


   


    public List<ClassResponse> getClassResponseList() {
        return classResponseList;
    }

    public void setClassResponseList(List<ClassResponse> classResponseList) {
        this.classResponseList = classResponseList;
    }

    public List<Student> getStudents() {
        return students;
    }

    public void setStudents(List<Student> students) {
        this.students = students;
    }

    public List<Teacher> getTeacher() {
        return teacher;
    }

    public void setTeacher(List<Teacher> teacher) {
        this.teacher = teacher;
    }

    public UpdateStudentRequest getUpdateStudentRequest() {
        return updateStudentRequest;
    }

    public void setUpdateStudentRequest(UpdateStudentRequest updateStudentRequest) {
        this.updateStudentRequest = updateStudentRequest;
    }

    public GetVersionResponse getVersionResponse() {
        return versionResponse;
    }

    public void setVersionResponse(GetVersionResponse versionResponse) {
        this.versionResponse = versionResponse;
    }

    public GetQRCodeResponse getQrCodeDetailResponse() {
        return qrCodeDetailResponse;
    }

    public void setQrCodeDetailResponse(GetQRCodeResponse qrCodeDetailResponse) {
        this.qrCodeDetailResponse = qrCodeDetailResponse;
    }


}
