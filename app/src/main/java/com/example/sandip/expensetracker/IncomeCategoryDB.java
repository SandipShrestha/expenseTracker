package com.example.sandip.expensetracker;

/**
 * Created by Sandip on 4/27/2018.
 */

public class IncomeCategoryDB {

    public String incomeCategory;
    public String key;

    public IncomeCategoryDB(){

    }
    public IncomeCategoryDB(String incomeCategory,String key){
        this.incomeCategory = incomeCategory;
        this.key=key;

    }

    public String getIncomeCategory() {
        return incomeCategory;
    }

    public void setIncomeCategory(String incomeCategory) {
        this.incomeCategory = incomeCategory;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}

