package com.addu.chatobal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.QuickContactBadge;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {
FirebaseAuth mAuth=FirebaseAuth.getInstance();
FirebaseAuth.AuthStateListener authStateListener;
private Button button_login;
private TextView toLogin;
private EditText email,password,username;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);



        username=findViewById(R.id.auth_username);
        button_login=findViewById(R.id.login);
        email=findViewById(R.id.auth_email);
        password=findViewById(R.id.auth_password);

        button_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkEmailPass())return;
                mAuth.createUserWithEmailAndPassword(email.getText().toString(), password.getText().toString()).addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {

                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            mAuth.removeAuthStateListener(authStateListener);
                            FirebaseDatabase.getInstance().getReference("Users").child(mAuth.getUid()).setValue(username.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()) {
                                        startActivity(new Intent(MainActivity.this, ChatScreen.class));
                                        finish();
                                    }else
                                        Toast.makeText(MainActivity.this, "DB Exception", Toast.LENGTH_SHORT).show();
                                    }
                            });

                        }else{
                            Log.e("First Stack Trace",task.getException().toString());
                            if(task.getException() instanceof FirebaseAuthUserCollisionException)
                                Toast.makeText(MainActivity.this, "User Already Exists", Toast.LENGTH_SHORT).show();
                            else if(task.getException() instanceof FirebaseAuthInvalidCredentialsException)
                                Toast.makeText(MainActivity.this, "Invalid Email or Password", Toast.LENGTH_SHORT).show();
                            else
                            Toast.makeText(MainActivity.this, "Signin Failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }

        });

        authStateListener=new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser()!=null){
                    startActivity(new Intent(MainActivity.this,ChatScreen.class));
                    finish();
                }}
        };

        toLogin=findViewById(R.id.toLogin);
        toLogin.setClickable(true);
        toLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "It's Clicked", Toast.LENGTH_SHORT).show();
                Intent intent=new Intent(MainActivity.this,LoginActivity.class);
                startActivity(intent);
                finish();
                Log.i("ACT","Its supposed to happen");

            }
        });
}

private boolean checkEmailPass() {
        if(username.getText().toString().isEmpty()){
            username.setError("Empty Field Not Allowed");
        return true;
        }
    if (email.getText().toString().isEmpty()){
       email.setError("Invalid Email");
        return true;
    }
    if (password.getText().toString().isEmpty()){
        return true;}
    if (password.getText().toString().length() <= 7){
        password.setError("Password must be > 7");
        return true;
    }
    return false;
}

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
    mAuth.removeAuthStateListener(authStateListener);
    }

}