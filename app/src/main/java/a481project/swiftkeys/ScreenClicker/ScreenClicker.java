package a481project.swiftkeys.ScreenClicker;

import android.util.Log;
import android.view.KeyEvent;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by mausilio on 3/20/2017.
 */

public class ScreenClicker {
    final static String TAG = ScreenClicker.class.getName();
    final static String[] LEFT_COMMAND ={ "sendevent /dev/input/event0 2  0  -5000;",
            "sendevent /dev/input/event0 0  0  0;",
            "sendevent /dev/input/event0 2  1  5000;",
            "sendevent /dev/input/event0 0  0  0;",
            "sendevent /dev/input/event0 4  4  589825;",
            "sendevent /dev/input/event0 1  272  1;",
            "sendevent /dev/input/event0 3  40  1;",
            "sendevent /dev/input/event0 0  0  0;",
            "sendevent /dev/input/event0 4  4  589825;",
            "sendevent /dev/input/event0 1  272  0;",
            "sendevent /dev/input/event0 3  40  0;",
            "sendevent /dev/input/event0 0  0  0;"};
    final static String[] CENTER_COMMAND = { "sendevent /dev/input/event0 2  0  -5000;",
            "sendevent /dev/input/event0 0  0  0;",
            "sendevent /dev/input/event0 2  1  5000;",
            "sendevent /dev/input/event0 0  0  0;",
            "sendevent /dev/input/event0 2  0  650;",
            "sendevent /dev/input/event0 0  0  0;",
            "sendevent /dev/input/event0 4  4  589825;",
            "sendevent /dev/input/event0 1  272  1;",
            "sendevent /dev/input/event0 3  40  1;",
            "sendevent /dev/input/event0 0  0  0;",
            "sendevent /dev/input/event0 4  4  589825;",
            "sendevent /dev/input/event0 1  272  0;",
            "sendevent /dev/input/event0 3  40  0;",
            "sendevent /dev/input/event0 0  0  0;"};
    final static String[] RIGHT_COMMAND = {"sendevent /dev/input/event0 2  0  -5000;",
            "sendevent /dev/input/event0 0  0  0;",
            "sendevent /dev/input/event0 2  1  5000;",
            "sendevent /dev/input/event0 0  0  0;",
            "sendevent /dev/input/event0 2  0  1090;",
            "sendevent /dev/input/event0 0  0  0;",
            "sendevent /dev/input/event0 4  4  589825;",
            "sendevent /dev/input/event0 1  272  1;",
            "sendevent /dev/input/event0 3  40  1;",
            "sendevent /dev/input/event0 0  0  0;",
            "sendevent /dev/input/event0 4  4  589825;",
            "sendevent /dev/input/event0 1  272  0;",
            "sendevent /dev/input/event0 3  40  0;",
            "sendevent /dev/input/event0 0  0  0;"};
    public static boolean isScreenClickKey(int keyCode){
        switch (keyCode){
            case KeyEvent.KEYCODE_DPAD_CENTER:
            case KeyEvent.KEYCODE_BUTTON_THUMBL:
            case KeyEvent.KEYCODE_BUTTON_THUMBR:
            case KeyEvent.KEYCODE_BUTTON_MODE:
            case KeyEvent.KEYCODE_MENU:
                return true;
        }
        return false;
    }
    public static void handleScreenClickKey(int keyCode, int keyAction){
        if(keyAction != KeyEvent.ACTION_UP){ return; }
        switch (keyCode){
            case KeyEvent.KEYCODE_DPAD_CENTER:
            case KeyEvent.KEYCODE_MENU:
                return;
            case KeyEvent.KEYCODE_BUTTON_THUMBL:
                Log.i(TAG, "autocomplete left");
                clickLeftComplete();
                break;
            case KeyEvent.KEYCODE_BUTTON_THUMBR:
                Log.i(TAG, "autocomplete center");
                clickCenterComplete();
                break;
            case KeyEvent.KEYCODE_BUTTON_MODE:
                Log.i(TAG, "autocomplete right");
                clickRightComplete();
                break;
        }

    }
    private static void clickLeftComplete(){
        new Thread(){
            @Override
            public void run(){
                Log.i(TAG, "Hello from other thread");
                Process p;
                try {
                    p = Runtime.getRuntime().exec("sh");
                    DataOutputStream os = new DataOutputStream(p.getOutputStream());
                    for (int i = 0; i < LEFT_COMMAND.length; ++i) {
                        os.writeBytes(LEFT_COMMAND[i]);
                    }
                    os.writeBytes("exit\n");
                    os.flush();
                }
                catch (Exception e){
                    Log.e(TAG, "sending command failed", e);
                }
            }
        }.start();
    }
    private static void clickCenterComplete(){
        new Thread(){
            @Override
            public void run(){
                Log.i(TAG, "Hello from other thread");
                Process p;
                try {
                    p = Runtime.getRuntime().exec("sh");
                    DataOutputStream os = new DataOutputStream(p.getOutputStream());
                    for (int i = 0; i < CENTER_COMMAND.length; ++i) {
                        os.writeBytes(CENTER_COMMAND[i]);
                    }
                    os.writeBytes("exit\n");
                    os.flush();
                }
                catch (Exception e){
                    Log.e(TAG, "sending command failed", e);
                }
            }
        }.start();
    }
    private static void clickRightComplete(){
        new Thread(){
            @Override
            public void run(){
                Log.i(TAG, "Hello from other thread");
                Process p;
                try {
                    p = Runtime.getRuntime().exec("sh");
                    DataOutputStream os = new DataOutputStream(p.getOutputStream());
                    for (int i = 0; i < RIGHT_COMMAND.length; ++i) {
                        os.writeBytes(RIGHT_COMMAND[i]);
                    }
                    os.writeBytes("exit\n");
                    os.flush();
                }
                catch (Exception e){
                    Log.e(TAG, "sending command failed", e);
                }
            }
        }.start();
    }
}
