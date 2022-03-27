package com.youmehe.ctstest;

import static com.youmehe.ctstest.MediaSessionTestService.KEY_EXPECTED_QUEUE_SIZE;
import static com.youmehe.ctstest.MediaSessionTestService.STEP_CHECK;
import static com.youmehe.ctstest.MediaSessionTestService.STEP_CLEAN_UP;
import static com.youmehe.ctstest.MediaSessionTestService.STEP_SET_UP;
import static com.youmehe.ctstest.MediaSessionTestService.TEST_SET_QUEUE;

import android.app.Activity;
import android.app.KeyguardManager;
import android.content.Context;
import android.media.MediaDescription;
import android.media.session.MediaController;
import android.media.session.MediaSession;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {
    public static final String KEY_SESSION_TOKEN = "KEY_SESSION_TOKEN";
    private static final String TAG = "MediaSessionTestActivity";

    private boolean mDeviceLocked;
    private boolean mResumed;

    // The maximum time to wait for an operation that is expected to succeed.
    private static final long TIME_OUT_MS = 3000L;
    // The maximum time to wait for an operation that is expected to fail.
    private static final long WAIT_MS = 100L;
    private static final int MAX_AUDIO_INFO_CHANGED_CALLBACK_COUNT = 10;
    private static final String TEST_SESSION_TAG = "test-session-tag";
    private static final String TEST_KEY = "test-key";
    private static final String TEST_VALUE = "test-val";
    private static final String TEST_SESSION_EVENT = "test-session-event";
    private static final String TEST_VOLUME_CONTROL_ID = "test-volume-control-id";
    private static final int TEST_CURRENT_VOLUME = 10;
    private static final int TEST_MAX_VOLUME = 11;
    private static final long TEST_QUEUE_ID = 12L;
    private static final long TEST_ACTION = 55L;
    private static final int TEST_TOO_MANY_SESSION_COUNT = 1000;

    private MediaSession mSession;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Wake up device.
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setTurnScreenOn(true);
        // Unlock device which is previously locked by power button press.
        // This is required even when the screen lock is set to 'None'.
        setShowWhenLocked(true);
        KeyguardManager keyguardManager =
            (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
        // KeyguardManager can be null for the instant mode.
        if (keyguardManager == null) {
            Log.i(TAG, "Unable to get KeyguardManager. Probably in the instant mode.");
        } else if (keyguardManager.isKeyguardLocked()) {
            Log.i(TAG, "Device is locked. Try unlocking and bring activity foreground.");
            mDeviceLocked = true;
            // Note: CTS requires 'no lock pattern or password is set on the device'.
            // However, try to dismiss keyguard for convenience.
            keyguardManager.requestDismissKeyguard(this,
                new KeyguardManager.KeyguardDismissCallback() {
                    @Override
                    public void onDismissError() {
                        finish();
                    }

                    @Override
                    public void onDismissCancelled() {
                        finish();
                    }

                    @Override
                    public void onDismissSucceeded() {
                        mDeviceLocked = false;
                        setMediaControllerIfInForeground();
                    }
                });
        }
        setUp();
        try {
            testSetQueueWithLargeNumberOfItems();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setUp(){
        mSession = new MediaSession(this, TEST_SESSION_TAG);
    }

    public void testSetQueueWithLargeNumberOfItems() throws Exception {
        int queueSize = 1_000;
        List<MediaSession.QueueItem> queue = new ArrayList<>();
        for (int id = 0; id < queueSize; id++) {
            MediaDescription description = new MediaDescription.Builder()
                .setMediaId(Integer.toString(id)).build();
            queue.add(new MediaSession.QueueItem(description, id));
        }

        try (RemoteService.Invoker invoker = new RemoteService.Invoker(this,
            MediaSessionTestService.class, TEST_SET_QUEUE)) {
            Bundle args = new Bundle();
            args.putParcelable(KEY_SESSION_TOKEN, mSession.getSessionToken());
            args.putInt(KEY_EXPECTED_QUEUE_SIZE, queueSize);
            invoker.run(STEP_SET_UP, args);
            mSession.setQueue(queue);
            invoker.run(STEP_CHECK);
            invoker.run(STEP_CLEAN_UP);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mResumed = true;
        setMediaControllerIfInForeground();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mResumed = false;
        setMediaController(null);
    }

    private void setMediaControllerIfInForeground() {
        if (mDeviceLocked || !mResumed) {
            return;
        }
        MediaSession.Token token = getIntent().getParcelableExtra(KEY_SESSION_TOKEN);
        if (token != null) {
            MediaController controller = new MediaController(this, token);
            setMediaController(controller);
        }
    }
}
