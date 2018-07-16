package com.example.sandip.expensetracker;

import java.util.Date;

/**
 * Created by Sandip on 4/27/2018.
 */

public class TransactionDB {
    public String type;
    public String date;
    public String category;
    public String method;
    public String description;
    public Integer amount;
    public String key;

    public TransactionDB() {

    }

    public TransactionDB(String type, String date, String category, String method, String description, Integer amount, String key) {
        this.type = type;
        this.date = date;
        this.category = category;
        this.method = method;
        this.description = description;
        this.amount = amount;
        this.key = key;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }



    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}