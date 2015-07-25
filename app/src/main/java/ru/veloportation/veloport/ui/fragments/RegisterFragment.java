package ru.veloportation.veloport.ui.fragments;

import android.content.Context;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import ru.veloportation.veloport.R;
import ru.veloportation.veloport.ui.activities.MainActivity;
import ru.veloportation.veloport.ui.activities.StartActivity;


public class RegisterFragment extends BaseFragment<StartActivity> {



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public int getLayoutResID() {
        return R.layout.fragment_register;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register, container, false);
        final EditText etName = (EditText) view.findViewById(R.id.etName);
        final EditText etLogin = (EditText) view.findViewById(R.id.etLogin);
        final EditText etEmail = (EditText) view.findViewById(R.id.etEmail);
        final EditText etPass = (EditText) view.findViewById(R.id.etPass);

        ImageButton ibBack = (ImageButton) view.findViewById(R.id.ibBack);
        ibBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getHostActivity().onBackPressed();
            }
        });

        Button buttonRegister = (Button) view.findViewById(R.id.buttonRegister);
        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject jsonObject = new JSONObject();

                try {
                    jsonObject.put(FIELD_LOGIN, etLogin.getText().toString());
                    jsonObject.put(FIELD_NAME, etName.getText().toString());
                    jsonObject.put(FIELD_EMAIL, etEmail.getText().toString());
                    jsonObject.put(FIELD_PASS, etPass.getText().toString());

                    openCustomer(jsonObject);
                } catch (JSONException ex) {
                    ex.printStackTrace();
                    throw new Error(ex.getMessage());
                }
            }
        });

        return view;
    }

    String FIELD_LOGIN = "login";
    String FIELD_NAME = "name";
    String FIELD_EMAIL = "email";
    String FIELD_PASS = "pass";
    String FIELD_REG_ID = "registerId";

    private void openCustomer(final JSONObject jsonObject) throws JSONException{
        TelephonyManager tManager = (TelephonyManager)getHostActivity().getSystemService(Context.TELEPHONY_SERVICE);
        final String uuid = tManager.getDeviceId();

        getHostActivity().setOnRegisterIdListener(new StartActivity.OnRegisterIdListener() {
            @Override
            public void onRegister(String regId) {

                if ( regId.contains("Error") ) {
                    getHostActivity().getProgressDialog().hide();
                    return;
                }

                try {
                    jsonObject.put(FIELD_REG_ID, regId);
                } catch (JSONException ex) {
                    ex.printStackTrace();
                    throw new Error(ex.getMessage());
                }


                getHostActivity().registerClientAction(uuid, jsonObject,

                new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {

                                if (response.contains("ERROR")) {
                                    Toast.makeText(getHostActivity(), R.string.authorization_incorrect_data, Toast.LENGTH_SHORT).show();
                                } else {
                                    getHostActivity().saveEnterDataFromJson(response);
                                    getHostActivity().getProgressDialog().hide();
                                    MainActivity.startCustomerActivity(getHostActivity());
                                }
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                getHostActivity().getProgressDialog().hide();
                                error.fillInStackTrace();
                            }
                        });



            }
        });

        getHostActivity().getProgressDialog().show();
        getHostActivity().registerInBackground();
    }


}
