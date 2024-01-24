package com.meuatelieweb.backend.domain.adjust;

import com.meuatelieweb.backend.domain.adjust.dto.SaveUpdateAdjustDTO;
import jakarta.persistence.EntityNotFoundException;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.UUID;

@Service
public class AdjustService {

    @Autowired
    private AdjustRepository repository;

    public Page<Adjust> findAll(Pageable pageable, String name, Boolean isActive) {
        Specification<Adjust> specification = AdjustSpecification.applyFilter(name, isActive);

        return repository.findAll(specification, pageable);
    }

    public Adjust findById(@NonNull UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("The given adjust does not exist"));
    }

    @Transactional
    public Adjust addAdjust(
            @NonNull
            SaveUpdateAdjustDTO saveAdjustDTO
    ) {

        this.validateSavingAdjustDTO(saveAdjustDTO);

        Adjust adjust = Adjust.builder()
                .name(saveAdjustDTO.getName())
                .cost(saveAdjustDTO.getCost())
                .build();

        return repository.save(adjust);
    }

    private void validateSavingAdjustDTO(SaveUpdateAdjustDTO saveAdjustDTO) {

        this.validateAdjustName(saveAdjustDTO.getName());

        if (repository.existsByName(saveAdjustDTO.getName())) {
            throw new DuplicateKeyException("The given adjust name already exists");
        }

        this.validateAdjustCostValue(saveAdjustDTO.getCost());
    }

    private void validateAdjustName(String name) {
        if (name == null) {
            throw new IllegalArgumentException("The given name cannot be empty");
        }
    }

    private void validateAdjustCostValue(Double cost) {
        if (cost == null) {
            throw new IllegalArgumentException("The given cost cannot be empty");
        }

        if (cost < 0.01) {
            throw new IllegalArgumentException("The given cost cannot be lesser than 0.01");
        }
    }

    @Transactional
    public Adjust updateAdjust(
            @NonNull
            UUID id,
            @NonNull
            SaveUpdateAdjustDTO updateAdjustDTO
    ) {
        Adjust adjust = repository.findByIdAndIsActiveTrue(id)
                .orElseThrow(() -> new EntityNotFoundException("The given adjust does not exist or is already inactive"));

        this.validateAdjustName(updateAdjustDTO.getName());

        if (repository.existsByNameAndIdNot(updateAdjustDTO.getName(), adjust.getId())) {
            throw new DuplicateKeyException("The given adjust name is already being used");
        }

        adjust.setName(updateAdjustDTO.getName());

        this.validateAdjustCostValue(updateAdjustDTO.getCost());

        adjust.setCost(updateAdjustDTO.getCost());

        return repository.save(adjust);
    }

    @Transactional
    public void deleteAdjust(UUID id) {
        if (!repository.existsByIdAndIsActiveTrue(id)) {
            throw new EntityNotFoundException("The given adjust does not exist or is already inactive");
        }
        repository.inactivateAdjustById(id);
    }


    public Set<Adjust> getAdjusts(Set<UUID> adjustsIds) {
        Set<Adjust> adjusts = repository.findByIdInAndIsActiveTrue(adjustsIds);

        if (adjusts.size() != adjustsIds.size()) {
            throw new IllegalArgumentException("Some of the given id adjusts are invalid");
        }

        return adjusts;
    }
}
