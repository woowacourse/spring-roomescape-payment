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

    @GetMapping("/reservations")
    public ResponseEntity<List<ReservationResponse>> reservationList() {
        return ResponseEntity.status(HttpStatus.OK).body(reservationService.getAllReservations());
    }

    @PostMapping("/reservations")
    public ResponseEntity<ReservationResponse> addReservation(
            @RequestBody @Valid final ReservationPaymentRequest request,
            final Long memberId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(reservationService.addReservation(memberId, request));
    }

    @DeleteMapping("/reservations/{id}")
    public ResponseEntity<Void> removeReservation(@PathVariable(name = "id") long id) {
        reservationService.removeReservation(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

//    @PostMapping("/admin/reservations")
//    public ResponseEntity<ReservationResponse> addReservationForAdmin(
//            @RequestBody @Valid final ReservationRecipe recipe) {
//        ReservationResponse response = reservationService.addReservation(recipe);
//        return ResponseEntity.status(HttpStatus.CREATED).body(response);
//    }

    @GetMapping("/admin/reservations")
    public ResponseEntity<List<ReservationResponse>> getReservationsByFilterForAdmin(
            @RequestParam(required = false, name = "memberId") Long memberId,
            @RequestParam(required = false, name = "themeId") Long themeId,
            @RequestParam(required = false, name = "dateFrom") LocalDate dateFrom,
            @RequestParam(required = false, name = "dateTo") LocalDate dateTo
    ) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(reservationService.getFilteredReservations(memberId, themeId, dateFrom, dateTo));
    }

    @GetMapping("/members/reservations")
    public ResponseEntity<List<MyPageReservationResponse>> getMyReservationsForUser(Long memberId) {
        List<MyPageReservationResponse> reservations = reservationService.getReservationsByMemberId(memberId);
        return ResponseEntity.ok(reservations);
    }
}
