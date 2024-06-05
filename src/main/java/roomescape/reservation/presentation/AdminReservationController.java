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
import roomescape.member.application.MemberService;
import roomescape.member.domain.Member;
import roomescape.reservation.application.BookingManageService;
import roomescape.reservation.application.BookingQueryService;
import roomescape.reservation.application.ReservationTimeService;
import roomescape.reservation.application.ThemeService;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.domain.ReservationTime;
import roomescape.reservation.domain.Theme;
import roomescape.reservation.dto.request.AdminReservationSaveRequest;
import roomescape.reservation.dto.response.ReservationResponse;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/admin/reservations")
public class AdminReservationController {
    private final BookingQueryService bookingQueryService;
    private final BookingManageService bookingManageService;
    private final MemberService memberService;
    private final ReservationTimeService reservationTimeService;
    private final ThemeService themeService;

    public AdminReservationController(BookingQueryService bookingQueryService,
                                      BookingManageService bookingManageService,
                                      MemberService memberService,
                                      ReservationTimeService reservationTimeService,
                                      ThemeService themeService) {
        this.bookingQueryService = bookingQueryService;
        this.bookingManageService = bookingManageService;
        this.memberService = memberService;
        this.reservationTimeService = reservationTimeService;
        this.themeService = themeService;
    }

    @PostMapping
    public ResponseEntity<ReservationResponse> createReservation(@RequestBody @Valid AdminReservationSaveRequest request) {
        ReservationTime reservationTime = reservationTimeService.findById(request.timeId());
        Theme theme = themeService.findById(request.themeId());
        Member member = memberService.findById(request.memberId());
        Reservation newReservation = request.toModel(theme, reservationTime, member);
        Reservation createdReservation = bookingManageService.create(newReservation);
        Reservation schduledReservation = bookingManageService.scheduleRecentReservation(createdReservation);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ReservationResponse.from(schduledReservation));
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