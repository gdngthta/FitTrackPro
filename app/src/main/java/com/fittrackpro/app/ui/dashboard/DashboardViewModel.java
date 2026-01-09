package com.fittrackpro.app.ui.dashboard;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;

import com.fittrackpro.app.data.local.AppDatabase;
import com.fittrackpro.app.data.model.CompletedWorkout;
import com.fittrackpro.app.data.model.LeaderboardEntry;
import com.fittrackpro.app.data.model.User;
import com.fittrackpro.app.data.model.WorkoutProgram;
import com.fittrackpro.app.data.repository.SocialRepository;
import com.fittrackpro.app.data.repository.UserRepository;
import com.fittrackpro.app.data.repository.WorkoutRepository;
import com.google.firebase.Timestamp;

import java.util.Calendar;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * DashboardViewModel manages dashboard data.
 *
 * Displays:
 * - User stats (from User model)
 * - Recent workouts
 * - Time-based greetings
 * - Weekly and monthly workout counts
 * - Average workout duration
 * - Active program status
 */
public class DashboardViewModel extends AndroidViewModel {

    private final UserRepository userRepository;
    private final WorkoutRepository workoutRepository;
    private final SocialRepository socialRepository;
    private final Executor executor;

    private final MutableLiveData<String> userId = new MutableLiveData<>();
    private final MediatorLiveData<User> user = new MediatorLiveData<>();
    private final MediatorLiveData<List<CompletedWorkout>> recentWorkouts = new MediatorLiveData<>();
    private final MediatorLiveData<List<LeaderboardEntry>> leaderboardPreview = new MediatorLiveData<>();
    private final MutableLiveData<Integer> thisWeekWorkouts = new MutableLiveData<>(0);
    private final MutableLiveData<Integer> thisMonthWorkouts = new MutableLiveData<>(0);
    private final MutableLiveData<Long> avgWorkoutDuration = new MutableLiveData<>(0L);
    private final MutableLiveData<WorkoutProgram> activeProgram = new MutableLiveData<>();
    private final MutableLiveData<String> greeting = new MutableLiveData<>();

    public DashboardViewModel(@NonNull Application application) {
        super(application);

        AppDatabase database = AppDatabase.getInstance(application);
        this.userRepository = new UserRepository(database, application);
        this.workoutRepository = new WorkoutRepository(database);
        this.socialRepository = new SocialRepository();
        this.executor = Executors.newSingleThreadExecutor();
        
        updateGreeting();
    }

    public void setUserId(String userId) {
        this.userId.setValue(userId);
        loadDashboardData(userId);
    }

    private void loadDashboardData(String userId) {
        // Load user data
        LiveData<User> userSource = userRepository.getUser(userId);
        user.addSource(userSource, user::setValue);

        // Load recent workouts
        LiveData<List<CompletedWorkout>> workoutsSource =
                workoutRepository.getRecentWorkouts(userId, 5);
        recentWorkouts.addSource(workoutsSource, workouts -> {
            recentWorkouts.setValue(workouts);
            if (workouts != null) {
                calculateWorkoutStats(workouts);
            }
        });

        // Load leaderboard preview (top 3)
        LiveData<List<LeaderboardEntry>> leaderboardSource =
                socialRepository.getGlobalLeaderboard(userId, 3);
        leaderboardPreview.addSource(leaderboardSource, leaderboardPreview::setValue);
        
        // Load active programs
        LiveData<List<WorkoutProgram>> activeProgramsSource =
                workoutRepository.getUserActivePrograms(userId);
        activeProgramsSource.observeForever(programs -> {
            if (programs != null && !programs.isEmpty()) {
                activeProgram.setValue(programs.get(0));
            } else {
                activeProgram.setValue(null);
            }
        });
    }

    private void calculateWorkoutStats(List<CompletedWorkout> allWorkouts) {
        executor.execute(() -> {
            if (allWorkouts == null || allWorkouts.isEmpty()) {
                thisWeekWorkouts.postValue(0);
                thisMonthWorkouts.postValue(0);
                avgWorkoutDuration.postValue(0L);
                return;
            }

            Calendar now = Calendar.getInstance();
            Calendar weekAgo = Calendar.getInstance();
            weekAgo.add(Calendar.DAY_OF_YEAR, -7);

            Calendar monthStart = Calendar.getInstance();
            monthStart.set(Calendar.DAY_OF_MONTH, 1);
            monthStart.set(Calendar.HOUR_OF_DAY, 0);
            monthStart.set(Calendar.MINUTE, 0);
            monthStart.set(Calendar.SECOND, 0);
            monthStart.set(Calendar.MILLISECOND, 0);

            int weekCount = 0;
            int monthCount = 0;
            long totalDuration = 0;

            for (CompletedWorkout workout : allWorkouts) {
                if (workout.getStartTime() != null) {
                    long workoutTime = workout.getStartTime().toDate().getTime();
                    
                    if (workoutTime >= weekAgo.getTimeInMillis()) {
                        weekCount++;
                    }
                    
                    if (workoutTime >= monthStart.getTimeInMillis()) {
                        monthCount++;
                    }
                    
                    totalDuration += workout.getDurationSeconds();
                }
            }

            thisWeekWorkouts.postValue(weekCount);
            thisMonthWorkouts.postValue(monthCount);
            
            if (!allWorkouts.isEmpty()) {
                long avgDurationMinutes = (totalDuration / allWorkouts.size()) / 60;
                avgWorkoutDuration.postValue(avgDurationMinutes);
            }
        });
    }

    private void updateGreeting() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        
        String greetingText;
        if (hour >= 5 && hour < 12) {
            greetingText = "Good Morning";
        } else if (hour >= 12 && hour < 17) {
            greetingText = "Good Afternoon";
        } else if (hour >= 17 && hour < 21) {
            greetingText = "Good Evening";
        } else {
            greetingText = "Good Night";
        }
        
        greeting.setValue(greetingText);
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

    public LiveData<Integer> getThisWeekWorkouts() {
        return thisWeekWorkouts;
    }

    public LiveData<Integer> getThisMonthWorkouts() {
        return thisMonthWorkouts;
    }

    public LiveData<Long> getAvgWorkoutDuration() {
        return avgWorkoutDuration;
    }

    public LiveData<WorkoutProgram> getActiveProgram() {
        return activeProgram;
    }

    public LiveData<String> getGreeting() {
        return greeting;
    }

    public void refreshData() {
        String currentUserId = userId.getValue();
        if (currentUserId != null) {
            updateGreeting();
            loadDashboardData(currentUserId);
        }
    }
}