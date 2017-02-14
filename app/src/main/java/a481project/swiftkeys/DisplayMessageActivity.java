package a481project.swiftkeys;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.ViewGroup;
import android.widget.TextView;

public class DisplayMessageActivity extends AppCompatActivity {
    /* GPIO Testing */
    private static final String TAG = "HomeActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /* setContentView(R.layout.activity_display_message);
        PeripheralManagerService manager = new PeripheralManagerService();
        List<String> portList = manager.getGpioList();
        String message = Integer.toString(portList.size()); */

        Intent intent = getIntent();
        String message = intent.getStringExtra(SwiftKeys.EXTRA_MESSAGE);
        TextView textView = new TextView(this);
        textView.setTextSize(40);
        textView.setText(message);

        ViewGroup layout = (ViewGroup) findViewById(R.id.activity_display_message);
        layout.addView(textView);
    }
}
