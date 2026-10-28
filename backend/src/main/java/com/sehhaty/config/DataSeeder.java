package com.sehhaty.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sehhaty.dto.ConseilDto;
import com.sehhaty.dto.DoctorDto;
import com.sehhaty.model.Conseil;
import com.sehhaty.model.Doctor;
import com.sehhaty.repository.ConseilRepository;
import com.sehhaty.repository.DoctorRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.List;

/** Au premier démarrage (tables vides), charge les médecins et conseils depuis resources/seed. */
@Component
public class DataSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);

    private final DoctorRepository doctors;
    private final ConseilRepository conseils;
    private final ObjectMapper mapper;

    public DataSeeder(DoctorRepository doctors, ConseilRepository conseils, ObjectMapper mapper) {
        this.doctors = doctors;
        this.conseils = conseils;
        this.mapper = mapper;
    }

    @Override
    public void run(String... args) throws Exception {
        if (doctors.count() == 0) {
            List<DoctorDto> list = read("seed/doctors.json", new TypeReference<>() {});
            doctors.saveAll(list.stream().map(d -> new Doctor(d.nom(), d.specialite(), d.telephone(),
                    d.adresse(), d.latitude(), d.longitude(), d.disponible(), d.note())).toList());
            log.info("{} médecins importés", list.size());
        }
        if (conseils.count() == 0) {
            List<ConseilDto> list = read("seed/conseils.json", new TypeReference<>() {});
            conseils.saveAll(list.stream().map(c -> new Conseil(c.titre(), c.contenu(),
                    c.categorie(), c.icone())).toList());
            log.info("{} conseils importés", list.size());
        }
    }

    private <T> T read(String path, TypeReference<T> type) throws Exception {
        try (InputStream in = new ClassPathResource(path).getInputStream()) {
            return mapper.readValue(in, type);
        }
    }
}
