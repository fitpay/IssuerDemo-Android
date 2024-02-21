package com.fitpay.issuerdemo

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.CheckBox

class CIQSimulatorActivity : AppCompatActivity() {

    private var installButtonView: Button? = null
    private var callbackUrlCheckBox: CheckBox? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ciqsimulator)
        initViews()
    }

    private fun initViews() {
        installButtonView = findViewById(R.id.install_button)
        callbackUrlCheckBox = findViewById(R.id.check_box)
        installButtonView?.setOnClickListener {
            val deepLink = getDeepLink()
            Log.d(TAG, "Deep Link sent: $deepLink")
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(deepLink)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }

    private fun getDeepLink(): String {
        return if (callbackUrlCheckBox?.isChecked == true) {
            Uri.parse(GARMIN_PAY_URL)
                .buildUpon()
                .appendQueryParameter(CALLBACK_URL, CIQ_CALLBACK_URL)
                .toString()
        } else {
            GARMIN_PAY_URL
        }
    }

    companion object {
        private val TAG = CIQSimulatorActivity::class.java.canonicalName

        private const val GARMIN_PAY_URL = "https://connect.garmin.com/payment/add"
        private const val CIQ_CALLBACK_URL = "connectiq://details/?deviceId=fenix7x&amp;appId=0e1f20d8-c2c9-493e-be19-78eb55f1a373"
        private const val CALLBACK_URL = "callbackURL"
    }
}