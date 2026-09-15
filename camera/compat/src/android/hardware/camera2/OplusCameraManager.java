package android.hardware.camera2;

import android.content.Context;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.IOplusCameraManager;
import android.hardware.camera2.impl.CameraMetadataNative;
import android.hardware.camera2.marshal.MarshalRegistry;
import android.media.Image;
import android.media.ImageReader;
import android.os.Binder;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemProperties;
import android.util.Log;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/* loaded from: classes.dex */
public final class OplusCameraManager implements IOplusCameraManager {
    private static final String PERMISSION_SAFE_CAMERA = "com.oplus.permission.safe.CAMERA";
    private static final String PERMISSION_SATELLITE_COMMUNICATION = "com.oplus.permission.safe.SATELLITE_COMMUNICATION";
    public static final int READ_CAMERA_SERVER_MEMORY_INFO = 1;
    public static final int READ_HAL_MEMORY_INFO = 0;
    public static final int STATELLITE_CALL_STATUS_CALLING = 1;
    public static final int STATELLITE_CALL_STATUS_IDLE = 0;
    private static final String TAG = "OplusCameraManager";
    private static final String SYSTEM_CAMERA_PACKNAME = SystemProperties.get("ro.oplus.system.camera.name");
    private static final CaptureRequest.Key<byte[]> KEY_OPLUS_PACKAGE = new CaptureRequest.Key<>("com.oplus.is.sdk.camera.package", byte[].class);
    private static OplusCameraManager mInstance = new OplusCameraManager();
    private static String[] SET_PACKAGE_BLACK_LIST = {"com.oplus.battery", "com.oplus.onetrace", "com.android.systemui", "com.oplus.obrain"};
    private String mOpPackageName = "";
    private boolean mIsCameraUnitSession = false;
    private boolean mbLoad = false;

    public native int nativeSendToAttachHWBufToBufQEvent(long j);

    public native int nativeSendToBufQAllocEnableEvent(long j);

    public native int nativeSendToExchgHWBufBtwBufQEvent(long j);

    public native void nativtSendToProcessHeif(long j);

    private OplusCameraManager() {
    }

    private void checkLoadLibrary() {
        Log.i(TAG, "checkLoadHeifLibbrary, mbLoad: " + this.mbLoad);
        if (this.mbLoad) {
            return;
        }
        try {
            System.loadLibrary("HeifWinBufExchg-jni");
            this.mbLoad = true;
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "checkLoadHeifLibbrary, error");
        }
    }

    public void sendToProcessHeif(long ptr) {
        checkLoadLibrary();
        nativtSendToProcessHeif(ptr);
    }

    public int sendToBufQAllocEnableEvent(long ptr) {
        checkLoadLibrary();
        return nativeSendToBufQAllocEnableEvent(ptr);
    }

    public int sendToExchgHWBufBtwBufQEvent(long ptr) {
        checkLoadLibrary();
        return nativeSendToExchgHWBufBtwBufQEvent(ptr);
    }

    public int sendToAttachHWBufToBufQEvent(long ptr) {
        checkLoadLibrary();
        return nativeSendToAttachHWBufToBufQEvent(ptr);
    }

    public static synchronized OplusCameraManager getInstance() {
        OplusCameraManager oplusCameraManager;
        synchronized (OplusCameraManager.class) {
            oplusCameraManager = mInstance;
        }
        return oplusCameraManager;
    }

    public static Object getEmptyCameraMetadataNative(long[] metadataPtr) {
        CameraMetadataNative meta = new CameraMetadataNative();
        if (metadataPtr != null && metadataPtr.length > 0) {
            metadataPtr[0] = meta.getMetadataPtr();
        }
        return meta;
    }

    public static TotalCaptureResult generateTotalCaptureResult(Object meta, long frameId) {
        if (meta == null || !(meta instanceof CameraMetadataNative)) {
            return null;
        }
        TotalCaptureResult r = new TotalCaptureResult((CameraMetadataNative) meta, 0);
        try {
            Field numField = CaptureResult.class.getDeclaredField("mFrameNumber");
            numField.setAccessible(true);
            numField.setLong(r, frameId);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            e.printStackTrace();
        }
        return r;
    }

    @Override // android.hardware.camera2.IOplusCameraManager
    public void addAuthResultInfo(Context context, int uid, int pid, int permBits, String packageName) {
        // OEM service extension unavailable; Camera2 enforces camera access.
        
    }

    @Override // android.hardware.camera2.IOplusCameraManager
    public void setDeathRecipient(IBinder client) {
        // OEM service extension unavailable; Camera2 enforces camera access.
        
    }

    @Override // android.hardware.camera2.IOplusCameraManager
    public boolean isAuthedClient(Context context) {
        // OEM service extension unavailable; Camera2 enforces camera access.
        return context != null && context.checkSelfPermission(android.Manifest.permission.CAMERA) == android.content.pm.PackageManager.PERMISSION_GRANTED;
    }

    @Override // android.hardware.camera2.IOplusCameraManager
    public void preOpenCamera(Context context) {
        // OEM service extension unavailable; Camera2 enforces camera access.
        
    }

    @Override // android.hardware.camera2.IOplusCameraManager
    public void sendOplusExtCamCmd(Context context, IOplusCameraManager.Cmd cmd, int[] param) {
        // OEM service extension unavailable; Camera2 enforces camera access.
        
    }

    public void readMemoryInfo(IOplusCameraManager.Cmd cmd, StringBuilder result, int model) {
        // OEM service extension unavailable; Camera2 enforces camera access.
        
    }

    @Override // android.hardware.camera2.IOplusCameraManager
    public void setCallInfo() {
        // OEM service extension unavailable; Camera2 enforces camera access.
        
    }

    @Override // android.hardware.camera2.IOplusCameraManager
    public void saveOpPackageName(String packageName) {
        this.mOpPackageName = packageName;
        Log.i(TAG, "saveOpPackageName, mOpPackageName: " + this.mOpPackageName);
    }

    @Override // android.hardware.camera2.IOplusCameraManager
    public void setPackageName() {
        // OEM service extension unavailable; Camera2 enforces camera access.
        
    }

    public void setRIOClientInfo() throws CameraAccessException {
        // OEM service extension unavailable; Camera2 enforces camera access.
        
    }

    public void unRegisterCameraDeviceCallback(Context context) throws CameraAccessException {
        // OEM service extension unavailable; Camera2 enforces camera access.
        
    }

    public void setDeathRecipient(Context context, IBinder client) {
        // OEM service extension unavailable; Camera2 enforces camera access.
        
    }

    public void setSatelliteCallStatus(Context context, int status) throws CameraAccessException {
        // OEM service extension unavailable; Camera2 enforces camera access.
        
    }

    @Override // android.hardware.camera2.IOplusCameraManager
    public boolean isPrivilegedApp(String packageName) {
        String str;
        if (packageName == null || (str = SYSTEM_CAMERA_PACKNAME) == null || !str.equals(packageName)) {
            return false;
        }
        return true;
    }

    public void setTorchIntensity(int torchIntensity) {
        // OEM service extension unavailable; Camera2 enforces camera access.
        
    }

    public void disconnectClients() {
        // OEM service extension unavailable; Camera2 enforces camera access.
        
    }

    public boolean isClientConnected() {
        // OEM service extension unavailable; Camera2 enforces camera access.
        return false;
    }

    @Override // android.hardware.camera2.IOplusCameraManager
    public boolean isCameraUnitSession() {
        return this.mIsCameraUnitSession;
    }

    @Override // android.hardware.camera2.IOplusCameraManager
    public void parseSessionParameters(CaptureRequest sessionParams) {
        if (sessionParams == null) {
            setIsCameraUnitSession(false);
            return;
        }
        byte[] result = (byte[]) sessionParams.get(KEY_OPLUS_PACKAGE);
        if (result == null || result.length == 0) {
            setIsCameraUnitSession(false);
            return;
        }
        if (1 == result[0]) {
            setIsCameraUnitSession(true);
        }
        Log.i(TAG, "parseSessionParameters mIsCameraUnitSession: " + this.mIsCameraUnitSession);
    }

    public void oplusDetachImage(Image image, ImageReader imgreader) {
        try {
            Method method = ImageReader.class.getDeclaredMethod("detachImage", Image.class);
            method.setAccessible(true);
            method.invoke(imgreader, image);
        } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            e.printStackTrace();
        }
        Log.i(TAG, "using reflection to visit detachImage method in ImageReader");
    }

    public static void setOmojiJson(String jsonInfo) {
        // OEM service extension unavailable; Camera2 enforces camera access.
        
    }

    public void closeAON() throws CameraAccessException {
        // OEM service extension unavailable; Camera2 enforces camera access.
        
    }

    private void setIsCameraUnitSession(boolean isCameraUnitSession) {
        // OEM service extension unavailable; Camera2 enforces camera access.
        this.mIsCameraUnitSession = isCameraUnitSession;
    }

    public static <T> T metaDataValueConvert(CaptureResult.Key<T> key, int i, byte[] bArr) {
        final String TAG = "OplusCameraManager";
        try {
            T result = (T) MarshalRegistry.getMarshaler(key.getNativeKey().getTypeReference(), i)
                    .unmarshal(ByteBuffer.wrap(bArr).order(ByteOrder.nativeOrder()));
            android.util.Log.d(TAG, "metaDataValueConvert OK");
            return result;
        } catch (Throwable t) {
            android.util.Log.e(TAG, "metaDataValueConvert FAIL");
            throw t; // rethrow the original exception
        }
    }

    public static int getMetadataTag(CaptureResult.Key key) {
        final String TAG = "OplusCameraManager";
        try {
            int tag = key.getNativeKey().getTag();
            android.util.Log.d(TAG, "getMetadataTag OK");
            return tag;
        } catch (Throwable t) {
            android.util.Log.e(TAG, "getMetadataTag FAIL");
            throw t;
        }
    }

}
