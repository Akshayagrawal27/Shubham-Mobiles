package com.shubhammobiles.shubhammobiles.notelist;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.Query;
import com.shubhammobiles.shubhammobiles.R;
import com.shubhammobiles.shubhammobiles.model.NoteList;
import com.shubhammobiles.shubhammobiles.util.Constants;
import com.shubhammobiles.shubhammobiles.util.Utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;

/**
 * Created by Akshay on 11-03-2018.
 */

class NoteListRecyclerAdapter extends FirebaseRecyclerAdapter<NoteList, NoteListRecyclerAdapter.NoteListRecyclerViewHolder> {

    static private NoteListClickListener mOnClickListener;

    /**
     * @param modelClass      Firebase will marshall the data at a location into
     *                        an instance of a class that you provide
     * @param modelLayout     This is the layout used to represent a single item in the list.
     *                        You will be responsible for populating an instance of the corresponding
     *                        view with the data from an instance of modelClass.
     * @param viewHolderClass The class that hold references to all sub-views in an instance modelLayout.
     * @param ref             The Firebase location to watch for data changes. Can also be a slice of a location,
     *                        using some combination of {@code limit()}, {@code startAt()}, and {@code endAt()}.
     */
    public NoteListRecyclerAdapter(Class<NoteList> modelClass,
                                   int modelLayout,
                                   Class<NoteListRecyclerViewHolder> viewHolderClass, Query ref,
                                   NoteListClickListener mOnClickListener) {
        super(modelClass, modelLayout, viewHolderClass, ref);
        this.mOnClickListener = mOnClickListener;
    }

    @Override
    protected void populateViewHolder(NoteListRecyclerViewHolder viewHolder, NoteList noteList, int position) {

        viewHolder.tvNoteDate.setText(Utils.getDateToShowFromFirebase(noteList.getNoteDate()));

        HashMap<String, Object> timestampLastChanged = noteList.getTimeStampLastChanged();
        Long timestamp = (Long)timestampLastChanged.get(Constants.FIREBASE_PROPERTY_TIMESTAMP);

        try{
            Calendar calendar = Calendar.getInstance();
            TimeZone tz = TimeZone.getTimeZone(Constants.COUNTRY_TIMEZONE);
            calendar.setTimeInMillis(timestamp);
            calendar.add(Calendar.MILLISECOND, tz.getOffset(calendar.getTimeInMillis()));
            SimpleDateFormat sdf = new SimpleDateFormat(Constants.DATE_FORMAT);
            Date currentTimeZone = (Date) calendar.getTime();
            viewHolder.tvLastUpdated.setText(sdf.format(currentTimeZone));
        }catch (Exception e) {

        }

        /*if (noteList.getNotePadText() != null){
            viewHolder.tvNotePreview.setText(noteList.getNotePadText());
        }*/
    }

    public interface NoteListClickListener {
        void onNoteListItemClick(int clickedItemIndex);
    }

    static public class NoteListRecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView tvNoteDate, tvLastUpdated, tvNotePreview;
        public NoteListRecyclerViewHolder(View itemView) {
            super(itemView);

            tvNoteDate = (TextView) itemView.findViewById(R.id.tv_note_date);
            tvLastUpdated = (TextView) itemView.findViewById(R.id.tv_note_last_updated);
            tvNotePreview = (TextView) itemView.findViewById(R.id.tv_note_preview);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            mOnClickListener.onNoteListItemClick(getAdapterPosition());
        }
    }
}
