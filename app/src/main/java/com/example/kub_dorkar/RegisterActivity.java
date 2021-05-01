package com.example.kub_dorkar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.kub_dorkar.model.Users1;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;

public class RegisterActivity extends AppCompatActivity {
Button signup_btn;
    private EditText regEmailText;
    private EditText regPassword;
    private EditText regConfirmPassword;

    private Button signupBtn;

    private ProgressBar regProgressBar;

    //firebase
    private FirebaseAuth mAuth;
    FirebaseFirestore firebaseFirestore;
    private DatabaseReference RootRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mAuth = FirebaseAuth.getInstance();
        firebaseFirestore=FirebaseFirestore.getInstance();

        regEmailText = (EditText) findViewById(R.id.signup_email);
        regPassword = (EditText) findViewById(R.id.signup_password);
        regConfirmPassword = (EditText) findViewById(R.id.signup_confirm_password);
        signupBtn = (Button)findViewById(R.id.signup_btn);
        regProgressBar = (ProgressBar)findViewById(R.id.reg_progress);
        signupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String email = regEmailText.getText().toString();
                final String password = regPassword.getText().toString();
                final String confirmPassword = regConfirmPassword.getText().toString();

                if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password) && !TextUtils.isEmpty(confirmPassword)){


                        regProgressBar.setVisibility(View.VISIBLE);
                        mAuth.createUserWithEmailAndPassword(password,confirmPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()){
                                    Users1 users=new Users1(email,password,confirmPassword);
                                    firebaseFirestore.collection("All_Users").document(password)
                                            .set(users)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        RootRef= FirebaseDatabase.getInstance().getReference();

                                                        String deviceToken= FirebaseInstanceId.getInstance().getToken();
                                                        String currentUserID=mAuth.getCurrentUser().getUid();
                                                        RootRef.child("Users").child(currentUserID).setValue("");
                                                        RootRef.child("Users").child(currentUserID).child("device_token").setValue(deviceToken)
                                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                        sendToStatus();

                                                                        Toast.makeText(RegisterActivity.this,"Account created Successfully...",Toast.LENGTH_SHORT).show();
                                                                    }
                                                                });



                                                    }
                                                }
                                            });

                                } else {

                                    String exception = task.getException().getMessage();
                                    Snackbar.make(findViewById(R.id.main_reg_layout), "Error : "+exception, Snackbar.LENGTH_SHORT).show();
                                }
                                regProgressBar.setVisibility(View.INVISIBLE);
                            }
                        });



                }

            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser!=null){
            sendToMain();
        }

    }

    private void sendToMain() {

        Intent mainIntent = new Intent(RegisterActivity.this,MainActivity.class);
        startActivity(mainIntent);
        finish();

    }

    private void sendToStatus(){

        Intent statusCheckIntent = new Intent(RegisterActivity.this,SetupActivity.class);
        statusCheckIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(statusCheckIntent);
    }
}
