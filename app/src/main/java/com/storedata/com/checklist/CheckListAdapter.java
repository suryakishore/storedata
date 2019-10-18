package com.storedata.com.checklist;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.storedata.com.R;
import com.storedata.com.notepad.NotePadItemClick;

import java.util.ArrayList;

/**
 * Created by ${3embed} on ${27-10-2017}.
 * Banglore
 */

public class CheckListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private ArrayList<String> checkList;
    private NotePadItemClick itemClick;


    public CheckListAdapter(ArrayList<String> checkList, NotePadItemClick itemClick) {
        this.checkList = checkList;
        this.itemClick = itemClick;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.checklist_item, parent, false);
        viewHolder = new CheckLIstItemViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position) {

        final CheckLIstItemViewHolder checkLIstItemViewHolder = (CheckLIstItemViewHolder) holder;
        checkLIstItemViewHolder.addtoDo.setText(checkList.get(position));
        checkLIstItemViewHolder.crossTodo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                itemClick.itemClick(null, position, 0);
            }
        });

        checkLIstItemViewHolder.addtoDo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                checkList.set(checkLIstItemViewHolder.getAdapterPosition(), s.toString());

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return checkList.size();
    }

}
