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
import org.springframework.web.bind.annotation.RestController;
import roomescape.member.domain.Member;
import roomescape.payment.application.PaymentService;
import roomescape.payment.domain.ConfirmedPayment;
import roomescape.reservation.application.BookingManageService;
import roomescape.reservation.application.BookingQueryService;
import roomescape.reservation.application.ReservationTimeService;
import roomescape.reservation.application.ThemeService;
import roomescape.reservation.application.WaitingManageService;
import roomescape.reservation.application.WaitingQueryService;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.domain.ReservationStatus;
import roomescape.reservation.domain.ReservationTime;
import roomescape.reservation.domain.Theme;
import roomescape.reservation.dto.request.ReservationPayRequest;
import roomescape.reservation.dto.request.ReservationSaveRequest;
import roomescape.reservation.dto.response.MyReservationResponse;
import roomescape.reservation.dto.response.ReservationResponse;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/reservations")
public class ReservationController {
    private final BookingQueryService bookingQueryService;
    private final WaitingManageService waitingManageService;
    private final BookingManageService bookingManageService;
    private final WaitingQueryService waitingQueryService;
    private final ReservationTimeService reservationTimeService;
    private final ThemeService themeService;
    private final PaymentService paymentService;

    public ReservationController(BookingQueryService bookingQueryService,
                                 WaitingManageService waitingManageService,
                                 BookingManageService bookingManageService,
                                 WaitingQueryService waitingQueryService,
                                 ReservationTimeService reservationTimeService,
                                 ThemeService themeService,
                                 PaymentService paymentService) {
        this.bookingQueryService = bookingQueryService;
        this.waitingManageService = waitingManageService;
        this.bookingManageService = bookingManageService;
        this.waitingQueryService = waitingQueryService;
        this.reservationTimeService = reservationTimeService;
        this.themeService = themeService;
        this.paymentService = paymentService;
    }

    @PostMapping
    public ResponseEntity<ReservationResponse> createReservation(@RequestBody @Valid ReservationPayRequest request,
                                                                 Member loginMember) {
        Reservation newReservation = toNewReservation(request.reservationSaveRequest(), loginMember, ReservationStatus.BOOKING);
        ConfirmedPayment confirmedPayment = paymentService.confirm(request.newPayment());
        Reservation createdReservation = bookingManageService.createWithPayment(newReservation, confirmedPayment);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ReservationResponse.from(createdReservation));
    }

    @PostMapping("/waiting")
    public ResponseEntity<ReservationResponse> createWaitingReservation(@RequestBody @Valid ReservationSaveRequest request,
                                                                        Member loginMember) {
        Reservation newReservation = toNewReservation(request, loginMember, ReservationStatus.WAITING);
        Reservation createdReservation = waitingManageService.create(newReservation);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ReservationResponse.from(createdReservation));
    }

    private Reservation toNewReservation(ReservationSaveRequest request, Member loginMember, ReservationStatus status) {
        ReservationTime reservationTime = reservationTimeService.findById(request.timeId());
        Theme theme = themeService.findById(request.themeId());
        return new Reservation(loginMember, request.date(), reservationTime, theme, status);
    }

    @GetMapping("/mine")
    public ResponseEntity<List<MyReservationResponse>> findMyReservations(Member loginMember) {
        List<MyReservationResponse> myReservationResponses = new ArrayList<>();
        myReservationResponses.addAll(
                bookingQueryService.findAllByMember(loginMember).stream()
                        .map(MyReservationResponse::from)
                        .toList()
        );
        myReservationResponses.addAll(
                waitingQueryService.findAllUnpaidByMember(loginMember).stream()
                        .map(MyReservationResponse::from)
                        .toList()
        );
        myReservationResponses.addAll(
                waitingQueryService.findAllWithPreviousCountByMember(loginMember).stream()
                        .map(MyReservationResponse::from)
                        .toList()
        );
        return ResponseEntity.ok(myReservationResponses);
    }

    @DeleteMapping("/{id}/waiting")
    public ResponseEntity<Void> deleteMyWaitingReservation(@PathVariable Long id, Member loginMember) {
        waitingManageService.delete(id, loginMember);
        return ResponseEntity.noContent().build();
    }
}
