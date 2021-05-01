package com.example.kub_dorkar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import id.zelory.compressor.Compressor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.kub_dorkar.model.Message_user;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class SetupActivity extends AppCompatActivity {
    private static final int READCODE = 1;
    private static final int WRITECODE = 2;

    private Uri mainImageUri = null;

    private CircleImageView setupImage;
    private EditText setupName;
    private TextView changePhoto;
    private EditText setupSkills;
    private ImageView setupButton;
    private ProgressBar setupProgress;
    private Boolean isChanged = false;
    private String current_user_id;
    private EditText aboutYou;
    private EditText emailUser;
    private EditText phoneNumber;
    private Bitmap compressedUserImage;
    private Toolbar mToolbar;

    String user_id = "";

    //firebase
    private StorageReference storageReference;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseFirestoreSettings settings;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;


    private String intentThatStartedThisActivity;
    TextView changeProfilePhoto;
    private final int PICK_IMAGE_REQUEST = 71;
    private Uri filePath;//Firebase
    DatabaseReference mDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.LoginTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);
        mToolbar = findViewById(R.id.profileToolBar);
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();

        actionBar.setTitle("Setup account");
        actionBar.setElevation(10.0f);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);


        firebaseAuth = FirebaseAuth.getInstance();
        user_id = firebaseAuth.getCurrentUser().getUid();
        firebaseFirestore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        firebaseFirestore.setFirestoreSettings(settings);
        firebaseDatabase = FirebaseDatabase.getInstance();
        changeProfilePhoto=findViewById(R.id.changeProfilePhoto);

        setupName = findViewById(R.id.setup_name);
        setupButton = findViewById(R.id.setup_button);
        setupProgress = findViewById(R.id.setup_progress);
        setupImage = findViewById(R.id.setup_image);
        setupSkills = findViewById(R.id.setup_skill);
        changePhoto = findViewById(R.id.changeProfilePhoto);
        aboutYou = findViewById(R.id.aboutyou);
        emailUser = findViewById(R.id.email);
        phoneNumber = findViewById(R.id.phoneNumber);
        changeProfilePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
chooseImage();
            }
        });
        changePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });


        intentThatStartedThisActivity = getIntent().getStringExtra("whichState");

        if(TextUtils.isEmpty(intentThatStartedThisActivity)){
            intentThatStartedThisActivity = "not_setup";
        }


        setupProgress.setVisibility(View.VISIBLE);
        setupButton.setEnabled(false);


        firebaseFirestore.collection("Users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if (task.isSuccessful()){

                    if (task.getResult().exists()){

                        String name = task.getResult().getString("name");
                        String image = task.getResult().getString("image");
                        String skills = task.getResult().getString("skills");
                        String about = task.getResult().getString("about");
                        String phone = task.getResult().getString("phone");

                        mainImageUri = Uri.parse(image);

                        setupName.setText(name);
                        setupSkills.setText(skills);
                        aboutYou.setText(about);
                        phoneNumber.setText(phone);
                        emailUser.setText(firebaseAuth.getCurrentUser().getEmail());

                        RequestOptions placeholderRequest = new RequestOptions();
                        placeholderRequest = placeholderRequest.placeholder(R.drawable.default_pic);
                        //Crashlytics.log(1,"GLIDE","Before setting setupActivity pic");
                       // Glide.with(getApplicationContext())
                              //  .setDefaultRequestOptions(placeholderRequest)
                               // .load(image)
                               // .into(setupImage);
                       // Crashlytics.log(1,"GLIDE","After setting setupActivity pic");
                    }

                } else {
                    Snackbar.make(findViewById(R.id.setup_layout),task.getException().getMessage(),Snackbar.LENGTH_SHORT).show();
                }
                setupProgress.setVisibility(View.INVISIBLE);
                setupButton.setEnabled(true);
            }
        });

        /*setupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String userName = setupName.getText().toString();
                final String userSkills = setupSkills.getText().toString();
                final String userAbout = aboutYou.getText().toString();
                final String userPhone = phoneNumber.getText().toString();
                final String userEmail = emailUser.getText().toString();

                if (!TextUtils.isEmpty(userName) &&mainImageUri!=null && !TextUtils.isEmpty(userSkills)) {
                    setupProgress.setVisibility(View.VISIBLE);
                    if (isChanged){

                        File newImageFile = new File(mainImageUri.getPath());

                        try {
                            compressedUserImage = new Compressor(SetupActivity.this)
                                    .setQuality(60)
                                    .compressToBitmap(newImageFile);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        compressedUserImage.compress(Bitmap.CompressFormat.JPEG,60,baos);
                        byte[] imageData = baos.toByteArray();

                        final StorageReference image_path = storageReference.child("profile_images").child(user_id + ".jpg");
                        image_path.putBytes(imageData).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                if (task.isSuccessful()) {

                                    storeFirestore(task, userName,userSkills,userAbout,userPhone,userEmail,image_path);

                                } else {
                                    Snackbar.make(findViewById(R.id.setup_layout), task.getException().getMessage(), Snackbar.LENGTH_SHORT).show();
                                    setupProgress.setVisibility(View.INVISIBLE);
                                }

                            }
                        });

                    } else {
                        storeFirestore(null,userName,userSkills,userAbout,userPhone,userEmail,null);
                    }
                } else {
                    Snackbar.make(findViewById(R.id.setup_layout), "Photo, name, email, skills and about are required", Snackbar.LENGTH_SHORT).show();
                }

            }
        });*/


        setupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String userName = setupName.getText().toString();
                final String userSkills = setupSkills.getText().toString();
                final String userAbout = aboutYou.getText().toString();
                final String userPhone = phoneNumber.getText().toString();
                final String userEmail = emailUser.getText().toString();
                if(filePath != null) {
                    setupProgress.setVisibility(View.VISIBLE);
                    final ProgressDialog progressDialog = new ProgressDialog(SetupActivity.this);
                    progressDialog.setTitle("Uploading...");
                    progressDialog.show();
                    final StorageReference image_path = storageReference.child("profile_images").child(user_id + ".jpg");
                    image_path.putFile(filePath)
                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    Task<Uri> uriTask=taskSnapshot.getStorage().getDownloadUrl();
                                    while (!uriTask.isSuccessful());
                                    final Uri downloadUri=uriTask.getResult();
                                    if (uriTask.isSuccessful()) {

                                        storeFirestore(taskSnapshot.getTask(), userName,userSkills,userAbout,userPhone,userEmail,image_path);

                                    }
                                    else {
                                        Snackbar.make(findViewById(R.id.setup_layout), taskSnapshot.getError().getMessage(), Snackbar.LENGTH_SHORT).show();
                                        setupProgress.setVisibility(View.INVISIBLE);
                                    }

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });

                }
            }
        });



    }



    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null )
        {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                setupImage.setImageBitmap(bitmap);
                mainImageUri=filePath;

                isChanged = true;
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    private void storeFirestore(Task<UploadTask.TaskSnapshot> task, String userName,
                                String userSkills,String userAbout,String userPhone,
                                String userEmail,StorageReference image_path){



        if (TextUtils.isEmpty(userPhone)){
            userPhone = "";
        }
        if (TextUtils.isEmpty(userName)){
            userName = "";
        }
        if (TextUtils.isEmpty(userSkills)){
            userSkills = "";
        }
        if (TextUtils.isEmpty(userAbout)){
            userAbout = "";
        }
        if (TextUtils.isEmpty(userEmail)){
            userEmail = firebaseAuth.getCurrentUser().getEmail();
        }

        if (image_path!=null){
            final String finalUserName = userName;
            final String finalUserSkills = userSkills;
            final String finalUserAbout = userAbout;
            final String finalUserPhone = userPhone;
            final String finalUserEmail = userEmail;
            image_path.getDownloadUrl().addOnSuccessListener(SetupActivity.this, new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    message_register(user_id,finalUserName,finalUserSkills,finalUserAbout,finalUserPhone,finalUserEmail,uri.toString());

                    Map<String, String> userMap = new HashMap<>();
                    userMap.put("user_id",user_id);
                    userMap.put("name", finalUserName);
                    userMap.put("skills", finalUserSkills);
                    userMap.put("about", finalUserAbout);
                    userMap.put("phone", finalUserPhone);
                    userMap.put("email", finalUserEmail);
                    userMap.put("image",uri.toString());


                    firebaseFirestore.collection("Users").document(user_id).set(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                Snackbar.make(findViewById(R.id.setup_layout),"Settings updated",Snackbar.LENGTH_SHORT).show();
                                Log.i("CHECK",intentThatStartedThisActivity);
                                sendToMain(intentThatStartedThisActivity);

                            } else {

                                Snackbar.make(findViewById(R.id.setup_layout),task.getException().getMessage(),Snackbar.LENGTH_SHORT).show();

                            }
                            databaseReference = firebaseDatabase.getReference();
                            databaseReference.child("Users").child(user_id).child("online").setValue(false);
                            setupProgress.setVisibility(View.INVISIBLE);
                        }
                    });

                }
            });
        } else {

            Map<String, String> userMap = new HashMap<>();
            userMap.put("user_id",user_id);
            userMap.put("name",userName);
            userMap.put("skills",userSkills);
            userMap.put("about",userAbout);
            userMap.put("phone",userPhone);
            userMap.put("email",userEmail);
            userMap.put("image",mainImageUri.toString());


            firebaseFirestore.collection("Users").document(user_id).set(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        Snackbar.make(findViewById(R.id.setup_layout),"Settings updated",Snackbar.LENGTH_SHORT).show();
                        Log.i("CHECK",intentThatStartedThisActivity);
                        sendToMain(intentThatStartedThisActivity);

                    } else {

                        Snackbar.make(findViewById(R.id.setup_layout),task.getException().getMessage(),Snackbar.LENGTH_SHORT).show();

                    }
                    databaseReference = firebaseDatabase.getReference();
                    databaseReference.child("Users").child(user_id).child("online").setValue(false);
                    setupProgress.setVisibility(View.INVISIBLE);
                }
            });

        }
    }

    private void message_register(String user_id, final String finalUserName,
                                  String finalUserSkills, String finalUserAbout,
                                  String finalUserPhone, String finalUserEmail, final String toString) {
        Message_user message_user=new Message_user(user_id,finalUserName,finalUserSkills,finalUserAbout,
                finalUserPhone,finalUserEmail,toString);
        firebaseFirestore.collection("User_Data")
                .document(finalUserEmail)
                .set(message_user)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser current_user = FirebaseAuth.getInstance().getCurrentUser();
                            String uid = current_user.getUid();

                            mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);

                            String device_token = FirebaseInstanceId.getInstance().getToken();

                            HashMap<String, String> userMap = new HashMap<>();
                            userMap.put("name", finalUserName);
                            userMap.put("status", "Hi there I'm using chatblo App.");
                            userMap.put("image", toString);
                            userMap.put("thumb_image", "default");
                            userMap.put("device_token", device_token);
                            mDatabase.setValue(userMap)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(SetupActivity.this, "Data Inserted", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        }

                    }
                });


    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (intentThatStartedThisActivity.equals("not_setup")){
            logout();
        }


    }

    private void sendToMain(String whichActivity){

        if (whichActivity.equals("not_setup")){
            Intent mainIntent = new Intent(SetupActivity.this,MainActivity.class);
            mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(mainIntent);
        } else {
            finish();

        }
    }

    /*@Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                mainImageUri = result.getUri();
                setupImage.setImageURI(mainImageUri);

                isChanged = true;

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Snackbar.make(findViewById(R.id.setup_layout), error.toString(), Snackbar.LENGTH_SHORT).show();

            }
        }
    }*/

    private void bringImagePicker(){
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1,1)
                .start(SetupActivity.this);
    }

    private void logout(){
        firebaseAuth.signOut();
        finish();
    }

}
