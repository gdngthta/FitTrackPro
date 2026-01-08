# Sync Architecture Documentation

## Overview
FitTrackPro implements an **offline-first** data synchronization system that allows users to use the app fully without internet connectivity. All data writes go to the local Room Database immediately for instant UI updates, then sync to Firestore when connectivity is available using WorkManager for guaranteed delivery.

## Architecture Components

### 1. Data Flow

```
┌─────────────────────────────────────────────────────────────────┐
│                         USER INTERACTION                          │
└─────────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│                         REPOSITORY LAYER                          │
│  • Writes go to Room IMMEDIATELY (instant UI update)             │
│  • Marks data as unsynced (synced = false)                       │
│  • Triggers SyncManager to schedule background sync               │
└─────────────────────────────────────────────────────────────────┘
                              │
                    ┌─────────┴─────────┐
                    ▼                   ▼
        ┌─────────────────┐   ┌──────────────────┐
        │  ROOM DATABASE  │   │  SYNC MANAGER    │
        │  (Local Cache)  │   │  (WorkManager)   │
        └─────────────────┘   └──────────────────┘
                    │                   │
                    │                   ▼
                    │         ┌──────────────────┐
                    │         │ DataSyncWorker   │
                    │         │ • Runs in bg     │
                    │         │ • Syncs unsynced │
                    │         │ • Retries errors │
                    │         └──────────────────┘
                    │                   │
                    │                   ▼
                    │         ┌──────────────────┐
                    └────────>│  FIRESTORE       │
                              │  (Cloud Storage) │
                              └──────────────────┘
```

### 2. Sync Tracking Fields

Every entity includes sync tracking fields:
```java
private boolean synced = false;           // Is data synced to Firestore?
private long lastSyncAttempt = 0;         // Timestamp of last sync attempt
private int syncAttempts = 0;             // Number of sync attempts
private String syncError = null;          // Last error message if failed
```

### 3. Entities with Sync Support

1. **CompletedWorkoutEntity** - Workout completion records
2. **UserEntity** - User profile and stats
3. **PersonalRecordEntity** - Personal records (PRs)
4. **FoodEntity** - Food database entries
5. **WorkoutProgramEntity** - Custom workout programs
6. **MealLoggedEntity** - Meal logging records

### 4. Sync Strategy by Data Type

#### User Data
- **Write**: Room first, then Firestore
- **Read**: Room (LiveData), refresh from Firestore in background
- **Conflict**: Last-write-wins based on `updatedAt` timestamp

#### Completed Workouts
- **Write**: Room immediately with `synced = false`
- **Sync**: Background via WorkManager
- **Conflict**: Local always wins (user's device is source of truth)

#### Personal Records
- **Write**: Room first
- **Sync**: Background sync
- **Conflict**: Keep highest value (never delete a PR)

#### Meal Logs
- **Write**: Room immediately
- **Sync**: Background sync
- **Conflict**: Last-write-wins

#### Custom Programs
- **Write**: Room first for custom programs
- **Sync**: Background sync
- **Conflict**: Prefer local modifications

### 5. Sync Worker

**DataSyncWorker** handles background synchronization:

```java
@Override
public Result doWork() {
    String userId = getInputData().getString("userId");
    
    try {
        syncAllData(userId);
        return Result.success();
    } catch (Exception e) {
        if (getRunAttemptCount() < MAX_SYNC_ATTEMPTS) {
            return Result.retry();
        }
        return Result.failure();
    }
}
```

**Features:**
- Processes all unsynced records (where `synced = false`)
- Max 3 retry attempts per record
- Uses `Tasks.await()` for synchronous execution in Worker
- Updates sync tracking fields after each attempt
- Skips records that have exceeded max attempts

### 6. SyncManager

**SyncManager** provides:
- `syncNow(userId)` - Immediate one-time sync
- `schedulePeriodicSync(userId)` - Periodic sync every 15 minutes
- `cancelSync()` - Cancel all sync work
- `getSyncStatus()` - Observable LiveData for UI

**Constraints:**
- Requires network connectivity (NetworkType.CONNECTED)
- Runs when device has adequate battery
- Respects data saver settings

### 7. NetworkMonitor

**NetworkMonitor** tracks connectivity changes:
- Registers callback for network state changes
- Automatically triggers sync when connectivity restored
- Provides LiveData<Boolean> for connectivity status

### 8. Firestore Offline Persistence

Enabled in `FitTrackApplication`:
```java
FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
    .setPersistenceEnabled(true)
    .setCacheSizeBytes(FirebaseFirestoreSettings.CACHE_SIZE_UNLIMITED)
    .build();
firestore.setFirestoreSettings(settings);
```

**Benefits:**
- Firestore maintains its own local cache
- Automatic query caching
- Syncs when app goes online
- Complementary to Room database

### 9. Repository Pattern (Offline-First)

Example: UserRepository
```java
public LiveData<User> getUser(String userId) {
    // Return Room LiveData immediately for instant UI
    LiveData<UserEntity> localData = userDao.getUserById(userId);
    
    // Fetch from Firestore in background and update Room
    fetchUserFromFirestore(userId);
    
    // Transform Entity to Model
    return Transformations.map(localData, this::convertToModel);
}

public void updateUser(User user) {
    // Write to Room immediately
    UserEntity entity = convertToEntity(user);
    entity.setSynced(false); // Mark as needing sync
    entity.setUpdatedAt(System.currentTimeMillis());
    executor.execute(() -> {
        userDao.updateUser(entity);
        // Schedule sync
        syncManager.syncNow(user.getUserId());
    });
}
```

## Conflict Resolution

### Rules by Data Type

1. **User Profile**
   - Strategy: Last-write-wins
   - Based on: `updatedAt` timestamp
   - Reasoning: User updates should reflect their most recent changes

2. **Workouts**
   - Strategy: Local always wins
   - Reasoning: User's device is source of truth for workout completion

3. **Personal Records**
   - Strategy: Keep highest value
   - Reasoning: Never delete a PR, always keep the best

4. **Programs**
   - Strategy: Merge if possible, prefer local
   - Reasoning: User modifications take precedence

5. **Meals**
   - Strategy: Last-write-wins
   - Reasoning: Most recent entry is most accurate

## Error Handling

### Sync Failures
1. Worker retries up to 3 times with exponential backoff
2. Failed records tracked with `syncAttempts` counter
3. Error messages stored in `syncError` field
4. User notified via SyncStatusView

### Network Errors
- WorkManager handles automatically
- Retries when network restored
- NetworkMonitor triggers immediate sync on reconnect

### Database Errors
- Room transactions ensure data integrity
- Fallback to local cache if Firestore unavailable

## UI Indicators

### SyncStatusView States

1. **SYNCED** (Green)
   - All data synced successfully
   - Icon: Cloud with checkmark

2. **SYNCING** (Gray)
   - Sync in progress
   - Icon: Cloud with arrows

3. **OFFLINE** (Gray)
   - No network connectivity
   - Data will sync when online
   - Icon: Cloud with slash

4. **ERROR** (Red)
   - Sync failed after retries
   - Icon: Cloud with exclamation

## Performance Considerations

### Optimizations
- WorkManager runs only when online (NetworkType.CONNECTED)
- Batch processing of unsynced records
- Efficient queries using Room indices
- LiveData prevents unnecessary updates

### Battery Efficiency
- WorkManager respects system constraints
- No continuous polling
- Event-driven sync triggers

### Data Usage
- Only unsynced records transmitted
- Firestore offline cache reduces redundant downloads
- Selective sync by data type

## Testing Guidelines

### Unit Tests
- Test SyncWorker sync logic
- Test Repository dual-source reads
- Test conflict resolution
- Test network state changes

### Integration Tests
- Test app works fully offline
- Test data syncs when connectivity restored
- Test WorkManager retry logic
- Test sync status indicators

### Manual Testing Checklist
- [ ] Complete workout offline, verify saves to Room
- [ ] Go online, verify syncs to Firestore
- [ ] Toggle airplane mode during workout
- [ ] Check Profile sync status indicator
- [ ] Create custom program offline
- [ ] Log meals offline
- [ ] Verify all data appears after going online

## Troubleshooting

### Common Issues

**Problem**: Data not syncing
- Check network connectivity
- Check WorkManager status via `getSyncStatus()`
- Check `syncAttempts` and `syncError` fields
- Verify Firebase Auth user is logged in

**Problem**: Sync taking too long
- Check number of unsynced records
- Consider reducing batch size
- Check Firestore quotas

**Problem**: Duplicate data
- Verify entity IDs are consistent
- Check conflict resolution logic
- Ensure `OnConflictStrategy.REPLACE` in DAOs

## Future Improvements

### Potential Enhancements
1. **Differential Sync** - Only sync changed fields
2. **Batch Optimization** - Group related records
3. **Priority Sync** - Sync critical data first
4. **Conflict UI** - Show user conflicts for resolution
5. **Sync Analytics** - Track sync performance metrics
6. **Selective Sync** - User controls what syncs
7. **Background Fetch** - Periodic background refresh

### Scalability
- Consider pagination for large datasets
- Implement incremental sync with timestamps
- Add sync queue prioritization
- Monitor and optimize battery/data usage

## Dependencies

```gradle
// WorkManager for background sync
implementation 'androidx.work:work-runtime:2.9.0'

// Room for local database
implementation 'androidx.room:room-runtime:2.6.0'

// Firestore for cloud storage
implementation 'com.google.firebase:firebase-firestore'
```

## Permissions

```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
```

## References

- [WorkManager Documentation](https://developer.android.com/topic/libraries/architecture/workmanager)
- [Room Documentation](https://developer.android.com/training/data-storage/room)
- [Firestore Offline Persistence](https://firebase.google.com/docs/firestore/manage-data/enable-offline)
- [Android Architecture Components](https://developer.android.com/topic/libraries/architecture)
