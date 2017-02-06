package me.zongren.databasesample;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class PersonActivity extends AppCompatActivity {

    public static final String KEY_NAME = "me.zongren.databasesample.name";
    public static final String KEY_AGE = "me.zongren.databasesample.age";

    private EditText mNameEditText;
    private EditText mAgeEditText;
    private Button mCancelButton;
    private Button mConfirmButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person);

        mNameEditText = (EditText) findViewById(R.id.activity_person_nameEditText);
        mAgeEditText = (EditText) findViewById(R.id.activity_person_ageEditText);
        mCancelButton = (Button) findViewById(R.id.activity_person_cancelButton);

        mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        mConfirmButton = (Button) findViewById(R.id.activity_person_saveButton);
        mConfirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirm();
            }
        });

        if (getIntent() != null && getIntent().getExtras() != null) {
            String name = getIntent().getStringExtra(KEY_NAME);
            String age = getIntent().getStringExtra(KEY_AGE);
            mNameEditText.setText(name);
            mAgeEditText.setText(age);
        }
    }

    private void confirm() {
        String name = mNameEditText.getText().toString();
        String age = mAgeEditText.getText().toString();
        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(age)) {
            Toast.makeText(this, "Please enter name and age", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent();
        intent.putExtra(KEY_NAME, name);
        intent.putExtra(KEY_AGE, age);
        setResult(RESULT_OK, intent);
        finish();
    }
}
