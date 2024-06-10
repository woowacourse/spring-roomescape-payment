package roomescape.core.controller;

import jakarta.validation.Valid;
import java.net.URI;
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
import roomescape.core.dto.member.LoginMember;
import roomescape.core.dto.payment.PaymentConfirmRequest;
import roomescape.core.dto.reservation.MyReservationResponse;
import roomescape.core.dto.reservation.ReservationPaymentRequest;
import roomescape.core.dto.reservation.ReservationRequest;
import roomescape.core.dto.reservation.ReservationResponse;
import roomescape.core.dto.reservation.WebPaidReservationResponse;
import roomescape.core.service.ReservationService;

@RestController
@RequestMapping("/reservations")
public class ReservationController {
    private final ReservationService reservationService;

    public ReservationController(final ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @PostMapping
    public ResponseEntity<WebPaidReservationResponse> create(
            @Valid @RequestBody final ReservationPaymentRequest request,
            final LoginMember member) {
        final PaymentConfirmRequest paymentRequest = new PaymentConfirmRequest(request);
        final ReservationRequest reservationRequest = new ReservationRequest(member.getId(),
                request.getDate(), request.getTimeId(), request.getThemeId());

        final WebPaidReservationResponse response
                = reservationService.createAndPay(reservationRequest, paymentRequest);
        return ResponseEntity
                .created(URI.create("/reservations/" + response.getReservationResponse().getId()))
                .body(response);
    }

    @GetMapping
    public ResponseEntity<List<ReservationResponse>> findAll() {
        return ResponseEntity.ok(reservationService.findAll());
    }

    @GetMapping(params = {"memberId", "themeId", "dateFrom", "dateTo"})
    public ResponseEntity<List<ReservationResponse>> findAllByMemberAndThemeAndPeriod(
            @RequestParam(name = "memberId") final Long memberId,
            @RequestParam(name = "themeId") final Long themeId,
            @RequestParam(name = "dateFrom") final String dateFrom,
            @RequestParam(name = "dateTo") final String dateTo) {
        return ResponseEntity.ok(reservationService
                .findAllByMemberAndThemeAndPeriod(memberId, themeId, dateFrom, dateTo));
    }

    @GetMapping("/mine")
    public ResponseEntity<List<MyReservationResponse>> findAllByLoginMember(
            final LoginMember loginMember) {
        return ResponseEntity.ok(reservationService.findAllByMember(loginMember));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") final long id,
                                       final LoginMember loginMember) {
        reservationService.delete(id, loginMember);
        return ResponseEntity.noContent().build();
    }
}
