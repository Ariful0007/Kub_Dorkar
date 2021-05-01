package com.example.kub_dorkar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import id.zelory.compressor.Compressor;

public class NewEventActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener
        , DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {


    private static final int READCODE = 1;
    private static final int WRITECODE = 2;
    private static final int LOCATION_GRANT_CODE = 667;
    public static final int PLACE_PICKER_REQUEST = 657;
    private static final String TAG = "NewEventActivity";

    private GoogleApiClient mClient;

    //Bottom sheet first
    private BottomSheetDialog mDialogNext;
    private BottomSheetDialog mDialog;
    private Switch isSingleDayEvent;
    public Menu mMenu;
    public Button confirm_details;
    public TextView eventDateTo, eventDateFrom;
    public EditText eventTitle,eventDesc, organizerContact;
    private ImageView eventImage;
    public Uri mEventUri = null;
    public FloatingActionButton placePicker;
    private ProgressBar confirmProgress;
    private String mTitle;
    private String mDescription;
    private String mDateTo;
    private String mDateFrom;
    private String mPhoneNumber;
    private Boolean isSingle = false ;

    //Bottom sheet second
    private TextView daySpinner;
    private ImageView addDay, removeDay;
    private EditText eventDayName;
    private TextView eventDayTimeTo, eventDayTimeFrom;
    private Button eventAddBtn, eventConfirmBtn;
    private ProgressBar eventConfirmProgress;
    private LinearLayout daySelectorLinearLayout;
    private Date mDateStart, mDateEnd;
    private Map<String, String> eventPerDaysMap = new HashMap<>();
    private int mCount = 0;
    private String current_user_id;
    private Bitmap compressedImageFile;
    private Bitmap compressedThumbFile;
    private String mPlaceId;



    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth mAuth;
    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.MainTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_event);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {

    }

    @Override
    public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}