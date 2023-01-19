package com.app.mschooling.network;

import com.app.mschooling.network.response.ApproveDeclineStudentResponse;
import com.app.mschooling.network.response.ApproveDeclineTeacherResponse;
import com.app.mschooling.network.response.ServerResponse;
import com.mschooling.transaction.common.api.Common;
import com.mschooling.transaction.filter.ClassCriteria;
import com.mschooling.transaction.filter.FeeDetailCriteria;
import com.mschooling.transaction.filter.FeeHistoryCriteria;
import com.mschooling.transaction.filter.ICardCriteria;
import com.mschooling.transaction.filter.SubjectCriteria;
import com.mschooling.transaction.request.CreateSessionRequest;
import com.mschooling.transaction.request.application.AddApplicationReasonRequest;
import com.mschooling.transaction.request.application.AddApplicationRequest;
import com.mschooling.transaction.request.attendance.AddStandaloneTeacherAttendanceRequest;
import com.mschooling.transaction.request.attendance.AddStudentAttendanceRequest;
import com.mschooling.transaction.request.attendance.AddTeacherAttendanceRequest;
import com.mschooling.transaction.request.broadcast.AddBroadcastRequest;
import com.mschooling.transaction.request.complain.AddComplainReasonRequest;
import com.mschooling.transaction.request.complain.AddComplainRequest;
import com.mschooling.transaction.request.discussion.AddDiscussionRequest;
import com.mschooling.transaction.request.event.AddEventRequest;
import com.mschooling.transaction.request.examination.AddExaminationRequest;
import com.mschooling.transaction.request.examination.AddStudentExaminationRequest;
import com.mschooling.transaction.request.fee.AddFeeSetupRequest;
import com.mschooling.transaction.request.fee.AddStudentFeeRequest;
import com.mschooling.transaction.request.fee.UpdateStudentFeeRequest;
import com.mschooling.transaction.request.feedback.AddFeedbackRequest;
import com.mschooling.transaction.request.gallery.AddGalleryRequest;
import com.mschooling.transaction.request.homework.AddStudentWorksheetRequest;
import com.mschooling.transaction.request.homework.AddWorksheetRequest;
import com.mschooling.transaction.request.homework.HomeworkCriteria;
import com.mschooling.transaction.request.homework.UpdateStudentWorksheetRequest;
import com.mschooling.transaction.request.live.AddLiveRequest;
import com.mschooling.transaction.request.live.LiveCriteria;
import com.mschooling.transaction.request.live.NotifyLiveRequest;
import com.mschooling.transaction.request.login.AuthenticationRequest;
import com.mschooling.transaction.request.login.ChangePasscodeRequest;
import com.mschooling.transaction.request.login.LoginRequest;
import com.mschooling.transaction.request.notice.AddNoticeRequest;
import com.mschooling.transaction.request.prayer.AddPrayerRequest;
import com.mschooling.transaction.request.profile.UpdateAdminProfileRequest;
import com.mschooling.transaction.request.profile.UpdateStudentProfileRequest;
import com.mschooling.transaction.request.profile.UpdateTeacherProfileRequest;
import com.mschooling.transaction.request.quiz.AddAssignQuizRequest;
import com.mschooling.transaction.request.quiz.AddQuizQuestionRequest;
import com.mschooling.transaction.request.quiz.AddQuizRequest;
import com.mschooling.transaction.request.quiz.AddStudentSubmitQuizRequest;
import com.mschooling.transaction.request.register.AddAccountRequest;
import com.mschooling.transaction.request.register.AddRegisterRequest;
import com.mschooling.transaction.request.school.info.UpdateSchoolInfoRequest;
import com.mschooling.transaction.request.setting.AddSettingRequest;
import com.mschooling.transaction.request.student.AddClassRequest;
import com.mschooling.transaction.request.student.AddStudentRequest;
import com.mschooling.transaction.request.student.DeleteClassRequest;
import com.mschooling.transaction.request.student.DeleteSectionRequest;
import com.mschooling.transaction.request.student.DeleteStudentRequestList;
import com.mschooling.transaction.request.student.DisableStudentRequest;
import com.mschooling.transaction.request.student.EnableStudentRequest;
import com.mschooling.transaction.request.student.UpdateStudentRequest;
import com.mschooling.transaction.request.study.AddStudyCategoryRequest;
import com.mschooling.transaction.request.study.AddStudyRequest;
import com.mschooling.transaction.request.subject.AddSubjectRequest;
import com.mschooling.transaction.request.subject.DeleteSubjectGroupRequest;
import com.mschooling.transaction.request.subject.DeleteSubjectRequest;
import com.mschooling.transaction.request.subject.UpdateSubjectGroupRequest;
import com.mschooling.transaction.request.syllabus.AddSyllabusRequest;
import com.mschooling.transaction.request.syllabus.SyllabusCriteria;
import com.mschooling.transaction.request.teacher.AddTeacherRequest;
import com.mschooling.transaction.request.teacher.DeleteTeacherRequestList;
import com.mschooling.transaction.request.teacher.DisableTeacherRequest;
import com.mschooling.transaction.request.teacher.EnableTeacherRequest;
import com.mschooling.transaction.request.teacher.UpdateTeacherRequest;
import com.mschooling.transaction.request.timetable.AddTimeTableRequestList;
import com.mschooling.transaction.request.timetable.TimetableCriteria;
import com.mschooling.transaction.request.video.AddVideoCategoryRequest;
import com.mschooling.transaction.request.video.AddVideoRequest;
import com.mschooling.transaction.request.zoom.AddZoomRequest;
import com.mschooling.transaction.response.about.us.GetAboutUsResponse;
import com.mschooling.transaction.response.application.AddApplicationReasonResponse;
import com.mschooling.transaction.response.application.AddApplicationResponse;
import com.mschooling.transaction.response.application.DeleteApplicationReasonResponse;
import com.mschooling.transaction.response.application.GetApplicationReasonResponse;
import com.mschooling.transaction.response.application.GetApplicationResponse;
import com.mschooling.transaction.response.attendance.AddStandaloneTeacherAttendanceResponse;
import com.mschooling.transaction.response.attendance.AddStudentAttendanceResponse;
import com.mschooling.transaction.response.attendance.AddTeacherAttendanceResponse;
import com.mschooling.transaction.response.attendance.GetAttendanceMonthResponse;
import com.mschooling.transaction.response.attendance.GetStandaloneTeacherAttendanceResponse;
import com.mschooling.transaction.response.attendance.GetStudentAttendanceDetailResponse;
import com.mschooling.transaction.response.attendance.GetStudentAttendanceResponse;
import com.mschooling.transaction.response.attendance.GetTeacherAttendanceDetailResponse;
import com.mschooling.transaction.response.attendance.GetTeacherAttendanceResponse;
import com.mschooling.transaction.response.birthday.GetBirthdayResponse;
import com.mschooling.transaction.response.broadcast.AddBroadcastResponse;
import com.mschooling.transaction.response.broadcast.GetBroadcastResponse;
import com.mschooling.transaction.response.complain.AddComplainReasonResponse;
import com.mschooling.transaction.response.complain.AddComplainResponse;
import com.mschooling.transaction.response.complain.DeleteComplainReasonResponse;
import com.mschooling.transaction.response.complain.GetComplainReasonResponse;
import com.mschooling.transaction.response.complain.GetComplainResponse;
import com.mschooling.transaction.response.configuration.GetConfigurationResponse;
import com.mschooling.transaction.response.configuration.GetDailyConfigurationResponse;
import com.mschooling.transaction.response.dashboard.GetStudentCountResponse;
import com.mschooling.transaction.response.discussion.AddDiscussionResponse;
import com.mschooling.transaction.response.discussion.GetDiscussionResponse;
import com.mschooling.transaction.response.event.AddEventResponse;
import com.mschooling.transaction.response.event.DeleteEventResponse;
import com.mschooling.transaction.response.event.GetEventResponse;
import com.mschooling.transaction.response.examination.AddExaminationResponse;
import com.mschooling.transaction.response.examination.DeleteExaminationResponse;
import com.mschooling.transaction.response.examination.GetExaminationResponse;
import com.mschooling.transaction.response.examination.GetStudentExaminationClassResultResponse;
import com.mschooling.transaction.response.examination.GetStudentExaminationResponse;
import com.mschooling.transaction.response.examination.PublishExaminationResponse;
import com.mschooling.transaction.response.fee.AddFeeSetupResponse;
import com.mschooling.transaction.response.fee.AddStudentFeeResponse;
import com.mschooling.transaction.response.fee.GetFeeSetupResponse;
import com.mschooling.transaction.response.fee.GetStudentFeeDetailResponse;
import com.mschooling.transaction.response.fee.GetStudentFeeHistoryResponse;
import com.mschooling.transaction.response.fee.GetStudentFeeResponse;
import com.mschooling.transaction.response.fee.UpdateStudentFeeResponse;
import com.mschooling.transaction.response.feedback.AddFeedbackResponse;
import com.mschooling.transaction.response.gallery.AddGalleryResponse;
import com.mschooling.transaction.response.gallery.DeleteGalleryResponse;
import com.mschooling.transaction.response.gallery.GetGalleryResponse;
import com.mschooling.transaction.response.help.GetHelpResponse;
import com.mschooling.transaction.response.homework.AddStudentWorksheetResponse;
import com.mschooling.transaction.response.homework.AddWorksheetResponse;
import com.mschooling.transaction.response.homework.DeleteWorksheetResponse;
import com.mschooling.transaction.response.homework.GetStudentUploadedWorksheetResponse;
import com.mschooling.transaction.response.homework.GetStudentWorksheetResponse;
import com.mschooling.transaction.response.homework.GetWorksheetResponse;
import com.mschooling.transaction.response.homework.UpdateStudentWorksheetResponse;
import com.mschooling.transaction.response.icard.GetICardResponse;
import com.mschooling.transaction.response.live.AddLiveResponse;
import com.mschooling.transaction.response.live.DeleteLiveResponse;
import com.mschooling.transaction.response.live.GetLiveResponse;
import com.mschooling.transaction.response.live.NotifyLiveResponse;
import com.mschooling.transaction.response.live.StartLiveResponse;
import com.mschooling.transaction.response.login.AuthenticationResponse;
import com.mschooling.transaction.response.login.ChangePasscodeResponse;
import com.mschooling.transaction.response.login.ForgetResponse;
import com.mschooling.transaction.response.login.GetRoleResponse;
import com.mschooling.transaction.response.login.LoginResponse;
import com.mschooling.transaction.response.login.UpdateTokenResponse;
import com.mschooling.transaction.response.notice.AddNoticeResponse;
import com.mschooling.transaction.response.notice.DeleteNoticeResponse;
import com.mschooling.transaction.response.notice.GetNoticeResponse;
import com.mschooling.transaction.response.notification.GetNotificationResponse;
import com.mschooling.transaction.response.notification.NotificationResponse;
import com.mschooling.transaction.response.pending.GetPendingTaskStudentResponse;
import com.mschooling.transaction.response.pending.GetPendingTaskTeacherResponse;
import com.mschooling.transaction.response.prayer.AddPrayerResponse;
import com.mschooling.transaction.response.prayer.DeletePrayerResponse;
import com.mschooling.transaction.response.prayer.GetPrayerResponse;
import com.mschooling.transaction.response.profile.GetAdminProfileResponse;
import com.mschooling.transaction.response.profile.GetStudentProfileResponse;
import com.mschooling.transaction.response.profile.GetTeacherProfileResponse;
import com.mschooling.transaction.response.profile.UpdateAdminMobileResponse;
import com.mschooling.transaction.response.profile.UpdateAdminProfileResponse;
import com.mschooling.transaction.response.profile.UpdateStudentProfileResponse;
import com.mschooling.transaction.response.profile.UpdateTeacherProfileResponse;
import com.mschooling.transaction.response.qrcode.GetQRCodeResponse;
import com.mschooling.transaction.response.quiz.AddQuizMappingResponse;
import com.mschooling.transaction.response.quiz.AddQuizQuestionResponse;
import com.mschooling.transaction.response.quiz.AddQuizResponse;
import com.mschooling.transaction.response.quiz.AddStudentSubmitQuizResponse;
import com.mschooling.transaction.response.quiz.DeleteAssignQuizResponse;
import com.mschooling.transaction.response.quiz.DeleteQuizQuestionResponse;
import com.mschooling.transaction.response.quiz.DeleteQuizResponse;
import com.mschooling.transaction.response.quiz.GetAssignQuizResponse;
import com.mschooling.transaction.response.quiz.GetQuizQuestionResponse;
import com.mschooling.transaction.response.quiz.GetQuizResponse;
import com.mschooling.transaction.response.quiz.GetStudentQuizQuestionResponse;
import com.mschooling.transaction.response.quiz.GetStudentQuizResponse;
import com.mschooling.transaction.response.quiz.GetStudentSubmitQuizResponse;
import com.mschooling.transaction.response.quiz.GetSubmitQuizResponse;
import com.mschooling.transaction.response.registration.AddAccountResponse;
import com.mschooling.transaction.response.registration.AddRegistrationResponse;
import com.mschooling.transaction.response.registration.GetValidateRegistrationResponse;
import com.mschooling.transaction.response.registration.SendOTPResponse;
import com.mschooling.transaction.response.resource.AddResourceResponse;
import com.mschooling.transaction.response.school.info.GetSchoolInfoResponse;
import com.mschooling.transaction.response.school.info.UpdateSchoolInfoResponse;
import com.mschooling.transaction.response.section.GetStandaloneClassResponse;
import com.mschooling.transaction.response.session.CreateSessionResponse;
import com.mschooling.transaction.response.session.GetSessionResponse;
import com.mschooling.transaction.response.session.UpdateSessionResponse;
import com.mschooling.transaction.response.setting.AddSettingResponse;
import com.mschooling.transaction.response.setting.GetSettingResponse;
import com.mschooling.transaction.response.student.AddClassResponse;
import com.mschooling.transaction.response.student.AddStudentResponse;
import com.mschooling.transaction.response.student.DeleteClassResponse;
import com.mschooling.transaction.response.student.DeleteSectionResponse;
import com.mschooling.transaction.response.student.DeleteStudentResponse;
import com.mschooling.transaction.response.student.DisableStudentResponse;
import com.mschooling.transaction.response.student.EnableStudentResponse;
import com.mschooling.transaction.response.student.GetClassResponse;
import com.mschooling.transaction.response.student.GetExternalClassResponse;
import com.mschooling.transaction.response.student.GetMyTeacherResponse;
import com.mschooling.transaction.response.student.GetStudentDetailResponse;
import com.mschooling.transaction.response.student.GetStudentResponse;
import com.mschooling.transaction.response.student.UpdateStudentResponse;
import com.mschooling.transaction.response.study.AddStudyCategoryResponse;
import com.mschooling.transaction.response.study.AddStudyResponse;
import com.mschooling.transaction.response.study.DeleteStudyResponse;
import com.mschooling.transaction.response.study.GetStudyCategoryResponse;
import com.mschooling.transaction.response.study.GetStudyResponse;
import com.mschooling.transaction.response.subject.AddSubjectResponse;
import com.mschooling.transaction.response.subject.DeleteSubjectGroupResponse;
import com.mschooling.transaction.response.subject.DeleteSubjectResponse;
import com.mschooling.transaction.response.subject.GetExternalSubjectResponse;
import com.mschooling.transaction.response.subject.GetStandaloneSubjectResponse;
import com.mschooling.transaction.response.subject.GetSubjectResponse;
import com.mschooling.transaction.response.subject.UpdateSubjectGroupResponse;
import com.mschooling.transaction.response.subscription.AddSubscriptionResponse;
import com.mschooling.transaction.response.syllabus.AddSyllabusResponse;
import com.mschooling.transaction.response.syllabus.DeleteSyllabusResponse;
import com.mschooling.transaction.response.syllabus.GetSyllabusResponse;
import com.mschooling.transaction.response.teacher.AddTeacherResponse;
import com.mschooling.transaction.response.teacher.DeleteTeacherResponse;
import com.mschooling.transaction.response.teacher.DisableTeacherResponse;
import com.mschooling.transaction.response.teacher.EnableTeacherResponse;
import com.mschooling.transaction.response.teacher.GetAllocationResponse;
import com.mschooling.transaction.response.teacher.GetTeacherDetailResponse;
import com.mschooling.transaction.response.teacher.GetTeacherResponse;
import com.mschooling.transaction.response.teacher.UpdateTeacherResponse;
import com.mschooling.transaction.response.timetable.AddTimeTableResponse;
import com.mschooling.transaction.response.timetable.GetTimeTableResponseList;
import com.mschooling.transaction.response.tnc.GetTNCResponse;
import com.mschooling.transaction.response.utility.GetAPIKeyResponse;
import com.mschooling.transaction.response.version.GetVersionResponse;
import com.mschooling.transaction.response.video.AddVideoResponse;
import com.mschooling.transaction.response.video.DeleteVideoResponse;
import com.mschooling.transaction.response.video.GetVideoCategoryResponse;
import com.mschooling.transaction.response.video.GetVideoResponse;
import com.mschooling.transaction.response.zoom.AddZoomResponse;
import com.mschooling.transaction.response.zoom.GetZoomResponse;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface Api {

//    String getAddressApi = "https://locationapi-100.appspot.com/location/reverse?lat=28&lon=77";

    String BASE_URL_DEVELOPMENT = "http://13.234.187.226:8081/";
    String BASE_URL_PRODUCTION = "http://13.234.187.226:8080/";
    String COMMON_CONTROLLER = "sps-mschooling/";
    String NOTIFICATION_CONTROLLER = "sps-notification/";


    @POST(COMMON_CONTROLLER + "login/")
    Call<LoginResponse> login(@Body LoginRequest login);

    @GET(COMMON_CONTROLLER + "admin/student/download/file/{operation}/{format}/")
    Call<ResponseBody> downloadExcel(@Path("operation") String operation, @Path("format") String format);

    @POST(COMMON_CONTROLLER + "admin/student/add/")
    Call<AddStudentResponse> addStudent(@Body AddStudentRequest request);

    @POST(COMMON_CONTROLLER + "admin/student/update/")
    Call<UpdateStudentResponse> updateStudent(@Body UpdateStudentRequest request);


    @GET(COMMON_CONTROLLER + "{role}/student/get")
    Call<GetStudentResponse> getStudentList(@Path("role") String role,
                                            @Query("name") String name,
                                            @Query("enrollmentId") String enrollmentId,
                                            @Query("gender ") Common.Gender gender,
                                            @Query("state") Common.State state,
                                            @Query("joiningStartDate") String joiningStartDate,
                                            @Query("joiningEndDate") String joiningEndDate,
                                            @Query("classIds") List<String> classIds,
                                            @Query("sectionIds") List<String> sectionIds);


    @POST(COMMON_CONTROLLER + "admin/student/detail/get/{enrollmentId}/")
    Call<GetStudentDetailResponse> getStudentDetails(@Path("enrollmentId") String enrollmentId);

    @POST(COMMON_CONTROLLER + "admin/student/class/get/")
    Call<GetClassResponse> getClassList(@Body ClassCriteria criteria);

    @GET(COMMON_CONTROLLER + "{role}/student/class/get/")
    Call<GetStandaloneClassResponse> getClassList(@Path("role") String role);

    @GET(COMMON_CONTROLLER + "public/external/class/get")
    Call<GetExternalClassResponse> getClassExternalList(@Query("schoolId") String enrollmentId);

    @POST(COMMON_CONTROLLER + "admin/student/delete/")
    Call<DeleteStudentResponse> deleteStudent(@Body DeleteStudentRequestList enrollmentId);

    @POST(COMMON_CONTROLLER + "admin/student/disable/")
    Call<DisableStudentResponse> disableStudent(@Body DisableStudentRequest enrollmentId);

    @POST(COMMON_CONTROLLER + "admin/student/enable/")
    Call<EnableStudentResponse> enableStudent(@Body EnableStudentRequest enrollmentId);

    @POST(COMMON_CONTROLLER + "common/notice/add/")
    Call<AddNoticeResponse> postNotice(@Body AddNoticeRequest addNoticeRequest);

    @POST(COMMON_CONTROLLER + "common/notice/get/{index}/")
    Call<GetNoticeResponse> getNotice(@Path("index") String page);

    @POST(COMMON_CONTROLLER + "common/video/add/")
    Call<AddVideoResponse> addVideo(@Body AddVideoRequest addVideoRequest);

    @POST(COMMON_CONTROLLER + "common/notice/delete/{id}/")
    Call<DeleteNoticeResponse> deleteNotice(@Path("id") String id);

    @POST(COMMON_CONTROLLER + "common/video/delete/{id1}/{id2}/")
    Call<DeleteVideoResponse> deleteVideo(@Path("id1") String categoryId, @Path("id2") String id);

    @POST(COMMON_CONTROLLER + "admin/broadcast/{operation}/")
    Call<AddBroadcastResponse> sendBroadCast(@Path("operation") String operation, @Body AddBroadcastRequest addBroadcastRequest);

    @GET(COMMON_CONTROLLER + "public/version/get/{version}/")
    Call<GetVersionResponse> version(@Path("version") String version);

    @POST(COMMON_CONTROLLER + "common/feedback/add/")
    Call<AddFeedbackResponse> feedback(@Body AddFeedbackRequest request);

    @POST(COMMON_CONTROLLER + "common/complain/reason/get/")
    Call<GetComplainReasonResponse> complainReasonList();

    @POST(COMMON_CONTROLLER + "common/complain/reason/delete/{id}/")
    Call<DeleteComplainReasonResponse> deleteComplainReason(@Path("id") String id);

    @POST(COMMON_CONTROLLER + "common/complain/add/")
    Call<AddComplainResponse> addComplain(@Body AddComplainRequest request);

    @POST(COMMON_CONTROLLER + "common/complain/get/{index}/")
    Call<GetComplainResponse> getComplain(@Path("index") String page);

    @POST(COMMON_CONTROLLER + "public/teacher/signup/")
    Call<AddTeacherResponse> signUpTeacher(@Body AddTeacherRequest request);

    @POST(COMMON_CONTROLLER + "public/student/signup/")
    Call<AddStudentResponse> signUpStudent(@Body AddStudentRequest request);

    @POST(COMMON_CONTROLLER + "authenticate/")
    Call<AuthenticationResponse> authenticate(@Body AuthenticationRequest request);

    @POST(COMMON_CONTROLLER + "common/complain/reason/add/")
    Call<AddComplainReasonResponse> addReason(@Body AddComplainReasonRequest request);

    @POST(COMMON_CONTROLLER + "admin/student/class/add/")
    Call<AddClassResponse> addClass(@Body AddClassRequest request);

    @POST(COMMON_CONTROLLER + "common/application/get/{index}/")
    Call<GetApplicationResponse> getApplication(@Path("index") String index);

    @POST(COMMON_CONTROLLER + "common/application/add/")
    Call<AddApplicationResponse> addApplication(@Body AddApplicationRequest request);

    @POST(COMMON_CONTROLLER + "common/application/reason/get/")
    Call<GetApplicationReasonResponse> getApplicationReason();

    @POST(COMMON_CONTROLLER + "common/application/reason/add/")
    Call<AddApplicationReasonResponse> addApplicationReason(@Body AddApplicationReasonRequest request);

    @POST(COMMON_CONTROLLER + "common/application/reason/delete/{id}/")
    Call<DeleteApplicationReasonResponse> deleteApplicationReason(@Path("id") String id);

    @Multipart
    @POST(COMMON_CONTROLLER + "admin/student/upload/file/{format}/")
    Call<ServerResponse> uploadFile(@Path("format") String format, @Part MultipartBody.Part filePart);


    @GET(COMMON_CONTROLLER + "admin/teacher/get")
    Call<GetTeacherResponse> getTeacherList(@Query("name") String name,
                                            @Query("enrollmentId") String enrollmentId,
                                            @Query("gender") Common.Gender gender,
                                            @Query("state") Common.State state,
                                            @Query("joiningStartDate") String joiningStartDate,
                                            @Query("joiningEndDate") String joiningEndDate);

    @POST(COMMON_CONTROLLER + "admin/student/my-teacher/get/")
    Call<GetMyTeacherResponse> getMyTeacherList();

    @GET(COMMON_CONTROLLER + "admin/teacher/allocation/get")
    Call<GetAllocationResponse> getAllocation(@Query("enrollmentId") String enrollmentId);


    @GET(COMMON_CONTROLLER + "teacher/allocation/get")
    Call<GetAllocationResponse> getTeacherAllocation();

    @POST(COMMON_CONTROLLER + "admin/teacher/detail/get/{enrollmentId}/")
    Call<GetTeacherDetailResponse> getTeacherDetails(@Path("enrollmentId") String enrollmentId);

    @POST(COMMON_CONTROLLER + "admin/teacher/delete/")
    Call<DeleteTeacherResponse> deleteTeacher(@Body DeleteTeacherRequestList enrollmentId);

    @POST(COMMON_CONTROLLER + "admin/teacher/disable/")
    Call<DisableTeacherResponse> disableTeacher(@Body DisableTeacherRequest enrollmentId);

    @POST(COMMON_CONTROLLER + "admin/teacher/enable/")
    Call<EnableTeacherResponse> enableTeacher(@Body EnableTeacherRequest enrollmentId);

    @POST(COMMON_CONTROLLER + "admin/teacher/update/")
    Call<UpdateTeacherResponse> updateTeacher(@Body UpdateTeacherRequest request);

    @POST(COMMON_CONTROLLER + "admin/student/pendingTask/get/{index}/")
    Call<GetPendingTaskStudentResponse> getStudentPending(@Path("index") String index);

    @POST(COMMON_CONTROLLER + "admin/teacher/pendingTask/get/{index}/")
    Call<GetPendingTaskTeacherResponse> getTeacherPending(@Path("index") String index);

    @POST(COMMON_CONTROLLER + "admin/pendingTask/review/{operation}/{id}/")
    Call<ApproveDeclineTeacherResponse> approveDeclineTeacher(@Path("operation") String operation, @Path("id") String index);

    @POST(COMMON_CONTROLLER + "admin/pendingTask/review/{operation}/{id}/")
    Call<ApproveDeclineStudentResponse> approveDeclineStudent(@Path("operation") String operation, @Path("id") String index);

    @POST(COMMON_CONTROLLER + "admin/teacher/add/")
    Call<AddTeacherResponse> addTeacher(@Body AddTeacherRequest request);

    @POST(COMMON_CONTROLLER + "forget/{mobile}/")
    Call<ForgetResponse> forgetEnrol(@Path("mobile") String mobile);

    @POST(COMMON_CONTROLLER + "logout/")
    Call<ForgetResponse> logout();

    @POST(COMMON_CONTROLLER + "common/event/get/")
    Call<GetEventResponse> getEventList();

    @POST(COMMON_CONTROLLER + "common/event/add/")
    Call<AddEventResponse> addEvent(@Body AddEventRequest request);

    @POST(COMMON_CONTROLLER + "admin/subject/add/")
    Call<AddSubjectResponse> addSubject(@Body AddSubjectRequest request);


    @GET(COMMON_CONTROLLER + "{role}/student/attendance/get")
    Call<GetStudentAttendanceResponse> getAttendanceStudent(@Path("role") String role,
                                                            @Query("date") String date,
                                                            @Query("attendance") Common.Attendance attendance,
                                                            @Query("classId") String classId,
                                                            @Query("sectionId") String sectionId,
                                                            @Query("subjectId") String subjectId
    );

    @GET(COMMON_CONTROLLER + "{role}/student/attendance/detail/get")
    Call<GetStudentAttendanceDetailResponse> getAttendanceStudent(@Path("role") String role, @Query("month") int month, @Query("year") int year, @Query("enrollmentId") String enrollmentId);

    @POST(COMMON_CONTROLLER + "admin/student/attendance/add/")
    Call<AddStudentAttendanceResponse> submitAttendance(@Body AddStudentAttendanceRequest addAttendanceRequestList);

    @POST(COMMON_CONTROLLER + "admin/teacher/attendance/add/")
    Call<AddTeacherAttendanceResponse> submitAllTeacherAttendance(@Body AddTeacherAttendanceRequest addAttendanceRequestList);


    @POST(COMMON_CONTROLLER + "teacher/attendance/add/")
    Call<AddStandaloneTeacherAttendanceResponse> submitSingleTeacherAttendance(@Body AddStandaloneTeacherAttendanceRequest addAttendanceRequestList);


    @GET(COMMON_CONTROLLER + "teacher/attendance/get")
    Call<GetStandaloneTeacherAttendanceResponse> getSingleTeacherAttendance(@Query("date") String date);


    @POST(COMMON_CONTROLLER + "common/syllabus/get/")
    Call<GetSyllabusResponse> getSyllabus(@Body SyllabusCriteria criteria);

    @POST(COMMON_CONTROLLER + "public/qrCode/get/{code}/")
    Call<GetQRCodeResponse> getQrCodeDetail(@Path("code") String code);

    @POST(COMMON_CONTROLLER + "admin/subject/get/")
    Call<GetSubjectResponse> getSubjectList(@Body SubjectCriteria criteria);

    @GET(COMMON_CONTROLLER + "{role}/subject/get/")
    Call<GetStandaloneSubjectResponse> getSubjectList(@Path("role") String role, @Query("classId") String classId, @Query("sectionId") String sectionId);


    @GET(COMMON_CONTROLLER + "public/external/subject/get")
    Call<GetExternalSubjectResponse> getExternalSubjectList(@Query("classId") String classId, @Query("schoolId") String schoolId);


    @POST(COMMON_CONTROLLER + "common/timetable/save/")
    Call<AddTimeTableResponse> saveTimeTable(@Body AddTimeTableRequestList request);

    @POST(COMMON_CONTROLLER + "common/timetable/get/")
    Call<GetTimeTableResponseList> getTimeTable(@Body TimetableCriteria request);

    @GET(COMMON_CONTROLLER + "public/about-us/get/")
    Call<GetAboutUsResponse> getAboutUs();


    @POST(COMMON_CONTROLLER + "change/")
    Call<ChangePasscodeResponse> changePasscode(@Body ChangePasscodeRequest request);

    @POST(COMMON_CONTROLLER + "firebase/token/update/{token}/")
    Call<UpdateTokenResponse> updateToken(@Path("token") String param);

    @POST(COMMON_CONTROLLER + "admin/broadcast/get/{index}/")
    Call<GetBroadcastResponse> broadcastList(@Path("index") int index);


    @POST(COMMON_CONTROLLER + "dashboard/student/count/")
    Call<GetStudentCountResponse> getDashboardCount();

    @POST(COMMON_CONTROLLER + "{role}/fee/get/{id}/")
    Call<GetFeeSetupResponse> getFeeList(@Path("role") String role,@Path("id") String page);

    @POST(COMMON_CONTROLLER + "admin/fee/student/detail/")
    Call<GetStudentFeeDetailResponse> getStudentFee(@Body FeeDetailCriteria page);

//    removed
//    @POST(COMMON_CONTROLLER + "admin/fee/student/history/")
//    Call<GetStudentFeeHistoryResponse> getFeeHistory(@Body FeeHistoryCriteria page);

    @GET(COMMON_CONTROLLER + "{role}/fee/student/history/get")
    Call<GetStudentFeeHistoryResponse> getFeeHistory(@Path("role") String role, @Query("enrollmentId") String enrollmentId );


    @GET(COMMON_CONTROLLER + "{role}/fee/student/get")
    Call<GetStudentFeeResponse> getStudentFeeList(@Path("role") String role, @Query("classId") String classId, @Query("sectionId") String sectionId);

    @POST(COMMON_CONTROLLER + "admin/fee/add/")
    Call<AddFeeSetupResponse> addFee(@Body AddFeeSetupRequest request);

    @POST(COMMON_CONTROLLER + "admin/fee/student/add/")
    Call<AddStudentFeeResponse> addStudentFee(@Body AddStudentFeeRequest request);

    @POST(COMMON_CONTROLLER + "admin/fee/student/update/")
    Call<UpdateStudentFeeResponse> updateStudentFee(@Body UpdateStudentFeeRequest request);

    @POST(COMMON_CONTROLLER + "admin/student/icard/get/")
    Call<GetICardResponse> getIdCard(@Body ICardCriteria request);

    @POST(COMMON_CONTROLLER + "admin/student/class/delete/")
    Call<DeleteClassResponse> deleteClass(@Body DeleteClassRequest request);

    @POST(COMMON_CONTROLLER + "admin/student/section/delete/")
    Call<DeleteSectionResponse> deleteSection(@Body DeleteSectionRequest request);

    @POST(COMMON_CONTROLLER + "admin/subject/delete/")
    Call<DeleteSubjectResponse> deleteSubject(@Body DeleteSubjectRequest request);

    @POST(COMMON_CONTROLLER + "common/discussion/add/")
    Call<AddDiscussionResponse> addDiscussion(@Body AddDiscussionRequest request);

    @POST(COMMON_CONTROLLER + "common/discussion/get/{index}/")
    Call<GetDiscussionResponse> getDiscussion(@Path("index") Integer page);


    @POST(COMMON_CONTROLLER + "common/student/profile/view/")
    Call<GetStudentProfileResponse> getStudentProfile();

    @POST(COMMON_CONTROLLER + "common/admin/profile/view/")
    Call<GetAdminProfileResponse> getAdminProfile();

    @POST(COMMON_CONTROLLER + "common/teacher/profile/view/")
    Call<GetTeacherProfileResponse> getTeacherProfile();

    @POST(COMMON_CONTROLLER + "common/student/profile/update/")
    Call<UpdateStudentProfileResponse> updateStudentProfile(@Body UpdateStudentProfileRequest page);

    @POST(COMMON_CONTROLLER + "common/admin/profile/update/")
    Call<UpdateAdminProfileResponse> updateAdminProfile(@Body UpdateAdminProfileRequest page);

    @POST(COMMON_CONTROLLER + "common/teacher/profile/update/")
    Call<UpdateTeacherProfileResponse> updateTeacherProfile(@Body UpdateTeacherProfileRequest page);

    @GET(COMMON_CONTROLLER + "common/employee/quiz/get/{index}/")
    Call<GetQuizResponse> getQuizList(@Path("index") String index);

    @GET(COMMON_CONTROLLER + "common/student/quiz/get/{id}/")
    Call<GetStudentQuizResponse> getQuizListStudent(@Path("id") String id);

    @POST(COMMON_CONTROLLER + "common/employee/quiz/add/")
    Call<AddQuizResponse> addQuiz(@Body AddQuizRequest request);

    @GET(COMMON_CONTROLLER + "common/employee/quiz/assign/get/{id}/")
    Call<GetAssignQuizResponse> getAssignedQuiz(@Path("id") String index);

    @GET(COMMON_CONTROLLER + "common/employee/quiz/question/get/{id}/")
    Call<GetQuizQuestionResponse> getAdminQuizQuestionList(@Path("id") String request);

    @GET(COMMON_CONTROLLER + "common/student/quiz/question/get/{id1}/{id2}/")
    Call<GetStudentQuizQuestionResponse> getStudentQuizQuestionList(@Path("id1") String id1, @Path("id2") String id2);

    @POST(COMMON_CONTROLLER + "common/employee/quiz/question/add/")
    Call<AddQuizQuestionResponse> addQuestion(@Body AddQuizQuestionRequest request);

    @DELETE(COMMON_CONTROLLER + "common/employee/quiz/question/delete/{id1}/{id2}/")
    Call<DeleteQuizQuestionResponse> deleteQuestion(@Path("id1") String id1, @Path("id2") String id2);

    @DELETE(COMMON_CONTROLLER + "common/employee/quiz/delete/{id}/")
    Call<DeleteQuizResponse> deleteQuiz(@Path("id") String id1);

    @Multipart
    @POST(COMMON_CONTROLLER + "common/resource/add")
    Call<AddResourceResponse> uploadResourceAdmin(@Query("type") Common.DocType docType, @Query("enrollmentId") String enrollmentId, @Part MultipartBody.Part filePart);

    @Multipart
    @POST(COMMON_CONTROLLER + "common/resource/add")
    Call<AddResourceResponse> uploadResource(@Query("type") Common.DocType docType, @Part MultipartBody.Part filePart);


    @POST(COMMON_CONTROLLER + "common/employee/quiz/assign/add/")
    Call<AddQuizMappingResponse> quizMapping(@Body AddAssignQuizRequest request);

    @POST(COMMON_CONTROLLER + "common/gallery/get/{index}/")
    Call<GetGalleryResponse> galleryList(@Path("index") Integer page);

    @POST(COMMON_CONTROLLER + "common/gallery/delete/{id}/")
    Call<DeleteGalleryResponse> deleteGallery(@Path("id") String id);

    @POST(COMMON_CONTROLLER + "common/student/quiz/submit/add/")
    Call<AddStudentSubmitQuizResponse> submitQuiz(@Body AddStudentSubmitQuizRequest request);

    @DELETE(COMMON_CONTROLLER + "common/employee/quiz/assign/delete/{id1}/{id2}/")
    Call<DeleteAssignQuizResponse> unassignedQuiz(@Path("id1") String id1, @Path("id2") String id2);

    @GET(COMMON_CONTROLLER + "common/student/quiz/submit/get/")
    Call<GetStudentSubmitQuizResponse> getStudentQuizResult();

    @GET(COMMON_CONTROLLER + "common/employee/quiz/submit/get/{id}/{enrollmentId}/")
    Call<GetSubmitQuizResponse> getQuizResult(@Path("id") String id1, @Path("enrollmentId") String enrollmentId);

    @GET(COMMON_CONTROLLER + "public/exception/report/")
    Call<ServerResponse> reportIssue(@Query("exception") String exception);

    @GET(COMMON_CONTROLLER + "public/help/")
    Call<GetHelpResponse> help();

    @POST(COMMON_CONTROLLER + "common/gallery/add/")
    Call<AddGalleryResponse> addGallery(@Body AddGalleryRequest request);

    @POST(COMMON_CONTROLLER + "common/syllabus/add/")
    Call<AddSyllabusResponse> addSyllabus(@Body AddSyllabusRequest request);

    @GET(COMMON_CONTROLLER + "public/tnc/get/")
    Call<GetTNCResponse> tnc();

    @GET(COMMON_CONTROLLER + "common/school-info/get/")
    Call<GetSchoolInfoResponse> getSchoolInfo();

    @POST(COMMON_CONTROLLER + "public/account/add/")
    Call<AddAccountResponse> registerSchool(@Body AddAccountRequest request);

    @GET(COMMON_CONTROLLER + "{role}/notification/get")
    Call<GetNotificationResponse> getNotification(@Path("role") String role);

    @GET(COMMON_CONTROLLER + "admin/teacher/attendance/detail/get")
    Call<GetTeacherAttendanceDetailResponse> getTeachersAllAttendanceFomAdminRole(@Query("month") int month, @Query("year") int year, @Query("enrollmentId") String enrollmentId);

    @GET(COMMON_CONTROLLER + "teacher/attendance/detail/get")
    Call<GetTeacherAttendanceDetailResponse> getTeachersAllAttendanceFomTeacherRole(@Query("month") int month, @Query("year") int year);

    @POST(COMMON_CONTROLLER + "admin/subscription/add/")
    Call<AddSubscriptionResponse> applySubscription();

    @POST(COMMON_CONTROLLER + "common/event/delete/{id}/")
    Call<DeleteEventResponse> deleteEvent(@Path("id") String id);

    @GET(COMMON_CONTROLLER + "admin/teacher/attendance/get")
    Call<GetTeacherAttendanceResponse> getAbsence(@Query("date") String date,
                                                  @Query("attendance") Common.Attendance attendance);

    @POST(COMMON_CONTROLLER + "common/syllabus/delete/{id1}/{id2}/")
    Call<DeleteSyllabusResponse> deleteSyllabus(@Path("id1") String id1, @Path("id2") String id2);

    @POST(COMMON_CONTROLLER + "admin/setting/get/")
    Call<GetSettingResponse> getSetting();

    @GET(COMMON_CONTROLLER + "{role}/examination/get")
    Call<GetExaminationResponse> getExaminationListAdminRole(@Path("role") String role);


    @GET(COMMON_CONTROLLER + "{role}/examination/student/get")
    Call<GetStudentExaminationResponse> getMarksExaminationList(@Path("role") String role,
                                                                @Query("id") String id,
                                                                @Query("classId") String classId,
                                                                @Query("sectionId") String sectionId,
                                                                @Query("subjectId") String subjectId);

    @POST(COMMON_CONTROLLER + "{role}/examination/student/add/")
    Call<AddExaminationResponse> addMarksExaminationList(@Path("role") String role, @Body AddStudentExaminationRequest criteria);

    @POST(COMMON_CONTROLLER + "{role}/examination/add/")
    Call<AddExaminationResponse> createExam(@Path("role") String role, @Body AddExaminationRequest criteria);

    @DELETE(COMMON_CONTROLLER + "{role}/examination/delete/{id}/")
    Call<DeleteExaminationResponse> deleteExam(@Path("role") String role, @Path("id") String id);

    @GET(COMMON_CONTROLLER + "{role}/examination/publish")
    Call<PublishExaminationResponse> publishExam(@Path("role") String role, @Query("id") String id, @Query("isPublish") boolean isPublish);

//    video

    @POST(COMMON_CONTROLLER + "common/video/get/{id}/")
    Call<GetVideoResponse> getVideoList(@Path("id") String page);

    @GET(COMMON_CONTROLLER + "common/video/category/get")
    Call<GetVideoCategoryResponse> getVideoCategoryList(@Query("index") String page, @Query("classId") String classId);

    @POST(COMMON_CONTROLLER + "common/video/category/add/")
    Call<AddVideoResponse> addVideoCategory(@Body AddVideoCategoryRequest addVideoRequest);

    @POST(COMMON_CONTROLLER + "common/video/category/delete/{id}/")
    Call<DeleteVideoResponse> deleteVideoCategory(@Path("id") String id);

    //    study material
//    1. category
    @GET(COMMON_CONTROLLER + "common/study/category/get")
    Call<GetStudyCategoryResponse> getStudyMaterialCategoryList(@Query("index") String page, @Query("classId") String classId);

    @POST(COMMON_CONTROLLER + "common/study/category/add/")
    Call<AddStudyCategoryResponse> addStudyMaterialCategory(@Body AddStudyCategoryRequest addVideoRequest);

    @POST(COMMON_CONTROLLER + "common/study/category/delete/{id}/")
    Call<DeleteStudyResponse> deleteStudyMaterialCategory(@Path("id") String id);

//    2. material

    @POST(COMMON_CONTROLLER + "common/study/get/{id}/")
    Call<GetStudyResponse> getStudyMaterialList(@Path("id") String page);

    @POST(COMMON_CONTROLLER + "common/study/add/")
    Call<AddStudyResponse> addStudyMaterial(@Body AddStudyRequest addVideoRequest);

    @POST(COMMON_CONTROLLER + "common/study/delete/{id1}/{id2}/")
    Call<DeleteStudyResponse> deleteStudyMaterial(@Path("id1") String id1, @Path("id2") String id2);

    @POST(COMMON_CONTROLLER + "common/zoom/credential/add/")
    Call<AddZoomResponse> updateZoomCredential(@Body AddZoomRequest request);

    @POST(COMMON_CONTROLLER + "common/zoom/credential/get/")
    Call<GetZoomResponse> getZoomCredential();

    @POST(COMMON_CONTROLLER + "admin/setting/add/")
    Call<AddSettingResponse> updateSetting(@Body AddSettingRequest request);

    @GET(COMMON_CONTROLLER + "report/examination/student/get/{id}/")
    Call<Object> result(@Path("id") String id);

    @GET(COMMON_CONTROLLER + "common/prayer/get/")
    Call<GetPrayerResponse> getPrayer();

    @POST(COMMON_CONTROLLER + "common/prayer/add/")
    Call<AddPrayerResponse> addPrayer(@Body AddPrayerRequest request);

    @DELETE(COMMON_CONTROLLER + "common/prayer/delete/{id}/")
    Call<DeletePrayerResponse> deletePrayer(@Path("id") String id);

    //  live class ->
    @POST(COMMON_CONTROLLER + "common/live/add/")
    Call<AddLiveResponse> createLiveClass(@Body AddLiveRequest request);

    @POST(COMMON_CONTROLLER + "common/live/get/")
    Call<GetLiveResponse> getLiveClassList(@Body LiveCriteria request);

    @POST(COMMON_CONTROLLER + "common/live/start/{param}/")
    Call<StartLiveResponse> startLiveClass(@Path("param") String param);

    @POST(COMMON_CONTROLLER + "common/live/notify/")
    Call<NotifyLiveResponse> notifyLiveClass(@Body NotifyLiveRequest request);

    @DELETE(COMMON_CONTROLLER + "common/live/delete/{id}/")
    Call<DeleteLiveResponse> deleteLive(@Path("id") String id);

    //==========

    @POST(COMMON_CONTROLLER + "common/school-info/update/")
    Call<UpdateSchoolInfoResponse> updateSchool(@Body() UpdateSchoolInfoRequest request);

    @GET(COMMON_CONTROLLER + "common/configuration/get/")
    Call<GetConfigurationResponse> configuration();

    @GET(COMMON_CONTROLLER + "common/session/get/")
    Call<GetSessionResponse> academicSession();

    @POST(COMMON_CONTROLLER + "common/session/update/{id}/")
    Call<UpdateSessionResponse> updateSession(@Path("id") String id);

    @GET(COMMON_CONTROLLER + "role/get/{enrollmentId}/")
    Call<GetRoleResponse> getRole(@Path("enrollmentId") String id);

    @PUT(COMMON_CONTROLLER + "admin/subject/group/update/")
    Call<UpdateSubjectGroupResponse> updateSubjectGroup(@Body UpdateSubjectGroupRequest request);

    @PUT(COMMON_CONTROLLER + "admin/subject/group/delete/")
    Call<DeleteSubjectGroupResponse> deleteSubjectGroup(@Body DeleteSubjectGroupRequest request);

    @GET(COMMON_CONTROLLER + "admin/examination/result/get")
    Call<GetStudentExaminationClassResultResponse> getStudentResultList(@Query("id") String examId, @Query("classId") String classId, @Query("sectionId") String sectionId);

    @PUT(COMMON_CONTROLLER + "public/otp/send")
    Call<SendOTPResponse> sendOTP(@Query("mobile") String mobile);

    @PUT(COMMON_CONTROLLER + "public/register/validate")
    Call<GetValidateRegistrationResponse> validateRegister(@Query("mobile") String mobile);

    @PUT(COMMON_CONTROLLER + "admin/mobile/update")
    Call<UpdateAdminMobileResponse> updateMobileNumber(@Query("mobile") String mobile, @Query("otp") String otp);

    @GET(COMMON_CONTROLLER + "public/key/get")
    Call<GetAPIKeyResponse> getGoogleApiKey();

    @POST(COMMON_CONTROLLER + "{role}/worksheet/add/")
    Call<AddWorksheetResponse> addHomeWork(@Path("role") String role, @Body AddWorksheetRequest request);

    @GET(COMMON_CONTROLLER + "{role}/worksheet/get")
    Call<GetWorksheetResponse> getHomeWork(@Path("role") String role, @Query("index") int index,
                                           @Query("date") String date,
                                           @Query("classId") String classId,
                                           @Query("sectionId") String sectionId,
                                           @Query("subjectId") String subjectId);

    @DELETE(COMMON_CONTROLLER + "{role}/worksheet/delete/{id}/")
    Call<DeleteWorksheetResponse> deleteHomeWork(@Path("role") String role, @Path("id") String id);


    @GET(COMMON_CONTROLLER + "{role}/worksheet/student/get")
    Call<GetStudentWorksheetResponse> getWorkSheet(@Path("role") String role, @Query("worksheetId") String worksheetId,
                                                   @Query("classId") String classId,
                                                   @Query("sectionId") String sectionId,
                                                   @Query("subjectId") String subjectId);


    @PUT(COMMON_CONTROLLER + "{role}/worksheet/student/update/")
    Call<UpdateStudentWorksheetResponse> updateWorkSheetStatus(@Path("role") String role, @Body UpdateStudentWorksheetRequest request);

    @POST(COMMON_CONTROLLER + "{role}/worksheet/uploaded/add/")
    Call<AddStudentWorksheetResponse> uploadWorksheet(@Path("role") String role, @Body AddStudentWorksheetRequest request);


    @GET(COMMON_CONTROLLER + "{role}/worksheet/uploaded/get")
    Call<GetStudentUploadedWorksheetResponse> getSubmittedHomework(@Path("role") String role, @Query("id") String worksheetId,
                                                                   @Query("enrollmentId") String enrollmentId);

    @GET(COMMON_CONTROLLER + "common/daily-configuration/get/")
    Call<GetDailyConfigurationResponse> getDailyConfigurationResponse();

    @GET(COMMON_CONTROLLER + "common/birthday/get/")
    Call<GetBirthdayResponse> getBirthdayResponse();


    @PUT(COMMON_CONTROLLER + "admin/session/add/")
    Call<CreateSessionResponse> migrateSession(@Body CreateSessionRequest request);


    @GET(COMMON_CONTROLLER + "common/attendance/month/get")
    Call<GetAttendanceMonthResponse> getMonth();


}

