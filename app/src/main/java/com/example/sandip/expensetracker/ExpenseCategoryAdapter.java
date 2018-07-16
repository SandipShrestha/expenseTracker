package com.example.sandip.expensetracker;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Sandip on 4/26/2018.
 */

public class ExpenseCategoryAdapter extends ArrayAdapter<ExpenseCategoryDB> {
    private Activity context;
    private List<ExpenseCategoryDB>ecatList;

    public ExpenseCategoryAdapter(Activity context,List<ExpenseCategoryDB>expenseCategoryDB){
        super(context,R.layout.custom_listview,expenseCategoryDB);
        this.context=context;
        this.ecatList=expenseCategoryDB;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();

        View listViewItem = inflater.inflate(R.layout.custom_listview,null,true);

        TextView category = (TextView) listViewItem.findViewById(R.id.textViewCustom);

        ExpenseCategoryDB expenseCategoryDB = ecatList.get(position);

        category.setText(expenseCategoryDB.getExpenseCategory());

        return  listViewItem;
    }
}
