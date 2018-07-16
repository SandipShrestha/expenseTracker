package com.example.sandip.expensetracker;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.nfc.Tag;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class IncomeForm extends AppCompatActivity {

    private TextView type;
    private Spinner incomeCategory, incomeMethod;
    private Button addIncome;
    private EditText incomeDescription, incomeAmount,chooseDate;
    private List<IncomeCategoryDB> icatList;
    private ArrayList<String> imethodList;
    private ArrayAdapter<String> madapter, cadapter;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference dbincome;
    private DatePickerDialog.OnDateSetListener datePicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_income_form);

        Toolbar toolbar = (Toolbar) findViewById(R.id.income_tool);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        firebaseAuth = FirebaseAuth.getInstance();
        dbincome = FirebaseDatabase.getInstance().getReference(firebaseAuth.getUid());
        dbincome.keepSynced(true);
        incomeFormUI();
        getDate();
        getMethod();
        getCategory();
        onClickAdd();


    }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
      switch (item.getItemId()){
          case android.R.id.home:onBackPressed();
      }
      return super.onOptionsItemSelected(item);
  }

    private void incomeFormUI() {
        type = (TextView) findViewById(R.id.transaction_type);
        chooseDate = (EditText) findViewById(R.id.income_date);
        incomeCategory = (Spinner) findViewById(R.id.input_category);
        incomeMethod = (Spinner) findViewById(R.id.income_method);
        incomeDescription = (EditText) findViewById(R.id.income_description);
        incomeAmount = (EditText) findViewById(R.id.income_amount);
        addIncome = (Button) findViewById(R.id.btnAdd);


    }

    private void getDate() {
        chooseDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        IncomeForm.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        datePicker,
                        year,month,day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });
        datePicker = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {

                month = month + 1;
                String date = day + "/" + month + "/" + year ;
                chooseDate.setText(date);

            }
        };


    }

    private void getMethod() {
        imethodList = new ArrayList<>();
        imethodList.add("Cash");
        imethodList.add("Cheque");
        imethodList.add("Transfer");

        madapter = new ArrayAdapter<>(IncomeForm.this, android.R.layout.simple_spinner_item, imethodList);

        madapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        incomeMethod.setAdapter(madapter);

    }

    private void getCategory() {

        dbincome.child("Income Category List").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final List<String> incList = new ArrayList<>();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    String catName = ds.child("incomeCategory").getValue(String.class);
                    incList.add(catName);
                    System.out.println("Category Names are ::::::::::::::::::::::" + incList);
                }

                cadapter = new ArrayAdapter<>(IncomeForm.this, android.R.layout.simple_spinner_item, incList);
                cadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                // attaching data adapter to spinner
                incomeCategory.setAdapter(cadapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    private void onClickAdd() {
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(firebaseAuth.getUid()).child("Transaction");
        addIncome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String type = "Income";
                String date = chooseDate.getText().toString();
                String category ;
                String method = incomeMethod.getSelectedItem().toString();
                String description = incomeDescription.getText().toString();
                Integer amount = null;
                String key=databaseReference.push().getKey();

                try {
                    category = incomeCategory.getSelectedItem().toString();
                } catch (Exception nfe) {
                    Toast.makeText(IncomeForm.this, "Please add some category first", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(IncomeForm.this, IncomeCategory.class));
                    return;
                }

                try {
                    amount = Integer.parseInt(incomeAmount.getText().toString());
                } catch (NumberFormatException nfe) {
                    incomeAmount.setError("Add some amount");
                    return;
                }



                if(TextUtils.isEmpty(description)){
                    incomeDescription.setError("Add some description");
                    return;
                }

                if(TextUtils.isEmpty(date)){
                    chooseDate.setError("Pick Date");
                    return;
                }

                else {
                    TransactionDB transactionDB = new TransactionDB(type,date,category,method,description,amount,key);
                    Toast.makeText(IncomeForm.this, "New transaction added", Toast.LENGTH_SHORT).show();
                    databaseReference.child(key).setValue(transactionDB);
                    chooseDate.setText("");
                    incomeDescription.setText("");
                    incomeAmount.setText("");
                }
            }
        });
    }
}
