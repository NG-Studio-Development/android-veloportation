package ru.veloportation.veloport.components;

import android.content.Context;
import android.content.Intent;

public class BroadcastManager {

    private static final String PARAM_TASK = "param_task";
    public static final String DEFAULT_ACTION = "ru.veloportation.veloport.broadcast.action";

    private Context context;
    private Intent intent = null;

    private static BroadcastManager instance = null;

    public static BroadcastManager getInstance( Context context ) {

        if (instance == null)
            instance = new BroadcastManager(context);

        return instance;
    }

    private BroadcastManager( Context context ) {
        this.context = context;

    }

    public void sendBroadcast(String what) {
        intent = new Intent(DEFAULT_ACTION);
        intent.putExtra(PARAM_TASK, what);
    }

    public String getWhat() {
        if (intent != null)
            return intent.getStringExtra(PARAM_TASK);
        return null;
    }
}
