package com.meuatelieweb.backend.domain.adjust;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import static com.meuatelieweb.backend.domain.adjust.AdjustCreator.createValidAdjust;
import static org.junit.jupiter.api.Assertions.assertFalse;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("Tests for Adjust Repository")
class AdjustRepositoryTest {

    @Autowired
    private AdjustRepository adjustRepository;

    @Test
    @DisplayName("inactivateAdjustById inactivates adjust when successful")
    void inactivateAdjustById_InactivatesAdjust_WhenSuccessful() {
        Adjust savedAdjust = this.adjustRepository.save(createValidAdjust());

        adjustRepository.inactivateAdjustById(savedAdjust.getId());

        Adjust inactivatedAdjust = adjustRepository.findById(savedAdjust.getId()).get();

        assertFalse(inactivatedAdjust.getIsActive());
    }
}
