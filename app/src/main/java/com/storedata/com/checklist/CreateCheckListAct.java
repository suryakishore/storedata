package com.storedata.com.checklist;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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

import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.InterstitialAd;
import com.facebook.ads.InterstitialAdListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.storedata.com.R;
import com.storedata.com.SessionManager;
import com.storedata.com.notepad.NotePadItemClick;
import com.storedata.com.notepad.NotepadItemPojo;
import com.storedata.com.utility.Utility;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Calendar;

public class CreateCheckListAct extends AppCompatActivity implements View.OnClickListener {
    private RecyclerView checklistRv;
    private ArrayList<String> checkList;
    private LinearLayoutManager linearLayoutManager;
    private CheckListAdapter checkListAdapter;
    private TextView date;
    private Calendar c;
    private int year, month, day;
    private ImageView saveTodo;
    private EditText addtoDo, checklistTitle;
    private NotePadItemClick itemClick;
    private AlertDialog dialog_parent = null;
    private String noteData = "", title = "", time;
    private SessionManager sessionManager;
    private String userName;
    private ArrayList<String> arrayList;
    private int docType;
    private RelativeLayout save, delete, option_menu;
    private InterstitialAd interstitialAd;
    private DatabaseReference mDatabase;
    private Activity mActivity;
    private String pushKey;
    private String fireBaseIndex;
    private int noteType;
    public static String DOC_TITLE = "document_title", DOC_DATA = "document_data", DOC_DATE = "document_date", DOC_TYPE = "document_type", FIRE_BASE_INDEX = "fire_base_index";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_check_list);
        showinterstetialAd();
        initializeData();
        getIntentData();

        // setData();
    }

    private void showinterstetialAd() {
        interstitialAd = new InterstitialAd(this, "YOUR_PLACEMENT_ID");
        // Set listeners for the Interstitial Ad
        interstitialAd.setAdListener(new InterstitialAdListener() {
            @Override
            public void onInterstitialDisplayed(Ad ad) {
                // Interstitial ad displayed callback
                Log.e("exe", "Interstitial ad displayed.");
            }

            @Override
            public void onInterstitialDismissed(Ad ad) {
                // Interstitial dismissed callback
                Log.e("exe", "Interstitial ad dismissed.");
            }

            @Override
            public void onError(Ad ad, AdError adError) {
                // Ad error callback
                Log.e("exe", "Interstitial ad failed to load: " + adError.getErrorMessage());
            }

            @Override
            public void onAdLoaded(Ad ad) {
                // Interstitial ad is loaded and ready to be displayed
                Log.d("exe", "Interstitial ad is loaded and ready to be displayed!");
                // Show the ad
                interstitialAd.show();
            }

            @Override
            public void onAdClicked(Ad ad) {
                // Ad clicked callback
                Log.d("exe", "Interstitial ad clicked!");
            }

            @Override
            public void onLoggingImpression(Ad ad) {
                // Ad impression logged callback
                Log.d("exe", "Interstitial ad impression logged!");
            }
        });

        // For auto play video ads, it's recommended to load the ad
        // at least 30 seconds before it is shown
        interstitialAd.loadAd();
    }


    @Override
    public void onBackPressed() {
        if (Utility.isNetworkAvailable(mActivity)) {

            if (!checklistTitle.getText().toString().isEmpty() && fireBaseIndex != null) {


                docType = 1;
/*
                checkList.clear();
                arrayList.add(checkList);*/
                NotepadItemPojo notepadItemPojo = new NotepadItemPojo();
                notepadItemPojo.setNoteType(noteType);
                notepadItemPojo.setDate(date.getText().toString().trim());
                notepadItemPojo.setTitle(checklistTitle.getText().toString().trim());
                notepadItemPojo.setNotedata(checkList);

                mDatabase.child(pushKey).child("USERDATA").child(fireBaseIndex).setValue(notepadItemPojo);

                Log.d("exe", "docIdupdateDocument" + docType + "fireBaseIndex" + fireBaseIndex);
                Intent intent = new Intent();
                intent.putExtra(DOC_TITLE, checklistTitle.getText().toString().trim());
                intent.putExtra(DOC_TYPE, docType);
                intent.putExtra(DOC_DATE, date.getText().toString().trim());
                intent.putExtra(DOC_DATA, checkList);
                intent.putExtra(FIRE_BASE_INDEX, fireBaseIndex);

                setResult(RESULT_OK, intent);
                CreateCheckListAct.this.finish();


            } else if (!checklistTitle.getText().toString().isEmpty() && fireBaseIndex == null) {

                Toast.makeText(this, "created Document", Toast.LENGTH_SHORT).show();


              /*  arrayList.clear();
                arrayList.add(notepadData.getText().toString());*/
                //  String docId = couchDbController.createDocument(notepadTitle.getText().toString().trim(), date.getText().toString().trim(), arrayList);
                docType = 0;
                NotepadItemPojo notepadItemPojo = new NotepadItemPojo();
                notepadItemPojo.setNoteType(noteType);
                notepadItemPojo.setDate(date.getText().toString().trim());
                notepadItemPojo.setTitle(checklistTitle.getText().toString().trim());
                notepadItemPojo.setNotedata(checkList);

                String userId = mDatabase.push().getKey();
                mDatabase.child(pushKey).child("USERDATA").child(userId).setValue(notepadItemPojo);

                Intent intent = new Intent();
                intent.putExtra(DOC_TITLE, checklistTitle.getText().toString().trim());
                intent.putExtra(DOC_TYPE, docType);
                intent.putExtra(DOC_DATE, date.getText().toString().trim());
                intent.putExtra(DOC_DATA, checkList);
                intent.putExtra(FIRE_BASE_INDEX, userId);

                setResult(RESULT_OK, intent);

                CreateCheckListAct.this.finish();
                Toast.makeText(this, "created Document", Toast.LENGTH_SHORT).show();
                super.onBackPressed();

            } else {
                addTitle(1);
                // Toast.makeText(this, "document title should not be empty", Toast.LENGTH_SHORT).show();
                //    finish();
            }

        } else {
            // Toast.makeText(mActivity, "Please connect to internet", Toast.LENGTH_SHORT).show();
            addTitle(2);
        }

        // finish();

    }


    public void addTitle(int i) {
        AlertDialog.Builder builder = Utility.createDialog(CreateCheckListAct.this);
        final File file = Utility.getFile();
        LayoutInflater inflater = CreateCheckListAct.this.getLayoutInflater();
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


    private void initializeData() {
        mActivity = CreateCheckListAct.this;
        sessionManager = new SessionManager(mActivity);
        pushKey = sessionManager.getPUSH_KEY();
        checkList = new ArrayList<>();
        arrayList = new ArrayList<>();
        checklistRv = findViewById(R.id.checklistRv);
        date = findViewById(R.id.date);
        saveTodo = findViewById(R.id.saveTodo);
        saveTodo.setOnClickListener(this);
        addtoDo = findViewById(R.id.addtoDo);
        checklistTitle = findViewById(R.id.checklistTitle);
        save = findViewById(R.id.save);
        save.setOnClickListener(this);
        delete = findViewById(R.id.delete);
        delete.setOnClickListener(this);
        option_menu = findViewById(R.id.option_menu);
        option_menu.setOnClickListener(this);
        c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);
        handleAdapterClicked();

        linearLayoutManager = new LinearLayoutManager(this);
        checklistRv.setLayoutManager(linearLayoutManager);
        checklistRv.setHasFixedSize(true);
        checkListAdapter = new CheckListAdapter(checkList, itemClick);
        checklistRv.setAdapter(checkListAdapter);
        Log.d("exe", "year" + year + "month" + month + "day" + day);
        mDatabase = FirebaseDatabase.getInstance().getReference("SignUpWithUsernameAndEmail");
    }

    private void getIntentData() {

        Bundle bundle = getIntent().getExtras();

        if (bundle != null) {

            title = bundle.getString("title");
            arrayList = (ArrayList<String>) bundle.getSerializable("noteData");
            time = bundle.getString("time");
            if (arrayList == null)
                arrayList = new ArrayList<>();
            noteType = bundle.getInt("noteType");
            fireBaseIndex = bundle.getString("fireBaseIndex");
            Log.d("exe", "fireBaseIndex" + fireBaseIndex);
            checklistTitle.setText(title);
            date.setText(time);
            checkList.addAll(arrayList);
            checkListAdapter.notifyDataSetChanged();
            addtoDo.requestFocus();
        }

        if (time == null || time.isEmpty())
            date.setText(new StringBuilder().append(day).append("-").append(month + 1).append("-").append(year));
    }

    @Override
    public void onDestroy() {
        if (interstitialAd != null) {
            interstitialAd.destroy();
        }
        super.onDestroy();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.saveTodo:
                if (addtoDo.getText().toString().length() > 0) {

                    checkList.add(0, addtoDo.getText().toString().trim());
                    checkListAdapter.notifyDataSetChanged();
                    addtoDo.setText("");
                }
                break;

            case R.id.save:
                onBackPressed();
                break;
            case R.id.delete:

                AlertDialog.Builder builder = Utility.createDialog(this);
                LayoutInflater inflater = this.getLayoutInflater();
                @SuppressLint("InflateParams") View dialogView = inflater.inflate(R.layout.delete_alert_dialog, null);
                builder.setView(dialogView);
                TextView bodyTitle = dialogView.findViewById(R.id.bodyTitle);
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
                                intent.putExtra(DOC_TYPE, docType);
                                setResult(RESULT_OK, intent);
                                CreateCheckListAct.this.finish();
                            }
                        }
                        if (dialog_parent.isShowing())
                            dialog_parent.dismiss();
                    }
                });
                dialog_parent = builder.show();
                dialog_parent.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
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

    private void handleAdapterClicked() {

        if (itemClick == null) {

            itemClick = new NotePadItemClick() {
                @Override
                public void itemClick(TextView view, final int position, int id) {
                    AlertDialog.Builder builder = Utility.createDialog(CreateCheckListAct.this);
                    LayoutInflater inflater = CreateCheckListAct.this.getLayoutInflater();
                    @SuppressLint("InflateParams") View dialogView = inflater.inflate(R.layout.delete_alert_dialog, null);
                    builder.setView(dialogView);

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
                            checkList.remove(position);
                            checkListAdapter.notifyDataSetChanged();
                            if (dialog_parent.isShowing())
                                dialog_parent.dismiss();
                        }
                    });
                    dialog_parent = builder.show();
                    dialog_parent.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

                    dialog_parent.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                    dialog_parent.show();


                }
            };
        }
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

                        if (checklistTitle.getText().toString().length() > 0) {
                            final File file = Utility.getFile();
                            File folder = new File(file, checklistTitle.getText().toString().trim() + ".txt");

                            Log.d("exe", "folder" + file.getPath());

                            try {
                                FileOutputStream f = new FileOutputStream(folder);
                                OutputStreamWriter o = new OutputStreamWriter(f);
                                String checkListData = "";
                                for (int j = 0; j < checkList.size(); j++) {
                                    checkListData += checkList.get(j) + "\n";
                                }

                                o.append(checkListData);

                                // Write objects to file

                                o.close();
                                f.close();

                                Toast.makeText(CreateCheckListAct.this, "Saved to" + folder.getPath(), Toast.LENGTH_SHORT).show();

                                onBackPressed();
                            } catch (FileNotFoundException e) {
                                Log.d("exe", "FileNotFoundException" + e.getMessage());
                            } catch (IOException e) {
                                System.out.println("Error initializing stream");
                            }

                        } else {
                            Toast.makeText(CreateCheckListAct.this, "Please enter title to save the data", Toast.LENGTH_SHORT).show();
                        }

                        return true;

                    case R.id.share:

                        Intent share = new Intent(Intent.ACTION_SEND);
                        share.setType("text/plain");
                        share.putExtra(Intent.EXTRA_SUBJECT, "storedata");
                        String checkListData = "";
                        for (int i = 0; i < checkList.size(); i++) {
                            checkListData += checkList.get(i) + "\n";
                        }

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


}
