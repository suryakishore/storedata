package com.storedata.com.checklist;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.storedata.com.R;

/**
 * Created by ${3embed} on ${27-10-2017}.
 * Banglore
 */

public class CheckLIstItemViewHolder extends RecyclerView.ViewHolder{
    public EditText addtoDo;
    public ImageView crossTodo;
    public CheckLIstItemViewHolder(View itemView) {
        super(itemView);
        addtoDo=itemView.findViewById(R.id.addtoDo);
        crossTodo=itemView.findViewById(R.id.crossTodo);

    }
}
