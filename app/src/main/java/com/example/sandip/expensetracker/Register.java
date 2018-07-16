package com.example.sandip.expensetracker;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Register extends AppCompatActivity {

    private TextInputEditText userName,userEmail,userContact,userPassword;
    private Button register;
    private TextView userLogin;
    private FirebaseAuth firebaseAuth;
    String name,email,contact,password;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_register);
        registrationUI();

        firebaseAuth = FirebaseAuth.getInstance();
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validate()) {
                    String user_Email = userEmail.getText().toString().trim();
                    String user_Password = userPassword.getText().toString().trim();

                    firebaseAuth.createUserWithEmailAndPassword(user_Email,user_Password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            sendUserData();
                            firebaseAuth.signOut();
                            Toast.makeText(Register.this,"Registration Successful",Toast.LENGTH_SHORT).show();
                            finish();
                            startActivity(new Intent(Register.this, MainActivity.class));
                        }
                        else {
                            Toast.makeText(Register.this,"Registration Failed",Toast.LENGTH_SHORT).show();
                        }

                        }
                    });
                }
            }
        });

        userLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Register.this, MainActivity.class));
            }
        });
    }
    private void registrationUI(){
        userName = (TextInputEditText) findViewById(R.id.register_name);
        userEmail = (TextInputEditText) findViewById(R.id.register_email);
        userContact = (TextInputEditText) findViewById(R.id.register_contact);
        userPassword = (TextInputEditText) findViewById(R.id.register_password);
        register = (Button)findViewById(R.id.register_register);
        userLogin = (TextView)findViewById(R.id.register_login);

    }

    private boolean validate(){
        boolean count = false;

        name = userName.getText().toString();
        email = userEmail.getText().toString();
        contact = userContact.getText().toString();
        password = userPassword.getText().toString();

        if (TextUtils.isEmpty(name)){
            userName.setError("Please enter your name");
        }
        if (TextUtils.isEmpty(email)){
            userEmail.setError("Please enter your email");
        }
        if (TextUtils.isEmpty(contact)){
            userContact.setError("Please enter your contact");
        }
        if (TextUtils.isEmpty(password)){
            userPassword.setError("Please enter your password");
        }



        else {
            count = true;
        }
        return count;
    }

    private void sendUserData(){
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        @SuppressLint("RestrictedApi") DatabaseReference myRef = firebaseDatabase.getReference(firebaseAuth.getUid()).child("User");
        UserProfile userProfile = new UserProfile(name,email,contact);
        myRef.setValue(userProfile);
    }
}
