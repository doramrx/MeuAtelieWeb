package com.meuatelieweb.backend.domain.measure;

import com.meuatelieweb.backend.domain.measure.dto.MeasureDTO;
import com.meuatelieweb.backend.domain.measure.dto.SaveUpdateMeasureDTO;
import jakarta.persistence.EntityNotFoundException;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class MeasureService {

    @Autowired
    private MeasureRepository repository;

    @Autowired
    private MeasureConverter converter;

    public Page<MeasureDTO> findAll(Pageable pageable, String name, Boolean isActive) {
        Specification<Measure> specification = MeasureSpecification.applyFilter(name, isActive);

        return repository.findAll(specification, pageable).map(converter::toMeasureDTO);
    }

    public MeasureDTO findById(UUID id) {
        return repository.findById(id)
                .map(converter::toMeasureDTO)
                .orElseThrow(() -> new EntityNotFoundException("The given measure does not exist"));
    }

    @Transactional
    public MeasureDTO addMeasure(
            @NonNull
            SaveUpdateMeasureDTO saveMeasureDTO
    ) {

        this.validateSavingMeasureDTO(saveMeasureDTO);

        Measure measure = Measure.builder().name(saveMeasureDTO.getName()).build();

        return converter.toMeasureDTO(repository.save(measure));
    }

    private void validateSavingMeasureDTO(SaveUpdateMeasureDTO saveMeasureDTO) {
        if (saveMeasureDTO.getName() == null) {
            throw new IllegalArgumentException("The given name cannot be empty");
        }

        if (repository.existsByName(saveMeasureDTO.getName())) {
            throw new DuplicateKeyException("The given measure name already exists");
        }
    }

    @Transactional
    public MeasureDTO updateMeasure(
            @NonNull
            UUID id,
            @NonNull
            SaveUpdateMeasureDTO updateMeasureDTO
    ) {
        Measure measure = repository.findByIdAndIsActiveTrue(id)
                .orElseThrow(() -> new EntityNotFoundException("The given measure does not exist or is already inactive"));

        if (updateMeasureDTO.getName() != null) {
            if (repository.existsByNameAndIdNot(updateMeasureDTO.getName(), measure.getId())) {
                throw new DuplicateKeyException("The given measure name is already being used");
            }
            measure.setName(updateMeasureDTO.getName());
        }
        return converter.toMeasureDTO(repository.save(measure));
    }

    @Transactional
    public void deleteMeasure(UUID id) {
        if (!repository.existsByIdAndIsActiveTrue(id)) {
            throw new EntityNotFoundException("The given measure does not exist or is already inactive");
        }
        repository.inactivateMeasureById(id);
    }
}
