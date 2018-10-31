package com.kerneldev.remindfit;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.ImageView;

public class AlarmReceiver extends BroadcastReceiver {
    private static final String CHANNEL_ID = "remind_fit";

    DBManager database;

    SharedPreferences sharedPreferences;

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub

        long when = System.currentTimeMillis();
        NotificationManager notificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);

        Intent notificationIntent = new Intent(context, ProfileActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 777,
                notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        sharedPreferences = context.getSharedPreferences("remindfit", Context.MODE_PRIVATE);

        database = new DBManager(context);
        database.open();
        Cursor c = database.fetchNextActivity(1);
        Log.v("Nextactivity", DatabaseUtils.dumpCursorToString(c));


        NotificationCompat.Builder mNotifyBuilder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.logo)
                .setContentTitle("Time For A New Activity!!")
                .setContentText(c.getString(c.getColumnIndex(DBHelper.NAME)))
                .setWhen(when)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        notificationManager.notify(777, mNotifyBuilder.build());

        c.close();
        database.close();

    }
}
