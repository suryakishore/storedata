package com.storedata.com.notepad;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.storedata.com.R;

/**
 * Created by ${3embed} on ${27-10-2017}.
 * Banglore
 */

public class NotepadItemViewHolder extends RecyclerView.ViewHolder{
    public TextView title,date;
    public LinearLayout notePadItem;
    public NotepadItemViewHolder(View itemView) {
        super(itemView);
        title=itemView.findViewById(R.id.title);
        date=itemView.findViewById(R.id.date);
        notePadItem=itemView.findViewById(R.id.notePadItem);

    }
}
