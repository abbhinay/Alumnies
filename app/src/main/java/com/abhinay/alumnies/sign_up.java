package com.abhinay.alumnies;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

public class sign_up extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    static final String CHAT_PREFS = "ChatPrefs";
    static final String DISPLAY_NAME_KEY = "username";
    static final String DISPLAY_EMAIL_KEY = "emailId";
    static final String DISPLAY_PASSWORD_KEY = "password";

    private AutoCompleteTextView m_signUp_username;
    private AutoCompleteTextView m_signUp_emailId;
    private EditText m_signUp_password;
    private EditText m_signUp_confirm_password;
    private EditText m_current_city;
    private ImageButton m_image_button;
    private EditText m_branch;
    private Boolean m_is_alumni;
    private Spinner spinner;


    private String branchSelected;
    private String alumniOrStudent = null;
    private int PICK_IMAGE = 1;
    private Uri imageUri;
    private String imageUrlToFirestore;
    String picName;
    String userName;
    String email;
    String password;
    String city;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseDatabase database;
    private FirebaseStorage storage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        m_signUp_username = (AutoCompleteTextView) findViewById(R.id.signUp_username);
        m_signUp_emailId = (AutoCompleteTextView) findViewById(R.id.signUp_emailId);
        m_signUp_password = (EditText) findViewById(R.id.signUp_password);
        m_signUp_confirm_password = (EditText) findViewById(R.id.signUp_confirm_password);
        m_current_city = (EditText) findViewById(R.id.current_city);
        m_image_button = (ImageButton) findViewById(R.id.imageButton);


        spinner = (Spinner) findViewById(R.id.branch);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.branch_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
    }

    public void signUpClick(View v){
        Log.d("abhi", "clicked");
        attemptRegistration();
    }

    public void attemptRegistration(){
        m_signUp_emailId.setError(null);
        m_signUp_password.setError(null);
        m_current_city.setError(null);

        userName = m_signUp_username.getText().toString();
        email = m_signUp_emailId.getText().toString();
        password = m_signUp_password.getText().toString();
        city = m_current_city.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if(!TextUtils.isEmpty(password) && !isPasswordValid(password)){
            m_signUp_password.setError("Password too short or does not match");
            focusView = m_signUp_password;
            cancel = true;
        }

        if (TextUtils.isEmpty(email)) {
            m_signUp_emailId.setError("This field is required");
            focusView = m_signUp_emailId;
            cancel = true;
        } else if (!isEmailValid(email)) {
            m_signUp_emailId.setError("This email address is invalid");
            focusView = m_signUp_emailId;
            cancel = true;
        }

        if(TextUtils.isEmpty(city)){
            m_current_city.setError("Please enter your city name");
            focusView = m_current_city;
            cancel = true;
        }

        if(alumniOrStudent == null){
            showErrorDialog("please select 'alumni' or 'student'");
        }


        if (cancel) {
            focusView.requestFocus();
        } else {
            // TODO: Call create FirebaseUser() here
            createFirebaseUser();

            //TODO: Call uploadPicToStorage
            uploadPicToStorage(userName);

            // TODO: Call create Firebase Firestore for signup
            //saveUserOnFirestore(userName, email, city, branchSelected, alumniOrStudent);

        }

    }

    private boolean isEmailValid(String email) {
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        String confirmPassword = m_signUp_confirm_password.getText().toString();
        return confirmPassword.equals(password) && password.length() > 4;
    }

    private void createFirebaseUser(){
        String email = m_signUp_emailId.getText().toString();
        String password = m_signUp_password.getText().toString();

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this,
                new OnCompleteListener<AuthResult>() {

                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d("FlashChat", "createUser onComplete: " + task.isSuccessful());

                        if(!task.isSuccessful()){
                            Log.d("FlashChat", "user creation failed");
                            showErrorDialog("Registration attempt failed");
                        } else {
                            saveDisplayName();
                            Intent intent = new Intent(sign_up.this, sign_in.class);
                            finish();
                            startActivity(intent);
                        }
                    }
                });

    }



    public void saveUserOnFirestore(String userName, String email, String city, String branch, String alumniOrStudent){
        if(alumniOrStudent == "alumni"){
            Map<String, Object> user = new HashMap<>();
            user.put("name", userName);
            user.put("email", email);
            user.put("city", city);
            user.put("branch", branch);
            user.put("image", imageUrlToFirestore);

            db.collection("alumni")
                    .add(user)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Log.d("firestore", "DocumentSnapshot added with ID: " + documentReference.getId());
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w("firestore", "Error adding document", e);
                        }
                    });
        }
    }

    public void onRadioButtonClicked(View v){
        boolean checked = ((RadioButton) v).isChecked();
        switch(v.getId()){
            case R.id.alumniButton:
                if(checked){
                    alumniOrStudent = "alumni";
                    Log.d("radio", alumniOrStudent);
                }
                break;

            case R.id.studentButton:
                if(checked){
                    alumniOrStudent = "student";
                    Log.d("radio", alumniOrStudent);
                }
                break;
        }
    }

    private void saveDisplayName() {
        String displayName = m_signUp_username.getText().toString();
        String emailId = m_signUp_emailId.getText().toString();
        String password = m_signUp_password.getText().toString();
        SharedPreferences prefs = getSharedPreferences(CHAT_PREFS, 0);
        prefs.edit().putString(DISPLAY_NAME_KEY, displayName).apply();
        prefs.edit().putString(DISPLAY_EMAIL_KEY, emailId).apply();
        prefs.edit().putString(DISPLAY_PASSWORD_KEY, password).apply();
    }


    //SELECTING IMAGE AND DOING SOMETHING WITH IT
    public void selectImage(View v){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == PICK_IMAGE) {
            Log.d("image", "image selected");

//            Uri selectedImageUri = data.getData();
//            String selectedImagePath = getPath(selectedImageUri);
//            System.out.println("Image Path : " + selectedImagePath);
//            m_image_button.setImageURI(selectedImageUri);

            imageUri = data.getData();
            String selectedImagePath = getPath(imageUri);
            System.out.println("Image Path : " + selectedImagePath);
            m_image_button.setImageURI(imageUri);
        }
    }

    public String getPath(Uri uri) {
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    public void uploadPicToStorage(String name){
        picName = name.replaceAll("[-+.^:,_]", "");
        final StorageReference storageReference = storage.getReference();
        storageReference.child("Uploads").child(picName).putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {


                getUrl();


                Log.d("fileName12", "after getUrl");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(sign_up.this, "file not uploaded", Toast.LENGTH_SHORT).show();
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                int currentProgress = (int) (100*taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
                //mProgressDialog.setProgress(currentProgress);
            }
        });
    }

    private void getUrl() {
        Log.d("fileName12", "inside getUrl");
        final StorageReference storageReference = storage.getReference();
        storageReference.child("Uploads/"+picName).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                imageUrlToFirestore = uri.toString();
                Log.d("image", imageUrlToFirestore);
                saveUserOnFirestore(userName, email, city, branchSelected, alumniOrStudent);
                //uploadToFirestore();

                //Log.d("fileName12", urlToDatabase);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.d("fileName12", "inside failure");
                getUrl();
            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (position){
            case 0:
                branchSelected = "Electronics";
                break;
            case 1:
                branchSelected = "Computer Science";
                break;
            case 2:
                branchSelected = "Mechanical";
                break;
            case 3:
                branchSelected = "Civil";
                break;
            case 4:
                branchSelected = "Petroleum";
                break;
            case 5:
                branchSelected = "Chemical";
                break;
        }
        Log.d("spinner", branchSelected);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private void showErrorDialog(String message){

        new AlertDialog.Builder(this)
                .setTitle("Oops")
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();

    }
}
