package com.meuatelieweb.backend.domain.adjust;

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
@DisplayName("Tests for Adjust Specification")
class AdjustSpecificationTest {

    @Autowired
    private AdjustRepository adjustRepository;

    @InjectMocks
    private AdjustSpecification adjustSpecification;

    private List<Adjust> adjusts;

    @BeforeEach
    public void setUpDatabase() {
        this.adjusts = List.of(
                Adjust.builder()
                        .id(UUID.randomUUID())
                        .name("Ajustes Cintura")
                        .cost(13.90)
                        .isActive(true)
                        .build(),
                Adjust.builder()
                        .id(UUID.randomUUID())
                        .name("Ajustes Quadril")
                        .cost(13.90)
                        .isActive(true)
                        .build(),
                Adjust.builder()
                        .id(UUID.randomUUID())
                        .name("Remendos")
                        .cost(13.90)
                        .isActive(false)
                        .build()
        );

        this.adjusts.forEach(adjustRepository::save);
    }

    @Test
    @DisplayName("applyFilter returns page of adjusts filtered by name when successful")
    void applyFilter_ReturnsPageOfAdjustsFilteredByName_WhenSuccessful() {

        Specification<Adjust> specification = AdjustSpecification
                .applyFilter("Ajustes", null);

        PageImpl<Adjust> page = new PageImpl<>(this.adjusts, Pageable.ofSize(20), 20);

        List<Adjust> result = adjustRepository.findAll(specification, page.getPageable()).toList();

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(2, result.size());
        assertTrue(result.contains(this.adjusts.get(0)));
        assertTrue(result.contains(this.adjusts.get(1)));
    }

    @Test
    @DisplayName("applyFilter returns page of adjusts filtered by isActive when successful")
    void applyFilter_ReturnsPageOfAdjustsFilteredByIsActive_WhenSuccessful() {

        Specification<Adjust> specification = AdjustSpecification
                .applyFilter(null, false);

        PageImpl<Adjust> page = new PageImpl<>(this.adjusts, Pageable.ofSize(20), 20);

        List<Adjust> result = adjustRepository.findAll(specification, page.getPageable()).toList();

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertTrue(result.contains(this.adjusts.get(2)));
    }
}
