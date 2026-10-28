package com.sante.app.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.sante.app.R;
import com.sante.app.activity.ConseilsActivity;
import com.sante.app.activity.DoctorListActivity;
import com.sante.app.activity.MapActivity;
import com.sante.app.activity.SensorActivity;
import com.sante.app.adapter.ConseilAdapter;
import com.sante.app.adapter.DoctorAdapter;
import com.sante.app.model.Conseil;
import com.sante.app.model.Doctor;
import com.sante.app.viewmodel.ConseilsViewModel;
import com.sante.app.viewmodel.DoctorViewModel;

import java.util.ArrayList;
import java.util.List;

/** Accueil : accès rapides + aperçu de 3 médecins et 2 conseils (données observées via ViewModel). */
public class HomeFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.findViewById(R.id.cardDoctors).setOnClickListener(v ->
                startActivity(new Intent(requireContext(), DoctorListActivity.class)));
        view.findViewById(R.id.cardMap).setOnClickListener(v ->
                startActivity(new Intent(requireContext(), MapActivity.class)));
        view.findViewById(R.id.cardConseils).setOnClickListener(v ->
                startActivity(new Intent(requireContext(), ConseilsActivity.class)));
        view.findViewById(R.id.cardCapteurs).setOnClickListener(v ->
                startActivity(new Intent(requireContext(), SensorActivity.class)));

        // Médecins populaires (3 premiers)
        RecyclerView recyclerDoctors = view.findViewById(R.id.recyclerViewDoctors);
        DoctorAdapter doctorAdapter = new DoctorAdapter(requireContext(), new ArrayList<>(), null);
        recyclerDoctors.setAdapter(doctorAdapter);

        DoctorViewModel doctorVm = new ViewModelProvider(requireActivity()).get(DoctorViewModel.class);
        doctorVm.getAllDoctors().observe(getViewLifecycleOwner(), (List<Doctor> doctors) -> {
            if (doctors == null) return;
            doctorAdapter.updateList(doctors.size() > 3 ? new ArrayList<>(doctors.subList(0, 3)) : doctors);
        });

        // Conseils (2 premiers)
        RecyclerView recyclerConseils = view.findViewById(R.id.recyclerViewConseils);
        ConseilAdapter conseilAdapter = new ConseilAdapter(requireContext(), new ArrayList<>());
        recyclerConseils.setAdapter(conseilAdapter);

        ConseilsViewModel conseilsVm = new ViewModelProvider(requireActivity()).get(ConseilsViewModel.class);
        conseilsVm.getConseils().observe(getViewLifecycleOwner(), (List<Conseil> conseils) -> {
            if (conseils == null) return;
            conseilAdapter.updateList(conseils.size() > 2 ? new ArrayList<>(conseils.subList(0, 2)) : conseils);
        });
        conseilsVm.refresh();
    }
}
