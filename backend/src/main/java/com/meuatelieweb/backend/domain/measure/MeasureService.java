package com.meuatelieweb.backend.domain.measure;

import com.meuatelieweb.backend.domain.measure.dto.MeasureDTO;
import com.meuatelieweb.backend.domain.measure.dto.SaveUpdateMeasureDTO;
import jakarta.persistence.EntityNotFoundException;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;
import java.util.Set;
import java.util.UUID;

@Service
public class MeasureService {

    @Autowired
    private MeasureRepository repository;

    @Autowired
    private MessageSource messageSource;

    public Page<Measure> findAll(Pageable pageable, String name, Boolean isActive) {
        Specification<Measure> specification = MeasureSpecification.applyFilter(name, isActive);

        return repository.findAll(specification, pageable);
    }

    public Measure findById(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        this.getMessage("measure.error.doesNotExist")
                ));
    }

    @Transactional
    public Measure addMeasure(
            @NonNull
            SaveUpdateMeasureDTO saveMeasureDTO
    ) {

        this.validateSavingMeasureDTO(saveMeasureDTO);

        Measure measure = Measure.builder().name(saveMeasureDTO.getName()).build();

        return repository.save(measure);
    }

    private void validateSavingMeasureDTO(SaveUpdateMeasureDTO saveMeasureDTO) {
        if (saveMeasureDTO.getName() == null) {
            throw new IllegalArgumentException(
                    this.getMessage("shared.error.emptyName")
            );
        }

        if (repository.existsByName(saveMeasureDTO.getName())) {
            throw new DuplicateKeyException(
                    this.getMessage("measure.error.nameAlreadyInUse")
            );
        }
    }

    @Transactional
    public Measure updateMeasure(
            @NonNull
            UUID id,
            @NonNull
            SaveUpdateMeasureDTO updateMeasureDTO
    ) {
        Measure measure = repository.findByIdAndIsActiveTrue(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        this.getMessage("measure.error.measureDoesNotExistOrIsInactive")
                ));

        if (updateMeasureDTO.getName() != null) {
            if (repository.existsByNameAndIdNot(updateMeasureDTO.getName(), measure.getId())) {
                throw new DuplicateKeyException(
                        this.getMessage("measure.error.nameAlreadyInUse")
                );
            }
            measure.setName(updateMeasureDTO.getName());
        }
        return repository.save(measure);
    }

    @Transactional
    public void deleteMeasure(UUID id) {
        if (!repository.existsByIdAndIsActiveTrue(id)) {
            throw new EntityNotFoundException(
                    this.getMessage("measure.error.measureDoesNotExistOrIsInactive")
            );
        }
        repository.inactivateMeasureById(id);
    }

    public Set<Measure> getMeasures(@NonNull Set<UUID> measuresIds) {
        Set<Measure> measures = repository.findByIdInAndIsActiveTrue(measuresIds)
                .orElseThrow(() -> new EntityNotFoundException(
                        this.getMessage("measure.error.measureDoesNotExistOrIsInactive")
                ));

        if (measures.size() != measuresIds.size()) {
            throw new IllegalArgumentException(
                    this.getMessage("measure.error.invalidIds")
            );
        }

        return measures;
    }

    private String getMessage(String key) {
        return messageSource.getMessage(key, null, Locale.getDefault());
    }
}
