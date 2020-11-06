package ideanity.oceans.farmertribe.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;

import ideanity.oceans.farmertribe.R;
import ideanity.oceans.farmertribe.adapter.ListAdapter;

public class AttendanceActivity extends AppCompatActivity {

    public static String time, period, topic, location, userid;
    ListView listView;
    ListAdapter adapter;
    ArrayAdapter<String> adapterSpinner;
    ArrayList<String> names;
    ArrayList<String> registers;
    Activity thisActivity = this;
    Spinner spinner;
    ImageView back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_attendance);

        time = getIntent().getStringExtra("DATE");
        period = getIntent().getStringExtra("PERIOD");
        topic = getIntent().getStringExtra("TOPIC");
        location = getIntent().getStringExtra("LOCATION");
        userid = getIntent().getStringExtra("USER");

        Log.d("Attendance", time + " -- " + period);
        listView = (ListView) findViewById(R.id.attendanceListViwe);
        names = new ArrayList<>();
        registers = new ArrayList<>();

        back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AttendanceActivity.super.onBackPressed();
            }
        });

        Button btnx = (Button) findViewById(R.id.buttonSaveAttendance);
        assert btnx != null;
        btnx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getBaseContext(), "Saving", Toast.LENGTH_LONG).show();
                adapter.saveAll();
            }
        });

        loadListView();
    }

    public void loadListView() {
        names.clear();
        registers.clear();
        String qu = "SELECT * FROM member ORDER BY id";
        Cursor cursor = DashboardActivity.handler.execQuery(qu);
        if (cursor == null || cursor.getCount() == 0) {
            Log.e("Attendance", "Null cursor");
        } else {
            int ctr = 0;
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                names.add(cursor.getString(1) + " (" + cursor.getInt(0) + ')');
                registers.add(cursor.getString(2));
                cursor.moveToNext();
                ctr++;
            }
            if (ctr == 0) {
                Toast.makeText(getBaseContext(), "No Students Found", Toast.LENGTH_LONG).show();
            }
            Log.d("Attendance", "Got " + ctr + " students");
        }
        adapter = new ListAdapter(thisActivity, names, registers);
        listView.setAdapter(adapter);
    }
}
