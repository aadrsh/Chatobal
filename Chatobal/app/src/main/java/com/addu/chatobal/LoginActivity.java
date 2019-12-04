package com.addu.chatobal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.database.FirebaseDatabase;

public class LoginActivity extends AppCompatActivity {

    FirebaseAuth mAuth=FirebaseAuth.getInstance();
    FirebaseAuth.AuthStateListener authStateListener;
    private Button button_login;
    private EditText email,password;
    private TextView toSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        toSignUp=findViewById(R.id.toSignup);
        toSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this,MainActivity.class));
                finish();
            }
        });

        button_login=findViewById(R.id.login_real);
        email=findViewById(R.id.auth_email_login);
        password=findViewById(R.id.auth_password_login);

        button_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkEmailPass())return;
                mAuth.signInWithEmailAndPassword(email.getText().toString(), password.getText().toString()).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {

                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            mAuth.removeAuthStateListener(authStateListener);
                                        startActivity(new Intent(LoginActivity.this, ChatScreen.class));
                                        finish();
                        }else{
                            Log.e("First Stack Trace",task.getException().toString());
                            if(task.getException() instanceof FirebaseAuthUserCollisionException)
                                Toast.makeText(LoginActivity.this, "User Already Exists", Toast.LENGTH_SHORT).show();
                            else if(task.getException() instanceof FirebaseAuthInvalidCredentialsException)
                                Toast.makeText(LoginActivity.this, "Invalid Email or Password", Toast.LENGTH_SHORT).show();
                            else
                                Toast.makeText(LoginActivity.this, "Signin Failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }

        });

        authStateListener=new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser()!=null){
                    startActivity(new Intent(LoginActivity.this,ChatScreen.class));
                finish();}
            }
        };
    }

    private boolean checkEmailPass() {
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