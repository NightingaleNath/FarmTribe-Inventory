package ideanity.oceans.farmertribe.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

import ideanity.oceans.farmertribe.R;
import ideanity.oceans.farmertribe.database.DatabaseHandler;

public class TrainSetupListActivity extends AppCompatActivity {

    ListView listView;
    ArrayAdapter adapter;
    ArrayList<String> s_id;
    ArrayList<String> topic;
    ArrayList<String> location;
    ArrayList<String> loadSet;

    Activity activity = this;
    public static DatabaseHandler handler;
    ImageView back, refresh;
    FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_train_setup_list);

        s_id = new ArrayList<>();
        topic = new ArrayList<>();
        location = new ArrayList<>();
        loadSet = new ArrayList<>();

        handler = new DatabaseHandler(this);

        listView = findViewById(R.id.setup_list);
        refresh = findViewById(R.id.refresh_setup);
        back = findViewById(R.id.back_setup_list);
        fab = findViewById(R.id.setup_fab);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TrainSetupListActivity.super.onBackPressed();
            }
        });

        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadSetup();
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent launchIntent = new Intent(activity, TrainSetupActivity.class);
                startActivity(launchIntent);
            }
        });

        loadSetup();

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog.Builder alert = new AlertDialog.Builder(activity);
                alert.setTitle("Delete ?");
                alert.setMessage("Do you want to delete this note ?");
                alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String qu = "DELETE FROM settings WHERE id = '" + s_id.get(position) + "'";
                        if (handler.execAction(qu)) {
                            Toast.makeText(activity, "Deleted", Toast.LENGTH_LONG).show();
                            loadSetup();
                        } else {

                            Toast.makeText(activity, "Failed", Toast.LENGTH_LONG).show();
                            loadSetup();
                        }
                        dialog.dismiss();
                    }
                });
                alert.setNegativeButton("No", null);
                alert.show();
                return true;
            }
        });

    }

    public void loadSetup() {
        loadSet.clear();
        s_id.clear();
        String qu = "SELECT * FROM settings";
        Cursor cursor = handler.execQuery(qu);
        if (cursor == null || cursor.getCount() == 0) {
            Toast.makeText(activity, "No SetUp have been added yet", Toast.LENGTH_LONG).show();
        } else {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                s_id.add(cursor.getString(0));
                topic.add(cursor.getString(1));
                location.add(cursor.getString(2));

                loadSet.add(cursor.getString(0) + "\t " + cursor.getString(1) + "\t " + cursor.getString(2));
                cursor.moveToNext();
            }
            adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, loadSet);
            listView.setAdapter(adapter);
        }
    }

}