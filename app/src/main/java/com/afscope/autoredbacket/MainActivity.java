package com.afscope.autoredbacket;

import androidx.appcompat.app.AppCompatActivity;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.accessibility.AccessibilityManager;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRandomIv();
        isWeChetServiceOpen();
    }

    private void setRandomIv() {
        int[] list={R.mipmap.icon_1,R.mipmap.icon_2,R.mipmap.icon_3,R.mipmap.icon_4,R.mipmap.icon_5};
        ImageView iv = findViewById(R.id.iv);
        Random random=new Random();
        int num = random.nextInt(5);
        iv.setImageResource(list[num]);
    }

    public static boolean isStartAccessibilityService(Context context, String name) {
        AccessibilityManager am = (AccessibilityManager) context.getSystemService(Context.ACCESSIBILITY_SERVICE);
        List<AccessibilityServiceInfo> serviceInfos = am.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_GENERIC);
        for (AccessibilityServiceInfo info : serviceInfos) {
            String id = info.getId();
            if (id.contains(name)) {
                return true;
            }
        }
        return false;
    }
    private void jumpToSettingPage() {
        Intent intent = new Intent(android.provider.Settings.ACTION_ACCESSIBILITY_SETTINGS);
        startActivity(intent);
    }
    private void isWeChetServiceOpen() {
        String weChetServiceName = ".WeChetService";
        boolean isWeChetServiceOpen = isStartAccessibilityService(this, weChetServiceName);
        if (isWeChetServiceOpen) {
            Toast.makeText(this,"已开启",Toast.LENGTH_LONG).show();
            Intent intent=new Intent(this,WeChetService.class);
            startService(intent);
        } else {
//            jumpToSettingPage();
            Toast.makeText(this,"没有开启",Toast.LENGTH_LONG).show();
        }
    }

    public void click(View view) {
        jumpToSettingPage();
    }
}
