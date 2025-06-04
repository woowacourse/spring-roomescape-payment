package roomescape.reservation.controller;

import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import roomescape.global.auth.AuthMember;
import roomescape.global.auth.LoginMember;
import roomescape.reservation.dto.AdminReservationResponse;
import roomescape.reservation.dto.MyReservationResponse;
import roomescape.reservation.dto.UserReservationCreateRequest;
import roomescape.reservation.dto.UserReservationResponse;
import roomescape.reservation.service.ReservationCommandService;
import roomescape.reservation.service.ReservationQueryService;

@RestController
public class ReservationController {

    private final ReservationCommandService reservationCommandService;
    private final ReservationQueryService reservationQueryService;

    public ReservationController(final ReservationCommandService reservationCommandService,
                                 final ReservationQueryService reservationQueryService) {
        this.reservationCommandService = reservationCommandService;
        this.reservationQueryService = reservationQueryService;
    }

    @PostMapping("/reservations")
    public ResponseEntity<UserReservationResponse> create(
            @RequestBody @Valid final UserReservationCreateRequest request,
            @AuthMember final LoginMember member
    ) {
        final UserReservationResponse response = reservationCommandService.createMyReservationByUser(
                request, member);
        return ResponseEntity.created(URI.create("/reservations/" + response.id())).body(response);
    }

    @GetMapping("/reservations")
    public ResponseEntity<List<AdminReservationResponse>> findAll() {
        final List<AdminReservationResponse> responses = reservationQueryService.findAllReservationsForAdmin();
        return ResponseEntity.ok().body(responses);
    }

    @GetMapping("/me/reservations")
    public ResponseEntity<List<MyReservationResponse>> findAllMyReservations(@AuthMember LoginMember loginMember) {
        final List<MyReservationResponse> responses = reservationQueryService.getMyReservations(loginMember);
        return ResponseEntity.ok().body(responses);
    }
}
