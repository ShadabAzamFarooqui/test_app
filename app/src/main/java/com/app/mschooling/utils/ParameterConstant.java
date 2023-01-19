package com.app.mschooling.utils;

import android.annotation.SuppressLint;
import android.content.Context;

import com.mschooling.transaction.common.api.Common;

import java.io.Serializable;



public class ParameterConstant {


    public static String getRole(Context context) {
        return  Preferences.getInstance(context).getROLE().equals(Common.Role.ADMIN.value()) ? "admin" : (Preferences.getInstance(context).getROLE().equals(Common.Role.TEACHER.value()) ? "teacher" : "student");
    }

    public static boolean isMSchooling(Context context) {
        return context.getPackageName().equals("com.app.mschooling");
    }

    public static boolean isLittleSteps(Context context) {
        return context.getPackageName().equals("com.app.mschooling.msch4u01");
    }

    public static boolean isAttendanceModeSubjectWise(Context context) {
        return Preferences.getInstance(context).getAttendanceMode().equalsIgnoreCase("Subject Wise");
    }


    public static String getMSchoolingText() {
        return "mSchooling is a professional, most trusted and verified school mobile app to simplify the daily life of educators. mSchooling offers one of the best e-governance ERP and best-in-class mobile applications to educational institutions such as schools, coaching, and colleges across the india.";
    }

    public static String getErrorMessage() {
        return "Some thing went wrong!!!";
    }

    public static int getMaxLine() {
        return 50;
    }

    public static String getPlayStoreUrl() {
        return "https://play.google.com/store/apps/details?id=";
    }

    public static String getDefaultUserUrl() {
        return "https://firebasestorage.googleapis.com/v0/b/m-schooling-pro.appspot.com/o/1%2FDEFAULT_IMAGE%2Fuser.png?alt=media&token=fd36884b-0ba9-425b-a025-1dcf88943bac";
    }

    public static String getFolderName() {
        return "mSchooling";
    }

    public static String getDateFormat() {
        return "dd/MM/yyyy";
    }

    public static String getShareMessage() {
        return  "mSchooling is a professional, most trusted and verified school mobile app developed by mSchooling community an internal entity of Innobins Decision Science Pvt Ltd. to simplify the daily life of educators. mSchooling offers one of the best e-governance ERP and best-in-class mobile applications to educational institutions such as schools, coaching, and colleges across the india.\\n-Get app-https://play.google.com/store/apps/details?id=com.app.mschooling";
    }

    public static String getShareTitle() {
        return "Sent from mSchooling app";
    }


    public static enum Notification implements Serializable{
        CLASS("CLASS"),
        SUBJECT("SUBJECT"),
        TIMETABLE("TIMETABLE"),
        FEE("FEE"),
        SYLLABUS("SYLLABUS"),
        STUDENT_DETAIL("STUDENT_DETAIL"),
        STUDENT_ATTENDANCE("STUDENT_ATTENDANCE"),
        HOMEWORK("HOMEWORK"),
        TEACHER_DETAIL("TEACHER_DETAIL"),
        TEACHER_ATTENDANCE("TEACHER_ATTENDANCE"),
        SALARY("SALARY"),
        LIVE("LIVE"),
        EXAMINATION("EXAMINATION"),
        GALLERY("GALLERY"),
        NOTICE("NOTICE"),
        COMPLAINT("COMPLAINT"),
        APPLICATION("APPLICATION"),
        DISCUSSION("DISCUSSION"),
        EVENT("EVENT"),
        STUDY_MATERIAL("STUDY_MATERIAL"),
        VIDEO("VIDEO");


        String key;

        Notification(String key) {
            this.key = key;
        }

        public String value() {
            return this.key;
        }
    }

}