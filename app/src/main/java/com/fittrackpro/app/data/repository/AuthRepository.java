package com.fittrackpro. app.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.fittrackpro.app.data.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * AuthRepository handles all authentication operations.
 *
 * Key responsibilities:
 * - Register new users with email/password
 * - Validate username uniqueness
 * - Login/logout
 * - Create user document in Firestore
 */
public class AuthRepository {

    private final FirebaseAuth firebaseAuth;
    private final FirebaseFirestore firestore;
    private final Executor executor;

    public AuthRepository() {
        this.firebaseAuth = FirebaseAuth. getInstance();
        this.firestore = FirebaseFirestore.getInstance();
        this.executor = Executors.newSingleThreadExecutor();
    }

    /**
     * Check if username is available (globally unique)
     * Returns true if available, false if taken, null if error checking
     */
    public LiveData<Boolean> isUsernameAvailable(String username) {
        MutableLiveData<Boolean> result = new MutableLiveData<>();

        firestore.collection("users")
                .whereEqualTo("username", username)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    // If query returns empty, username is available
                    result.setValue(querySnapshot.isEmpty());
                })
                .addOnFailureListener(e -> {
                    // On error, return null to indicate we couldn't check
                    // This prevents falsely claiming username is taken
                    result.setValue(null);
                });

        return result;
    }

    /**
     * Register new user with email, password, username, and display name
     */
    public LiveData<AuthResult> registerUser(String email, String password, String username, String displayName) {
        MutableLiveData<AuthResult> result = new MutableLiveData<>();

        // First, verify username is unique
        firestore.collection("users")
                .whereEqualTo("username", username)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (!querySnapshot.isEmpty()) {
                        result.setValue(new AuthResult(false, "Username already taken", null));
                        return;
                    }

                    // Username is unique, proceed with Firebase Auth
                    firebaseAuth.createUserWithEmailAndPassword(email, password)
                            .addOnSuccessListener(authResult -> {
                                FirebaseUser firebaseUser = authResult.getUser();
                                if (firebaseUser != null) {
                                    String userId = firebaseUser.getUid();

                                    // Create user document in Firestore
                                    User user = new User(userId, email, username, displayName);

                                    firestore.collection("users")
                                            .document(userId)
                                            .set(user)
                                            .addOnSuccessListener(aVoid -> {
                                                result.setValue(new AuthResult(true, "Registration successful", user));
                                            })
                                            .addOnFailureListener(e -> {
                                                result.setValue(new AuthResult(false, "Failed to create user profile:  " + e.getMessage(), null));
                                            });
                                } else {
                                    result.setValue(new AuthResult(false, "User creation failed", null));
                                }
                            })
                            .addOnFailureListener(e -> {
                                result. setValue(new AuthResult(false, e.getMessage(), null));
                            });
                })
                .addOnFailureListener(e -> {
                    result.setValue(new AuthResult(false, "Failed to verify username: " + e.getMessage(), null));
                });

        return result;
    }

    /**
     * Login with email and password
     */
    public LiveData<AuthResult> loginUser(String email, String password) {
        MutableLiveData<AuthResult> result = new MutableLiveData<>();

        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    FirebaseUser firebaseUser = authResult.getUser();
                    if (firebaseUser != null) {
                        String userId = firebaseUser.getUid();

                        // Fetch user data from Firestore
                        firestore.collection("users")
                                .document(userId)
                                .get()
                                .addOnSuccessListener(documentSnapshot -> {
                                    User user = documentSnapshot.toObject(User.class);
                                    if (user != null) {
                                        result.setValue(new AuthResult(true, "Login successful", user));
                                    } else {
                                        result.setValue(new AuthResult(false, "User profile not found", null));
                                    }
                                })
                                .addOnFailureListener(e -> {
                                    result.setValue(new AuthResult(false, "Failed to fetch user data: " + e.getMessage(), null));
                                });
                    } else {
                        result.setValue(new AuthResult(false, "Login failed", null));
                    }
                })
                .addOnFailureListener(e -> {
                    result.setValue(new AuthResult(false, e.getMessage(), null));
                });

        return result;
    }

    /**
     * Logout current user
     */
    public void logout() {
        firebaseAuth.signOut();
    }

    /**
     * Get current user ID
     */
    public String getCurrentUserId() {
        FirebaseUser user = firebaseAuth. getCurrentUser();
        return user != null ? user.getUid() : null;
    }

    /**
     * Check if user is logged in
     */
    public boolean isUserLoggedIn() {
        return firebaseAuth.getCurrentUser() != null;
    }

    /**
     * Auth result wrapper class
     */
    public static class AuthResult {
        private final boolean success;
        private final String message;
        private final User user;

        public AuthResult(boolean success, String message, User user) {
            this.success = success;
            this.message = message;
            this.user = user;
        }

        public boolean isSuccess() { return success; }
        public String getMessage() { return message; }
        public User getUser() { return user; }
    }
}