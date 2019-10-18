package com.storedata.com.notepad;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.storedata.com.R;

import java.util.ArrayList;

/**
 * Created by ${3embed} on ${27-10-2017}.
 * Banglore
 */

public class NotepadAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<NotepadItemPojo> notepadItemPojos;
    private NotePadItemClick itemClick;

    public NotepadAdapter(ArrayList<NotepadItemPojo> notepadItemPojos, NotePadItemClick itemClick) {
        this.notepadItemPojos = notepadItemPojos;
        this.itemClick = itemClick;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.notepad_item, parent, false);
        viewHolder = new NotepadItemViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position) {
        NotepadItemViewHolder notepadItemViewHolder = (NotepadItemViewHolder) holder;
        NotepadItemPojo notepadItemPojo = notepadItemPojos.get(position);
        notepadItemViewHolder.title.setText(notepadItemPojo.getTitle());
        notepadItemViewHolder.date.setText(notepadItemPojo.getDate());

        ((NotepadItemViewHolder) holder).date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                itemClick.itemClick(((NotepadItemViewHolder) holder).date, position, 0);

            }
        });

        ((NotepadItemViewHolder) holder).title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                itemClick.itemClick(((NotepadItemViewHolder) holder).title, position, 1);

            }
        });

    }

    @Override
    public int getItemCount() {
        return notepadItemPojos.size();
    }

    public void setFilter(ArrayList<NotepadItemPojo> filterdNames) {
        this.notepadItemPojos = filterdNames;
        notifyDataSetChanged();
    }
}
