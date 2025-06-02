package roomescape.reservation.controller;

import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import roomescape.reservation.dto.AdminReservationPaymentRequest;
import roomescape.reservation.dto.MyPageReservationResponse;
import roomescape.reservation.dto.ReservationPaymentRequest;
import roomescape.reservation.dto.ReservationResponse;
import roomescape.reservation.service.ReservationService;

@RestController
public class ReservationController {

    private final ReservationService reservationService;

    public ReservationController(final ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @PostMapping("/reservations")
    public ResponseEntity<ReservationResponse> addReservation(
            @Valid @RequestBody ReservationPaymentRequest request,
            Long memberId) {
        ReservationResponse response = reservationService.addReservation(memberId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/admin/reservations")
    public ResponseEntity<ReservationResponse> addReservationForAdmin(
            @RequestBody AdminReservationPaymentRequest request) {
        ReservationResponse response = reservationService.addReservationByAdmin(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/reservations")
    public ResponseEntity<List<ReservationResponse>> getAllReservations() {
        return ResponseEntity.status(HttpStatus.OK).body(reservationService.getAllReservations());
    }

    @GetMapping("/members/reservations")
    public ResponseEntity<List<MyPageReservationResponse>> getMyReservations(Long memberId) {
        List<MyPageReservationResponse> response = reservationService.getMyPageReservations(memberId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/admin/reservations")
    public ResponseEntity<List<ReservationResponse>> getReservationsByFilterForAdmin(
            @RequestParam(required = false, name = "memberId") Long memberId,
            @RequestParam(required = false, name = "themeId") Long themeId,
            @RequestParam(required = false, name = "dateFrom") LocalDate dateFrom,
            @RequestParam(required = false, name = "dateTo") LocalDate dateTo) {
        List<ReservationResponse> response = reservationService.getFilteredReservations(
                memberId,
                themeId,
                dateFrom,
                dateTo);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping("/reservations/{id}")
    public ResponseEntity<Void> removeReservation(@PathVariable(name = "id") Long id) {
        reservationService.removeReservation(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
