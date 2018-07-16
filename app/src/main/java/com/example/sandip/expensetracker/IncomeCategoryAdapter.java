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
 * Created by Sandip on 4/27/2018.
 */


    public class IncomeCategoryAdapter extends ArrayAdapter<IncomeCategoryDB> {
        private Activity context;
        private List<IncomeCategoryDB>icatList;

        public IncomeCategoryAdapter(Activity context,List<IncomeCategoryDB>incomeCategoryDB){
            super(context,R.layout.custom_listview,incomeCategoryDB);
            this.context=context;
            this.icatList=incomeCategoryDB;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            LayoutInflater inflater = context.getLayoutInflater();

            View listViewItem = inflater.inflate(R.layout.custom_listview,null,true);

            TextView category = (TextView) listViewItem.findViewById(R.id.textViewCustom);

         IncomeCategoryDB incomeCategoryDB = icatList.get(position);

            category.setText(incomeCategoryDB.getIncomeCategory());

            return  listViewItem;
        }
    }

