package ideanity.oceans.farmertribe.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import ideanity.oceans.farmertribe.R;
import ideanity.oceans.farmertribe.adapter.ProfileAdapter;
import ideanity.oceans.farmertribe.database.DatabaseHandler;

public class IndividualRecordsActivity extends AppCompatActivity {

    DatabaseHandler handler = DashboardActivity.handler;
    Activity IndividualRecordsActivity = this;
    ListView listView;
    ProfileAdapter adapter;
    ArrayList<String> dates;
    ArrayList<String> datesALONE;
    ArrayList<Integer> hourALONE;
    ArrayList<Boolean> atts;
    Activity activity = this;
    private View v;
    ImageView back;
    TextView editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_individual_records);

        dates = new ArrayList<>();
        datesALONE = new ArrayList<>();
        hourALONE = new ArrayList<>();
        atts = new ArrayList<>();

        back = findViewById(R.id.back_individual);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IndividualRecordsActivity.super.onBackPressed();
            }
        });

        listView = (ListView) findViewById(R.id.indivi_list);
        TextView textView = (TextView) findViewById(R.id.profileContentView);
        assert textView != null;
        textView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(activity);
                alert.setTitle("Delete Student");
                alert.setMessage("Are you sure ?");
                alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        EditText editText = (EditText) findViewById(R.id.editTextSearch);
                        String regno = editText.getText().toString();
                            String qa = "DELETE FROM ATTENDANCE WHERE farmerId = '" + regno.toUpperCase() + "'";
                            if (DashboardActivity.handler.execAction(qa)) {
                                Toast.makeText(getBaseContext(), "Deleted", Toast.LENGTH_LONG).show();
                                Log.d("delete", "done from attendance");
                            }
                    }
                });
                alert.setNegativeButton("No", null);
                alert.show();
                return true;
            }
        });

        Button findButton = (Button) findViewById(R.id.buttonSearch);
        assert findButton != null;
        findButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                find(v);
            }
        });
    }

    public void find(View view) {
        dates.clear();
        atts.clear();
        EditText editText = (EditText) findViewById(R.id.editTextSearch);
        TextView textView = (TextView) findViewById(R.id.profileContentView);
        String reg = editText.getText().toString();
        String qu = "SELECT * FROM member WHERE farmerId = '" + reg.toUpperCase() + "'";
        String qc = "SELECT * FROM ATTENDANCE WHERE farmerId = '" + reg.toUpperCase() + "';";
        String qd = "SELECT * FROM ATTENDANCE WHERE farmerId = '" + reg.toUpperCase() + "' AND isPresent = 1";
        Cursor cursor = handler.execQuery(qu);
        //Start Count Here

        float att = 0f;
        Cursor cur = handler.execQuery(qc);
        Cursor cur1 = handler.execQuery(qd);
        if (cur == null) {
            Log.d("profile", "cur null");
        }
        if (cur1 == null) {
            Log.d("profile", "cur1 null");
        }
        if (cur != null && cur1 != null) {
            cur.moveToFirst();
            cur1.moveToFirst();
            try {
                att = ((float) cur1.getCount() / cur.getCount()) * 100;
                if (att <= 0)
                    att = 0f;
                Log.d("IndividualRecords", "Total = " + cur.getCount() + " avail = " + cur1.getCount() + " per " + att);
            } catch (Exception e) {
                att = -1;
            }
        }


        if (cursor == null || cursor.getCount() == 0) {
            assert textView != null;
            textView.setText("No Data Available");
        } else {
            String attendance = "";
            if (att < 0) {
                attendance = "Attendance Not Available";
            } else
                attendance = " Attendance " + att + " %";
            cursor.moveToFirst();
            String buffer = "";
            buffer += " " + cursor.getString(1) + "\n";
            buffer += " " + cursor.getString(2) + "\n";
            buffer += " " + cursor.getString(3) + "\n";
            buffer += " " + cursor.getString(6) + "\n";
            buffer += " " + attendance + "\n";
            textView.setText(buffer);

            String q = "SELECT * FROM ATTENDANCE WHERE farmerId = '" + editText.getText().toString().toUpperCase() + "'";
            Cursor cursorx = handler.execQuery(q);
            if (cursorx == null || cursorx.getCount() == 0) {
                Toast.makeText(getBaseContext(), "No Attendance Info Available", Toast.LENGTH_LONG).show();
            } else {
                cursorx.moveToFirst();
                while (!cursorx.isAfterLast()) {
                    datesALONE.add(cursorx.getString(0));
                    hourALONE.add(cursorx.getInt(1));
                    dates.add(cursorx.getString(0) + " :" + cursorx.getInt(1) + " th Hour");
                    if (cursorx.getInt(6) == 1)
                        atts.add(true);
                    else {
                        Log.d("profile", cursorx.getString(0) + " -> " + cursorx.getInt(3));
                        atts.add(false);
                    }
                    cursorx.moveToNext();
                }
                adapter = new ProfileAdapter(dates, atts, IndividualRecordsActivity, datesALONE, hourALONE, editText.getText().toString().toUpperCase());
                listView.setAdapter(adapter);
            }
        }
    }

}
