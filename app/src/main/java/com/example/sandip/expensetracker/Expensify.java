package com.example.sandip.expensetracker;

import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.ContentFrameLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

public class Expensify extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private FirebaseAuth firebaseAuth;
    private Button income, expense;
    private TextView totalIncome, totalExpense;
    private static String TAG = "Expensify";
    private int[] yData;
    private String[] xData = {"Income", "Expense"};
    PieChart pieChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expensify);
        homeUI();
        addIncome();
        addExpense();
        calcIncome();
        calcExpense();
        // warning();
        new Handler().postDelayed(() -> warning(), 3000);
//        new Handler().postDelayed(() -> expensifyPie(), 10000);

        firebaseAuth = FirebaseAuth.getInstance();


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


    }

    @Override
    protected void onStart() {
        super.onStart();
       /* if (totalIncome.equals("")) {
            totalIncome.setText("0");
        }
        if (totalExpense.equals("")) {
            totalExpense.setText("0");
        }*/
        new Handler().postDelayed(() -> expensifyPie(), 1000);
    }

    private void homeUI() {
        income = (Button) findViewById(R.id.btn_income_main);
        expense = (Button) findViewById(R.id.btn_expense_main);
        totalIncome = (TextView) findViewById(R.id.total_income);
        pieChart = (PieChart) findViewById(R.id.mainChart);
        totalExpense = (TextView) findViewById(R.id.total_expense);
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.expensify, menu);
        return true;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            // Handle the camera action
        } else if (id == R.id.nav_expense) {
            Intent i = new Intent(Expensify.this, ExpenseCategory.class);
            startActivity(i);
        } else if (id == R.id.nav_income) {

            Intent i = new Intent(Expensify.this, IncomeCategory.class);
            startActivity(i);

        } else if (id == R.id.nav_transaction) {
            Intent i = new Intent(Expensify.this, Transaction.class);
            startActivity(i);
        } else if (id == R.id.nav_calculator) {
            Intent i = new Intent(Expensify.this, Calculator.class);
            startActivity(i);
        } else if (id == R.id.nav_about) {
            Intent i = new Intent(Expensify.this, About.class);
            startActivity(i);
        } else if (id == R.id.nav_logout) {

            Logout();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    private void Logout() {
        new AlertDialog.Builder(this)
                .setTitle("Logout")
                .setMessage("Do you want to logout?")
                .setIcon(android.R.drawable.ic_dialog_info)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        firebaseAuth.signOut();
                        finish();
                        startActivity(new Intent(Expensify.this, MainActivity.class));
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }

    private void addIncome() {
        income.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(Expensify.this, IncomeForm.class));
                // setContentView(R.layout.activity_income_form); //starting new xml

            }
        });
    }

    private void addExpense() {
        expense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if ((Integer.parseInt(totalIncome.getText().toString())) < (Integer.parseInt(totalExpense.getText().toString()))) {
                        new AlertDialog.Builder(Expensify.this)
                                .setTitle("Your expense is higher than your income")
                                .setMessage("Do you want still want to expend?")
                                .setIcon(android.R.drawable.ic_dialog_info)
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        startActivity(new Intent(Expensify.this, ExpenseForm.class));
                                    }
                                })
                                .setNegativeButton("No", null)
                                .show();
                    } else {
                        startActivity(new Intent(Expensify.this, ExpenseForm.class));
                        // setContentView(R.layout.activity_income_form); //starting new xml
                    }
                }
                catch (NumberFormatException e){
                    startActivity(new Intent(Expensify.this, ExpenseForm.class));
                }
            }
        });
    }

    private void calcIncome() {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(firebaseAuth.getUid()).child("Transaction");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int sum = 0;

                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    TransactionDB transactionDB = ds.getValue(TransactionDB.class);
                    if (transactionDB.getType().equals("Income")) {
                        Map<String, Object> map = (Map<String, Object>) ds.getValue();
                        Object income = map.get("amount");
                        int pValue = Integer.parseInt(String.valueOf(income));
                        sum += pValue;


                        totalIncome.setText(String.valueOf(sum));

                        int totalIncomeGained = sum;
                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void calcExpense() {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(firebaseAuth.getUid()).child("Transaction");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int sum = 0;

                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    TransactionDB transactionDB = ds.getValue(TransactionDB.class);
                    if (transactionDB.getType().equals("Expense")) {
                        Map<String, Object> map = (Map<String, Object>) ds.getValue();
                        Object income = map.get("amount");
                        int pValue = Integer.parseInt(String.valueOf(income));
                        sum += pValue;
                        totalExpense.setText(String.valueOf(sum));
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void expensifyPie() {

        Description description = new Description();

        description.setText("Income-Expense Chart");
        pieChart.setDescription(description);
        pieChart.setCenterText("Have a wonderful day.");
        try {
            addDataPie();
        } catch (NumberFormatException nfe) {
        }

    }

    private void addDataPie() {
        int[] yData = {Integer.parseInt(totalIncome.getText().toString()),
                Integer.parseInt(totalExpense.getText().toString())};
        Log.d(TAG, "adding data");

        ArrayList<PieEntry> yEntry = new ArrayList<>();
        ArrayList<String> xEntry = new ArrayList<>();

        for (int i = 0; i < yData.length; i++) {
            yEntry.add(new PieEntry(yData[i], i));
        }

        for (int i = 1; i < xData.length; i++) {
            xEntry.add(xData[i]);
        }

        PieDataSet pieDataSet = new PieDataSet(yEntry, "Income");
        pieDataSet.setSliceSpace(2);
        pieDataSet.setValueTextSize(12);

        xEntry.add("Income");
        xEntry.add("Expense");


        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(Color.GREEN);
        colors.add(Color.RED);

        pieDataSet.setColors(colors);

        Legend legend = pieChart.getLegend();
        legend.setForm(Legend.LegendForm.CIRCLE);
        legend.setPosition(Legend.LegendPosition.BELOW_CHART_CENTER);

        PieData pieData = new PieData((pieDataSet));
        pieChart.setData(pieData);
        pieChart.notifyDataSetChanged();
        pieChart.invalidate();//This means refresh chart


        pieChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                Log.d(TAG, "onValueSelected:" + e.toString());
                int pos = e.toString().indexOf("y:");
                String totalTrans = e.toString().substring(pos + 3);

                Toast.makeText(Expensify.this, "The total transaction is Rs " + totalTrans, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected() {

            }
        });
    }

    private void warning() {
        try {
            if ((Integer.parseInt(totalIncome.getText().toString())) < (Integer.parseInt(totalExpense.getText().toString()))) {

                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
                LayoutInflater inflater = getLayoutInflater();
                final View dialogView = inflater.inflate(R.layout.warning, null);
                dialogBuilder.setView(dialogView);

                final TextView warning = (TextView) dialogView.findViewById(R.id.warnText);

                warning.setText("Your expense is greater than your income.");

                final AlertDialog alertDialog = dialogBuilder.create();
                alertDialog.show();

            }
        } catch (NumberFormatException e) {
        }
    }
}
