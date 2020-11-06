package ideanity.oceans.farmertribe.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;

import java.util.Objects;

import ideanity.oceans.farmertribe.R;
import ideanity.oceans.farmertribe.database.DatabaseHandler;

public class RegistrationActivity extends AppCompatActivity {

    TextInputLayout name, staffId, password;
    Button signUp, loginAlready;
    Activity activity = this;
    public static DatabaseHandler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_registration);

        handler = new DatabaseHandler(this);

        name = findViewById(R.id.fullname);
        staffId = findViewById(R.id.staffId);
        password = findViewById(R.id.pass);
        signUp = findViewById(R.id.btn_Signup);
        loginAlready = findViewById(R.id.already_login);

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                registerUser();
               /* String qu = "INSERT INTO user VALUES('" + fullname + "'," +
                        "'" + staffID + "'," +
                        "'" + pass + "');";
                Log.d("User Reg", qu);
                handler.execAction(qu);
                qu = "SELECT * FROM user WHERE username = '" + staffID + "';";
                if (handler.execQuery(qu) != null) {
                    Toast.makeText(RegistrationActivity.this, "User Added Successfully", Toast.LENGTH_LONG).show();
                    finish();
                }*/
            }
        });

        loginAlready.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                emptyFields();
                startActivity(intent);
                finish();
            }
        });
    }

    public void registerUser() {

        try {
            String fullname = name.getEditText().getText().toString().trim();
            String staffID = staffId.getEditText().getText().toString().trim();
            String pass = password.getEditText().getText().toString().trim();

            if (fullname.isEmpty() || fullname.length() < 2) {
                AlertDialog.Builder alert = new AlertDialog.Builder(RegistrationActivity.this);
                alert.setTitle("Full Name");
                alert.setMessage("Full Name must not be empty or less than 2");
                alert.setPositiveButton("OK", null);
                alert.show();
                return;
            } else if (staffID.isEmpty() || staffID.length() < 2) {
                AlertDialog.Builder alert = new AlertDialog.Builder(RegistrationActivity.this);
                alert.setTitle("Staff ID");
                alert.setMessage("Staff ID must not be empty or less than 2");
                alert.setPositiveButton("OK", null);
                alert.show();
                return;
            } else if (pass.isEmpty() || pass.length() < 2) {
                AlertDialog.Builder alert = new AlertDialog.Builder(activity);
                alert.setTitle("Password");
                alert.setMessage("Password must not be empty or less than 2");
                alert.setPositiveButton("OK", null);
                alert.show();
                password.requestFocus();
                return;
            } else {
                String qu = "INSERT INTO user(name, username, password) VALUES('" + fullname + "','" + staffID + "','" + pass + "')";
                Log.d("User Reg", qu);
                handler.execAction(qu);
                qu = "SELECT * FROM user WHERE username = '" + staffID + "';";
                if (handler.execQuery(qu) != null) {
                    Toast.makeText(RegistrationActivity.this, "User Added successfully", Toast.LENGTH_LONG).show();
                    emptyFields();
                    finish();
                }
            }
        } catch (Exception ex) {
            Toast.makeText(activity, "User Registration Failed", Toast.LENGTH_SHORT).show();
        }
    }

    private void emptyFields(){
        Objects.requireNonNull(name.getEditText()).setText(null);
        staffId.getEditText().setText(null);
        password.getEditText().setText(null);
    }

}
