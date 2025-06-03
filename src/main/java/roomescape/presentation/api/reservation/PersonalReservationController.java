package roomescape.presentation.api.reservation;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import roomescape.application.reservation.query.ReservationQueryService;
import roomescape.application.reservation.query.WaitingQueryService;
import roomescape.presentation.api.reservation.response.MyReservationResponse;
import roomescape.presentation.support.methodresolver.AuthInfo;
import roomescape.presentation.support.methodresolver.AuthPrincipal;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

@RestController
public class PersonalReservationController {

    private final ReservationQueryService reservationQueryService;
    private final WaitingQueryService waitingQueryService;

    public PersonalReservationController(final ReservationQueryService reservationQueryService,
                                         final WaitingQueryService waitingQueryService) {
        this.reservationQueryService = reservationQueryService;
        this.waitingQueryService = waitingQueryService;
    }

    @GetMapping("/reservations-mine")
    public ResponseEntity<List<MyReservationResponse>> findMineReservations(@AuthPrincipal final AuthInfo authInfo) {
        final List<MyReservationResponse> reservations = getSortedReservationsWithStatus(authInfo.memberId());
        return ResponseEntity.ok(reservations);
    }

    private List<MyReservationResponse> getSortedReservationsWithStatus(final Long memberId) {
        final List<MyReservationResponse> confirmedReservations = getReservations(memberId);
        final List<MyReservationResponse> waitingReservations = getWaitings(memberId);
        return Stream.concat(confirmedReservations.stream(), waitingReservations.stream())
                .sorted(Comparator.comparing(MyReservationResponse::date))
                .toList();
    }

    private List<MyReservationResponse> getReservations(final Long memberId) {
        return reservationQueryService.findReservationsWithStatus(memberId)
                .stream()
                .map(MyReservationResponse::from)
                .toList();
    }

    private List<MyReservationResponse> getWaitings(final Long memberId) {
        return waitingQueryService.findWaitingByMemberId(memberId)
                .stream()
                .map(MyReservationResponse::from)
                .toList();
    }
}
