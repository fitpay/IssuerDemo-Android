package com.fitpay.issuerdemo

import androidx.fragment.app.FragmentActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import android.os.Bundle
import android.view.MenuItem
import androidx.fragment.app.Fragment

class PushProvisioningActivity : FragmentActivity() {
    private var bottomNavigationView: BottomNavigationView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_push_provisioning)
        setupActionBarTitle("Push Provisioning")
        initViews()
        val pushProvFragmentVisa = PushProvisioningFragment()
        val pushProvFragmentMc = PushProvisioningWebFragment()
        setCurrentFragment(pushProvFragmentVisa)
        bottomNavigationView?.setOnNavigationItemSelectedListener { item: MenuItem ->
            when (item.itemId) {
                R.id.push_prov -> setCurrentFragment(pushProvFragmentVisa)
                R.id.push_prov_custom -> setCurrentFragment(pushProvFragmentMc)
            }
            true
        }
    }

    private fun initViews() {
        bottomNavigationView = findViewById(R.id.bottom_navigation_view)
    }

    private fun setCurrentFragment(fragment: Fragment) {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.main_fragment, fragment)
            .commit()
    }

    fun setupActionBarTitle(title: String?) {
        val actionBar = actionBar
        if (actionBar != null) {
            actionBar.title = title
        }
    }
}