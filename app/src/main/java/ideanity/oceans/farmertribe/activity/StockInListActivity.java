package ideanity.oceans.farmertribe.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

import java.io.File;
import java.util.Locale;

import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

import ideanity.oceans.farmertribe.R;
import ideanity.oceans.farmertribe.database.DatabaseHandler;

public class StockInListActivity extends AppCompatActivity {

    ListView listView;
    ArrayAdapter adapter;
    ArrayList<String> s_id;
    ArrayList<String> sload;
    public static String userid;

    Activity activity = this;
    public static DatabaseHandler handler;
    ImageView back, refresh, export;
    FloatingActionButton fab;
    TextView tStock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_stock_in_list);

        s_id = new ArrayList<>();
        sload = new ArrayList<>();

        handler = new DatabaseHandler(this);

        userid = getIntent().getStringExtra("USER");

        listView = findViewById(R.id.stock_list);
        refresh = findViewById(R.id.refresh_stock);
        export = findViewById(R.id.exportStock);
        back = findViewById(R.id.back_stock_list);
        fab = findViewById(R.id.stock_fab);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StockInListActivity.super.onBackPressed();
            }
        });

        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadStock();
            }
        });

        export.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exportExcel();
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent launchIntent = new Intent(activity, StockInActivity.class);
                launchIntent.putExtra("USER", userid);
                startActivity(launchIntent);
            }
        });

        loadStock();

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog.Builder alert = new AlertDialog.Builder(activity);
                alert.setTitle("Delete ?");
                alert.setMessage("Do you want to delete this Stock ?");
                alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String qu = "DELETE FROM stockIn WHERE id = '" + s_id.get(position) + "'";
                        if (handler.execAction(qu)) {
                            Toast.makeText(activity, "Deleted", Toast.LENGTH_LONG).show();
                            loadStock();
                        } else {

                            Toast.makeText(activity, "Failed", Toast.LENGTH_LONG).show();
                            loadStock();
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

    private void loadStock() {
        sload.clear();
        s_id.clear();
        String qu = "SELECT * FROM stockIn";
        Cursor cursor = handler.execQuery(qu);
        if (cursor == null || cursor.getCount() == 0) {
            Toast.makeText(activity, "No Stocked product yet", Toast.LENGTH_LONG).show();
        } else {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                s_id.add(cursor.getString(0));
                sload.add("Stocked ID : " + cursor.getString(0) + "\n" + "Reference # : " + cursor.getString(1) + "\n" + "Product Code : " + cursor.getString(2) + "\n" + "Prouct : " + cursor.getString(3) + "\n" + "Quantity : " + cursor.getString(4) + "\n" + "Stocked Date : " + cursor.getString(5) + "\n" + "Stocked By : " + cursor.getString(6) + "\n" + "Status : " + cursor.getString(7));
                cursor.moveToNext();
            }
            adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, sload);
            listView.setAdapter(adapter);
        }
    }

    public void exportExcel() {
        // Exporting to excel
        File sd = Environment.getExternalStorageDirectory();
        String csvFile = "stock.xls";

        File directory = new File(sd.getAbsolutePath());
        //create directory if not exist
        if (!directory.isDirectory()) {
            directory.mkdirs();
        }
        try {
            //file path
            File file = new File(directory, csvFile);
            WorkbookSettings wbSettings = new WorkbookSettings();
            wbSettings.setLocale(new Locale("en", "EN"));
            WritableWorkbook workbook;
            workbook = Workbook.createWorkbook(file, wbSettings);
            //Excel sheet name. 0 represents first sheet
            WritableSheet sheet = workbook.createSheet("stockList", 0);

            sheet.addCell(new Label(0, 0, "#")); // column and row
            sheet.addCell(new Label(1, 0, "ID"));
            sheet.addCell(new Label(2, 0, "REFERENCE #"));
            sheet.addCell(new Label(3, 0, "PCODE"));
            sheet.addCell(new Label(4, 0, "PRODUCT"));
            sheet.addCell(new Label(5, 0, "QUANTITY"));
            sheet.addCell(new Label(6, 0, "STOCK DATE"));
            sheet.addCell(new Label(7, 0, "STOCKIN BY"));
            sheet.addCell(new Label(8, 0, "STATUS"));

            String qu = "SELECT * FROM stockIn";
            Cursor cursor = handler.execQuery(qu);
            if (cursor.moveToFirst()) {
                do {
                    String id = cursor.getString(0);
                    String refno = cursor.getString(1);
                    String pcode = cursor.getString(2);
                    String product = cursor.getString(3);
                    String quant = cursor.getString(4);
                    String stockdate = cursor.getString(5);
                    String stockby = cursor.getString(6);
                    String status = cursor.getString(7);

                    int i = cursor.getPosition() + 1;
                    sheet.addCell(new Label(0, i, id));
                    sheet.addCell(new Label(1, i, id));
                    sheet.addCell(new Label(2, i, refno));
                    sheet.addCell(new Label(3, i, pcode));
                    sheet.addCell(new Label(4, i, product));
                    sheet.addCell(new Label(5, i, quant));
                    sheet.addCell(new Label(6, i, stockdate));
                    sheet.addCell(new Label(7, i, stockby));
                    sheet.addCell(new Label(8, i, status));

                } while (cursor.moveToNext());
            }

            //closing cursor
            cursor.close();
            workbook.write();
            workbook.close();
            Toast.makeText(getApplication(), "Data Exported in a Excel Sheet", Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
