package com.fitpay.issuerdemo

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button

class CIQSimulatorActivity : AppCompatActivity() {

    private val GARMIN_PAY_URL = "https://connect.garmin.com/payment/ciq/android"

    private var installButtonView: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ciqsimulator)
        initViews()
    }

    private fun initViews() {
        installButtonView = findViewById(R.id.install_button)
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
        return GARMIN_PAY_URL
    }

    companion object {
        private val TAG = CIQSimulatorActivity::class.java.canonicalName
    }
}