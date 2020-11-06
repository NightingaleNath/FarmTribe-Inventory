package ideanity.oceans.farmertribe.adapter;

import android.app.Activity;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.ArrayList;

import ideanity.oceans.farmertribe.R;
import ideanity.oceans.farmertribe.activity.AttendanceActivity;
import ideanity.oceans.farmertribe.activity.DashboardActivity;
import ideanity.oceans.farmertribe.activity.MainActivity;

public class ListAdapter extends BaseAdapter {

    ArrayList<String> nameList;
    ArrayList<String> registers;
    Activity activity;

    ArrayList<Boolean> attendanceList;

    public ListAdapter(Activity activity, ArrayList<String> nameList, ArrayList<String> reg) {
        this.nameList = nameList;
        this.activity = activity;
        this.registers = reg;
        attendanceList = new ArrayList<>();
        for (int i = 0; i < nameList.size(); i++) {
            attendanceList.add(new Boolean(true));
        }
    }

    @Override
    public int getCount() {
        return nameList.size();
    }

    @Override
    public Object getItem(int position) {
        return nameList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View v, ViewGroup parent) {
        if (v == null) {
            LayoutInflater vi = LayoutInflater.from(activity);
            v = vi.inflate(R.layout.list_ele, null);
        }
        final int pos = position;
        TextView textView = (TextView) v.findViewById(R.id.attendanceName);
        textView.setText(nameList.get(position));
        final CheckBox checkBox = (CheckBox) v.findViewById(R.id.attMarker);
        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attendanceList.set(pos, checkBox.isChecked());
                Log.d("Attendance", nameList.get(position).toString() + " is absent " + attendanceList.get(position));
            }
        });
        return v;
    }

    public void saveAll() {
        for (int i = 0; i < nameList.size(); i++) {
            int sts = 1;
            if (attendanceList.get(i))
                sts = 1;
            else sts = 0;

            String qu = "INSERT INTO ATTENDANCE(datex,hour,farmerId,topic,location,userid,isPresent) VALUES('" + AttendanceActivity.time + "', '" + AttendanceActivity.period + "' ,'" + registers.get(i) + "', '" + AttendanceActivity.topic + "', '" + AttendanceActivity.location + "', '" + AttendanceActivity.userid + "', '" + sts + "')";
            DashboardActivity.handler.execAction(qu);
            activity.finish();
        }
    }
}
