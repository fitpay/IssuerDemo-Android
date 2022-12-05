package com.fitpay.issuerdemo

import android.app.Activity
import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.ViewGroup
import android.os.Bundle
import android.content.Intent
import android.net.Uri
import android.widget.AdapterView.OnItemSelectedListener
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.*
import androidx.fragment.app.Fragment
import java.lang.StringBuilder

class PushProvisioningFragment : Fragment() {
    private val AMP = "&"
    private val EQU = "="
    private val GARMIN_PAY_URL = "https://connect.garmin.com/payment/push/android/mc?"
    private val PUSH_DATA = "pushData"
    private val PUSH_ACCOUNT_RECEIPT = "pushAccountReceipts"
    private val CALLBACK_URL = "callbackURL"
    private val CALLBACK_URL_DEFAULT_VALUE = "https://tokenconnect.mcsrcteststore.com/"
    private val CALLBACK_REQUIRED = "callbackRequired"
    private val COMPLETE_ISSUER_APP_ACTIVATION = "completeIssuerAppActivation"
    private val APPROVED_MASTER_CARD = "TST-MCC7F0AE-298E-48EB-AA43-A7C40B32DDDE"
    private val ADDITIONAL_AUT_MASTER_CARD = "TST-MCC7F0AE-298E-48EB-AA43-ADDAUTH04566"
    private val DECLINED_MASTER_CARD = "TST-MCC7F0AE-298E-48EB-AA43-DECLINE77777"
    private val ERROR_MASTER_CARD = "TST-MCC7F0AE-298E-48EB-AA43-ERROR9999999"
    private val PVL_PRIVATE_LABEL = "TST-PVLGTJ94-9D5T-96MO-WV36-56AZN95Y8DUL"
    private val VISA =
        "eyJraWQiOiJhMjAwOTRjYy0yMTE1LTQzZTgtYjZkOS05Y2ZkMTYxODQwNWYiLCJjdHkiOiJhcHBsaWNhdGlvbi9qc29uIiwiZW5jIjoiQTI1NkdDTSIsInRhZyI6IldCbnZnaHZ4Vk9SNV9XSUh3TXBEaHciLCJhbGciOiJBMjU2R0NNS1ciLCJpdiI6Ill1ZHpGUW92emdkdGlheFAifQ.eMz0qvnyrK3sPpg9pgcc3M9cNDJDuGogKxO5J7QZX6k.z9b1YlXvI0YigapnwmrN6A.nZOTwdx5DlcarSPe_Y8yzjMH0lAhpNMvZwEHbojYD3WTc6sMjvs_m4kpf-ewpB6pzWQ_uSW93HBZPEPmWvRbhgFIk7c_xOaESk2f85S46dgJo_cRTso_jJXRVjQuqizabyOGM-Mnt5a1RfH6QvCSWrEKIV0NbtTjbXFrhcyRgaG-i8moa5lOMOuTLd4QHz4DF32ZC_aG5OQ5M7o8l_su7L7WEXgsu3f7TDc5r6Biyaei95pDwMZMaKIFFJWiBl0yEbJozA.lwpuG0VYDMh01CkiQw"
    private var issuerList: Spinner? = null
    private var callBackUrlEt: EditText? = null
    private var callbackUrlRequired: CheckBox? = null
    private var completeIssuerAppActivation: CheckBox? = null
    private var btnSend: Button? = null
    private var deepLink = StringBuilder()
    private val issuerString = StringBuilder()
    private val callbackUrlRequiredString = StringBuilder()
    private val completeIssuerAppActivationString = StringBuilder()

    private val itemSelectedListener = object : OnItemSelectedListener {
        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            issuerString.setLength(0)
            when (position) {
                1 -> {
                    issuerString.append(PUSH_ACCOUNT_RECEIPT)
                        .append(EQU)
                        .append(APPROVED_MASTER_CARD)
                }
                2 -> {
                    issuerString.append(PUSH_ACCOUNT_RECEIPT)
                        .append(EQU)
                        .append(ADDITIONAL_AUT_MASTER_CARD)
                }
                3 -> {
                    issuerString.append(PUSH_ACCOUNT_RECEIPT)
                        .append(EQU)
                        .append(DECLINED_MASTER_CARD)
                }
                4 -> {
                    issuerString.append(PUSH_ACCOUNT_RECEIPT)
                        .append(EQU)
                        .append(ERROR_MASTER_CARD)
                }
                5 -> {
                    issuerString.append(PUSH_ACCOUNT_RECEIPT)
                        .append(EQU)
                        .append(PVL_PRIVATE_LABEL)
                }
                6 -> {
                    issuerString.append(PUSH_DATA)
                        .append(EQU)
                        .append(VISA)
                }
            }
        }

        override fun onNothingSelected(parent: AdapterView<*>?) {}
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_push_provisioning, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews(view)
        setupViews()
    }

    private fun initViews(view: View) {
        issuerList = view.findViewById(R.id.issuer_list)
        callBackUrlEt = view.findViewById(R.id.callback_url_et)
        callBackUrlEt?.setText(CALLBACK_URL_DEFAULT_VALUE)
        callbackUrlRequired = view.findViewById(R.id.callback_required_chk)
        callbackUrlRequired?.isChecked = true
        completeIssuerAppActivation = view.findViewById(R.id.complete_issuer_app_act_chk)
        btnSend = view.findViewById(R.id.btnSend)
        btnSend?.setOnClickListener {
            if (issuerList?.selectedItemPosition == 0) {
                showNoIssuerSelectedDialog()
            }
            val deepLink = getDeepLink()
            Log.d(TAG, "Deep Link sent: $deepLink")
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(deepLink)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }

    private fun setupViews() {
        // Spinner
        val res = resources
        val issuerItems = res.getStringArray(R.array.issuers)
        val issuerAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, issuerItems)
        issuerList?.adapter = issuerAdapter
        issuerList?.onItemSelectedListener = itemSelectedListener

        //Checkboxes
        callbackUrlRequired?.isChecked?.let {
            appendCallbackUrlRequired(it)
        }

        callbackUrlRequired?.setOnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->
            appendCallbackUrlRequired(isChecked)
        }

        completeIssuerAppActivation?.isChecked?.let {
            appendCompleteIssuerAppActivation(it)
        }

        completeIssuerAppActivation?.setOnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->
            appendCompleteIssuerAppActivation(isChecked)
        }
    }

    private fun appendCallbackUrlRequired(isChecked: Boolean) {
        callbackUrlRequiredString.setLength(0)
        if (isChecked) {
            callbackUrlRequiredString.append(AMP).append(CALLBACK_REQUIRED).append(EQU).append("true")
        } else {
            callbackUrlRequiredString.append(AMP).append(CALLBACK_REQUIRED).append(EQU).append("false")
        }
    }
    private fun appendCompleteIssuerAppActivation(isChecked: Boolean) {
        completeIssuerAppActivationString.setLength(0)
        if (isChecked) {
            completeIssuerAppActivationString.append(AMP).append(COMPLETE_ISSUER_APP_ACTIVATION).append(EQU).append("true")
        } else {
            completeIssuerAppActivationString.append(AMP).append(COMPLETE_ISSUER_APP_ACTIVATION).append(EQU).append("false")
        }
    }

    private fun getDeepLink(): String {
        val callBackUrlEtString = StringBuilder()
        if (!TextUtils.isEmpty(callBackUrlEt?.text)) {
            callBackUrlEtString.append(AMP)
                .append(CALLBACK_URL)
                .append(EQU)
                .append(callBackUrlEt?.text)
        }
        deepLink = StringBuilder(GARMIN_PAY_URL)
        return deepLink.append(issuerString)
            .append(callBackUrlEtString)
            .append(callbackUrlRequiredString)
            .append(completeIssuerAppActivationString).toString()
    }

    private fun showNoIssuerSelectedDialog() {
        AlertDialog.Builder(activity)
            .setTitle("Error")
            .setMessage("Issuer is a mandatory field!")
            .setPositiveButton(android.R.string.yes, null)
            .setIcon(android.R.drawable.ic_dialog_alert)
            .show()
    }

    companion object {
        private val TAG = PushProvisioningFragment::class.java.canonicalName
    }
}