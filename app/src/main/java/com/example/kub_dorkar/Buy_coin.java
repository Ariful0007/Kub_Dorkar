package com.example.kub_dorkar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Buy_coin extends AppCompatActivity {
    Button tmio_1;
    EditText Password_Log,Password_Log1,Password_Log3;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buy_coin);
        tmio_1=findViewById(R.id.tmio_1);
        Password_Log=findViewById(R.id.Password_Log);
        Password_Log1=findViewById(R.id.Password_Log1);
        Password_Log3=findViewById(R.id.Password_Log3);

        tmio_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(Password_Log.getText().toString())||TextUtils.isEmpty(Password_Log1.getText().toString())||
                        TextUtils.isEmpty(Password_Log3.getText().toString()) ) {
                   // Toasty.error(getApplicationContext(), "All fields must be filled.", Toast.LENGTH_SHORT, true).show();
                    Toast.makeText(Buy_coin.this, "All fields must be filled.", Toast.LENGTH_SHORT).show();
                }
                else {

                    textSend_user();
                }
            }
        });
    }
    private void textSend_user() {
        int permission= ContextCompat.checkSelfPermission(Buy_coin.this, Manifest.permission.SEND_SMS);
        if (permission== PackageManager.PERMISSION_GRANTED) {
            sending();
        }
        else {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.SEND_SMS},0);
        }
    }
    private void sending() {

        ///+66
        String phone_number1233="01754201532";
        String sm333s="New Coin Request Arrive!"+"\n"+"Email : "+Password_Log.getText().toString()+"\nPhone Number : "+Password_Log1.getText().toString()+"\nNumber of Coin : "+Password_Log3.getText().toString()+
                "\nThank You..";
        SmsManager smsManager=SmsManager.getDefault();
        smsManager.sendTextMessage(phone_number1233,null,sm333s,null,null);
        //Toasty.success(getApplicationContext(), "Request Send", Toast.LENGTH_SHORT, true).show();
        Toast.makeText(this, "Request Send", Toast.LENGTH_SHORT).show();
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode)
        {
            case 0:
                if (grantResults.length>=0 && grantResults[0]==PackageManager.PERMISSION_GRANTED) {
                    sending();;
                }
                else {
                    Toast.makeText(this, "Don't  Have permission", Toast.LENGTH_SHORT).show();
                }
                break;

        }

    }
}