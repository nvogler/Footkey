package a481project.swiftkeys;

import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.format.Formatter;
import android.util.Log;
import android.widget.EditText;

import a481project.swiftkeys.BlueToothManager.BlueToothManager;
import a481project.swiftkeys.TextHandler.TextHandler;

public class SwiftKeys extends AppCompatActivity {
    private final String TAG = this.getClass().getName();
    public final static String EXTRA_MESSAGE = "com.example.SwiftKeys.MESSAGE";
    public BlueToothManager blueToothManager;
    public TextHandler textHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_swift_keys);
    }
    @Override
    protected void onStart(){
        if(blueToothManager == null) {
            blueToothManager = new BlueToothManager("b62c4e8d-62cc-404b-bbbf-bf3e3bbb1376");//("7413bb3b-3ebf-bfbb-4b40-cc628d4e2cb6");//

        }

        if(!blueToothManager.isConnectionReady()) {
            Log.i(TAG, "Attempting to initiate connection.");
            WifiManager wm = (WifiManager) getSystemService(WIFI_SERVICE);
            Log.i(TAG, "The found ip was :" + Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress()));
            blueToothManager.initiateConnection(wm.getConnectionInfo().getIpAddress());
        }
        textHandler = new TextHandler(blueToothManager);
        EditText editText = (EditText) findViewById(R.id.edit_message);
        editText.setOnKeyListener(textHandler.getKeyListener());
        editText.addTextChangedListener(textHandler.getTextWatcher());
        super.onStart();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}