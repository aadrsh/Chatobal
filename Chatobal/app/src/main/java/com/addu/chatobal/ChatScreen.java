package com.addu.chatobal;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ChatScreen extends AppCompatActivity {
    static boolean backPressed=false;
    FirebaseAuth.AuthStateListener authStateListener;
    FirebaseAuth mAuth=FirebaseAuth.getInstance();
    ArrayList<String> chatList=new ArrayList<>();
    DatabaseReference dbRef;
    Button send;
    static String username;
    ArrayAdapter adapter;
    ListView chats;
    EditText textToSend;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_screen);



        chats=findViewById(R.id.List_view_chat);
        send=findViewById(R.id.button_Send);
        textToSend=findViewById(R.id.sendText);
        adapter=new ArrayAdapter(this,android.R.layout.simple_list_item_1,chatList);
        chats.setAdapter(adapter);
        chats.setClickable(false);


//        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        dbRef=FirebaseDatabase.getInstance().getReference();
        FirebaseDatabase.getInstance().getReference("Users/"+mAuth.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                username=dataSnapshot.getValue(String.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        dbRef.child("Chats").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                chatList.add((dataSnapshot.getValue(Chats.class)).toString());
        adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(textToSend.getText().toString().isEmpty())
                    return;
                DatabaseReference db=dbRef.child("Chats").push();
                db.setValue(new Chats(username,textToSend.getText().toString()));
                textToSend.setText("");
                new CountDownTimer(250,250){
                    @Override
                    public void onTick(long millisUntilFinished) {

                    }

                    @Override
                    public void onFinish() {
                        chats.smoothScrollToPosition(chats.getBottom());
                    }
                }.start();

            }
        });
    }
    @Override
    public void onBackPressed() {
        if(backPressed==false) {
            Toast.makeText(this, "Press back again to Exit !", Toast.LENGTH_SHORT).show();
            backPressed=true;
            new CountDownTimer(2000, 2000) {
                @Override
                public void onTick(long millisUntilFinished) {

                }

                @Override
                public void onFinish() {
                    backPressed=false;
                }
            }.start();
        }else {
            finish();
            finishAffinity();
            System.exit(0);
        }

    }
}
