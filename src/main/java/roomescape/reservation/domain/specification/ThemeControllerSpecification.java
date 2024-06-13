package roomescape.reservation.domain.specification;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

import roomescape.exception.ErrorType;
import roomescape.global.annotation.ApiErrorResponse;
import roomescape.reservation.controller.dto.ThemeRequest;
import roomescape.reservation.controller.dto.ThemeResponse;

import java.time.LocalDate;
import java.util.List;

public interface ThemeControllerSpecification {

    @ApiErrorResponse(value = ErrorType.INVALID_REQUEST_ERROR)
    ThemeResponse create(@Valid ThemeRequest themeRequest);

    List<ThemeResponse> findAll();

    @ApiResponse(description = "삭제를 성공했습니다.", responseCode = "204")
    @ApiErrorResponse(value = ErrorType.RESERVATION_NOT_DELETED)
    void delete(@Min(1) long themeId);

    List<ThemeResponse> findPopular(LocalDate startDate,
                                    LocalDate endDate,
                                    @Min(value = 1) @Max(value = 20) int limit);
}
