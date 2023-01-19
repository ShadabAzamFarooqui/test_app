package com.app.mschooling.utils;

import static android.os.Build.VERSION_CODES.M;
import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.location.LocationManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Vibrator;
import android.provider.OpenableColumns;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import com.app.mschooling.com.BuildConfig;
import com.app.mschooling.com.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.mschooling.transaction.common.Firebase;
import com.mschooling.transaction.response.subject.GetExternalSubjectResponse;
import com.mschooling.transaction.response.subject.GetStandaloneSubjectResponse;
import com.mschooling.transaction.response.subject.OtherSubjectResponse;
import com.mschooling.transaction.response.subject.StandaloneSubject;

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Helper {
    public static final String FOLDER_NAME = "mSchooling";

    //    public static void initActionbar(Activity activity, ActionBar actionBar, String tittleText, boolean homeButton) {
//        View viewActionBar = activity.getLayoutInflater().inflate(R.layout.tool_bar, null);
//        ActionBar.LayoutParams params = new ActionBar.LayoutParams(//Center the textview in the ActionBar !
//                ActionBar.LayoutParams.MATCH_PARENT,
//                ActionBar.LayoutParams.WRAP_CONTENT,
//                Gravity.CENTER);
//        actionBar.setElevation(0);
//        actionBar.setBackgroundDrawable(new ColorDrawable(activity.getResources().getColor(R.color.colorPrimaryDark)));
//        actionBar.setDisplayHomeAsUpEnabled(true);
//        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
//        actionBar.setCustomView(viewActionBar, params);
//        TextView actionbarTitle = (TextView) viewActionBar.findViewById(R.id.title);
//        actionbarTitle.setText(tittleText);
//        actionbarTitle.setTypeface(TypefaceCache.get(activity.getAssets(), 3));
//        actionbarTitle.setTextSize(18);
//        actionBar.setDisplayShowCustomEnabled(true);
//        actionBar.setDisplayShowTitleEnabled(false);
//        actionBar.setDisplayHomeAsUpEnabled(homeButton);
//        actionBar.setHomeButtonEnabled(homeButton);
//    }
    boolean isKeyPadOpen;

    public static void enableDisableView(View view, boolean enabled) {
        view.setEnabled(enabled);
        if (view instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) view;
            for (int idx = 0; idx < group.getChildCount(); idx++) {
                enableDisableView(group.getChildAt(idx), enabled);
            }
        }
    }

    public static void enableDisableView(View view, Button button, boolean enabled) {
        view.setEnabled(enabled);
        if (view instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) view;
            for (int idx = 0; idx < group.getChildCount(); idx++) {
                enableDisableView(group.getChildAt(idx), enabled);
            }
        }
        if (!enabled) {
            button.setVisibility(View.GONE);
        } else {
            button.setVisibility(View.VISIBLE);
        }
    }

    public static String getVersion(Context context) {
        PackageInfo packageInfo = null;
        try {
            packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (packageInfo != null) {
            return packageInfo.versionName;
        } else {
            return "";
        }
    }

    public static void enableDisableView(View view, View showHideView, boolean enabled) {
        view.setEnabled(enabled);
        if (view instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) view;
            for (int idx = 0; idx < group.getChildCount(); idx++) {
                enableDisableView(group.getChildAt(idx), enabled);
            }
        }
        if (!enabled) {
            showHideView.setVisibility(View.GONE);
        } else {
            showHideView.setVisibility(View.VISIBLE);
        }
    }

    public static List<String> getAlphabet() {
        List<String> alphabet = new ArrayList<String>(Arrays.asList("a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z"));
//        List<String> alphabet = new ArrayList<String>(Arrays.asList("z", "z", "z", "z", "z", "z", "z", "z", "z", "z", "z", "z", "z", "z", "z", "z", "z", "z", "z", "z", "z", "z", "z", "z", "z", "z"));
        alphabet.addAll(alphabet);
        Collections.reverse(alphabet);
        return alphabet;
    }

    public static void welcomeVoice(Context context) {
        if (!BuildConfig.DEBUG) {
            final MediaPlayer mediaPlayer;
            final Vibrator vib;
            if (ParameterConstant.isMSchooling(context)) {
                mediaPlayer = MediaPlayer.create(context, R.raw.mschooling);
            } else if (ParameterConstant.isLittleSteps(context)) {
                mediaPlayer = MediaPlayer.create(context, R.raw.little_steps);
            } else {
                mediaPlayer = MediaPlayer.create(context, R.raw.mschooling);
                return;
            }

            mediaPlayer.start();
//        vib = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
//        vib.vibrate(111110000);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    mediaPlayer.stop();
                }
            }, 5000);
        }
    }

    public static void getProfileImage(Context context, Firebase firebase, ImageView image, ShimmerFrameLayout shimmerFrameLayout) {

        if (firebase == null) {
            firebase = new Firebase();
            firebase.setUrl(ParameterConstant.getDefaultUserUrl());
        }
        Glide.with(context)
                .load(firebase.getUrl())
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .listener(new RequestListener<Drawable>() {
                    @SuppressLint("UseCompatLoadingForDrawables")
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        shimmerFrameLayout.setVisibility(View.GONE);
                        image.setVisibility(View.VISIBLE);
                        image.setImageDrawable(context.getResources().getDrawable(R.drawable.user));
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        shimmerFrameLayout.setVisibility(View.GONE);
                        return false;
                    }
                })
                .into(image);
    }


    public static void getProfileImageStringUrl(Context context, String url, ImageView image, ShimmerFrameLayout shimmerFrameLayout) {

        if (url == null) {
            url = ParameterConstant.getDefaultUserUrl();
        } else if (url.isEmpty()) {
            url = ParameterConstant.getDefaultUserUrl();
        }
        Glide.with(context)
                .load(url)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .listener(new RequestListener<Drawable>() {
                    @SuppressLint("UseCompatLoadingForDrawables")
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        shimmerFrameLayout.setVisibility(View.GONE);
                        image.setVisibility(View.VISIBLE);
                        image.setImageDrawable(context.getResources().getDrawable(R.drawable.user));
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        shimmerFrameLayout.setVisibility(View.GONE);
                        return false;
                    }
                })
                .into(image);
    }


    public static void setProfilePic(ImageView v, String path) {
        v.setImageBitmap(BitmapFactory.decodeFile(path));
    }

    public static String[] createStringRange(int start, int delta) {

        String res[] = new String[delta];

        for (int i = 0; i < delta; i++) {
            res[i] = String.valueOf(start + i);
        }
        return res;
    }

    public static float defineVolume(Context ctx, int stream) {

        AudioManager audioManager = (AudioManager) ctx.getSystemService(ctx.AUDIO_SERVICE);

        final float actualVolume = (float) audioManager.getStreamVolume(stream);
        final float maxVolume = (float) audioManager.getStreamMaxVolume(stream);
        float volume = actualVolume / maxVolume;
        volume = volume == 0 ? .5f : volume;
        return volume;
    }

    public static boolean isNetworkAvailable(Context context) {

        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static boolean isNetworkAvailableWithDialog(Context ctx) {

        ConnectivityManager connectivityManager
                = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

        boolean available = activeNetworkInfo != null && activeNetworkInfo.isConnected();

        if (!available) {
            new AlertDialog.Builder(ctx)
                    .setMessage("Check out your Network connection!")
                    .setPositiveButton(ctx.getString(R.string.ok), null)
                    .show();
        }
        return available;
    }

    public static void isLocationEnabled(Context ctx) {

        LocationManager service = (LocationManager) ctx.getSystemService(Context.LOCATION_SERVICE);

        boolean gpsEnabled = service.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean netEnabled = service.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        if (!gpsEnabled && !netEnabled) {

//            new AlertDialog.Builder(ctx)
//                    .setMessage(ctx.getString(R.string.enable_location))
//                    .setPositiveButton(ctx.getString(R.string.ok), (d, i) ->
//                            ctx.startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)))
//                    .show();
        }
    }

    public static void isGPSEnabled(Context ctx) {


        LocationManager service = (LocationManager) ctx.getSystemService(Context.LOCATION_SERVICE);

        boolean gpsEnabled = service.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if (!gpsEnabled) {

//            new AlertDialog.Builder(ctx)
//                    .setMessage(ctx.getString(R.string.enable_gps))
//                    .setPositiveButton(ctx.getString(R.string.ok), (d, i) ->
//                            ctx.startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)))
//                    .setNegativeButton(ctx.getString(R.string.btn_cancel), null)
//                    .show();
        }
    }

//    @SuppressLint("HardwareIds")
//    public static String getDeviceId(Context ctx) {
//
//        TelephonyManager tm = (TelephonyManager) ctx.getSystemService(Context.TELEPHONY_SERVICE);
//
//        String deviceId = "";
//        try {
//            if (ActivityCompat.checkSelfPermission(ctx, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
//                // TODO: Consider calling
//                //    ActivityCompat#requestPermissions
//                // here to request the missing permissions, and then overriding
//                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//                //                                          int[] grantResults)
//                // to handle the case where the user grants the permission. See the documentation
//                // for ActivityCompat#requestPermissions for more details.
//                return null;
//            }
//            if (tm.getDeviceId() != null) {
//
//                if (ActivityCompat.checkSelfPermission(ctx, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
//                    // TODO: Consider calling
//                    //    ActivityCompat#requestPermissions
//                    // here to request the missing permissions, and then overriding
//                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//                    //                                          int[] grantResults)
//                    // to handle the case where the user grants the permission. See the documentation
//                    // for ActivityCompat#requestPermissions for more details.
//                    return null;
//                }
//                deviceId = tm.getDeviceId();
//            } else {
//
//                deviceId = Settings.Secure.getString(
//                        ctx.getApplicationContext().getContentResolver(),
//                        Settings.Secure.ANDROID_ID);
//            }
//        } catch (Exception e) {
//            deviceId = "";
//        }
//
//
//        return deviceId;
//    }

    public static String bitmapToBase64(Bitmap bitmap) {

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    public static Bitmap base64ToBitmap(String input) {
        byte[] decodedBytes = Base64.decode(input, 0);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }

    public static Bitmap getRoundBitmap(Bitmap bitmap) {

        int min = Math.min(bitmap.getWidth(), bitmap.getHeight());

        Bitmap bitmapRounded = Bitmap.createBitmap(min, min, bitmap.getConfig());

        Canvas canvas = new Canvas(bitmapRounded);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setShader(new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP));
        canvas.drawRoundRect((new RectF(0.0f, 0.0f, min, min)), min / 2, min / 2, paint);

        return bitmapRounded;
    }


    public static void openKeyPad(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }

    public static void closeKeyPad(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.RESULT_UNCHANGED_HIDDEN, 0);
    }

    public static String getMonth(int month) {
        String[] ar = {"", "Jan", "Feb", "Mar", "Apr", "May", "Jun", "July", "Aug", "Sep", "Oct", "Nov", "Dec"};
        return ar[month];
    }

    public static String getDay(String date) {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat format1 = new SimpleDateFormat(ParameterConstant.getDateFormat());
        Date dt1 = null;
        try {
            dt1 = format1.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        @SuppressLint("SimpleDateFormat") DateFormat format2 = new SimpleDateFormat("EEEE");
        String finalDay = format2.format(dt1);
        finalDay = finalDay.substring(0, 3);
        return finalDay;
    }

    public static String getDayFromDateString(String stringDate) {
        String[] daysArray = new String[]{"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
        String day = "";

        int dayOfWeek = 0;
        //dateTimeFormat = yyyy-MM-dd HH:mm:ss
        @SuppressLint("SimpleDateFormat") SimpleDateFormat formatter = new SimpleDateFormat(ParameterConstant.getDateFormat());
//        SimpleDateFormat formatter = new SimpleDateFormat(dateTimeFormat);
        Date date;
        try {
            date = formatter.parse(stringDate);
            Calendar c = Calendar.getInstance();
            c.setTime(date);
            dayOfWeek = c.get(Calendar.DAY_OF_WEEK) - 1;
            if (dayOfWeek < 0) {
                dayOfWeek += 7;
            }
            day = daysArray[dayOfWeek];
        } catch (Exception e) {
            e.printStackTrace();
        }

        return day;
    }

    public static void closeKeyPad(Activity activity, boolean isKeyPadOpen) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (isKeyPadOpen) {
            imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
        }
    }

    public static File getOutputMediaFile(int type, String imageNameUrl) {
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), ParameterConstant.getFolderName());

        if (!mediaStorageDir.exists()) {
            mediaStorageDir.mkdirs();
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(mediaStorageDir.getPath());
            stringBuilder.append(File.separator);
            stringBuilder.append(imageNameUrl);
            stringBuilder.append(".jpg");
            return new File(stringBuilder.toString());
        } else if (type != MEDIA_TYPE_IMAGE) {
            return null;
        } else {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(mediaStorageDir.getPath());
            stringBuilder.append(File.separator);
            stringBuilder.append(imageNameUrl);
            stringBuilder.append(".jpg");
            return new File(stringBuilder.toString());
        }
    }

    public static String getRandom() {
//        return String.format("%07d", new Random().nextInt(10000));
        return "" + System.currentTimeMillis();
    }

    public static Bitmap getBitmapFromView(View view) {
        //Define a bitmap with the same size as the view
        Bitmap returnedBitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        //Bind a canvas to it
        Canvas canvas = new Canvas(returnedBitmap);
        //Get the view's background
        Drawable bgDrawable = view.getBackground();
        if (bgDrawable != null)
            //has background drawable, then draw it on the canvas
            bgDrawable.draw(canvas);
        else
            //does not have background drawable, then draw white background on the canvas
            canvas.drawColor(Color.WHITE);
        // draw the view on the canvas
        view.draw(canvas);
        //return the bitmap
        return returnedBitmap;
    }

    public static int dateValidation(String start, String end) {
        SimpleDateFormat dateFormatter = new SimpleDateFormat(ParameterConstant.getDateFormat(), Locale.US);
        Date dateStart = null;
        Date dateEnd = null;
        try {
            dateStart = dateFormatter.parse(start);
            dateEnd = dateFormatter.parse(end);
        } catch (ParseException e) {
            e.printStackTrace();
        }
       /* if (dateObject2.compareTo(dateObject1) == -1) {
            return;
        }*/
        return dateEnd.compareTo(dateStart);
    }

    @SuppressLint("Range")
    public static String getFileName(Context context, Uri uri) {
        String displayName = null;
        if (uri.toString().startsWith("content://")) {
            Cursor cursor = null;
            try {
                cursor = context.getContentResolver().query(uri, null, null, null, null);
                if (cursor != null && cursor.moveToFirst()) {
                    displayName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        } else if (uri.toString().startsWith("file://")) {
            displayName = new File(uri.toString()).getName();
        }
        return displayName;
    }

    @SuppressLint("SimpleDateFormat")
    public static String getCurrentDate() {
        return new SimpleDateFormat(ParameterConstant.getDateFormat()).format(new Date());

    }

    @SuppressLint("SimpleDateFormat")
    public static String getCurrentDateTime() {
        return new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date());

    }

    public static String getTomorrowDateTime() {

        Date date = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.DATE, 1);
        date = c.getTime();
        @SuppressLint("SimpleDateFormat") DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        return dateFormat.format(date);

    }

    @SuppressLint("SimpleDateFormat")
    public static int getCurrentDay() {
        return Integer.valueOf(new SimpleDateFormat("dd").format(new Date()));

    }

    @SuppressLint("SimpleDateFormat")
    public static int getCurrentMonth() {
        return Integer.valueOf(new SimpleDateFormat("MM").format(new Date()));

    }

    @SuppressLint("SimpleDateFormat")
    public static String getCurrentDayMonth() {
        return new SimpleDateFormat("dd MMM").format(new Date());

    }

    @SuppressLint("SimpleDateFormat")
    public static String getCurrentYear() {
        return new SimpleDateFormat("yyyy").format(new Date());

    }

    @SuppressLint("SimpleDateFormat")
    public static String getCurrentTime() {
        return new SimpleDateFormat("hh:mm a").format(new Date());
    }

    public static String getDate(int plus) {
        if (plus == 0) {
            return getCurrentDate();
        }
        Date date = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.DATE, plus);
        date = c.getTime();
        @SuppressLint("SimpleDateFormat") DateFormat dateFormat = new SimpleDateFormat(ParameterConstant.getDateFormat());
        return dateFormat.format(date);
    }

    public static void preview(Context context, File file) {
        Uri imgUri;
        String state = Environment.getExternalStorageState();
        if (!Environment.MEDIA_MOUNTED.equals(state)) {
            Toast.makeText(context, "Mounted", Toast.LENGTH_SHORT).show();
        }
        if (Build.VERSION.SDK_INT > M) {
            imgUri = FileProvider.getUriForFile(context,
                    BuildConfig.APPLICATION_ID + ".provider", file);
            Log.d("PdfUriPath", String.valueOf(imgUri));
        } else {
            imgUri = Uri.fromFile(file);
        }

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(imgUri, "application/pdf");
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

// Helper.preview(getContext(),Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS + "/mSchooling/QrCode.pdf"));

    public static Date toDate(String dateStr) {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat(ParameterConstant.getDateFormat());
        Date date = null;
        try {
            date = format.parse(dateStr);
            System.out.println(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return date;
    }

    public static String[] getStringArray(ArrayList<String> arr) {
        Object[] objArr = arr.toArray();
        String[] str = Arrays.copyOf(objArr, objArr.length, String[].class);
        return str;
    }

    public static String getDay(int position) {
        String[] daysArray = new String[]{"MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY", "SUNDAY"};
        return daysArray[position];
    }

    public static int getDay() {
        String weekday_name = new SimpleDateFormat("EEEE", Locale.ENGLISH).format(System.currentTimeMillis());
        if (weekday_name.equals("All")) {
            return 0;
        }
        if (weekday_name.equals("Monday")) {
            return 1;
        } else if (weekday_name.equals("Tuesday")) {
            return 2;
        } else if (weekday_name.equals("Wednesday")) {
            return 3;
        } else if (weekday_name.equals("Thursday")) {
            return 4;
        } else if (weekday_name.equals("Friday")) {
            return 5;
        } else if (weekday_name.equals("Saturday")) {
            return 6;
        } else if (weekday_name.equals("Sunday")) {
            return 7;
        } else {
            return -1;
        }
    }

    /*public static Map<String, List<SubjectResponse>> getOptionalPaper(ClassResponse classResponse) {
        Map<String, List<SubjectResponse>> optionalSubjectMap = new HashMap<>();
        for (int i = 0; i < classResponse.getSubjectResponses().size(); i++) {
            List<SubjectResponse> list = new ArrayList<>();
            SubjectType subjectTypeI = classResponse.getSubjectResponses().get(i).getSubjectType();
            if (subjectTypeI != null) {
                if (subjectTypeI.getGroupName() != null) {
                    if (!subjectTypeI.getGroupName().equalsIgnoreCase("other")) {
                        for (int j = 0; j < classResponse.getSubjectResponses().size(); j++) {
                            SubjectType subjectTypeJ = classResponse.getSubjectResponses().get(j).getSubjectType();
                            if (subjectTypeJ != null) {
                                if (subjectTypeJ.getGroupName() != null) {
                                    if (subjectTypeI.getGroupName().equals(subjectTypeJ.getGroupName())) {
                                        list.add(classResponse.getSubjectResponses().get(j));
                                    }
                                }
                            }
                        }
                    }

                }
            }


            if (list.size() > 0) {
                optionalSubjectMap.put(classResponse.getSubjectResponses().get(i).getSubjectType().getGroupName(), list);
            }

        }

        return optionalSubjectMap;
    }*/


    public static Map<String, List<StandaloneSubject>> getOptionalPaper(GetStandaloneSubjectResponse response) {
        Map<String, List<StandaloneSubject>> optionalSubjectMap = new HashMap<>();
        for (int i = 0; i < response.getStandaloneSubjectResponses().size(); i++) {
            if (!response.getStandaloneSubjectResponses().get(i).getName().equalsIgnoreCase("Mandatory") && !response.getStandaloneSubjectResponses().get(i).getName().equalsIgnoreCase("Other"))
                optionalSubjectMap.put(response.getStandaloneSubjectResponses().get(i).getName(), response.getStandaloneSubjectResponses().get(i).getStandaloneSubjects());
        }
        return optionalSubjectMap;
    }

    public static Map<String, List<OtherSubjectResponse>> getOptionalPaperExternal(GetExternalSubjectResponse response) {
        Map<String, List<OtherSubjectResponse>> optionalSubjectMap = new HashMap<>();
        for (int i = 0; i < response.getExternalSubjectResponse().getOtherSubjectGroupResponses().size(); i++) {
            if (!response.getExternalSubjectResponse().getOtherSubjectGroupResponses().get(i).getName().equalsIgnoreCase("Other")) {
                optionalSubjectMap.put(response.getExternalSubjectResponse().getOtherSubjectGroupResponses().get(i).getName(), response.getExternalSubjectResponse().getOtherSubjectGroupResponses().get(i).getOtherSubjectResponses());
            }
        }
        return optionalSubjectMap;
    }

    public static void setFireBase(Activity context, Firebase fireBase, ImageView image) {
        if (fireBase != null) {
            if (fireBase.getUrl() != null) {
                Glide.with(context)
                        .load(fireBase.getUrl())
                        .into(image);


                    /*Glide.with(getApplicationContext())
                            .load(response.getFirebase().getUrl())
                            .transition(DrawableTransitionOptions.withCrossFade())
                            .apply(RequestOptions.skipMemoryCacheOf(true))
                            .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.ALL))
                            .apply(new RequestOptions().placeholder(R.drawable.user))
                            .into(image);*/
            } else {
                image.setImageDrawable(context.getResources().getDrawable(R.drawable.user));
            }
        } else {
            image.setImageDrawable(context.getResources().getDrawable(R.drawable.user));
        }

    }

    public static void setFireBase(Activity context, String url, ImageView image) {
        if (url != null) {
            Glide.with(context)
                    .load(url)
                    .into(image);
        } else {
            image.setImageDrawable(context.getResources().getDrawable(R.drawable.user));
        }

    }

    public static void share(Activity activity) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, ParameterConstant.getShareTitle());
        shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, ParameterConstant.getShareMessage());
        activity.startActivity(Intent.createChooser(shareIntent, "Share via"));
    }

    public static String extractYTId(String ytUrl) {
        String vId = null;
        Pattern pattern = Pattern.compile(
                "^https?://.*(?:youtu.be/|v/|u/\\w/|embed/|watch?v=)([^#&?]*).*$",
                Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(ytUrl);
        if (matcher.matches()) {
            vId = matcher.group(1);
        } else {
            Pattern pattern1 = Pattern.compile(
                    "^http?://.*(?:youtu.be/|v/|u/\\w/|embed/|watch?v=)([^#&?]*).*$",
                    Pattern.CASE_INSENSITIVE);
            Matcher matcher1 = pattern1.matcher(ytUrl);
            if (matcher.matches()) {
                vId = matcher.group(1);
            }
        }
        return vId;
    }

    public static void setTextGradient(Context context, TextView textView) {
       /* Shader shader = new LinearGradient(0,0,0,textView.getLineHeight(),
                context.getResources().getColor(R.color.startColor), context.getResources().getColor(R.color.endColor), Shader.TileMode.REPEAT);
        textView.getPaint().setShader(shader);*/
    }

    public static String getPackageName() {
        return "com.app.mschooling";
    }

    public static String roundOff(double d) {
        DecimalFormat f = new DecimalFormat("##.00");
        return f.format(d);
    }

    public static String getName(String firstName, String lastName) {
        String name;
        if (lastName == null) {
            return firstName;
        } else {
            return firstName + " " + lastName;
        }
    }

    public static String getFatherName(String name) {
        if (name == null || "".equals(name)) {
            return "N/A";
        } else {
            return name;
        }
    }

    public boolean isKeyPadOpen(Activity context) {
        isKeyPadOpen = false;
        KeyboardVisibilityEvent.setEventListener(context, new KeyboardVisibilityEventListener() {
            @Override
            public void onVisibilityChanged(boolean isOpen) {
                isKeyPadOpen = isOpen;
            }
        });
        return isKeyPadOpen;
    }


    @SuppressLint("DefaultLocale")
    public static String getTimeHM(long millis) {
        return String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millis),
                TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
                TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
    }

    @SuppressLint("DefaultLocale")
    public static String getTimeHMS(long millis) {
        return String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
                TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
    }

    public static String capsFirstOtherInLower(String str) {
        return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
    }


    public static String getValidatedString(String content) {
        if (content == null)
            return "";
        else
            return content.trim();
    }

}
