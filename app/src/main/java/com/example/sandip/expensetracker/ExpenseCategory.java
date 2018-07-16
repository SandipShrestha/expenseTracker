package com.example.sandip.expensetracker;

import android.annotation.SuppressLint;
import android.preference.PreferenceFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseListOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ExpenseCategory extends AppCompatActivity {


    private EditText expenseCategory;
    private Button addExpense;
    private ListView expenseView;
    public DatabaseReference dbexpense;
    private FirebaseAuth firebaseAuth;
    private List<ExpenseCategoryDB>ecatList;



    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense_category);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        firebaseAuth = FirebaseAuth.getInstance();

        dbexpense = FirebaseDatabase.getInstance().getReference(firebaseAuth.getUid()).child("Expense Category List");
        dbexpense.keepSynced(true);
        expenseCatUI();
        addExpenseClick();
        retriveExpenseCategory();
        onLongClick();
        ecatList=new ArrayList<>();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    private void expenseCatUI() {
        expenseCategory = (EditText) findViewById(R.id.input_expense_category);
        addExpense = (Button) findViewById(R.id.add_expense_category);
        expenseView = (ListView) findViewById(R.id.list_expense_category);


    }


    private void retriveExpenseCategory(){
        dbexpense.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                ecatList.clear();
                for (DataSnapshot ds :dataSnapshot.getChildren()) {
                    ExpenseCategoryDB expenseCategoryDB = ds.getValue(ExpenseCategoryDB.class);
                    ecatList.add(expenseCategoryDB);
                }

                ExpenseCategoryAdapter adapter = new ExpenseCategoryAdapter(ExpenseCategory.this, ecatList);
                expenseView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void addExpenseClick(){
        addExpense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validate()) {
                    String category = expenseCategory.getText().toString().trim();
                    String key = dbexpense.push().getKey();

                    ExpenseCategoryDB expenseCategoryDB = new ExpenseCategoryDB(category,key);
                    Toast.makeText(ExpenseCategory.this, "New category added", Toast.LENGTH_SHORT).show();
                    dbexpense.child(key).setValue(expenseCategoryDB);
                    expenseCategory.setText("");
                }
                else
                {
                    Toast.makeText(ExpenseCategory.this, "Failed to add new category ", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public boolean validate() {
        boolean count = false;
        String category = expenseCategory.getText().toString();
        if (category.isEmpty()) {
            Toast.makeText(this, "Please specify expense category", Toast.LENGTH_SHORT).show();
        } else {
            count = true;
        }
        return count;
    }

    private void onLongClick(){
        expenseView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                ExpenseCategoryDB expenseCategoryDB = ecatList.get(i);
                showUpdateDialog(expenseCategoryDB.getExpenseCategory(),expenseCategoryDB.getKey());

                return false;
            }
        });

    }

    private void showUpdateDialog( String category,final String key ) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.update_delete, null);
        dialogBuilder.setView(dialogView);

        final EditText editCategory = (EditText) dialogView.findViewById(R.id.editCategory);
        final Button btnUpdate = (Button) dialogView.findViewById(R.id.btnUpdate);
        final Button btnDelete = (Button) dialogView.findViewById(R.id.btnDelete);

        dialogBuilder.setTitle("Updating category  " + category);
        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String newCategory= editCategory.getText().toString().trim();

                if(TextUtils.isEmpty(newCategory)){
                    editCategory.setError("FIll category name");
                    return;
                }
                updateCategory(newCategory,key);
                alertDialog.dismiss();
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              deleteCategory(key);
                alertDialog.dismiss();
            }
        });


    }
    private void updateCategory(String newCategory,String key) {
        @SuppressLint("RestrictedApi") DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(firebaseAuth.getUid()).child("Expense Category List").child(key);

        ExpenseCategoryDB category =new ExpenseCategoryDB(newCategory,key);
        databaseReference.setValue(category);
        Toast.makeText(this,"Update Successful",Toast.LENGTH_SHORT).show();

    }
    private void deleteCategory(String key){
        @SuppressLint("RestrictedApi") DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(firebaseAuth.getUid()).child("Expense Category List").child(key);
        databaseReference.removeValue();
        Toast.makeText(this,"Delete Successful",Toast.LENGTH_SHORT).show();
    }
}
