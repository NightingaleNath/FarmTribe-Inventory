package ideanity.oceans.farmertribe.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;

import ideanity.oceans.farmertribe.R;
import ideanity.oceans.farmertribe.database.DatabaseHandler;

public class MemberActivity extends AppCompatActivity {

    Activity activity = this;

    public static DatabaseHandler handler;

    ArrayList<String> b_id;
    ArrayList<String> storename;
    ArrayList<String> code;
    ArrayList<String> address;
    ArrayAdapter<String> adapterSpinner, adapterSpinnerGender;

    ImageView back;
    public static Button brandReg, pick;
    TextInputLayout farmerId;
    TextInputLayout farmerName;
    TextInputLayout age;
    TextInputLayout gender;
    TextInputLayout phone;
    TextInputLayout community;
    public static ArrayList<String> divisions, genders;
    Spinner spinner, spinnerGend;
    String scode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_member);

        handler = new DatabaseHandler(this);

        divisions = new ArrayList<>();
        divisions.add("Select Member type to proceed");
        divisions.add("SHF");
        divisions.add("CF");

        genders = new ArrayList<>();
        genders.add("Select Gender");
        genders.add("Male");
        genders.add("Female");

        spinnerGend = (Spinner) findViewById(R.id.spinnerGender);
        adapterSpinnerGender = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, genders);
        assert spinner != null;
        spinnerGend.setAdapter(adapterSpinnerGender);

        storename = new ArrayList<>();
        code = new ArrayList<>();
        address = new ArrayList<>();
        b_id = new ArrayList<>();

        spinner = (Spinner) findViewById(R.id.spinnerFarm);
        adapterSpinner = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, divisions);
        assert spinner != null;
        spinner.setAdapter(adapterSpinner);

        back = findViewById(R.id.back_member);
        farmerId = findViewById(R.id.farmId);
        farmerName = findViewById(R.id.farmerName);
        age = findViewById(R.id.age);
        spinnerGend = findViewById(R.id.spinnerGender);
        phone = findViewById(R.id.phone);
        community = findViewById(R.id.community);

        brandReg = findViewById(R.id.btn_member);
        pick = findViewById(R.id.btn_pick);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MemberActivity.super.onBackPressed();
            }
        });

        pick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (spinner.getSelectedItem() == "SHF"){
                    scode = "SHF";
                }
                else if (spinner.getSelectedItem() == "CF"){
                    scode = "CF";
                }
                else {
                    scode = "OF";
                }
                String pickCode = "";
                int c = 0;
                String qu = "SELECT * FROM store";
                String qb = "SELECT id FROM member";
                Cursor cursor = handler.execQuery(qu);

                int att = 0;
                Cursor cur = handler.execQuery(qb);

                if (cursor == null || cursor.getCount() == 0) {
                    pickCode = "001" + scode + att;
                } else {
                    cursor.moveToFirst();
                    try {
                       pickCode = cursor.getString(1) + scode;
                    } catch (Exception e) { }
                }

                if (cur != null) {
                    cur.moveToFirst();
                    try {
                        att = (cur.getCount() + 1);
                        if (att <= 0)
                            att = 1;
                        //Log.d("ProfileActivity", "Total = " + cur.getCount() + " avail = " + cur1.getCount() + " per " + att);
                    } catch (Exception e) {
                        att = 1;
                    }
                }

                /*String qb = "SELECT count(id) + 1 FROM member";
                Cursor cur = handler.execQuery(qb);
                cur.moveToFirst();
                c = cur.getInt(1) + 1;*/

                //String generate = pickCode + c;
                farmerId.getEditText().setText(pickCode + att);
            }
        });

        brandReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveData();
            }
        });

    }

    private void saveData() {
        try {
            String farmId = farmerId.getEditText().getText().toString().trim();
            String farmName = farmerName.getEditText().getText().toString().trim();
            String farmAge = age.getEditText().getText().toString().trim();
            String farmGender = spinnerGend.getSelectedItem().toString();
            String farmPhone = phone.getEditText().getText().toString().trim();
            String farmCom = community.getEditText().getText().toString().trim();

            if (farmId.isEmpty()) {
                AlertDialog.Builder alert = new AlertDialog.Builder(MemberActivity.this);
                alert.setTitle("Farmer ID");
                alert.setMessage("Farmer ID can not be empty");
                alert.setPositiveButton("OK", null);
                alert.show();
                return;
            }
            else if (farmName.isEmpty()) {
                AlertDialog.Builder alert = new AlertDialog.Builder(MemberActivity.this);
                alert.setTitle("Farmer Name");
                alert.setMessage("Farmer Name can not be empty");
                alert.setPositiveButton("OK", null);
                alert.show();
                return;
            }
            else if (farmAge.isEmpty() || farmAge.length() > 3) {
                AlertDialog.Builder alert = new AlertDialog.Builder(MemberActivity.this);
                alert.setTitle("Age");
                alert.setMessage("Age can not be empty");
                alert.setPositiveButton("OK", null);
                alert.show();
                return;
            }
            else if (farmGender.isEmpty()) {
                AlertDialog.Builder alert = new AlertDialog.Builder(MemberActivity.this);
                alert.setTitle("Gender");
                alert.setMessage("Gender can not be empty");
                alert.setPositiveButton("OK", null);
                alert.show();
                return;
            }
            else if (farmPhone.isEmpty() || farmPhone.length() > 10) {
                AlertDialog.Builder alert = new AlertDialog.Builder(MemberActivity.this);
                alert.setTitle("Phone Number");
                alert.setMessage("Phone Number can not be empty");
                alert.setPositiveButton("OK", null);
                alert.show();
                return;
            }
            else if (farmCom.isEmpty()) {
                AlertDialog.Builder alert = new AlertDialog.Builder(MemberActivity.this);
                alert.setTitle("Community");
                alert.setMessage("Community can not be empty");
                alert.setPositiveButton("OK", null);
                alert.show();
                return;
            }
            else {
                SQLiteDatabase database = openOrCreateDatabase("FARM", Context.MODE_PRIVATE, null);
                String sql = "CREATE TABLE IF NOT EXISTS member(id INTEGER PRIMARY KEY AUTOINCREMENT, farmerName VARCHAR(100), farmerId VARCHAR(100), community VARCHAR(100), age VARCHAR(10), sex VARCHAR(10), phone VARCHAR(50));";
                database.execSQL(sql);

                String qu = " INSERT INTO member(farmerName, farmerId, community, age, sex, phone) VALUES(?,?,?,?,?,?)";

                SQLiteStatement statement = database.compileStatement(qu);
                statement.bindString(1, farmName);
                statement.bindString(2, farmId);
                statement.bindString(3, farmCom);
                statement.bindString(4, farmAge);
                statement.bindString(5, farmGender);
                statement.bindString(6, farmPhone);
                statement.execute();
                Toast.makeText(activity, "New Member Added Successfully", Toast.LENGTH_LONG).show();
                farmerId.requestFocus();
                farmerId.getEditText().setText("");
                farmerName.getEditText().setText("");
                age.getEditText().setText("");
                spinnerGend.setSelection(0);
                phone.getEditText().setText("");
                community.getEditText().setText("");
                phone.getEditText().setText("");
                community.getEditText().setText("");
            }

        } catch (Exception ignored) {
            Toast.makeText(activity, "Member Registration Failed", Toast.LENGTH_SHORT).show();
        }

    }

    /*public void updateData(){
        try {
            // Load from List
            Intent i = getIntent();
            String bid = i.getStringExtra("b_id");
            String brand = i.getStringExtra("brands");

            brandText.getEditText().setText(brand);
            text.setText(bid);

            String b_id = text.getText().toString().trim();
            String brandUpdate = brandText.getEditText().getText().toString().trim();

            String qu = "UPDATE brand SET brandname = '"+brandUpdate+"' WHERE regno = '"+b_id+"'";
            Log.d("BrandActivity", qu);
            if (handler.execAction(qu)) {
                Toast.makeText(activity, "Updated Successfully", Toast.LENGTH_LONG).show();
                finish();
            }else{
                Toast.makeText(activity, "Failed to Update", Toast.LENGTH_LONG).show();
            }
        }
        catch (Exception ex){Toast.makeText(activity, "Brand Update Failed", Toast.LENGTH_SHORT).show();}
    }*/

}
