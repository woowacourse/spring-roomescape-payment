package roomescape.reservation.controller;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import roomescape.auth.dto.LoggedInMember;
import roomescape.reservation.dto.MyReservationWaitingResponse;
import roomescape.reservation.service.ReservationPaymentService;
import roomescape.waiting.service.WaitingService;

@RestController
@RequestMapping("/my/reservaitons")
public class MyReservationController {
    private final WaitingService waitingService;
    private final ReservationPaymentService reservationPaymentService;

    public MyReservationController(WaitingService waitingService,
                                   ReservationPaymentService reservationPaymentService) {
        this.waitingService = waitingService;
        this.reservationPaymentService = reservationPaymentService;
    }

    @GetMapping
    public List<MyReservationWaitingResponse> findMyReservations(LoggedInMember member) {
        Long memberId = member.id();

        return Stream.concat(
                        reservationPaymentService.findMyReservationsWithPayment(memberId).stream(),
                        waitingService.findMyWaitings(memberId).stream())
                .sorted(Comparator.comparing(myReservationResponse ->
                        LocalDateTime.of(myReservationResponse.date(), myReservationResponse.startAt())))
                .toList();
    }
}
