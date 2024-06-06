package roomescape.reservation.controller;

import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import roomescape.common.dto.MultipleResponses;
import roomescape.payment.service.PaymentService;
import roomescape.reservation.domain.ReservationStatus;
import roomescape.reservation.dto.ReservationResponse;
import roomescape.reservation.dto.ReservationSaveRequest;
import roomescape.reservation.dto.ReservationSearchConditionRequest;
import roomescape.reservation.dto.ReservationWaitingResponse;
import roomescape.reservation.service.ReservationService;

@RestController
@RequestMapping("/admin")
public class AdminReservationApiController {

    private final ReservationService reservationService;
    private final PaymentService paymentService;

    public AdminReservationApiController(ReservationService reservationService, PaymentService paymentService) {
        this.reservationService = reservationService;
        this.paymentService = paymentService;
    }

    @GetMapping("/reservations")
    public ResponseEntity<MultipleResponses<ReservationResponse>> findAll(
            @RequestParam(name = "status", defaultValue = "SUCCESS") ReservationStatus reservationStatus
    ) {
        List<ReservationResponse> reservationResponses = reservationService.findAllByStatus(reservationStatus);

        return ResponseEntity.ok(new MultipleResponses<>(reservationResponses));
    }

    @GetMapping("/reservations/search")
    public ResponseEntity<MultipleResponses<ReservationResponse>> findAllBySearchCond(
            @Valid @ModelAttribute ReservationSearchConditionRequest reservationSearchConditionRequest
    ) {
        List<ReservationResponse> reservationResponses =
                reservationService.findAllBySearchCondition(reservationSearchConditionRequest);

        return ResponseEntity.ok(new MultipleResponses<>(reservationResponses));
    }

    @GetMapping("/reservations/waiting")
    public ResponseEntity<MultipleResponses<ReservationWaitingResponse>> findWaitingReservations() {
        List<ReservationWaitingResponse> waitingReservations = reservationService.findWaitingReservations();

        return ResponseEntity.ok(new MultipleResponses<>(waitingReservations));
    }

    @PostMapping("/reservations")
    public ResponseEntity<ReservationResponse> saveReservationByAdmin(
            @Valid @RequestBody ReservationSaveRequest reservationSaveRequest
    ) {
        ReservationResponse reservationResponse =
                reservationService.saveReservationSuccessByAdmin(reservationSaveRequest);

        return ResponseEntity.created(URI.create("/reservations/" + reservationResponse.id()))
                .body(reservationResponse);
    }

    @PatchMapping("/reservations/{id}")
    public ResponseEntity<Void> cancelReservation(@PathVariable("id") Long id) {
        paymentService.cancel(reservationService.findById(id));
        reservationService.cancelReservationById(id);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/reservations/{id}")
    public ResponseEntity<Void> deleteWaiting(@PathVariable("id") Long id) {
        reservationService.deleteWaitingById(id);

        return ResponseEntity.noContent().build();
    }
}
