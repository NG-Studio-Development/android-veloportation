package ru.veloportation.veloport.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import ru.veloportation.veloport.R;
import ru.veloportation.veloport.ui.activities.MainActivity;
import ru.veloportation.veloport.ui.activities.StartActivity;

public class LoginFragment extends BaseFragment<StartActivity> {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public int getLayoutResID() {
        return R.layout.fragment_login;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_login, container, false);

        final EditText etLogin = (EditText) view.findViewById(R.id.etLogin);
        final EditText etPass = (EditText) view.findViewById(R.id.etPass);

        Button button = (Button) view.findViewById(R.id.buttonAuthorize);
        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                openCourier(etLogin.getText().toString(), etPass.getText().toString());
            }
        });

        return view;
    }

    private void openCourier(final String login, final String pass) {

        getHostActivity().setOnRegisterIdListener(new StartActivity.OnRegisterIdListener() {
            @Override
            public void onRegister(String regId) {
                getHostActivity().authCourierAction(login, pass, regId, new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {

                                getHostActivity().getProgressDialog().dismiss();

                                if (response.contains("Error")) {
                                    Toast.makeText(getHostActivity(), R.string.authorization_incorrect_data, Toast.LENGTH_SHORT).show();
                                } else {
                                    getHostActivity().saveEnterDataFromJson(response);
                                    MainActivity.startCourierActivity(getHostActivity());
                                }
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                error.fillInStackTrace();
                                getHostActivity().getProgressDialog().dismiss();
                            }
                        });
            }
        });

        getHostActivity().getProgressDialog().setMessage(getString(R.string.check_of_login_and_pass));
        getHostActivity().getProgressDialog().show();

        getHostActivity().registerInBackground();
    }
}
