package com.example.project_prm392.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.ListPreference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.Preference;

import com.example.project_prm392.R;
import com.example.project_prm392.ui.profile.ChangePasswordActivity;
import com.example.project_prm392.ui.profile.PrivacyPolicyActivity;

public class SettingsFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);

        Preference changePasswordPref = findPreference("change_password");
        if (changePasswordPref != null) {
            changePasswordPref.setOnPreferenceClickListener(preference -> {
                startActivity(new Intent(getContext(), ChangePasswordActivity.class));
                return true;
            });
        }

        Preference privacyPolicyPref = findPreference("privacy_policy");
        if (privacyPolicyPref != null) {
            privacyPolicyPref.setOnPreferenceClickListener(preference -> {
                startActivity(new Intent(getContext(), PrivacyPolicyActivity.class));
                return true;
            });
        }

        // Xử lý sự kiện thay đổi cho Chế độ tối
        ListPreference darkModePref = findPreference("dark_mode");
        if(darkModePref != null) {
            darkModePref.setOnPreferenceChangeListener((preference, newValue) -> {
                String theme = (String) newValue;
                switch (theme) {
                    case "light":
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                        break;
                    case "dark":
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                        break;
                    default:
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                        break;
                }
                return true;
            });
        }
    }
}