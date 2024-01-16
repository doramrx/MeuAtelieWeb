package com.meuatelieweb.backend.domain.measure;

import com.meuatelieweb.backend.domain.measure.dto.MeasureDTO;
import lombok.NonNull;
import org.springframework.stereotype.Component;

@Component
public class MeasureConverter {

    public MeasureDTO toMeasureDTO(@NonNull Measure measure) {
        return new MeasureDTO()
                .builder()
                .id(measure.getId())
                .name(measure.getName())
                .isActive(measure.getIsActive())
                .build();
    }
}
