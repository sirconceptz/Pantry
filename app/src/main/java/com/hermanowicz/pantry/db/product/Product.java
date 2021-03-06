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

package com.hermanowicz.pantry.db.product;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

/**
 * <h1>Product/h1>
 * Product model. Is needed to support database and used to store a product data.
 *
 * @author  Mateusz Hermanowicz
 * @version 1.0
 * @since   1.0
 */

@Entity(tableName = "products")
public class Product implements Serializable {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String name;
    private String hashCode;
    private String typeOfProduct;
    private String productFeatures;
    private String storageLocation;
    private String expirationDate;
    private String productionDate;
    private String composition;
    private String healingProperties;
    private String dosage;
    private int volume;
    private int weight;
    private boolean hasSugar;
    private boolean hasSalt;
    private boolean isVege;
    private boolean isBio;
    private String taste;
    private String photoName;
    private String photoDescription;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getShortName(){
        if(name.length()>18)
            return name.substring(0, 17) + "...";
        else
            return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHashCode() {
        return hashCode;
    }

    public void setHashCode(String hashCode) {
        this.hashCode = hashCode;
    }

    public String getTypeOfProduct() {
        return typeOfProduct;
    }

    public void setTypeOfProduct(String typeOfProduct) {
        this.typeOfProduct = typeOfProduct;
    }

    public String getProductFeatures() {
        return productFeatures;
    }

    public void setProductFeatures(String productFeatures) {
        this.productFeatures = productFeatures;
    }

    public String getStorageLocation() {
        return storageLocation;
    }

    public void setStorageLocation(String storageLocation) {
        this.storageLocation = storageLocation;
    }

    public String getExpirationDate() {
        return expirationDate;
    }
    public void setExpirationDate(String expirationDate) {
        this.expirationDate = expirationDate;
    }

    public String getProductionDate() {
        return productionDate;
    }

    public void setProductionDate(String productionDate) {
        this.productionDate = productionDate;
    }

    public String getComposition() {
        return composition;
    }

    public void setComposition(String composition) {
        this.composition = composition;
    }

    public String getHealingProperties() {
        return healingProperties;
    }

    public void setHealingProperties(String healingProperties) {
        this.healingProperties = healingProperties;
    }

    public String getDosage() {
        return dosage;
    }

    public void setDosage(String dosage) {
        this.dosage = dosage;
    }

    public int getVolume() {
        return volume;
    }

    public void setVolume(int volume) {
        this.volume = volume;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public boolean getHasSugar() {
        return hasSugar;
    }

    public void setHasSugar(boolean hasSugar) {
        this.hasSugar = hasSugar;
    }

    public boolean getHasSalt() {
        return hasSalt;
    }

    public void setHasSalt(boolean hasSalt) {
        this.hasSalt = hasSalt;
    }

    public boolean getIsVege() {
        return isVege;
    }

    public void setIsVege(boolean vege) {
        isVege = vege;
    }

    public boolean getIsBio() {
        return isBio;
    }

    public void setIsBio(boolean bio) {
        isBio = bio;
    }

    public String getTaste() {
        return taste;
    }

    public void setTaste(String taste) {
        this.taste = taste;
    }

    public String getPhotoName() {
        return photoName;
    }

    public void setPhotoName(String photoName) {
        this.photoName = photoName;
    }

    public String getPhotoDescription() {
        return photoDescription;
    }

    public void setPhotoDescription(String photoDescription) {
        this.photoDescription = photoDescription;
    }
}