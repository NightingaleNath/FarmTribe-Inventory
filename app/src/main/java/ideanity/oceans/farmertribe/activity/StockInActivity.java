package ideanity.oceans.farmertribe.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
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
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;
import java.util.Calendar;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.Random;

import ideanity.oceans.farmertribe.R;
import ideanity.oceans.farmertribe.database.DatabaseHandler;

public class StockInActivity extends AppCompatActivity {

    Activity activity = this;

    public static DatabaseHandler handler;

    private DatePickerDialog.OnDateSetListener mDateSetListener;
    private static final String TAG = "StockInActivity";

    Random number;
    int rnum;
    public static String userid;
    ImageView back;
    public static Button brandReg, pick, loadProduct;
    TextInputLayout pcode;
    TextInputLayout stockdate;
    TextInputLayout stockby;
    TextInputLayout ref;
    TextInputLayout qty;
    String status;
    Spinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_stock_in);

        handler = new DatabaseHandler(this);

        userid = getIntent().getStringExtra("USER");

        back = findViewById(R.id.back_stock);

        ref = findViewById(R.id.genRef);
        stockby = findViewById(R.id.stockby);
        stockdate = findViewById(R.id.date);
        pcode = findViewById(R.id.product_code);
        qty = findViewById(R.id.qty);
        spinner = findViewById(R.id.productSpinner);

        stockby.getEditText().setText(userid);

        stockdate.getEditText().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        StockInActivity.this,
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
                stockdate.getEditText().setText(date);
            }
        };

        String qu = "SELECT * FROM product";
        ArrayList<String> subs = new ArrayList<>();
        subs.add("Select Product");
        Cursor cr = handler.execQuery(qu);
        if (cr != null) {
            cr.moveToFirst();
            while (!cr.isAfterLast()) {
                subs.add(cr.getString(1));

                cr.moveToNext();
            }
        } else
            Log.d("StockInActivity.class", "No SUBS" + cr.getString(1));

        ArrayAdapter<String> adapterSpinner = new ArrayAdapter<String>(activity, android.R.layout.simple_spinner_dropdown_item, subs);
        assert spinner != null;
        spinner.setAdapter(adapterSpinner);

        brandReg = findViewById(R.id.btn_add_stock);
        pick = findViewById(R.id.generate);
        loadProduct = findViewById(R.id.loadProduct);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StockInActivity.super.onBackPressed();
            }
        });

        brandReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveData();
            }
        });

        pick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                generate();

            }
        });

        loadProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String qu = "SELECT * FROM product WHERE pdes = '" + spinner.getSelectedItem().toString() + "' ORDER BY pcode";

                Cursor cursor = handler.execQuery(qu);
                if (cursor == null || cursor.getCount() == 0) {
                    Log.e("Product", "Null cursor");
                }else {
                    cursor.moveToFirst();
                    pcode.getEditText().setText(cursor.getString(0));
                    qty.getEditText().setText(cursor.getString(6));
                }
            }
        });
    }

    private void generate() {
        number = new Random();
        rnum = number.nextInt(10000);
        String code = String.valueOf(rnum);
        ref.getEditText().setText(code);
    }


    private void saveData() {
        try {

            String reference = ref.getEditText().getText().toString().trim();
            String stockInby = stockby.getEditText().getText().toString().trim();
            String date = stockdate.getEditText().getText().toString().trim();
            String codep = pcode.getEditText().getText().toString().trim();
            String quant = qty.getEditText().getText().toString().trim();
            String prod = spinner.getSelectedItem().toString();
            status = "Done";

            if (reference.isEmpty()) {
                AlertDialog.Builder alert = new AlertDialog.Builder(StockInActivity.this);
                alert.setTitle("Reference Number");
                alert.setMessage("Reference can not be empty");
                alert.setPositiveButton("OK", null);
                alert.show();
                return;
            }
            else if (stockInby.isEmpty()) {
                AlertDialog.Builder alert = new AlertDialog.Builder(StockInActivity.this);
                alert.setTitle("Stock By");
                alert.setMessage("StockIn By can not be empty");
                alert.setPositiveButton("OK", null);
                alert.show();
                return;
            }
            else if (date.isEmpty()) {
                AlertDialog.Builder alert = new AlertDialog.Builder(StockInActivity.this);
                alert.setTitle("Stock Date");
                alert.setMessage("Date can not be empty");
                alert.setPositiveButton("OK", null);
                alert.show();
                return;
            }
            else if (codep.isEmpty()) {
                AlertDialog.Builder alert = new AlertDialog.Builder(StockInActivity.this);
                alert.setTitle("Product Code");
                alert.setMessage("Code can not be empty");
                alert.setPositiveButton("OK", null);
                alert.show();
                return;
            }
            else {
                    try {
                        Cursor cursor = handler.execQuery("SELECT * FROM stockIn WHERE pcode = '" + codep + "';");
                        if (cursor == null || cursor.getCount() == 0)
                        {
                            String qb = " INSERT INTO stockIn(refno, pcode, pdesc, qty, sdate, stockinby, status) VALUES('" + reference + "','" + codep + "','" + prod + "','" + quant + "','" + date + "','" + stockInby + "','" + status + "')";
                            if (handler.execAction(qb)){
                                Toast.makeText(activity, "Product Stocked", Toast.LENGTH_LONG).show();

                                 stockdate.getEditText().setText("");
                                 pcode.getEditText().setText("");
                                 stockby.getEditText().setText("");
                                 ref.getEditText().setText("");
                                 qty.getEditText().setText("");
                            }
                        }else {
                            Toast.makeText(activity, "Product Already exist", Toast.LENGTH_LONG).show();
                            String qp = "UPDATE stockIn SET qty = '"+quant+"' WHERE pcode = '"+codep+"'";
                            String qs = "UPDATE product SET qty = '"+quant+"' WHERE pcode = '"+codep+"'";
                            handler.execAction(qp);
                            handler.execAction(qs);

                            stockdate.getEditText().setText("");
                            pcode.getEditText().setText("");
                            stockby.getEditText().setText("");
                            ref.getEditText().setText("");
                            qty.getEditText().setText("");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }


            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
