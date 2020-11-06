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

public class LoadProduct extends AppCompatActivity {

    ListView listView;
    ArrayAdapter adapter;
    ArrayList<String> c_id;
    ArrayList<String> qty;
    ArrayList<String> pcode;
    ArrayList<String> pload;

    Activity activity = this;
    public static DatabaseHandler handler;
    ImageView back, refresh, export;
    TextView tSale;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_load_product);

        c_id = new ArrayList<>();
        qty = new ArrayList<>();
        pcode = new ArrayList<>();
        pload = new ArrayList<>();

        handler = new DatabaseHandler(this);


        listView = findViewById(R.id.brand_load_pro);
        back = findViewById(R.id.back_load_pro);
        refresh = findViewById(R.id.refresh_load_pro);
        export = findViewById(R.id.exportSale);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoadProduct.super.onBackPressed();
            }
        });

        export.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exportExcel();
            }
        });

        loadProducts();

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog.Builder alert = new AlertDialog.Builder(activity);
                alert.setTitle("Delete ?");
                alert.setMessage("Do you want to delete this Product ?");
                alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String qu = "DELETE FROM cart WHERE pcode = '" + pcode.get(position) + "'";
                        if (handler.execAction(qu)) {
                            String qs = "UPDATE product SET qty = qty + '" + qty.get(position) + "' WHERE pcode = '" + pcode.get(position) + "'";
                            String qp = "UPDATE stockIn SET qty = qty + '" + qty.get(position) + "' WHERE pcode = '" + pcode.get(position) + "'";
                            handler.execAction(qs);
                            handler.execAction(qp);

                            Toast.makeText(activity, "Deleted", Toast.LENGTH_LONG).show();
                            loadProducts();
                        } else {

                            Toast.makeText(activity, "Failed", Toast.LENGTH_LONG).show();
                            loadProducts();
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

    private void loadProducts() {
        pload.clear();
        c_id.clear();
        String qu = "SELECT * FROM cart";
        Cursor cursor = handler.execQuery(qu);
        if (cursor == null || cursor.getCount() == 0) {
            Toast.makeText(activity, "No Selling have been made yet", Toast.LENGTH_LONG).show();
        } else {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                c_id.add(cursor.getString(0));
                qty.add(cursor.getString(5));
                pload.add("Invoice # : " + cursor.getString(1) + "\n" + "Product Code : " + cursor.getString(2) + "\n" + "Product : " + cursor.getString(3) + "\n" + "Price : GHȼ" + cursor.getString(4) + "\n" + "Quantity : " + cursor.getString(5) + "\n" + "Discount : " + cursor.getString(6) + "\n" + "Total : GHȼ" + cursor.getString(7) + "\n" + "Customer ID: " + cursor.getString(8) + "\n" + "Customer Name : " + cursor.getString(9) + "\n" + "Community : " + cursor.getString(10));
                cursor.moveToNext();
            }
            adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, pload);
            listView.setAdapter(adapter);
        }
    }

    public void exportExcel(){
        // Exporting to excel
        File sd = Environment.getExternalStorageDirectory();
        String csvFile = "cart.xls";

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
            WritableSheet sheet = workbook.createSheet("cartList", 0);

            sheet.addCell(new Label(0, 0, "#")); // column and row
            sheet.addCell(new Label(1, 0, "ID"));
            sheet.addCell(new Label(2, 0, "TRANSACTION ID"));
            sheet.addCell(new Label(3, 0, "PCODE"));
            sheet.addCell(new Label(4, 0, "PRODUCT"));
            sheet.addCell(new Label(5, 0, "SALE PRICE"));
            sheet.addCell(new Label(6, 0, "QUANTITY"));
            sheet.addCell(new Label(7, 0, "DISCOUNT"));
            sheet.addCell(new Label(8, 0, "TOTAL"));
            sheet.addCell(new Label(9, 0, "VENDOR ID"));
            sheet.addCell(new Label(10, 0, "VENDOR NAME"));
            sheet.addCell(new Label(11, 0, "COMMUNITY"));
            sheet.addCell(new Label(12, 0, "SALE DATE"));
            sheet.addCell(new Label(13, 0, "SALE PERSON"));
            sheet.addCell(new Label(14, 0, "STATUS"));

            String qu = "SELECT * FROM cart";
            Cursor cursor = handler.execQuery(qu);
            if (cursor.moveToFirst()) {
                do {
                    String id = cursor.getString(0);
                    String trans = cursor.getString(1);
                    String pcode = cursor.getString(2);
                    String pdes = cursor.getString(3);
                    String saleprice = cursor.getString(4);
                    String quant = cursor.getString(5);
                    String discount = cursor.getString(6);
                    String total = cursor.getString(7);
                    String vendorId = cursor.getString(8);
                    String vendorName = cursor.getString(9);
                    String community = cursor.getString(10);
                    String saledate = cursor.getString(11);
                    String saleperson = cursor.getString(12);
                    String status = cursor.getString(13);

                    int i = cursor.getPosition() + 1;
                    sheet.addCell(new Label(0, i, id));
                    sheet.addCell(new Label(1, i, id));
                    sheet.addCell(new Label(2, i, trans));
                    sheet.addCell(new Label(3, i, pcode));
                    sheet.addCell(new Label(4, i, pdes));
                    sheet.addCell(new Label(5, i, saleprice));
                    sheet.addCell(new Label(6, i, quant));
                    sheet.addCell(new Label(7, i, discount));
                    sheet.addCell(new Label(8, i, total));
                    sheet.addCell(new Label(9, i, vendorId));
                    sheet.addCell(new Label(10, i, vendorName));
                    sheet.addCell(new Label(11, i, community));
                    sheet.addCell(new Label(12, i, saledate));
                    sheet.addCell(new Label(13, i, saleperson));
                    sheet.addCell(new Label(14, i, status));

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
