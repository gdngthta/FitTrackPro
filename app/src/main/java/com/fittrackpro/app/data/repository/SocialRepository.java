package com.fittrackpro. app.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.fittrackpro.app.data.model. Friendship;
import com.fittrackpro.app.data.model.LeaderboardEntry;
import com. google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

/**
 * SocialRepository manages friendships and leaderboards.
 *
 * Key responsibilities:
 * - Search users by username
 * - Add/remove friends
 * - Fetch global leaderboard
 * - Fetch friends-only leaderboard
 */
public class SocialRepository {

    private final FirebaseFirestore firestore;

    public SocialRepository() {
        this.firestore = FirebaseFirestore.getInstance();
    }

    // ==================== FRIEND MANAGEMENT ====================

    /**
     * Search users by username
     */
    public LiveData<List<LeaderboardEntry>> searchUsersByUsername(String query) {
        MutableLiveData<List<LeaderboardEntry>> result = new MutableLiveData<>();

        firestore.collection("users")
                .orderBy("username")
                .startAt(query)
                .endAt(query + "\uf8ff")
                .limit(20)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<LeaderboardEntry> users = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : querySnapshot) {
                        LeaderboardEntry entry = new LeaderboardEntry();
                        entry.setUserId(doc. getString("userId"));
                        entry. setUsername(doc.getString("username"));
                        entry.setDisplayName(doc.getString("displayName"));
                        entry.setTotalVolume(doc.getDouble("totalVolumeLifted") != null ?
                                doc.getDouble("totalVolumeLifted") : 0.0);
                        users.add(entry);
                    }
                    result.setValue(users);
                })
                .addOnFailureListener(e -> result. setValue(new ArrayList<>()));

        return result;
    }

    /**
     * Add friend
     */
    public LiveData<Boolean> addFriend(String userId, String friendUserId, String friendUsername,
                                       String friendDisplayName) {
        MutableLiveData<Boolean> result = new MutableLiveData<>();

        String friendshipId = firestore.collection("friendships").document().getId();

        Friendship friendship = new Friendship();
        friendship.setFriendshipId(friendshipId);
        friendship.setUserId(userId);
        friendship.setFriendUserId(friendUserId);
        friendship.setFriendUsername(friendUsername);
        friendship. setFriendDisplayName(friendDisplayName);
        friendship.setCreatedAt(Timestamp.now());

        firestore. collection("friendships")
                .document(friendshipId)
                .set(friendship)
                .addOnSuccessListener(aVoid -> result.setValue(true))
                .addOnFailureListener(e -> result.setValue(false));

        return result;
    }

    /**
     * Remove friend
     */
    public LiveData<Boolean> removeFriend(String userId, String friendUserId) {
        MutableLiveData<Boolean> result = new MutableLiveData<>();

        firestore.collection("friendships")
                .whereEqualTo("userId", userId)
                .whereEqualTo("friendUserId", friendUserId)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (!querySnapshot.isEmpty()) {
                        querySnapshot.getDocuments().get(0).getReference().delete()
                                .addOnSuccessListener(aVoid -> result. setValue(true))
                                . addOnFailureListener(e -> result.setValue(false));
                    } else {
                        result.setValue(false);
                    }
                })
                .addOnFailureListener(e -> result.setValue(false));

        return result;
    }

    /**
     * Get user's friends
     */
    public LiveData<List<Friendship>> getFriends(String userId) {
        MutableLiveData<List<Friendship>> result = new MutableLiveData<>();

        firestore.collection("friendships")
                .whereEqualTo("userId", userId)
                .orderBy("friendUsername")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<Friendship> friends = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : querySnapshot) {
                        Friendship friendship = doc.toObject(Friendship.class);
                        friends. add(friendship);
                    }
                    result.setValue(friends);
                })
                .addOnFailureListener(e -> result.setValue(new ArrayList<>()));

        return result;
    }

    // ==================== LEADERBOARDS ====================

    /**
     * Get global leaderboard
     */
    public LiveData<List<LeaderboardEntry>> getGlobalLeaderboard(String currentUserId, int limit) {
        MutableLiveData<List<LeaderboardEntry>> result = new MutableLiveData<>();

        firestore.collection("users")
                .orderBy("totalVolumeLifted", Query.Direction.DESCENDING)
                .limit(limit)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<LeaderboardEntry> entries = new ArrayList<>();
                    int rank = 1;
                    for (QueryDocumentSnapshot doc : querySnapshot) {
                        LeaderboardEntry entry = new LeaderboardEntry();
                        entry.setUserId(doc.getString("userId"));
                        entry.setUsername(doc.getString("username"));
                        entry.setDisplayName(doc.getString("displayName"));
                        entry.setTotalVolume(doc.getDouble("totalVolumeLifted") != null ?
                                doc.getDouble("totalVolumeLifted") : 0.0);
                        entry.setRank(rank++);
                        entry.setCurrentUser(entry.getUserId().equals(currentUserId));
                        entries.add(entry);
                    }
                    result.setValue(entries);
                })
                .addOnFailureListener(e -> result.setValue(new ArrayList<>()));

        return result;
    }

    /**
     * Get friends-only leaderboard
     */
    public LiveData<List<LeaderboardEntry>> getFriendsLeaderboard(String userId) {
        MutableLiveData<List<LeaderboardEntry>> result = new MutableLiveData<>();

        // First get friend IDs
        firestore.collection("friendships")
                .whereEqualTo("userId", userId)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<String> friendIds = new ArrayList<>();
                    friendIds.add(userId); // Include current user

                    for (QueryDocumentSnapshot doc : querySnapshot) {
                        friendIds.add(doc.getString("friendUserId"));
                    }

                    if (friendIds.isEmpty()) {
                        result.setValue(new ArrayList<>());
                        return;
                    }

                    // Fetch user data for all friends
                    // Note:  Firestore has limit of 10 items in "in" query, need to batch if more friends
                    firestore.collection("users")
                            .whereIn("userId", friendIds. subList(0, Math.min(friendIds.size(), 10)))
                            .orderBy("totalVolumeLifted", Query.Direction.DESCENDING)
                            .get()
                            .addOnSuccessListener(userSnapshot -> {
                                List<LeaderboardEntry> entries = new ArrayList<>();
                                int rank = 1;
                                for (QueryDocumentSnapshot doc :  userSnapshot) {
                                    LeaderboardEntry entry = new LeaderboardEntry();
                                    entry. setUserId(doc.getString("userId"));
                                    entry.setUsername(doc.getString("username"));
                                    entry.setDisplayName(doc.getString("displayName"));
                                    entry.setTotalVolume(doc.getDouble("totalVolumeLifted") != null ?
                                            doc. getDouble("totalVolumeLifted") : 0.0);
                                    entry.setRank(rank++);
                                    entry.setCurrentUser(entry.getUserId().equals(userId));
                                    entries.add(entry);
                                }
                                result. setValue(entries);
                            })
                            .addOnFailureListener(e -> result.setValue(new ArrayList<>()));
                })
                .addOnFailureListener(e -> result.setValue(new ArrayList<>()));

        return result;
    }
}