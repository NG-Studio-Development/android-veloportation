package ru.veloportation.veloport.ui.activities;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.gson.Gson;

import java.io.IOException;

import ru.veloportation.VeloportApplication;
import ru.veloportation.veloport.ConstantsVeloportApp;
import ru.veloportation.veloport.R;
import ru.veloportation.veloport.model.db.User;
import ru.veloportation.veloport.model.requests.RegisterIdRequest;
import ru.veloportation.veloport.ui.fragments.EnterFragment;


public class StartActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        addFragment(new EnterFragment(), false);
    }

    @Override
    protected int getContainer() {
        return R.id.container;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_start, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    GoogleCloudMessaging gcm;
    String regId;
    String SENDER_ID = "197291868967";

    public void registerInBackground() {
        new AsyncTask<Void, String, String>() {
            @Override
            protected String doInBackground(Void... params) {
                try {
                    if (gcm == null)
                        gcm = GoogleCloudMessaging.getInstance(StartActivity.this);

                    gcm.unregister();
                    regId = gcm.register(SENDER_ID);
                    //storeRegistrationId(getHostActivity(), regId);
                } catch (IOException ex) {
                    return "Error :" + ex.getMessage();
                }

                TelephonyManager tManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
                String uuid = tManager.getDeviceId();

                authClientAction(uuid);
                return regId;
            }

            @Override
            protected void onPostExecute(String result) {

                if (result.contains("Error")) {
                    Log.d("REGISTER API", result);
                    return;
                }
            }
        }.execute(null, null, null);
    }

    private void authClientAction(String uuid) {


        RegisterIdRequest request = RegisterIdRequest.requestRegisterUUID(uuid, regId,

                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        if (response.contains("ERROR"))
                            Toast.makeText(StartActivity.this, R.string.authorization_incorrect_data, Toast.LENGTH_SHORT).show();
                        else
                            saveEnterDataFromJson(response);

                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.fillInStackTrace();
                    }
                });

        Volley.newRequestQueue(StartActivity.this).add(request);
    }

    public void saveEnterDataFromJson(String stringJson) {
        Gson gson = new Gson();
        User user = null;// = gson.fromJson(stringJson, User.class);
        saveEnterData(user);
    }

    void saveEnterData(User user) {
        VeloportApplication.getInstance().getApplicationPreferencesEditor().putBoolean(ConstantsVeloportApp.PREF_KEY_IS_LOGGED_IN, true);
        //WhereAreYouApplication.getInstance().getApplicationPreferencesEditor().putString(ConstantsVeloportApp.PREF_KEY_LOGIN,user.getLogin());
        VeloportApplication.getInstance().getApplicationPreferencesEditor().commit();
        MainActivity.startCourierActivity(StartActivity.this);
    }


}
