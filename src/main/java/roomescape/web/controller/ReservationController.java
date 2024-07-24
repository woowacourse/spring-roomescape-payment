package roomescape.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import roomescape.dto.payment.PaymentResponse;
import roomescape.dto.reservation.ReservationRequest;
import roomescape.dto.reservation.ReservationResponse;
import roomescape.dto.reservation.ReservationfilterRequest;
import roomescape.dto.reservation.UserReservationPaymentRequest;
import roomescape.dto.reservation.UserReservationPaymentResponse;
import roomescape.service.booking.reservation.ReservationService;
import roomescape.service.booking.reservation.module.PaymentService;
import roomescape.web.argumentresolver.MemberId;

@Tag(name = "예약 관리")
@RestController
class ReservationController {

    private final ReservationService reservationService;
    private final PaymentService paymentService;

    public ReservationController(ReservationService reservationService, PaymentService paymentService) {
        this.reservationService = reservationService;
        this.paymentService = paymentService;
    }

    @PostMapping("/admin/reservations")
    public ResponseEntity<ReservationResponse> addReservationByAdmin(
            @RequestBody ReservationRequest reservationRequest) {
        ReservationResponse reservationResponse = reservationService.registerReservation(reservationRequest);
        return ResponseEntity.created(URI.create("/reservations/" + reservationResponse.id()))
                .body(reservationResponse);
    }

    @Operation(summary = "예약 등록", description = "사용자의 예약 정보를 받아 예약을 등록한다.")
    @PostMapping("/reservations")
    public ResponseEntity<ReservationResponse> addReservationByUser(
            @RequestBody UserReservationPaymentRequest userReservationPaymentRequest,
            @MemberId Long memberId) {
        PaymentResponse paymentResponse = paymentService.payByToss(userReservationPaymentRequest);
        ReservationResponse reservationResponse = reservationService.registerReservationPayments(
                userReservationPaymentRequest, memberId, paymentResponse);
        return ResponseEntity.created(URI.create("/reservations/" + reservationResponse.id()))
                .body(reservationResponse);
    }

    @GetMapping("/reservations")
    public ResponseEntity<List<ReservationResponse>> getReservations() {
        return ResponseEntity.ok(reservationService.findAllReservations());
    }

    @GetMapping(value = "/reservations/search")
    public ResponseEntity<List<ReservationResponse>> getReservations(
            @ModelAttribute ReservationfilterRequest reservationfilterRequest
    ) {
        return ResponseEntity.ok(reservationService.findReservationsByFilter(reservationfilterRequest));
    }

    @GetMapping("/reservations-mine")
    public ResponseEntity<List<UserReservationPaymentResponse>> getReservationsMine(@MemberId Long memberId) {
        List<UserReservationPaymentResponse> userReservationResponses = reservationService.findReservationByMemberId(
                memberId);

        return ResponseEntity.ok(userReservationResponses);
    }

    @GetMapping("/reservations/{id}")
    public ResponseEntity<ReservationResponse> getReservation(@PathVariable Long id) {
        ReservationResponse reservationResponse = reservationService.findReservation(id);
        return ResponseEntity.ok(reservationResponse);
    }

    @DeleteMapping("/reservations/{id}")
    public ResponseEntity<Void> deleteReservation(@PathVariable Long id) {
        reservationService.deleteReservation(id);
        return ResponseEntity.noContent().build();
    }
}
