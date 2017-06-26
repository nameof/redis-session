package com.casandroidclient;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.casandroidclient.component.CustomAlertDialogFactory;

import static android.R.id.message;

public class MainActivity extends AppCompatActivity {

    private String username;
    private String password;
    private TextView showName;
    private ProgressDialog mProgressDialog;
    private static final int REQUEST_SCAN = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        showName = (TextView) findViewById(R.id.username);
        Bundle extras = getIntent().getExtras();
        username = extras.getString("username");
        password = extras.getString("password");
        showName.setText("welcome " + username + " !");
        findViewById(R.id.btn_scan).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA}, 1);
                } else {
                    startActivityForResult(new Intent(MainActivity.this, CaptureActivity.class), REQUEST_SCAN);
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startActivityForResult(new Intent(MainActivity.this, CaptureActivity.class), REQUEST_SCAN);
                } else {
                    Toast.makeText(MainActivity.this, "拒绝", Toast.LENGTH_LONG).show();
                }
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_SCAN && resultCode == RESULT_OK) {
            if (mProgressDialog == null) {
                mProgressDialog  = CustomAlertDialogFactory.createProgressDialog(this,
                        "正在登录...", false);
            }
            mProgressDialog.show();
            final String result = data.getStringExtra("barCode");
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (null != mProgressDialog && mProgressDialog.isShowing()) {
                        mProgressDialog.dismiss();
                        Toast.makeText(MainActivity.this, result, Toast.LENGTH_LONG).show();
                    }
                }
            }, 2000);
        }
    }
}
