package com.meuatelieweb.backend.domain.measure;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import static com.meuatelieweb.backend.domain.measure.MeasureCreator.createValidMeasure;
import static org.junit.jupiter.api.Assertions.assertFalse;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("Tests for Measure Repository")
class MeasureRepositoryTest {

    @Autowired
    private MeasureRepository measureRepository;

    @Test
    @DisplayName("inactivateMeasureById inactivates measure when successful")
    void inactivateMeasureById_InactivatesMeasure_WhenSuccessful() {
        Measure savedMeasure = this.measureRepository.save(createValidMeasure());

        measureRepository.inactivateMeasureById(savedMeasure.getId());

        Measure inactivatedMeasure = measureRepository.findById(savedMeasure.getId()).get();

        assertFalse(inactivatedMeasure.getIsActive());
    }
}
