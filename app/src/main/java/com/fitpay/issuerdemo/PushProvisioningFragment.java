package com.fitpay.issuerdemo;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;

public class PushProvisioningFragment extends Fragment {

    private static final String TAG = PushProvisioningFragment.class.getCanonicalName();

    private final String AMP = "&";
    private final String EQU = "=";

    private final String GARMIN_PAY_URL = "https://connect.garmin.com/payment/push/android/mc?";
    private final String PUSH_DATA = "pushData";
    private final String PUSH_ACCOUNT_RECEIPT = "pushAccountReceipts";
    private final String CALLBACK_URL = "callbackURL";
    private final String CALLBACK_REQUIRED = "callbackRequired";
    private final String COMPLETE_ISSUER_APP_ACTIVATION = "completeIssuerAppActivation";

    private final String APPROVED_MASTER_CARD = "TST-MCC7F0AE-298E-48EB-AA43-A7C40B32DDDE";
    private final String ADDITIONAL_AUT_MASTER_CARD = "TST-MCC7F0AE-298E-48EB-AA43-ADDAUTH04566";
    private final String DECLINED_MASTER_CARD = "TST-MCC7F0AE-298E-48EB-AA43-DECLINE77777";
    private final String ERROR_MASTER_CARD = "TST-MCC7F0AE-298E-48EB-AA43-ERROR9999999";
    private final String PVL_PRIVATE_LABEL = "PVL-1E8GTJ94-9D5T-96MOWV36-56AZN95Y8DUL";
    private final String VISA = "eyJraWQiOiJhMjAwOTRjYy0yMTE1LTQzZTgtYjZkOS05Y2ZkMTYxODQwNWYiLCJjdHkiOiJhcHBsaWNhdGlvbi9qc29uIiwiZW5jIjoiQTI1NkdDTSIsInRhZyI6IldCbnZnaHZ4Vk9SNV9XSUh3TXBEaHciLCJhbGciOiJBMjU2R0NNS1ciLCJpdiI6Ill1ZHpGUW92emdkdGlheFAifQ.eMz0qvnyrK3sPpg9pgcc3M9cNDJDuGogKxO5J7QZX6k.z9b1YlXvI0YigapnwmrN6A.nZOTwdx5DlcarSPe_Y8yzjMH0lAhpNMvZwEHbojYD3WTc6sMjvs_m4kpf-ewpB6pzWQ_uSW93HBZPEPmWvRbhgFIk7c_xOaESk2f85S46dgJo_cRTso_jJXRVjQuqizabyOGM-Mnt5a1RfH6QvCSWrEKIV0NbtTjbXFrhcyRgaG-i8moa5lOMOuTLd4QHz4DF32ZC_aG5OQ5M7o8l_su7L7WEXgsu3f7TDc5r6Biyaei95pDwMZMaKIFFJWiBl0yEbJozA.lwpuG0VYDMh01CkiQw";

    private Spinner issuerList;
    private EditText callBackUrlEt;
    private CheckBox callbackUrlRequired;
    private CheckBox completeIssuerAppActivation;
    private Button btnSend;

    private Activity activity;
    private StringBuilder deepLink = new StringBuilder();
    private StringBuilder issuerString = new StringBuilder();
    private StringBuilder callbackUrlRequiredString = new StringBuilder();
    private StringBuilder completeIssuerAppActivationString = new StringBuilder();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_push_provisioning, container, false);
        ((MainActivity)getActivity()).setupActionBarTitle("Push Provisioning");
        initViews(view);
        activity = getActivity();
        setupViews();
        return view;
    }

    private void initViews(View view) {
        issuerList = view.findViewById(R.id.issuer_list);
        callBackUrlEt = view.findViewById(R.id.callback_url_et);
        callbackUrlRequired = view.findViewById(R.id.callback_required_chk);
        completeIssuerAppActivation = view.findViewById(R.id.complete_issuer_app_act_chk);
        btnSend = view.findViewById(R.id.btnSend);
        btnSend.setOnClickListener(v -> {
            if (issuerList.getSelectedItemPosition() == 0) {
                showNoIssuerSelectedDialog();
                return;
            }
            String deepLink = getDeepLink();
            Log.d(TAG, "Deep Link sent: " + deepLink);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(deepLink));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        });
    }

    void setupViews() {
        // Spinner
        Resources res = getResources();
        String[] issuerItems = res.getStringArray(R.array.issuers);

        ArrayAdapter<String> issuerAdapter = new ArrayAdapter<>(activity, android.R.layout.simple_spinner_dropdown_item, issuerItems);
        issuerList.setAdapter(issuerAdapter);
        issuerList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                issuerString.setLength(0);
                if (position == 1) {
                    issuerString.append(PUSH_ACCOUNT_RECEIPT)
                                    .append(EQU)
                                    .append(APPROVED_MASTER_CARD);
                } else if (position == 2) {
                    issuerString.append(PUSH_ACCOUNT_RECEIPT)
                                    .append(EQU)
                                    .append(ADDITIONAL_AUT_MASTER_CARD);
                } else if (position == 3) {
                    issuerString.append(PUSH_ACCOUNT_RECEIPT)
                                    .append(EQU)
                                    .append(DECLINED_MASTER_CARD);
                } else if (position == 4) {
                    issuerString.append(PUSH_ACCOUNT_RECEIPT)
                                    .append(EQU)
                                    .append(ERROR_MASTER_CARD);
                } else if (position == 5) {
                    issuerString.append(PUSH_ACCOUNT_RECEIPT)
                                    .append(EQU)
                                    .append(PVL_PRIVATE_LABEL);
                } else if (position == 6) {
                    issuerString.append(PUSH_DATA)
                                    .append(EQU)
                                    .append(VISA);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //Checkboxes
        callbackUrlRequired.setOnCheckedChangeListener((buttonView, isChecked) -> {
            callbackUrlRequiredString.setLength(0);
            if (isChecked) {
                callbackUrlRequiredString.append(AMP).append(CALLBACK_REQUIRED).append(EQU).append("true");
            } else {
                callbackUrlRequiredString.append(AMP).append(CALLBACK_REQUIRED).append(EQU).append("false");
            }
        });

        completeIssuerAppActivation.setOnCheckedChangeListener((buttonView, isChecked) -> {
            completeIssuerAppActivationString.setLength(0);
            if (isChecked) {
                completeIssuerAppActivationString.append(AMP).append(COMPLETE_ISSUER_APP_ACTIVATION).append(EQU).append("true");
            } else {
                completeIssuerAppActivationString.append(AMP).append(COMPLETE_ISSUER_APP_ACTIVATION).append(EQU).append("false");
            }
        });
    }

    private String getDeepLink() {
        StringBuilder callBackUrlEtString = new StringBuilder();
        if (!TextUtils.isEmpty(callBackUrlEt.getText())) {
            callBackUrlEtString.append(AMP)
                            .append(CALLBACK_URL)
                            .append(EQU)
                            .append(callBackUrlEt.getText());
        }
        deepLink = new StringBuilder(GARMIN_PAY_URL);
        return deepLink.append(issuerString)
                        .append(callBackUrlEtString)
                        .append(callbackUrlRequiredString)
                        .append(completeIssuerAppActivationString).toString();
    }

    private void showNoIssuerSelectedDialog() {
        new AlertDialog.Builder(activity)
                        .setTitle("Error")
                        .setMessage("Issuer is a mandatory field!")
                        .setPositiveButton(android.R.string.yes, null)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
    }
}