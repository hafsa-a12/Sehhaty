package com.sante.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sante.app.R;
import com.sante.app.model.Note;

import java.util.List;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.ViewHolder> {

    private List<Note> notes;
    private Context context;
    private OnNoteActionListener listener;

    public interface OnNoteActionListener {
        void onEditClick(Note note);
        void onDeleteClick(Note note, int position);
    }

    public NoteAdapter(Context context, List<Note> notes,
                       OnNoteActionListener listener) {
        this.context  = context;
        this.notes    = notes;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_note, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Note n = notes.get(position);
        holder.tvTitre.setText(n.getTitre());
        holder.tvContenu.setText(n.getContenu());
        holder.tvDate.setText(n.getDate());
        holder.tvTag.setText(n.getTag() != null ? "#" + n.getTag() : "");

        holder.btnEdit.setOnClickListener(v -> listener.onEditClick(n));
        holder.btnDelete.setOnClickListener(v ->
                listener.onDeleteClick(n, holder.getAdapterPosition()));
    }

    @Override
    public int getItemCount() { return notes.size(); }

    public void removeItem(int position) {
        notes.remove(position);
        notifyItemRemoved(position);
    }

    public void updateList(List<Note> newList) {
        this.notes = newList;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitre, tvContenu, tvDate, tvTag;
        ImageButton btnEdit, btnDelete;

        ViewHolder(View itemView) {
            super(itemView);
            tvTitre   = itemView.findViewById(R.id.tvTitre);
            tvContenu = itemView.findViewById(R.id.tvContenu);
            tvDate    = itemView.findViewById(R.id.tvDate);
            tvTag     = itemView.findViewById(R.id.tvTag);
            btnEdit   = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}