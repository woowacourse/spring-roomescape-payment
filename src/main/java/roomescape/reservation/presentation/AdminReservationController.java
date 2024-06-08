package roomescape.reservation.presentation;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import roomescape.member.domain.Member;
import roomescape.reservation.application.BookingManageService;
import roomescape.reservation.application.BookingQueryService;
import roomescape.reservation.application.ReservationFactory;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.domain.ReservationStatus;
import roomescape.reservation.dto.request.AdminReservationSaveRequest;
import roomescape.reservation.dto.response.ReservationResponse;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/admin/reservations")
public class AdminReservationController {
    private final ReservationFactory reservationFactory;
    private final BookingQueryService bookingQueryService;
    private final BookingManageService bookingManageService;

    public AdminReservationController(ReservationFactory reservationFactory,
                                      BookingQueryService bookingQueryService,
                                      BookingManageService bookingManageService) {
        this.reservationFactory = reservationFactory;
        this.bookingQueryService = bookingQueryService;
        this.bookingManageService = bookingManageService;
    }

    @PostMapping
    public ResponseEntity<ReservationResponse> createReservation(@RequestBody @Valid AdminReservationSaveRequest request) {
        Reservation newReservation = reservationFactory.create(request.timeId(), request.themeId(),
                request.memberId(), request.date(), ReservationStatus.BOOKING);
        Reservation createdReservation = bookingManageService.create(newReservation);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ReservationResponse.from(createdReservation));
    }

    @GetMapping
    public ResponseEntity<List<ReservationResponse>> findReservations() {
        List<Reservation> reservations = bookingQueryService.findAll();
        return ResponseEntity.ok(reservations.stream()
                .map(ReservationResponse::from)
                .toList());
    }

    @GetMapping("/searching")
    public ResponseEntity<List<ReservationResponse>> findReservationsByMemberIdAndThemeIdAndDateBetween(
            @RequestParam Long memberId, @RequestParam Long themeId,
            @RequestParam LocalDate fromDate, @RequestParam LocalDate toDate) {
        List<Reservation> reservations = bookingQueryService.findAllByMemberIdAndThemeIdAndDateBetween(
                memberId, themeId, fromDate, toDate);
        return ResponseEntity.ok(reservations.stream()
                .map(ReservationResponse::from)
                .toList());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReservation(@PathVariable Long id, Member loginMember) {
        bookingManageService.delete(id, loginMember);
        return ResponseEntity.noContent().build();
    }
}
