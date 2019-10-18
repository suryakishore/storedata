package com.storedata.com.createnote;

import android.util.Log;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.Document;
import com.couchbase.lite.Manager;
import com.couchbase.lite.android.AndroidContext;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by ${3embed} on ${27-10-2017}.
 * Banglore
 */

public class CouchDbController {
    private String DB_NAME = "storedatadb";
    private static final String TAG = "CouchBaseEvents";
    private Manager manager;
    private Database database;
    private AndroidContext a_context;

    public CouchDbController(AndroidContext a_context) {
        this.a_context = a_context;
        try {
            manager = getManagerInstance();
            database = getDatabaseInstance();
        } catch (Exception e) {
            Log.e(TAG, "Error getting database", e);
        }
    }

    /*
   * To get existing db or create new db if db doesn't exists
   * */
    private Database getDatabaseInstance() {
        if ((this.database == null) & (this.manager != null)) {
            try {
                this.database = manager.getExistingDatabase(DB_NAME);
                if (database == null) {
                    this.database = manager.getDatabase(DB_NAME);
                }
                database.open();
            } catch (CouchbaseLiteException e) {
                Log.e(TAG, "Error getting database", e);
            }
        }
        return database;
    }

    /**
     * to get instance of db manager
     */
    private Manager getManagerInstance() throws IOException {
        if (manager == null) {
            manager = new Manager(a_context, Manager.DEFAULT_OPTIONS);
        }
        return manager;
    }

    public String createDocument(String title, String date, ArrayList<String> notedata) {
        Document document = database.createDocument();
        String docID = "";
        Map<String, Object> docContent = new HashMap<String, Object>();
        docContent.put("title", title);
        docContent.put("date", date);
        docContent.put("notedata", notedata);


        try {
            document.putProperties(docContent);
            Log.d(TAG, "Document written to database named " + DB_NAME + " with ID = " + document.getId());
        } catch (CouchbaseLiteException e) {
            Log.e(TAG, "Cannot write document to database", e);
        }
        docID = document.getId();
        return docID;
    }

    public Map<String, Object> retrieveDocument(String docId) {
        Document retrievedDocument = database.getDocument(docId);
        Log.d(TAG, "retrievedDocument=" + String.valueOf(retrievedDocument.getProperties()));
        return retrievedDocument.getProperties();
    }

    public void updateDocument(String docId,String title,String date,ArrayList<String> notedata){
        Document retrievedDocument = database.getDocument(docId);
        Map<String, Object> updatedProperties = new HashMap<String, Object>();
        updatedProperties.putAll(retrievedDocument.getProperties());
        updatedProperties.put("title", title);
        updatedProperties.put("date", date);
        updatedProperties.put("notedata", notedata);
        try {
            retrievedDocument.putProperties(updatedProperties);
            Log.d(TAG, "updated retrievedDocument=" + String.valueOf(retrievedDocument.getProperties()));
        } catch (CouchbaseLiteException e) {
            Log.e (TAG, "Cannot update document", e);
        }
    }

    public  boolean deleteDocument(String docId ){
        Document retrievedDocument = database.getDocument(docId);
        try {
            retrievedDocument.delete();
            Log.d (TAG, "Deleted document, deletion status = " + retrievedDocument.isDeleted());
            return retrievedDocument.isDeleted();
        } catch (CouchbaseLiteException e) {
            Log.e (TAG, "Cannot delete document", e);
        }
        Log.d(TAG, "End CouchBase Dtabase");
        return retrievedDocument.isDeleted();
    }

}
