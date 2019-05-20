/******************************************************************************
 * Copyright (c) 2019.                                                        *
 * Mateusz Hermanowicz                                                        *
 * My Pantry                                                                  *
 * https://www.mypantry.eu                                                    *
 * Released under Apache License Version 2.0                                  *
 ******************************************************************************/

package com.hermanowicz.pantry.dialog;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.hermanowicz.pantry.R;
import com.hermanowicz.pantry.interfaces.DialogListener;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ExpirationDateFilterDialog extends AppCompatDialogFragment implements DatePickerDialog.OnDateSetListener {

    @BindView(R.id.edittext_expirationDateSince)
    EditText edittextExpirationDateSince;
    @BindView(R.id.edittext_expirationDateFor)
    EditText edittextExpirationDateFor;
    @BindView(R.id.button_clear)
    Button btnClear;
    private Context context;
    private Resources resources;
    private Activity activity;
    private DialogListener dialogListener;
    private Calendar calendar;
    private DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    private Date dateExpirationSince, dateExpirationFor;
    private String filterExpirationDateSince;
    private String filterExpirationDateFor;
    private String expirationDateSinceConverted = "";
    private String expirationDateForConverted = "";
    private String[] dateArray;
    private DatePickerDialog.OnDateSetListener expirationDateSinceListener, expirationDateForListener;
    private int year, month, day;

    public ExpirationDateFilterDialog(String filterExpirationDateSince, String filterExpirationDateFor) {
        this.filterExpirationDateSince = filterExpirationDateSince;
        this.filterExpirationDateFor = filterExpirationDateFor;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        activity = getActivity();
        assert activity != null;
        context = activity.getApplicationContext();
        resources = context.getResources();

        DATE_FORMAT.setLenient(false);

        calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        LayoutInflater layoutInflater = activity.getLayoutInflater();

        View view = layoutInflater.inflate(R.layout.dialog_expiration_date, null);

        ButterKnife.bind(this, view);

        if (filterExpirationDateSince != null) {
            dateArray = filterExpirationDateSince.split("-");
            edittextExpirationDateSince.setText(dateArray[2] + "." + dateArray[1] + "." + dateArray[0]);
        }
        if (filterExpirationDateFor != null) {
            dateArray = filterExpirationDateFor.split("-");
            edittextExpirationDateFor.setText(dateArray[2] + "." + dateArray[1] + "." + dateArray[0]);
        }

        edittextExpirationDateSince.setOnClickListener(v -> {
            if (edittextExpirationDateSince.length() < 1) {
                year = calendar.get(Calendar.YEAR);
                month = calendar.get(Calendar.MONTH);
                day = calendar.get(Calendar.DAY_OF_MONTH);
            } else {
                String date = edittextExpirationDateSince.getText().toString();
                dateArray = date.split("\\.");
                year = Integer.valueOf(dateArray[2]);
                month = Integer.valueOf(dateArray[1]);
                day = Integer.valueOf(dateArray[0]);
            }
            DatePickerDialog dialog = new DatePickerDialog(
                    activity,
                    android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                    expirationDateSinceListener,
                    year, month, day);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();
        });

        edittextExpirationDateFor.setOnClickListener(v -> {
            if (edittextExpirationDateFor.length() < 1) {
                year = calendar.get(Calendar.YEAR);
                month = calendar.get(Calendar.MONTH);
                day = calendar.get(Calendar.DAY_OF_MONTH);
            } else {
                String date = edittextExpirationDateFor.getText().toString();
                dateArray = date.split("\\.");
                year = Integer.valueOf(dateArray[2]);
                month = Integer.valueOf(dateArray[1]);
                day = Integer.valueOf(dateArray[0]);
            }

            DatePickerDialog dialog = new DatePickerDialog(
                    activity,
                    android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                    expirationDateForListener,
                    year, month, day);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.getDatePicker();
            dialog.show();
        });

        expirationDateSinceListener = (datePicker, year, month, day) -> {
            month = month + 1;
            edittextExpirationDateSince.setText(day + "." + month + "." + year);
            expirationDateSinceConverted = year + "-" + month + "-" + day;
        };

        expirationDateForListener = (datePicker, year, month, day) -> {
            month = month + 1;
            edittextExpirationDateFor.setText(day + "." + month + "." + year);
            expirationDateForConverted = year + "-" + month + "-" + day;
        };

        btnClear.setOnClickListener(view12 -> {
            edittextExpirationDateSince.setText("");
            edittextExpirationDateFor.setText("");
        });
        builder.setView(view)
                .setTitle(resources.getString(R.string.ProductDetailsActivity_expiration_date))
                .setNegativeButton(resources.getString(R.string.MyPantryActivity_cancel), (dialog, which) -> {
                })
                .setPositiveButton(resources.getString(R.string.MyPantryActivity_set), (dialog, which) -> {
                    try {
                        filterExpirationDateSince = DATE_FORMAT.format(DATE_FORMAT.parse(expirationDateSinceConverted));
                        dateExpirationSince = DATE_FORMAT.parse(expirationDateSinceConverted);
                    } catch (ParseException e) {
                        if (expirationDateSinceConverted.length() < 1) {
                            filterExpirationDateSince = null;
                        } else {
                            Toast.makeText(context, resources.getString(R.string.Errors_wrong_data), Toast.LENGTH_LONG).show();
                        }
                        e.printStackTrace();
                    }
                    try {
                        filterExpirationDateFor = DATE_FORMAT.format(DATE_FORMAT.parse(expirationDateForConverted));
                        dateExpirationFor = DATE_FORMAT.parse(expirationDateForConverted);
                    } catch (ParseException e) {
                        if (expirationDateForConverted.length() < 1) {
                            filterExpirationDateFor = null;
                        } else {
                            Toast.makeText(context, resources.getString(R.string.Errors_wrong_data), Toast.LENGTH_LONG).show();
                        }
                        e.printStackTrace();
                    }
                    if (filterExpirationDateSince == null && filterExpirationDateFor == null) {
                        dialogListener.clearFilterExpirationDate();
                    } else {
                        try {
                            if (dateExpirationSince.compareTo(dateExpirationFor) == 0 || dateExpirationSince.compareTo(dateExpirationFor) < 0) {
                                dialogListener.setFilterExpirationDate(filterExpirationDateSince, filterExpirationDateFor);
                            } else {
                                Toast.makeText(context, resources.getString(R.string.Errors_wrong_data), Toast.LENGTH_LONG).show();
                            }
                        } catch (NullPointerException e) {
                            dialogListener.setFilterExpirationDate(filterExpirationDateSince, filterExpirationDateFor);
                            e.printStackTrace();
                        }

                    }
                });
        return builder.create();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            dialogListener = (DialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString());
        }
    }

    @Override
    public void onDateSet(@NonNull DatePicker view, int year, int month, int dayOfMonth) {
    }
}