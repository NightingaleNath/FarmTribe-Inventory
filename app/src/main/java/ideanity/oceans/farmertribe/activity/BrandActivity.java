package ideanity.oceans.farmertribe.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;

import ideanity.oceans.farmertribe.R;
import ideanity.oceans.farmertribe.database.DatabaseHandler;

public class BrandActivity extends AppCompatActivity {

    Activity activity = this;

    //public static DatabaseHandler handler;

    ImageView back;
    public static Button brandReg;
    TextInputLayout brandText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_brand);

        //handler = new DatabaseHandler(this);

        back = findViewById(R.id.back_brand);
        brandText = findViewById(R.id.brandName);
        brandReg = findViewById(R.id.btn_brand);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BrandActivity.super.onBackPressed();
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
            String brandName = brandText.getEditText().getText().toString().trim();

            if (brandName.isEmpty()) {
                AlertDialog.Builder alert = new AlertDialog.Builder(BrandActivity.this);
                alert.setTitle("Brand Name");
                alert.setMessage("Brand Name can not be empty");
                alert.setPositiveButton("OK", null);
                alert.show();
                return;
            } else {
                SQLiteDatabase database = openOrCreateDatabase("FARM", Context.MODE_PRIVATE, null);
                String sql = "CREATE TABLE IF NOT EXISTS brand(id INTEGER PRIMARY KEY AUTOINCREMENT, brandname VARCHAR(1000))";
                database.execSQL(sql);

                String qu = " INSERT INTO brand(brandname) VALUES(?)";

                SQLiteStatement statement = database.compileStatement(qu);
                statement.bindString(1, brandName);
                statement.execute();
                Toast.makeText(activity, "New Brand Added Successfully", Toast.LENGTH_LONG).show();
                brandText.requestFocus();
                brandText.getEditText().setText("");

            }

        } catch (Exception ignored) {
            Toast.makeText(activity, "Brand Registration Failed", Toast.LENGTH_SHORT).show();
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
