package com.fitpay.issuerdemo;

import android.os.Bundle;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends FragmentActivity {

    @BindView(R.id.bottom_navigation_view)
    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        final AppToAppFragment appToAppFragment = new AppToAppFragment();
        final PushProvisioningFragment pushProvFragment = new PushProvisioningFragment();

        setCurrentFragment(appToAppFragment);

        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.app_to_app: setCurrentFragment(appToAppFragment); break;
                case R.id.push_prov: setCurrentFragment(pushProvFragment); break;
            }
            return true;
        });
    }

    private void setCurrentFragment(Fragment fragment) {
        getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.main_fragment, fragment)
                        .commit();
    }
}
