package com.example.chitchat.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chitchat.Adapter.MessagesAdapter;
import com.example.chitchat.ModelClass.Messages;
import com.example.chitchat.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {

    String receiverImage,receiverUID,receiverName,senderUID;
    CircleImageView receiverProfileImage;
    TextView userName;
    CardView btnSend;
    EditText etMessage;
    RecyclerView chatRecyclerview;
    ArrayList<Messages> messagesArrayList;
    String senderRoom,receiverRoom;

    MessagesAdapter messagesAdapter;

    public static String sImage;
    public static String rImage;

    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();

        receiverName = getIntent().getStringExtra("name");
        receiverImage = getIntent().getStringExtra("receiverName");
        receiverUID = getIntent().getStringExtra("uid");

        messagesArrayList = new ArrayList<>();

        senderUID = firebaseAuth.getUid();

        senderRoom = senderUID + receiverUID;
        receiverRoom = receiverUID + senderUID;

        userName = findViewById(R.id.userName);
        receiverProfileImage = findViewById(R.id.receiverProfileImage);

        chatRecyclerview = findViewById(R.id.chatRecyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        chatRecyclerview.setLayoutManager(linearLayoutManager);
        messagesAdapter = new MessagesAdapter(ChatActivity.this,messagesArrayList);
        chatRecyclerview.setAdapter(messagesAdapter);

        btnSend = findViewById(R.id.btnSend);
        etMessage = findViewById(R.id.etMessage);

        Picasso.get().load(receiverImage).into(receiverProfileImage);
        userName.setText(receiverName);

        DatabaseReference databaseReference = firebaseDatabase.getReference().child("user").child(firebaseAuth.getUid());
        DatabaseReference chatReference = firebaseDatabase.getReference().child("chats").child(senderRoom).child("messages");


        chatReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                messagesArrayList.clear();

                for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                    Messages messages = dataSnapshot.getValue(Messages.class);
                    messagesArrayList.add(messages);
                }
                messagesAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                sImage = snapshot.child("imageUri").getValue().toString();
                rImage = receiverImage;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = etMessage.getText().toString();
                if (message.isEmpty()){
                    Toast.makeText(ChatActivity.this, "Enter a Message to Send", Toast.LENGTH_SHORT).show();
                }else {
                    etMessage.setText("");
                    Date date = new Date();

                    Messages messages = new Messages(message,senderUID,date.getTime());

                    firebaseDatabase.getReference().child("chats")
                            .child(senderRoom)
                            .child("messages")
                            .push()
                            .setValue(messages).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    firebaseDatabase.getReference().child("chats")
                                            .child(receiverRoom)
                                            .child("messages")
                                            .push().setValue(messages).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {

                                                }
                                            });
                                }
                            });
                }
            }
        });
    }
}