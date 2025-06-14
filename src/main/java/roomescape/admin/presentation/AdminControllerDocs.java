package roomescape.admin.presentation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import roomescape.admin.dto.AdminReservationRequest;
import roomescape.reservation.dto.response.ReservationResponse;

@Tag(name = "관리자", description = "관리자 관련 API")
public interface AdminControllerDocs {

    @Operation(summary = "관리자 예약 생성")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "예약 생성 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content(examples = {
                    @ExampleObject(
                            name = "이미 예약된 시간",
                            value = "{\"message\": \"이미 예약된 시간입니다.\"}"
                    ),
                    @ExampleObject(
                            name = "존재하지 않는 회원",
                            value = "{\"message\": \"존재하지 않는 회원입니다.\"}"
                    )
            })),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자"),
            @ApiResponse(responseCode = "403", description = "권한이 없는 사용자")
    })
    ResponseEntity<ReservationResponse> createReservation(AdminReservationRequest request);
} 