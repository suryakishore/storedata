package com.storedata.com.createnote;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.storedata.com.AppController;
import com.storedata.com.R;
import com.storedata.com.SessionManager;
import com.storedata.com.notepad.NotepadItemPojo;
import com.storedata.com.sqlitedatabase.DatabaseHelper;
import com.storedata.com.utility.Utility;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Calendar;

public class CreateNoteActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView date;
    private Calendar c;
    private int year, month, day;
    private RelativeLayout save, delete, option_menu;
    private EditText notepadTitle, notepadData;
    private CouchDbController couchDbController;
    //  public static String DOC_ID = "document_id", DOC_TYPE = "document_type";
    public static String DOC_TITLE = "document_title", DOC_DATA = "document_data", DOC_DATE = "document_date", DOC_TYPE = "document_type", FIRE_BASE_INDEX = "fire_base_index";

    private String noteData = "", title = "", time, browserData;
    private int docType;
    private AlertDialog dialog_parent = null;
    private RewardedVideoAd mRewardedVideoAd;
    private DatabaseHelper databaseHelper;
    private SessionManager sessionManager;
    private String userName;
    private ArrayList<String> arrayList;
    private DatabaseReference mDatabase;
    private Activity mActivity;
    private String pushKey;
    private String fireBaseIndex;
    private int noteType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_note);
        showRewardedVideoAd();
        initilizedata();
    }


    private void initilizedata() {
        mActivity = CreateNoteActivity.this;
        //  arrayList = new ArrayList<>();
        sessionManager = new SessionManager(this);
        userName = sessionManager.getUSER_NAME();
        pushKey = sessionManager.getPUSH_KEY();
        date = findViewById(R.id.date);
        notepadTitle = findViewById(R.id.notepadTitle);
        notepadData = findViewById(R.id.notepadData);
        option_menu = findViewById(R.id.option_menu);
        save = findViewById(R.id.save);
        delete = findViewById(R.id.delete);
        option_menu.setOnClickListener(this);
        save.setOnClickListener(this);
        delete.setOnClickListener(this);
        databaseHelper = new DatabaseHelper(this);
        couchDbController = AppController.getInstance().getCounchDatabase();
        c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);
        mDatabase = FirebaseDatabase.getInstance().getReference("SignUpWithUsernameAndEmail");

        getIntentData();
    }

    private void getIntentData() {
        Bundle bundle = getIntent().getExtras();

        if (bundle != null) {
            title = bundle.getString("title");
            arrayList = (ArrayList<String>) bundle.getSerializable("noteData");
            if (arrayList != null)
                noteData = arrayList.get(0);
            else
                arrayList = new ArrayList<>();
            time = bundle.getString("time");
            noteType = bundle.getInt("noteType");
            fireBaseIndex = bundle.getString("fireBaseIndex");
            Log.d("exe", "fireBaseIndex" + fireBaseIndex);
            notepadTitle.setText(title);
            date.setText(time);
            if (noteData == null || noteData.isEmpty()) {
                browserData = bundle.getString("browserData");
                notepadData.setText(browserData);
            } else
                notepadData.setText(noteData);
            if (time == null || time.isEmpty()) {
                date.setText(new StringBuilder().append(day).append("-").append(month + 1).append("-").append(year));
            }
        } else {
            date.setText(new StringBuilder().append(day).append("-").append(month + 1).append("-").append(year));
        }

    }

    @Override
    public void onDestroy() {

        mRewardedVideoAd.destroy(this);

        super.onDestroy();

    }

    @Override
    public void onBackPressed() {

        if (Utility.isNetworkAvailable(mActivity)) {
            //&& (!title.equals(notepadTitle.getText().toString()) || !noteData.equals(notepadData.getText().toString()))
            if (!notepadTitle.getText().toString().isEmpty() && fireBaseIndex != null) {

                //update values

                docType = 1;

                arrayList.clear();
                arrayList.add(notepadData.getText().toString().trim());
                NotepadItemPojo notepadItemPojo = new NotepadItemPojo();
                notepadItemPojo.setNoteType(noteType);
                notepadItemPojo.setDate(date.getText().toString().trim());
                notepadItemPojo.setTitle(notepadTitle.getText().toString().trim());
                notepadItemPojo.setNotedata(arrayList);

                mDatabase.child(pushKey).child("USERDATA").child(fireBaseIndex).setValue(notepadItemPojo);

                Log.d("exe", "docIdupdateDocument" + docType + "fireBaseIndex" + fireBaseIndex);
                Intent intent = new Intent();
                intent.putExtra(DOC_TITLE, notepadTitle.getText().toString().trim());
                intent.putExtra(DOC_TYPE, docType);
                intent.putExtra(DOC_DATE, date.getText().toString().trim());
                intent.putExtra(DOC_DATA, arrayList);
                intent.putExtra(FIRE_BASE_INDEX, fireBaseIndex);

                setResult(RESULT_OK, intent);
                CreateNoteActivity.this.finish();


                //couchDbController.updateDocument(docId, notepadTitle.getText().toString().trim(), date.getText().toString().trim(), arrayList);

            } else if (!notepadTitle.getText().toString().isEmpty() && fireBaseIndex == null) {
                arrayList.clear();
                arrayList.add(notepadData.getText().toString());
                //  String docId = couchDbController.createDocument(notepadTitle.getText().toString().trim(), date.getText().toString().trim(), arrayList);
                docType = 0;
                NotepadItemPojo notepadItemPojo = new NotepadItemPojo();
                notepadItemPojo.setNoteType(noteType);
                notepadItemPojo.setDate(date.getText().toString().trim());
                notepadItemPojo.setTitle(notepadTitle.getText().toString().trim());
                notepadItemPojo.setNotedata(arrayList);

                String userId = mDatabase.push().getKey();
                mDatabase.child(pushKey).child("USERDATA").child(userId).setValue(notepadItemPojo);

                Intent intent = new Intent();
                intent.putExtra(DOC_TITLE, notepadTitle.getText().toString().trim());
                intent.putExtra(DOC_TYPE, docType);
                intent.putExtra(DOC_DATE, date.getText().toString().trim());
                intent.putExtra(DOC_DATA, arrayList);
                intent.putExtra(FIRE_BASE_INDEX, userId);

                setResult(RESULT_OK, intent);

                CreateNoteActivity.this.finish();
                Toast.makeText(this, "created Document", Toast.LENGTH_SHORT).show();
                super.onBackPressed();

            } else {
                // Toast.makeText(this, "document title should not be empty", Toast.LENGTH_SHORT).show();
                addTitle(1);
                // finish();
            }
        } else {
            addTitle(2);
            // Toast.makeText(mActivity, "Please connect to internet", Toast.LENGTH_SHORT).show();
        }
    }


    public void addTitle(int i) {
        AlertDialog.Builder builder = Utility.createDialog(CreateNoteActivity.this);
        final File file = Utility.getFile();
        LayoutInflater inflater = CreateNoteActivity.this.getLayoutInflater();
        @SuppressLint("InflateParams") View dialogView = inflater.inflate(R.layout.delete_alert_dialog, null);
        builder.setView(dialogView);
        ImageView headerIcon = dialogView.findViewById(R.id.headerIcon);
        TextView header_title = dialogView.findViewById(R.id.header_title);
        TextView bodyTitle = dialogView.findViewById(R.id.bodyTitle);

        if (i == 1) {
            headerIcon.setImageResource(R.drawable.ic_empty_title);
            header_title.setText("Empty title");
            bodyTitle.setText("Title shouldn't be empty\nPlease fill up the title for this document.\n\nif you want to save this data please put some title and press ok otherwise press Cancel to ignore this data ");
        } else if (i == 2) {
            headerIcon.setImageResource(R.drawable.ic_internet);
            header_title.setText("No internet");
            bodyTitle.setText("Please connect to internet\nwithout internet we cant process your document.\n\nif you want to save this data please connect to internet and press ok otherwise press Cancel to ignore this data ");
        }
        TextView take_photo = (TextView) dialogView.findViewById(R.id.cancel);
        take_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                if (dialog_parent.isShowing())
                    dialog_parent.dismiss();
            }
        });
        TextView from_gallery = (TextView) dialogView.findViewById(R.id.ok);
        from_gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dialog_parent.isShowing())
                    dialog_parent.dismiss();
            }
        });
        dialog_parent = builder.show();
        dialog_parent.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

        dialog_parent.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog_parent.show();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.save:
                Log.d("exe", "browserData" + browserData);
                /*if (browserData != null && !browserData.isEmpty()) {
                    setBrowserData();
                } else*/
                onBackPressed();
                break;
            case R.id.delete:

                AlertDialog.Builder builder = Utility.createDialog(this);
                LayoutInflater inflater = this.getLayoutInflater();
                @SuppressLint("InflateParams") View dialogView = inflater.inflate(R.layout.delete_alert_dialog, null);
                builder.setView(dialogView);
                TextView bodyTitle=dialogView.findViewById(R.id.bodyTitle);
                bodyTitle.setText("Are you sure you want to delete permanently this entry?");
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

                        if (title==null) {
                            finish();

                        } else {
                            if (!fireBaseIndex.equals("") && !fireBaseIndex.isEmpty()) {
                                mDatabase.child(pushKey).child("USERDATA").child(fireBaseIndex).setValue(null);

                                //couchDbController.deleteDocument(docId);
                                docType = 2;
                                Log.d("exe", "docId delete" + fireBaseIndex + docType);
                                Intent intent = new Intent();
                                // intent.putExtra(DOC_ID, docId);
                                intent.putExtra(DOC_TYPE, docType);
                                setResult(RESULT_OK, intent);
                                CreateNoteActivity.this.finish();
                            }
                        }
                        if (dialog_parent.isShowing())
                            dialog_parent.dismiss();
                    }
                });
                dialog_parent = builder.show();
                dialog_parent.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

                dialog_parent.show();

                break;

            case R.id.option_menu:
                popupMenu();
                break;
            default:
                break;
        }
    }


    private void showRewardedVideoAd() {
        MobileAds.initialize(this, getResources().getString(R.string.appid));

        mRewardedVideoAd = MobileAds.getRewardedVideoAdInstance(this);
        mRewardedVideoAd.setRewardedVideoAdListener(new RewardedVideoAdListener() {

            @Override
            public void onRewarded(RewardItem rewardItem) {
                Toast.makeText(CreateNoteActivity.this, "onRewarded! currency: " + rewardItem.getType() + "  amount: " +
                        rewardItem.getAmount(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onRewardedVideoAdLeftApplication() {
                Toast.makeText(CreateNoteActivity.this, "onRewardedVideoAdLeftApplication",
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onRewardedVideoAdClosed() {
                Toast.makeText(CreateNoteActivity.this, "onRewardedVideoAdClosed", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onRewardedVideoAdFailedToLoad(int errorCode) {
                Toast.makeText(CreateNoteActivity.this, "onRewardedVideoAdFailedToLoad", Toast.LENGTH_SHORT).show();
                Log.d("exe", "errorCode" + errorCode);
            }

            @Override
            public void onRewardedVideoCompleted() {

            }

            @Override
            public void onRewardedVideoAdLoaded() {

                Toast.makeText(CreateNoteActivity.this, "onRewardedVideoAdLoaded", Toast.LENGTH_SHORT).show();
                showRewardedVideo();

            }

            @Override
            public void onRewardedVideoAdOpened() {

                Toast.makeText(CreateNoteActivity.this, "onRewardedVideoAdOpened", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onRewardedVideoStarted() {

                Toast.makeText(CreateNoteActivity.this, "onRewardedVideoStarted", Toast.LENGTH_SHORT).show();

            }

        });

        loadRewardedVideoAd();
    }

    private void loadRewardedVideoAd() {
        mRewardedVideoAd.loadAd(getString(R.string.rewarded), new AdRequest.Builder().build());
        // showing the ad to user

    }

    private void showRewardedVideo() {
        // make sure the ad is loaded completely before showing it
        Log.d("exe", "showRewardedVideo");

        if (mRewardedVideoAd.isLoaded()) {
            Log.d("exe", "show");
            mRewardedVideoAd.show();
        }
    }


    @Override
    public void onResume() {
        mRewardedVideoAd.resume(this);
        super.onResume();
    }


    @Override
    public void onPause() {
        mRewardedVideoAd.pause(this);
        super.onPause();
    }


    private void popupMenu() {

        final PopupMenu popupMenu;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            popupMenu = new PopupMenu(this, option_menu, Gravity.END);
        } else {
            popupMenu = new PopupMenu(this, option_menu);
        }
        popupMenu.getMenuInflater().inflate(R.menu.create_note_menu, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {

                    case R.id.exportNote:

                        if (notepadTitle.getText().toString().length() > 0) {
                            final File file = Utility.getFile();
                            File folder = new File(file, notepadTitle.getText().toString().trim() + ".txt");

                            Log.d("exe", "folder" + file.getPath());

                            try {
                                FileOutputStream f = new FileOutputStream(folder);
                                OutputStreamWriter o = new OutputStreamWriter(f);
                                o.append(notepadData.getText().toString().trim());

                                // Write objects to file
                                o.close();
                                f.close();

                                Toast.makeText(CreateNoteActivity.this, "Saved to" + folder.getPath(), Toast.LENGTH_SHORT).show();

                                onBackPressed();
                            } catch (FileNotFoundException e) {
                                Log.d("exe", "FileNotFoundException" + e.getMessage());
                            } catch (IOException e) {
                                System.out.println("Error initializing stream");
                            }

                        } else {
                            Toast.makeText(CreateNoteActivity.this, "Please enter title to save the data", Toast.LENGTH_SHORT).show();
                        }

                        return true;

                    case R.id.share:

                        Intent share = new Intent(Intent.ACTION_SEND);
                        share.setType("text/plain");
                        share.putExtra(Intent.EXTRA_SUBJECT, "storedata");
                        String checkListData = "";

                            checkListData=notepadData.getText().toString();

                        share.putExtra(Intent.EXTRA_TEXT, checkListData);
                        share.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // this will make such that when user returns to your app, your app is displayed, instead of the email app.
                        startActivity(Intent.createChooser(share, "Share on Social Media"));


                        return  true;


                    default:
                        return false;
                }
            }
        });
        popupMenu.show();
    }

   /* @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d("exe", "data" + data.getData());

    }*/
}
