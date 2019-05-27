package com.abhinay.alumnies;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class sign_in extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private AutoCompleteTextView m_emailField;
    private EditText m_passwordField;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        m_emailField = findViewById(R.id.emailIdField);
        m_passwordField = findViewById(R.id.passwordField);

        setUpDisplayFields();

        Log.d("people", "inside on create");

        mAuth = FirebaseAuth.getInstance();
    }

    public void setUpDisplayFields(){
        //SharedPreferences prefs = getSharedPreferences(signUpActivity.CHAT_PREFS, MODE_PRIVATE);
        //String emailId = prefs.getString(signUpActivity.DISPLAY_EMAIL_KEY, null);
        //String password = prefs.getString(signUpActivity.DISPLAY_PASSWORD_KEY, null);

        //m_emailField.setText(emailId);
        //m_passwordField.setText(password);
    }

    public void signInExistingUser(View v){
        Log.d("people", "inside on signInExistingUser");
        attemptLogin();
    }

    private void attemptLogin(){
        Log.d("people", "inside on attemptLogin");
        String email = m_emailField.getText().toString();
        String password = m_passwordField.getText().toString();

        SharedPreferences prefs = getSharedPreferences("ChatPrefs", 0);
        prefs.edit().putString("emailId", email).apply();
        prefs.edit().putString("password", password).apply();

        if(email.equals("") || password.equals("")) return;
        Toast.makeText(this, "Login in progress...", Toast.LENGTH_SHORT).show();

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                Log.d("people", "inside on onComplete");

                Log.d("FlashChat", "signInWithEmail() onComplete: " + task.isSuccessful());

                if (!task.isSuccessful()) {
                    Log.d("people", "inside on taskisSuccessfull");
                    Log.d("FlashChat", "Problem signing in: " + task.getException());
                    showErrorDialog("There was a problem signing in");
                } else {
                    Intent intent = new Intent(sign_in.this, chatActivity.class);
                    finish();
                    startActivity(intent);
                }

            }
        });
    }

    private void showErrorDialog(String message) {

        new AlertDialog.Builder(this)
                .setTitle("Oops")
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }
}
