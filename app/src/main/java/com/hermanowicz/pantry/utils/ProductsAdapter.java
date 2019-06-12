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

package com.hermanowicz.pantry.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hermanowicz.pantry.R;
import com.hermanowicz.pantry.db.Product;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ProductsAdapter extends
        RecyclerView.Adapter<ProductsAdapter.ViewHolder> {

    private static final String PREFERENCES_DAYS_TO_NOTIFICATIONS = "HOW_MANY_DAYS_BEFORE_EXPIRATION_DATE_SEND_A_NOTIFICATION?";

    private List<Product> productList = new ArrayList<>();
    private List<Product> multiSelectList = new ArrayList<>();
    private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private SharedPreferences preferences;

    public ProductsAdapter(SharedPreferences preferences) {
        this.preferences = preferences;
    }

    public void setData(List<Product> newData){
        this.productList = newData;
        notifyDataSetChanged();
    }

    public void setMultiSelectList(List<Product> multiSelectList){
        this.multiSelectList = multiSelectList;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {

        TextView nameTv = viewHolder.nameTv;
        TextView volumeTv = viewHolder.volumeTv;
        TextView weightTv = viewHolder.weightTv;
        TextView expirationDateTv = viewHolder.expirationDateTv;

        Context context = nameTv.getContext();
        Resources resources = context.getResources();
        final Product selectedProduct = productList.get(position);
        Calendar calendar = Calendar.getInstance();
        Date expirationDateDt = calendar.getTime();
        String volumeString = String.format("%s: %s%s", resources.getString(R.string.ProductDetailsActivity_volume), selectedProduct.getVolume(), resources.getString(R.string.ProductDetailsActivity_volume_unit));
        String weightString = String.format("%s: %s%s", resources.getString(R.string.ProductDetailsActivity_weight), selectedProduct.getWeight(), resources.getString(R.string.ProductDetailsActivity_weight_unit));

        nameTv.setText(selectedProduct.getShortName());
        volumeTv.setText(volumeString);
        weightTv.setText(weightString);
        if(selectedProduct.getExpirationDate().length() > 1) {
            DateHelper dateHelper = new DateHelper(selectedProduct.getExpirationDate());
            expirationDateTv.setText(dateHelper.getDateInLocalFormat());
        }
        else{
            expirationDateTv.setText(selectedProduct.getExpirationDate());
        }

        try {
            expirationDateDt = simpleDateFormat.parse(selectedProduct.getExpirationDate());
        } catch (ParseException e) {
            Log.e("ProductsAdapter", e.toString());
        }
        calendar.add(Calendar.DAY_OF_MONTH, preferences.getInt(
                PREFERENCES_DAYS_TO_NOTIFICATIONS, Notification.NOTIFICATION_DEFAULT_DAYS));
        Date dayOfNotification = calendar.getTime();
        if (multiSelectList.contains(productList.get(position))) {
            viewHolder.itemView.setBackgroundColor(resources.getColor(R.color.background_product_selected));
        }
        else{
            if (dayOfNotification.after(expirationDateDt)){
                viewHolder.itemView.setBackgroundColor(resources.getColor(R.color.background_expired_products));
            }
            else{
                viewHolder.itemView.setBackgroundColor(resources.getColor(R.color.background_material));
            }
        }
    }

    @NonNull
    @Override
    public ProductsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View productView = inflater.inflate(R.layout.recycler_view_products, parent, false);
        ViewHolder holder = new ViewHolder(productView);

        return holder;
    }

   class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.text_productName)
        TextView nameTv;
        @BindView(R.id.text_productVolume)
        TextView volumeTv;
        @BindView(R.id.text_productWeight)
        TextView weightTv;
        @BindView(R.id.text_expirationDateValue)
        TextView expirationDateTv;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }
}