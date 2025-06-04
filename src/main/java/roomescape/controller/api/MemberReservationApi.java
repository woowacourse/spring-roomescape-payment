package roomescape.controller.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import roomescape.dto.request.ReservationPendingRequest;
import roomescape.dto.request.ReservationRequest;
import roomescape.dto.response.MyPageReservationResponse;
import roomescape.dto.response.ReservationResponse;
import roomescape.global.LoginInfo;

import java.util.List;

@Tag(name = "1. 예약 관련 API")
public interface MemberReservationApi {

    @Operation(summary = "결제 승인 및 예약")
    ResponseEntity<ReservationResponse> reserve(
            @RequestBody(required = true) ReservationRequest request,
            @Parameter(hidden = true) LoginInfo loginInfo
    );

    @Operation(summary = "예약 대기")
    ResponseEntity<ReservationResponse> addPending(
            @RequestBody(required = true) ReservationPendingRequest request,
            @Parameter(hidden = true) LoginInfo loginInfo
    );

    @Operation(summary = "내 예약 조회")
    ResponseEntity<List<MyPageReservationResponse>> getMines(
            @Parameter(hidden = true) LoginInfo loginInfo
    );

    @Operation(summary = "내 예약 삭제")
    ResponseEntity<Void> remove(
            @Parameter(example = "1", required = true) long reservationId
    );
}
