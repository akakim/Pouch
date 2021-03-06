/**
 * Copyright 2014 Daum Kakao Corp.
 *
 * Redistribution and modification in source or binary forms are not permitted without specific prior written permission. 
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.kakao.auth.api;

import android.Manifest.permission;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.webkit.CookieSyncManager;

import com.kakao.auth.AgeAuthParamBuilder;
import com.kakao.auth.AuthService.AgeAuthStatus;
import com.kakao.auth.KakaoSDK;
import com.kakao.auth.SingleNetworkTask;
import com.kakao.auth.StringSet;
import com.kakao.auth.authorization.AuthorizationResult;
import com.kakao.auth.authorization.accesstoken.AccessToken;
import com.kakao.auth.authorization.accesstoken.AccessTokenRequest;
import com.kakao.auth.authorization.authcode.KakaoWebViewDialog;
import com.kakao.auth.authorization.authcode.KakaoWebViewDialog.OnWebViewCompleteListener;
import com.kakao.auth.network.request.AccessTokenInfoRequest;
import com.kakao.auth.network.response.AccessTokenInfoResponse;
import com.kakao.network.ServerProtocol;
import com.kakao.network.response.ResponseBody;
import com.kakao.network.response.ResponseData;
import com.kakao.util.exception.KakaoException;
import com.kakao.util.helper.Utility;
import com.kakao.util.helper.log.Logger;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Bloking으로 동작하며, 인증관련 내부 API콜을 한다.
 * @author leoshin
 */
public class AuthApi {
    public static void synchronizeCookies(Context context) {
        CookieSyncManager syncManager = CookieSyncManager.createInstance(context);
        syncManager.sync();
    }

    public static AuthorizationResult requestAccessToken(Context context, String appKey, String redirectUri, String authCode, String refreshToken, String approvalType) throws Exception {
        SingleNetworkTask networkTask = new SingleNetworkTask();
        ResponseBody result = networkTask.requestAuth(new AccessTokenRequest(context, appKey, redirectUri, authCode, refreshToken, approvalType));
        final AccessToken accessToken = new AccessToken(result);
        return AuthorizationResult.createSuccessAccessTokenResult(accessToken);
    }

    private static boolean requestWebviewAuth(Context context, AgeAuthParamBuilder builder, boolean useSmsReceiver, OnWebViewCompleteListener listener) {
        synchronizeCookies(context);

        boolean isUsingTimer = KakaoSDK.getAdapter().getSessionConfig().isUsingWebviewTimer();
        Uri uri = Utility.buildUri(ServerProtocol.AGE_AUTH_AUTHORITY, ServerProtocol.ACCESS_AGE_AUTH_PATH, builder.build());
        Logger.d("AgeAuth request Url : " + uri);
        KakaoWebViewDialog loginDialog = new KakaoWebViewDialog(context, uri.toString(), isUsingTimer, useSmsReceiver, listener);
        loginDialog.show();
        return true;
    }

    /**
     * {@link com.kakao.auth.ErrorCode} NEED_TO_AGE_AUTHENTICATION(-405)가 발생하였을때 연령인증을 시도한다.
     * 이때 연령인증중 발생할 수 있는 sms수신여부를 해당앱의 permission이 존재하는지 여부를 보고 판단하도록 한다.
     * @param context 현재 화면의 topActivity의 context
     * @param builder AgeAuthParamBuilder
     * @return status code
     */
    public static int requestShowAgeAuthDialog(final Context context, final AgeAuthParamBuilder builder) {
        return requestShowAgeAuthDialog(builder, Utility.isUsablePermission(context, permission.RECEIVE_SMS));
    }

    public static int requestShowAgeAuthDialog(final AgeAuthParamBuilder builder, final boolean useSmsReceiver) {
        final Context context = KakaoSDK.getAdapter().getApplicationConfig().getTopActivity();
        if (useSmsReceiver && !Utility.isUsablePermission(context, permission.RECEIVE_SMS)) {
            throw new SecurityException("Don't have permission RECEIVE_SMS");
        }

        final AtomicInteger result = new AtomicInteger();
        final CountDownLatch lock = new CountDownLatch(1);
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                try {
                    OnWebViewCompleteListener listener = new OnWebViewCompleteListener() {
                        @Override
                        public void onComplete(String redirectURL, KakaoException exception) {
                            int status = AgeAuthStatus.CLIENT_ERROR.getValue();
                            if (redirectURL != null) {
                                if (Uri.parse(redirectURL).getQueryParameter(StringSet.status) != null) {
                                    status = Integer.valueOf(Uri.parse(redirectURL).getQueryParameter(StringSet.status));
                                }
                            }

                            result.set(status);
                            lock.countDown();
                        }
                    };

                    requestWebviewAuth(context, builder, useSmsReceiver, listener);
                } catch (Exception e) {
                    result.set(AgeAuthStatus.CLIENT_ERROR.getValue());
                    lock.countDown();
                }
            }
        });

        // 사용자가 취소를 하여도 종료.
        try {
            lock.await();
        } catch (InterruptedException ignor) {
        }

        return result.get();
    }

    public static int requestAccessTokenInfo() throws Exception {
        SingleNetworkTask networkTask = new SingleNetworkTask();
        ResponseData result = networkTask.requestApi(new AccessTokenInfoRequest());
        return new AccessTokenInfoResponse(result).getExpiresInMillis();
    }
}
