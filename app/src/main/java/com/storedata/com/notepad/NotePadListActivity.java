package com.storedata.com.notepad;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.storedata.com.R;
import com.storedata.com.SessionManager;
import com.storedata.com.checklist.CreateCheckListAct;
import com.storedata.com.createnote.CreateNoteActivity;
import com.storedata.com.settings.SettingsActivity;
import com.storedata.com.utility.Utility;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;

public class NotePadListActivity extends AppCompatActivity implements View.OnClickListener, TextWatcher {
    private RecyclerView rvNotePadList;
    private RelativeLayout option_menu, createNotePad, searchrl, search, loading_view;
    private LinearLayoutManager linearLayoutManager;
    private NotepadAdapter notepadAdapter;
    private ArrayList<NotepadItemPojo> notepadItemPojos;
    /*
        private ArrayList<UserDocs> userDocsArrayList;
    */
    private Dialog dialog;
    private TextView createOne, page_title, createCheckList, userNametxt;
    private String userName;
    private NotePadItemClick itemClick;
    private LinearLayout titleLl, dateLl, data_not_found;
    private ImageView title_iv, date_iv;
    private boolean titleOrder, dateOrder;
    private int itemPosition;
    private AlertDialog dialog_parent = null;
    private ImageView cross_button, search_button;
    private EditText search_text;
    private AdView mAdView;
    private SessionManager sessionManager;
    private int noteType;
    private DatabaseReference mDatabase;
    private Activity mActivity;
    private String pushKey;
    private DatabaseReference mBackUpDatabase;
    private SwipeRefreshLayout refresh;
    private String browserData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_pad_list);
        initializeData();
    }

    private void initializeData() {
        mActivity = NotePadListActivity.this;

        sessionManager = new SessionManager(this);
        userName = sessionManager.getUSER_NAME();
        pushKey = sessionManager.getPUSH_KEY();

        mAdView = (AdView) findViewById(R.id.adView);
        showBannerMobAd();
        notepadItemPojos = new ArrayList<>();
        rvNotePadList = findViewById(R.id.rvNotePadList);
        option_menu = findViewById(R.id.option_menu);
        createNotePad = findViewById(R.id.createNotePad);
        searchrl = findViewById(R.id.searchrl);
        search = findViewById(R.id.search);
        page_title = findViewById(R.id.page_title);
        refresh = findViewById(R.id.refresh);
        search.setOnClickListener(this);
        createNotePad.setOnClickListener(this);
        titleLl = findViewById(R.id.titleLl);
        dateLl = findViewById(R.id.dateLl);
        search_text = findViewById(R.id.search_text);
        search_text.requestFocus();
        search_text.addTextChangedListener(this);
        userNametxt = findViewById(R.id.userNametxt);
        userNametxt.setText(userName);
        titleLl.setOnClickListener(this);
        dateLl.setOnClickListener(this);
        cross_button = findViewById(R.id.cross_button);
        cross_button.setOnClickListener(this);
        search_button = findViewById(R.id.search_button);
        title_iv = findViewById(R.id.title_iv);
        date_iv = findViewById(R.id.date_iv);
        loading_view = findViewById(R.id.loading_view);
        data_not_found = findViewById(R.id.data_not_found);

        notepadItemPojos.clear();
        handleAdapterClicked();
        linearLayoutManager = new LinearLayoutManager(this);
        rvNotePadList.setLayoutManager(linearLayoutManager);
        rvNotePadList.setHasFixedSize(true);
        notepadAdapter = new NotepadAdapter(notepadItemPojos, itemClick);
        rvNotePadList.setAdapter(notepadAdapter);
        dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_list);
        createOne = dialog.findViewById(R.id.createOne);
        createOne.setOnClickListener(this);
        createCheckList = dialog.findViewById(R.id.createCheckList);
        createCheckList.setOnClickListener(this);
        option_menu.setOnClickListener(this);

        mDatabase = FirebaseDatabase.getInstance().getReference(userName);
        mBackUpDatabase = FirebaseDatabase.getInstance().getReference("SignUpWithUsernameAndEmail");
        refresh.setColorSchemeColors(getResources().getColor(R.color.colorfour),getResources().getColor(R.color.colorPrimary));
        refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                notepadItemPojos.clear();
                data_not_found.setVisibility(View.GONE);
                initializeDocsData();
                refresh.setRefreshing(false);
            }

        });


        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            browserData = bundle.getString("browserData");
            if (browserData != null) {
                Intent intent = new Intent(this, CreateNoteActivity.class);
                intent.putExtra("browserData", browserData);
                intent.putExtra("noteType", noteType);
                startActivityForResult(intent, 0);
            }
        }

        // InitializeDatabaseData();
        initializeDocsData();

    }

    private void showBannerMobAd() {
        // mAdView.setAdSize(AdSize.SMART_BANNER);
        MobileAds.initialize(this, getResources().getString(R.string.appid));
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdFailedToLoad(int i) {
                super.onAdFailedToLoad(i);
                Log.d("BannerAd", i + "");
            }

            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                Log.d("BannerAd", "loaded");
                mAdView.setVisibility(View.VISIBLE);
            }

        });






    }

    private void handleAdapterClicked() {

        if (itemClick == null) {

            itemClick = new NotePadItemClick() {
                @Override
                public void itemClick(TextView view, int position, int id) {
                    if (id == 0)
                        initPopupMenu(view, position);
                    else if (id == 1) {
                        itemPosition = position;
                        NotepadItemPojo notepadItem = notepadItemPojos.get(position);
                        Log.d("exe", "itemPosition" + itemPosition + "notepadItem.getNoteType()" + notepadItem.getNoteType());

                        if (notepadItem.getNoteType() == 0) {
                            Intent intent = new Intent(NotePadListActivity.this, CreateNoteActivity.class);
                            ArrayList<String> noteData = notepadItem.getNotedata();
                            String title = notepadItem.getTitle();
                            String time = notepadItem.getDate();
                            String fireBaseIndex = notepadItem.getFireBasedocIndex();

                            //notepadItemPojos

                            intent.putExtra("noteData", noteData);
                            intent.putExtra("title", title);
                            intent.putExtra("time", time);
                            intent.putExtra("noteType", 0);
                            intent.putExtra("fireBaseIndex", fireBaseIndex);

                            startActivityForResult(intent, 0);

                        } else {
                            Intent intent = new Intent(NotePadListActivity.this, CreateCheckListAct.class);
                            ArrayList<String> noteData = notepadItem.getNotedata();
                            String title = notepadItem.getTitle();
                            String time = notepadItem.getDate();
                            String fireBaseIndex = notepadItem.getFireBasedocIndex();
                            intent.putExtra("noteData", noteData);
                            intent.putExtra("title", title);
                            intent.putExtra("time", time);
                            intent.putExtra("noteType", 1);
                            intent.putExtra("fireBaseIndex", fireBaseIndex);

                            startActivityForResult(intent, 1);
                        }
                    }
                }
            };
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.createNotePad:
                if (dialog != null)
                    dialog.show();
                break;

            case R.id.createOne:
                noteType = 0;
                Intent intent = new Intent(this, CreateNoteActivity.class);
                intent.putExtra("noteType", noteType);

                startActivityForResult(intent, 0);
                if (dialog.isShowing())
                    dialog.dismiss();
                break;

            case R.id.createCheckList:
                noteType = 1;
                Intent intentcreateCheckList = new Intent(this, CreateCheckListAct.class);
                intentcreateCheckList.putExtra("noteType", noteType);

                startActivityForResult(intentcreateCheckList, 1);
                if (dialog.isShowing())
                    dialog.dismiss();
                break;

            case R.id.titleLl:
                if (notepadItemPojos.size() > 0)
                    setTitleData();
                break;

            case R.id.dateLl:
                if (notepadItemPojos.size() > 0)
                    setDateData();
                break;

            case R.id.option_menu:
                popupMenu();
                break;

            case R.id.search:
                searchrl.setVisibility(View.VISIBLE);
                search.setVisibility(View.GONE);
                page_title.setVisibility(View.GONE);
                search_text.requestFocus();
                Utility.openKeyboard(this, search_button);
                break;

            case R.id.cross_button:
                if (search_text.getText().toString().length() > 0)
                    search_text.setText("");
                else {
                    searchrl.setVisibility(View.GONE);
                    search.setVisibility(View.VISIBLE);
                    page_title.setVisibility(View.VISIBLE);
                    Utility.closeKEybord(this);
                }
                break;

            default:
                break;
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }


    private void initializeDocsData() {
        if (Utility.isNetworkAvailable(mActivity)) {
            loading_view.setVisibility(View.VISIBLE);
            mBackUpDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot childDataSnapshot : dataSnapshot.getChildren()) {
                        //  Log.v("exe", "getKey" + childDataSnapshot.getKey());

                        if (childDataSnapshot.getKey().equals(pushKey)) {
                            DatabaseReference databaseReference = childDataSnapshot.getRef().child("USERDATA");
                            Log.d("exe", "key" + pushKey + "  ");

                            //You can use the single or the value.. depending if you want to keep track
                            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    for (DataSnapshot snap : dataSnapshot.getChildren()) {


                                        NotepadItemPojo notepadItemPojo = snap.getValue(NotepadItemPojo.class);
                                        Log.d("exe", "key" + snap.getKey() + "  " + snap.getChildrenCount() + "notepadItemPojo" + notepadItemPojo);

                                        notepadItemPojo.setFireBasedocIndex(snap.getKey());
                                        notepadItemPojos.add(notepadItemPojo);

                                    }
                                    notepadAdapter.notifyDataSetChanged();
                                    loading_view.setVisibility(View.GONE);
                                    if (notepadItemPojos.size() > 0)
                                        data_not_found.setVisibility(View.GONE);
                                    else
                                        data_not_found.setVisibility(View.VISIBLE);
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });


                        }


                        //  setbackupData(notepadItemPojo);


                    }

                    Log.v("exe", "DataSnapshot" + dataSnapshot.getChildrenCount() + "size" + notepadItemPojos.size());
/*
                    if (dataSnapshot.getChildrenCount() > 0) {
                        Log.v("exe", "removeValue");

                        dataSnapshot.getRef().removeValue();
                    }*/


                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        } else {
            //Toast.makeText(mActivity, "Please connect to internet", Toast.LENGTH_SHORT).show();
            checkNetworkConnection();
        }

    }


    public void checkNetworkConnection() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.Aleret_dialog_theme);
        builder.setTitle(getResources().getString(R.string.noInternetConnection));
        builder.setMessage(getResources().getString(R.string.pleaseConnectInternet));
        builder.setNegativeButton("close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (Utility.isNetworkAvailable(mActivity)) {
                    initializeDocsData();
                } else
                    checkNetworkConnection();
            }
        });
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.setCancelable(false);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0) {
            if (resultCode == Activity.RESULT_OK && data != null) {
                Log.d("exe", "size" + notepadItemPojos.size());
                //  String doc_id = data.getStringExtra(CreateNoteActivity.DOC_ID);
                int docType = data.getIntExtra(CreateNoteActivity.DOC_TYPE, 0);
                if (docType == 0) {
                    String docTitle = data.getStringExtra(CreateNoteActivity.DOC_TITLE);
                    ArrayList<String> docData = data.getStringArrayListExtra(CreateNoteActivity.DOC_DATA);
                    String docDate = data.getStringExtra(CreateNoteActivity.DOC_DATE);
                    String doc_FireBaseIndex = data.getStringExtra(CreateNoteActivity.FIRE_BASE_INDEX);

                    NotepadItemPojo notepadItemPojo = new NotepadItemPojo();
                    notepadItemPojo.setNoteType(0);
                    notepadItemPojo.setDate(docDate);
                    notepadItemPojo.setTitle(docTitle);
                    notepadItemPojo.setNotedata(docData);
                    notepadItemPojo.setFireBasedocIndex(doc_FireBaseIndex);
                    setdocumentDat(notepadItemPojo);
                } else if (docType == 1) {
                    //  updatedocumentDat(doc_id);
                    String docTitle = data.getStringExtra(CreateNoteActivity.DOC_TITLE);
                    ArrayList<String> docData = data.getStringArrayListExtra(CreateNoteActivity.DOC_DATA);
                    String docDate = data.getStringExtra(CreateNoteActivity.DOC_DATE);
                    updatedocumentDat(docTitle, docData, docDate);

                } else if (docType == 2) {
                    deletedocumentDat();
                }
            }
        } else if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK && data != null) {
                int docType = data.getIntExtra(CreateCheckListAct.DOC_TYPE, 0);
                if (docType == 0) {
                    String docTitle = data.getStringExtra(CreateCheckListAct.DOC_TITLE);
                    ArrayList<String> docData = data.getStringArrayListExtra(CreateCheckListAct.DOC_DATA);
                    String docDate = data.getStringExtra(CreateCheckListAct.DOC_DATE);
                    String doc_FireBaseIndex = data.getStringExtra(CreateCheckListAct.FIRE_BASE_INDEX);

                    NotepadItemPojo notepadItemPojo = new NotepadItemPojo();
                    notepadItemPojo.setNoteType(1);
                    notepadItemPojo.setDate(docDate);
                    notepadItemPojo.setTitle(docTitle);
                    notepadItemPojo.setNotedata(docData);
                    notepadItemPojo.setFireBasedocIndex(doc_FireBaseIndex);
                    setdocumentDat(notepadItemPojo);
                } else if (docType == 1) {
                    String docTitle = data.getStringExtra(CreateNoteActivity.DOC_TITLE);
                    ArrayList<String> docData = data.getStringArrayListExtra(CreateNoteActivity.DOC_DATA);
                    String docDate = data.getStringExtra(CreateNoteActivity.DOC_DATE);
                    updatedocumentDat(docTitle, docData, docDate);

                } else if (docType == 2) {
                    deletedocumentDat();
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void setdocumentDat(NotepadItemPojo notepadItemPojo) {

        notepadItemPojos.add(0, notepadItemPojo);
        notepadAdapter.notifyDataSetChanged();

        if (notepadItemPojos.size() > 0) {
            data_not_found.setVisibility(View.GONE);
        } else
            data_not_found.setVisibility(View.VISIBLE);
    }


    private void updatedocumentDat(String docTitle, ArrayList<String> docData, String docDate) {

        NotepadItemPojo notepadItemPojo = notepadItemPojos.get(itemPosition);
        notepadItemPojo.setDate(docDate);
        notepadItemPojo.setTitle(docTitle);
        notepadItemPojo.setNotedata(docData);
        notepadAdapter.notifyItemChanged(itemPosition);
        Log.d("exe", "notetype" + notepadItemPojo.getNoteType());

    }

    private void deletedocumentDat() {

        notepadItemPojos.remove(itemPosition);
        notepadAdapter.notifyDataSetChanged();
        if (notepadItemPojos.size() == 0)
            data_not_found.setVisibility(View.VISIBLE);
        else
            data_not_found.setVisibility(View.GONE);
    }

    private void setTitleData() {
        if (!titleOrder) {
            title_iv.setImageResource(R.drawable.ic_down_arrow);
            //assceding order
            Collections.sort(notepadItemPojos, new Comparator<NotepadItemPojo>() {
                @Override
                public int compare(NotepadItemPojo lhs, NotepadItemPojo rhs) {
                    return lhs.getTitle().compareTo(rhs.getTitle());
                }
            });
            titleOrder = true;
        } else {
            title_iv.setImageResource(R.drawable.ic_up_arrow);
            //descending order
            Collections.sort(notepadItemPojos, new Comparator<NotepadItemPojo>() {
                @Override
                public int compare(NotepadItemPojo lhs, NotepadItemPojo rhs) {
                    return rhs.getTitle().compareTo(lhs.getTitle());
                }
            });
            titleOrder = false;
        }

        notepadAdapter.notifyDataSetChanged();
    }


    private void setDateData() {

        if (!dateOrder) {
            date_iv.setImageResource(R.drawable.ic_down_arrow);
            //assceding order
            Collections.sort(notepadItemPojos, new Comparator<NotepadItemPojo>() {
                @Override
                public int compare(NotepadItemPojo lhs, NotepadItemPojo rhs) {
                    return lhs.getDate().compareTo(rhs.getDate());
                }
            });
            dateOrder = true;
        } else {
            date_iv.setImageResource(R.drawable.ic_up_arrow);
            //descending order
            Collections.sort(notepadItemPojos, new Comparator<NotepadItemPojo>() {
                @Override
                public int compare(NotepadItemPojo lhs, NotepadItemPojo rhs) {
                    return rhs.getDate().compareTo(lhs.getDate());
                }
            });
            dateOrder = false;
        }
        notepadAdapter.notifyDataSetChanged();
    }


    /**
     * Create Option menu  in the activity.
     */
    private void initPopupMenu(TextView view, final int position) {
        final PopupMenu popupMenu;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            popupMenu = new PopupMenu(this, view, Gravity.END);
        } else {
            popupMenu = new PopupMenu(this, view);
        }
        popupMenu.getMenuInflater().inflate(R.menu.notepad_item_menu, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.shareItem:
                        NotepadItemPojo notepadItem = notepadItemPojos.get(position);
                        final ArrayList<String> noteData = notepadItem.getNotedata();
                        String title = notepadItem.getTitle();
                        Intent share = new Intent(Intent.ACTION_SEND);
                        share.setType("text/plain");
                        share.putExtra(Intent.EXTRA_SUBJECT, title);
                        String checkListData = "";
                        for (int i = 0; i < noteData.size(); i++) {
                            checkListData += noteData.get(i) + "\n";
                        }
                        share.putExtra(Intent.EXTRA_TEXT, checkListData);
                        share.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // this will make such that when user returns to your app, your app is displayed, instead of the email app.
                        startActivity(Intent.createChooser(share, "Share on Social Media"));

                        return true;

                    case R.id.deleteItem:

                        AlertDialog.Builder builder = Utility.createDialog(NotePadListActivity.this);
                        LayoutInflater inflater = NotePadListActivity.this.getLayoutInflater();
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

                                if (Utility.isNetworkAvailable(mActivity)) {

                                    String fireBasedocIndex = notepadItemPojos.get(position).getFireBasedocIndex();
                                    NotepadItemPojo notepadItem = new NotepadItemPojo();
                                    notepadItem.setTitle(notepadItemPojos.get(position).getTitle());
                                    notepadItem.setNotedata(notepadItemPojos.get(position).getNotedata());
                                    notepadItem.setNoteType(notepadItemPojos.get(position).getNoteType());
                                    notepadItem.setDate(notepadItemPojos.get(position).getDate());
                                    notepadItem.setFireBasedocIndex(fireBasedocIndex);
                                    String userId = mDatabase.push().getKey();
                                    mDatabase.child(userId).setValue(notepadItem);

                                    mBackUpDatabase.child(pushKey).child("USERDATA").child(fireBasedocIndex).setValue(null);

                                    notepadItemPojos.remove(position);
                                    notepadAdapter.notifyDataSetChanged();

                                    if (notepadItemPojos.size() == 0)
                                        data_not_found.setVisibility(View.VISIBLE);
                                    else
                                        data_not_found.setVisibility(View.GONE);


                                } else {
                                    Toast.makeText(mActivity, "Please connect to internet", Toast.LENGTH_SHORT).show();
                                }

                                if (dialog_parent.isShowing())
                                    dialog_parent.dismiss();

                            }
                        });
                        dialog_parent = builder.show();
                        dialog_parent.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

                        dialog_parent.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                        dialog_parent.show();

                        return true;

                    default:
                        return false;
                }
            }
        });
        popupMenu.show();
    }

    private void popupMenu() {

        final PopupMenu popupMenu;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            popupMenu = new PopupMenu(this, option_menu, Gravity.END);
        } else {
            popupMenu = new PopupMenu(this, option_menu);
        }
        popupMenu.getMenuInflater().inflate(R.menu.notepad_main_menu, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.backupdata:
                        AlertDialog.Builder builder = Utility.createDialog(NotePadListActivity.this);
                        final File file = Utility.getFile();
                        LayoutInflater inflater = NotePadListActivity.this.getLayoutInflater();
                        @SuppressLint("InflateParams") View dialogView = inflater.inflate(R.layout.delete_alert_dialog, null);
                        builder.setView(dialogView);
                        ImageView headerIcon = dialogView.findViewById(R.id.headerIcon);
                        headerIcon.setVisibility(View.GONE);
                        TextView header_title = dialogView.findViewById(R.id.header_title);
                        header_title.setText("Your backup is saved locally on your device" + file.getPath());
                        TextView bodyTitle = dialogView.findViewById(R.id.bodyTitle);
                        bodyTitle.setText("Do you also want to backup your files as TXT and send an email?");
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
                                File folder = new File(file, "storedata.txt");

                                Calendar c = Calendar.getInstance();
                                int year = c.get(Calendar.YEAR);
                                int month = c.get(Calendar.MONTH);
                                int day = c.get(Calendar.DAY_OF_MONTH);

                                int Hr24 = c.get(Calendar.HOUR_OF_DAY);
                                int Min = c.get(Calendar.MINUTE);

                                try {
                                    FileOutputStream fOut = new FileOutputStream(folder);
                                    //  ObjectOutputStream o = new ObjectOutputStream(fOut);
                                    OutputStreamWriter o = new OutputStreamWriter(fOut);

                                    o.append("Backup on " + " " + new StringBuilder().append(day).append("/").append(month + 1).append("/").append(year).append("  ").append(Hr24).append(":").append(Min));
                                    o.append("\n\r");

                                    for (int i = 0; i < notepadItemPojos.size(); i++) {
                                        NotepadItemPojo notepadItemPojo = notepadItemPojos.get(i);
                                        o.append("Title: " + notepadItemPojo.getTitle());
                                        o.append("\n");

                                        String checkListData = "";

                                        for (int j = 0; j < notepadItemPojo.getNotedata().size(); j++) {

                                            checkListData += notepadItemPojo.getNotedata().get(j) + "\n";

                                        }

                                        o.append(checkListData);
                                        o.append("\n\r");

                                    }

                                    o.close();
                                    fOut.close();

                                    Uri data = Utility.getUri_Path(NotePadListActivity.this, folder);
                                    //  Uri uri = Uri.parse(Utility.getFilePathFromContentUri(data, getApplicationContext().getContentResolver()));
                                    Intent share = new Intent(Intent.ACTION_SEND);
                                    share.setType("text/plain");
                                    share.putExtra(Intent.EXTRA_SUBJECT, "text_document");
                                    share.putExtra(Intent.EXTRA_STREAM, data);

                                    // share.setData(Uri.parse("mailto:default@recipient.com")); // or just "mailto:" for blank
                                    share.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // this will make such that when user returns to your app, your app is displayed, instead of the email app.
                                    startActivity(Intent.createChooser(share, "Backup"));



                                } catch (FileNotFoundException e) {
                                    Log.d("exe", "FileNotFoundException" + e.getMessage());
                                } catch (IOException e) {
                                    System.out.println("Error initializing stream");
                                }

                                if (dialog_parent.isShowing())
                                    dialog_parent.dismiss();
                            }
                        });
                        dialog_parent = builder.show();
                        dialog_parent.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
                        dialog_parent.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                        dialog_parent.show();

                        return true;
                    case R.id.restoreData:


                        AlertDialog.Builder builder1 = Utility.createDialog(NotePadListActivity.this);
                        LayoutInflater inflater1 = NotePadListActivity.this.getLayoutInflater();
                        @SuppressLint("InflateParams") View d = inflater1.inflate(R.layout.delete_alert_dialog, null);
                        builder1.setView(d);
                        ImageView header = d.findViewById(R.id.headerIcon);
                        header.setVisibility(View.GONE);
                        TextView title = d.findViewById(R.id.header_title);
                        title.setText("Restore Your Data");
                        TextView body = d.findViewById(R.id.bodyTitle);
                        body.setText("Are you sure you want to restore your notes?");
                        TextView textView = (TextView) d.findViewById(R.id.cancel);
                        textView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (dialog_parent.isShowing())
                                    dialog_parent.dismiss();
                            }
                        });

                        TextView ok = (TextView) d.findViewById(R.id.ok);
                        ok.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (Utility.isNetworkAvailable(mActivity)) {

                                    mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            for (DataSnapshot childDataSnapshot : dataSnapshot.getChildren()) {
                                                NotepadItemPojo notepadItemPojo = childDataSnapshot.getValue(NotepadItemPojo.class);
                                                //  Log.v("exe", "getKey" + childDataSnapshot.getKey());

                                                Log.v("exe", "list" + notepadItemPojo);

                                                setdocumentDat(notepadItemPojo);


                                                NotepadItemPojo notepadItems = new NotepadItemPojo();
                                                notepadItems.setNoteType(notepadItemPojo.getNoteType());
                                                notepadItems.setDate(notepadItemPojo.getDate());
                                                notepadItems.setTitle(notepadItemPojo.getTitle());
                                                notepadItems.setNotedata(notepadItemPojo.getNotedata());
                                                mBackUpDatabase.child(pushKey).child("USERDATA").child(notepadItemPojo.getFireBasedocIndex()).setValue(notepadItems);


                                            }

                                            Log.v("exe", "DataSnapshot" + dataSnapshot.getChildrenCount());

                                            if (dataSnapshot.getChildrenCount() > 0) {
                                                Log.v("exe", "removeValue");

                                                dataSnapshot.getRef().removeValue();
                                            }
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });
                                } else {
                                    Toast.makeText(mActivity, "Please connect to internet", Toast.LENGTH_SHORT).show();
                                }
                                if (dialog_parent.isShowing())
                                    dialog_parent.dismiss();

                            }

                        });
                        dialog_parent = builder1.show();
                        dialog_parent.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
                        dialog_parent.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                        dialog_parent.show();


                        return true;


                    case R.id.settings:
                        Intent intent = new Intent(NotePadListActivity.this, SettingsActivity.class);
                        startActivity(intent);

                        return true;


                    default:
                        return false;
                }
            }
        });
        popupMenu.show();
    }




    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable editable) {
        if (search_text.getText().toString().length() > 0) {
            search_button.setVisibility(View.GONE);
        } else {
            search_button.setVisibility(View.VISIBLE);
        }

        filter(editable.toString());

    }

    private void filter(String text) {
        ArrayList<NotepadItemPojo> filterdNames = new ArrayList<>();
        for (int i = 0; i < notepadItemPojos.size(); i++) {
            NotepadItemPojo add_lockers_data = notepadItemPojos.get(i);
            if (add_lockers_data.getTitle().toLowerCase().contains(text.toLowerCase())) {
                filterdNames.add(add_lockers_data);
            }
        }
        notepadAdapter.setFilter(filterdNames);
    }


  /*  public boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v("exe", "Permission is granted");
                return true;
            } else {

                Log.v("exe", "Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            Log.v("exe", "Permission is granted");
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.v("exe", "Permission: " + permissions[0] + "was " + grantResults[0]);
            //resume tasks needing this permission


        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        isStoragePermissionGranted();
    }
*/

}
