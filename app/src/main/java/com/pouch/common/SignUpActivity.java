package com.pouch.common;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.kakao.auth.ApiResponseCallback;
import com.kakao.auth.ErrorCode;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.MeResponseCallback;
import com.kakao.usermgmt.response.model.UserProfile;
import com.kakao.util.helper.log.Logger;
import com.pouch.R;
import com.pouch.ui.MainActivity;
import com.pouch.widget.KakaoToast;
import com.squareup.picasso.Picasso;

import java.lang.ref.WeakReference;
import java.util.Map;

/**
 * 유효한 세션이 있다는 검증 후
 * me를 호출하여 가입 여부에 따라 가입 페이지를 그리던지 Main 페이지로 이동 시킨다.
 */
public class SignUpActivity extends BaseActivity {
    private final String TAG = getClass().getSimpleName();

    public static final int REQUEST_CODE = 1001;
    private ImageView Profile_img;
    private TextView userNameTextView;


//    private MainActivity activity;
    /**
     * Main으로 넘길지 가입 페이지를 그릴지 판단하기 위해 me를 호출한다.
     * @param savedInstanceState 기존 session 정보가 저장된 객체
     */
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
  //      activity = getApplication();
        Log.v(TAG, "Hash Key" + getResources().getString(R.string.kakao_app_key));
        requestMe();

    }

    protected void showSignup() {
        setContentView(R.layout.activity_signup);
        final ExtraUserPropertyLayout extraUserPropertyLayout = (ExtraUserPropertyLayout) findViewById(R.id.extra_user_property);
        Button signupButton = (Button) findViewById(R.id.buttonSignup);
        signupButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                requestSignUp(extraUserPropertyLayout.getProperties());
            }
        });
    }

    private void requestSignUp(final Map<String, String> properties) {
        UserManagement.requestSignup(new ApiResponseCallback<Long>() {
            @Override
            public void onNotSignedUp() {
            }

            @Override
            public void onSuccess(Long result) {
                requestMe();
            }

            @Override
            public void onFailure(ErrorResult errorResult) {
                final String message = "UsermgmtResponseCallback : failure : " + errorResult;
                com.kakao.util.helper.log.Logger.w(message);
                KakaoToast.makeToast(self, message, Toast.LENGTH_LONG).show();
                finish();
            }

            @Override
            public void onSessionClosed(ErrorResult errorResult) {
            }
        }, properties);
    }

    /**
     * 사용자의 상태를 알아 보기 위해 me API 호출을 한다.
     */
    protected void requestMe() {
        UserManagement.requestMe(new MeResponseCallback() {
            @Override
            public void onFailure(ErrorResult errorResult) {
                String message = "failed to get user info. msg=" + errorResult;
                Logger.d(message);

                ErrorCode result = ErrorCode.valueOf(errorResult.getErrorCode());
                if (result == ErrorCode.CLIENT_ERROR_CODE) {
                    KakaoToast.makeToast(getApplicationContext(), "service unvaluable", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    redirectLoginActivity();
                }
            }

            @Override
            public void onSessionClosed(ErrorResult errorResult) {
                redirectLoginActivity();
            }

            @Override
            public void onSuccess(UserProfile userProfile) {
                Log.v(TAG, "onSuccess");
                Logger.d("UserProfile : " + userProfile);


              String profileUrl = userProfile.getProfileImagePath();
              String userName = userProfile.getNickname();
                long id = userProfile.getId();



                redirectMainActivity(profileUrl,userName,id);
            }

            @Override
            public void onNotSignedUp() {
                showSignup();
            }
        });
    }

    private void redirectMainActivity(String url,String username,long id) {
        Log.v(TAG, "redirectMainActivity() this TO KakaoServiceListActivity");
        Intent i = new Intent(this,MainActivity.class);
        i.putExtra("ProfileURL",url);
        i.putExtra("UserName",username);
        i.putExtra("ID",id);
        startActivity(i);
//        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}
