package ideanity.oceans.farmertribe.activity;

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

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

import ideanity.oceans.farmertribe.R;
import ideanity.oceans.farmertribe.database.DatabaseHandler;

public class MemberListActivity extends AppCompatActivity {

    ListView listView;
    ArrayAdapter adapter;
    ArrayList<String> m_id;
    ArrayList<String> farmName;
    ArrayList<String> farmId;
    ArrayList<String> farmCom;
    ArrayList<String> farmAge;
    ArrayList<String> farmGender;
    ArrayList<String> farmPhone;
    ArrayList<String> farmLoad;

    Activity activity = this;
    public static DatabaseHandler handler;
    ImageView back, refresh;
    FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_member_list);

        m_id = new ArrayList<>();
        farmName = new ArrayList<>();
        farmId = new ArrayList<>();
        farmCom = new ArrayList<>();
        farmAge = new ArrayList<>();
        farmGender = new ArrayList<>();
        farmPhone = new ArrayList<>();
        farmLoad = new ArrayList<>();

        handler = new DatabaseHandler(this);

        listView = findViewById(R.id.member_list);
        refresh = findViewById(R.id.refresh_member);
        back = findViewById(R.id.back_member_list);
        fab = findViewById(R.id.member_fab);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MemberListActivity.super.onBackPressed();
            }
        });

        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadMember();
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent launchIntent = new Intent(activity, MemberActivity.class);
                startActivity(launchIntent);
            }
        });

        loadMember();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                String f_id = m_id.get(position).toString();
                String farmerName = farmName.get(position).toString();
                String farmerId = farmId.get(position).toString();
                String comm = farmCom.get(position).toString();
                String age = farmAge.get(position).toString();
                String gender = farmGender.get(position).toString();
                String phone = farmPhone.get(position).toString();

                Intent intent = new Intent(getApplicationContext(), BrandUpdateActivity.class);
                intent.putExtra("m_id", f_id);
                intent.putExtra("farmName", farmerName);
                intent.putExtra("farmId", farmerId);
                intent.putExtra("farmCom", comm);
                intent.putExtra("farmAge", age);
                intent.putExtra("farmGender", gender);
                intent.putExtra("farmPhone", phone);

                startActivity(intent);
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog.Builder alert = new AlertDialog.Builder(activity);
                alert.setTitle("Delete ?");
                alert.setMessage("Do you want to delete this note ?");
                alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String qu = "DELETE FROM member WHERE id = '" + m_id.get(position) + "'";
                        if (handler.execAction(qu)) {
                            Toast.makeText(activity, "Deleted", Toast.LENGTH_LONG).show();
                            loadMember();
                        } else {

                            Toast.makeText(activity, "Failed", Toast.LENGTH_LONG).show();
                            loadMember();
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

    public void loadMember() {
        farmLoad.clear();
        m_id.clear();
        String qu = "SELECT * FROM member";
        Cursor cursor = handler.execQuery(qu);
        if (cursor == null || cursor.getCount() == 0) {
            Toast.makeText(activity, "No Member have been added yet", Toast.LENGTH_LONG).show();
        } else {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                m_id.add(cursor.getString(0));
                farmName.add(cursor.getString(1));
                farmId.add(cursor.getString(2));
                farmCom.add(cursor.getString(3));
                farmAge.add(cursor.getString(4));
                farmGender.add(cursor.getString(5));
                farmPhone.add(cursor.getString(6));

                farmLoad.add("Member ID : " + cursor.getString(2) + "\n" + "Member Name : " + cursor.getString(1) + "\n" + "Community : " + cursor.getString(3)+ "\n" + "Phone Number : " + cursor.getString(6));
                cursor.moveToNext();
            }
            adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, farmLoad);
            listView.setAdapter(adapter);
        }
    }

}
