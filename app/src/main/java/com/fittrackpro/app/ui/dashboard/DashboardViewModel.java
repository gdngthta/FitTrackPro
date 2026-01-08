package com.fittrackpro.app.ui.dashboard;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx. lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;

import com.fittrackpro.app.data.local.AppDatabase;
import com.fittrackpro.app.data. model.CompletedWorkout;
import com.fittrackpro.app.data.model.LeaderboardEntry;
import com. fittrackpro.app. data.model.User;
import com.fittrackpro.app.data.repository.SocialRepository;
import com.fittrackpro.app.data. repository.UserRepository;
import com.fittrackpro.app.data.repository.WorkoutRepository;

import java.util.List;

/**
 * DashboardViewModel manages dashboard data.
 *
 * Displays:
 * - User stats (from User model)
 * - Recent workouts
 * - Leaderboard preview (top 3)
 */
public class DashboardViewModel extends AndroidViewModel {

    private final UserRepository userRepository;
    private final WorkoutRepository workoutRepository;
    private final SocialRepository socialRepository;

    private final MutableLiveData<String> userId = new MutableLiveData<>();
    private final MediatorLiveData<User> user = new MediatorLiveData<>();
    private final MediatorLiveData<List<CompletedWorkout>> recentWorkouts = new MediatorLiveData<>();
    private final MediatorLiveData<List<LeaderboardEntry>> leaderboardPreview = new MediatorLiveData<>();

    public DashboardViewModel(@NonNull Application application) {
        super(application);

        AppDatabase database = AppDatabase.getInstance(application);
        this.userRepository = new UserRepository(database);
        this.workoutRepository = new WorkoutRepository(database);
        this.socialRepository = new SocialRepository();
    }

    public void setUserId(String userId) {
        this.userId.setValue(userId);
        loadDashboardData(userId);
    }

    private void loadDashboardData(String userId) {
        // Load user data
        LiveData<User> userSource = userRepository.getUser(userId);
        user.addSource(userSource, user:: setValue);

        // Load recent workouts
        LiveData<List<CompletedWorkout>> workoutsSource =
                workoutRepository.getRecentWorkouts(userId, 5);
        recentWorkouts. addSource(workoutsSource, recentWorkouts::setValue);

        // Load leaderboard preview (top 3)
        LiveData<List<LeaderboardEntry>> leaderboardSource =
                socialRepository.getGlobalLeaderboard(userId, 3);
        leaderboardPreview.addSource(leaderboardSource, leaderboardPreview:: setValue);
    }

    public LiveData<User> getUser() {
        return user;
    }

    public LiveData<List<CompletedWorkout>> getRecentWorkouts() {
        return recentWorkouts;
    }

    public LiveData<List<LeaderboardEntry>> getLeaderboardPreview() {
        return leaderboardPreview;
    }

    public void refreshData() {
        String currentUserId = userId.getValue();
        if (currentUserId != null) {
            loadDashboardData(currentUserId);
        }
    }
}