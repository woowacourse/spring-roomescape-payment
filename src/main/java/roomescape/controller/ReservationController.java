package roomescape.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import roomescape.dto.*;
import roomescape.service.PaymentService;
import roomescape.service.ReservationService;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;

@Tag(name = "Reservation", description = "예약 API")
@RestController
public class ReservationController {
    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService, PaymentService paymentService) {
        this.reservationService = reservationService;
    }

    @Transactional
    @PostMapping("/reservations")
    public ResponseEntity<ReservationResponse> saveReservation(
            @Authenticated LoginMemberRequest loginMemberRequest,
            @RequestBody ReservationWithPaymentRequest reservationWithPaymentRequest
    ) {
        ReservationResponse savedReservationResponse = reservationService.saveByUser(loginMemberRequest,
                reservationWithPaymentRequest);
        return ResponseEntity.created(URI.create("/reservations/" + savedReservationResponse.id()))
                .body(savedReservationResponse);
    }

    @PostMapping("/admin/reservations")
    public ResponseEntity<ReservationResponse> saveReservationByAdmin(
            @RequestBody AdminReservationRequest reservationRequest) {
        ReservationResponse reservationResponse = reservationService.saveByAdmin(reservationRequest);
        return ResponseEntity.created(URI.create("/reservations/" + reservationResponse.id()))
                .body(reservationResponse);
    }

    @GetMapping("/reservations")
    public List<ReservationResponse> findAllReservations() {
        return reservationService.findAll();
    }

    @GetMapping("/reservations/waiting")
    @AdminOnly
    public List<ReservationResponse> findAllRemainedWaiting() {
        return reservationService.findAllRemainedWaiting();
    }

    @GetMapping("/reservations/mine")
    public List<ReservationDetailResponse> findMemberReservations(
            @Authenticated LoginMemberRequest loginMemberRequest) {
        return reservationService.findAllByMemberId(loginMemberRequest.id());
    }

    @GetMapping("/reservations/search")
    public List<ReservationResponse> searchReservation(@RequestParam(required = false) Long themeId,
                                                       @RequestParam(required = false) Long memberId,
                                                       @RequestParam(defaultValue = "#{T(java.time.LocalDate).now()}")
                                                       LocalDate dateFrom,
                                                       @RequestParam(defaultValue = "#{T(java.time.LocalDate).now()}")
                                                       LocalDate dateTo) {
        return reservationService.searchReservation(themeId, memberId, dateFrom, dateTo);
    }

    @DeleteMapping("/reservations/{id}")
    public ResponseEntity<Void> delete(@Authenticated LoginMemberRequest loginMemberRequest,
                                       @PathVariable long id) {
        reservationService.deleteByUser(loginMemberRequest, id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/admin/reservations/{id}")
    @AdminOnly
    public ResponseEntity<Void> deleteByAdmin(@PathVariable long id) {
        reservationService.deleteWaitingByAdmin(id);
        return ResponseEntity.noContent().build();
    }
}
