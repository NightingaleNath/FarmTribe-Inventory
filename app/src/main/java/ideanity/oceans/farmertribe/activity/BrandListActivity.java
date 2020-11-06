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

public class BrandListActivity extends AppCompatActivity {

    ListView listView;
    ArrayAdapter adapter;
    ArrayList<String> b_id;
    ArrayList<String> brandx;
    ArrayList<String> brands;
    Activity activity = this;
    public static DatabaseHandler handler;
    ImageView back, refresh;
    FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_brand_list);


        brands = new ArrayList<>();
        brandx = new ArrayList<>();
        b_id = new ArrayList<>();
        handler = new DatabaseHandler(this);

        listView = findViewById(R.id.brand_list);
        refresh = findViewById(R.id.refresh_brand);
        back = findViewById(R.id.back_brand);
        fab = findViewById(R.id.brand_fab);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BrandListActivity.super.onBackPressed();
            }
        });

        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadBrand();
                exportExcel();
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent launchIntent = new Intent(activity, BrandActivity.class);
                startActivity(launchIntent);
            }
        });

        loadBrand();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                String brandUpdate = brands.get(position).toString();
                String brandId = b_id.get(position).toString();
                Intent intent = new Intent(getApplicationContext(), BrandUpdateActivity.class);
                intent.putExtra("b_id", brandId);
                intent.putExtra("brands", brandUpdate);
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
                        String qu = "DELETE FROM brand WHERE id = '" + b_id.get(position) + "' AND brandname = '"+brands.get(position)+"'";
                        if (handler.execAction(qu)) {
                            Toast.makeText(activity, "Deleted", Toast.LENGTH_LONG).show();
                            loadBrand();
                        } else {

                            Toast.makeText(activity, "Failed", Toast.LENGTH_LONG).show();
                            loadBrand();
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

    public void loadBrand() {
        brandx.clear();
        b_id.clear();
        String qu = "SELECT * FROM brand";
        Cursor cursor = handler.execQuery(qu);
        if (cursor == null || cursor.getCount() == 0) {
            Toast.makeText(activity, "No Brands have been added yet", Toast.LENGTH_LONG).show();
        } else {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                b_id.add(cursor.getString(0));
                brands.add(cursor.getString(1));
                brandx.add("Brand ID : " + cursor.getString(0) + "\n" + "Brand Name : " +  cursor.getString(1));
                cursor.moveToNext();
            }
            adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, brandx);
            listView.setAdapter(adapter);
        }
    }

    public void exportExcel(){
        // Exporting to excel
        File sd = Environment.getExternalStorageDirectory();
        String csvFile = "brand.xls";

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
            WritableSheet sheet = workbook.createSheet("brandList", 0);

            sheet.addCell(new Label(0, 0, "ID")); // column and row
            sheet.addCell(new Label(1, 0, "BRAND"));

            String qu = "SELECT * FROM brand";
            Cursor cursor = handler.execQuery(qu);
            if (cursor.moveToFirst()) {
                do {
                    String id = cursor.getString(0);
                    String brand = cursor.getString(1);

                    int i = cursor.getPosition() + 1;
                    sheet.addCell(new Label(0, i, id));
                    sheet.addCell(new Label(1, i, brand));
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
