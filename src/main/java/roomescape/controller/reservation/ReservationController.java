package roomescape.controller.reservation;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import roomescape.controller.auth.AuthenticationPrincipal;
import roomescape.dto.auth.LoginMember;
import roomescape.dto.payment.PaymentDto;
import roomescape.dto.payment.PaymentSaveRequest;
import roomescape.dto.reservation.MyReservationWithRankResponse;
import roomescape.dto.reservation.ReservationDto;
import roomescape.dto.reservation.ReservationResponse;
import roomescape.dto.reservation.ReservationSaveRequest;
import roomescape.service.PaymentService;
import roomescape.service.ReservationFacadeService;
import roomescape.service.ReservationService;

import java.util.List;

@RequestMapping("/reservations")
@RestController
public class ReservationController {

    private final ReservationService reservationService;
    private final PaymentService paymentService;
    private final ReservationFacadeService reservationFacadeService;

    public ReservationController(final ReservationService reservationService,
                                 final PaymentService paymentService,
                                 final ReservationFacadeService reservationFacadeService) {
        this.reservationService = reservationService;
        this.paymentService = paymentService;
        this.reservationFacadeService = reservationFacadeService;
    }

    @PostMapping
    public ResponseEntity<ReservationResponse> createReservation(
            @AuthenticationPrincipal final LoginMember loginMember,
            @RequestBody final ReservationSaveRequest request) {
        final ReservationDto reservationDto = ReservationDto.of(request, loginMember.id());
        final PaymentDto paymentDto = PaymentDto.from(request);
        final ReservationResponse reservationResponse = reservationFacadeService.createReservation(reservationDto, paymentDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(reservationResponse);
    }

    @GetMapping
    public ResponseEntity<List<ReservationResponse>> findReservations() {
        return ResponseEntity.ok(reservationService.findAll());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelReservation(@PathVariable final Long id) {
        reservationFacadeService.cancelReservation(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/mine")
    public ResponseEntity<List<MyReservationWithRankResponse>> findMyReservationsAndWaitings(
            @AuthenticationPrincipal final LoginMember loginMember) {
        return ResponseEntity.ok(reservationService.findMyReservationsAndWaitings(loginMember));
    }

    @PostMapping("/{id}/pay")
    public ResponseEntity<Void> payForReservation(@PathVariable final Long id,
                                                  @AuthenticationPrincipal final LoginMember loginMember,
                                                  @RequestBody final PaymentSaveRequest request) {
        paymentService.payForReservation(PaymentDto.from(request), id, loginMember.id());
        return ResponseEntity.ok().build();
    }
}
