package ideanity.oceans.farmertribe.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;

import java.util.Objects;

import ideanity.oceans.farmertribe.R;
import ideanity.oceans.farmertribe.database.DatabaseHandler;
import ideanity.oceans.farmertribe.model.PreferenceUtils;

import static ideanity.oceans.farmertribe.activity.RegistrationActivity.handler;

public class LoginActivity extends AppCompatActivity {

    Button login, signUp;
    TextView tView, slogan;
    ImageView logo;
    TextInputLayout username, password;
    public static DatabaseHandler handler;
    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);

        handler = new DatabaseHandler(this);

        login = findViewById(R.id.btn_login);
        signUp = findViewById(R.id.signUp);
        tView = findViewById(R.id.logo_name);
        slogan = findViewById(R.id.logo_slogan);
        logo = findViewById(R.id.login_image);
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);

      /*  PreferenceUtils utils = new PreferenceUtils();

        if (utils.getUser(this) != null ){
            Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);
            startActivity(intent);
        }else{

        }*/

        //===============================================| Create SharedPreferences & Check it |===========================================
        preferences = getSharedPreferences("loginInfo", Context.MODE_PRIVATE); //Create login.xml file in private data folder in your mobile apps
        //Logging time access
        boolean isLoggedIn = preferences.getBoolean("isLoggedIn",false); //Retrieve value by key="isLoggedIn" from login.xml file. False coz if do not get find any value.

        if(isLoggedIn) {
            goToHome();
        }
        else {
            login.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Login(v);
                }
            });
        }

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), RegistrationActivity.class);

                Pair[] pairs = new Pair[7];
                pairs[0] = new Pair<View, String>(logo, "login_image");
                pairs[1] = new Pair<View, String>(tView, "logo_text");
                pairs[2] = new Pair<View, String>(slogan, "logo_slogan");
                pairs[3] = new Pair<View, String>(username, "logo_staff");
                pairs[4] = new Pair<View, String>(password, "logo_pass");
                pairs[5] = new Pair<View, String>(login, "logo_go");
                pairs[6] = new Pair<View, String>(signUp, "logo_new");

                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(LoginActivity.this, pairs);
                    startActivity(intent, options.toBundle());
                }
            }
        });

    }

    public void Login(View view) {
        String staffId = username.getEditText().getText().toString().trim();
        String pass = password.getEditText().getText().toString().trim();

        if (staffId.isEmpty() || pass.isEmpty()) {
            Toast.makeText(this, "Please StaffID and Password cannot be empty", Toast.LENGTH_SHORT).show();
        } else {

            String qd = "SELECT * FROM user WHERE username = '" + staffId + "' AND password = '" + pass + "'";
            Cursor cursor = handler.execQuery(qd);

            if (cursor == null || cursor.getCount() == 0){
                AlertDialog.Builder alert = new AlertDialog.Builder(LoginActivity.this);
                alert.setTitle("Invalid");
                alert.setMessage("Staff ID and Password do not exit");
                alert.setPositiveButton("OK", null);
                alert.show();
                emptyFields();
                return;
            }
            else {
//                PreferenceUtils.saveUser(staffId, this);
//                PreferenceUtils.savePassword(pass, this);
                //===============================================| Writes SharedPreferences |===========================================
                SharedPreferences.Editor editor = preferences.edit(); //Write
                editor.putBoolean("isLoggedIn", true); //key = isLoggedIn and value="true"
                editor.putString("staffId", staffId);
                editor.putString("pass", pass);
                editor.apply();
                editor.commit();
                //======================================================================================================================

                Intent intent = new Intent(getApplicationContext(), DashboardActivity.class);
                //intent.putExtra("staffId", staffId);
                emptyFields();
                startActivity(intent);
                finish();
            }
        }
    }

    private void emptyFields() {
        username.getEditText().setText(null);
        password.getEditText().setText(null);
        username.requestFocus();
    }

    //============================================================| SharedPreferences
    private void goToHome() {
        Intent intent = new Intent(this, DashboardActivity.class);
        startActivity(intent);
        finish();
    }
}
