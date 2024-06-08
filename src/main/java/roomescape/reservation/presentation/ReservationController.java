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
import roomescape.reservation.application.ReservationFactory;
import roomescape.reservation.application.ReservationQueryService;
import roomescape.reservation.application.WaitingManageService;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.domain.ReservationStatus;
import roomescape.reservation.dto.request.ReservationPayRequest;
import roomescape.reservation.dto.request.ReservationSaveRequest;
import roomescape.reservation.dto.response.MyReservationResponse;
import roomescape.reservation.dto.response.ReservationResponse;

import java.util.List;

@RestController
@RequestMapping("/reservations")
public class ReservationController {
    private final ReservationFactory reservationFactory;
    private final WaitingManageService waitingManageService;
    private final BookingManageService bookingManageService;
    private final PaymentService paymentService;
    private final ReservationQueryService reservationQueryService;

    public ReservationController(ReservationFactory reservationFactory,
                                 WaitingManageService waitingManageService,
                                 BookingManageService bookingManageService,
                                 PaymentService paymentService,
                                 ReservationQueryService reservationQueryService) {
        this.reservationFactory = reservationFactory;
        this.waitingManageService = waitingManageService;
        this.bookingManageService = bookingManageService;
        this.paymentService = paymentService;
        this.reservationQueryService = reservationQueryService;
    }

    @PostMapping
    public ResponseEntity<ReservationResponse> createReservation(@RequestBody @Valid ReservationPayRequest request,
                                                                 Member loginMember) {
        ReservationSaveRequest reservationSaveRequest = request.reservationSaveRequest();
        Reservation newReservation = reservationFactory.create(reservationSaveRequest.timeId(),
                reservationSaveRequest.themeId(), loginMember.getId(), reservationSaveRequest.date(), ReservationStatus.BOOKING);
        ConfirmedPayment confirmedPayment = paymentService.confirm(request.newPayment());
        Reservation createdReservation = bookingManageService.createWithPayment(newReservation, confirmedPayment);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ReservationResponse.from(createdReservation));
    }

    @PostMapping("/waiting")
    public ResponseEntity<ReservationResponse> createWaitingReservation(@RequestBody @Valid ReservationSaveRequest request,
                                                                        Member loginMember) {
        Reservation newReservation = reservationFactory.create(request.timeId(), request.themeId(),
                loginMember.getId(), request.date(), ReservationStatus.WAITING);
        Reservation createdReservation = waitingManageService.create(newReservation);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ReservationResponse.from(createdReservation));
    }

    @GetMapping("/mine")
    public ResponseEntity<List<MyReservationResponse>> findMyReservations(Member loginMember) {
        return ResponseEntity.ok(reservationQueryService.findAllMyReservations(loginMember));
    }

    @DeleteMapping("/{id}/waiting")
    public ResponseEntity<Void> deleteMyWaitingReservation(@PathVariable Long id, Member loginMember) {
        waitingManageService.delete(id, loginMember);
        return ResponseEntity.noContent().build();
    }
}
