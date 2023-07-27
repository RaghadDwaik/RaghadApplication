package com.example.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class TestUtils {

    private static final List<String> toastMessages = new ArrayList<>();
    private static final List<Intent> startedActivities = new ArrayList<>();
    private static final List<Intent> startedIntents = new ArrayList<>();

    public static String getLastToastMessage() {
        if (!toastMessages.isEmpty()) {
            return toastMessages.get(toastMessages.size() - 1);
        }
        return null;
    }

    public static Intent getLastStartedActivity() {
        if (!startedActivities.isEmpty()) {
            return startedActivities.get(startedActivities.size() - 1);
        }
        return null;
    }

    public static Intent getLastIntent() {
        if (!startedIntents.isEmpty()) {
            return startedIntents.get(startedIntents.size() - 1);
        }
        return null;
    }

    public static void clearToastMessages() {
        toastMessages.clear();
    }

    public static void clearStartedActivities() {
        startedActivities.clear();
    }

    public static void clearStartedIntents() {
        startedIntents.clear();
    }

    public static void interceptToast(Activity activity, CharSequence message) {
        LayoutInflater inflater = activity.getLayoutInflater();
        View layout = inflater.inflate(R.layout.custom_toast, activity.findViewById(R.id.toast_root));
        TextView text = layout.findViewById(R.id.toast_error);
        text.setText(message);

        Toast toast = new Toast(activity);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();

        toastMessages.add(message.toString());
    }

    public static void interceptStartedActivity(Intent intent) {
        startedActivities.add(intent);
    }

    public static void interceptStartedIntent(Intent intent) {
        startedIntents.add(intent);
    }
}

