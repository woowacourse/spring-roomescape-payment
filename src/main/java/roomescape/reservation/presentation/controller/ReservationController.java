package roomescape.reservation.presentation.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import roomescape.global.auth.Auth;
import roomescape.member.domain.Role;
import roomescape.member.presentation.resolver.LoginMember;
import roomescape.reservation.application.dto.ReservationRequest;
import roomescape.reservation.application.service.ReservationService;
import roomescape.reservation.application.dto.ReservationResponse;
import roomescape.reservation.application.dto.UserReservationsResponse;

@RestController
@RequestMapping("/reservations")
public class ReservationController {

    private final ReservationService reservationService;

    public ReservationController(final ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @Auth(Role.USER)
    @Operation(summary = "예약 추가 API")
    @PostMapping
    public ResponseEntity<ReservationResponse> createReservation(
            final @RequestBody @Valid ReservationRequest reservationRequest,
            final @Parameter(hidden = true) @LoginMember Long memberId
    ) {
        ReservationResponse reservation = reservationService.createUserReservationAndPayment(reservationRequest, memberId);

        return ResponseEntity.created(createUri(reservation.getId()))
                .body(reservation);
    }

    @Auth(Role.USER)
    @Operation(summary = "예약 조회 API")
    @GetMapping
    public ResponseEntity<List<ReservationResponse>> getReservations(
            final @RequestParam(required = false) Long memberId,
            final @RequestParam(required = false) Long themeId,
            final @RequestParam(required = false) LocalDate dateFrom,
            final @RequestParam(required = false) LocalDate dateTo
    ) {
        return ResponseEntity.ok().body(
                reservationService.getReservations(memberId, themeId, dateFrom, dateTo)
        );
    }

    @Auth(Role.USER)
    @Operation(summary = "내 예약 조회 API")
    @GetMapping("/mine")
    public ResponseEntity<List<UserReservationsResponse>> getUserReservations(
            final @Parameter(hidden = true) @LoginMember Long memberId
    ) {
        return ResponseEntity.ok().body(
                reservationService.getUserReservations(memberId)
        );
    }

    @Auth(Role.USER)
    @Operation(summary = "예약 삭제 API")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReservation(
            final @PathVariable Long id
    ) {
        reservationService.deleteReservation(id);
        return ResponseEntity.noContent().build();
    }

    private URI createUri(Long reservationId) {
        return ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(reservationId)
                .toUri();
    }
}
