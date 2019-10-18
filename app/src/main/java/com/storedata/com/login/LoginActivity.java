package com.storedata.com.login;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.ads.Ad;
import com.facebook.ads.AdChoicesView;
import com.facebook.ads.AdError;
import com.facebook.ads.AdIconView;
import com.facebook.ads.NativeAdListener;
import com.facebook.ads.NativeBannerAd;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.storedata.com.AppController;
import com.storedata.com.Note;
import com.storedata.com.R;
import com.storedata.com.SessionManager;
import com.storedata.com.notepad.NotePadListActivity;
import com.storedata.com.signup.SignUpActivity;
import com.storedata.com.sqlitedatabase.DatabaseHelper;
import com.storedata.com.sqlitedatabase.User;
import com.storedata.com.utility.Utility;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener, TextWatcher {
    private EditText userNameEt, passWordEt;
    private Button loginBtn, signupBtn;
    private RelativeLayout back_button;
    private DatabaseHelper databaseHelper;
    private SessionManager sessionManager;
    private RelativeLayout nativeBannerAdContainer;
    private LinearLayout adView;
    private NativeBannerAd nativeBannerAd;
    private DatabaseReference mDatabase;
    private Activity mActivity;
    private boolean checkUser = false;
    private String pushKey;
    private Bundle bundle;
    private String browserData, userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mActivity = LoginActivity.this;

        userNameEt = findViewById(R.id.userNameEt);
        passWordEt = findViewById(R.id.passWordEt);
        loginBtn = findViewById(R.id.loginBtn);
        signupBtn = findViewById(R.id.signupBtn);
        back_button = findViewById(R.id.back_button);
        loginBtn.setOnClickListener(this);
        signupBtn.setOnClickListener(this);
        back_button.setOnClickListener(this);
        userNameEt.addTextChangedListener(this);
        passWordEt.addTextChangedListener(this);
        databaseHelper = new DatabaseHelper(this);
        sessionManager = new SessionManager(this);
        mDatabase = FirebaseDatabase.getInstance().getReference("SignUpWithUsernameAndEmail");
        bundle = getIntent().getExtras();
        if (bundle != null)
            browserData = bundle.getString("browserData");
        initializeAd();


        Observable<List<Note>> notesObservable = AppController.getInstance().getNotesObservable();

        Observer<List<Note>> notesObserver = getNotesObserver();

        notesObservable.observeOn(Schedulers.io())
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribeWith(notesObserver);

        notesObservable.observeOn(Schedulers.io())
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribeWith(notesObserver);


    }


    private Observer<List<Note>> getNotesObserver() {
        return new Observer<List<Note>>() {

            @Override
            public void onSubscribe(Disposable d) {
                Log.d("exe", "onSubscribe");
            }

            @Override
            public void onNext(List<Note> notes) {
                Log.d("exe", "onNext: " + notes.toString());

            }



            @Override
            public void onError(Throwable e) {
                Log.e("exe", "onError: " + e.getMessage());
            }

            @Override
            public void onComplete() {
                Log.d("exe", "onComplete");
            }
        };
    }


    private void initializeAd() {

        nativeBannerAd = new NativeBannerAd(this, "YOUR_PLACEMENT_ID");
        nativeBannerAd.setAdListener(new NativeAdListener() {

            @Override
            public void onMediaDownloaded(Ad ad) {

            }

            @Override
            public void onError(Ad ad, AdError adError) {

            }

            @Override
            public void onAdLoaded(Ad ad) {
                // Race condition, load() called again before last ad was displayed
                if (nativeBannerAd == null || nativeBannerAd != ad) {
                    return;
                }
                // Inflate Native Banner Ad into Container
                inflateAd(nativeBannerAd);
            }

            @Override
            public void onAdClicked(Ad ad) {

            }

            @Override
            public void onLoggingImpression(Ad ad) {

            }

        });
        // load the ad
        nativeBannerAd.loadAd();


    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.signupBtn:
                Intent intent = new Intent(this, SignUpActivity.class);
                intent.putExtra("browserData", browserData);
                startActivity(intent);
                break;
            case R.id.back_button:
                onBackPressed();
                break;
            case R.id.loginBtn:

                if (Utility.isNetworkAvailable(mActivity)) {
                    mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot childDataSnapshot : dataSnapshot.getChildren()) {
                                User user = childDataSnapshot.getValue(User.class);
                                //  Log.v("exe", "getKey" + childDataSnapshot.getKey());
                                if (user != null) {
                                    Log.v("exe", "userName" + user.getName() + user.getPassword());
                                    if ((user.getName().equals(userNameEt.getText().toString().trim())) && (user.getPassword().equals(passWordEt.getText().toString().trim()))) {
                                        checkUser = true;
                                        pushKey = childDataSnapshot.getKey();
                                        userEmail=user.getEmail();
                                        break;
                                    } else {
                                        checkUser = false;
                                    }
                                }
                            }

                            Log.v("exe", "DataSnapshot" + dataSnapshot.getChildrenCount() + "checkUser" + checkUser);


                            //  isUserNameExist = databaseHelper.checkUser(editable.toString().trim());
                            Log.d("exe", "checkUser" + checkUser);

                            if (checkUser) {
                                Toast.makeText(mActivity, "Login successfully", Toast.LENGTH_SHORT).show();
                                Intent notePadIntent = new Intent(mActivity, NotePadListActivity.class);
                                notePadIntent.putExtra("browserData", browserData);
                                sessionManager.setUserLoginStatus(true);
                                sessionManager.setUSER_NAME(userNameEt.getText().toString().trim());
                                sessionManager.setPUSH_KEY(pushKey);
                                sessionManager.setUserEmail(userEmail);
                                startActivity(notePadIntent);
                                finish();
                            } else {
                                Toast.makeText(mActivity, "user not exist,Please create an user.", Toast.LENGTH_SHORT).show();
                            }

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });


                    // boolean checkUser = databaseHelper.checkUser(userNameEt.getText().toString().trim(), passWordEt.getText().toString().trim());


                } else {
                    Toast.makeText(mActivity, "Please connect to internet", Toast.LENGTH_SHORT).show();
                }


                break;
        }
    }


    private void inflateAd(NativeBannerAd nativeBannerAd) {
        // Unregister last ad
        nativeBannerAd.unregisterView();

        // Add the Ad view into the ad container.
        nativeBannerAdContainer = findViewById(R.id.native_banner_ad_container);
        LayoutInflater inflater = LayoutInflater.from(LoginActivity.this);
        // Inflate the Ad view.  The layout referenced is the one you created in the last step.
        adView = (LinearLayout) inflater.inflate(R.layout.nantivebannerad, nativeBannerAdContainer, false);
        nativeBannerAdContainer.addView(adView);

        // Add the AdChoices icon
        RelativeLayout adChoicesContainer = adView.findViewById(R.id.ad_choices_container);
        AdChoicesView adChoicesView = new AdChoicesView(LoginActivity.this, nativeBannerAd, true);
        adChoicesContainer.addView(adChoicesView, 0);

        // Create native UI using the ad metadata.
        TextView nativeAdTitle = adView.findViewById(R.id.native_ad_title);
        TextView nativeAdSocialContext = adView.findViewById(R.id.native_ad_social_context);
        TextView sponsoredLabel = adView.findViewById(R.id.native_ad_sponsored_label);
        AdIconView nativeAdIconView = adView.findViewById(R.id.native_icon_view);
        Button nativeAdCallToAction = adView.findViewById(R.id.native_ad_call_to_action);

        // Set the Text.
        nativeAdCallToAction.setText(nativeBannerAd.getAdCallToAction());
        nativeAdCallToAction.setVisibility(
                nativeBannerAd.hasCallToAction() ? View.VISIBLE : View.INVISIBLE);
        nativeAdTitle.setText(nativeBannerAd.getAdvertiserName());
        nativeAdSocialContext.setText(nativeBannerAd.getAdSocialContext());
        sponsoredLabel.setText(nativeBannerAd.getSponsoredTranslation());

        // Register the Title and CTA button to listen for clicks.
        List<View> clickableViews = new ArrayList<>();
        clickableViews.add(nativeAdTitle);
        clickableViews.add(nativeAdCallToAction);
        nativeBannerAd.registerViewForInteraction(adView, nativeAdIconView, clickableViews);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable editable) {
        if (editable == userNameEt.getEditableText()) {
            if (!userNameEt.getText().toString().isEmpty() && !passWordEt.getText().toString().isEmpty()) {
                SignUpActivity.setLoginButton_state(LoginActivity.this, true, loginBtn);
                loginBtn.setEnabled(true);
            } else {
                SignUpActivity.setLoginButton_state(LoginActivity.this, false, loginBtn);
                loginBtn.setEnabled(false);
            }
        } else {
            if (!userNameEt.getText().toString().isEmpty() && !passWordEt.getText().toString().isEmpty()) {
                SignUpActivity.setLoginButton_state(LoginActivity.this, true, loginBtn);
                loginBtn.setEnabled(true);
            } else {
                SignUpActivity.setLoginButton_state(LoginActivity.this, false, loginBtn);
                loginBtn.setEnabled(false);
            }
        }

    }


}
