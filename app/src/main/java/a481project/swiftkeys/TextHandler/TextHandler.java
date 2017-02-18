package a481project.swiftkeys.TextHandler;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;

import java.util.Vector;

import a481project.swiftkeys.BlueToothManager.BlueToothManager;

/**
 * Created by Michael on 2/15/2017.
 */

public class TextHandler {
    Vector<KeyEvent> pressedText;
    BlueToothManager mblueToothManager;

    public TextHandler(BlueToothManager blueToothManager) {
        mblueToothManager = blueToothManager;
    }

    private final View.OnKeyListener keyListener = new View.OnKeyListener() {
        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            mblueToothManager.write(event.getAction(), event.getKeyCode());
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
        }

        @Override
        public void afterTextChanged(Editable s) {
            String text = s.toString();
            if (s.length() != 0 && s.charAt(s.length() - 1) == ' ') {mblueToothManager.sendString(text);
                s.clear();
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
