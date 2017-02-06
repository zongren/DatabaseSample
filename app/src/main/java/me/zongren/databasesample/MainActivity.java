package me.zongren.databasesample;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import me.zongren.databasesample.PersonContract.PersonEntry;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int REQUEST_CODE_PERSON_ADD = 0;
    private static final int REQUEST_CODE_PERSON_EDIT = 1;

    private ListView mListView;
    private PersonOpenHelper mPersonOpenHelper;
    private PersonAdapter mPersonAdapter;
    private SQLiteDatabase mPersonDatabase;
    private AlertDialog mAlertDialog;
    private AlertDialog mDeleteDialog;
    private Button mDeleteButton;
    private int mPressedPosition;
    private SearchView mSearchView;
    private MenuItem mSearchItem;

    @Override
    public void onClick(View view) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        mSearchItem = menu.findItem(R.id.activity_main_search);
        mSearchView = (SearchView) mSearchItem.getActionView();
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                mSearchItem.collapseActionView();
                searchPerson(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                searchPerson(query);
                return true;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.activity_main_add:
                openPersonActivity();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CODE_PERSON_ADD:
                if (resultCode == RESULT_OK && data != null && data.getExtras() != null) {
                    onGetPerson(data.getStringExtra(PersonActivity.KEY_NAME),
                            data.getStringExtra(PersonActivity.KEY_AGE));
                }
                break;
            case REQUEST_CODE_PERSON_EDIT:
                if (resultCode == RESULT_OK && data != null && data.getExtras() != null) {
                    updatePerson(data.getStringExtra(PersonActivity.KEY_NAME),
                            data.getStringExtra(PersonActivity.KEY_AGE));
                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mListView = (ListView) findViewById(R.id.activity_main_listView);

        View customDialogView = LayoutInflater.from(this).inflate(R.layout.activity_main_alert,
                null);
        mAlertDialog = new AlertDialog.Builder(MainActivity.this).create();
        mAlertDialog.setView(customDialogView, 0, 0, 0, 0);
        mAlertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        Button deleteButton = (Button) customDialogView.findViewById(
                R.id.activity_main_alert_deleteButton);
        if (deleteButton != null) {
            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mAlertDialog.dismiss();
                    mDeleteDialog.show();
                }
            });
        }

        Button editButton = (Button) customDialogView.findViewById(
                R.id.activity_main_alert_editButton);
        if (editButton != null) {
            editButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mAlertDialog.dismiss();
                    editPerson();
                }
            });
        }

        mDeleteDialog = new AlertDialog.Builder(this).setTitle("Are you sure?").setPositiveButton(
                "Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mDeleteDialog.dismiss();
                        MainActivity.this.deletePerson();
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mDeleteDialog.dismiss();
            }
        }).create();

        mPersonOpenHelper = new PersonOpenHelper(this);
        mPersonDatabase = mPersonOpenHelper.getReadableDatabase();
        mPersonAdapter = new PersonAdapter(this, null);
        mListView.setAdapter(mPersonAdapter);

        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int i, long l) {
                mPressedPosition = i;
                mAlertDialog.show();
                return true;
            }
        });

        readDatabase();
    }

    @Override
    protected void onDestroy() {
        mPersonDatabase.close();
        mPersonAdapter.getCursor().close();
        super.onDestroy();
    }

    private void deletePerson() {
        mPersonAdapter.getCursor().moveToPosition(mPressedPosition);
        long id = mPersonAdapter.getCursor().getLong(
                mPersonAdapter.getCursor().getColumnIndex(PersonEntry._ID));

        String selection = PersonEntry._ID + " = ?";
        String[] selectionArgs = {id + ""};
        mPersonDatabase.delete(PersonEntry.TABLE_NAME, selection, selectionArgs);
        readDatabase();
    }

    private void editPerson() {
        Intent intent = new Intent(this, PersonActivity.class);
        Cursor cursor = mPersonAdapter.getCursor();
        cursor.moveToPosition(mPressedPosition);
        String name = cursor.getString(cursor.getColumnIndex(PersonEntry.COLUMN_NAME_NAME));
        String age = cursor.getString(cursor.getColumnIndex(PersonEntry.COLUMN_NAME_AGE));
        intent.putExtra(PersonActivity.KEY_NAME, name);
        intent.putExtra(PersonActivity.KEY_AGE, age);
        startActivityForResult(intent, REQUEST_CODE_PERSON_EDIT);
    }

    private void onGetPerson(String name, String age) {
        savePerson(name, age);
        readDatabase();
    }

    private void openPersonActivity() {
        Intent intent = new Intent(this, PersonActivity.class);
        startActivityForResult(intent, REQUEST_CODE_PERSON_ADD);
    }

    private void readDatabase() {
        String[] projection = {PersonEntry._ID, PersonEntry.COLUMN_NAME_NAME, PersonEntry.COLUMN_NAME_AGE};
        String sortOrder = PersonEntry._ID + " ASC";

        Cursor cursor = mPersonDatabase.query(PersonEntry.TABLE_NAME,
                // The table to query
                projection,                               // The columns to return
                null,                                // The columns for the WHERE clause
                null,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                sortOrder                                 // The sort order
        );

        mPersonAdapter.changeCursor(cursor);
    }

    private void savePerson(String name, String age) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(PersonEntry.COLUMN_NAME_AGE, age);
        contentValues.put(PersonEntry.COLUMN_NAME_NAME, name);
        mPersonDatabase.insert(PersonEntry.TABLE_NAME, null, contentValues);
        readDatabase();
    }

    private void searchPerson(String query) {
        String[] projection = {PersonEntry._ID, PersonEntry.COLUMN_NAME_NAME, PersonEntry.COLUMN_NAME_AGE};
        String selection;
        String[] arguments;
        if (TextUtils.isEmpty(query)) {
            selection = " 1 = 1";
            arguments = new String[]{};
        } else {
            selection = PersonEntry.COLUMN_NAME_NAME + " LIKE ?";
            arguments = new String[]{"%" + query + "%"};
        }
        String order = PersonEntry._ID + " ASC";
        Cursor cursor = mPersonDatabase.query(PersonEntry.TABLE_NAME, projection, selection,
                arguments, null, null, order);
        mPersonAdapter.changeCursor(cursor);

    }

    private void updatePerson(String name, String age) {
        Cursor cursor = mPersonAdapter.getCursor();
        cursor.moveToPosition(mPressedPosition);
        long id = cursor.getLong(cursor.getColumnIndex(PersonEntry._ID));

        String selection = PersonEntry._ID + " = ?";

        String[] selectionArgs = {id + ""};
        ContentValues contentValues = new ContentValues();
        contentValues.put(PersonEntry.COLUMN_NAME_NAME, name);
        contentValues.put(PersonEntry.COLUMN_NAME_AGE, age);
        mPersonDatabase.update(PersonEntry.TABLE_NAME, contentValues, selection, selectionArgs);
        readDatabase();
    }
}
