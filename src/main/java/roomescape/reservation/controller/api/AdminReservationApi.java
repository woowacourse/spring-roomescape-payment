package roomescape.reservation.controller.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import roomescape.auth.annotation.RequiredAdmin;
import roomescape.reservation.dto.request.AdminReservationRequest;
import roomescape.reservation.dto.response.ReservationResponse;

@Tag(name = "AdminReservation", description = "어드민 예약 API")
@RequestMapping("/admin/reservations")
public interface AdminReservationApi {

    @Operation(summary = "어드민 예약 생성", description = "어드민 페이지에서 예약을 생성한다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "예약 생성 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "401", description = "로그인 정보 없음"),
            @ApiResponse(responseCode = "409", description = "중복 예약 생성")
    })
    @RequiredAdmin
    @PostMapping
    ResponseEntity<ReservationResponse> create(@Valid @RequestBody final AdminReservationRequest request);
}
