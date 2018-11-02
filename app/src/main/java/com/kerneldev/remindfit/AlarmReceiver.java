package com.kerneldev.remindfit;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.support.v4.app.NotificationCompat;

public class AlarmReceiver extends BroadcastReceiver {
    private static final String CHANNEL_ID = "remind_fit";

    DBManager database;

    SharedPreferences sharedPreferences;

    String name, res;
    int id;
    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub
        sharedPreferences = context.getSharedPreferences("remindfit", Context.MODE_PRIVATE);

        database = new DBManager(context);
        database.open();
        Cursor c = database.fetchNextActivity(sharedPreferences.getInt("logged_in_user", -1));

        if(c != null && c.getCount() > 0){
             name = c.getString(c.getColumnIndex(DBHelper.NAME));
             res = c.getString(c.getColumnIndex(DBHelper.ACTIVITY_RESOURCE));
             id = c.getInt(c.getColumnIndex(DBHelper._ID));
        } else {
            id = -1;
        }

        c.close();
        database.close();


        NotificationManager notificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);

        if(id != -1) {

            Intent notificationIntent = new Intent(context, ExerciseActivity.class);

            notificationIntent.putExtra("ActivityName", name);
            notificationIntent.putExtra("ActivitySource", res);
            notificationIntent.putExtra("ActivityID", id);

            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

            PendingIntent pendingIntent = PendingIntent.getActivity(context, 777,
                    notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);


            NotificationCompat.Builder mNotifyBuilder = new NotificationCompat.Builder(context, CHANNEL_ID)
                    .setSmallIcon(R.drawable.logo)
                    .setContentTitle("It's time for a new activity!!")
                    .setContentText(name)
                    .setWhen(System.currentTimeMillis())
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent);

            notificationManager.notify(777, mNotifyBuilder.build());
        } else {

            //TODO show some other type of notifications if used not logged in
            //TODO Ex: 1 - Register account notification, 2 - Add user detail notification

        }



    }
}
