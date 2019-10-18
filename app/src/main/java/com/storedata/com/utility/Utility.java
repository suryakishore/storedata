package com.storedata.com.utility;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by ${3embed} on ${27-10-2017}.
 * Banglore
 */

public class Utility {
    /**
     * <h2>isEmailValid</h2>
     * Method is to simple validate Email String Pattern.
     * Return boolean that specify the inout parameter
     * match with given expression .
     * <p>
     * this method always return boolean. True for the given TextField text match with the expression.
     * if not match then return false and Showing DialogBox to say not match .
     * </P>
     *
     * @param email contain the email text field text
     * @return boolean it is true for Valid and false for invalid
     * @see Pattern
     * @see Matcher
     */
    public static boolean isEmailValid(String email) {
        String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@" + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }


    public static  AlertDialog.Builder createDialog(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        return builder;

    }

    public static  void  openKeyboard(Context context, ImageView imageView){
        InputMethodManager inputMethodManager =
                (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInputFromWindow(imageView.getApplicationWindowToken(),
                InputMethodManager.SHOW_FORCED, 0);
    }
   public static  void  closeKEybord(Context context){
       InputMethodManager imm = (InputMethodManager)context. getSystemService(context.INPUT_METHOD_SERVICE);
       imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
   }

    /**
     * File storing path Image and video storing.*/
    public static final String App_folder="Storedata";
    /**
     * Image storing path.*/
    public static final String App_Picture_folder="/textfiles";



    public static File getFile(){
        String filePath = Environment.getExternalStorageDirectory().toString() + "/storeData";
        File file = new File(filePath);

        Log.d("exe", "file.exists()" + file.exists() + file.getPath());

        if (!file.exists()) {
            file.mkdirs();
            Log.d("exe", "file.exists()" + file.exists());
        }
        return file;
    }
    /**
     * In this method we get the status of internet connectivity. isConnectedOrConnecting() which is
     * present inside NetworkInfo class which Indicates whether network connectivity exists or is in
     * the process of being established.
     * @param activity the current context
     * @return true is Internet connection is available
     */
    public static boolean isNetworkAvailable(Activity activity)
    {
        ConnectivityManager cm = (ConnectivityManager)activity.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    /**
     * <h2>getUri_Path</h2>
     * <p>
     * <p>
     * </P>
     */
    public static Uri getUri_Path(Context context, File file) {
        Uri uri;
        /*
         * Checking if the build version is greater then 25 then no need ask for runtime permission.*/
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            uri = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".provider", file);
        } else {
            uri = Uri.fromFile(file);
        }
        return uri;
    }


}
