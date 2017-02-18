package a481project.swiftkeys;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import a481project.swiftkeys.BlueToothManager.BlueToothManager;
import a481project.swiftkeys.TextHandler.TextHandler;

public class SwiftKeys extends AppCompatActivity {
    public final static String EXTRA_MESSAGE = "com.example.SwiftKeys.MESSAGE";
    public BlueToothManager blueToothManager;
    public TextHandler textHandler;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        String TAG = this.getClass().getName();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_swift_keys);
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();


        blueToothManager = new BlueToothManager("b62c4e8d-62cc-404b-bbbf-bf3e3bbb1376");//("7413bb3b-3ebf-bfbb-4b40-cc628d4e2cb6");//
        Log.i(TAG, "Attempting to initiate connection.");
        blueToothManager.initiateConnection();
        textHandler = new TextHandler(blueToothManager);
        EditText editText = (EditText) findViewById(R.id.edit_message);
        editText.setOnKeyListener(textHandler.getKeyListener());
        editText.addTextChangedListener(textHandler.getTextWatcher());
    }

   /* PeripheralManagerService manager = new PeripheralManagerService();
    List<String> portList = manager.getGpioList();
    String message = Integer.toString(portList.size()); */
    /**
     * Called when the user clicks the Send button
     */
    public void sendMessage(View view) {
        EditText editText = (EditText) findViewById(R.id.edit_message);
        String message = editText.getText().toString();
        blueToothManager.sendString(message);
        editText.setText("");
        //Intent intent = new Intent(this, DisplayMessageActivity.class);
        //EditText editText = (EditText) findViewById(R.id.edit_message);
        //String message = editText.getText().toString();
        //intent.putExtra(EXTRA_MESSAGE, message);
        //startActivity(intent);
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("SwiftKeys Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }
}