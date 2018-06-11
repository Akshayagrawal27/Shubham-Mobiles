package com.shubhammobiles.shubhammobiles.notelist;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.shubhammobiles.shubhammobiles.BaseActivity;
import com.shubhammobiles.shubhammobiles.R;
import com.shubhammobiles.shubhammobiles.Sharing.ShareActivity;
import com.shubhammobiles.shubhammobiles.model.NoteList;
import com.shubhammobiles.shubhammobiles.util.Constants;
import com.shubhammobiles.shubhammobiles.util.FirebaseUtil;
import com.shubhammobiles.shubhammobiles.util.Utils;

public class NoteListActivity extends BaseActivity implements NoteListRecyclerAdapter.NoteListClickListener {

    private DatabaseReference noteListReference;
    private Query noteListQuery;

    private RecyclerView mRecyclerNoteList;
    private NoteListRecyclerAdapter mNoteListRecyclerAdapter;
    private String accountName, accountKey;
    private boolean mOwner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_list);

        Intent intent = this.getIntent();
        accountName = intent.getStringExtra(Constants.KEY_ACCOUNT_NAME);
        setTitle(accountName);
        accountKey = intent.getStringExtra(Constants.KEY_ACCOUNT_KEY);
        mOwner = intent.getBooleanExtra(Constants.KEY_AM_I_OWNER, false);

        noteListQuery = FirebaseUtil.getNoteListReference().child(accountKey).orderByChild(Constants.FIREBASE_PROPERTY_NOTE_DATE);
        noteListReference = FirebaseUtil.getNoteListReference().child(accountKey);

        mRecyclerNoteList = (RecyclerView) findViewById(R.id.recycler_note_list);

        LinearLayoutManager layoutManager =
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, true);

        mRecyclerNoteList.setLayoutManager(layoutManager);

        mNoteListRecyclerAdapter = new NoteListRecyclerAdapter(NoteList.class,
                R.layout.list_note,
                NoteListRecyclerAdapter.NoteListRecyclerViewHolder.class,
                noteListQuery, this);

        mRecyclerNoteList.setAdapter(mNoteListRecyclerAdapter);

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            // Called when a user swipes left on a ViewHolder
            @Override
            public void onSwiped(final RecyclerView.ViewHolder viewHolder, int swipeDir) {
                if (swipeDir == ItemTouchHelper.LEFT) {
                    String noteListId = mNoteListRecyclerAdapter.getRef(viewHolder.getLayoutPosition()).getKey();
                    buildAlert(viewHolder, noteListId);
                }
            }
        }).attachToRecyclerView(mRecyclerNoteList);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_brand_model, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_share_brand){
            showSearchUserDialog();
            return true;
        }
        return false;
    }

    public void showAddNoteListDialog(View view) {
        Intent intent = new Intent(this, Notepad.class);
        intent.setAction(Constants.ADD_NEW_NOTE);
        intent.putExtra(Constants.KEY_ACCOUNT_KEY, accountKey);
        startActivity(intent);
    }

    @Override
    public void onNoteListItemClick(int clickedItemIndex) {
        Intent intent = new Intent(this, Notepad.class);
        intent.setAction(Constants.EDIT_NOTE);
        intent.putExtra(Constants.KEY_ACCOUNT_KEY, accountKey);
        intent.putExtra(Constants.KEY_NOTE_KEY, mNoteListRecyclerAdapter.getRef(clickedItemIndex).getKey());
        startActivity(intent);
    }

    private void removeItem(String noteListId) {
        noteListReference.child(noteListId).removeValue();
    }

    public void buildAlert(final RecyclerView.ViewHolder viewHolder, final String noteListId) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this, R.style.CustomTheme_Dialog)
                .setTitle(R.string.alert_remove_note)
                .setMessage(R.string.alert_are_u_sure_remove_note)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        removeItem(noteListId);
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        mNoteListRecyclerAdapter.notifyItemChanged(viewHolder.getAdapterPosition());
                        dialog.dismiss();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert);

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void showSearchUserDialog() {
        if (mOwner){
            Intent intent = new Intent(this, ShareActivity.class);
            intent.setAction(Constants.SHARE_ACCOUNTS);
            intent.putExtra(Constants.KEY_ACCOUNT_KEY, accountKey);
            intent.putExtra(Constants.KEY_ACCOUNT_NAME, accountName);
            startActivity(intent);
        }else{
            Utils.showToast(this, "Please Contact owner of this list to share");
        }
    }
}
