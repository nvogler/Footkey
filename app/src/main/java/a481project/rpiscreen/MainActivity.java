package a481project.rpiscreen;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.projection.MediaProjectionManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.Arrays;
import java.util.HashMap;

public class MainActivity extends Activity {
    private static final String TAG = "MainActivity";

    private static final String PREF_COMMON = "common";
    private static final String PREF_KEY_RECEIVER = "receiver";

    private static final int REQUEST_MEDIA_PROJECTION = 100;
    private static final String STATE_RESULT_CODE = "result_code";
    private static final String STATE_RESULT_DATA = "result_data";

    private Context mContext;
    private MediaProjectionManager mMediaProjectionManager;
    private Handler mHandler = new Handler(new HandlerCallback());
    private Messenger mMessenger = new Messenger(mHandler);
    private Messenger mServiceMessenger = null;
    private ListView mDiscoverListView;
    private ArrayAdapter<String> mDiscoverAdapter;
    private HashMap<String, String> mDiscoverdMap;
    private String mReceiverIp = "";
    private DiscoveryTask mDiscoveryTask;
    private int mResultCode;
    private Intent mResultData;

    private class HandlerCallback implements Handler.Callback {
        public boolean handleMessage(Message msg) {
            Log.d(TAG, "Handler got event, what: " + msg.what);
            return false;
        }
    }

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(TAG, "Service connected, name: " + name);
            mServiceMessenger = new Messenger(service);
            try {
                Message msg = Message.obtain(null, Common.MSG_REGISTER_CLIENT);
                msg.replyTo = mMessenger;
                mServiceMessenger.send(msg);
                Log.d(TAG, "Connected to service, send register client back");
            } catch (RemoteException e) {
                Log.d(TAG, "Failed to send message back to service, e: " + e.toString());
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(TAG, "Service disconnected, name: " + name);
            mServiceMessenger = null;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState != null) {
            mResultCode = savedInstanceState.getInt(STATE_RESULT_CODE);
            mResultData = savedInstanceState.getParcelable(STATE_RESULT_DATA);
        }

        mContext = this;
        mMediaProjectionManager = (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);

        mDiscoverdMap = new HashMap<>();
        mDiscoverListView = (ListView) findViewById(R.id.discover_listview);
        mDiscoverAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1);
        mDiscoverAdapter.addAll(mDiscoverdMap.keySet());
        mDiscoverListView.setAdapter(mDiscoverAdapter);
        mDiscoverListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String name = mDiscoverAdapter.getItem(i);
                String ip = mDiscoverdMap.get(name);
                Log.d(TAG, "Select receiver name: " + name + ", ip: " + ip);
                mReceiverIp = ip;
                mContext.getSharedPreferences(PREF_COMMON, 0).edit().putString(PREF_KEY_RECEIVER, mReceiverIp).commit();
            }
        });

        mReceiverIp = mContext.getSharedPreferences(PREF_COMMON, 0).getString(PREF_KEY_RECEIVER, "");
        startService();
    }

    @Override
    public void onResume() {
        super.onResume();

        // start discovery task
        mDiscoveryTask = new DiscoveryTask();
        mDiscoveryTask.execute();
    }

    @Override
    public void onPause() {
        super.onPause();
        mDiscoveryTask.cancel(true);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        doUnbindService();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_start) {
            Log.d(TAG, "==== start ====");
            if (mReceiverIp != null) {
                startCaptureScreen();
            }
            return true;
        } else if (id == R.id.action_stop) {
            Log.d(TAG, "==== stop ====");
            stopScreenCapture();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_MEDIA_PROJECTION) {
            if (resultCode != Activity.RESULT_OK) {
                Log.d(TAG, "User cancelled");
                Toast.makeText(mContext, R.string.user_cancelled, Toast.LENGTH_SHORT).show();
                return;
            }
            Log.d(TAG, "Starting screen capture");
            mResultCode = resultCode;
            mResultData = data;
            startCaptureScreen();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mResultData != null) {
            outState.putInt(STATE_RESULT_CODE, mResultCode);
            outState.putParcelable(STATE_RESULT_DATA, mResultData);
        }
    }

    private void startCaptureScreen() {
        if (mResultCode != 0 && mResultData != null) {
            startService();
        } else {
            Log.d(TAG, "Requesting confirmation");
            // This initiates a prompt dialog for the user to confirm screen projection.
            startActivityForResult(
                    mMediaProjectionManager.createScreenCaptureIntent(),
                    REQUEST_MEDIA_PROJECTION);
        }
    }

    private void stopScreenCapture() {
        if (mServiceMessenger == null) {
            return;
        }
        final Intent stopCastIntent = new Intent(Common.ACTION_STOP_CAST);
        sendBroadcast(stopCastIntent);
    }

    private void startService() {
        if (mResultCode != 0 && mResultData != null && mReceiverIp != null) {
            Intent intent = new Intent(this, CastService.class);
            intent.putExtra(Common.EXTRA_RESULT_CODE, mResultCode);
            intent.putExtra(Common.EXTRA_RESULT_DATA, mResultData);
            intent.putExtra(Common.EXTRA_RECEIVER_IP, mReceiverIp);
            Log.d(TAG, "===== start service =====");
            startService(intent);
            bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
        } else {
            Intent intent = new Intent(this, CastService.class);
            startService(intent);
            bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
        }
    }

    private void doUnbindService() {
        if (mServiceMessenger != null) {
            try {
                Message msg = Message.obtain(null, Common.MSG_UNREGISTER_CLIENT);
                msg.replyTo = mMessenger;
                mServiceMessenger.send(msg);
            } catch (RemoteException e) {
                Log.d(TAG, "Failed to send unregister message to service, e: " + e.toString());
                e.printStackTrace();
            }
            unbindService(mServiceConnection);
        }
    }

    private class DiscoveryTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            try {
                DatagramSocket discoverUdpSocket = new DatagramSocket();
                Log.d(TAG, "Bind local port: " + discoverUdpSocket.getLocalPort());
                discoverUdpSocket.setSoTimeout(3000);
                byte[] buf = new byte[1024];
                while (true) {
                    if (!Utils.sendBroadcastMessage(mContext, discoverUdpSocket, Common.DISCOVER_PORT, Common.DISCOVER_MESSAGE)) {
                        Log.w(TAG, "Failed to send discovery message");
                    }
                    Arrays.fill(buf, (byte)0);
                    DatagramPacket receivePacket = new DatagramPacket(buf, buf.length);
                    try {
                        discoverUdpSocket.receive(receivePacket);
                        String ip = receivePacket.getAddress().getHostAddress();
                        Log.d(TAG, "Receive discover response from " + ip + ", length: " + receivePacket.getLength());
                        if (receivePacket.getLength() > 9) {
                            String respMsg = new String(receivePacket.getData());
                            Log.d(TAG, "Discover response message: " + respMsg);
                            try {
                                JSONObject json = new JSONObject(respMsg);
                                String name = json.getString("name");
                                String width = json.getString("width");
                                String height = json.getString("height");
                                mDiscoverdMap.put(name, ip);
                                mHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        mDiscoverAdapter.clear();
                                        mDiscoverAdapter.addAll(mDiscoverdMap.keySet());
                                    }
                                });
                                Log.d(TAG, "Got receiver name: " + name + ", ip: " + ip + ", width: " + width + ", height: " + height);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    } catch (SocketTimeoutException e) {
                    }

                    Thread.sleep(3000);
                }
            } catch (SocketException e) {
                Log.d(TAG, "Failed to create socket for discovery");
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
