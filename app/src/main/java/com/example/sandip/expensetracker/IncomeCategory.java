package com.example.sandip.expensetracker;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
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
import android.support.v7.app.AlertDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class IncomeCategory extends AppCompatActivity {

    private EditText incomeCategory;
    private TextView income;
    private Button add, back;
    private ListView incomeView;
    public DatabaseReference dbincome;
    private FirebaseAuth firebaseAuth;
    private List<IncomeCategoryDB> icatList;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_income_category);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        firebaseAuth = FirebaseAuth.getInstance();
        dbincome = FirebaseDatabase.getInstance().getReference(firebaseAuth.getUid()).child("Income Category List");
        dbincome.keepSynced(true);
        incomeCatUI();
        icatList = new ArrayList<>();
        addIncomeClick();
        onLongClick();
        retriveIncomeCategory();


     /*   back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(IncomeCategory.this, Expensify.class);
                startActivity(intent);
            }
        });*/

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    private void incomeCatUI() {
        incomeCategory = (EditText) findViewById(R.id.input_category);
        add = (Button) findViewById(R.id.add_category);
        incomeView = (ListView) findViewById(R.id.list_income_category);
        income = (TextView) findViewById(R.id.textview_income);


    }

    private void addIncomeClick() {
        add.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onClick(View view) {
                if (validate()) {
                    String category = incomeCategory.getText().toString().trim();
                    String key =dbincome.push().getKey();

                    IncomeCategoryDB incomeCategoryDB = new IncomeCategoryDB(category,key);
                    Toast.makeText(IncomeCategory.this, "New category added", Toast.LENGTH_SHORT).show();
                    dbincome.child(key).setValue(incomeCategoryDB);
                    incomeCategory.setText("");
                }
                else {
                    Toast.makeText(IncomeCategory.this, "Failed to add new category ", Toast.LENGTH_SHORT).show();
                }
            }

        });

    }

    public boolean validate() {
        boolean count = false;
        String category = incomeCategory.getText().toString();

        if (category.isEmpty()) {
            Toast.makeText(this, "Please specify income category", Toast.LENGTH_SHORT).show();
        } else {
            count = true;
        }
        return count;
    }
    private void retriveIncomeCategory() {
        dbincome.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                icatList.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    IncomeCategoryDB incomeCategoryDB = ds.getValue(IncomeCategoryDB.class);
                    icatList.add(incomeCategoryDB);
                }

                IncomeCategoryAdapter adapter = new IncomeCategoryAdapter(IncomeCategory.this, icatList);
                incomeView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    private void onLongClick(){
        incomeView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                IncomeCategoryDB incomeCategoryDB = icatList.get(i);
                showUpdateDialog(incomeCategoryDB.getIncomeCategory(),incomeCategoryDB.getKey());

                return false;
            }
        });

    }

    private void showUpdateDialog(final String category, final String key) {
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
    @SuppressLint("RestrictedApi") DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(firebaseAuth.getUid()).child("Income Category List").child(key);

        IncomeCategoryDB category =new IncomeCategoryDB(newCategory,key);
        databaseReference.setValue(category);
        Toast.makeText(this,"Update Successful",Toast.LENGTH_SHORT).show();
    }

    private void deleteCategory(String key){
       @SuppressLint("RestrictedApi") DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(firebaseAuth.getUid()).child("Income Category List").child(key);
        databaseReference.removeValue();
        Toast.makeText(this,"Delete Successful",Toast.LENGTH_SHORT).show();
    }

}

