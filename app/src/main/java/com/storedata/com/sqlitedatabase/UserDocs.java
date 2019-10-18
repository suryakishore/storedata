package com.storedata.com.sqlitedatabase;

/**
 * Created by ${3embed} on ${27-10-2017}.
 * Banglore
 */

public class UserDocs {
    private String docId;
    private int id;
    private String name;
    private int userNameRowNo;
    private int docType;

    public int getDocType() {
        return docType;
    }

    public void setDocType(int docType) {
        this.docType = docType;
    }

    public int getUserNameRowNo() {
        return userNameRowNo;
    }

    public void setUserNameRowNo(int userNameRowNo) {
        this.userNameRowNo = userNameRowNo;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDocId() {
        return docId;
    }

    public void setDocId(String docId) {
        this.docId = docId;
    }


}
