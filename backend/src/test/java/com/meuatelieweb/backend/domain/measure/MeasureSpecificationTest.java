package com.meuatelieweb.backend.domain.measure;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("Tests for Measure Specification")
class MeasureSpecificationTest {

    @Autowired
    private MeasureRepository measureRepository;

    @InjectMocks
    private MeasureSpecification measureSpecification;

    private List<Measure> measures;

    @BeforeEach
    public void setUpDatabase() {
        this.measures = List.of(
                Measure.builder()
                        .id(UUID.randomUUID())
                        .name("Medida Cintura")
                        .isActive(true)
                        .build(),
                Measure.builder()
                        .id(UUID.randomUUID())
                        .name("Medida Quadril")
                        .isActive(true)
                        .build(),
                Measure.builder()
                        .id(UUID.randomUUID())
                        .name("Comprimento Perna")
                        .isActive(false)
                        .build()
        );

        this.measures.forEach(measureRepository::save);
    }

    @Test
    @DisplayName("applyFilter returns page of measures filtered by name when successful")
    void applyFilter_ReturnsPageOfMeasuresFilteredByName_WhenSuccessful() {

        Specification<Measure> specification = MeasureSpecification
                .applyFilter("Medida", null);

        PageImpl<Measure> page = new PageImpl<>(this.measures, Pageable.ofSize(20), 20);

        List<Measure> result = measureRepository.findAll(specification, page.getPageable()).toList();

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(2, result.size());
        assertTrue(result.contains(this.measures.get(0)));
        assertTrue(result.contains(this.measures.get(1)));
    }

    @Test
    @DisplayName("applyFilter returns page of measures filtered by isActive when successful")
    void applyFilter_ReturnsPageOfMeasuresFilteredByIsActive_WhenSuccessful() {

        Specification<Measure> specification = MeasureSpecification
                .applyFilter(null, false);

        PageImpl<Measure> page = new PageImpl<>(this.measures, Pageable.ofSize(20), 20);

        List<Measure> result = measureRepository.findAll(specification, page.getPageable()).toList();

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertTrue(result.contains(this.measures.get(2)));
    }
}
