package com.example.sandip.expensetracker;

import android.app.Activity;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import static android.graphics.Color.GREEN;

/**
 * Created by Sandip on 4/28/2018.
 */

public class TransactionAdapter extends ArrayAdapter<TransactionDB> {
    private Activity context;
    private List<TransactionDB> transList;

    public TransactionAdapter(Activity context,List<TransactionDB> transList){
        super(context,R.layout.transaction_listview,transList);
        this.context=context;
        this.transList=transList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();

        View listViewItem = inflater.inflate(R.layout.transaction_listview,null,true);

        TextView date = (TextView) listViewItem.findViewById(R.id.textView_date);
        TextView amount = (TextView) listViewItem.findViewById(R.id.textView_amount);
        TextView category = (TextView) listViewItem.findViewById(R.id.textView_category);
        TextView method = (TextView) listViewItem.findViewById(R.id.textView_method);
        TextView description = (TextView) listViewItem.findViewById(R.id.textView_description);
        TextView type = (TextView) listViewItem.findViewById(R.id.textView_type);

        TransactionDB transactionDB = transList.get(position);

        date.setText(transactionDB.getDate());
        amount.setText("Rs " + transactionDB.getAmount());
        category.setText(transactionDB.getCategory());
        method.setText(transactionDB.getMethod());
        description.setText(transactionDB.getDescription());
        type.setText(transactionDB.getType());

        return  listViewItem;

    }


}
