package ideanity.oceans.farmertribe.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
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

public class BrandUpdateActivity extends AppCompatActivity {

    Activity activity = this;

    public static DatabaseHandler handler;

    ImageView back;
    public static Button brandUpdate;
    TextInputLayout brandText;
    TextView text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_update_brand);

        handler = new DatabaseHandler(this);

        back = findViewById(R.id.back_update);
        brandText = findViewById(R.id.brandUpdate);
        text = findViewById(R.id.text_id);
        brandUpdate = findViewById(R.id.btn_brand_update);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BrandUpdateActivity.super.onBackPressed();
            }
        });

        // Load from List
        Intent i = getIntent();
        String bid = i.getStringExtra("b_id");
        String brand = i.getStringExtra("brands");

        brandText.getEditText().setText(brand);
        text.setText(bid);

        brandUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateData();
            }
        });

    }

    public void updateData(){
        try {

            String b_id = text.getText().toString().trim();
            String brandUpdate = brandText.getEditText().getText().toString().trim();

            String qu = "UPDATE brand SET brandname = '"+brandUpdate+"' WHERE id = '"+b_id+"'";
            if (handler.execAction(qu)) {
                Toast.makeText(activity, "Updated Successfully", Toast.LENGTH_LONG).show();
                finish();
            }else{
                Toast.makeText(activity, "Failed to Update", Toast.LENGTH_LONG).show();
            }
        }
        catch (Exception ex){Toast.makeText(activity, "Brand Update Failed", Toast.LENGTH_SHORT).show();}
    }

}
