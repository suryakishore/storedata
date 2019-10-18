package com.storedata.com.signup;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.facebook.ads.AdSize;
import com.facebook.ads.AdView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.storedata.com.R;
import com.storedata.com.SessionManager;
import com.storedata.com.notepad.NotePadListActivity;
import com.storedata.com.sqlitedatabase.DatabaseHelper;
import com.storedata.com.sqlitedatabase.User;
import com.storedata.com.utility.Utility;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {
    private RelativeLayout back_button;
    private Button signInBtn;
    private DatabaseHelper databaseHelper;
    private ProgressBar validating_name_progressbar, validating_email_progressbar;
    private EditText user_name, email, password;
    private ImageView name_already_exist_aleret, email_already_exist_aleret, email_tick_mark_icon, username_tickmark;
    private boolean isUserNameExist, isEmailExist, isPasswordValid;
    private SessionManager sessionManager;
    private AdView bannerAdd;
    private DatabaseReference mDatabase;
    private Activity mActivity;
    private String browserData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        initializeData();

    }

    private void initializeData() {
        mActivity = SignUpActivity.this;

        back_button = (RelativeLayout) findViewById(R.id.back_button);
        signInBtn = (Button) findViewById(R.id.signInBtn);
        validating_name_progressbar = (ProgressBar) findViewById(R.id.validating_name_progressbar);
        validating_email_progressbar = (ProgressBar) findViewById(R.id.validating_email_progressbar);
        user_name = (EditText) findViewById(R.id.user_name);
        email = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);
        name_already_exist_aleret = (ImageView) findViewById(R.id.name_already_exist_aleret);
        email_already_exist_aleret = (ImageView) findViewById(R.id.email_already_exist_aleret);
        email_tick_mark_icon = (ImageView) findViewById(R.id.email_tick_mark_icon);
        username_tickmark = (ImageView) findViewById(R.id.username_tickmark);
        signInBtn.setOnClickListener(this);
        back_button.setOnClickListener(this);
        email_already_exist_aleret.setOnClickListener(this);
        name_already_exist_aleret.setOnClickListener(this);
        user_name.addTextChangedListener(new USerNameTExtWatcher());
        email.addTextChangedListener(new EmialTextWatcher());
        password.addTextChangedListener(new PasswordTextWatcher());
        databaseHelper = new DatabaseHelper(this);
        sessionManager = new SessionManager(this);
        mDatabase = FirebaseDatabase.getInstance().getReference("SignUpWithUsernameAndEmail");

        initilizeadd();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            browserData = bundle.getString("browserData");
        }

    }

    private void initilizeadd() {
        bannerAdd = new AdView(this, "YOUR_PLACEMENT_ID", AdSize.BANNER_HEIGHT_90);

        // Find the Ad Container
        LinearLayout adContainer = (LinearLayout) findViewById(R.id.banner_container);

        // Add the ad view to your activity layout
        adContainer.addView(bannerAdd);

        // Request an ad
        bannerAdd.loadAd();

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.back_button:
                onBackPressed();
                break;

            case R.id.signInBtn:
                createUser();
                break;

            case R.id.email_already_exist_aleret:
                Toast.makeText(this, "email already exist,please try with another mail id", Toast.LENGTH_SHORT).show();
                break;

            case R.id.name_already_exist_aleret:
                Toast.makeText(this, "username already exist,please try with another user name", Toast.LENGTH_SHORT).show();
                break;

        }

    }

    private void createUser() {
        /*User user = new User();
        user.setName(user_name.getText().toString());
        user.setEmail(email.getText().toString());
        user.setPassword(password.getText().toString());
        databaseHelper.addUser(user);*/

        if (Utility.isNetworkAvailable(mActivity)) {

            String userId = mDatabase.push().getKey();
            User user = new User();
            user.setName(user_name.getText().toString().trim());
            user.setPassword(password.getText().toString().trim());
            user.setEmail(email.getText().toString().trim());
            mDatabase.child(userId).setValue(user);
            Intent notePadIntent = new Intent(this, NotePadListActivity.class);
            notePadIntent.putExtra("browserData", browserData);
            sessionManager.setUserLoginStatus(true);
            sessionManager.setUSER_NAME(user_name.getText().toString().trim());
            sessionManager.setPUSH_KEY(userId);
            sessionManager.setUserEmail(email.getText().toString().trim());
            startActivity(notePadIntent);
            finish();
            Toast.makeText(this, "congrats,You successfully registered the user", Toast.LENGTH_SHORT).show();

        } else {
            Toast.makeText(mActivity, "Please connect to internet", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    public static void setLoginButton_state(Context context, boolean isActive, Button login_bt) {

        if (isActive) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                login_bt.setBackground(ContextCompat.getDrawable(context, R.drawable.button_login_active_bag));
            } else {
                login_bt.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.button_login_active_bag));
            }
            login_bt.setTextColor(ContextCompat.getColor(context, R.color.colorWhite));

        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                login_bt.setBackground(ContextCompat.getDrawable(context, R.drawable.button_login_in_active_bag));
            } else {
                login_bt.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.button_login_in_active_bag));
            }
            login_bt.setTextColor(ContextCompat.getColor(context, R.color.colorBlack));
        }
    }

    private class EmialTextWatcher implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            if (Utility.isEmailValid(editable.toString().trim())) {
                isEmailExist = databaseHelper.checkUserEmail(editable.toString().trim());
                if (!isEmailExist) {
                    email_tick_mark_icon.setVisibility(View.VISIBLE);
                    email_already_exist_aleret.setVisibility(View.GONE);

                } else {
                    email_tick_mark_icon.setVisibility(View.GONE);

                    email_already_exist_aleret.setVisibility(View.VISIBLE);
                }
            } else {
                email_tick_mark_icon.setVisibility(View.GONE);
            }
            Log.d("exe", "isEmailExist" + isEmailExist);


            if (!isEmailExist && Utility.isEmailValid(editable.toString().trim()) && !user_name.getText().toString().isEmpty() && !password.getText().toString().isEmpty()) {
                signInBtn.setEnabled(true);
                setLoginButton_state(SignUpActivity.this, true, signInBtn);
            } else {
                signInBtn.setEnabled(false);
                setLoginButton_state(SignUpActivity.this, false, signInBtn);
            }

        }
    }

    private class USerNameTExtWatcher implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(final Editable editable) {


            if (Utility.isNetworkAvailable(mActivity)) {

                mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot childDataSnapshot : dataSnapshot.getChildren()) {
                            User user = childDataSnapshot.getValue(User.class);

                            if (user != null) {
                                String userName = user.getName();
                                Log.v("exe", "userName" + userName);
                                if (userName.equals(editable.toString().trim())) {
                                    isUserNameExist = true;
                                    break;
                                } else {
                                    isUserNameExist = false;
                                }
                            }

                        }

                        Log.v("exe", "DataSnapshot" + dataSnapshot.getChildrenCount() + "isUserNameExist" + isUserNameExist);


                        //  isUserNameExist = databaseHelper.checkUser(editable.toString().trim());
                        Log.d("exe", "isUserNameExist" + isUserNameExist);
                        if (!isUserNameExist) {
                            username_tickmark.setVisibility(View.VISIBLE);
                            name_already_exist_aleret.setVisibility(View.GONE);
                        } else {
                            name_already_exist_aleret.setVisibility(View.VISIBLE);
                            username_tickmark.setVisibility(View.GONE);
                        }
                        if (!isUserNameExist && !email.getText().toString().isEmpty() && !password.getText().toString().isEmpty()) {
                            signInBtn.setEnabled(true);
                            setLoginButton_state(SignUpActivity.this, true, signInBtn);
                        } else {
                            signInBtn.setEnabled(false);
                            setLoginButton_state(SignUpActivity.this, false, signInBtn);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            } else {
                Toast.makeText(mActivity, "Please connect to internet", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private class PasswordTextWatcher implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            isPasswordValid = !editable.toString().isEmpty();
            if (isPasswordValid && !isEmailExist && !isUserNameExist && Utility.isEmailValid(email.getText().toString()) && !user_name.getText().toString().isEmpty()) {
                signInBtn.setEnabled(true);
                setLoginButton_state(SignUpActivity.this, true, signInBtn);
            } else {
                signInBtn.setEnabled(false);
                setLoginButton_state(SignUpActivity.this, false, signInBtn);
            }
        }
    }


    @Override
    protected void onDestroy() {
        if (bannerAdd != null) {
            bannerAdd.destroy();
        }
        super.onDestroy();
    }
}
