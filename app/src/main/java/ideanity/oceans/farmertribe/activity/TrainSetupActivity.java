package ideanity.oceans.farmertribe.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;

import ideanity.oceans.farmertribe.R;

public class TrainSetupActivity extends AppCompatActivity {

    Activity activity = this;

    //public static DatabaseHandler handler;

    ImageView back;
    public static Button brandReg;
    TextInputLayout topic, location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_train_setup);

        //handler = new DatabaseHandler(this);

        back = findViewById(R.id.back_setting);
        topic = findViewById(R.id.topic);
        location = findViewById(R.id.location);
        brandReg = findViewById(R.id.btn_settings);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TrainSetupActivity.super.onBackPressed();
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
            String topicN = topic.getEditText().getText().toString().trim();
            String locationN = location.getEditText().getText().toString().trim();

            if (topicN.isEmpty()) {
                AlertDialog.Builder alert = new AlertDialog.Builder(TrainSetupActivity.this);
                alert.setTitle("New Topic");
                alert.setMessage("Topic can not be empty");
                alert.setPositiveButton("OK", null);
                alert.show();
                return;

            } else if (locationN.isEmpty()) {
                AlertDialog.Builder alert = new AlertDialog.Builder(TrainSetupActivity.this);
                alert.setTitle("New Location");
                alert.setMessage("Location can not be empty");
                alert.setPositiveButton("OK", null);
                alert.show();
                return;
            } else {
                SQLiteDatabase database = openOrCreateDatabase("FARM", Context.MODE_PRIVATE, null);
                String sql = "CREATE TABLE IF NOT EXISTS settings(id INTEGER PRIMARY KEY AUTOINCREMENT, topic VARCHAR(100), location VARCHAR(100))";
                database.execSQL(sql);

                String qu = " INSERT INTO settings(topic, location) VALUES(?,?)";

                SQLiteStatement statement = database.compileStatement(qu);
                statement.bindString(1, topicN);
                statement.bindString(2, locationN);
                statement.execute();
                Toast.makeText(activity, "Settings Set Successfully", Toast.LENGTH_LONG).show();
                topic.requestFocus();
                topic.getEditText().setText("");
                location.getEditText().setText("");

            }

        } catch (Exception ignored) {
            Toast.makeText(activity, "Settings Registration Failed", Toast.LENGTH_SHORT).show();
        }
    }
}
