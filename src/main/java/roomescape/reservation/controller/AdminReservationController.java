package roomescape.reservation.controller;

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
import roomescape.reservation.dto.request.AdminReservationRequest;
import roomescape.reservation.dto.request.ReservationCreateRequest;
import roomescape.reservation.dto.response.ReservationResponse;
import roomescape.reservation.service.ReservationService;

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
