package com.abhinay.alumnies;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class chatActivity extends AppCompatActivity {

    private String mDisplayName;
    private ListView mChatListView;
    private EditText mInputText;
    private ImageButton mSendButton;
    private DatabaseReference mDatabaseReference;

    private ChatListAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        setupDisplayName();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();

        mInputText = (EditText) findViewById(R.id.messageInput);
        mChatListView = (ListView) findViewById(R.id.chatList);
        mSendButton = (ImageButton) findViewById(R.id.sendButton);
    }

    public void setupDisplayName(){
        SharedPreferences prefs = getSharedPreferences(sign_up.CHAT_PREFS, MODE_PRIVATE);
        mDisplayName = prefs.getString(sign_up.DISPLAY_NAME_KEY, null);

        if(mDisplayName == null){
            mDisplayName = "Anonymous";
        }
    }

    public void sendMessage(View v){
        String input = mInputText.getText().toString();
        if(!input.equals("")){
            Log.d("abhinay", "inside if of sendMessage");
            InstantMessage chat = new InstantMessage(input, mDisplayName);
            mDatabaseReference.child("messages").push().setValue(chat);
            mInputText.setText("");
        }
    }

    @Override
    public void onStart(){
        super.onStart();
        mAdapter = new ChatListAdapter(this, mDatabaseReference, mDisplayName);
        mChatListView.setAdapter(mAdapter);
    }

    @Override
    public void onStop(){
        super.onStop();
        mAdapter.cleanup();
    }
}

