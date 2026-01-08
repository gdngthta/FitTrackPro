package com.fittrackpro.app.sync;

import android.content.Context;
import androidx.lifecycle.LiveData;
import androidx.work.*;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * SyncManager handles scheduling and managing data synchronization.
 * Uses WorkManager for reliable background sync.
 */
public class SyncManager {

    private static final String SYNC_WORK_TAG = "fittrack_data_sync";
    private static SyncManager instance;
    private final Context context;

    private SyncManager(Context context) {
        this.context = context.getApplicationContext();
    }

    public static synchronized SyncManager getInstance(Context context) {
        if (instance == null) {
            instance = new SyncManager(context);
        }
        return instance;
    }

    /**
     * Schedule immediate sync
     */
    public void syncNow(String userId) {
        Data inputData = new Data.Builder()
                .putString("userId", userId)
                .build();

        OneTimeWorkRequest syncRequest = new OneTimeWorkRequest.Builder(DataSyncWorker.class)
                .setInputData(inputData)
                .setConstraints(getDefaultConstraints())
                .addTag(SYNC_WORK_TAG)
                .build();

        WorkManager.getInstance(context).enqueue(syncRequest);
    }

    /**
     * Schedule periodic sync (every 15 minutes when online)
     */
    public void schedulePeriodicSync(String userId) {
        Data inputData = new Data.Builder()
                .putString("userId", userId)
                .build();

        PeriodicWorkRequest periodicSync = new PeriodicWorkRequest.Builder(
                DataSyncWorker.class,
                15, TimeUnit.MINUTES)
                .setInputData(inputData)
                .setConstraints(getDefaultConstraints())
                .addTag(SYNC_WORK_TAG)
                .build();

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                "periodic_sync_" + userId,
                ExistingPeriodicWorkPolicy.KEEP,
                periodicSync
        );
    }

    /**
     * Cancel all sync work
     */
    public void cancelSync() {
        WorkManager.getInstance(context).cancelAllWorkByTag(SYNC_WORK_TAG);
    }

    /**
     * Get default constraints (requires network)
     */
    private Constraints getDefaultConstraints() {
        return new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();
    }

    /**
     * Observe sync status
     */
    public LiveData<List<WorkInfo>> getSyncStatus() {
        return WorkManager.getInstance(context).getWorkInfosByTagLiveData(SYNC_WORK_TAG);
    }
}
