package com.example.sandip.expensetracker;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Transaction extends AppCompatActivity {
    private TextView transaction;
    private Button btnAll,btnIncome,btnExpense;
    private ListView transactionView;
    private FirebaseAuth firebaseAuth;
    private List<TransactionDB> transList;
    private List<String> dateList;
    private List<String> transDateList;
    private List<String> overallDateList;
    private DatabaseReference dbtransaction;
    private DatePickerDialog.OnDateSetListener startdDatePicker,enddDatePicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction);

        firebaseAuth = FirebaseAuth.getInstance();
        dbtransaction = FirebaseDatabase.getInstance().getReference(firebaseAuth.getUid()).child("Transaction");
        dbtransaction.keepSynced(true);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar3);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        transList=new ArrayList<>();
        dateList = new ArrayList<>();
        transDateList = new ArrayList<>();
        overallDateList = new ArrayList<>();



        transactionUI();
        getTransactionData();
        incomeBtnClick();
        expenseBtnClick();
        allBtnClick();
        onLongClick();

    }
    private void transactionUI(){
        btnAll = (Button)findViewById(R.id.all_transaction_btn);
        btnIncome=(Button)findViewById(R.id.income_transaction_btn);
        btnExpense = (Button) findViewById(R.id.expense_transaction_btn);
        transactionView = (ListView)findViewById(R.id.list_transaction);

    }

    private void allBtnClick(){
        btnAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getTransactionData();
            }
        });
    }
   private void getTransactionData(){
        dbtransaction.orderByChild("date").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                transList.clear();
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    TransactionDB transactionDB = ds.getValue(TransactionDB.class);
                    transList.add(transactionDB);
                }
                TransactionAdapter adapter = new TransactionAdapter(Transaction.this,transList);
                transactionView.setAdapter(adapter);
                Collections.reverse(transList);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void incomeBtnClick(){
        btnIncome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showIncome();
            }
        });
    }


    private void showIncome(){
        DatabaseReference incomeSnapshot = FirebaseDatabase.getInstance().getReference(firebaseAuth.getUid());

        incomeSnapshot.child("Transaction").orderByChild("date").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                transList.clear();
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    TransactionDB transactionDB = ds.getValue(TransactionDB.class);

                    if (transactionDB.getType().equals("Income")){
                        transList.add(transactionDB);
                    }
                }
                TransactionAdapter adapter = new TransactionAdapter(Transaction.this,transList);
                transactionView.setAdapter(adapter);
                Collections.reverse(transList);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    private void expenseBtnClick(){
        btnExpense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showExpense();
            }
        });
    }

    private void showExpense(){
        DatabaseReference expenseSnapshot = FirebaseDatabase.getInstance().getReference(firebaseAuth.getUid());

        expenseSnapshot.child("Transaction").orderByChild("date").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                transList.clear();
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    TransactionDB transactionDB = ds.getValue(TransactionDB.class);

                    if (transactionDB.getType().equals("Expense")){
                        transList.add(transactionDB);
                    }
                }
                TransactionAdapter adapter = new TransactionAdapter(Transaction.this,transList);
                transactionView.setAdapter(adapter);
                Collections.reverse(transList);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void onLongClick(){
        transactionView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                TransactionDB transactionDB = transList.get(i);
                showUpdateDialog(transactionDB.getKey());

                return false;
            }
        });

    }

    private void showUpdateDialog(final String key ) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.delete, null);
        dialogBuilder.setView(dialogView);
        final Button btnDelete = (Button) dialogView.findViewById(R.id.delete);

          dialogBuilder.setTitle("     Do you want delete transaction? " );
        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();



        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteTransaction(key);
                alertDialog.dismiss();
            }
        });


    }
    private void deleteTransaction(String key){
        @SuppressLint("RestrictedApi") DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(firebaseAuth.getUid()).child("Transaction").child(key);
        databaseReference.removeValue();
        Toast.makeText(this,"Delete Successful",Toast.LENGTH_SHORT).show();
    }


    private void showToday(){
        DatabaseReference expenseSnapshot = FirebaseDatabase.getInstance().getReference(firebaseAuth.getUid());
        final String currentDate = new SimpleDateFormat("d/M/yyyy", Locale.getDefault()).format(new Date());

        System.out.println(currentDate);
        expenseSnapshot.child("Transaction").orderByChild("date").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                transList.clear();
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    TransactionDB transactionDB = ds.getValue(TransactionDB.class);

                    if (currentDate.equals(transactionDB.getDate())){
                        transList.add(transactionDB);
                        System.out.println(transList);
                    }
                }
                TransactionAdapter adapter = new TransactionAdapter(Transaction.this,transList);
                transactionView.setAdapter(adapter);
                Collections.reverse(transList);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void showMonth(){
        DatabaseReference expenseSnapshot = FirebaseDatabase.getInstance().getReference(firebaseAuth.getUid());
        Calendar calendar = Calendar.getInstance();
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
         final int currentMonth = month +1;

        System.out.println("Month =" +currentMonth);
        System.out.println("Year ="+ year);
        expenseSnapshot.child("Transaction").orderByChild("date").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                transList.clear();
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    TransactionDB transactionDB = ds.getValue(TransactionDB.class);

                     String transDate = transactionDB.getDate();
                    String[] calend = transDate.split("/");
                    int transMonth = Integer.parseInt(calend[1]);
                    int transYear = Integer.parseInt(calend[2]);
                    System.out.println("the month is " + transMonth);
                    System.out.println("the year is " + transYear);
                    if (currentMonth==transMonth && year==transYear){
                        transList.add(transactionDB);
                        System.out.println(transList);
                    }
                }
                TransactionAdapter adapter = new TransactionAdapter(Transaction.this,transList);
                transactionView.setAdapter(adapter);
                Collections.reverse(transList);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }



   private void showList(){
       AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
       LayoutInflater inflater = getLayoutInflater();
       final View dialogView = inflater.inflate(R.layout.custom_date, null);
       dialogBuilder.setView(dialogView);

       final EditText startDate = (EditText) dialogView.findViewById(R.id.startDate);
       final EditText endDate = (EditText) dialogView.findViewById(R.id.endDate);
       final Button list = (Button) dialogView.findViewById(R.id.btnList);

       dialogBuilder.setTitle("Choose the date range" );
       final AlertDialog alertDialog = dialogBuilder.create();
       alertDialog.show();
       overallDateList.clear();
       startDate.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               Calendar calendar = Calendar.getInstance();
               int year = calendar.get(Calendar.YEAR);
               int month = calendar.get(Calendar.MONTH);
               int day = calendar.get(Calendar.DAY_OF_MONTH);

               DatePickerDialog dialog = new DatePickerDialog(
                       Transaction.this,
                           android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                           startdDatePicker,
                           year,month,day);
                   dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                   dialog.show();
               }
           });
           startdDatePicker = new DatePickerDialog.OnDateSetListener() {
               @Override
               public void onDateSet(DatePicker datePicker, int year, int month, int day) {

                   month = month + 1;
                   String date = day + "/" + month + "/" + year ;
                   startDate.setText(date);
                   System.out.println(startDate);
               }
           };


       endDate.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               Calendar calendar = Calendar.getInstance();
               int year = calendar.get(Calendar.YEAR);
               int month = calendar.get(Calendar.MONTH);
               int day = calendar.get(Calendar.DAY_OF_MONTH);

               DatePickerDialog dialog = new DatePickerDialog(
                       Transaction.this,
                       android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                       enddDatePicker,
                       year,month,day);
               dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
               dialog.show();
           }
       });
       enddDatePicker = new DatePickerDialog.OnDateSetListener() {
           @Override
           public void onDateSet(DatePicker datePicker, int year, int month, int day) {

               month = month + 1;
               String date = day + "/" + month + "/" + year ;
               endDate.setText(date);
               System.out.println(endDate);
           }
       };





       list.setOnClickListener(new View.OnClickListener() {

           @Override
           public void onClick(View view) {
                dateList.clear();

               try {
                   String fromDate = startDate.getText().toString();
                   String toDate = endDate.getText().toString();
                   SimpleDateFormat myFormat = new SimpleDateFormat("d/M/yyyy");
                   Date fDate = myFormat.parse(fromDate);
                   Date tDate = myFormat.parse(toDate);
                   long  from = fDate.getTime();
                   long to=tDate.getTime();
                   long ONE_DAY = 24 * 60 * 60 * 1000L;

                   int x=0;

                   while(from <= to) {
                       x=x+1;
                       String dates = new SimpleDateFormat("d/M/yyyy").format(new Date(from));
                       System.out.println ("Dates  : "+dates);
                       dateList.add(dates);

                       from += ONE_DAY;
                   }
                   System.out.println ("No of Dates  :"+ x);



               } catch (ParseException e) {
                   e.printStackTrace();
               }


               DatabaseReference expenseSnapshot = FirebaseDatabase.getInstance().getReference(firebaseAuth.getUid());
               expenseSnapshot.child("Transaction").orderByChild("date").addValueEventListener(new ValueEventListener() {
                   @Override
                   public void onDataChange(DataSnapshot dataSnapshot) {
                    //transDateList.clear();
                       transList.clear();
                       for (DataSnapshot ds: dataSnapshot.getChildren()){
                           TransactionDB transactionDB = ds.getValue(TransactionDB.class);
                           transDateList.add(transactionDB.getDate());


                           overallDateList.clear();
                            for (String transDates : dateList){
                                if (transDateList.contains(transDates)){

                                    overallDateList.add(transDates);
                                    System.out.println(overallDateList);
                                }
                            }

                            for (String date: overallDateList){

                                if (transDateList.contains(date)) {

                                    transList.add(transactionDB);
                                    System.out.println(transList);
                                    break;
                                }
                            }
                            transDateList.clear();

                       }
                       TransactionAdapter adapter = new TransactionAdapter(Transaction.this,transList);
                       transactionView.setAdapter(adapter);
                       Collections.reverse(transList);
                       alertDialog.dismiss();

                   }

                   @Override
                   public void onCancelled(DatabaseError databaseError) {

                   }
               });


           }
       });



   }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.transaction, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
       switch (item.getItemId()){
           case android.R.id.home:onBackPressed();
           case R.id.today:
              showToday();
               return true;
           case R.id.month:
             showMonth();
             return true;
           case R.id.chosen:
               showList();
               return true;
           default:
               return super.onOptionsItemSelected(item);
       }

    }
}


