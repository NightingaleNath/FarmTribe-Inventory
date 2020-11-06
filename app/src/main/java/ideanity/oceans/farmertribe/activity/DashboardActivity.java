package ideanity.oceans.farmertribe.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.text.ParseException;
import java.util.Date;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.card.MaterialCardView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import ideanity.oceans.farmertribe.R;
import ideanity.oceans.farmertribe.database.DatabaseHandler;
import ideanity.oceans.farmertribe.model.PreferenceUtils;

public class DashboardActivity extends AppCompatActivity {

    TextView user, codeStore, store;
    MaterialCardView brandCard, memberCard, settingsCard, trainingCard, productCard, stockInCard, posCard, posViewCard, attendCard, logoutCard;
    public static DatabaseHandler handler;
    public static Activity activity;
    //For checking SharedPreferences
    SharedPreferences preferences;
    ImageView about;
    private final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
    private final long ONE_DAY = 24 * 60 * 60 * 1000;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_dashboard);

        handler = new DatabaseHandler(this);
        activity = this;

        ///Checking the expire date
        SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
        String installDate = sharedPreferences.getString("InstallDate", null);
        if(installDate == null){
            // First run, so save the current date
            SharedPreferences.Editor editor = sharedPreferences.edit();
            Date now = new Date();
            String dateString = formatter.format(now);
            editor.putString("InstallDate", dateString);
            // Commit the edits!
            editor.commit();
        }
        else{
            // This is not the 1st run, check install date
            Date before = null;
            try {
                before = (Date)formatter.parse(installDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            Date now = new Date();
            long diff = now.getTime() - before.getTime();
            long days = diff / ONE_DAY;
            if(days > 5) { // More than 30 days?
                finishAffinity();
                System.exit(0);
            }
        }

        user = findViewById(R.id.userSign);
        codeStore = findViewById(R.id.codeStore);
        store = findViewById(R.id.textView);
        brandCard = findViewById(R.id.card_brand);
        memberCard = findViewById(R.id.card_member);
        settingsCard = findViewById(R.id.card_settings);
        trainingCard = findViewById(R.id.card_training);
        productCard = findViewById(R.id.card_product);
        stockInCard = findViewById(R.id.card_stock);
        posCard = findViewById(R.id.card_pos);
        posViewCard = findViewById(R.id.card_poview);
        attendCard = findViewById(R.id.card_train_records);
        logoutCard = findViewById(R.id.card_logut);
        about = findViewById(R.id.about);

        /*Intent intent = getIntent();
        if (intent.hasExtra("staffId")) {
            String nameFromIntent = getIntent().getStringExtra("staffId");
            user.setText(nameFromIntent);
        }else {
            String staffId = PreferenceUtils.getUser(this);
            user.setText(staffId);
        }*/

        //===============================================| Getting SharedPreferences |===========================================
        //for checking have any sharedPreferences
        preferences = getSharedPreferences("loginInfo", MODE_PRIVATE);
        boolean isLoggedIn = preferences.getBoolean("isLoggedIn", false);
        String staffID = preferences.getString("staffId", "Data not found");
        Toast.makeText(this, "Login Details are "+isLoggedIn +" for "+staffID, Toast.LENGTH_SHORT).show();
        user.setText(staffID);
        //========================================================================================================================


        Thread t = new Thread() {
            @Override
            public void run() {
                try {
                    while (!isInterrupted()) {
                        Thread.sleep(1000);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                TextView time = (TextView)findViewById(R.id.time);
                                long date = System.currentTimeMillis();
                                SimpleDateFormat sdf = new SimpleDateFormat("hh-mm-ss a");
                                String dateString = sdf.format(date);
                                time.setText(dateString);

                            }
                        });
                    }
                } catch (InterruptedException e) {
                }
            }
        };
        t.start();

        String qst = "SELECT * FROM store";
        Cursor cur1 = handler.execQuery(qst);
        if (cur1 != null){
            cur1.moveToFirst();
            try {
                store.setText(cur1.getString(0));
                codeStore.setText("Store Code: " + cur1.getString(1));
            } catch (Exception ex) { ex.printStackTrace();}
        }
        else {
            store.setText("FarmerTribe");
            codeStore.setText("Store Code: 001");
        }

        about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               aboutInfo();
            }
        });

        brandCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), BrandListActivity.class));
            }
        });

        memberCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), MemberListActivity.class));
            }
        });

        trainingCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = activity.getFragmentManager();
                createRequest request = new createRequest();
                request.show(fm, "Select");
            }
        });

        settingsCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
            }
        });

        productCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), ProductListActivity.class));
            }
        });

        stockInCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userId = user.getText().toString();

                Intent launchinIntent = new Intent(getApplicationContext(), StockInListActivity.class);
                launchinIntent.putExtra("USER", userId);
                startActivity(launchinIntent);
            }
        });

        posCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userId = user.getText().toString();

                Intent launchinIntent = new Intent(getApplicationContext(), PosActivity.class);
                launchinIntent.putExtra("USER", userId);
                startActivity(launchinIntent);
            }
        });

        posViewCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), LoadProduct.class));
            }
        });

        attendCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), ViewAttendanceActivity.class));
            }
        });

        logoutCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(activity);
                alert.setTitle("LOGOUT");
                alert.setMessage("DO YOU WANT TO LOGOUT?");
                alert.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                       /* PreferenceUtils.savePassword(null, activity);
                        PreferenceUtils.saveUser(null, activity);
                        Intent intent = new Intent(activity, LoginActivity.class);
                        startActivity(intent);
                        finish();
                        dialog.dismiss();*/

                        //===============================================| Remove SharedPreferences |===========================================
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.clear(); //Remove from login.xml file
                        editor.apply();
                        editor.commit();
                        //======================================================================================================================

                        Intent intent = new Intent(DashboardActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                        dialog.dismiss();
                    }
                });
                alert.setNegativeButton("NO", null);
                alert.show();
            }
        });

    }

    private void aboutInfo() {
        new AlertDialog.Builder(DashboardActivity.this)
                .setTitle("About")
                .setMessage("FarmerTribe 2020 \nVersion 1.0 \n" + DashboardActivity.this.getString(R.string.developed_by))
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(getApplicationContext(), "Thank You", Toast.LENGTH_SHORT);
                    }
                }).show();
    }

    @SuppressLint("ValidFragment")
    public class createRequest extends DialogFragment {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            // Get the layout inflater
            LayoutInflater inflater = getActivity().getLayoutInflater();
            final View v = inflater.inflate(R.layout.pick_period, null);
            final DatePicker datePicker = (DatePicker) v.findViewById(R.id.datePicker);
            final EditText hour = (EditText) v.findViewById(R.id.periodID);
            final TextView users = (TextView) v.findViewById(R.id.userId);
            final Spinner spn = (Spinner) v.findViewById(R.id.spinnerTopic);
            final Spinner spnl = (Spinner) v.findViewById(R.id.spinnerLocation);

            String nameFromIntent = user.getText().toString();
            //String nameFromIntent = getIntent().getStringExtra("staffId");
            users.setText(nameFromIntent);

            String qu = "SELECT * FROM settings";
            ArrayList<String> subs = new ArrayList<>();
            ArrayList<String> subl = new ArrayList<>();
            subs.add("Select Training Topic");
            subl.add("Select Training Location");
            Cursor cr = handler.execQuery(qu);
            if (cr != null) {
                cr.moveToFirst();
                while (!cr.isAfterLast()) {
                    subs.add(cr.getString(1));
                    subl.add(cr.getString(2));

                    cr.moveToNext();
                }
            } else
                Log.d("GridAdapter.class", "No SUBS" + cr.getString(1));

            ArrayAdapter<String> adapterSpinner = new ArrayAdapter<String>(activity, android.R.layout.simple_spinner_dropdown_item, subs);
            assert spn != null;
            spn.setAdapter(adapterSpinner);

            ArrayAdapter<String> adapterSpinnerl = new ArrayAdapter<String>(activity, android.R.layout.simple_spinner_dropdown_item, subl);
            assert spnl != null;
            spnl.setAdapter(adapterSpinnerl);

            builder.setView(v)
                    // Add action buttons
                    .setPositiveButton("Enter", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {

                            int day = datePicker.getDayOfMonth();
                            int month = datePicker.getMonth() + 1;
                            int year = datePicker.getYear();
                            String date = year + "-" + month + "-" + day;
                            String topic = spn.getSelectedItem().toString();
                            String location = spnl.getSelectedItem().toString();
                            String userId = users.getText().toString();

                            Cursor cursor = handler.execQuery("SELECT * FROM ATTENDANCE WHERE datex = '" +
                                    date + "' AND hour = " + hour.getText() + ";");
                            if (cursor == null || cursor.getCount() == 0) {
                                Intent launchinIntent = new Intent(activity, AttendanceActivity.class);
                                launchinIntent.putExtra("DATE", date);
                                launchinIntent.putExtra("PERIOD", hour.getText().toString());
                                launchinIntent.putExtra("TOPIC", topic);
                                launchinIntent.putExtra("LOCATION", location);
                                launchinIntent.putExtra("USER", userId);
                                activity.startActivity(launchinIntent);
                            } else {
                                Toast.makeText(getActivity(), "Period Already Added", Toast.LENGTH_LONG).show();
                            }
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                        }
                    });

            return builder.create();
        }
    }
}
