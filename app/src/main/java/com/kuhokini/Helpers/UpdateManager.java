package com.kuhokini.Helpers;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.UpdateAvailability;

public class UpdateManager {
    private static final int UPDATE_REQUEST_CODE = 160;
    private final Activity activity;
    private final AppUpdateManager appUpdateManager;

    public UpdateManager(Activity activity) {
        this.activity = activity;
        this.appUpdateManager = AppUpdateManagerFactory.create(activity);
    }

    public void checkForUpdates() {
        appUpdateManager.getAppUpdateInfo().addOnSuccessListener(appUpdateInfo -> {
            // Log update availability for debugging
            Log.d("UpdateManager", "Update availability: " + appUpdateInfo.updateAvailability());
            Log.d("UpdateManager", "Available version code: " + appUpdateInfo.availableVersionCode());

            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE) {
                // Check if the update is allowed (network type, etc.)
                if (appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
                    showUpdateDialog(appUpdateInfo);
                } else {
                    Log.d("UpdateManager", "Immediate update not allowed");
                }
            } else if (appUpdateInfo.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                startImmediateUpdate(appUpdateInfo);
            } else {
                Log.d("UpdateManager", "No update available");
            }
        }).addOnFailureListener(e -> {
            Log.e("UpdateManager", "Failed to check for updates", e);
        });
    }

    private void showUpdateDialog(AppUpdateInfo appUpdateInfo) {
        new AlertDialog.Builder(activity)
                .setTitle("New Update Available")
                .setMessage("A new version of the app is available. Please update for the best experience.")
                .setPositiveButton("Update", (dialog, which) -> {
                    startImmediateUpdate(appUpdateInfo);
                })
                .setNegativeButton("Later", (dialog, which) -> {
                    // Optional: You can implement logic to remind later
                    Toast.makeText(activity, "You can update later from Play Store", Toast.LENGTH_SHORT).show();
                })
                .setCancelable(false)
                .show();
    }

    private void startImmediateUpdate(AppUpdateInfo appUpdateInfo) {
        try {
            appUpdateManager.startUpdateFlowForResult(
                    appUpdateInfo,
                    AppUpdateType.IMMEDIATE, // Or AppUpdateType.FLEXIBLE for flexible updates
                    activity,
                    UPDATE_REQUEST_CODE);
        } catch (Exception e) {
            // Fallback to Play Store if in-app update fails
            openPlayStore();
        }
    }

    private void openPlayStore() {
        try {
            activity.startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("market://details?id=" + activity.getPackageName())));
        } catch (android.content.ActivityNotFoundException e) {
            activity.startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id=" + activity.getPackageName())));
        }
    }

    public void onResume() {
        appUpdateManager.getAppUpdateInfo().addOnSuccessListener(appUpdateInfo -> {
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                // If an update is already running, resume the update
                startImmediateUpdate(appUpdateInfo);
            }
        });
    }
}
