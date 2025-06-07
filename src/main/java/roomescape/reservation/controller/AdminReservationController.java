package roomescape.reservation.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import roomescape.auth.annotation.RequiredAdmin;
import roomescape.auth.service.AuthService;
import roomescape.auth.service.dto.LoginMember;
import roomescape.reservation.service.CreateReservationService;
import roomescape.reservation.service.dto.request.AdminReservationRequest;
import roomescape.reservation.service.dto.request.ReservationCreateRequest;
import roomescape.reservation.service.dto.response.ReservationResponse;

import java.net.URI;

@Tag(name = "어드민 예약 관리")
@RestController
@RequestMapping("/admin/reservations")
public class AdminReservationController {

    private final AuthService authService;
    private final CreateReservationService createReservationService;

    public AdminReservationController(
            final AuthService authService,
            final CreateReservationService createReservationService
    ) {
        this.authService = authService;
        this.createReservationService = createReservationService;
    }

    @Operation(summary = "어드민 예약 생성", description = "어드민 권한의 예약을 생성하여 결제 정보가 포함되지 않는다.")
    @RequiredAdmin
    @PostMapping
    public ResponseEntity<ReservationResponse> create(
            @Valid @RequestBody final AdminReservationRequest request
    ) {
        LoginMember member = authService.findLoginMemberById(request.memberId());
        ReservationCreateRequest reservationCreateRequest =
                new ReservationCreateRequest(request.date(), request.timeId(), request.themeId(), member);
        ReservationResponse response = createReservationService.create(reservationCreateRequest);

        return ResponseEntity.created(URI.create("/reservations/" + response.id()))
                .body(response);
    }
}
