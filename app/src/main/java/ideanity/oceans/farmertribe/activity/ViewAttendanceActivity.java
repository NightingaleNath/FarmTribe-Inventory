package ideanity.oceans.farmertribe.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;

import ideanity.oceans.farmertribe.R;
import ideanity.oceans.farmertribe.database.DatabaseHandler;

public class ViewAttendanceActivity extends AppCompatActivity {

    private DatePickerDialog.OnDateSetListener mDateSetListener;
    private static final String TAG = "ViewAttendanceActivity";

    DatabaseHandler handler = DashboardActivity.handler;
    Activity activity = this;
    ListView listView;
    ArrayList<String> dates;
    ArrayList<Boolean> atts;
    ImageView back, individual;
    TextView editText;
    ArrayAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_view_attendance);

        dates = new ArrayList<>();
        atts = new ArrayList<>();

        back = findViewById(R.id.back_attend_rec);
        individual = findViewById(R.id.individual_prof);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewAttendanceActivity.super.onBackPressed();
            }
        });

        individual.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), IndividualRecordsActivity.class));
            }
        });

        listView = (ListView) findViewById(R.id.attend_list);

        editText = findViewById(R.id.editText);
        editText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        ViewAttendanceActivity.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        mDateSetListener,
                        year,month,day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });
        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;
                Log.d(TAG, "onDateSet: yyy/mm/dd: " + year + "-" + month + "-" + day);

                String date = year + "-" + month + "-" + day;
                editText.setText(date);
            }
        };

        Button findButton = (Button) findViewById(R.id.buttonFind);
        assert findButton != null;
        findButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                find(v);
            }
        });
    }

    private void find(View v) {
        dates.clear();
        atts.clear();

        String date = editText.getText().toString();
        String qu = "SELECT * FROM member";
        String qc = "SELECT * FROM ATTENDANCE WHERE datex = '" + date + "';";
        String qd = "SELECT * FROM ATTENDANCE WHERE datex = '" + date + "' AND isPresent = 1";
        Cursor cursor = handler.execQuery(qc);
        //Start Count Here

        int att = 0;
        Cursor cur = handler.execQuery(qc);
        Cursor cur1 = handler.execQuery(qd);
        if (cur == null) {
            Log.d("attendanceView", "cur null");
        }
        if (cur1 == null) {
            Log.d("attendanceView", "cur1 null");
        }

        if (cur != null && cur1 != null) {
            cur.moveToFirst();
            cur1.moveToFirst();
            try {
                att = cur1.getCount();
                if (att <= 0)
                    att = 0;
                Log.d("ViewAttendanceActivity", "Total = " + cur1.getCount() + " per " + att);
            } catch (Exception e) {
                att = -1;
            }
        }

        if (cursor == null || cursor.getCount() == 0){
            Toast.makeText(activity, "No Data Available", Toast.LENGTH_LONG).show();
        }
        else
        {
            String attendance = "";
            if (att < 0) {
                attendance = "Attendance Not Available";
            } else
                attendance = " Attendance : " + att;

            cursor.moveToFirst();
            String buffer = "";
            buffer += "Training Topic : " + cursor.getString(3) + "\n";
            buffer += "Training Location : " + cursor.getString(4) + "\n";
            buffer += "Member ID : " + cursor.getString(2) + "\n";
            buffer += "Staff : " + cursor.getString(5) + "\n";
            buffer += "" + attendance + "\n";

            adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, Collections.singletonList(buffer));
            listView.setAdapter(adapter);
        }
    }
}
