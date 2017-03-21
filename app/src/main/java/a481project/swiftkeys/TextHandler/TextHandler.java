package a481project.swiftkeys.TextHandler;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.View;

import java.util.Vector;

import a481project.swiftkeys.BlueToothManager.BlueToothManager;
import a481project.swiftkeys.ScreenClicker.ScreenClicker;

/**
 * Created by Michael on 2/15/2017.
 */

public class TextHandler {
    private final String TAG = this.getClass().getName();
    Vector<String> typedText = new Vector<String>();
    String prevString = "";
    BlueToothManager mblueToothManager;
    private static final KeyCharacterMap mKeyCharacterMap = KeyCharacterMap.load(KeyCharacterMap.VIRTUAL_KEYBOARD);
    public TextHandler(BlueToothManager blueToothManager) {
        mblueToothManager = blueToothManager;
    }

    private final View.OnKeyListener keyListener = new View.OnKeyListener() {
        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            Log.i(TAG, "writing keycode : " + event.getKeyCode());
            if(ScreenClicker.isScreenClickKey(event.getKeyCode())){
                ScreenClicker.handleScreenClickKey(event.getKeyCode(), event.getAction());
            }
            else {
                mblueToothManager.write(event.getAction(), event.getKeyCode());
            }
            return false;
        }
    };
    private final TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String text = s.toString();
            Log.i(TAG, "# key pressed" + count);
        }

        @Override
        public void afterTextChanged(Editable s) {
            Log.i(TAG, "# of characters in text " + s.length());

            String text = s.toString();
            Log.i(TAG, "prev Text ='"+prevString +"'");
            Log.i(TAG, "text = '" + text+"'");

            //case where delete was pressed
            if (text.length() - prevString.length() == -1 && !(text.equals("") && prevString.equals(" "))){
                mblueToothManager.deleteCharacters(1);
            }
            else if(s.length() == 0){

            }
            else if (text.length() < prevString.length()){
                mblueToothManager.deleteCharacters(1);
            }
            //case where autocomplete occurs and replaces previously typed
            //characters
            else if(!text.substring(0, prevString.length()).equals(prevString)){
                mblueToothManager.deleteCharacters(prevString.length());
                mblueToothManager.sendString(text);
            }
            //case where characters are added to string
            else if(text.length() > prevString.length()){
                mblueToothManager.sendString(text.substring(prevString.length()));
            }

            prevString = text;
            if (s.length() != 0 && s.charAt(s.length() - 1) == ' ') {
                s.clear();
                prevString = "";
            }
        }
    };

    public TextWatcher getTextWatcher() {
        return textWatcher;
    }

    public View.OnKeyListener getKeyListener() {
        return keyListener;
    }
}
