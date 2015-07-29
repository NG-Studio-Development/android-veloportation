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

import ru.veloportation.veloport.ui.activities.CourierActivity;
import ru.veloportation.veloport.ui.activities.CustomerActivity;
import ru.veloportation.veloport.ui.fragments.BaseFragment;
import ru.veloportation.veloport.ui.fragments.MapFragment;
import ru.veloportation.veloport.ui.fragments.OrderFragment;

public class GCMIntentService extends IntentService {

    public static final String TYPE_SEND_MESSAGE = "sendmessage";

    public static final String ACTION_CREATE_ORDER = "create_order";

    public static final String ACTION_TAKE_ORDER = "take_order";

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
        String action = extras.getString("action");

        if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {
            if (mess != null)
                sendNotification(action);

            Intent intentBroadcast = createIntentBroadcast(notificationType, extras);
            sendBroadcast(intentBroadcast);
        }
    }

    private static String TYPE_MAKE_ORDER = "makeOrder";
    private static String TYPE_UPDATE_LOCATION = "updateLocation";

    Intent createIntentBroadcast(String notificationType, Bundle extras) {

        Intent intentBroadcast = new Intent(ConstantsVeloportApp.BROADCAST_ACTION);

        if (notificationType.equals(TYPE_MAKE_ORDER)) {

            intentBroadcast.putExtra(BaseFragment.PARAM_ACTION, OrderFragment.ACTION_TAKE_ORDER);
            intentBroadcast.putExtra(BaseFragment.PARAM_ID_ORDER, extras.getString("order_id"));
            intentBroadcast.putExtra(BaseFragment.PARAM_TIME_IN_MILLIS, extras.getString("time"));

        } else if (notificationType.equals(TYPE_UPDATE_LOCATION)) {

            intentBroadcast.putExtra(BaseFragment.PARAM_ACTION, MapFragment.ACTION_NEW_LOCATION);
            intentBroadcast.putExtra(MapFragment.KEY_DATA,extras);
        }

        return intentBroadcast;
    }


    /* private void saveIncomingMessage(Intent intent) {
        List<Message> list = new ArrayList<Message>();
        list.add(MessagesHelpers.getInstance().getMessageFromIntent(intent));
        MessagesHelpers.getInstance().saveMessages(list);
    } */

    private void sendNotification( String action ) {



        mNotificationManager = (android.app.NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);

        final TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);

        Class clazz = null;

        if ( action.equals(ACTION_CREATE_ORDER) )
            clazz = CourierActivity.class;
            //stackBuilder.addParentStack(CourierActivity.class);
        else
            clazz = CustomerActivity.class;
            //stackBuilder.addParentStack(CustomerActivity.class);

        if (clazz != null) {
            stackBuilder.addParentStack(clazz);
            stackBuilder.addNextIntent(createIntentUpdateData( getApplicationContext(), clazz ));
            final PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_ONE_SHOT);

            NotificationCompat.Builder mBuilder = buildNotification(action);


            callSound();
            mBuilder.setAutoCancel(true);
            mBuilder.setContentIntent(pendingIntent);
            mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
        }

        /*stackBuilder.addNextIntent(createIntentUpdateData( getApplicationContext() ));
        final PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_ONE_SHOT);

        NotificationCompat.Builder mBuilder = buildNotification(action);


        callSound();
        mBuilder.setAutoCancel(true);
        mBuilder.setContentIntent(pendingIntent);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());*/
    }

    private NotificationCompat.Builder buildNotification(String action) {
        String message = "Veloport notification";
        if ( action.equals(ACTION_CREATE_ORDER) ) {
            message = getString(R.string.created_new_order);
        } else if ( action.equals(ACTION_TAKE_ORDER) ) {
            message = getString(R.string.your_order_was_taking);
        }

        return  new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Veloport")
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setContentText(message);
    }

    public static Intent createIntentUpdateData( Context context, Class clazz ) {
        Intent intent = new Intent( context, clazz );
        Bundle b = new Bundle();
        //b.putSerializable(MainActivity.START_TYPE, MainActivity.START_COURIER);
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