package roomescape.controller.api.docs;

import java.time.LocalDate;
import java.util.List;

import org.springframework.http.ResponseEntity;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import roomescape.controller.dto.response.ErrorMessageResponse;
import roomescape.controller.dto.response.TimeAndAvailabilityResponse;

@Tag(name = "UserReservationTime", description = "사용자도 확인 가능한 방탈출 예약 가능 시간 관련 API")
public interface UserReservationTimeApiDocs {
    @Operation(summary = "예약 가능한 시간 조회", description = "예약 가능 여부와 예약 시간을 함께 조회할 수 있다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = {@Content(
                            mediaType = "application/json", schema = @Schema(implementation = TimeAndAvailabilityResponse.class))}),
            @ApiResponse(responseCode = "400", description = "요청 정보 문제로 실패",
                    content = {@Content(
                            mediaType = "application/json", schema = @Schema(implementation = ErrorMessageResponse.class))}),
            @ApiResponse(responseCode = "500", description = "서버 내부 문제로 실패",
                    content = {@Content(
                            mediaType = "application/json", schema = @Schema(implementation = ErrorMessageResponse.class))}),
    })
    ResponseEntity<List<TimeAndAvailabilityResponse>> findAllWithAvailability(LocalDate date, Long id);
}
