package roomescape.controller.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import roomescape.dto.response.ReservationTimeResponse;
import roomescape.dto.response.ReservationTimeWithAvailabilityResponse;

import java.time.LocalDate;
import java.util.List;

@Tag(name = "3. 예약 시간 관련 API")
public interface MemberTimeApi {

    @Operation(summary = "모든 예약 시간 조회")
    ResponseEntity<List<ReservationTimeResponse>> getAll();

    @Operation(summary = "테마, 날짜의 예약 가능 시간 조회")
    ResponseEntity<List<ReservationTimeWithAvailabilityResponse>> getAvailables(
            @Parameter(required = true) long themeId,
            @Parameter(required = true) LocalDate date
    );
}
