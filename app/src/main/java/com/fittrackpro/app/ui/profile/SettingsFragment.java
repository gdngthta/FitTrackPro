package com.fittrackpro.app.ui.profile;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.fittrackpro.app.R;
import com.fittrackpro.app.databinding.FragmentSettingsBinding;
import com.fittrackpro.app.util.Constants;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * SettingsFragment manages app settings and preferences.
 * 
 * Features:
 * - Measurement unit toggle (Metric/Imperial)
 * - Workout reminders preference
 * - Show equivalence preference  
 * - Logout functionality
 * - Delete account with confirmation
 */
public class SettingsFragment extends Fragment {

    private FragmentSettingsBinding binding;
    private SharedPreferences prefs;
    private FirebaseAuth auth;
    private FirebaseFirestore firestore;
    private boolean isMetric = true;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        prefs = requireContext().getSharedPreferences(Constants.PREF_NAME, android.content.Context.MODE_PRIVATE);
        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        loadSettings();
        setupListeners();
    }

    private void loadSettings() {
        // Weight unit
        String weightUnit = prefs.getString(Constants.PREF_WEIGHT_UNIT, Constants.UNIT_KG);
        isMetric = weightUnit.equals(Constants.UNIT_KG);
        updateUnitButtons();

        // Workout reminders
        boolean workoutReminders = prefs.getBoolean("workout_reminders", false);
        binding.switchWorkoutReminders.setChecked(workoutReminders);

        // Show equivalence
        boolean showEquivalence = prefs.getBoolean(Constants.PREF_SHOW_EQUIVALENCE, true);
        binding.switchShowEquivalence.setChecked(showEquivalence);
    }

    private void updateUnitButtons() {
        if (isMetric) {
            binding.buttonMetric.setBackgroundColor(getResources().getColor(R.color.md_theme_primary, null));
            binding.buttonMetric.setTextColor(getResources().getColor(R.color.md_theme_onPrimary, null));
            binding.buttonImperial.setBackgroundColor(getResources().getColor(android.R.color.transparent, null));
            binding.buttonImperial.setTextColor(getResources().getColor(R.color.md_theme_onSurface, null));
        } else {
            binding.buttonImperial.setBackgroundColor(getResources().getColor(R.color.md_theme_primary, null));
            binding.buttonImperial.setTextColor(getResources().getColor(R.color.md_theme_onPrimary, null));
            binding.buttonMetric.setBackgroundColor(getResources().getColor(android.R.color.transparent, null));
            binding.buttonMetric.setTextColor(getResources().getColor(R.color.md_theme_onSurface, null));
        }
    }

    private void setupListeners() {
        // Back button
        binding.buttonBack.setOnClickListener(v ->
                Navigation.findNavController(v).navigateUp()
        );

        // Unit toggle buttons
        binding.buttonMetric.setOnClickListener(v -> {
            if (!isMetric) {
                isMetric = true;
                prefs.edit().putString(Constants.PREF_WEIGHT_UNIT, Constants.UNIT_KG).apply();
                updateUnitButtons();
            }
        });

        binding.buttonImperial.setOnClickListener(v -> {
            if (isMetric) {
                isMetric = false;
                prefs.edit().putString(Constants.PREF_WEIGHT_UNIT, Constants.UNIT_LB).apply();
                updateUnitButtons();
            }
        });

        // Workout reminders switch
        binding.switchWorkoutReminders.setOnCheckedChangeListener((buttonView, isChecked) -> {
            prefs.edit().putBoolean("workout_reminders", isChecked).apply();
            // TODO: Schedule/cancel workout reminder notifications
        });

        // Show equivalence switch
        binding.switchShowEquivalence.setOnCheckedChangeListener((buttonView, isChecked) -> {
            prefs.edit().putBoolean(Constants.PREF_SHOW_EQUIVALENCE, isChecked).apply();
        });

        // Logout button
        binding.buttonLogout.setOnClickListener(v -> {
            handleLogout();
        });

        // Delete account button
        binding.buttonDeleteAccount.setOnClickListener(v -> {
            showDeleteAccountConfirmation();
        });
    }

    private void handleLogout() {
        auth.signOut();
        
        // Clear local preferences
        prefs.edit().clear().apply();
        
        // Return to auth activity
        requireActivity().finish();
        
        Toast.makeText(requireContext(), R.string.logout_message, Toast.LENGTH_SHORT).show();
    }

    private void showDeleteAccountConfirmation() {
        new AlertDialog.Builder(requireContext())
                .setTitle(R.string.delete_account_confirm_title)
                .setMessage(R.string.delete_account_confirm_message)
                .setPositiveButton(R.string.delete, (dialog, which) -> {
                    deleteAccount();
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    private void deleteAccount() {
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(requireContext(), R.string.delete_account_failed, Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = currentUser.getUid();

        // Delete user data from Firestore
        firestore.collection(Constants.COLLECTION_USERS)
                .document(userId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    // Delete completed workouts
                    deleteUserCollection(userId, Constants.COLLECTION_COMPLETED_WORKOUTS);
                    
                    // Delete workout programs
                    deleteUserCollection(userId, Constants.COLLECTION_WORKOUT_PROGRAMS);
                    
                    // Delete personal records
                    deleteUserCollection(userId, Constants.COLLECTION_PERSONAL_RECORDS);
                    
                    // Delete nutrition profiles
                    deleteUserCollection(userId, Constants.COLLECTION_NUTRITION_PROFILES);
                    
                    // Delete meals logged
                    deleteUserCollection(userId, Constants.COLLECTION_MEALS_LOGGED);
                    
                    // Delete friendships
                    deleteUserCollection(userId, Constants.COLLECTION_FRIENDSHIPS);
                    
                    // Finally delete Firebase Auth account
                    currentUser.delete()
                            .addOnSuccessListener(aVoid2 -> {
                                Toast.makeText(requireContext(), R.string.delete_account_success, Toast.LENGTH_SHORT).show();
                                
                                // Clear local data
                                prefs.edit().clear().apply();
                                
                                // Return to auth activity
                                requireActivity().finish();
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(requireContext(), R.string.delete_account_failed, Toast.LENGTH_SHORT).show();
                            });
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(requireContext(), R.string.delete_account_failed, Toast.LENGTH_SHORT).show();
                });
    }

    private void deleteUserCollection(String userId, String collectionName) {
        firestore.collection(collectionName)
                .whereEqualTo("userId", userId)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    querySnapshot.getDocuments().forEach(doc -> doc.getReference().delete());
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}