package com.fitpay.issuerdemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.etPassword)
    EditText etPassword;

    private boolean canSkipWithSuccess;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        if (getIntent() != null && getIntent().getAction() != null) {
            switch (getIntent().getAction()) {
                case "com.fitpay.issuerdemo.generate_auth_code":
                    //submit password
                    break;
                case "com.fitpay.issuerdemo.authenticate_user":
                    canSkipWithSuccess = true;
                    break;
                default:
                    //launched manually
            }
        }
    }

    @OnClick(R.id.btnSubmit)
    void submit() {
        String password = etPassword.getText().toString();
        if (!TextUtils.isEmpty(password)) {
            if (canSkipWithSuccess) {
                finishActivity("approved", null);
                return;
            }

            String authResponse = "approved";
            String authCode = null;

            String lastStr = password.substring(password.length() - 1);
            switch (lastStr) {
                case "d":
                    authResponse = "declined";
                    break;
                case "f":
                    authResponse = "failure";
                    break;
                default:
                    authCode = password;
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
        result.putExtra("STEP_UP_RESPONSE", authResponse);
        if (authCode != null) {
            result.putExtra("STEP_UP_AUTH_CODE", authCode);
        }
        setResult(RESULT_OK, result);
        finish();
    }
}
