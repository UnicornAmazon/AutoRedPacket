package com.afscope.autoredbacket;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.GestureDescription;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Handler;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Toast;

import java.util.List;

public class WeChetService extends AccessibilityService {

    private static final String TAG = "WeChetService";
    private static final String ENVELOPE_TEXT_KEY = "[微信红包]";

    private static final String WECHET_PACKAGE_NAME = "com.tencent.mm";
    private static final String WECHET_LAUCHER = "com.tencent.mm.ui.LauncherUI";
    private static final String LUCKEY_MONEY_RECEIVER = "com.tencent.mm.plugin.luckymoney.ui.LuckyMoneyNotHookReceiveUI";
    private static final String LUCKEY_MONEY_DETAIL = "com.tencent.mm.plugin.luckymoney.ui.LuckyMoneyDetailUI";

    private Handler handler = new Handler();
    private CharSequence mClassName = "";
//    private boolean isOpenRP=false;

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        final int eventType = event.getEventType();
        switch (eventType) {
            case AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED:
                List<CharSequence> texts = event.getText();
                if (!texts.isEmpty()) {
                    for (CharSequence t : texts) {
                        String text = String.valueOf(t);
                        Log.d(TAG, "监控微信消息==" + text);
                        if (text.contains(ENVELOPE_TEXT_KEY)) {
                            openNotification(event);
                            break;
                        }
                    }
                }
                break;
            case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:
                openEnvelope(event);
                break;
            case AccessibilityEvent.TYPE_VIEW_SCROLLED:
            case AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED:
                openEnvelopeContent(event);
                break;
            default:
                break;
        }
    }

    /**
     * 打开通知栏
     *
     * @param event
     */
    private void openNotification(AccessibilityEvent event) {
        if (event.getParcelableData() == null || !(event.getParcelableData() instanceof Notification)) {
            return;
        }
        Notification notification = (Notification) event.getParcelableData();
        PendingIntent pendingIntent = notification.contentIntent;
        try {
            pendingIntent.send();
//            isOpenRP=false;
        } catch (PendingIntent.CanceledException e) {
            e.printStackTrace();
        }
    }

    private void openEnvelope(AccessibilityEvent event) {
        CharSequence className = event.getClassName();
        CharSequence packageName = event.getPackageName();
        boolean isWechetPage = WECHET_PACKAGE_NAME.equals(packageName);
        if (!isWechetPage) {
            return;
        }
        if (className.toString().contains(WECHET_PACKAGE_NAME)) {
            mClassName = className;
        }
        Log.i("openEnvelope", event.getClassName() + "");
        if (WECHET_LAUCHER.equals(className)) {
            findRedPacket();
        } else if (LUCKEY_MONEY_RECEIVER.equals(className)) {
            openRedPacket();
        } else if (LUCKEY_MONEY_DETAIL.equals(className)) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    clickBackKey();
                }
            }, 300);
            // Toast.makeText(this, "红包已经抢完--------", Toast.LENGTH_SHORT).show();
        }
    }

    private void openEnvelopeContent(AccessibilityEvent event) {
        Log.i("openEnvelopeContent", mClassName + "");
        if (mClassName.equals(WECHET_LAUCHER)) {
            findRedPacket();
        }
    }

    /**
     * 寻找领取红包关键字,找到之后点击打开
     */
    private void findRedPacket() {
//        if (isOpenRP){
//            return;
//        }
        AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
        if (nodeInfo == null) {
            return;
        }
        List<AccessibilityNodeInfo> list = nodeInfo.findAccessibilityNodeInfosByText("微信红包");
        if (list.isEmpty()) {
            return;
        }
//        for (AccessibilityNodeInfo info : list) {
        AccessibilityNodeInfo parent = list.get(list.size() - 1).getParent();
        if (parent != null) {
            parent.performAction(AccessibilityNodeInfo.ACTION_CLICK);
//                break;
        }
//        }
    }

    /**
     * 打开红包,加延时是因为红包界面出现会有延时
     * x 表示开字相对屏幕所在的横坐标, ｙ 表示开字相对屏幕所在的纵坐标，不同手机会有不一样
     */
    private void openRedPacket() {
        final int x = ScreenUtils.getWindowsWidth(WeChetService.this) / 2;
        final int y;
        int densityDpit = ScreenUtils.getDensityDpit(this);
        switch (densityDpit) {
            case 640://1440p
                y = 1575;
                break;
            case 480://1080p
                y = 1465;
                break;
            case 440://1080*2160
                y = 1250;
                break;
            case 420:
                y = 1213;
                break;
            case 320://720p
                y = 780;
                break;
            default:
                y = 1400;
        }
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                dispatchGestureView(x,y);
            }
        }, 300);
    }


    /**
     * 模拟点击事件
     *
     * @param x
     * @param y
     */
    private void dispatchGestureView(float x,float y) {
        GestureDescription.Builder builder = new GestureDescription.Builder();
        Path p = new Path();
        p.moveTo(x, y);
        builder.addStroke(new GestureDescription.StrokeDescription(p, 0L, 500L));
        GestureDescription gesture = builder.build();
        dispatchGesture(gesture, new GestureResultCallback() {
            @Override
            public void onCompleted(GestureDescription gestureDescription) {
                super.onCompleted(gestureDescription);
//                isOpenRP=true;
                Log.d(TAG, "onCompleted: 完成..........");
            }

            @Override
            public void onCancelled(GestureDescription gestureDescription) {
                super.onCancelled(gestureDescription);
                Log.d(TAG, "onCompleted: 取消..........");
            }
        }, null);
    }

    @Override
    public void onInterrupt() {
        Toast.makeText(this, "微信抢红包服务已关闭", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        Toast.makeText(this, "微信抢红包服务已开启", Toast.LENGTH_SHORT).show();
    }

    /**
     * 模拟返回键
     */
    private void clickBackKey() {
        performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

}
