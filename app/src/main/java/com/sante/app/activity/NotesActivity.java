package com.sante.app.activity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.sante.app.R;
import com.sante.app.adapter.NoteAdapter;
import com.sante.app.model.Note;
import com.sante.app.viewmodel.NotesViewModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/** Notes médicales : CRUD complet, la liste se met à jour toute seule (Room + LiveData). */
public class NotesActivity extends AppCompatActivity implements NoteAdapter.OnNoteActionListener {

    private static final List<String> TAGS = Arrays.asList(
            "general", "tension", "poids", "symptome", "medicament", "allergie", "sport");

    private NoteAdapter adapter;
    private NotesViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);

        viewModel = new ViewModelProvider(this).get(NotesViewModel.class);

        RecyclerView recycler = findViewById(R.id.recyclerNotes);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        adapter = new NoteAdapter(this, new ArrayList<>(), this);
        recycler.setAdapter(adapter);

        viewModel.getNotes().observe(this, notes -> {
            if (notes != null) adapter.updateList(notes);
        });

        FloatingActionButton fab = findViewById(R.id.fabAjouterNote);
        fab.setOnClickListener(v -> afficherDialogueNote(null));
    }

    private void afficherDialogueNote(Note noteExistante) {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_note, null);
        EditText etTitre = dialogView.findViewById(R.id.etTitre);
        EditText etContenu = dialogView.findViewById(R.id.etContenu);
        Spinner spinTag = dialogView.findViewById(R.id.spinTag);

        spinTag.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, TAGS));

        if (noteExistante != null) {
            etTitre.setText(noteExistante.getTitre());
            etContenu.setText(noteExistante.getContenu());
            int idx = TAGS.indexOf(noteExistante.getTag());
            if (idx >= 0) spinTag.setSelection(idx);
        }

        new AlertDialog.Builder(this)
                .setTitle(noteExistante == null ? "Nouvelle note" : "Modifier la note")
                .setView(dialogView)
                .setPositiveButton("Sauvegarder", (dialog, which) -> {
                    String titre = etTitre.getText().toString().trim();
                    String contenu = etContenu.getText().toString().trim();
                    String tag = spinTag.getSelectedItem().toString();

                    if (titre.isEmpty()) {
                        Toast.makeText(this, "Le titre est requis", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (noteExistante == null) {
                        String date = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                                .format(new Date());
                        viewModel.add(new Note(titre, contenu, date, tag));
                        Toast.makeText(this, "Note ajoutée", Toast.LENGTH_SHORT).show();
                    } else {
                        noteExistante.setTitre(titre);
                        noteExistante.setContenu(contenu);
                        noteExistante.setTag(tag);
                        viewModel.update(noteExistante);
                        Toast.makeText(this, "Note mise à jour", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Annuler", null)
                .show();
    }

    @Override
    public void onEditClick(Note note) {
        afficherDialogueNote(note);
    }

    @Override
    public void onDeleteClick(Note note, int position) {
        new AlertDialog.Builder(this)
                .setTitle("Supprimer la note")
                .setMessage("Confirmer la suppression de « " + note.getTitre() + " » ?")
                .setPositiveButton("Supprimer", (d, w) -> {
                    viewModel.delete(note);
                    Toast.makeText(this, "Note supprimée", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Annuler", null)
                .show();
    }
}
