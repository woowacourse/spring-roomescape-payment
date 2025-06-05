package roomescape.controller.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import roomescape.dto.request.ReservationPendingRequest;
import roomescape.dto.request.ReservationRequest;
import roomescape.dto.response.MyPageReservationResponse;
import roomescape.dto.response.ReservationResponse;
import roomescape.global.LoginInfo;
import roomescape.global.exception.ErrorResponse;

import javax.naming.AuthenticationException;
import java.util.List;

@Tag(name = "2. 예약 관련 API")
public interface MemberReservationApi {

    @Operation(summary = "결제 승인 및 예약")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "정상 응답"),
            @ApiResponse(responseCode = "400", description = "이미 예약/대기 내역이 존재하는 경우", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "400", description = "토스 결제 승인 중 문제가 발생한 경우", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
    ResponseEntity<ReservationResponse> reserve(
            @RequestBody(required = true) ReservationRequest request,
            @Parameter(hidden = true) LoginInfo loginInfo
    );

    @Operation(summary = "예약 대기")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "정상 응답"),
            @ApiResponse(responseCode = "400", description = "이미 예약/대기 내역이 존재하는 경우", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
    ResponseEntity<ReservationResponse> addPending(
            @RequestBody(required = true) ReservationPendingRequest request,
            @Parameter(hidden = true) LoginInfo loginInfo
    );

    @Operation(summary = "내 예약 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "정상 응답"),
    })
    ResponseEntity<List<MyPageReservationResponse>> getMines(
            @Parameter(hidden = true) LoginInfo loginInfo
    );

    @Operation(summary = "내 예약 삭제")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "정상 응답"),
            @ApiResponse(responseCode = "401", description = "내 예약이 아닌 경우", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "id에 해당하는 예약이 없는 경우", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
    ResponseEntity<Void> remove(
            @Parameter(example = "1", required = true) long reservationId,
            @Parameter(hidden = true) LoginInfo loginInfo
    ) throws AuthenticationException;
}
