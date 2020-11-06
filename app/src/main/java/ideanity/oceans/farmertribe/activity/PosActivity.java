package ideanity.oceans.farmertribe.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Objects;

import ideanity.oceans.farmertribe.R;
import ideanity.oceans.farmertribe.database.DatabaseHandler;

public class PosActivity extends AppCompatActivity {

    private ArrayList<String> data = new ArrayList<String>();
    private ArrayList<String> data1 = new ArrayList<String>();
    private ArrayList<String> data2 = new ArrayList<String>();
    private ArrayList<String> data3 = new ArrayList<String>();
    private ArrayList<String> data4 = new ArrayList<String>();

    Activity activity = this;

    public static DatabaseHandler handler;

    public static String userid;
    private TableLayout table;
    TextInputLayout dateTrans;
    TextInputLayout pcodeTrans;
    TextInputLayout customerName;
    TextInputLayout customerId;
    TextInputLayout customerPlace;
    TextInputLayout pTotal;
    TextView salePers;
    TextInputLayout priceSale;

    TextInputLayout product, quant, price, disc, totalSale, balance, payment;
    Spinner spinner, spinerProduct;
    ImageView back;
    EditText transact;
    public static Button add, finalized, clear, loadGen, loadForEdit, loadProduct;
    String QTY;
    String status;
    TableRow sale_row;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_pos);

        handler = new DatabaseHandler(this);

        userid = getIntent().getStringExtra("USER");
        salePers = findViewById(R.id.salePerson);
        salePers.setText(userid);

        back = findViewById(R.id.back_sale);

        product = findViewById(R.id.product_sale);
        quant = findViewById(R.id.qty_sale);
        price = findViewById(R.id.price_sale);
        disc = findViewById(R.id.discount);
        totalSale = findViewById(R.id.total);
        balance = findViewById(R.id.balance);
        payment = findViewById(R.id.payment);
        transact = findViewById(R.id.trans_no);
        dateTrans = findViewById(R.id.date_trans);
        pcodeTrans = findViewById(R.id.pcode_trans);
        customerName = findViewById(R.id.customer_name);
        customerId = findViewById(R.id.customer_Id);
        customerPlace = findViewById(R.id.customer_place);
        pTotal = findViewById(R.id.p_total);

        add = findViewById(R.id.btn_add);
        finalized = findViewById(R.id.btn_final);
        clear = findViewById(R.id.btn_clear);
        loadGen = findViewById(R.id.loadGen);
        loadForEdit = findViewById(R.id.loadCust);
        loadProduct = findViewById(R.id.load_Product);

        table = findViewById(R.id.sale_table);

        spinerProduct = findViewById(R.id.productSpineer);

        spinner = findViewById(R.id.custSpinner);
        String qu = "SELECT * FROM member ORDER BY id";
        ArrayList<String> subs = new ArrayList<>();
        subs.add("Select Name to Load Details");
        Cursor cr = handler.execQuery(qu);
        if (cr != null) {
            cr.moveToFirst();
            while (!cr.isAfterLast()) {
                subs.add(cr.getString(1));
                cr.moveToNext();
            }
        } else
            Log.d("PosActivity.class", "No SUBS" + cr.getString(1));

        ArrayAdapter<String> adapterSpinner = new ArrayAdapter<String>(activity, android.R.layout.simple_spinner_dropdown_item, subs);
        assert spinner != null;
        spinner.setAdapter(adapterSpinner);

        disc.getEditText().setText("0");

        loadForEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String qd = "SELECT * FROM member WHERE farmerName = '" + spinner.getSelectedItem().toString() + "'";
                Cursor cr = handler.execQuery(qd);
                if (cr != null || cr != null) {
                    cr.moveToFirst();
                    try {
                        customerId.getEditText().setText(cr.getString(2));
                        customerName.getEditText().setText(cr.getString(1));
                        customerPlace.getEditText().setText(cr.getString(3));
                    } catch (Exception e) {
                    }
                } else {
                    Toast.makeText(getBaseContext(), "No Such Member", Toast.LENGTH_LONG).show();
                }
            }
        });

        long date = System.currentTimeMillis();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        String dateString = sdf.format(date);
        dateTrans.getEditText().setText(dateString);

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveData();
            }
        });

        loadGen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GetTransactionNo();
            }
        });

        sale_row = new TableRow(PosActivity.this);
        finalized.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (payment.getEditText().getText().toString().isEmpty()) {
                    Toast.makeText(activity, "PLEASE MAKE PAYMENT FIRST", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    table.removeView(sale_row);
                    quant.getEditText().setText("");
                    disc.getEditText().setText("0");
                    price.getEditText().setText("");
                    pTotal.getEditText().setText("0.00");
                    totalSale.getEditText().setText("0.00");
                    pcodeTrans.getEditText().setText("");
                    customerName.getEditText().setText("");
                    customerId.getEditText().setText("");
                    customerPlace.getEditText().setText("");
                    GetTransactionNo();
                    clear.setClickable(true);
                }
            }
        });

        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                table.removeView(sale_row);
                quant.getEditText().setText("");
                disc.getEditText().setText("0");
                price.getEditText().setText("");
                pTotal.getEditText().setText("0.00");
                pcodeTrans.getEditText().setText("");
                customerName.getEditText().setText("");
                customerId.getEditText().setText("");
                customerPlace.getEditText().setText("");
                totalSale.getEditText().setText("0.00");
                finish();
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PosActivity.super.onBackPressed();
            }
        });

        // Load Products for sale
        String pro = "SELECT * FROM product";
        ArrayList<String> subpr = new ArrayList<>();
        subpr.add("Select Item to Buy");
        Cursor cursor = handler.execQuery(pro);
        if (cursor != null) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                subpr.add(cursor.getString(1));
                cursor.moveToNext();
            }
        } else
            Log.d("PosActivity.class", "No SUBS" + cursor.getString(1));

        ArrayAdapter<String> adapters = new ArrayAdapter<String>(activity, android.R.layout.simple_spinner_dropdown_item, subpr);
        assert spinerProduct != null;
        spinerProduct.setAdapter(adapters);

        loadProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String cust = customerName.getEditText().getText().toString();
                if (cust.isEmpty()) {
                    android.app.AlertDialog.Builder alert = new AlertDialog.Builder(PosActivity.this);
                    alert.setTitle("Customer Name");
                    alert.setMessage("Customer Name can not be empty");
                    alert.setPositiveButton("OK", null);
                    alert.show();
                    return;
                }

                String sel = "SELECT * FROM product WHERE pdes = '" + spinerProduct.getSelectedItem().toString() + "'";
                Cursor cr1 = handler.execQuery(sel);
                if (cr1 != null || cr1.getCount() != 0) {
                    cr1.moveToFirst();
                    try {
                        pcodeTrans.getEditText().setText(cr1.getString(0));
                        product.getEditText().setText(cr1.getString(1));
                        price.getEditText().setText(cr1.getString(5));
                        QTY = cr1.getString(6);

                    } catch (Exception e) {
                    }
                } else {
                    Toast.makeText(activity, "No Such Member", Toast.LENGTH_LONG).show();
                }
            }
        });

        quant.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    String qty = quant.getEditText().getText().toString();
                    String ds = disc.getEditText().getText().toString();
                    String perPrice = price.getEditText().getText().toString();

                    if (qty.isEmpty()) {
                        pTotal.getEditText().setText("0.0");
                    } else if (ds.isEmpty()) {
                        disc.getEditText().setText("0");
                    } else {

                        double total = (Double.parseDouble(qty) * Double.parseDouble(perPrice));
                        double d = (total * Double.parseDouble(ds)) / 100;
                        double totalAmount = (total) - d;
                        pTotal.getEditText().setText("" + totalAmount);

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        disc.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    String qty = quant.getEditText().getText().toString();
                    String ds = disc.getEditText().getText().toString();
                    String perPrice = price.getEditText().getText().toString();

                    if (qty.isEmpty()) {
                        pTotal.getEditText().setText("0.0");
                    } else if (ds.isEmpty()) {
                        disc.getEditText().setText("0");
                    } else {

                        double total = (Double.parseDouble(qty) * Double.parseDouble(perPrice));
                        double d = (total * Double.parseDouble(ds)) / 100;
                        double totalAmount = (total) - d;
                        pTotal.getEditText().setText("" + totalAmount);

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        payment.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    String tot = totalSale.getEditText().getText().toString();
                    String pay = payment.getEditText().getText().toString();
                    String bal = balance.getEditText().getText().toString();

                    if (pay.isEmpty()) {
                        balance.getEditText().setText(tot);
                    } else if (tot.isEmpty()) {
                        payment.getEditText().setText("0.00");
                        balance.getEditText().setText("0.00");
                    } else {

                        double balanced = (Double.parseDouble(tot) - Double.parseDouble(pay));
                        balance.getEditText().setText("" + balanced);

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        GetTransactionNo();
    }

    public void GetTransactionNo() {
        try {

            long date = System.currentTimeMillis();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
            String dateStrings = sdf.format(date);
            int transno = 0;
            int count;

            String tran = "select transno from cart";
            Cursor cr = handler.execQuery(tran);
            if (cr != null) {
                cr.moveToFirst();
                transno = Integer.parseInt(String.valueOf(cr.getCount()));

            }
            count = transno;
            transact.setText(dateStrings + "100" + (count + 1));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addCart() {
        double tot;
        double tots;
        String productName = product.getEditText().getText().toString();
        double priceSale = Double.parseDouble(price.getEditText().getText().toString());
        double discount = Double.parseDouble(disc.getEditText().getText().toString());
        int qty = Integer.parseInt(quant.getEditText().getText().toString());

        tot = priceSale * qty;

        double d = (tot * discount) / 100;

        tots = (tot) - d;

        data.add(productName);
        data1.add(String.valueOf(priceSale));
        data2.add(String.valueOf(qty));
        data3.add(String.valueOf(discount));
        data4.add(String.valueOf(tots));

        //TableRow sale_row = new TableRow(this);
        TextView t1 = new TextView(this);
        TextView t2 = new TextView(this);
        TextView t3 = new TextView(this);
        TextView t4 = new TextView(this);
        TextView t5 = new TextView(this);

        String total;
        double sum = 0.0;

        for (int i = 0; i < data.size(); i++) {
            String pname = data.get(i);
            String pric = data1.get(i);
            String qtun = data2.get(i);
            String disco = data3.get(i);
            total = data4.get(i);

            t1.setText(pname);
            t2.setText(pric);
            t3.setText(qtun);
            t4.setText(disco);
            t5.setText(total);

            sum = sum + Double.parseDouble(data4.get(i).toString());
        }

        sale_row.addView(t1);
        sale_row.addView(t2);
        sale_row.addView(t3);
        sale_row.addView(t4);
        sale_row.addView(t5);

        table.addView(sale_row);
        //saveData();
        totalSale.getEditText().setText(String.valueOf(sum));
        product.getEditText().setText("");
        price.getEditText().setText("");
        quant.getEditText().setText("");
        disc.getEditText().setText("");
        clear.setClickable(false);
    }

    public void saveData() {
        try {
            String saleProduct = product.getEditText().getText().toString();
            String salePrice = price.getEditText().getText().toString();
            String saleDisc = disc.getEditText().getText().toString();
            String saleQty = quant.getEditText().getText().toString();
            String saleTotal = pTotal.getEditText().getText().toString();
            String saleCode = pcodeTrans.getEditText().getText().toString();
            String transNo = transact.getText().toString();
            String dateDaily = dateTrans.getEditText().getText().toString();
            String custName = customerName.getEditText().getText().toString();
            String custId = customerId.getEditText().getText().toString();
            String custPlace = customerPlace.getEditText().getText().toString();
            status = "Sold";
            String salePerson = salePers.getText().toString();

            //SQLiteDatabase database = openOrCreateDatabase("FARM", Context.MODE_PRIVATE, null);
            //String sql = "CREATE TABLE IF NOT EXISTS cart(id INTEGER PRIMARY KEY AUTOINCREMENT, transno VARCHAR(100),  pcode VARCHAR(100),  pdes VARCHAR(100), price VARCHAR(100), qty VARCHAR(50),  disc VARCHAR(50),  total VARCHAR(100), vendorId VARCHAR(100), vendorName VARCHAR(100), vendorPlace VARCHAR(100), sdate date, saleperson VARCHAR(100), status VARCHAR(100));";
            // database.execSQL(sql);
            Cursor cursor = handler.execQuery("SELECT * FROM product WHERE pcode = '" + saleCode + "';");
            cursor.moveToFirst();
            if (cursor.getInt(6) < Integer.parseInt(saleQty)) {
                Toast.makeText(activity, "You entered more product than at stock. You have" + cursor.getInt(6) + "at hand", Toast.LENGTH_LONG).show();
            } else {
                String qu = " INSERT INTO cart(transno, pcode, pdes, price, qty, disc, total, vendorId, vendorName, vendorPlace, sdate, saleperson, status) VALUES('" + transNo + "','" + saleCode + "','" + saleProduct + "','" + salePrice + "','" + saleQty + "','" + saleDisc + "','" + saleTotal + "', '" + custId + "','" + custName + "','" + custPlace + "','" + dateDaily + "','" + salePerson + "','" + status + "')";
                //String qu = "insert into cart (transno, pcode, pdes, price, qty, disc, total, vendorId, vendorName, vendorPlace, sdate, saleperson, status) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?)";

                   /* SQLiteStatement statement = database.compileStatement(qu);
                    statement.bindString(1, transNo);
                    statement.bindString(2, saleCode);
                    statement.bindString(3, saleProduct);
                    statement.bindString(4, salePrice);
                    statement.bindString(5, saleQty);
                    statement.bindString(6, saleDisc);
                    statement.bindString(7, saleTotal);
                    statement.bindString(8, custId);
                    statement.bindString(9, custName);
                    statement.bindString(10, custPlace);
                    statement.bindString(11, dateDaily);
                    statement.bindString(12, salePerson);
                    statement.bindString(13, status);

                    statement.execute();
                    Toast.makeText(activity, "New Sale Added Successfully", Toast.LENGTH_LONG).show();
                    product.getEditText().setText("");
                    price.getEditText().setText("");
                    quant.getEditText().setText("");
                    disc.getEditText().setText("");*/

                if (handler.execAction(qu)) {
                    addCart();
                    Objects.requireNonNull(payment.getEditText()).setEnabled(true);
                    payment.getEditText().setFocusable(true);
                    payment.getEditText().setFocusableInTouchMode(true);
                    Toast.makeText(activity, "New Sale Added Successfully", Toast.LENGTH_LONG).show();
                    String qs = "UPDATE product SET qty = qty - '" + Integer.parseInt(saleQty) + "' WHERE pcode = '" + saleCode + "'";
                    String qp = "UPDATE stockIn SET qty = qty - '" + Integer.parseInt(saleQty) + "' WHERE pcode = '" + saleCode + "'";
                    handler.execAction(qs);
                    handler.execAction(qp);
                    product.getEditText().setText("");
                    price.getEditText().setText("");
                    quant.getEditText().setText("");
                    disc.getEditText().setText("");
                } else {
                    Toast.makeText(activity, "error occured", Toast.LENGTH_LONG).show();
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
