package com.storedata.com.settings;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.ads.Ad;
import com.facebook.ads.AdChoicesView;
import com.facebook.ads.AdError;
import com.facebook.ads.AdIconView;
import com.facebook.ads.MediaView;
import com.facebook.ads.NativeAd;
import com.facebook.ads.NativeAdListener;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.ShortDynamicLink;
import com.storedata.com.R;
import com.storedata.com.SessionManager;
import com.storedata.com.login.LoginActivity;
import com.storedata.com.sqlitedatabase.User;
import com.storedata.com.utility.Utility;

import java.util.ArrayList;
import java.util.List;


public class SettingsActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView logout, reportIssue, referYourFriend, editProfile, appVersion;
    private SessionManager sessionManager;
    private InterstitialAd interstitial;
    private RelativeLayout back_button;
    private Button refresh;
    private CheckBox startVideoAdsMuted;
    private TextView videoStatus;
    private LinearLayout nativeAdContainer;
    private LinearLayout adView;
    private NativeAd nativeAd;
    private AlertDialog dialog_parent = null;
    private Activity mActivity;
    private TextView userName;
    private EditText userEmail, newPassword, confirmPass;
    private DatabaseReference mDatabase;

    //  private static final String ADMOB_AD_UNIT_ID = "ca-app-pub-3940256099942544/2247696110";
    //  private static final String ADMOB_APP_ID = "ca-app-pub-3940256099942544~3347511713";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        showIntrestetialAd();
        initializedata();
    }


    private void initializedata() {
        mActivity = SettingsActivity.this;

        sessionManager = new SessionManager(this);
        logout = findViewById(R.id.logout);
        reportIssue = findViewById(R.id.reportIssue);
        back_button = findViewById(R.id.back_button);
        referYourFriend = findViewById(R.id.referYourFriend);
        editProfile = findViewById(R.id.editProfile);
        appVersion = findViewById(R.id.appVersion);
        back_button.setOnClickListener(this);
        logout.setOnClickListener(this);
        reportIssue.setOnClickListener(this);
        referYourFriend.setOnClickListener(this);
        editProfile.setOnClickListener(this);
        mDatabase = FirebaseDatabase.getInstance().getReference("SignUpWithUsernameAndEmail");

        try {
            String versionName = getResources().getString(R.string.version) + " " + mActivity.getPackageManager().getPackageInfo(mActivity.getPackageName(), 0).versionName;
            if (!versionName.isEmpty())
                appVersion.setText(versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        loadNativeAd();

    }

    private void showIntrestetialAd() {

        MobileAds.initialize(this, getString(R.string.appid));
        AdRequest adIRequest = new AdRequest.Builder().build();

        // Prepare the Interstitial Ad Activity
        interstitial = new InterstitialAd(SettingsActivity.this);

        // Insert the Ad Unit ID
        interstitial.setAdUnitId(getString(R.string.Interstitial));

        // Interstitial Ad load Request
        interstitial.loadAd(adIRequest);

        // Prepare an Interstitial Ad Listener
        interstitial.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                // Call displayInterstitial() function when the Ad loads
                displayInterstitial();
            }

            @Override
            public void onAdFailedToLoad(int i) {
                super.onAdFailedToLoad(i);
                Log.d("exe", "onAdFailedToLoad" + i);
            }

        });

    }

    private void displayInterstitial() {
        // If Interstitial Ads are loaded then show else show nothing.
        if (interstitial.isLoaded()) {
            interstitial.show();
        }
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.logout:

                AlertDialog.Builder builder = Utility.createDialog(this);
                LayoutInflater inflater = this.getLayoutInflater();
                @SuppressLint("InflateParams") View dialogView = inflater.inflate(R.layout.delete_alert_dialog, null);
                builder.setView(dialogView);
                ImageView headerIcon = dialogView.findViewById(R.id.headerIcon);
                headerIcon.setImageResource(R.drawable.ic_exit);
                TextView header_title = dialogView.findViewById(R.id.header_title);
                header_title.setText("LOGOUT");
                TextView bodyTitle = dialogView.findViewById(R.id.bodyTitle);
                bodyTitle.setText("Are you sure want to be logged out?");
                TextView take_photo = (TextView) dialogView.findViewById(R.id.cancel);
                take_photo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (dialog_parent.isShowing())
                            dialog_parent.dismiss();
                    }
                });

                TextView from_gallery = (TextView) dialogView.findViewById(R.id.ok);
                from_gallery.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sessionManager.setUserLoginStatus(false);
                        sessionManager.setUSER_NAME(null);
                        sessionManager.setPUSH_KEY(null);
                        sessionManager.setUserEmail(null);
                        Intent LoginIntent = new Intent(SettingsActivity.this, LoginActivity.class);
                        LoginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(LoginIntent);
                        if (dialog_parent.isShowing())
                            dialog_parent.dismiss();
                    }
                });
                dialog_parent = builder.show();
                dialog_parent.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

                dialog_parent.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

                dialog_parent.show();

                break;

            case R.id.reportIssue:
                sendEmail("vallemkishore@gmail.com", "problem on storedata");
                break;

            case R.id.back_button:
                finish();
                break;
           /* case R.id.btn_refresh:
                //refreshAd();
                break;*/

            case R.id.referYourFriend:
                Task<ShortDynamicLink> shortLinkTask;
                shortLinkTask = FirebaseDynamicLinks.getInstance().createDynamicLink()
                        .setLink(Uri.parse("https://www.storedata.com"))
                        .setDynamicLinkDomain("storedata.page.link")
                        // Set parameters

                        .setAndroidParameters(new DynamicLink.AndroidParameters.Builder("com.storedata.com")
                                .setMinimumVersion(125)
                                .build())
                        //  .setIosParameters(new DynamicLink.IosParameters.Builder("com.jsonoja.oja").build())
                        .setSocialMetaTagParameters(new DynamicLink.SocialMetaTagParameters.Builder()
                                .setTitle("storedata")
                                .setDescription("storedata is an simple data storage to store the data from wherever you want like web browser,app etc...")
                                .setImageUrl(Uri.parse("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSgrtNXAnE5DCMrwhXrLQF7RWelGTcCqeZdcW2-qW199kW9xIMd"))
                                .build())
                        .buildShortDynamicLink()
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d("exe", "Exception" + e.getMessage() + e.getLocalizedMessage() + e.getCause());
                            }
                        })
                        .addOnCompleteListener((Activity) mActivity, new OnCompleteListener<ShortDynamicLink>() {
                            @Override
                            public void onComplete(@NonNull Task<ShortDynamicLink> task) {
                                if (task.isSuccessful()) {
                                    // Short link created
                                    Uri shortLink = task.getResult().getShortLink();
                                    Log.d("exe", "dynamicLinkUri" + shortLink);
                                    // shareOnFacebook(shortLink,post_url, productImage, productName, description);
                                    Intent intent = new Intent();
                                    intent.setAction(Intent.ACTION_SEND);
                                    intent.putExtra(Intent.EXTRA_SUBJECT, mActivity.getResources().getString(R.string.app_name));
                                    intent.putExtra(Intent.EXTRA_TEXT, shortLink.toString());
                                    intent.setType("text/plain");
                                    startActivity(intent);
                                } else {
                                    Log.d("exe", "task" + task.getException());
                                    Toast.makeText(mActivity, "Sharing failed, Try again", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                break;

            case R.id.editProfile:

                AlertDialog.Builder builderEdit = Utility.createDialog(this);
                LayoutInflater inflaterEdit = this.getLayoutInflater();
                @SuppressLint("InflateParams") View dialogViewEdit = inflaterEdit.inflate(R.layout.edit_password, null);
                builderEdit.setView(dialogViewEdit);

                final TextView userName = dialogViewEdit.findViewById(R.id.userName);
                userName.setText(sessionManager.getUSER_NAME());
                final EditText userEmail = dialogViewEdit.findViewById(R.id.userEmail);
                userEmail.setText(sessionManager.getUserEmail());
                final EditText newPassword = dialogViewEdit.findViewById(R.id.newPassword);
                final EditText confirmPass = dialogViewEdit.findViewById(R.id.confirmPass);


                TextView cancel = (TextView) dialogViewEdit.findViewById(R.id.cancel);
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (dialog_parent.isShowing())
                            dialog_parent.dismiss();
                    }
                });

                TextView ok = (TextView) dialogViewEdit.findViewById(R.id.ok);
                ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (Utility.isNetworkAvailable(mActivity)) {
                            if (userEmail.getText().toString().length() > 0) {
                                if (Utility.isEmailValid(userEmail.getText().toString().trim()) && !(newPassword.getText().toString().isEmpty() || confirmPass.getText().toString().isEmpty())) {
                                    if (newPassword.getText().toString().length() > 0 && confirmPass.getText().toString().length() > 0) {
                                        if (newPassword.getText().toString().equals(confirmPass.getText().toString())) {

                                            User user = new User();
                                            user.setName(sessionManager.getUSER_NAME());
                                            user.setPassword(newPassword.getText().toString().trim());
                                            user.setEmail(userEmail.getText().toString().trim());
                                            mDatabase.child(sessionManager.getPUSH_KEY()).setValue(user);
                                            sessionManager.setUserEmail(userEmail.getText().toString().trim());
                                            if (dialog_parent.isShowing())
                                                dialog_parent.dismiss();

                                        } else {
                                            openAlertDialog("Password missmatch", "The entered passwords are not equal");
                                        }

                                    } else {
                                        openAlertDialog("Enter password", "Please enter password");

                                    }

                                } else if (Utility.isEmailValid(userEmail.getText().toString().trim()) &&(newPassword.getText().toString().isEmpty() || confirmPass.getText().toString().isEmpty())) {

                                    mDatabase.child(sessionManager.getPUSH_KEY()).child("email").setValue(userEmail.getText().toString().trim());
                                    sessionManager.setUserEmail(userEmail.getText().toString().trim());
                                    if (dialog_parent.isShowing())
                                        dialog_parent.dismiss();
                                } else {
                                    openAlertDialog("Valid EmailId", "Please enter valid EmailId");
                                }
                            }
                            else {
                                openAlertDialog("Valid EmailId", "Please enter valid EmailId");
                            }

                        } else {
                            openAlertDialog(getResources().getString(R.string.noInternetConnection), getResources().getString(R.string.pleaseConnectInternet));
                        }


                    }
                });
                dialog_parent = builderEdit.show();
                dialog_parent.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
                dialog_parent.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                dialog_parent.show();


                break;


            default:
                break;

        }
    }


    public void openAlertDialog(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.Aleret_dialog_theme);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setNegativeButton("close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
       /* builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (Utility.isNetworkAvailable(mActivity)) {
                    initializeDocsData();
                } else
                    checkNetworkConnection();
            }
        });*/
        final AlertDialog alertDialog = builder.create();
        alertDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        alertDialog.show();
    }




   /* private void refreshAd() {

        Log.d("exe", "refreshAd0");
        MobileAds.initialize(this, ADMOB_APP_ID);
        refresh.setEnabled(false);

        VideoOptions videoOptions = new VideoOptions.Builder()
                .setStartMuted(false)
                .build();

        NativeAdOptions adOptions = new NativeAdOptions.Builder()
                .setVideoOptions(videoOptions)
                .build();
        final AdLoader adLoader = new AdLoader.Builder(this, ADMOB_AD_UNIT_ID)
                .forAppInstallAd(new NativeAppInstallAd.OnAppInstallAdLoadedListener() {
                    @Override
                    public void onAppInstallAdLoaded(NativeAppInstallAd ad) {

                        // some code that displays the app install ad.

                       *//* if (adLoader.isLoading()) {
                            // The AdLoader is still loading ads.
                            // Expect more adLoaded or onAdFailedToLoad callbacks.
                        } else {
                            // The AdLoader has finished loading ads.
                        }*//*
                    }
                })


                .forContentAd(new NativeContentAd.OnContentAdLoadedListener() {
                    @Override
                    public void onContentAdLoaded(NativeContentAd nativeContentAd) {

                        FrameLayout frameLayout =
                                findViewById(R.id.fl_adplaceholder);

                        NativeContentAdView adView = (NativeContentAdView) getLayoutInflater()
                                .inflate(R.layout.native_advanced, null);

                        populateUnifiedNativeAdView(nativeContentAd, adView);
                        frameLayout.removeAllViews();
                        frameLayout.addView(adView);
                    }
                }).withAdListener(new AdListener() {
                    @Override
                    public void onAdFailedToLoad(int errorCode) {
                        // Handle the failure by logging, altering the UI, and so on.

                        Log.d("exe", "errorCode" + errorCode);
                    }
                })

                .withNativeAdOptions(adOptions)
                .build();
        adLoader.loadAds(new AdRequest.Builder().build(), 10);


        videoStatus.setText("");


    }

    private void populateUnifiedNativeAdView(NativeContentAd nativeAd, NativeContentAdView adView) {
        // Get the video controller for the ad. One will always be provided, even if the ad doesn't
        // have a video asset.
        VideoController vc = nativeAd.getVideoController();

        // Create a new VideoLifecycleCallbacks object and pass it to the VideoController. The
        // VideoController will call methods on this object when events occur in the video
        // lifecycle.
        vc.setVideoLifecycleCallbacks(new VideoController.VideoLifecycleCallbacks() {
            public void onVideoEnd() {
                // Publishers should allow native ads to complete video playback before refreshing
                // or replacing them with another ad in the same UI location.
                refresh.setEnabled(true);
                videoStatus.setText("Video status: Video playback has ended.");
                super.onVideoEnd();
            }
        });

        MediaView mediaView = adView.findViewById(R.id.ad_media);
        ImageView mainImageView = adView.findViewById(R.id.ad_image);

        // Apps can check the VideoController's hasVideoContent property to determine if the
        // NativeAppInstallAd has a video asset.
        if (vc.hasVideoContent()) {
            adView.setMediaView(mediaView);
            mainImageView.setVisibility(View.GONE);
            videoStatus.setText(String.format(Locale.getDefault(),
                    "Video status: Ad contains a %.2f:1 video asset.",
                    vc.getAspectRatio()));
        } else {
            adView.setImageView(mainImageView);
            mediaView.setVisibility(View.GONE);

            // At least one image is guaranteed.
            List<NativeAd.Image> images = nativeAd.getImages();
            mainImageView.setImageDrawable(images.get(0).getDrawable());

            refresh.setEnabled(true);
            // videoStatus.setText("Video status: Ad does not contain a video asset.");
        }

        adView.setHeadlineView(adView.findViewById(R.id.ad_headline));
        adView.setBodyView(adView.findViewById(R.id.ad_body));
        adView.setCallToActionView(adView.findViewById(R.id.ad_call_to_action));
        adView.setLogoView(adView.findViewById(R.id.ad_app_icon));
        //adView.setPriceView(adView.findViewById(R.id.ad_price));
        //adView.setStarRatingView(adView.findViewById(R.id.ad_stars));
        //adView.setStoreView(adView.findViewById(R.id.ad_store));
        adView.setAdvertiserView(adView.findViewById(R.id.ad_advertiser));

        // Some assets are guaranteed to be in every UnifiedNativeAd.
        ((TextView) adView.getHeadlineView()).setText(nativeAd.getHeadline());
        ((TextView) adView.getBodyView()).setText(nativeAd.getBody());
        ((Button) adView.getCallToActionView()).setText(nativeAd.getCallToAction());
        if (nativeAd.getLogo() != null)
            ((ImageView) adView.getLogoView()).setImageURI(nativeAd.getLogo().getUri());
        Log.d("exe", "getExtras" + nativeAd.getExtras());


        if (nativeAd.getAdvertiser() == null) {
            adView.getAdvertiserView().setVisibility(View.INVISIBLE);
        } else {
            ((TextView) adView.getAdvertiserView()).setText(nativeAd.getAdvertiser());
            adView.getAdvertiserView().setVisibility(View.VISIBLE);
        }

        adView.setNativeAd(nativeAd);
    }*/

    // Instantiate a NativeAd object.
    // NOTE: the placement ID will eventually identify this as your App, you can ignore it for
    // now, while you are testing and replace it later when you have signed up.
    // While you are using this temporary code you will only get test ads and if you release
    // your code like this to the Google Play your users will not receive ads (you will get a no fill error).
    // Instantiate a NativeAd object.
    // NOTE: the placement ID will eventually identify this as your App, you can ignore it for
    // now, while you are testing and replace it later when you have signed up.
    // While you are using this temporary code you will only get test ads and if you release
    // your code like this to the Google Play your users will not receive ads (you will get a no fill error).
    private void loadNativeAd() {
        // Instantiate a NativeAd object.
        // NOTE: the placement ID will eventually identify this as your App, you can ignore it for
        // now, while you are testing and replace it later when you have signed up.
        // While you are using this temporary code you will only get test ads and if you release
        // your code like this to the Google Play your users will not receive ads (you will get a no fill error).
        nativeAd = new NativeAd(this, "YOUR_PLACEMENT_ID");
        nativeAd.setAdListener(new NativeAdListener() {

            @Override
            public void onMediaDownloaded(Ad ad) {

            }

            @Override
            public void onError(Ad ad, AdError adError) {
                Log.d("exe", "AdError" + adError.getErrorMessage() + "ad" + adError.getErrorCode());

            }

            @Override
            public void onAdLoaded(Ad ad) {
                // Race condition, load() called again before last ad was displayed
                if (nativeAd == null || nativeAd != ad) {
                    return;
                }

                // Inflate Native Ad into Container
                inflateAd(nativeAd);
            }

            @Override
            public void onAdClicked(Ad ad) {

            }

            @Override
            public void onLoggingImpression(Ad ad) {

            }
        });
        // Request an ad
        nativeAd.loadAd();
    }

    private void inflateAd(NativeAd nativeAd) {

        nativeAd.unregisterView();


        // Add the Ad view into the ad container.
        nativeAdContainer = findViewById(R.id.native_ad_container);
        LayoutInflater inflater = LayoutInflater.from(SettingsActivity.this);
        // Inflate the Ad view.  The layout referenced should be the one you created in the last step.
        adView = (LinearLayout) inflater.inflate(R.layout.facebook_native_ad, nativeAdContainer, false);
        nativeAdContainer.addView(adView);

        // Add the AdChoices icon
        LinearLayout adChoicesContainer = findViewById(R.id.ad_choices_container);
        AdChoicesView adChoicesView = new AdChoicesView(SettingsActivity.this, nativeAd, true);
        adChoicesContainer.addView(adChoicesView, 0);

        // Create native UI using the ad metadata.
        AdIconView nativeAdIcon = adView.findViewById(R.id.native_ad_icon);
        TextView nativeAdTitle = adView.findViewById(R.id.native_ad_title);
        MediaView nativeAdMedia = adView.findViewById(R.id.native_ad_media);
        TextView nativeAdSocialContext = adView.findViewById(R.id.native_ad_social_context);
        TextView nativeAdBody = adView.findViewById(R.id.native_ad_body);
        TextView sponsoredLabel = adView.findViewById(R.id.native_ad_sponsored_label);
        Button nativeAdCallToAction = adView.findViewById(R.id.native_ad_call_to_action);

        // Set the Text.
        nativeAdTitle.setText(nativeAd.getAdvertiserName());
        nativeAdBody.setText(nativeAd.getAdBodyText());
        nativeAdSocialContext.setText(nativeAd.getAdSocialContext());
        nativeAdCallToAction.setVisibility(nativeAd.hasCallToAction() ? View.VISIBLE : View.INVISIBLE);
        nativeAdCallToAction.setText(nativeAd.getAdCallToAction());
        sponsoredLabel.setText(nativeAd.getSponsoredTranslation());

        // Create a list of clickable views
        List<View> clickableViews = new ArrayList<>();
        clickableViews.add(nativeAdTitle);
        clickableViews.add(nativeAdCallToAction);

        // Register the Title and CTA button to listen for clicks.
        nativeAd.registerViewForInteraction(
                adView,
                nativeAdMedia,
                nativeAdIcon,
                clickableViews);
    }


    public void sendEmail(String emailId, String emailSubject) {

        Log.i("Send email", "");
        String[] TO = {emailId};
        String[] CC = {""};
        Intent intent = new Intent(Intent.ACTION_VIEW);
        Uri data = Uri.parse("mailto:" + emailId + "?subject=" + emailSubject);
        intent.setData(data);
        startActivity(intent);
    }

}
