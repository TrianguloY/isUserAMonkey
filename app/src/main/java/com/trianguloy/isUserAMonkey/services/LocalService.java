package com.trianguloy.isUserAMonkey.services;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Binder;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import android.widget.Toast;

import com.trianguloy.isUserAMonkey.R;

/** The most basic service possible that will display a toast with the transaction code obtained. */
public class LocalService extends Service {
    @Override
    public IBinder onBind(Intent intent) {
        return new Binder() {
            @Override
            protected boolean onTransact(int code, Parcel data, Parcel reply, int flags) {
                Toast.makeText(LocalService.this, getString(R.string.service_toast, code), Toast.LENGTH_SHORT).show();
                return true;
            }
        };
    }

    public interface Callback {
        void onTransactionCompleted(boolean result);
    }

    /**
     * Sends a transaction code to the service. The callback will be called with the result of the transaction (true iff successful)
     */
    public static void sendTransaction(int code, Context cntx, Callback callback) {
        final ServiceConnection[] mConnection = new ServiceConnection[1];

        mConnection[0] = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName className, IBinder service) {
                try {
                    callback.onTransactionCompleted(service.transact(code, Parcel.obtain(), Parcel.obtain(), 0));
                } catch (RemoteException e) {
                    callback.onTransactionCompleted(false);
                }

                // unbind immediately
                cntx.unbindService(mConnection[0]);
            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {
            }
        };

        // perform a single-time bind, the service connection will be unbinded immediately
        var binded = cntx.bindService(new Intent(cntx, LocalService.class), mConnection[0], Context.BIND_AUTO_CREATE);
        if (!binded) {
            callback.onTransactionCompleted(false);
        }
    }
}