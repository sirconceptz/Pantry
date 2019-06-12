/*
 * Copyright (c) 2019
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

package com.hermanowicz.pantry.activities;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.hermanowicz.pantry.R;
import com.hermanowicz.pantry.db.Product;
import com.hermanowicz.pantry.db.ProductDb;
import com.hermanowicz.pantry.interfaces.NewProductView;
import com.hermanowicz.pantry.presenters.NewProductPresenter;
import com.hermanowicz.pantry.utils.DateHelper;
import com.hermanowicz.pantry.utils.Notification;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * <h1>NewProductActivity</h1>
 * Activity to add a new product. User can add a new product to the database.
 * In new product user can choose a type of product, tasteGroup, name, production and expiration dates,
 * composition, volume, weight and for specific products attributes like healing properties
 * or dosage. Product can have a sugar and a salt (checkbox). User can add more like 1 item after
 * giving quantity. After inserting a new product to database user will be asked (in different
 * activity) to print a QR code to scan in the future.
 *
 * @author  Mateusz Hermanowicz
 * @version 1.0
 * @since   1.0
 */
public class NewProductActivity extends AppCompatActivity implements OnItemSelectedListener, DatePickerDialog.OnDateSetListener, NewProductView {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.edittext_name)
    EditText name;
    @BindView(R.id.spinner_productType)
    Spinner productTypeSpinner;
    @BindView(R.id.spinner_productFeatures)
    Spinner productFeaturesSpinner;
    @BindView(R.id.edittext_expirationDate)
    EditText expirationDate;
    @BindView(R.id.edittext_productionDate)
    EditText productionDate;
    @BindView(R.id.edittext_quantity)
    EditText quantity;
    @BindView(R.id.edittext_composition)
    EditText composition;
    @BindView(R.id.edittext_healingProperties)
    EditText healingProperties;
    @BindView(R.id.edittext_dosage)
    EditText dosage;
    @BindView(R.id.edittext_volume)
    EditText volume;
    @BindView(R.id.edittext_weight)
    EditText weight;
    @BindView(R.id.checkbox_hasSugar)
    CheckBox hasSugar;
    @BindView(R.id.checkbox_hasSalt)
    CheckBox hasSalt;
    @BindView(R.id.radiogroup_taste)
    RadioGroup tasteGroup;
    @BindView(R.id.text_volumeLabel)
    TextView volumeLabel;
    @BindView(R.id.text_weightLabel)
    TextView weightLabel;
    @BindView(R.id.adBanner)
    AdView adView;

    private Context context;
    private Resources resources;
    private int day, month, year;
    private boolean isTypeOfProductTouched;
    private DatePickerDialog.OnDateSetListener productionDateListener, expirationDateListener;
    private ArrayAdapter<CharSequence> typeOfProductAdapter, productFeaturesAdapter;

    private NewProductPresenter presenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_product);

        ButterKnife.bind(this);

        init();

        presenter = new NewProductPresenter(this, resources, ProductDb.getInstance(context));

        typeOfProductAdapter = ArrayAdapter.createFromResource(context, R.array.ProductDetailsActivity_type_of_product_array, android.R.layout.simple_spinner_item);
        typeOfProductAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        productTypeSpinner.setAdapter(typeOfProductAdapter);

        productFeaturesAdapter = ArrayAdapter.createFromResource(context, R.array.ProductDetailsActivity_choose_array, android.R.layout.simple_spinner_item);
        productFeaturesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        productFeaturesSpinner.setAdapter(productFeaturesAdapter);

        expirationDate.setOnClickListener(v -> {
            if (expirationDate.length() < 1) {
                year = DateHelper.getActualYear();
                month = DateHelper.getActualMonth();
                day = DateHelper.getActualDay(1);
            } else {
                int[] expirationDateArray = presenter.getExpirationDateArray();
                year = expirationDateArray[0];
                month = expirationDateArray[1];
                day = expirationDateArray[2];
            }
            DatePickerDialog dialog = new DatePickerDialog(
                    context,
                    R.style.AppThemeDatePicker,
                    expirationDateListener,
                    year, month, day);
            dialog.getDatePicker().setMinDate(new Date().getTime());
            Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();
        });

        expirationDateListener = (datePicker, year, month, day) -> {
            presenter.showExpirationDate(day, month, year);
            presenter.setExpirationDate(year, month, day);
        };

        productionDate.setOnClickListener(v -> {
            if (productionDate.length() < 1) {
                year = DateHelper.getActualYear();
                month = DateHelper.getActualMonth();
                day = DateHelper.getActualDay(0);
            } else {
                int[] productionDateArray = presenter.getProductionDateArray();
                year = productionDateArray[0];
                month = productionDateArray[1];
                day = productionDateArray[2];
            }
            DatePickerDialog dialog = new DatePickerDialog(
                    context,
                    R.style.AppThemeDatePicker,
                    productionDateListener,
                    year, month, day);
            Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();
        });

        productionDateListener = (datePicker, year, month, day) -> {
            presenter.showProductionDate(day, month, year);
            presenter.setProductionDate(year, month, day);
        };

        productTypeSpinner.setOnTouchListener((v, event) -> {
            isTypeOfProductTouched = true;
            return false;
        });

        productTypeSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if(isTypeOfProductTouched) {
                    String typeOfProductValue = String.valueOf(productTypeSpinner.getSelectedItem());
                    presenter.updateProductFeaturesAdapter(typeOfProductValue);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    @OnClick(R.id.button_addProduct)
    void onClickAddProduct() {
        int selectedTasteId = tasteGroup.getCheckedRadioButtonId();
        RadioButton taste = findViewById(selectedTasteId);

        Product product = new Product();
        product.setName(name.getText().toString());
        product.setTypeOfProduct(String.valueOf(productTypeSpinner.getSelectedItem()));
        product.setProductFeatures(String.valueOf(productFeaturesSpinner.getSelectedItem()));
        product.setComposition(composition.getText().toString());
        product.setHealingProperties(healingProperties.getText().toString());
        product.setDosage(dosage.getText().toString());
        product.setVolume(Integer.parseInt(volume.getText().toString()));
        product.setWeight(Integer.parseInt(weight.getText().toString()));
        product.setHasSugar(hasSugar.isChecked());
        product.setHasSalt(hasSalt.isChecked());
        product.setTaste(String.valueOf(taste.getText()));
        presenter.setQuantity(quantity.getText().toString());
        presenter.addProducts(product);
    }

    @Override
    public void onItemSelected(@NonNull AdapterView<?> parent, @NonNull View view, int position, long id) {
    }

    @Override
    public void onNothingSelected(@NonNull AdapterView<?> parent) {
    }

    @Override
    public void onDateSet(@NonNull DatePicker view, int year, int month, int dayOfMonth) {
    }

    private void init(){
        context = NewProductActivity.this;
        resources = context.getResources();

        setSupportActionBar(toolbar);

        MobileAds.initialize(getApplicationContext(), getResources().getString(R.string.admob_ad_id));

        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

        volumeLabel.setText(String.format("%s (%s)", getString(R.string.ProductDetailsActivity_volume), getString(R.string.ProductDetailsActivity_volume_unit)));

        weightLabel.setText(String.format("%s (%s)", getString(R.string.ProductDetailsActivity_weight), getString(R.string.ProductDetailsActivity_weight_unit)));

        name.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        composition.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        healingProperties.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        dosage.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
    }

    @Override
    public boolean onKeyDown(int keyCode, @NonNull KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            presenter.navigateToMainActivity();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onResume() {
        super.onResume();
        adView.resume();
    }

    @Override
    public void onPause() {
        adView.pause();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        adView.destroy();
        super.onDestroy();
    }

    @Override
    public void navigateToPrintQRCodesActivity(ArrayList<String> textToQRCodeList, ArrayList<String> namesOfProductsList, ArrayList<String> expirationDatesList) {
        Intent printQRCodesActivityIntent = new Intent(context, PrintQRCodesActivity.class)
                .putStringArrayListExtra("text_to_qr_code", textToQRCodeList)
                .putStringArrayListExtra("expiration_dates", expirationDatesList)
                .putStringArrayListExtra("names_of_products", namesOfProductsList);

        startActivity(printQRCodesActivityIntent);
        finish();
    }

    @Override
    public void onProductsAdd(List<Product> products) {
        for (Product product : products) {
            Notification.createNotification(context, product);
        }
    }

    @Override
    public void updateProductFeaturesAdapter(String typeOfProductSpinnerValue) {
        String[] productTypesArray = resources.getStringArray(R.array.ProductDetailsActivity_type_of_product_array);
        if (typeOfProductSpinnerValue.equals(productTypesArray[0]))
            productFeaturesAdapter = ArrayAdapter.createFromResource(context, R.array.ProductDetailsActivity_choose_array, android.R.layout.simple_spinner_item);
        else if (typeOfProductSpinnerValue.equals(productTypesArray[1]))
            productFeaturesAdapter = ArrayAdapter.createFromResource(context, R.array.ProductDetailsActivity_store_products_array, android.R.layout.simple_spinner_item);
        else if (typeOfProductSpinnerValue.equals(productTypesArray[2]))
            productFeaturesAdapter = ArrayAdapter.createFromResource(context, R.array.ProductDetailsActivity_ready_meals_array, android.R.layout.simple_spinner_item);
        else if (typeOfProductSpinnerValue.equals(productTypesArray[3]))
            productFeaturesAdapter = ArrayAdapter.createFromResource(context, R.array.ProductDetailsActivity_vegetables_array, android.R.layout.simple_spinner_item);
        else if (typeOfProductSpinnerValue.equals(productTypesArray[4]))
            productFeaturesAdapter = ArrayAdapter.createFromResource(context, R.array.ProductDetailsActivity_fruits_array, android.R.layout.simple_spinner_item);
        else if (typeOfProductSpinnerValue.equals(productTypesArray[5]))
            productFeaturesAdapter = ArrayAdapter.createFromResource(context, R.array.ProductDetailsActivity_herbs_array, android.R.layout.simple_spinner_item);
        else if (typeOfProductSpinnerValue.equals(productTypesArray[6]))
            productFeaturesAdapter = ArrayAdapter.createFromResource(context, R.array.ProductDetailsActivity_liqueurs_array, android.R.layout.simple_spinner_item);
        else if (typeOfProductSpinnerValue.equals(productTypesArray[7]))
            productFeaturesAdapter = ArrayAdapter.createFromResource(context, R.array.ProductDetailsActivity_wines_type_array, android.R.layout.simple_spinner_item);
        else if (typeOfProductSpinnerValue.equals(productTypesArray[8]))
            productFeaturesAdapter = ArrayAdapter.createFromResource(context, R.array.ProductDetailsActivity_mushrooms_array, android.R.layout.simple_spinner_item);
        else if (typeOfProductSpinnerValue.equals(productTypesArray[9]))
            productFeaturesAdapter = ArrayAdapter.createFromResource(context, R.array.ProductDetailsActivity_vinegars_array, android.R.layout.simple_spinner_item);
        else if (typeOfProductSpinnerValue.equals(productTypesArray[10]))
            productFeaturesAdapter = ArrayAdapter.createFromResource(context, R.array.ProductDetailsActivity_chemical_products_array, android.R.layout.simple_spinner_item);
        else if (typeOfProductSpinnerValue.equals(productTypesArray[11]))
            productFeaturesAdapter = ArrayAdapter.createFromResource(context, R.array.ProductDetailsActivity_other_products_array, android.R.layout.simple_spinner_item);

        productFeaturesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        productFeaturesAdapter.notifyDataSetChanged();
        productFeaturesSpinner.setAdapter(productFeaturesAdapter);
    }

    @Override
    public void showStatementOnAreProductsAdded(String statementToShow) {
        Toast.makeText(context, statementToShow, Toast.LENGTH_LONG).show();
    }

    @Override
    public void showExpirationDate(String date) {
        expirationDate.setText(date);
    }

    @Override
    public void showProductionDate(String date) {
        productionDate.setText(date);
    }

    @Override
    public void showErrorNameNotSet() {
        name.setError(getString(R.string.Errors_product_name_is_required));
        Toast.makeText(context, getString(R.string.Errors_product_name_is_required), Toast.LENGTH_LONG).show();
    }

    @Override
    public void showErrorCategoryNotSelected() {
        Toast.makeText(context, getString(R.string.Errors_category_not_selected), Toast.LENGTH_LONG).show();
    }

    @Override
    public void navigateToMainActivity() {
        Intent mainActivityIntent = new Intent(context, MainActivity.class);
        startActivity(mainActivityIntent);
        finish();
    }
}