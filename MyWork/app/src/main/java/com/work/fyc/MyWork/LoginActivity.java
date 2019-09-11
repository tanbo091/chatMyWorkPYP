package com.work.fyc.MyWork;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.work.fyc.MyWork.entity.LoginResultEntity;
import com.work.fyc.MyWork.entity.ResultEntity;
import com.work.fyc.MyWork.net.HttpMethods;

import java.util.HashMap;
import java.util.Map;

import rx.Subscriber;


public class LoginActivity extends AppCompatActivity {

    private EditText username;
    private EditText userPassword;
    private Button login_bt;
    private TextView forget;
    private TextView visit;
    private TextView register;
    private MyHandler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN); //去除顶部状态栏

        SharedPreferences sharedPreferences = getSharedPreferences("USER", MODE_PRIVATE);
        if (!sharedPreferences.getAll().isEmpty()) {
            Intent toMain = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(toMain);
            finish();
        }

        init();//初始化控件对象

        //一般情况不是直接 new Click(),而是使用 onClickListener。
        // 不需要重新写一个 Class Click() 继承 OnClickListener
//        login_bt.setOnClickListener(new Click());
//        register.setOnClickListener(new Click());
//        forget.setOnClickListener(new Click());
//        visit.setOnClickListener(new Click());

        login_bt.setOnClickListener(onClickListener);
        register.setOnClickListener(onClickListener);
        forget.setOnClickListener(onClickListener);
        visit.setOnClickListener(onClickListener);

        //背景使用这样（ selector_press_unpress_shadow ）的按钮效果，可以避免代码 OnTouch 。
        // Touch() 一般用来处理更高端的手势识别
        //Ctrl + 左键  点击 selector_press_unpress_shadow 可以跳转过去直接查看代码
        register.setBackgroundResource(R.drawable.selector_press_unpress_shadow);
        forget.setBackgroundResource(R.drawable.selector_press_unpress_shadow);
        visit.setBackgroundResource(R.drawable.selector_press_unpress_shadow);
//        login_bt.setOnTouchListener(new Touch());
//        register.setOnTouchListener(new Touch());
//        forget.setOnTouchListener(new Touch());
//        visit.setOnTouchListener(new Touch());

    }

    //没必要重新写一个 Class . 使用 onClickListener 这个全局变量即可
    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.login_bt:
                    String userName = username.getText().toString();
                    String password = userPassword.getText().toString();
                    if (userName.isEmpty()) {
                        Toast.makeText(getApplication(), getResources().getString(R.string.need_user_name), Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (password.isEmpty()) {
                        Toast.makeText(getApplication(), getResources().getString(R.string.need_user_pwd), Toast.LENGTH_SHORT).show();
                        return;
                    }
                    Map<String, String> map = new HashMap<>();
                    map.put("name", userName);
                    map.put("password", password);
                    //这里给个retrofit2的示例。异步线程里调用登录API，在主线程里返回
                    HttpMethods.getInstance().requestSubscribe(
                            HttpMethods.getInstance().getApiService().loginApi(map),
                            new Subscriber<LoginResultEntity>() {
                                @Override
                                public void onCompleted() {
                                    //onNext 或者 onError 走完之后走这里
                                }

                                @Override
                                public void onError(Throwable e) {
                                    //如果网络报错，或者 onNext里代码报错，走这里
                                    failToast(e.getLocalizedMessage());
                                }

                                @Override
                                public void onNext(LoginResultEntity resultEntity) {
                                    //调用API返回数据。 即 { "result":"123" } 。这里已经在主线程了
                                    if (resultEntity.Result != null) {
                                        switch (resultEntity.Result) {
                                            case "success":
                                                SharedPreferences sharedPreferences = LoginActivity.this.getSharedPreferences("USER", Activity.MODE_PRIVATE);
                                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                                editor.putString("username", resultEntity.username);
                                                editor.putString("password", resultEntity.password);
                                                editor.putString("id", resultEntity.id);
                                                editor.putString("power", resultEntity.power);
                                                editor.apply();

                                                loginSuccess();
                                                break;
                                            case "user_error":
                                                failToast("用户不存在");
                                                break;
                                            case "password_error":
                                                failToast("密码错误");
                                                break;
                                        }
                                    }
                                }
                            }
                    );
//
//                    if ("".equals(username.getText().toString()) || "".equals(userPassword.getText().toString()))
//                        Toast.makeText(getApplication(), getResources().getString(R.string.no_complete), Toast.LENGTH_SHORT).show();
//                    else {
//                        Thread thread = new Thread(new Runnable() {
//                            @Override
//                            public void run() {
//                                JSONObject jsonObject = new JSONObject();
//                                try {
//                                    jsonObject.put("name", username.getText().toString());
//                                    jsonObject.put("password", userPassword.getText().toString());
//
//                                    NetWord netWord = new NetWord("http://2579f0157d.wicp.vip:80/MyForum1/login", jsonObject, handler);
//                                    jsonObject = netWord.doPost();
//
//                                    if (jsonObject == null) {
//                                    } else if (jsonObject.getString("result").equals("success")) {
//                                        JSONObject jsonObject1 = jsonObject.getJSONObject("user");
//
//                                        //保存登陆状态
//
//                                    } else if (jsonObject.getString("result").equals("user_error")) {
//                                        handler.sendEmptyMessage(2);
//                                    } else if (jsonObject.getString("result").equals("password_error")) {
//                                        handler.sendEmptyMessage(3);
//                                    } else {
//
//                                    }
//                                } catch (JSONException e) {
//                                    e.printStackTrace();
//                                }
//
//                            }
//                        });
//                        thread.start();
//                    }
                    break;
                case R.id.register:
                    Intent toReg = new Intent(LoginActivity.this, RegisterActivity.class);
                    startActivityForResult(toReg, 1);
                    break;
                case R.id.forget:
                    Toast.makeText(getApplication(), "敬请期待！！！", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.visit:
                    Intent toMain = new Intent(LoginActivity.this, MainActivity.class);
                    toMain.putExtra("isVisit", true);
                    startActivity(toMain);
                    finish();
                    break;
            }
        }
    };

    //没必要重新写一个 Class . 使用 onClickListener 这个全局变量即可
    private class Click implements View.OnClickListener {

        @Override
        public void onClick(View v) {
        }
    }

    private class Touch implements View.OnTouchListener {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (v.getId()) {
                case R.id.login_bt:
                    if (event.getAction() == MotionEvent.ACTION_DOWN)
                        login_bt.setBackgroundColor(Color.argb(80, 19, 86, 255));
                    else if (event.getAction() == MotionEvent.ACTION_UP)
                        login_bt.setBackgroundResource(R.drawable.bt_bg);
                    break;
                case R.id.register:
                    if (event.getAction() == MotionEvent.ACTION_DOWN)
                        register.setTextColor(Color.BLUE);
                    else if (event.getAction() == MotionEvent.ACTION_UP)
                        register.setTextColor(Color.BLACK);
                    break;
                case R.id.forget:
                    if (event.getAction() == MotionEvent.ACTION_DOWN)
                        forget.setTextColor(Color.BLUE);
                    else if (event.getAction() == MotionEvent.ACTION_UP)
                        forget.setTextColor(Color.BLACK);
                    break;
                case R.id.visit:
                    if (event.getAction() == MotionEvent.ACTION_DOWN)
                        visit.setTextColor(Color.BLUE);
                    else if (event.getAction() == MotionEvent.ACTION_UP)
                        visit.setTextColor(Color.BLACK);
                    break;
            }
            return false;
        }
    }

    private void init() {
        username = (EditText) findViewById(R.id.username);
        userPassword = (EditText) findViewById(R.id.userPassword);
        login_bt = (Button) findViewById(R.id.login_bt);
        forget = (TextView) findViewById(R.id.forget);
        visit = (TextView) findViewById(R.id.visit);
        register = (TextView) findViewById(R.id.register);

        handler = new MyHandler();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void loginSuccess() {
        Toast.makeText(getApplication(), "登陆成功", Toast.LENGTH_SHORT).show();
        Intent toMain = new Intent(LoginActivity.this, MainActivity.class);
        toMain.putExtra("isVisit", false);
        startActivity(toMain);
        finish();
    }

    private void failToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private class MyHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    Toast.makeText(getApplication(), "登陆成功", Toast.LENGTH_SHORT).show();
                    Intent toMain = new Intent(LoginActivity.this, MainActivity.class);
                    toMain.putExtra("isVisit", false);
                    startActivity(toMain);
                    finish();
                    break;
                case 2:
                    Toast.makeText(getApplication(), "用户不存在", Toast.LENGTH_SHORT).show();
                    break;
                case 3:
                    Toast.makeText(getApplication(), "密码错误", Toast.LENGTH_SHORT).show();
                    break;
                case 4:
                    Toast.makeText(getApplication(), "网络错误", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }
}
