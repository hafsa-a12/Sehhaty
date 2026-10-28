package com.sante.app.activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sante.app.R;
import com.sante.app.adapter.ConseilAdapter;
import com.sante.app.viewmodel.ConseilsViewModel;

import java.util.ArrayList;

/** Conseils santé : API en priorité, JSON embarqué en secours (géré par le Repository). */
public class ConseilsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conseils);

        RecyclerView recycler = findViewById(R.id.recyclerConseils);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        ConseilAdapter adapter = new ConseilAdapter(this, new ArrayList<>());
        recycler.setAdapter(adapter);

        ConseilsViewModel vm = new ViewModelProvider(this).get(ConseilsViewModel.class);
        vm.getConseils().observe(this, conseils -> {
            if (conseils != null) adapter.updateList(conseils);
        });
        if (savedInstanceState == null) vm.refresh();
    }
}
