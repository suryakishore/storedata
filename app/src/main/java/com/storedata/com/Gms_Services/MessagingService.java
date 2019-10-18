package com.storedata.com.Gms_Services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.storedata.com.R;
import com.storedata.com.splash.SplashActivity;

/**
 * <h1>MessagingService</h1>
 * <P>
 *
 * </P>
 * @since 1/10/16.
 */
public class MessagingService extends FirebaseMessagingService
{
  //  SessionManager sessionManager;
    private String from;
    private String messagenCustome;
    private String callValue;
    private boolean createNotification=true;
    private NotificationChannel mChannel=null;
    private NotificationManager notificationManager=null;
    private  String body;

    @Override
    public void onCreate()
    {
        super.onCreate();
        //sessionManager=new SessionManager(this);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage)
    {
        super.onMessageReceived(remoteMessage);

        try {
            Log.d("exe","remoteMessage"+remoteMessage.getFrom());
            Log.i("TITLE", "" + remoteMessage.getNotification().getTitle());
            Log.i("NOTIFICATION", "" + remoteMessage.getNotification().getBody());
            String bodyNotification=remoteMessage.getNotification().toString();
            Log.d("exe","bodyNotification"+bodyNotification);
            setNotification("storedata",remoteMessage.getNotification().getBody());
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("exe","Exception"+e.getMessage());
        }

    }





    /**
     * Notifications for the chats and calls
     */
//

    private void setNotification(String title,String message)
    {
        Log.d("exe","title"+title+"message"+message);



        Intent intent = new Intent(this, SplashActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("body",body);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
      NotificationChannel mChannel=null;
      //  NotificationManager

        if (notificationManager==null)
         notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Log.d("exe","Build.VERSION.SDK_INT"+Build.VERSION.SDK_INT);
            NotificationCompat.Builder builder;
            int importance = NotificationManager.IMPORTANCE_HIGH;
            if (mChannel == null) {
                mChannel = new NotificationChannel
                        ("0", title, importance);
                mChannel.setDescription(message);
                mChannel.enableVibration(true);
                notificationManager.createNotificationChannel(mChannel);
            }
            builder = new NotificationCompat.Builder(this, "0");
            builder.setContentTitle(title)
                   // .setSmallIcon(R.drawable.ic_stat_p) // required
                    .setSmallIcon(R.mipmap.ic_launcher) // required
                    .setContentText(message)  // required
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setAutoCancel(true)
                    .setLargeIcon(BitmapFactory.decodeResource
                            (getResources(), R.mipmap.ic_launcher))
                  //  .setBadgeIconType(R.drawable.ic_stat_p)
                    .setContentIntent(pendingIntent);
                  //  .setSound(defaultSoundUri);
            Notification notification = builder.build();
            notificationManager.notify(0, notification);
        }
        else {
       // Uri defaultSoundUri = RingtoneManager.getDefaultUri(R.raw.tone_cuver_sample);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(),R.mipmap.ic_launcher))
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setBadgeIconType(R.drawable.ic_launcher_background)
                //.setDefaults(Notification.DEFAULT_SOUND)
              //  .setSound(defaultSoundUri)
                .setPriority(NotificationCompat.PRIORITY_HIGH);
       // if (Build.VERSION.SDK_INT >= 21) notificationBuilder.setVibrate(new long[0]);
            Notification notification = notificationBuilder.build();
            notificationManager.notify(0, notification);
       // notificationManager.notify(0, notificationBuilder.build());
    }
    }
}
