/*
 * Copyright (c) 2019-2021
 * Mateusz Hermanowicz - All rights reserved.
 * My Pantry
 * https://www.mypantry.eu
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hermanowicz.pantry.util;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.hermanowicz.pantry.db.product.Product;
import com.hermanowicz.pantry.db.product.ProductDb;
import com.hermanowicz.pantry.receiver.NotificationBroadcastReceiver;

import java.sql.Date;
import java.util.Calendar;
import java.util.List;

/**
 * <h1>Notification</h1>
 * Class to support the notification in the application.
 *
 * @author  Mateusz Hermanowicz
 * @version 1.0
 * @since   1.0
 */

public class Notification {

    public static final int NOTIFICATION_DEFAULT_HOUR = 12;
    public static final int NOTIFICATION_DEFAULT_DAYS = 3;
    private static final String PREFERENCES_DAYS_TO_NOTIFICATIONS = "HOW_MANY_DAYS_BEFORE_EXPIRATION_DATE_SEND_A_NOTIFICATION?";
    private static final String PREFERENCES_HOUR_OF_NOTIFICATIONS = "HOUR_OF_NOTIFICATIONS?";

    private static Calendar createCalendar(@NonNull Context context, @NonNull String expirationDate){
        String[] dateArray = expirationDate.split("-");
        Calendar calendar = Calendar.getInstance();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);

        calendar.set(Calendar.YEAR, Integer.parseInt(dateArray[0]));
        calendar.set(Calendar.MONTH, (Integer.parseInt(dateArray[1]))-1);
        calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(dateArray[2]));
        calendar.set(Calendar.HOUR_OF_DAY, preferences.getInt(
                PREFERENCES_HOUR_OF_NOTIFICATIONS, Notification.NOTIFICATION_DEFAULT_HOUR));
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.add(Calendar.DAY_OF_MONTH, -(preferences.getInt(
                PREFERENCES_DAYS_TO_NOTIFICATIONS, Notification.NOTIFICATION_DEFAULT_DAYS)));

        return calendar;
    }

    public static void createNotification(@NonNull Context context, @Nullable Product product) {
        Intent intent = new Intent(context, NotificationBroadcastReceiver.class);
        assert product != null;
        intent.putExtra("PRODUCT_NAME", product.getName());
        intent.putExtra("PRODUCT_ID", product.getId());
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, product.getId(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager)(context.getSystemService(Context.ALARM_SERVICE));

        if(!product.getExpirationDate().equals("-"))
        {
            Calendar calendar = createCalendar(context, product.getExpirationDate());

            if (Build.VERSION.SDK_INT >= 23) {
                assert alarmManager != null;
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,
                        calendar.getTimeInMillis(), pendingIntent);
            } else if (Build.VERSION.SDK_INT >= 19) {
                assert alarmManager != null;
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
            } else {
                assert alarmManager != null;
                alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
            }
        }
    }

    public static void createNotificationsForAllProducts(@NonNull Context context){
        ProductDb productDb = ProductDb.getInstance(context);
        List<Product> productsList = productDb.productsDao().getAllProductsList();

        for(int counter=0; counter < productsList.size(); counter++){
            Product selectedProduct = productsList.get(counter);
            String expirationDate = selectedProduct.getExpirationDate();
            if (Date.valueOf(expirationDate).after(new Date(System.currentTimeMillis())))
                Notification.createNotification(context, selectedProduct);
        }
    }

    public static void cancelNotification(@NonNull Context context, @NonNull Product product) {
        if(!product.getExpirationDate().equals("-")) {
            AlarmManager alarmManager = (AlarmManager) (context.getSystemService(Context.ALARM_SERVICE));
            Intent intent = new Intent(context, NotificationBroadcastReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    context, product.getId(), intent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
            pendingIntent.cancel();
            assert alarmManager != null;
            alarmManager.cancel(pendingIntent);
        }
    }

    public static void cancelAllNotifications(@NonNull Context context) {
        ProductDb productDb = ProductDb.getInstance(context);
        List<Product> productsList = productDb.productsDao().getAllProductsList();
        AlarmManager alarmManager = (AlarmManager)(context.getSystemService(Context.ALARM_SERVICE));
        Intent intent = new Intent(context, NotificationBroadcastReceiver.class);
        for(int i = 0; i < productsList.size(); i++){
            if(!productsList.get(i).getExpirationDate().equals("-")) {
                PendingIntent pendingIntent = PendingIntent.getBroadcast(
                        context, productsList.get(i).getId(), intent,
                        PendingIntent.FLAG_UPDATE_CURRENT);
                pendingIntent.cancel();
                alarmManager.cancel(pendingIntent);
            }
        }
    }
}