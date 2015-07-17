package ru.veloportation.veloport.ui.fragments;

import android.content.Context;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import ru.veloportation.veloport.R;
import ru.veloportation.veloport.ui.activities.MainActivity;
import ru.veloportation.veloport.ui.activities.StartActivity;


public class EnterFragment extends BaseFragment<StartActivity> implements View.OnClickListener {


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public int getLayoutResID() {
        return R.layout.fragment_enter;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_enter, container, false);

        view.findViewById(R.id.tvCourier).setOnClickListener(this);
        view.findViewById(R.id.tvCustomer).setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.tvCourier:
                getHostActivity().replaceFragment(new LoginFragment(), true);
                break;
            case R.id.tvCustomer:
                openCustomer();
                break;
        }

    }

    private void openCustomer() {
        TelephonyManager tManager = (TelephonyManager)getHostActivity().getSystemService(Context.TELEPHONY_SERVICE);
        final String uuid = tManager.getDeviceId();

        getHostActivity().setOnRegisterIdListener(new StartActivity.OnRegisterIdListener() {
            @Override
            public void onRegister(String regId) {

                if ( regId.contains("Error") ) {
                    getHostActivity().getProgressDialog().hide();
                    return;
                }

                getHostActivity().authClientAction(uuid, regId,

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
