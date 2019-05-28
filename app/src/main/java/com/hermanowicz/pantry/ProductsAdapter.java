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

package com.hermanowicz.pantry;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

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

    private List<Product> productList;
    private SharedPreferences myPreferences;
    private final OnItemClickListener listener;

    ProductsAdapter(OnItemClickListener listener, SharedPreferences myPreferences) {
        this.listener = listener;
        this.myPreferences = myPreferences;
        this.productList = new ArrayList<>();
    }

    public void setData(List<Product> newData){
        if (this.productList != null) {
            this.productList.clear();
            this.productList = newData;
        }
        else{
            this.productList = newData;
        }
        notifyDataSetChanged();
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ProductsAdapter.ViewHolder viewHolder, int position) {

        TextView nameTv = viewHolder.nameTv;
        TextView volumeTv = viewHolder.volumeTv;
        TextView weightTv = viewHolder.weightTv;
        TextView expirationDateTv = viewHolder.expirationDateTv;

        Context context = nameTv.getContext();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Resources resources = context.getResources();
        final Product selectedProduct = productList.get(position);
        Calendar calendar = Calendar.getInstance();
        Date expirationDateDt = calendar.getTime();
        String volumeString = resources.getString(R.string.ProductDetailsActivity_volume) + ": " +  selectedProduct.getVolume() + resources.getString(R.string.ProductDetailsActivity_volume_unit);
        String weightString = resources.getString(R.string.ProductDetailsActivity_weight) + ": " +  selectedProduct.getWeight() + resources.getString(R.string.ProductDetailsActivity_weight_unit);
        String expirationDateString = selectedProduct.getExpirationDate();
        String[] dateArray = expirationDateString.split("-");
        if (dateArray.length > 1)
            expirationDateString = dateArray[2] + "." + dateArray[1] + "." + dateArray[0];

        if (selectedProduct.getName().length() > 25)
            nameTv.setText(selectedProduct.getName().substring(0, 24) + "...");
        else
            nameTv.setText(selectedProduct.getName());
        volumeTv.setText(volumeString);
        weightTv.setText(weightString);
        expirationDateTv.setText(expirationDateString);

        try {
            expirationDateDt = simpleDateFormat.parse(selectedProduct.getExpirationDate());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        calendar.add(Calendar.DAY_OF_MONTH, myPreferences.getInt(
                PREFERENCES_DAYS_TO_NOTIFICATIONS, Notification.NOTIFICATION_DEFAULT_DAYS));
        calendar.add(Calendar.DAY_OF_MONTH, 3);
        Date dayOfNotification = calendar.getTime();
        if (dayOfNotification.after(expirationDateDt))
            viewHolder.itemView.setBackgroundColor(resources.getColor(R.color.background_expired_products));

        viewHolder.itemView.setOnClickListener(v -> listener.onItemClick(selectedProduct));

        Animation animation = AnimationUtils.loadAnimation(context, android.R.anim.slide_in_left);
        viewHolder.itemView.setAnimation(animation);
    }

    public interface OnItemClickListener {
        void onItemClick(Product product);
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

    public class ViewHolder extends RecyclerView.ViewHolder {

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

            nameTv = itemView.findViewById(R.id.text_productName);
            volumeTv = itemView.findViewById(R.id.text_productVolume);
            weightTv = itemView.findViewById(R.id.text_productWeight);
            expirationDateTv = itemView.findViewById(R.id.text_expirationDateValue);
        }

        public void bind(final Product product, final OnItemClickListener listener) {
        }
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }
}