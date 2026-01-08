package com.fittrackpro. app.ui.profile;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;

import com.fittrackpro.app.data. local.AppDatabase;
import com.fittrackpro.app.data.model.PersonalRecord;
import com.fittrackpro.app.data.model.User;
import com.fittrackpro.app.data.repository.UserRepository;
import com.fittrackpro.app.data.repository.WorkoutRepository;

import java.util.List;

/**
 * ProfileViewModel manages user profile and settings.
 *
 * Displays:
 * - User info (display name, username, email)
 * - Stats
 * - Personal records
 * - Settings
 */
public class ProfileViewModel extends AndroidViewModel {

    private final UserRepository userRepository;
    private final WorkoutRepository workoutRepository;

    private final MutableLiveData<String> userId = new MutableLiveData<>();
    private final MediatorLiveData<User> user = new MediatorLiveData<>();
    private final MediatorLiveData<List<PersonalRecord>> personalRecords = new MediatorLiveData<>();

    public ProfileViewModel(@NonNull Application application) {
        super(application);

        AppDatabase database = AppDatabase.getInstance(application);
        this.userRepository = new UserRepository(database);
        this.workoutRepository = new WorkoutRepository(database);
    }

    public void setUserId(String userId) {
        this.userId.setValue(userId);
        loadProfileData(userId);
    }

    private void loadProfileData(String userId) {
        // Load user data
        LiveData<User> userSource = userRepository.getUser(userId);
        user.addSource(userSource, user:: setValue);

        // Load personal records
        LiveData<List<PersonalRecord>> recordsSource = workoutRepository.getPersonalRecords(userId);
        personalRecords.addSource(recordsSource, personalRecords::setValue);
    }

    public LiveData<Boolean> updateDisplayName(String displayName) {
        String currentUserId = userId. getValue();
        if (currentUserId != null) {
            return userRepository.updateDisplayName(currentUserId, displayName);
        }
        return new MutableLiveData<>(false);
    }

    public LiveData<User> getUser() {
        return user;
    }

    public LiveData<List<PersonalRecord>> getPersonalRecords() {
        return personalRecords;
    }
}