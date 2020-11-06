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

public class ProductListActivity extends AppCompatActivity {

    ListView listView;
    ArrayAdapter adapter;
    ArrayList<String> pcode;
    ArrayList<String> pdesc;
    ArrayList<String> pload;

    Activity activity = this;
    public static DatabaseHandler handler;
    ImageView back, refresh, export;
    FloatingActionButton fab;
    TextView tStock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_product_list);

        pcode = new ArrayList<>();
        pdesc = new ArrayList<>();
        pload = new ArrayList<>();

        handler = new DatabaseHandler(this);


        listView = findViewById(R.id.product_list);
        refresh = findViewById(R.id.refresh_product);
        export = findViewById(R.id.exportProduct);
        back = findViewById(R.id.back_product_list);
        fab = findViewById(R.id.product_fab);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProductListActivity.super.onBackPressed();
            }
        });

        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadProduct();
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
                Intent launchIntent = new Intent(activity, ProductActivity.class);
                startActivity(launchIntent);
            }
        });

        loadProduct();

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog.Builder alert = new AlertDialog.Builder(activity);
                alert.setTitle("Delete ?");
                alert.setMessage("Do you want to delete this Product ?");
                alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String qu = "DELETE FROM product WHERE pcode = '" + pcode.get(position) + "'";
                        if (handler.execAction(qu)) {
                            Toast.makeText(activity, "Deleted", Toast.LENGTH_LONG).show();
                            loadProduct();
                        } else {

                            Toast.makeText(activity, "Failed", Toast.LENGTH_LONG).show();
                            loadProduct();
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

    public void loadProduct() {
        pload.clear();
        pcode.clear();
        String qu = "SELECT * FROM product";
        Cursor cursor = handler.execQuery(qu);
        if (cursor == null || cursor.getCount() == 0) {
            Toast.makeText(activity, "No Product have been added yet", Toast.LENGTH_LONG).show();
        } else {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                pcode.add(cursor.getString(0));
                pdesc.add(cursor.getString(1));

                pload.add("Product Code : " + cursor.getString(0) + "\n" + "Product Name : " + cursor.getString(1) + "\n" + "Sale Price: GHȼ " + cursor.getString(5) + "\n" + "Re-Order (Qty) : " + cursor.getString(7)+ "\n" + "Vendor ID : " + cursor.getString(9) + "\n" + "Vendor Name : " + cursor.getString(10));
                cursor.moveToNext();
            }
            adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, pload);
            listView.setAdapter(adapter);
        }
    }

    public void exportExcel(){
        // Exporting to excel
        File sd = Environment.getExternalStorageDirectory();
        String csvFile = "product.xls";

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
            WritableSheet sheet = workbook.createSheet("productList", 0);

            sheet.addCell(new Label(0, 0, "#")); // column and row
            sheet.addCell(new Label(1, 0, "PCODE"));
            sheet.addCell(new Label(2, 0, "PRODUCT"));
            sheet.addCell(new Label(3, 0, "BRAND"));
            sheet.addCell(new Label(4, 0, "WEIGHT"));
            sheet.addCell(new Label(5, 0, "PURCHASE PRICE"));
            sheet.addCell(new Label(6, 0, "SALE PRICE"));
            sheet.addCell(new Label(7, 0, "QUANTITY"));
            sheet.addCell(new Label(8, 0, "REORDER"));
            sheet.addCell(new Label(9, 0, "VENDOR ID"));
            sheet.addCell(new Label(10, 0, "VENDOR NAME"));
            sheet.addCell(new Label(11, 0, "VENDOR NUMBER"));
            sheet.addCell(new Label(12, 0, "COMMUNITY"));

            String qu = "SELECT * FROM product";
            Cursor cursor = handler.execQuery(qu);
            if (cursor.moveToFirst()) {
                do {
                    String pcode = cursor.getString(0);
                    String pdes = cursor.getString(1);
                    String brandname = cursor.getString(2);
                    String weight = cursor.getString(3) + " KG";
                    String purchase = cursor.getString(4);
                    String price = cursor.getString(5);
                    String quantity = cursor.getString(6);
                    String reorder = cursor.getString(7);
                    String vendorId = cursor.getString(8);
                    String vendorName = cursor.getString(9);
                    String vendorphone = cursor.getString(10);
                    String community = cursor.getString(11);

                    int i = cursor.getPosition() + 1;
                    sheet.addCell(new Label(0, i, "#"));
                    sheet.addCell(new Label(1, i, pcode));
                    sheet.addCell(new Label(2, i, pdes));
                    sheet.addCell(new Label(3, i, brandname));
                    sheet.addCell(new Label(4, i, weight));
                    sheet.addCell(new Label(5, i, purchase));
                    sheet.addCell(new Label(6, i, price));
                    sheet.addCell(new Label(7, i, quantity));
                    sheet.addCell(new Label(8, i, reorder));
                    sheet.addCell(new Label(9, i, vendorId));
                    sheet.addCell(new Label(10, i, vendorName));
                    sheet.addCell(new Label(11, i, vendorphone));
                    sheet.addCell(new Label(12, i, community));

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
