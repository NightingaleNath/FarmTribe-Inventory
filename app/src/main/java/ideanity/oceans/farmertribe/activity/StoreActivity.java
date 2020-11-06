package ideanity.oceans.farmertribe.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;

import ideanity.oceans.farmertribe.R;
import ideanity.oceans.farmertribe.database.DatabaseHandler;

public class StoreActivity extends AppCompatActivity {
    Activity activity = this;

    public static DatabaseHandler handler;

    ImageView back;
    public static Button brandReg, pick;
    TextInputLayout businessName;
    TextInputLayout businessAddress;
    TextInputLayout businessCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_store);

        handler = new DatabaseHandler(this);

        back = findViewById(R.id.back_store);
        businessName = findViewById(R.id.businesName);
        businessAddress = findViewById(R.id.business_address);
        businessCode = findViewById(R.id.code);
        brandReg = findViewById(R.id.btn_store);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StoreActivity.super.onBackPressed();
            }
        });

        brandReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveData();
            }
        });

        Loadstore();
    }

    public void Loadstore(){
        String qst = "SELECT * FROM store";
        Cursor cur1 = handler.execQuery(qst);
        if (cur1 != null){
            cur1.moveToFirst();
            try {
                businessName.getEditText().setText(cur1.getString(0));
                businessCode.getEditText().setText(cur1.getString(1));
                businessAddress.getEditText().setText(cur1.getString(2));
            } catch (Exception ex) { ex.printStackTrace();}
        }
        else {
            Toast.makeText(getBaseContext(), "No Registered Store", Toast.LENGTH_LONG).show();
        }
    }

    private void saveData() {

        try {

            String busName = businessName.getEditText().getText().toString().trim();
            String busAddress = businessAddress.getEditText().getText().toString().trim();
            String busCode = businessCode.getEditText().getText().toString().trim();

            if (busName.isEmpty()) {
                AlertDialog.Builder alert = new AlertDialog.Builder(StoreActivity.this);
                alert.setTitle("Business Name");
                alert.setMessage("Business Name can not be empty");
                alert.setPositiveButton("OK", null);
                alert.show();
                return;
            }
            else if (busAddress.isEmpty()) {
                AlertDialog.Builder alert = new AlertDialog.Builder(StoreActivity.this);
                alert.setTitle("Business Address");
                alert.setMessage("Business Address can not be empty");
                alert.setPositiveButton("OK", null);
                alert.show();
                return;
            }
            else if (busCode.isEmpty()) {
                AlertDialog.Builder alert = new AlertDialog.Builder(StoreActivity.this);
                alert.setTitle("Business Code");
                alert.setMessage("Business Code can not be empty");
                alert.setPositiveButton("OK", null);
                alert.show();
                return;
            }

            else{
                Cursor cursor = handler.execQuery("SELECT * FROM store");
                if (cursor == null || cursor.getCount() == 0){
                    String qb = " INSERT INTO store(storename, code, address) VALUES('" + busName + "','" + busCode + "','" + busAddress + "')";
                    if (handler.execAction(qb)){
                        Toast.makeText(activity, "Store Registered", Toast.LENGTH_LONG).show();
                        Loadstore();
                    }
                    else{
                        String qs = "UPDATE store SET storename = '" +busName+"', code = '"+busCode+"', address = '"+busAddress+"'";
                        handler.execAction(qs);
                        Loadstore();
                    }
                }
            }

        }catch (Exception ex){
            ex.printStackTrace();
        }
    }
}
