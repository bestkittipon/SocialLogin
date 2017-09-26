package com.bsd.socialloginlib.testsociallogin;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.bsd.socialloginlib.FacebookLogin;
import com.bsd.socialloginlib.GoogleLogin;
import com.bsd.socialloginlib.ProfileInfo;
import com.bsd.socialloginlib.iSocialProfileCallback;


public class MainActivity extends AppCompatActivity implements iSocialProfileCallback {

    FacebookLogin facebookLogin;
    GoogleLogin googleLogin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        facebookLogin = new FacebookLogin(this);

        googleLogin = new GoogleLogin(this);


        Button btnLoginFB = (Button)findViewById(R.id.btnLoginFB);
        btnLoginFB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                facebookLogin.login();
            }
        });

        Button btnLogoutFB = (Button)findViewById(R.id.btnLogoutFB);
        btnLogoutFB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                facebookLogin.logout();
            }
        });

        Button btnLoginGG = (Button)findViewById(R.id.btnLoginGG);
        btnLoginGG.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                googleLogin.signIn();
            }
        });

        Button btnLogoutGG = (Button)findViewById(R.id.btnLogoutGG);
        btnLogoutGG.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                googleLogin.signOut();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        facebookLogin.facebookOnActivityResult(requestCode , resultCode , data);
        googleLogin.googleOnActivityResult(requestCode , resultCode , data);
    }

    @Override
    public void onLoginSuccess(ProfileInfo profileInfo) {

    }

    @Override
    public void onCancel() {

    }

    @Override
    public void onError(Exception e) {

    }

    @Override
    public void onLogout() {

    }
}
