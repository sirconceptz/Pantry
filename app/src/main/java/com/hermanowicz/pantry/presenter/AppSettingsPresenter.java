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

package com.hermanowicz.pantry.presenter;

import android.content.SharedPreferences;

import androidx.annotation.NonNull;

import com.hermanowicz.pantry.activity.AppSettingsActivity;
import com.hermanowicz.pantry.model.AppSettingsModel;

public class AppSettingsPresenter {

    private final AppSettingsActivity.MyPreferenceFragment view;
    private final AppSettingsModel model;

    public AppSettingsPresenter(@NonNull AppSettingsActivity.MyPreferenceFragment view, @NonNull SharedPreferences preferences) {
        this.view = view;
        this.model = new AppSettingsModel(preferences);
    }

    public void clearDatabase() {
        view.onDatabaseClear();
    }

    public void reCreateNotifications(){
        view.recreateNotifications();
    }

    public void showStoredPreferences() {
        showSelectedTheme();
        showSelectedScanCamera();
        showEmailAddress();
        showDaysToNotification();
        showVersionCode();
    }

    public void showSelectedTheme() {
        view.showSelectedTheme(model.getSelectedAppTheme());
    }

    public void showSelectedScanCamera() {
        view.showSelectedScanCamera(model.getSelectedScanCamera());
    }

    public void showEmailAddress() {
        if(model.isValidEmail())
            view.showEmailAddress(model.getEmailAddress());
        else{
            model.clearEmailAddress();
            view.setEmailPreferences();
        }
    }

    public void showDaysToNotification() {
        view.showDaysToNotification(model.getDaysToNotification());
    }

    private void showVersionCode() {
        view.showVersionCode(model.getAppVersion());
    }
}