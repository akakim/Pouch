package com.pouch.common;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;

import com.kakao.auth.ISessionCallback;
import com.kakao.auth.KakaoSDK;
import com.kakao.auth.Session;
import com.kakao.util.exception.KakaoException;
import com.kakao.util.helper.log.Logger;
import com.pouch.R;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class LoginActivity extends BaseActivity {
    private SessionCallback callback;
    private final String TAG = getClass().getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        getAppKeyHash();
        callback = new SessionCallback();

        Session.getCurrentSession().addCallback(callback);
        if (!Session.getCurrentSession().checkAndImplicitOpen()) {
            setContentView(R.layout.activity_login);
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Session.getCurrentSession().removeCallback(callback);
        setContentView(R.layout.activity_login);

        if (Session.getCurrentSession().handleActivityResult(requestCode, resultCode, data)) {
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Session.getCurrentSession().removeCallback(callback);
    }

    private class SessionCallback implements ISessionCallback{
        public void onSessionOpened() {
            redirectSignupActivity();
        }
        @Override
        public void onSessionOpenFailed(KakaoException exception) {
            if(exception != null) {
                Logger.e(exception);
            }
            Log.v("SessionCallBack", "onSessionOpenFailed()");
            setContentView(R.layout.activity_login);
        }
    }
    /**
     * Kakao와 연동하기 위해 Hash 키 값을 얻기 위한 method.
     * 원래는 keytool이라는 cmd를 입력해서 구했지만 정확하게 나오지않아서
     *
     * 이 메소드를 사용해 Hash 키 값을 Log로 찍은 후 개발자센터에 등록.
     */
    private void getAppKeyHash() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md;

                md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String something = new String(Base64.encode(md.digest(), 0));
                System.out.println("HASH  " + something);
//                showSignedHashKey(something);
                Log.v("getKey ",something);
            }
        } catch (PackageManager.NameNotFoundException e1) {
            // TODO Auto-generated catch block
            Log.e("name not found", e1.toString());
        } catch (NoSuchAlgorithmException e) {

            Log.e("no such an algorithm", e.toString());
        } catch (Exception e) {
            Log.e("exception", e.toString());
        }
    }
}
