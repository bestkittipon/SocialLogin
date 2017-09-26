package com.bsd.socialloginlib;


/**
 * Created by KittipolC on 22/9/2560.
 */

public interface iSocialProfileCallback {
    void onLoginSuccess(ProfileInfo profileInfo);
    void onCancel();
    void onError(Exception e);
    void onLogout();
}
