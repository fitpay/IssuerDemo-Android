package com.fitpay.issuerdemo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class MainActivity : AppCompatActivity() {

    private var appToAppButtonView: Button? = null
    private var pushProvButtonView: Button? = null
    private var ciqAppButtonView: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initViews()
    }

    private fun initViews() {
        appToAppButtonView = findViewById(R.id.app_to_app_button)
        pushProvButtonView = findViewById(R.id.push_prov_button)
        ciqAppButtonView = findViewById(R.id.ciq_app_button)

        appToAppButtonView?.setOnClickListener {
            val intent = Intent(this, AppToAppActivity::class.java)
            startActivity(intent)
        }

        pushProvButtonView?.setOnClickListener {
            val intent = Intent(this, PushProvisioningActivity::class.java)
            startActivity(intent)
        }

        ciqAppButtonView?.setOnClickListener {
            val intent = Intent(this, CIQSimulatorActivity::class.java)
            startActivity(intent)
        }
    }
}