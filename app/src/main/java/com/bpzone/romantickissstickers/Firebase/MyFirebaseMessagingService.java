package com.bpzone.romantickissstickers.Firebase;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.bpzone.romantickissstickers.WriteLog;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private final String TAG = "MyFirebaseMessagingService";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        try {
            if (remoteMessage.getData().size() > 0) {
                if (true) {
                    scheduleJob();
                } else {
                    handleNow();
                }
            }
        } catch (Exception e) {
            WriteLog.GetInstance().addToLog(TAG, "onMessageReceived", e.getMessage(), e);
        }
    }

    @Override
    public void onNewToken(String token) {
        sendRegistrationToServer(token);
    }

    private void scheduleJob() {
        // [START dispatch_job]
        // [END dispatch_job]
    }

    private void handleNow() {
    }


    private void sendRegistrationToServer(String token) {
    }
}