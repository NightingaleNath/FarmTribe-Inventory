package ideanity.oceans.farmertribe.database;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;

public class DatabaseHandler {

    Activity activity;
    private SQLiteDatabase database;

    public DatabaseHandler(Activity activity) {
        this.activity = activity;
        database = activity.openOrCreateDatabase("FARM", activity.MODE_PRIVATE, null);
        createTable();
    }

    private void createTable() {
        try {
            String qu = "CREATE TABLE IF NOT EXISTS user(user_id INTEGER PRIMARY KEY AUTOINCREMENT, name VARCHAR(1000)," +
                    "username VARCHAR(100), " +
                    "password VARCHAR(100));";
            database.execSQL(qu);
        } catch (Exception e) {
            Toast.makeText(activity, "Error Occured for create table", Toast.LENGTH_LONG).show();
        }

        try {
            String qu = "CREATE TABLE IF NOT EXISTS brand(id INTEGER PRIMARY KEY AUTOINCREMENT, brandname VARCHAR(1000));";
            database.execSQL(qu);
        } catch (Exception e) {
            Toast.makeText(activity, "Error Occured for create table", Toast.LENGTH_LONG).show();
        }

        try {
            String qu = "CREATE TABLE IF NOT EXISTS cart(id INTEGER PRIMARY KEY AUTOINCREMENT, transno VARCHAR(100),  pcode VARCHAR(100),  pdes VARCHAR(100), price VARCHAR(100), qty VARCHAR(50),  disc VARCHAR(50),  total VARCHAR(100), vendorId VARCHAR(100), vendorName VARCHAR(100), vendorPlace VARCHAR(100), sdate date, saleperson VARCHAR(100), status VARCHAR(100));";
            database.execSQL(qu);
        } catch (Exception e) {
            Toast.makeText(activity, "Error Occured for create table", Toast.LENGTH_LONG).show();
        }

        try {
            String qu = "CREATE TABLE IF NOT EXISTS stockIn(id INTEGER PRIMARY KEY AUTOINCREMENT, refno VARCHAR(100), pcode VARCHAR(100), pdesc VARCHAR(100), qty INTEGER, sdate date, stockinby VARCHAR(100), status VARCHAR(100));";
            database.execSQL(qu);
        } catch (Exception e) {
            Toast.makeText(activity, "Error Occured for create table", Toast.LENGTH_LONG).show();
        }

        try {
            String qu = "CREATE TABLE IF NOT EXISTS product(pcode VARCHAR(100) PRIMARY KEY, pdes VARCHAR(100), brandname VARCHAR(100), unit VARCHAR(100), purchasePrice VARCHAR(100), price VARCHAR(100), qty INTEGER, reorder INTEGER, vendorId VARCHAR(100), vendorName VARCHAR(100), vendorPhone VARCHAR(100), vendorPlace VARCHAR(100));";
            database.execSQL(qu);
        } catch (Exception e) {
            Toast.makeText(activity, "Error Occured for create table", Toast.LENGTH_LONG).show();
        }

        try {
            String qu = "CREATE TABLE IF NOT EXISTS store(storename VARCHAR(100), code VARCHAR(100), address VARCHAR(100));";
            database.execSQL(qu);
        } catch (Exception e) {
            Toast.makeText(activity, "Error Occured for create table", Toast.LENGTH_LONG).show();
        }

        try {
                    String qu = "CREATE TABLE IF NOT EXISTS settings(id INTEGER PRIMARY KEY AUTOINCREMENT, topic VARCHAR(100), location VARCHAR(100));";
                    database.execSQL(qu);
                } catch (Exception e) {
                    Toast.makeText(activity, "Error Occured for create table", Toast.LENGTH_LONG).show();
                }

        try {
            String qu = "CREATE TABLE IF NOT EXISTS member(id INTEGER PRIMARY KEY AUTOINCREMENT, farmerName VARCHAR(100)," +
                    "farmerId VARCHAR(100), " +
                    "community VARCHAR(100), age VARCHAR(10), sex VARCHAR(10), phone VARCHAR(50));";
            database.execSQL(qu);
        } catch (Exception e) {
            Toast.makeText(activity, "Error Occured for create table", Toast.LENGTH_LONG).show();
        }

        try {
            String qu = "CREATE TABLE IF NOT EXISTS ATTENDANCE(datex date," +
                    "hour int, " +
                    "farmerId varchar(100), topic varchar(100), location varchar(100), userid varchar(100), isPresent boolean);";
            database.execSQL(qu);
        } catch (Exception e) {
            Toast.makeText(activity, "Error Occured for create table", Toast.LENGTH_LONG).show();
        }

    }

    public boolean execAction(String qu) {
        Log.i("DatabaseHandler", qu);
        try {
            database.execSQL(qu);
        } catch (Exception e) {
            Log.e("DatabaseHandler", qu);
            Toast.makeText(activity, "Error Occured for execAction", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    public Cursor execQuery(String qu) {
        try {
            return database.rawQuery(qu, null);
        } catch (Exception e) {
            Log.e("DatabaseHandler", qu);
            Toast.makeText(activity,"Error Occurred for execAction",Toast.LENGTH_LONG).show();
        }
        return null;
    }
}
