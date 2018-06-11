package com.shubhammobiles.shubhammobiles.notelist;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.shubhammobiles.shubhammobiles.R;
import com.shubhammobiles.shubhammobiles.model.NoteList;
import com.shubhammobiles.shubhammobiles.util.Constants;
import com.shubhammobiles.shubhammobiles.util.FirebaseUtil;
import com.shubhammobiles.shubhammobiles.util.Utils;

import java.util.Calendar;
import java.util.HashMap;

public class Notepad extends AppCompatActivity {

    private DatabaseReference noteListReference;

    private EditText etNotepad;
    private TextView tvNoteDate;

    private DatePickerDialog datePickerDialog;

    private String accountKey, action, noteKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notepad);

        Intent intent = this.getIntent();
        action = intent.getAction();
        accountKey = intent.getStringExtra(Constants.KEY_ACCOUNT_KEY);

        noteListReference = FirebaseUtil.getNoteListReference();

        etNotepad =(EditText) findViewById(R.id.et_notepad);
        tvNoteDate = (TextView) findViewById(R.id.tv_notepad_date);

        if (action.equals(Constants.ADD_NEW_NOTE)){
            tvNoteDate.setText(Utils.getCurrentDate());
        }else{
            noteKey = intent.getStringExtra(Constants.KEY_NOTE_KEY);
            noteListReference.child(accountKey).child(noteKey).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    NoteList noteList = dataSnapshot.getValue(NoteList.class);
                    tvNoteDate.setText(Utils.getDateToShowFromFirebase(noteList.getNoteDate()));
                    etNotepad.setText(noteList.getNotePadText());
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

        tvNoteDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePicker(tvNoteDate);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_notepad, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.action_save){
            saveNote();
            return true;
        }
        return false;
    }

    private void saveNote() {

        String noteDate = Utils.getDateToSave(tvNoteDate.getText().toString());
        String notePadText = etNotepad.getText().toString();
        String owner = FirebaseUtil.getUserPhoneNumber();
        HashMap<String, Object> timestampLastChanged = new HashMap<String, Object>();
        timestampLastChanged.put(Constants.FIREBASE_PROPERTY_TIMESTAMP, ServerValue.TIMESTAMP);

        if (action.equals(Constants.ADD_NEW_NOTE)){
            noteListReference.child(accountKey).child(noteListReference.push().getKey())
                    .setValue(new NoteList(noteDate, notePadText, owner, timestampLastChanged))
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                Utils.showToast(getBaseContext(), "Note Saved");
                            }
                        }
                    });
        }else {
            noteListReference.child(accountKey).child(noteKey)
                    .setValue(new NoteList(noteDate, notePadText, owner, timestampLastChanged))
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                Utils.showToast(getBaseContext(), "Note Updated");
                            }
                        }
                    });
        }
        finish();
    }

    private void showDatePicker(final TextView tvView) {
        final Calendar c = Calendar.getInstance();
        final int mYear = c.get(Calendar.YEAR); // current year
        final int mMonth = c.get(Calendar.MONTH); // current month
        final int mDay = c.get(Calendar.DAY_OF_MONTH); // current day
        // date picker dialog
        datePickerDialog = new DatePickerDialog(Notepad.this,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        // set day of month , month and year value in the edit text
                        tvView.setText(Utils.getDateToShow(year, monthOfYear, dayOfMonth));

                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }
}
