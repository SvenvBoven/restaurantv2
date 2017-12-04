package com.example.dingen.myapplication;

/**
 * Created by Sven on 4/12/2017.
 */

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.widget.ResourceCursorAdapter;
import android.widget.TextView;


public class RestoAdapter extends ResourceCursorAdapter {
    public RestoAdapter(Context context, Cursor cursor){
        super(context , R.layout.row_oder, cursor);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView count = view.findViewById(R.id.tvCount);
        TextView name = view.findViewById(R.id.tvname);
        name.setText(cursor.getString(cursor.getColumnIndex(cursor.getColumnName(1))));
        count.setText(cursor.getString(cursor.getColumnIndex(cursor.getColumnName(3))));
    }
}
