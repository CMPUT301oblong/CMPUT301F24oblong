package com.example.oblong;

import static android.os.Build.VERSION_CODES.O;

import static androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;
import android.Manifest;
import android.widget.RemoteViews;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.checkerframework.common.returnsreceiver.qual.This;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    /**
     * from https://firebase.google.com/docs/cloud-messaging/android/client#monitor-token-generation
     * There are two scenarios when onNewToken is called:
     * 1) When a new token is generated on initial app startup
     * 2) Whenever an existing token is changed
     * Under #2, there are three scenarios when the existing token is changed:
     * A) App is restored to a new device
     * B) User uninstalls/reinstalls the app
     * C) User clears app data
     */
    @Override
    public void onNewToken(@NonNull String token) {
        Log.d("TAG", "Refreshed token: " + token);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // FCM registration token to your app server.
        //sendRegistrationToServer(token);    need to implement this method if want to use
    }

    //method taken from https://github.com/datanapps/FirebaseMessaging/blob/master/app/src/main/java/com/datanapps/firebasemessaging/fcm/MyFirebaseMessagingService.java
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.d("FCM", remoteMessage.toString());
        //TODO implement what to do when message received
        //code taken from https://www.geeksforgeeks.org/how-to-push-notification-in-android-using-firebase-cloud-messaging/
        /*  if there is a data payload
        if(remoteMessage.getData().size()>0){
            showNotification(remoteMessage.getData().get("title"), remoteMessage.getData().get("message"));
        } */

        // Only notification payload, no data payload
        if (remoteMessage.getNotification() != null) {
            // Since the notification is received directly
            // from FCM, the title and the body can be
            // fetched directly as below.
            //showNotification(remoteMessage.getNotification().getTitle(), remoteMessage.getNotification().getBody());
            //must implement showNotification if i want to use it
        }
    }

    /*          Commented code taken from firebase cloud messaging documentation
    *********** This code is used to get user notification permissions ***********
    // Declare the launcher at the top of your Activity/Fragment:
private final ActivityResultLauncher<String> requestPermissionLauncher =
        registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
            if (isGranted) {
                // FCM SDK (and your app) can post notifications.
            } else {
                // TODO: Inform user that that your app will not show notifications.
            }
        });

* May not need this method
private void askNotificationPermission() {
    // This is only necessary for API level >= 33 (TIRAMISU)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED) {
            // FCM SDK (and your app) can post notifications.
        } else if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
            // TODO: display an educational UI explaining to the user the features that will be enabled
            //       by them granting the POST_NOTIFICATION permission. This UI should provide the user
            //       "OK" and "No thanks" buttons. If the user selects "OK," directly request the permission.
            //       If the user selects "No thanks," allow the user to continue without notifications.
        } else {
            // Directly ask for the permission
            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
        }
    }
}

    *********** This code is used to get user registration token ***********
    * should be in start up activity
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w("FCM", "Fetching FCM registration token failed", task.getException());
                            return;
                        }

                        // Get new FCM registration token
                        String token = task.getResult();
                    }
                });
                *
    */


}

/*
Possibly useful websites:
    https://www.geeksforgeeks.org/how-to-use-firebase-cloud-messaging-fcm-in-android/
    https://github.com/datanapps/FirebaseMessaging/tree/master
    https://riptutorial.com/android/example/15755/firebase-cloud-messaging
    https://stackoverflow.com/questions/13902115/how-to-create-a-notification-with-notificationcompat-builder
    https://www.tutorialspoint.com/android/android_push_notification.htm
    https://firebase.google.com/docs/cloud-messaging/concept-options
 */