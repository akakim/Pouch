package com.pouch.common;

import android.app.Activity;
import android.content.Intent;

import com.pouch.ui.MainActivity;

/**
 * Created by USER on 2016-06-30.
 */
public class BaseActivity extends Activity {

    protected static Activity self;

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
    }

    private void clearReferences() {

        Activity currActivity = GlobalApplication.getCurrentActivity();
        if (currActivity != null && currActivity.equals(this)) {
            GlobalApplication.setCurrentActivity(null);
        }
    }

    protected void redirectLoginActivity() {
        final Intent intent = new Intent(this.getApplicationContext(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
        finish();
    }

    protected void redirectSignupActivity() {
        final Intent intent = new Intent(this, SignUpActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
        finish();
    }

}
