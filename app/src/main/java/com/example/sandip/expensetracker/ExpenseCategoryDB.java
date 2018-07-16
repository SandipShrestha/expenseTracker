package com.example.sandip.expensetracker;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Sandip on 4/25/2018.
 */

public class ExpenseCategoryDB {
    public String expenseCategory;
    public String key;

    public ExpenseCategoryDB(){

    }
    public ExpenseCategoryDB(String expenseCategory,String key){
        this.expenseCategory = expenseCategory;
        this.key=key;

    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getExpenseCategory() {
        return expenseCategory;
    }

    public void setExpenseCategory(String expenseCategory) {
        this.expenseCategory = expenseCategory;
    }

   /* public Map<String,Object>toMap(){
        HashMap<String,Object> result = new HashMap<>();
        result.put("ExpenseCat", expenseCategory);
        return result;


    }*/
}
