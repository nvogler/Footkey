package a481project.rpiscreen;

/**
 * Created by Nic on 3/16/2017.
 */

import android.media.MediaFormat;


public class Common {
    public static final int VIEWER_PORT = 53515;

    public static final int DISCOVER_PORT = 53515;
    public static final String DISCOVER_MESSAGE = "hello";

    public static final int SCREEN_WIDTH = 1280;
    public static final int SCREEN_HEIGHT = 720;
    public static final int SCREEN_DPI = 320;
    public static final int VIDEO_BITRATE = 6144000;
    public static final int VIDEO_FPS = 25;
    public static final String VIDEO_MIME_TYPE = MediaFormat.MIMETYPE_VIDEO_AVC;

    // Activity to service
    public static final int MSG_REGISTER_CLIENT = 200;
    public static final int MSG_UNREGISTER_CLIENT = 201;
    public static final int MSG_STOP_CAST = 301;

    public static final String EXTRA_RESULT_CODE = "result_code";
    public static final String EXTRA_RESULT_DATA = "result_data";
    public static final String EXTRA_RECEIVER_IP = "receiver_ip";

    public static final String ACTION_STOP_CAST = "a481project.rpiscreen.ACTION_STOP_CAST";
}
