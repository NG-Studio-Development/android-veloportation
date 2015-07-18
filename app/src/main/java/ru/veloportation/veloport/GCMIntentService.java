package ru.veloportation.veloport;

import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import ru.veloportation.veloport.ui.activities.MainActivity;
import ru.veloportation.veloport.ui.fragments.OrderFragment;

public class GCMIntentService extends IntentService {

    public static final String TYPE_SEND_MESSAGE = "sendmessage";

    private static final String TAG = "GCMIntentService";

    public GCMIntentService() {
        super("GCMIntentService");
    }

    public static final int NOTIFICATION_ID = 1;
    private android.app.NotificationManager mNotificationManager;

    @Override
    protected void onHandleIntent(Intent intent) {

        Bundle extras = intent.getExtras();

        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        String messageType = gcm.getMessageType(intent);
        String mess = "INCOMIN PUSH";
        String notificationType = extras.getString("type");

        if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {
            if (mess != null)
                sendNotification("Received: "+notificationType);

            Intent intentBroadcast = new Intent(ConstantsVeloportApp.BROADCAST_ACTION);
            intentBroadcast.putExtra(OrderFragment.PARAM_ACTION, OrderFragment.ACTION_TAKE_ORDER);

            sendBroadcast(intentBroadcast);
        }
    }

    /* private void saveIncomingMessage(Intent intent) {
        List<Message> list = new ArrayList<Message>();
        list.add(MessagesHelpers.getInstance().getMessageFromIntent(intent));
        MessagesHelpers.getInstance().saveMessages(list);
    } */

    private void sendNotification( String msg ) {
        mNotificationManager = (android.app.NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);

        final TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(createIntentUpdateData(getApplicationContext() ));
        final PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_ONE_SHOT);



        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        //.setSmallIcon(R.drawable.ic_stat_gcm)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle("Friend Step")
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(msg))
                        .setContentText(msg);

        callSound();
        mBuilder.setAutoCancel(true);
        mBuilder.setContentIntent(pendingIntent);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }

    public static Intent createIntentUpdateData( Context context ) {
        Intent intent = new Intent(context, MainActivity.class);
        Bundle b = new Bundle();
        b.putSerializable(MainActivity.START_TYPE, MainActivity.START_COURIER);
        intent.putExtras(b);

        return intent;
    }

    private void callSound() {
        try {
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
            r.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}