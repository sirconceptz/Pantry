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

package com.hermanowicz.pantry.presenters;

import androidx.lifecycle.LiveData;

import com.hermanowicz.pantry.db.Product;
import com.hermanowicz.pantry.filter.Filter;
import com.hermanowicz.pantry.filter.FilterModel;
import com.hermanowicz.pantry.interfaces.MyPantryView;
import com.hermanowicz.pantry.models.MyPantryModel;
import com.hermanowicz.pantry.utils.PrintQRData;

import java.util.ArrayList;
import java.util.List;

public class MyPantryPresenter {

    private MyPantryView view;
    private MyPantryModel model = new MyPantryModel();

    public MyPantryPresenter(MyPantryView view) {
        this.view = view;
    }

    public void setProductList(List<Product> productList) {
        if (productList.size() == 0)
            view.showEmptyPantryStatement(true);
        else
            view.showEmptyPantryStatement(false);
        model.setProductList(productList);
    }

    public List<Product> getProductList() {
        return model.getProductList();
    }

    public void clearSelectList() {
        model.clearSelectList();
    }

    public List<Product> getSelectList() {
        return model.getSelectProductList();
    }

    public void setIsMultiSelect(boolean state) {
        model.setIsMultiSelect(state);
    }

    public boolean getIsMultiSelect() {
        return model.getIsMultiSelect();
    }

    public void addMultiSelectProduct(int position) {
        model.addMultiSelect(position);
        view.updateSelectsRecyclerViewAdapter();
    }

    public void deleteSelectedProducts() {
        List<Product> productList = model.getSelectProductList();
        view.onDeleteProducts(productList);
        clearFilters();
    }

    public void printSelectedProducts() {
        ArrayList<String> textToQRCodeList, namesOfProductsList, expirationDatesList;
        List<Product> productList = model.getSelectProductList();

        textToQRCodeList = PrintQRData.getTextToQRCodeList(productList);
        namesOfProductsList = PrintQRData.getNamesOfProductsList(productList);
        expirationDatesList = PrintQRData.getExpirationDatesList(productList);

        view.onPrintProducts(textToQRCodeList, namesOfProductsList, expirationDatesList);
    }

    public void clearFilters() {
        model.clearFilters();
        view.clearFilterIcons();
        model.setProductLiveData(view.getProductLiveData());
        view.updateProductsRecyclerViewAdapter();
    }

    public void setProductLiveData(LiveData<List<Product>> productLiveData) {
        model.setProductLiveData(productLiveData);
    }

    public LiveData<List<Product>> getProductLiveData() {
        LiveData<List<Product>> productLiveData = model.getProductLiveData();
        return productLiveData;
    }

    public FilterModel getFilterProduct() {
        FilterModel filterModel = model.getFilterProduct();
        return filterModel;
    }

    public void setFilterName(String filterName) {
        if(filterName == null) { //For disabled filter
            view.clearFilterIcon(1);
        } else {
            view.setFilterIcon(1);
        }
        model.filterProductListByName(filterName);
        view.updateProductsRecyclerViewAdapter();
    }

    public void setFilterExpirationDate(String filterExpirationDateSince, String filterExpirationDateFor) {
        if(filterExpirationDateSince == null && filterExpirationDateFor == null){ //For disabled filter
            view.clearFilterIcon(2);
        } else {
            view.setFilterIcon(2);
        }
        model.filterProductListByExpirationDate(filterExpirationDateSince, filterExpirationDateFor);
        view.updateProductsRecyclerViewAdapter();
    }

    public void setFilterProductionDate(String filterProductionDateSince, String filterProductionDateFor) {
        if(filterProductionDateSince == null && filterProductionDateFor == null){ //For disabled filter
            view.clearFilterIcon(3);
        } else {
            view.setFilterIcon(3);
        }
        model.filterProductListByProductionDate(filterProductionDateSince, filterProductionDateFor);
        view.updateProductsRecyclerViewAdapter();
    }

    public void setFilterTypeOfProduct(String filterTypeOfProduct, String filterProductFeatures) {
        if(filterTypeOfProduct == null && filterProductFeatures == null) { //For disabled filter
            view.clearFilterIcon(4);
        } else {
            view.setFilterIcon(4);
        }
        model.filterProductListByTypeOfProduct(filterTypeOfProduct, filterProductFeatures);
        view.updateProductsRecyclerViewAdapter();
    }

    public void setFilterVolume(int filterVolumeSince, int filterVolumeFor) {
        if(filterVolumeSince == -1 && filterVolumeFor == -1) { //For disabled filter
            view.clearFilterIcon(5);
        } else {
            view.setFilterIcon(5);
        }
        model.filterProductListByVolume(filterVolumeSince, filterVolumeFor);
        view.updateProductsRecyclerViewAdapter();
    }

    public void setFilterWeight(int filterWeightSince, int filterWeightFor) {
        if(filterWeightSince == -1 && filterWeightFor == -1){ //For disabled filter
            view.clearFilterIcon(6);
        } else {
            view.setFilterIcon(6);
        }
        model.filterProductListByWeight(filterWeightSince, filterWeightFor);
        view.updateProductsRecyclerViewAdapter();
    }

    public void setFilterTaste(String filterTaste) {
        if(filterTaste == null){ //For disabled filter
            view.clearFilterIcon(7);
        } else {
            view.setFilterIcon(7);
        }

        model.filterProductListByTaste(filterTaste);
        view.updateProductsRecyclerViewAdapter();
    }

    public void setProductFeatures(Filter.Set filterHasSugar, Filter.Set filterHasSalt) {
        if(filterHasSugar == Filter.Set.DISABLED && filterHasSalt == Filter.Set.DISABLED){  //For disabled filter
            view.clearFilterIcon(8);
        } else {
            view.setFilterIcon(8);
        }
        model.filterProductListBySugarAndSalt(filterHasSugar, filterHasSalt);
        view.updateProductsRecyclerViewAdapter();
    }

    public void navigateToMainActivity() {
        view.navigateToMainActivity();
    }

    public void openDialog(int typeOfDialog) {
        view.openFilterDialog(typeOfDialog);
    }
}