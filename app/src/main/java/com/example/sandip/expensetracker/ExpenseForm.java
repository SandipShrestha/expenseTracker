package com.example.sandip.expensetracker;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
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

import java.security.PrivateKey;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ExpenseForm extends AppCompatActivity {

    private TextView type;
    private Spinner expenseCategory, expenseMethod;
    private Button addExpense;
    private EditText expenseDescription, expenseAmount,chooseDate;
    private List<ExpenseCategoryDB> ecatList;
    private ArrayList<String> emethodList;
    private ArrayAdapter<String> madapter, cadapter;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference dbexpense;
    private DatePickerDialog.OnDateSetListener datePicker;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense_form);

        Toolbar toolbar = (Toolbar) findViewById(R.id.expense_tool);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        firebaseAuth = FirebaseAuth.getInstance();
        dbexpense = FirebaseDatabase.getInstance().getReference(firebaseAuth.getUid());
        dbexpense.keepSynced(true);

        expenseFormUI();
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

    private void expenseFormUI() {
        type = (TextView) findViewById(R.id.transaction_type_expense);
        chooseDate = (EditText) findViewById(R.id.expense_date);
        expenseCategory = (Spinner) findViewById(R.id.expense_category);
        expenseMethod = (Spinner) findViewById(R.id.expense_method);
        expenseDescription = (EditText) findViewById(R.id.expense_description);
        expenseAmount = (EditText) findViewById(R.id.expense_amount);
        addExpense = (Button) findViewById(R.id.btnAdd_expense);


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
                        ExpenseForm.this,
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

    private void getCategory() {

        dbexpense.child("Expense Category List").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final List<String> expList = new ArrayList<>();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    String catName = ds.child("expenseCategory").getValue(String.class);
                    expList.add(catName);
                    System.out.println("Category Names are ::::::::::::::::::::::" + expList);
                }

                cadapter = new ArrayAdapter<>(ExpenseForm.this, android.R.layout.simple_spinner_item, expList);
                cadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                // attaching data adapter to spinner
                expenseCategory.setAdapter(cadapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void getMethod() {
        emethodList = new ArrayList<>();
        emethodList.add("Cash");
        emethodList.add("Cheque");
        emethodList.add("Transfer");

        madapter = new ArrayAdapter<>(ExpenseForm.this, android.R.layout.simple_spinner_item, emethodList);

        madapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        expenseMethod.setAdapter(madapter);

    }

    private void onClickAdd() {
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(firebaseAuth.getUid()).child("Transaction");
        addExpense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String type = "Expense";
                String date = chooseDate.getText().toString();
                String category;
                String method = expenseMethod.getSelectedItem().toString();
                String description = expenseDescription.getText().toString();
                Integer amount = null;
                String key=databaseReference.push().getKey();

                try {
                    category = expenseCategory.getSelectedItem().toString();
                } catch (Exception nfe) {
                    Toast.makeText(ExpenseForm.this, "Please add some category first", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(ExpenseForm.this, ExpenseCategory.class));
                    return;
                }
                try {
                    amount = Integer.parseInt(expenseAmount.getText().toString());
                } catch (NumberFormatException nfe) {
                    expenseAmount.setError("Add some amount");
                    return;
                }

                if(TextUtils.isEmpty(description)){
                    expenseDescription.setError("Add some description");
                    return;
                }

                if(TextUtils.isEmpty(date)){
                    chooseDate.setError("Pick Date");
                    return;
                }
                if (expenseCategory.getSelectedItem().equals("")){
                    Toast.makeText(ExpenseForm.this, "Please add some category first", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(ExpenseForm.this, ExpenseCategory.class));
                }

                else {
                    TransactionDB transactionDB = new TransactionDB(type,date,category,method,description,amount,key);
                    Toast.makeText(ExpenseForm.this, "New transaction added", Toast.LENGTH_SHORT).show();
                    databaseReference.child(key).setValue(transactionDB);
                    chooseDate.setText("");
                    expenseDescription.setText("");
                    expenseAmount.setText("");
                }
            }
        });
    }


}
