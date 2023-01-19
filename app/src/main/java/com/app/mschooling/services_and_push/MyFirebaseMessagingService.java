package com.app.mschooling.services_and_push;

import static kotlin.random.RandomKt.Random;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.os.Build;
import android.util.Log;
import android.widget.RemoteViews;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.app.mschooling.network.ThisApp;
import com.app.mschooling.notice.activity.NoticeListActivity;
import com.app.mschooling.other.activity.SplashActivity;
import com.app.mschooling.com.R;
import com.app.mschooling.utils.Helper;
import com.app.mschooling.utils.ParameterConstant;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.mschooling.transaction.response.login.UpdateTokenResponse;

import org.json.JSONObject;

import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * Created by Shadab Azam Farooqui on 8/9/2018.
 */

class FirebaseResponse {
    String key;
    String title;
    String message;

    public FirebaseResponse(String key, String title, String message) {
        this.key = key;
        this.title = title;
        this.message = message;
    }

}

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "MyFirebaseMsgService";
    public static final int FCM_NOTIFY_ID = 0xCAFE;
    PendingIntent pendingIntent;
    FirebaseResponse firebaseResponse;

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        ThisApp.getApiCommonController(this).updateToken(s).enqueue(new Callback<UpdateTokenResponse>() {
            @Override
            public void onResponse(Call<UpdateTokenResponse> call, Response<UpdateTokenResponse> response) {

            }

            @Override
            public void onFailure(Call<UpdateTokenResponse> call, Throwable t) {

            }
        });
    }

    @Override
    public void onMessageReceived(RemoteMessage message) {
        super.onMessageReceived(message);
        String from = message.getFrom();
        Map data = message.getData();
        JSONObject jsonObject = new JSONObject(data);
        Log.e("http fcm", "" + jsonObject);
        Log.e("http fcm", String.valueOf(data));
        try {
            firebaseResponse = new FirebaseResponse("", jsonObject.getString("title"), jsonObject.getString("message"));
//            sendNotification(jsonObject.getString("title"),jsonObject.getString("message"));
            sendNotification(firebaseResponse.title, firebaseResponse.message);



            /*new generatePictureStyleNotification(this,
                    jsonObject.getString("title"),
                    jsonObject.getString("message"),
                    jsonObject.getString("url"))
                    .execute();*/
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void sendNotification(String title, String message) {
//        intent used to click on notification
        Intent intent = new Intent(this, SplashActivity.class);
        if (firebaseResponse.key == ParameterConstant.Notification.NOTICE.value()) {
            intent = new Intent(this, NoticeListActivity.class);
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        @SuppressLint("UnspecifiedImmutableFlag") PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

//        custom notification view
        RemoteViews contentView = new RemoteViews(getPackageName(), R.layout.custom_push);
        contentView.setImageViewResource(R.id.image, R.drawable.m1);
        contentView.setTextViewText(R.id.title, title);
        contentView.setTextViewText(R.id.text, message);
        contentView.setTextViewText(R.id.time, Helper.getCurrentTime());

//        notification code
        final String CHANNEL_ID = "channel_01";
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, getString(R.string.app_name),
                    NotificationManager.IMPORTANCE_DEFAULT);
            mNotificationManager.createNotificationChannel(mChannel);
        }
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setSmallIcon(R.drawable.m1)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.m1))
                .setColor(Color.RED)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setContentTitle(title)
                .setContent(contentView)
                .setContentIntent(pendingIntent);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setChannelId(CHANNEL_ID); // Channel ID
        }
        builder.setAutoCancel(true);
//        builder.getNotification().flags |= Notification.FLAG_AUTO_CANCEL;
        int id = Random(System.currentTimeMillis()).nextInt(1000);
//        mNotificationManager.notify(0, builder.build());
        mNotificationManager.notify(id, builder.build());
    }



    /*

   DETAIL:

   API:  https://fcm.googleapis.com/fcm/send
   HEADER: Content-Type   :    "application/json"
   HEADER: Authorization   :    "key=AAAArdQppM4:APA91bFH3q4QXsOsmnbnIOLfm05lnWG5BY6CelfxxMxQxuKYgVzOtAI9CKrgNqsU1tChnj4GfOOhX2lEndWEG7MFmnEUBWryW4BDsl1nW737RBeBOTbSls_YzY6shB7GKtcyhIReJxBT"


NOTE:
'AAAArdQppM4.....shB7GKtcyhIReJxBT' is server key


MULTI USER REQUEST:

{
  "data":{
      "title": "mSchooling",
      "message": "Shadab Azam Farooqui"
    },

    "registration_ids": [
        "f_4KeSIRQR-mif7dOOeZhD:APA91bFfTggecLZAreF1Rpp4eZ3xOIXbqf6VkjyZiNU4tZe20L30wrBbMny4czh3p2yRlUm7YSuVbOZ_MrdDb_NxmBZ872949YFOKtB8kTL94KSMjVpiIyzdFiK9OgQE8oNytnO69zYu"
      ]
}


SINGLE USER REQUEST:

{
  "data":{
      "title": "mSchooling",
      "message": "Shadab Azam Farooqui"
    },

    "to":"f_4KeSIRQR-mif7dOOeZhD:APA91bFfTggecLZAreF1Rpp4eZ3xOIXbqf6VkjyZiNU4tZe20L30wrBbMny4czh3p2yRlUm7YSuVbOZ_MrdDb_NxmBZ872949YFOKtB8kTL94KSMjVpiIyzdFiK9OgQE8oNytnO69zYu"
}


NOTE:
'f_4KeSIRQR-mif7dOO....gQE8oNytnO69zYu' is token id



*/

    /*  public class generatePictureStyleNotification extends AsyncTask<String, Void, Bitmap> {

        private Context mContext;
        private String title, message, imageUrl;

        public generatePictureStyleNotification(Context context, String title, String message, String imageUrl) {
            super();
            this.mContext = context;
            this.title = title;
            this.message = message;
            this.imageUrl = imageUrl;
        }

        @Override
        protected Bitmap doInBackground(String... params) {

            InputStream in;
            try {
                URL url = new URL(this.imageUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                in = connection.getInputStream();
                Bitmap myBitmap = BitmapFactory.decodeStream(in);
                return myBitmap;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
        @Override
        protected void onPostExecute(Bitmap result) {
            super.onPostExecute(result);

            sendNotificationWithImage(title,message,result);
        }
    }

    private void sendNotificationWithImage(String tittle,String message, Bitmap image) {
        try {


            Intent intent = new Intent(this, SplashActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 */

    /* Request code */

    /*, intent,
                    PendingIntent.FLAG_ONE_SHOT);

            final String CHANNEL_ID = "channel_01";
            NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, getString(R.string.app_name),
                        NotificationManager.IMPORTANCE_DEFAULT);
                mNotificationManager.createNotificationChannel(mChannel);
            }
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
            builder.setSmallIcon(R.drawable.without_m)
//                    .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_circular_icon))
                    .setColor(getResources().getColor(R.color.colorPrimary))
                    .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                    .setContentTitle(tittle)
                    .setContentText(message)
                    .setStyle(new NotificationCompat.BigPictureStyle()
                            .bigPicture(image))*/

    /*Notification with Image*/

    /*
                    .setContentIntent(pendingIntent);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                builder.setChannelId(CHANNEL_ID); // Channel ID
            }
            builder.setAutoCancel(true);
//        builder.getNotification().flags |= Notification.FLAG_AUTO_CANCEL;
            mNotificationManager.notify(0, builder.build());
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    // to open app when notification received.


        <uses-permission android:name="android.permission.WAKE_LOCK" />
        <uses-permission android:name="android.permission.DISABLE_KEYGUARD" />
        <uses-permission android:name="android.permission.PARTIAL_WAKE_LOCK" />
        <uses-permission android:name="android.permission.FULL_WAKE_LOCK" />


        <activity android:name=".Activities.call.IncomingCallActivity">
            <intent-filter>
                <action android:name="OPEN_ACTIVITY_1" />
                <category android:name="android.intent.category.DEFAULT" />
            </intent-filter>
         </activity>


        super.onCreate(savedInstanceState);
        final Window win= getWindow();
        win.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        win.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        setContentView(R.layout.activity_incoming_call);


        turn on screen light: MyFirebaseMessagingService
          PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
                    boolean result = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH && powerManager.isInteractive() || Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT_WATCH && powerManager.isScreenOn();

                    if (!result) {
                        @SuppressLint("InvalidWakeLockTag") PowerManager.WakeLock wl = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.ON_AFTER_RELEASE, "MH24_SCREENLOCK");
                        wl.acquire(10000);
                        @SuppressLint("InvalidWakeLockTag") PowerManager.WakeLock wl_cpu = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "MH24_SCREENLOCK");
                        wl_cpu.acquire(10000);
                    }
//                    Intent in = new Intent(Intent.ACTION_POWER_CONNECTED);
//                    getApplicationContext().sendBroadcast(in);
//                    isAppRunning();


    */


}
