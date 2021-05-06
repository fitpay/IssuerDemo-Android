package com.fitpay.issuerdemo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

public class AppToAppFragment extends Fragment {

    @BindView(R.id.etPassword)
    EditText etPassword;
    @BindView(R.id.issuer_list)
    Spinner issuer_list;
    @BindView(R.id.response_type)
    Spinner response_type;
    @BindView(R.id.request)
    TextView request;

    private boolean canSkipWithSuccess;
    private Activity activity;
    private Intent intent;

    private String VISA_AUTH_CODE_KEY = "STEP_UP_AUTH_CODE";
    private String VISA_RESPONSE_KEY = "STEP_UP_RESPONSE";
    private String MASTER_CARD_AUTH_CODE_KEY = "TAV";
    private String MASTER_CARD_RESPONSE_KEY = "issuerMobileAppAuthResponse";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_app_to_app, container, false);
        ButterKnife.bind(this, view);

        activity = getActivity();
        intent = activity.getIntent();

        // setup spinners
        setupSpinners();

        if (intent != null && intent.getAction() != null) {
            switch (intent.getAction()) {
                case "com.fitpay.issuerdemo.generate_auth_code":
                    request.setText(intent.getAction());
                    //submit password
                    etPassword.setVisibility(View.VISIBLE);
                    break;
                case "com.fitpay.issuerdemo.authenticate_user":
                    request.setText(intent.getAction());
                    canSkipWithSuccess = true;
                    // automatic flow, hide input text
                    etPassword.setVisibility(View.INVISIBLE);
                    break;
                default:
                    //launched manually
            }
        }
        return view;
    }

    void setupSpinners() {
        String[] issuerItems = new String[]{"VISA", "MASTERCARD", "MAESTRO"};
        String[] responseItems = new String[]{"Approved", "Declined", "Failure"};

        ArrayAdapter<String> issuerAdapter = new ArrayAdapter<>(activity, android.R.layout.simple_spinner_dropdown_item, issuerItems);
        issuer_list.setAdapter(issuerAdapter);
        ArrayAdapter<String> responseAdapter = new ArrayAdapter<>(activity, android.R.layout.simple_spinner_dropdown_item, responseItems);
        response_type.setAdapter(responseAdapter);
    }

    @OnClick(R.id.btnSubmit)
    void submit() {
        if (canSkipWithSuccess) {
            handleAutomaticFlow();
            return;
        }

        handleAuthCodeFlow();
    }

    private void handleAutomaticFlow() {
        String selectedResponse = (String) response_type.getSelectedItem();
        switch (selectedResponse) {
            case "Approved":
                finishActivity("approved", null);
                break;
            case "Declined":
                finishActivity("declined", null);
                break;
            case "Failure":
                finishActivity("failure", null);
                break;
        }
    }

    private void handleAuthCodeFlow() {
        String password = etPassword.getText().toString();
        if (!TextUtils.isEmpty(password)) {


            String authResponse = "approved";
            String authCode = null;

            String response = (String) response_type.getSelectedItem();
            switch (response) {
                case "Approved":
                    authResponse = "approved";
                    authCode = password;
                    break;

                case "Declined":
                    authResponse = "declined";
                    break;

                case "Failure":
                    authResponse = "failure";
                    break;
            }

            finishActivity(authResponse, authCode);
        } else {
            Toast.makeText(activity, "password is empty", Toast.LENGTH_SHORT).show();
        }
    }

    private void finishActivityError(String error) {
        Intent intent = new Intent();
        intent.putExtra("STEP_UP_ERROR", error);
        activity.setResult(RESULT_CANCELED, intent);
        activity.finish();
    }

    private void finishActivity(String authResponse, String authCode) {
        Intent result = new Intent();

        String issuerType = (String) issuer_list.getSelectedItem();

        if (issuerType.equals("VISA")) {
            result.putExtra(VISA_RESPONSE_KEY, authResponse);
            if (authCode != null) {
                result.putExtra(VISA_AUTH_CODE_KEY, authCode);
            }
        } else {
            result.putExtra(MASTER_CARD_RESPONSE_KEY, authResponse);
            if (authCode != null) {
                result.putExtra(MASTER_CARD_AUTH_CODE_KEY, authCode);
            }
        }
        activity.setResult(RESULT_OK, result);
        activity.finish();
    }
}