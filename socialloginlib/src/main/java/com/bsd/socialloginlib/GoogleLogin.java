package com.bsd.socialloginlib;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

/**
 * Created by KittipolC on 25/9/2560.
 */

public class GoogleLogin {
    private Activity mActivity;
    private Context mContext;
    private iSocialProfileCallback listener;
    private GoogleApiClient mGoogleApiClient;
    private final int RC_SIGN_IN = 11001;
    private ProfileInfo profileInfo;

    private GoogleLogin() {

    }

    public GoogleLogin(iSocialProfileCallback pListener) {
        this();
        if(pListener instanceof Activity) {
            mActivity = (Activity) pListener;
        }else if(pListener instanceof Fragment) {
            mActivity = ((Fragment)pListener).getActivity();
        }
        mContext = mActivity.getApplicationContext();
        listener = pListener;

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestProfile()
                .requestId()
                .requestIdToken(mContext.getResources().getString(R.string.client_id))
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(mContext)
                .enableAutoManage((FragmentActivity) mActivity, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }

    public void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        mActivity.startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    public void signOut() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        listener.onLogout();
                    }
                });
    }

    public boolean isLoggedInGoogle(){
        try {
            return mGoogleApiClient != null && mGoogleApiClient.isConnected();
        }catch (Exception e) {
            return false;
        }
    }

    public void googleOnActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if(result.isSuccess() && result.getSignInAccount() != null) {
                profileInfo = new ProfileInfo();
                profileInfo.setToken(result.getSignInAccount().getIdToken());
                profileInfo.setId(result.getSignInAccount().getId());
                profileInfo.setFristName(result.getSignInAccount().getGivenName());
                profileInfo.setLastName(result.getSignInAccount().getFamilyName());
                profileInfo.setUrlImage(result.getSignInAccount().getPhotoUrl().toString());
                profileInfo.setEmail(result.getSignInAccount().getEmail());
                listener.onLoginSuccess(profileInfo);
            }else {
                listener.onError(null);
            }
        }
    }
}
