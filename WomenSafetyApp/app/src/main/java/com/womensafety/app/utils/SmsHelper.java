package com.womensafety.app.utils;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.telephony.SmsManager;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import com.womensafety.app.models.Contact;

import java.util.List;

public class SmsHelper {

    public static void sendSOSToContacts(Context context, List<Contact> contacts,
                                         double latitude, double longitude, String userName) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(context, "SMS permission required", Toast.LENGTH_SHORT).show();
            return;
        }

        String mapsLink = LocationHelper.getMapsLink(latitude, longitude);
        String message = "🚨 EMERGENCY ALERT\n"
                + userName + " may be in danger and needs immediate help.\n"
                + "Please contact them or come to their location immediately.\n"
                + "📍 Location:\n" + mapsLink;

        SmsManager smsManager = SmsManager.getDefault();
        for (Contact contact : contacts) {
            try {
                smsManager.sendTextMessage(contact.getPhoneNumber(), null, message, null, null);
            } catch (Exception e) {
                Toast.makeText(context, "Failed to SMS " + contact.getContactName(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    public static void makeEmergencyCall(Context context, String phoneNumber) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE)
                != PackageManager.PERMISSION_GRANTED) {
            Intent dialIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phoneNumber));
            context.startActivity(dialIntent);
            return;
        }
        Intent callIntent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phoneNumber));
        context.startActivity(callIntent);
    }
}
