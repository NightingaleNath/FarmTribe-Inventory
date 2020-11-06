package ideanity.oceans.farmertribe.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;


/**
 * Created by delaroy on 3/26/18.
 */

public class PreferenceUtils {

    public PreferenceUtils(){

    }

    public static boolean saveUser(String username, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor prefsEditor = prefs.edit();
        prefsEditor.putString(Constants.KEY_USER, username);
        prefsEditor.apply();
        return true;
    }

    public static String getUser(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(Constants.KEY_USER, null);
    }

    public static boolean savePassword(String password, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor prefsEditor = prefs.edit();
        prefsEditor.putString(Constants.KEY_PASSWORD, password);
        prefsEditor.apply();
        return true;
    }

    public static String getPassword(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(Constants.KEY_PASSWORD, null);
    }
}
