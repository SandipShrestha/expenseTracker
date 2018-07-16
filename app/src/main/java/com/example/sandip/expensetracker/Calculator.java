package com.example.sandip.expensetracker;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

public class Calculator extends AppCompatActivity {
    private Button calculate,clear;
    private Spinner category;
    private RadioGroup typeGroup;
    private TextView result;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference calcDB;
    private ArrayAdapter<String> eadapter, iadapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculator);

        Toolbar toolbar = (Toolbar) findViewById(R.id.calculator_tool);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        firebaseAuth = FirebaseAuth.getInstance();
        calcDB = FirebaseDatabase.getInstance().getReference(firebaseAuth.getUid());
        calcDB.keepSynced(true);

        calculatorUI();
        getType();
        calculateTotal();
        getIncomeCategory();
        clearresult();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    private void calculatorUI(){
        calculate = (Button)findViewById(R.id.btn_calc);
        category = (Spinner) findViewById(R.id.calc_spinner);
        typeGroup = (RadioGroup)findViewById(R.id.choose_type);
        result = (TextView)findViewById(R.id.showResult_tv);
        clear = (Button)findViewById(R.id.clear_calc);

    }

    private void getType(){
        typeGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch(checkedId){
                    case R.id.rb_income:
                        getIncomeCategory();
                        break;
                    case R.id.rb_expense:
                        getExpenseCategory();
                        break;
                }
            }
        });
    }
    private void getExpenseCategory() {

        calcDB.child("Expense Category List").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final List<String> expenseList = new ArrayList<>();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    String catName = ds.child("expenseCategory").getValue(String.class);
                    expenseList.add(catName);
                    System.out.println("Category Names are ::::::::::::::::::::::" + expenseList);
                }

                eadapter = new ArrayAdapter<>(Calculator.this, android.R.layout.simple_spinner_item, expenseList);
                eadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                // attaching data adapter to spinner
                category.setAdapter(eadapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void getIncomeCategory() {

        calcDB.child("Income Category List").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final List<String> incomeList = new ArrayList<>();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    String catName = ds.child("incomeCategory").getValue(String.class);
                    incomeList.add(catName);
                    System.out.println("Category Names are ::::::::::::::::::::::" + incomeList);
                }

                iadapter = new ArrayAdapter<>(Calculator.this, android.R.layout.simple_spinner_item, incomeList);
                iadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                // attaching data adapter to spinner
                category.setAdapter(iadapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    private void calcExpense(){
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(firebaseAuth.getUid()).child("Transaction");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                int sum = 0;
                final String catToFind = category.getSelectedItem().toString();

                for (DataSnapshot ds : dataSnapshot.getChildren()){
                    TransactionDB transactionDB = ds.getValue(TransactionDB.class);


                    if (catToFind.equals(transactionDB.getCategory())) {

                        Map<String, Object> map = (Map<String, Object>) ds.getValue();
                        Object income = map.get("amount");
                        int pValue = Integer.parseInt(String.valueOf(income));
                          sum += pValue;
//                        result.setText(String.valueOf(sum));
                        result.setText("The total transaction in the category "+ catToFind + " is  Rs." + (String.valueOf(sum)));

                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    private void calculateTotal(){
        calculate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calcExpense();
            }
        });
    }

    private void clearresult(){
        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                result.setText("");
            }
        });
    }
}
