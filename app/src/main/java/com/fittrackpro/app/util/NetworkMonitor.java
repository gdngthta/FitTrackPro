package com.fittrackpro.app.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.fittrackpro.app.sync.SyncManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * NetworkMonitor tracks network connectivity changes and triggers sync when online.
 */
public class NetworkMonitor {

    private final Context context;
    private final MutableLiveData<Boolean> isConnected = new MutableLiveData<>();

    public NetworkMonitor(Context context) {
        this.context = context.getApplicationContext();
        registerNetworkCallback();
    }

    private void registerNetworkCallback() {
        ConnectivityManager cm = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (cm == null) {
            return;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            cm.registerDefaultNetworkCallback(new ConnectivityManager.NetworkCallback() {
                @Override
                public void onAvailable(@NonNull Network network) {
                    isConnected.postValue(true);
                    // Trigger sync when connectivity restored
                    triggerSyncOnReconnect();
                }

                @Override
                public void onLost(@NonNull Network network) {
                    isConnected.postValue(false);
                }
            });
        } else {
            // Legacy API < 24
            NetworkRequest request = new NetworkRequest.Builder()
                    .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                    .build();
            cm.registerNetworkCallback(request, new ConnectivityManager.NetworkCallback() {
                @Override
                public void onAvailable(@NonNull Network network) {
                    isConnected.postValue(true);
                    triggerSyncOnReconnect();
                }

                @Override
                public void onLost(@NonNull Network network) {
                    isConnected.postValue(false);
                }
            });
        }
    }

    private void triggerSyncOnReconnect() {
        String userId = getCurrentUserId();
        if (userId != null) {
            SyncManager.getInstance(context).syncNow(userId);
        }
    }

    public LiveData<Boolean> isConnected() {
        return isConnected;
    }

    private String getCurrentUserId() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        return user != null ? user.getUid() : null;
    }
}
