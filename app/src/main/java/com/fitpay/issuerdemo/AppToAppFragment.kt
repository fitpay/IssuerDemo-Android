package com.fitpay.issuerdemo

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.*
import androidx.fragment.app.Fragment

class AppToAppFragment : Fragment() {
    private var etPassword: EditText? = null
    private var issuer_list: Spinner? = null
    private var response_type: Spinner? = null
    private var request: TextView? = null
    private var btnSubmit: Button? = null
    private var canSkipWithSuccess = false
    private var activity: Activity? = null
    private var intent: Intent? = null
    private val VISA_AUTH_CODE_KEY = "STEP_UP_AUTH_CODE"
    private val VISA_RESPONSE_KEY = "STEP_UP_RESPONSE"
    private val MASTER_CARD_AUTH_CODE_KEY = "TAV"
    private val MASTER_CARD_RESPONSE_KEY = "issuerMobileAppAuthResponse"
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_app_to_app, container, false)
        (getActivity() as PushProvisioningActivity?)!!.setupActionBarTitle("App to app verification")
        initViews(view)
        activity = getActivity()
        intent = activity!!.intent

        // setup spinners
        setupSpinners()
        if (intent != null && intent!!.action != null) {
            when (intent!!.action) {
                "com.fitpay.issuerdemo.generate_auth_code" -> {
                    request!!.text = intent!!.action
                    //submit password
                    etPassword!!.visibility = View.VISIBLE
                }
                "com.fitpay.issuerdemo.authenticate_user" -> {
                    request!!.text = intent!!.action
                    canSkipWithSuccess = true
                    // automatic flow, hide input text
                    etPassword!!.visibility = View.INVISIBLE
                }
                else -> {}
            }
        }
        return view
    }

    private fun initViews(view: View) {
        etPassword = view.findViewById(R.id.etPassword)
        issuer_list = view.findViewById(R.id.issuer_list)
        response_type = view.findViewById(R.id.response_type)
        request = view.findViewById(R.id.request)
        btnSubmit = view.findViewById(R.id.btnSubmit)
        btnSubmit?.setOnClickListener {
            if (canSkipWithSuccess) {
                handleAutomaticFlow()
            }
            handleAuthCodeFlow()
        }
    }

    fun setupSpinners() {
        val issuerItems = arrayOf("VISA", "MASTERCARD", "MAESTRO")
        val responseItems = arrayOf("Approved", "Declined", "Failure")
        val issuerAdapter = ArrayAdapter(activity!!, android.R.layout.simple_spinner_dropdown_item, issuerItems)
        issuer_list!!.adapter = issuerAdapter
        val responseAdapter = ArrayAdapter(activity!!, android.R.layout.simple_spinner_dropdown_item, responseItems)
        response_type!!.adapter = responseAdapter
    }

    private fun handleAutomaticFlow() {
        val selectedResponse = response_type!!.selectedItem as String
        when (selectedResponse) {
            "Approved" -> finishActivity("approved", null)
            "Declined" -> finishActivity("declined", null)
            "Failure" -> finishActivity("failure", null)
        }
    }

    private fun handleAuthCodeFlow() {
        val password = etPassword!!.text.toString()
        if (!TextUtils.isEmpty(password)) {
            var authResponse = "approved"
            var authCode: String? = null
            val response = response_type!!.selectedItem as String
            when (response) {
                "Approved" -> {
                    authResponse = "approved"
                    authCode = password
                }
                "Declined" -> authResponse = "declined"
                "Failure" -> authResponse = "failure"
            }
            finishActivity(authResponse, authCode)
        } else {
            Toast.makeText(activity, "password is empty", Toast.LENGTH_SHORT).show()
        }
    }

    private fun finishActivityError(error: String) {
        val intent = Intent()
        intent.putExtra("STEP_UP_ERROR", error)
        activity!!.setResult(Activity.RESULT_CANCELED, intent)
        activity!!.finish()
    }

    private fun finishActivity(authResponse: String, authCode: String?) {
        val result = Intent()
        val issuerType = issuer_list!!.selectedItem as String
        if (issuerType == "VISA") {
            result.putExtra(VISA_RESPONSE_KEY, authResponse)
            if (authCode != null) {
                result.putExtra(VISA_AUTH_CODE_KEY, authCode)
            }
        } else {
            result.putExtra(MASTER_CARD_RESPONSE_KEY, authResponse)
            if (authCode != null) {
                result.putExtra(MASTER_CARD_AUTH_CODE_KEY, authCode)
            }
        }
        activity!!.setResult(Activity.RESULT_OK, result)
        activity!!.finish()
    }
}