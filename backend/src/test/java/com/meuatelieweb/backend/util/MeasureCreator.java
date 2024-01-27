package com.meuatelieweb.backend.util;

import com.meuatelieweb.backend.domain.measure.Measure;
import com.meuatelieweb.backend.domain.measure.dto.MeasureDTO;
import com.meuatelieweb.backend.domain.measure.dto.SaveUpdateMeasureDTO;

import java.util.UUID;

public class MeasureCreator {

    public static Measure createValidMeasure() {
        return Measure.builder()
                .id(UUID.randomUUID())
                .name("Cintura")
                .isActive(true)
                .build();
    }

    public static MeasureDTO createValidMeasureDTO(UUID id) {
        return MeasureDTO.builder()
                .id(id)
                .name("Cintura")
                .isActive(true)
                .build();
    }

    public static SaveUpdateMeasureDTO createValidSaveUpdateMeasureDTO() {
        return SaveUpdateMeasureDTO.builder()
                .name("Cintura")
                .build();
    }
}
