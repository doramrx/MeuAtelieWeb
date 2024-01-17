package com.meuatelieweb.backend.domain.adjust;

import com.meuatelieweb.backend.domain.adjust.dto.AdjustDTO;
import com.meuatelieweb.backend.domain.adjust.dto.SaveUpdateAdjustDTO;

import java.util.UUID;

public class AdjustCreator {

    public static Adjust createValidAdjust() {
        return Adjust.builder()
                .id(UUID.randomUUID())
                .name("Ajuste de Cintura")
                .cost(13.90)
                .isActive(true)
                .build();
    }

    public static AdjustDTO createValidAdjustDTO(UUID id) {
        return AdjustDTO.builder()
                .id(id)
                .name("Ajuste de Cintura")
                .cost(13.90)
                .isActive(true)
                .build();
    }

    public static SaveUpdateAdjustDTO createValidSaveUpdateAdjustDTO() {
        return SaveUpdateAdjustDTO.builder()
                .name("Ajuste de Cintura")
                .cost(13.90)
                .build();
    }
}
