package me.zongren.databasesample;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by 宗仁 on 2017/2/6.
 * All Rights Reserved By 秦皇岛商之翼网络科技有限公司.
 */

public class PersonAdapter extends CursorAdapter {
    public PersonAdapter(Context context, Cursor cursor) {
        super(context, cursor, false);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.activity_main_item_person, parent, false);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView textView = (TextView) view;
        long id = cursor.getLong(cursor.getColumnIndexOrThrow(PersonContract.PersonEntry._ID));
        String name = cursor.getString(
                cursor.getColumnIndexOrThrow(PersonContract.PersonEntry.COLUMN_NAME_NAME));
        String age = cursor.getString(
                cursor.getColumnIndexOrThrow(PersonContract.PersonEntry.COLUMN_NAME_AGE));
        textView.setText("Person{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", age='" + age + '\'' +
                '}');
    }

}
