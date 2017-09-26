package com.bsd.socialloginlib;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

/**
 * Created by KittipolC on 22/9/2560.
 */

public class FacebookLogin  {
    private Context mContext;
    private Activity mActivity;
    private LoginManager mLoginManager;
    private CallbackManager callbackManager;
    private iSocialProfileCallback listener;
    private ProfileInfo profileInfo;

    private FacebookLogin() {
        mLoginManager = LoginManager.getInstance();
        initCallBackFacebook();
    }

    public FacebookLogin(iSocialProfileCallback pListener) {
        this();
        if(pListener instanceof Activity) {
            mActivity = (Activity) pListener;
        }else if(pListener instanceof Fragment) {
            mActivity = ((Fragment)pListener).getActivity();
        }
        mContext = mActivity.getApplicationContext();
        listener = pListener;
    }


    public void login() {
        try {
            mLoginManager.logInWithReadPermissions(mActivity, Arrays.asList("public_profile", "email"));
        }catch (Exception e) {
            throw e;
        }
    }

    public void logout(){
        try {
            FacebookSdk.sdkInitialize(mActivity);
            mLoginManager.logOut();
            listener.onLogout();
        }catch (Exception e) {
            throw e;
        }
    }

    public boolean isLoggedInFacebook(){
        try {
            return AccessToken.getCurrentAccessToken() != null;
        }catch (Exception e) {
            return false;
        }
    }

    public void facebookOnActivityResult(int requestCode, int resultCode, Intent data){
        callbackManager.onActivityResult(requestCode , resultCode , data);
    }

    private void initCallBackFacebook(){
        FacebookSdk.sdkInitialize(mContext);
        callbackManager = CallbackManager.Factory.create();
        mLoginManager.registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(final LoginResult loginResult) {

                        GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken() , new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                try {
                                    Profile profile = Profile.getCurrentProfile();
                                    profileInfo = new ProfileInfo();
                                    profileInfo.setToken(loginResult.getAccessToken().getToken());
                                    if (!object.has("email") && !object.isNull("email") && !object.getString("email").isEmpty()) {
                                        profileInfo.setEmail(object.getString("email"));
                                    }

                                    if(profile != null) {
                                        if (!profile.getFirstName().isEmpty())
                                            profileInfo.setFristName(profile.getFirstName());
                                        if (!profile.getLastName().isEmpty())
                                            profileInfo.setLastName(profile.getLastName());
                                        if (!profile.getId().isEmpty())
                                            profileInfo.setId(profile.getId());
                                    }

                                    if(response.getJSONObject() != null && !response.getJSONObject().isNull("picture")){
                                        profileInfo.setUrlImage((String) response.getJSONObject().getJSONObject("picture").getJSONObject("data").get("url"));
                                    }
                                    listener.onLoginSuccess(profileInfo);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });

                        Bundle parameters = new Bundle();
                        parameters.putString("fields", "id,name,email,picture.type(large)");
                        request.setParameters(parameters);
                        request.executeAsync();

                    }

                    @Override
                    public void onCancel() {
                        listener.onCancel();
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        listener.onError(exception);
                    }
                });
    }
}
