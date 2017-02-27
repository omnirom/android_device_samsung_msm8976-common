/*
* Copyright (C) 2016 The OmniROM Project
*
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 2 of the License, or
* (at your option) any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with this program. If not, see <http://www.gnu.org/licenses/>.
*
*/
package org.omnirom.device;

import android.app.ActivityManagerNative;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.IAudioService;
import android.media.AudioManager;
import android.media.session.MediaSessionLegacyHelper;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemClock;
import android.os.UserHandle;
import android.provider.Settings;
import android.provider.Settings.Global;
import android.util.Log;
import android.view.KeyEvent;
import android.view.WindowManagerGlobal;

import com.android.internal.os.DeviceKeyHandler;
import com.android.internal.util.ArrayUtils;

public class KeyHandler implements DeviceKeyHandler {

    private static final String TAG = KeyHandler.class.getSimpleName();
    private static final boolean DEBUG = true;
    protected static final int GESTURE_REQUEST = 1;
    private static final int GESTURE_WAKELOCK_DURATION = 2000;
    private static final String KEY_CONTROL_PATH = "/proc/s1302/virtual_key";
    private static final String FPC_CONTROL_PATH = "/sys/devices/soc/soc:fpc_fpc1020/proximity_state";

    private static final int KEY_DOUBLE_TAP = 143;
    private static final int KEY_HOME = 127;
    private static final int KEY_BACK = 158;
    private static final int KEY_RECENTS = 580;


    private static final int[] sDisabledKeys = new int[]{
        KEY_HOME,
        KEY_BACK,
        KEY_RECENTS
    };

/*
    private static final int[] sHandledGestures = new int[]{
        GESTURE_V_SCANCODE,
        GESTURE_II_SCANCODE,
        GESTURE_LEFT_V_SCANCODE,
        GESTURE_RIGHT_V_SCANCODE,
        KEY_SLIDER_TOP,
        KEY_SLIDER_CENTER,
        KEY_SLIDER_BOTTOM
    };
*/


/*    private static final int[] sProxiCheckedGestures = new int[]{
        KEY_HOME
    };
*/
    protected final Context mContext;
//    private final PowerManager mPowerManager;
//    private EventHandler mEventHandler;
    private WakeLock mGestureWakeLock;
    private Handler mHandler = new Handler();
    private SettingsObserver mSettingsObserver;
    private static boolean mButtonDisabled;
    private static boolean mHomeButtonWakeEnabled;
//    private final NotificationManager mNoMan;
//    private final AudioManager mAudioManager;
//    private CameraManager mCameraManager;
//    private String mRearCameraId;
//    private boolean mTorchEnabled;
//    private SensorManager mSensorManager;
//    private Sensor mSensor;
//    private boolean mProxyIsNear;
//    private boolean mUseProxiCheck;

/*
    private SensorEventListener mProximitySensor = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            mProxyIsNear = event.values[0] < mSensor.getMaximumRange();
            if (DEBUG) Log.d(TAG, "mProxyIsNear = " + mProxyIsNear);
            if(Utils.fileWritable(FPC_CONTROL_PATH)) {
                Utils.writeValue(FPC_CONTROL_PATH, mProxyIsNear ? "1" : "0");
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    };
*/

    private class SettingsObserver extends ContentObserver {
        SettingsObserver(Handler handler) {
            super(handler);
        }

        void observe() {
            mContext.getContentResolver().registerContentObserver(Settings.System.getUriFor(
                    Settings.System.HARDWARE_KEYS_DISABLE),
                    false, this);
            mContext.getContentResolver().registerContentObserver(Settings.System.getUriFor(
                    Settings.System.BUTTON_EXTRA_KEY_MAPPING),
                    false, this);
            update();
        }

        @Override
        public void onChange(boolean selfChange) {
            update();
        }

        public void update() {
            if (DEBUG) Log.i(TAG, "update called" );
            setButtonSetting(mContext);
            
/*            mHomeButtonWakeEnabled = Settings.System.getIntForUser(
                    mContext.getContentResolver(), Settings.System.BUTTON_EXTRA_KEY_MAPPING, 0,
                    UserHandle.USER_CURRENT) == 1;
            mButtonDisabled = Settings.System.getIntForUser(
                    mContext.getContentResolver(), Settings.System.HARDWARE_KEYS_DISABLE, 0,
                    UserHandle.USER_CURRENT) == 1;*/
/*
            mUseProxiCheck = Settings.System.getIntForUser(
                    mContext.getContentResolver(), Settings.System.DEVICE_PROXI_CHECK_ENABLED, 1,
                    UserHandle.USER_CURRENT) == 1;
*/
        }
    }

/*
    private class MyTorchCallback extends CameraManager.TorchCallback {
        @Override
        public void onTorchModeChanged(String cameraId, boolean enabled) {
            if (!cameraId.equals(mRearCameraId))
                return;
            mTorchEnabled = enabled;
        }

        @Override
        public void onTorchModeUnavailable(String cameraId) {
            if (!cameraId.equals(mRearCameraId))
                return;
            mTorchEnabled = false;
        }
    }
*/

/*
    private BroadcastReceiver mScreenStateReceiver = new BroadcastReceiver() {
         @Override
         public void onReceive(Context context, Intent intent) {
             if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
                 onDisplayOn();
             } else if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
                 onDisplayOff();
             }
         }
    };
*/


    public KeyHandler(Context context) {
        if (DEBUG) Log.i(TAG, "KeyHandler called");
        mContext = context;
//        mEventHandler = new EventHandler();
//        mPowerManager = (PowerManager) mContext.getSystemService(Context.POWER_SERVICE);
//        mGestureWakeLock = mPowerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
//                "GestureWakeLock");
        mSettingsObserver = new SettingsObserver(mHandler);
        mSettingsObserver.observe();
//        mNoMan = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
//        mAudioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
//        mCameraManager = (CameraManager) mContext.getSystemService(Context.CAMERA_SERVICE);
//        mCameraManager.registerTorchCallback(new MyTorchCallback(), mEventHandler);
//        mSensorManager = (SensorManager) mContext.getSystemService(Context.SENSOR_SERVICE);
//        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
//        IntentFilter screenStateFilter = new IntentFilter(Intent.ACTION_SCREEN_ON);
//        screenStateFilter.addAction(Intent.ACTION_SCREEN_OFF);
//        mContext.registerReceiver(mScreenStateReceiver, screenStateFilter);
    }
/*
    private class EventHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            KeyEvent event = (KeyEvent) msg.obj;
            handleKey(event.getScanCode());
        }
    }

    private void handleKey(int scanCode) {
        if (DEBUG) Log.i(TAG, "handleKey called");
        switch(scanCode) {
        case KEY_HOME:
            if (DEBUG) Log.i(TAG, "KEY_HOME");
//            mGestureWakeLock.acquire(GESTURE_WAKELOCK_DURATION);
//            doHandleSliderAction(2);
            break;
        }
    }
*/

    @Override
    public boolean handleKeyEvent(KeyEvent event) {
        if (DEBUG) Log.i(TAG, "handleKeyEvent called - scancode=" + event.getScanCode() + " - keyevent=" + event.getAction());
        if (event.getAction() != KeyEvent.ACTION_UP) {
            return false;
        }
        return ArrayUtils.contains(sDisabledKeys, event.getScanCode());
        
/*        if (event.getScanCode() == KEY_HOME) {
            if (DEBUG) Log.i(TAG, "scanCode=" + event.getScanCode());
            Message msg = getMessageForKeyEvent(event);
            mEventHandler.removeMessages(GESTURE_REQUEST);
            mEventHandler.sendMessage(msg);
            return true;
        }
        return false;
*/
    }

    @Override
    public boolean canHandleKeyEvent(KeyEvent event) {
        Log.i(TAG, "canHandleKeyEvent called - scancode=" + event.getScanCode() + " - keyevent=" + event.getAction());
        return ArrayUtils.contains(sDisabledKeys, event.getScanCode());
/*
        if (event.getScanCode() == KEY_HOME) {
            return true;
        }
        return false;
*/
    }

    @Override
    public boolean isDisabledKeyEvent(KeyEvent event) {
        if (DEBUG) Log.i(TAG, "isDisabledKeyEvent called");
        if (mButtonDisabled) {
            if (DEBUG) Log.i(TAG, "Buttons are disabled");
            if (ArrayUtils.contains(sDisabledKeys, event.getScanCode())) {
                if (DEBUG) Log.i(TAG, "Key blocked=" + event.getScanCode());
                return true;
            }
        }
        return false;
    }

/*
    private Message getMessageForKeyEvent(KeyEvent keyEvent) {
        Message msg = mEventHandler.obtainMessage(GESTURE_REQUEST);
        msg.obj = keyEvent;
        return msg;
    }
*/

    public static void setButtonSetting(Context context) {
        Log.i(TAG, "SetButtonDisable called" );
        mButtonDisabled = Settings.System.getIntForUser(
                context.getContentResolver(), Settings.System.HARDWARE_KEYS_DISABLE, 0,
                UserHandle.USER_CURRENT) == 1;
        if (DEBUG) Log.i(TAG, "setButtonDisable=" + mButtonDisabled);
        mHomeButtonWakeEnabled = Settings.System.getIntForUser(
                context.getContentResolver(), Settings.System.BUTTON_EXTRA_KEY_MAPPING, 0,
                UserHandle.USER_CURRENT) == 1;
        if (DEBUG) Log.i(TAG, "mHomeButtonWakeEnabled=" + mHomeButtonWakeEnabled);
    }

    @Override
    public boolean isCameraLaunchEvent(KeyEvent event) {
//        if (event.getAction() != KeyEvent.ACTION_UP) {
//            return false;
//        }
        return false;
    }

    @Override
    public boolean isWakeEvent(KeyEvent event){
        if (DEBUG) Log.i(TAG, "isWakeEvent called - scancode=" + event.getScanCode() + " - keyevent=" + event.getAction());
        if (event.getAction() != KeyEvent.ACTION_UP) {
            return false;
        }
        if (DEBUG) Log.i(TAG, "mHomeButtonWakeEnabled=" + mHomeButtonWakeEnabled);
        if (mHomeButtonWakeEnabled){
            if (DEBUG) Log.i(TAG, "bHomeButtonWake == true");
            if (event.getScanCode() == KEY_HOME) {
                if (DEBUG) Log.i(TAG, "KEY_HOME pressed");
                return true;
            }
        } else {
            if (DEBUG) Log.i(TAG, "bHomeButtonWake == false");
        }
        return false;
    }

/*
    private IAudioService getAudioService() {
        IAudioService audioService = IAudioService.Stub
                .asInterface(ServiceManager.checkService(Context.AUDIO_SERVICE));
        if (audioService == null) {
            Log.w(TAG, "Unable to find IAudioService interface.");
        }
        return audioService;
    }
*/

/*
    boolean isMusicActive() {
        return mAudioManager.isMusicActive();
    }
*/

/*
    private void dispatchMediaKeyWithWakeLockToAudioService(int keycode) {
        if (ActivityManagerNative.isSystemReady()) {
            IAudioService audioService = getAudioService();
            if (audioService != null) {
                KeyEvent event = new KeyEvent(SystemClock.uptimeMillis(),
                        SystemClock.uptimeMillis(), KeyEvent.ACTION_DOWN,
                        keycode, 0);
                dispatchMediaKeyEventUnderWakelock(event);
                event = KeyEvent.changeAction(event, KeyEvent.ACTION_UP);
                dispatchMediaKeyEventUnderWakelock(event);
            }
        }
    }
*/

/*
    private void dispatchMediaKeyEventUnderWakelock(KeyEvent event) {
        if (ActivityManagerNative.isSystemReady()) {
            MediaSessionLegacyHelper.getHelper(mContext).sendMediaButtonEvent(event, true);
        }
    }
*/

/*
    private String getRearCameraId() {
        if (mRearCameraId == null) {
            try {
                for (final String cameraId : mCameraManager.getCameraIdList()) {
                    CameraCharacteristics c = mCameraManager.getCameraCharacteristics(cameraId);
                    Boolean flashAvailable = c.get(CameraCharacteristics.FLASH_INFO_AVAILABLE);
                    Integer lensFacing = c.get(CameraCharacteristics.LENS_FACING);
                    if (flashAvailable != null && flashAvailable
                            && lensFacing != null && lensFacing == CameraCharacteristics.LENS_FACING_BACK) {
                        mRearCameraId = cameraId;
                        break;
                    }
                }
            } catch (CameraAccessException e) {
                // Ignore
            }
        }
        return mRearCameraId;
    }
*/

/*
    private void onDisplayOn() {
        if (mUseProxiCheck) {
            if (DEBUG) Log.d(TAG, "Display on");
            mSensorManager.unregisterListener(mProximitySensor, mSensor);
        }
    }
*/

/*
    private void onDisplayOff() {
        if (mUseProxiCheck) {
            if (DEBUG) Log.d(TAG, "Display off");
            mSensorManager.registerListener(mProximitySensor, mSensor,
                        SensorManager.SENSOR_DELAY_NORMAL);
        }
    }
*/

/*
    private int getSliderAction(int position) {
        String value = Settings.System.getStringForUser(mContext.getContentResolver(),
                    Settings.System.BUTTON_EXTRA_KEY_MAPPING,
                    UserHandle.USER_CURRENT);
        final String defaultValue = "5,3,0";

        if (value == null) {
            value = defaultValue;
        } else if (value.indexOf(",") == -1) {
            value = defaultValue;
        }
        try {
            String[] parts = value.split(",");
            return Integer.valueOf(parts[position]);
        } catch (Exception e) {
        }
        return 0;
    }
*/

/*
    private void doHandleSliderAction(int position) {
        int action = getSliderAction(position);
        if ( action == 0) {
            mNoMan.setZenMode(Global.ZEN_MODE_OFF, null, TAG);
            mAudioManager.setRingerModeInternal(AudioManager.RINGER_MODE_NORMAL);
        } else if (action == 1) {
            mNoMan.setZenMode(Global.ZEN_MODE_OFF, null, TAG);
            mAudioManager.setRingerModeInternal(AudioManager.RINGER_MODE_VIBRATE);
        } else if (action == 2) {
            mNoMan.setZenMode(Global.ZEN_MODE_OFF_ONLY, null, TAG);
            mAudioManager.setRingerModeInternal(AudioManager.RINGER_MODE_SILENT);
        } else if (action == 3) {
            mNoMan.setZenMode(Global.ZEN_MODE_IMPORTANT_INTERRUPTIONS, null, TAG);
            mAudioManager.setRingerModeInternal(AudioManager.RINGER_MODE_NORMAL);
        } else if (action == 4) {
            mNoMan.setZenMode(Global.ZEN_MODE_ALARMS, null, TAG);
            mAudioManager.setRingerModeInternal(AudioManager.RINGER_MODE_NORMAL);
        } else if (action == 5) {
            mNoMan.setZenMode(Global.ZEN_MODE_NO_INTERRUPTIONS, null, TAG);
            mAudioManager.setRingerModeInternal(AudioManager.RINGER_MODE_NORMAL);
        }
    }
*/
}

