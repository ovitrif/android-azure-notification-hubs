package com.bringme.poc.notif.notifications;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.bringme.poc.notif.MainActivity;
import com.bringme.poc.notif.notifications.settings.NotificationSettings;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.microsoft.windowsazure.messaging.NotificationHub;

public class RegistrationIntentService extends IntentService {

    private static final String TAG = "RegIntentService";

    private NotificationHub hub;

    public RegistrationIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String resultString = null;
        String regID = null;

        try {
            InstanceID instanceID = InstanceID.getInstance(this);
            String token = instanceID
                    .getToken(NotificationSettings.SENDER_ID, GoogleCloudMessaging.INSTANCE_ID_SCOPE);

            Log.i(TAG, "Got GCM Registration Token: " + token);

            // Storing the registration id that indicates whether the generated token has been
            // sent to your server. If it is not stored, send the token to your server,
            // otherwise your server should have already received the token.
            if ((regID = sharedPreferences.getString("registrationID", null)) == null) {

                NotificationHub hub = new NotificationHub(
                        NotificationSettings.HUB_NAME,
                        NotificationSettings.HUB_LISTEN_CONNECTION_STRING,
                        this);

                Log.i(TAG, "Attempting to register with NH using token : " + token);

                regID = hub.register(token).getRegistrationId();

                // If you want to use tags...
                // Refer to : https://azure.microsoft.com/en-us/documentation/articles/notification-hubs-routing-tag-expressions/
                // regID = hub.register(token, "tag1,tag2").getRegistrationId();

                resultString = "Registered Successfully - RegId : " + regID;
                Log.i(TAG, resultString);

                sharedPreferences.edit().putString("registrationID", regID).apply();
            } else {
                resultString = "Previously Registered Successfully - RegId : " + regID;
            }
        } catch (Exception e) {
            // If an exception happens while fetching the new token or updating our registration data
            // on a third-party server, this ensures that we'll attempt the update at a later time.
            Log.e(TAG, resultString = "Failed to complete token refresh", e);
        }

        // Notify UI that registration has completed.
        if (MainActivity.isVisible) {
            MainActivity.mainActivity.ToastNotify(resultString);
        }
    }
}

