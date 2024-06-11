package roomescape.service.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.LocalDate;

public record PopularThemeRequest(
        @NotNull
        @JsonFormat(shape = Shape.STRING, pattern = "yyyy-MM-dd")
        LocalDate startDate,

        @NotNull
        @JsonFormat(shape = Shape.STRING, pattern = "yyyy-MM-dd")
        LocalDate endDate,

        @NotNull
        @Positive
        Integer limit
) {
}
