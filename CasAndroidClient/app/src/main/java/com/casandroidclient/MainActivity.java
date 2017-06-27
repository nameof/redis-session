package com.casandroidclient;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.casandroidclient.component.CustomAlertDialogFactory;
import com.casandroidclient.utils.HttpRequest;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private String username;
    private String password;
    private TextView showName;
    private ProgressDialog mProgressDialog;
    private static final int REQUEST_SCAN = 0;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            mProgressDialog.dismiss();
            Toast.makeText(MainActivity.this,msg.obj.toString(),Toast.LENGTH_LONG).show();
        }
    };
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
        //处理二维码
        if (requestCode == REQUEST_SCAN && resultCode == RESULT_OK) {
            if (mProgressDialog == null) {
                mProgressDialog  = CustomAlertDialogFactory.createProgressDialog(this,
                        "正在登录...", false);
            }
            mProgressDialog.show();
            final String result = data.getStringExtra("barCode");
            new Thread(){
                @Override
                public void run() {
                    String msg = "";
                    try {
                        JSONObject json = new JSONObject(result);
                        String token = json.getString("sessionid");
                        Map<String, String> cookies = new HashMap<>();
                        cookies.put("token", token);
                        msg = HttpRequest.httpPost(HttpRequest.SERVER,
                                "name=" + username + "&passwd=" + password, cookies);
                    } catch (JSONException e) {
                        msg = "不合法的二维码!";
                    }catch (IOException e) {
                        msg = "请求失败!" + e;
                    }
                    Message message = handler.obtainMessage();
                    message.obj = msg;
                    handler.sendMessageDelayed(message, 2000);
                }
            }.start();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = new MenuInflater(this);
        inflater.inflate(R.menu.main_menu, menu);
        setIconEnable(menu,true);
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * 显示自定义菜单图标
     * @param menu
     * @param enable
     */
    private void setIconEnable(Menu menu, boolean enable)
    {
        try {
            Class<?> clazz = Class.forName("com.android.internal.view.menu.MenuBuilder");
            Method m = clazz.getDeclaredMethod("setOptionalIconsVisible", boolean.class);
            m.setAccessible(true);
            m.invoke(menu, enable);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_serverUrl:
                final EditText et = new EditText(this);
                et.setText(HttpRequest.SERVER);
                new AlertDialog.Builder(this).setTitle("设置服务器地址")
                        .setIcon(android.R.drawable.ic_dialog_info)
                        .setView(et)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                HttpRequest.SERVER = et.getText().toString();
                            }
                        })
                        .setNegativeButton("取消", null)
                        .show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
