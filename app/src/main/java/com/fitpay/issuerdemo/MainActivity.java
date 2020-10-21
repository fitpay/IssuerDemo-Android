package com.fitpay.issuerdemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.etPassword)
    EditText etPassword;
    @BindView(R.id.issuer_list)
    Spinner issuer_list;
    @BindView(R.id.response_type)
    Spinner response_type;
    @BindView(R.id.request)
    TextView request;

    private boolean canSkipWithSuccess;

    private String VISA_AUTH_CODE_KEY = "STEP_UP_AUTH_CODE";
    private String VISA_RESPONSE_KEY = "STEP_UP_RESPONSE";
    private String MASTER_CARD_AUTH_CODE_KEY = "TAV";
    private String MASTER_CARD_RESPONSE_KEY = "issuerMobileAppAuthResponse";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        // setup spinners
        setupSpinners();

        if (getIntent() != null && getIntent().getAction() != null) {
            switch (getIntent().getAction()) {
                case "com.fitpay.issuerdemo.generate_auth_code":
                    request.setText(getIntent().getAction());
                    //submit password
                    etPassword.setVisibility(View.VISIBLE);
                    break;
                case "com.fitpay.issuerdemo.authenticate_user":
                    request.setText(getIntent().getAction());
                    canSkipWithSuccess = true;
                    // automatic flow, hide input text
                    etPassword.setVisibility(View.INVISIBLE);
                    break;
                default:
                    //launched manually
            }
        }
    }

    void setupSpinners() {
        String[] issuerItems = new String[]{"VISA", "MASTERCARD", "MAESTRO"};
        String[] responseItems = new String[]{"Approved", "Declined", "Failure"};

        ArrayAdapter<String> issuerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, issuerItems);
        issuer_list.setAdapter(issuerAdapter);
        ArrayAdapter<String> responseAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, responseItems);
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
            Toast.makeText(MainActivity.this, "password is empty", Toast.LENGTH_SHORT).show();
        }
    }

    private void finishActivityError(String error) {
        Intent intent = new Intent();
        intent.putExtra("STEP_UP_ERROR", error);
        setResult(RESULT_CANCELED, intent);
        finish();
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
        setResult(RESULT_OK, result);
        finish();
    }
}
