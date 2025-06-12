package roomescape.presentation.api.reservation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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

@Tag(name = "사용자 예약 API")
@RestController
public class PersonalReservationController {

    private final ReservationQueryService reservationQueryService;
    private final WaitingQueryService waitingQueryService;

    public PersonalReservationController(final ReservationQueryService reservationQueryService,
                                         final WaitingQueryService waitingQueryService) {
        this.reservationQueryService = reservationQueryService;
        this.waitingQueryService = waitingQueryService;
    }

    @Operation(
            summary = "개인 예약 조회",
            description = "사용자가 자신의 예약과 대기열을 조회합니다. 예약은 날짜순으로 정렬됩니다."
    )
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
        return reservationQueryService.getReservationsWithStatusAndPayment(memberId)
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
