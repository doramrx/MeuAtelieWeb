package com.meuatelieweb.backend.controllers;

import com.meuatelieweb.backend.domain.adjust.AdjustService;
import com.meuatelieweb.backend.domain.adjust.dto.AdjustDTO;
import com.meuatelieweb.backend.domain.adjust.dto.SaveUpdateAdjustDTO;
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
@RequestMapping("adjusts")
public class AdjustController {

    @Autowired
    private AdjustService service;

    @GetMapping
    public ResponseEntity<Page<AdjustDTO>> findAll(
            Pageable pageable,
            @RequestParam(name = "name", required = false)
            String name,
            @RequestParam(name = "isActive", required = false)
            Boolean isActive
    ) {
        return ResponseEntity.ok().body(service.findAll(pageable, name, isActive));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AdjustDTO> findById(@PathVariable UUID id) {
        AdjustDTO adjustDTO = service.findById(id);

        return ResponseEntity.ok().body(adjustDTO);
    }

    @PostMapping
    public ResponseEntity<AdjustDTO> addAdjust(
            @RequestBody @Valid SaveUpdateAdjustDTO saveAdjustDTO,
            UriComponentsBuilder uriComponentsBuilder
    ) {
        AdjustDTO savedAdjust = service.addAdjust(saveAdjustDTO);

        URI uri = uriComponentsBuilder
                .path("/adjusts/{id}")
                .buildAndExpand(savedAdjust.getId())
                .toUri();

        return ResponseEntity.created(uri).body(savedAdjust);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AdjustDTO> updateAdjust(
            @PathVariable UUID id,
            @RequestBody
            @Valid
            SaveUpdateAdjustDTO updateAdjustDTO
    ) {
        AdjustDTO updatedAdjust = service.updateAdjust(id, updateAdjustDTO);

        return ResponseEntity.ok().body(updatedAdjust);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAdjust(@PathVariable UUID id) {
        service.deleteAdjust(id);

        return ResponseEntity.noContent().build();
    }
}
