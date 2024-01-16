package com.meuatelieweb.backend.controllers;

import com.meuatelieweb.backend.domain.measure.MeasureService;
import com.meuatelieweb.backend.domain.measure.dto.MeasureDTO;
import com.meuatelieweb.backend.domain.measure.dto.SaveUpdateMeasureDTO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("measures")
public class MeasureController {

    @Autowired
    private MeasureService service;

    @GetMapping
    public ResponseEntity<Page<MeasureDTO>> findAll(
            Pageable pageable,
            @RequestParam(name = "name", required = false)
            String name,
            @RequestParam(name = "isActive", required = false)
            Boolean isActive
    ) {
        return ResponseEntity.ok().body(service.findAll(pageable, name, isActive));
    }

    @GetMapping("/{id}")
    public ResponseEntity<MeasureDTO> findById(@PathVariable UUID id) {
        MeasureDTO measureDTO = service.findById(id);

        return ResponseEntity.ok().body(measureDTO);
    }

    @PostMapping
    public ResponseEntity<MeasureDTO> addMeasure(
            @RequestBody @Valid SaveUpdateMeasureDTO saveMeasureDTO,
            UriComponentsBuilder uriComponentsBuilder
    ) {
        MeasureDTO savedMeasure = service.addMeasure(saveMeasureDTO);

        URI uri = uriComponentsBuilder
                .path("/measures/{id}")
                .buildAndExpand(savedMeasure.getId())
                .toUri();

        return ResponseEntity.created(uri).body(savedMeasure);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MeasureDTO> updateMeasure(
            @PathVariable UUID id,
            @RequestBody
            @Valid
            SaveUpdateMeasureDTO updateMeasureDTO
    ) {
        MeasureDTO updatedMeasure = service.updateMeasure(id, updateMeasureDTO);

        return ResponseEntity.ok().body(updatedMeasure);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMeasure(@PathVariable UUID id) {
        service.deleteMeasure(id);

        return ResponseEntity.noContent().build();
    }
}
