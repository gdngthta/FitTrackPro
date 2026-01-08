package com.fittrackpro.app.ui.social;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle. AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx. lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;

import com.fittrackpro.app.data.model.LeaderboardEntry;
import com. fittrackpro.app. data.repository.SocialRepository;
import com.fittrackpro.app.util.Constants;

import java.util.List;

/**
 * LeaderboardViewModel manages leaderboard display.
 *
 * Shows:
 * - Global leaderboard
 * - Friends-only leaderboard
 * - Current user's rank highlighted
 */
public class LeaderboardViewModel extends AndroidViewModel {

    private final SocialRepository socialRepository;

    private final MutableLiveData<String> userId = new MutableLiveData<>();
    private final MutableLiveData<Boolean> showFriendsOnly = new MutableLiveData<>(false);

    private final MediatorLiveData<List<LeaderboardEntry>> leaderboard = new MediatorLiveData<>();

    public LeaderboardViewModel(@NonNull Application application) {
        super(application);

        this.socialRepository = new SocialRepository();
    }

    public void setUserId(String userId) {
        this.userId.setValue(userId);
        loadLeaderboard();
    }

    public void toggleLeaderboardMode() {
        Boolean current = showFriendsOnly. getValue();
        showFriendsOnly.setValue(current != null ? !current : true);
        loadLeaderboard();
    }

    private void loadLeaderboard() {
        String currentUserId = userId.getValue();
        Boolean friendsOnly = showFriendsOnly.getValue();

        if (currentUserId == null) {
            return;
        }

        if (friendsOnly != null && friendsOnly) {
            LiveData<List<LeaderboardEntry>> source =
                    socialRepository.getFriendsLeaderboard(currentUserId);
            leaderboard.addSource(source, leaderboard::setValue);
        } else {
            LiveData<List<LeaderboardEntry>> source =
                    socialRepository.getGlobalLeaderboard(currentUserId, Constants.LEADERBOARD_DEFAULT_LIMIT);
            leaderboard.addSource(source, leaderboard::setValue);
        }
    }

    public LiveData<List<LeaderboardEntry>> getLeaderboard() {
        return leaderboard;
    }

    public LiveData<Boolean> getShowFriendsOnly() {
        return showFriendsOnly;
    }
}