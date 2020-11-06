package ideanity.oceans.farmertribe.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.Random;

import ideanity.oceans.farmertribe.R;
import ideanity.oceans.farmertribe.database.DatabaseHandler;

public class ProductActivity extends AppCompatActivity {

    Activity activity = this;

    public static DatabaseHandler handler;

    Random number;
    int rnum;
    ProductListActivity productListActivity;
    ImageView back;
    public static Button brandReg, pick, loadVendor;
    TextInputLayout vendorId;
    TextInputLayout vendorName;
    TextInputLayout vendorPhone;
    TextInputLayout vendorPlace;
    TextInputLayout product;
    TextInputLayout unit;
    TextInputLayout reorder;
    TextInputLayout purchase;
    TextInputLayout price;
    Spinner spinner;
    EditText serachVendor, pCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_product);

        handler = new DatabaseHandler(this);

        back = findViewById(R.id.back_prod);

        vendorId = findViewById(R.id.vendor_Id);
        assert vendorId != null;
        vendorName = findViewById(R.id.vendor_name);
        assert vendorName != null;
        vendorPhone = findViewById(R.id.vendor_phone);
        assert vendorPhone != null;
        vendorPlace = findViewById(R.id.vendor_place);
        assert vendorPlace != null;
        pCode = findViewById(R.id.pcode);
        product = findViewById(R.id.product_name);
        unit = findViewById(R.id.unit);
        reorder = findViewById(R.id.reorder);
        purchase = findViewById(R.id.purchase);
        price = findViewById(R.id.price);
        serachVendor = findViewById(R.id.search_vendor);
        spinner = findViewById(R.id.brandSpinner);

        String qu = "SELECT * FROM brand";
        ArrayList<String> subs = new ArrayList<>();
        subs.add("Select Brand");
        Cursor cr = handler.execQuery(qu);
        if (cr != null) {
            cr.moveToFirst();
            while (!cr.isAfterLast()) {
                subs.add(cr.getString(1));

                cr.moveToNext();
            }
        } else
            Log.d("ProductActivity.class", "No SUBS" + cr.getString(1));

        ArrayAdapter<String> adapterSpinner = new ArrayAdapter<String>(activity, android.R.layout.simple_spinner_dropdown_item, subs);
        assert spinner != null;
        spinner.setAdapter(adapterSpinner);

        brandReg = findViewById(R.id.btn_add_product);
        pick = findViewById(R.id.pick);
        loadVendor = findViewById(R.id.loadForEdit);
        assert loadVendor != null;

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProductActivity.super.onBackPressed();
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
                pickCode();

            }
        });

        loadVendor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String qu = "SELECT * FROM member WHERE farmerId = '" + serachVendor.getText().toString().toUpperCase() + "'";
                String qd = "SELECT * FROM member WHERE farmerName = '" + serachVendor.getText().toString().toUpperCase() + "'";
                Cursor cr = handler.execQuery(qu);
                Cursor cur1 = handler.execQuery(qd);
                if (cr != null || cur1 != null){
                    cr.moveToFirst();
                    cur1.moveToFirst();
                    try {
                        vendorId.getEditText().setText(cr.getString(2));
                        vendorName.getEditText().setText(cr.getString(1));
                        vendorPhone.getEditText().setText(cr.getString(6));
                        vendorPlace.getEditText().setText(cr.getString(3));
                    } catch (Exception e) { }
                }
                else {
                    Toast.makeText(getBaseContext(), "No Such Member", Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    private void pickCode() {
        number = new Random();
        rnum = number.nextInt(1000);
        String code = "P" + rnum;
        pCode.setText(code);
    }

    private void saveData() {

        try {
            String farmId = vendorId.getEditText().getText().toString().trim();
            String farmName = vendorName.getEditText().getText().toString().trim();
            String farmPhone = vendorPhone.getEditText().getText().toString().trim();
            String farmCom = vendorPlace.getEditText().getText().toString().trim();
            String pcode = pCode.getText().toString().trim();
            String productName = product.getEditText().getText().toString().trim();
            String weight = unit.getEditText().getText().toString().trim();
            String re_order = reorder.getEditText().getText().toString().trim();
            String purchas = purchase.getEditText().getText().toString().trim();
            String sale = price.getEditText().getText().toString().trim();
            String brand = spinner.getSelectedItem().toString();

            if (farmId.isEmpty()) {
                AlertDialog.Builder alert = new AlertDialog.Builder(ProductActivity.this);
                alert.setTitle("Farmer ID");
                alert.setMessage("Farmer ID can not be empty");
                alert.setPositiveButton("OK", null);
                alert.show();
                return;
            }
            else if (farmName.isEmpty()) {
                AlertDialog.Builder alert = new AlertDialog.Builder(ProductActivity.this);
                alert.setTitle("Farmer Name");
                alert.setMessage("Farmer Name can not be empty");
                alert.setPositiveButton("OK", null);
                alert.show();
                return;
            }
            else if (pcode.isEmpty()) {
                AlertDialog.Builder alert = new AlertDialog.Builder(ProductActivity.this);
                alert.setTitle("Product Code");
                alert.setMessage("Product code can not be empty");
                alert.setPositiveButton("OK", null);
                alert.show();
                return;
            }
            else if (productName.isEmpty()) {
                AlertDialog.Builder alert = new AlertDialog.Builder(ProductActivity.this);
                alert.setTitle("Product Name");
                alert.setMessage("Product Name can not be empty");
                alert.setPositiveButton("OK", null);
                alert.show();
                return;
            }
            else if (sale.isEmpty()) {
                AlertDialog.Builder alert = new AlertDialog.Builder(ProductActivity.this);
                alert.setTitle("Price");
                alert.setMessage("Sale Price can not be empty");
                alert.setPositiveButton("OK", null);
                alert.show();
                return;
            }
            else if (re_order.isEmpty()) {
                AlertDialog.Builder alert = new AlertDialog.Builder(ProductActivity.this);
                alert.setTitle("Reorder");
                alert.setMessage("Reorder Qty can not be empty");
                alert.setPositiveButton("OK", null);
                alert.show();
                return;
            }
            else {
//                SQLiteDatabase database = openOrCreateDatabase("FARM", Context.MODE_PRIVATE, null);
//                String sql = "CREATE TABLE IF NOT EXISTS product(pcode VARCHAR(100) PRIMARY KEY, pdes VARCHAR(100), brandname VARCHAR(100), unit VARCHAR(100), purchasePrice VARCHAR(100), price VARCHAR(100), qty VARCHAR(100), reorder VARCHAR(100), vendorId VARCHAR(100), vendorName VARCHAR(100), vendorPhone VARCHAR(100), vendorPlace VARCHAR(100));";
//                database.execSQL(sql);

                String qu = " INSERT INTO product(pcode, pdes, brandname, unit, purchasePrice, price, qty, reorder, vendorId, vendorName, vendorPhone, vendorPlace) VALUES('"+pcode+"','"+productName+"','"+brand+"','"+weight+"','"+purchas+"','"+sale+"','"+re_order+"','"+re_order+"','"+farmId+"','"+farmName+"','"+farmPhone+"','"+farmCom+"')";

                /*SQLiteStatement statement = database.compileStatement(qu);
                statement.bindString(0, pcode);
                statement.bindString(1, productName);
                statement.bindString(2, brand);
                statement.bindString(3, weight);
                statement.bindString(4, purchas);
                statement.bindString(5, sale);
                statement.bindString(6, re_order);
                statement.bindString(7, re_order);
                statement.bindString(8, farmId);
                statement.bindString(9, farmName);
                statement.bindString(10, farmPhone);
                statement.bindString(11, farmCom);
                statement.execute();*/
                if (handler.execAction(qu)) {
                    Toast.makeText(activity, "New Product Added Successfully", Toast.LENGTH_LONG).show();
                    //productListActivity.loadProduct();

                    product.requestFocus();
                    pickCode();
                    product.getEditText().setText("");
                    unit.getEditText().setText("");
                    reorder.getEditText().setText("");
                    purchase.getEditText().setText("");
                    price.getEditText().setText("");

                }
                //spinner.setSelection(0);
            }
        }
        catch (Exception ex){Toast.makeText(activity, "Product Registration Failed", Toast.LENGTH_SHORT).show();}

    }

}
