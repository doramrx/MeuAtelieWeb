package com.meuatelieweb.backend.domain.adjust;

import com.meuatelieweb.backend.domain.adjust.dto.AdjustDTO;
import lombok.NonNull;
import org.springframework.stereotype.Component;

@Component
public class AdjustConverter {

    public AdjustDTO toAdjustDTO(@NonNull Adjust adjust) {
        return new AdjustDTO()
                .builder()
                .id(adjust.getId())
                .name(adjust.getName())
                .cost(adjust.getCost())
                .isActive(adjust.getIsActive())
                .build();
    }
}
