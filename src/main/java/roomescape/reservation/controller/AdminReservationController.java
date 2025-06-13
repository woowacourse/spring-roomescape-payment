package roomescape.reservation.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.net.URI;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import roomescape.auth.annotation.RequiredAdmin;
import roomescape.auth.dto.LoginMember;
import roomescape.auth.service.AuthService;
import roomescape.common.exception.dto.ErrorResponse;
import roomescape.reservation.dto.request.AdminReservationRequest;
import roomescape.reservation.dto.request.ReservationCreateRequest;
import roomescape.reservation.dto.response.ReservationResponse;
import roomescape.reservation.service.ReservationService;

@Tag(name = "관리자 예약", description = "관리자 예약 API")
@Slf4j
@RequestMapping("/admin/reservations")
@RestController
public class AdminReservationController {

    private final AuthService authService;
    private final ReservationService reservationService;

    public AdminReservationController(final AuthService authService, final ReservationService reservationService) {
        this.authService = authService;
        this.reservationService = reservationService;
    }

    @Operation(summary = "관리자 예약 생성", description = "관리자가 특정 회원의 예약을 생성합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "예약 생성 성공",
                    content = @Content(
                            mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "이미 예약 또는 대기 중이거나 지난 시간",
                    content = @Content(
                            mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 회원/시간/테마",
                    content = @Content(
                            mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @RequiredAdmin
    @PostMapping
    public ResponseEntity<ReservationResponse> create(
            @Valid @RequestBody final AdminReservationRequest request
    ) {
        LoginMember member = authService.findLoginMemberById(request.memberId());
        log.info("관리자 예약 생성 요청: memberId={}, date={}, timeId={}, themeId={}",
                member.id(), request.date(), request.timeId(), request.themeId());

        ReservationCreateRequest reservationCreateRequest = new ReservationCreateRequest(
                request.date(),
                request.timeId(),
                request.themeId(),
                member
        );
        ReservationResponse response = reservationService.create(reservationCreateRequest);

        log.info("관리자 예약 생성 완료: reservationId={}", response.id());
        return ResponseEntity.created(URI.create("/reservations/" + response.id()))
                .body(response);
    }
}
