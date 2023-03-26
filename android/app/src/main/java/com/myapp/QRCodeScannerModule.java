package com.myapp;

import com.facebook.react.bridge.ActivityEventListener;
import com.facebook.react.bridge.BaseActivityEventListener;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Promise;

import android.app.Activity;
import android.content.Intent;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class QRCodeScannerModule extends ReactContextBaseJavaModule {

    private Promise qrPickerPromise;
    private static final String E_FAILED_TO_SHOW_PICKER = "E_FAILED_TO_SHOW_PICKER";
    private IntentIntegrator integrator;

    @Override
    public String getName() {
        return "QRCodeScanner";
    }

    @ReactMethod
    public void scanQRCode(String type, Promise promise) {
        try {
            integrator = new IntentIntegrator(getCurrentActivity());
            qrPickerPromise = promise;
            integrator.setOrientationLocked(false);
            integrator.setCameraId(0);
            integrator.setBeepEnabled(false);
            integrator.setPrompt("Scan a " + type.toUpperCase() + " code");

            // Configura o tipo de código de barras que o scanner irá ler
            if (type.equals("qr")) {
                integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
            } else if (type.equals("bar")) {
                integrator.setDesiredBarcodeFormats(IntentIntegrator.ONE_D_CODE_TYPES);
            }
            integrator.initiateScan();
        } catch (Exception e) {
            qrPickerPromise.reject(E_FAILED_TO_SHOW_PICKER, e);
            qrPickerPromise = null;
            promise.reject("QRCodeScanner", e.getMessage());
        }
    }

    private final ActivityEventListener mActivityEventListener = new BaseActivityEventListener() {
        @Override
        public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent intent) {
            IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
            if (qrPickerPromise != null && scanResult != null) {
                qrPickerPromise.resolve(scanResult.getContents());
            }
        }
    };

    QRCodeScannerModule(ReactApplicationContext reactContext) {
        super(reactContext);
        reactContext.addActivityEventListener(mActivityEventListener);
    }
    public void cancelScan() {
        if (qrPickerPromise != null) {
            qrPickerPromise.reject(E_FAILED_TO_SHOW_PICKER, new Exception("Scan cancelled by user"));
            qrPickerPromise = null;
        }
    }
    
}
