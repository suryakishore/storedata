package com.storedata.com.splash;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.storedata.com.R;
import com.storedata.com.SessionManager;
import com.storedata.com.login.LoginActivity;
import com.storedata.com.notepad.NotePadListActivity;

import java.io.IOException;

public class SplashActivity extends AppCompatActivity {
    private String browserData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);


        final SessionManager sessionManager = new SessionManager(this);

        String token = FirebaseInstanceId.getInstance().getToken();
        if (token == null) {
            try {
                FirebaseInstanceId.getInstance().deleteInstanceId();
            } catch (IOException e) {
                e.printStackTrace();
                Log.d("exe", "IOException" + e.getMessage());
            }
            token = FirebaseInstanceId.getInstance().getToken();
            Log.d("exe", "token" + token);
        }
        sessionManager.setRegister_ID(token);
        Log.d("exe", "token" + token);


        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            browserData = bundle.getString(Intent.EXTRA_TEXT);

            Log.d("exe", "browserData" + browserData);
        }


        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //Do something after 2000ms
                if (sessionManager.getUserLoginStatus()) {
                    Intent notePadIntent = new Intent(SplashActivity.this, NotePadListActivity.class);
                    notePadIntent.putExtra("browserData", browserData);
                    startActivity(notePadIntent);
                } else {
                    Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                    intent.putExtra("browserData", browserData);
                    startActivity(intent);
                }
                SplashActivity.this.finish();
            }
        }, 2000);
    }
}
