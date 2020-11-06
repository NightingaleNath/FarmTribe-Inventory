package ideanity.oceans.farmertribe.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.DialogFragment;
import android.app.FragmentManager;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;

import ideanity.oceans.farmertribe.R;
import ideanity.oceans.farmertribe.database.DatabaseHandler;

public class MainActivity extends AppCompatActivity {

    TextView user;
    MaterialCardView brandCard, memberCard, settingsCard, trainingCard;
    public static DatabaseHandler handler;
    public static Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        handler = new DatabaseHandler(this);
        activity = this;

        user = findViewById(R.id.user);
        brandCard = findViewById(R.id.brand_card);
        memberCard = findViewById(R.id.member_card);
        settingsCard = findViewById(R.id.card_settings);
        trainingCard = findViewById(R.id.card_training);

        String nameFromIntent = getIntent().getStringExtra("staffId");
        user.setText("Welcome " + nameFromIntent);

        brandCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), BrandListActivity.class));
            }
        });

        settingsCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
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

            String nameFromIntent = getIntent().getStringExtra("staffId");
            users.setText(nameFromIntent);

            String qu = "SELECT * FROM settings";
            ArrayList<String> subs = new ArrayList<>();
            ArrayList<String> subl = new ArrayList<>();
            subs.add("Not Specified");
            Cursor cr = MainActivity.handler.execQuery(qu);
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

                            Cursor cursor = MainActivity.handler.execQuery("SELECT * FROM ATTENDANCE WHERE datex = '" +
                                    date + "' AND hour = " + hour.getText() + ";");
                            if (cursor == null || cursor.getCount() == 0) {
                                Intent launchinIntent = new Intent(MainActivity.activity, AttendanceActivity.class);
                                launchinIntent.putExtra("DATE", date);
                                launchinIntent.putExtra("PERIOD", hour.getText().toString());
                                launchinIntent.putExtra("TOPIC", topic);
                                launchinIntent.putExtra("LOCATION", location);
                                launchinIntent.putExtra("USER", userId);
                                MainActivity.activity.startActivity(launchinIntent);
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
