package com.sehhaty.web;

import com.sehhaty.dto.ConseilDto;
import com.sehhaty.repository.ConseilRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/conseils")
public class ConseilController {

    private final ConseilRepository conseils;

    public ConseilController(ConseilRepository conseils) {
        this.conseils = conseils;
    }

    @GetMapping
    public List<ConseilDto> all() {
        return conseils.findAll().stream().map(ConseilDto::from).toList();
    }
}
