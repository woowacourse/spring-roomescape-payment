package roomescape.reservation.service.component;

import java.util.List;
import java.util.stream.Stream;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.reservation.controller.dto.response.MemberReservationResponse;
import roomescape.reservation.service.module.ReservationQueryService;
import roomescape.reservation.service.module.WaitingQueryService;

@Service
public class MemberReservationService {

    private final ReservationQueryService reservationQueryService;
    private final WaitingQueryService waitingQueryService;

    public MemberReservationService(
            ReservationQueryService reservationQueryService,
            WaitingQueryService waitingQueryService
    ) {
        this.reservationQueryService = reservationQueryService;
        this.waitingQueryService = waitingQueryService;
    }

    @Transactional(readOnly = true)
    public List<MemberReservationResponse> findReservationsAndWaitings(Long memberId) {
        List<MemberReservationResponse> reservations = findMemberReservations(memberId);
        List<MemberReservationResponse> waitings = findMemberWaitings(memberId);

        return Stream.of(reservations, waitings)
                .flatMap(List::stream)
                .toList();
    }

    private List<MemberReservationResponse> findMemberReservations(Long memberId) {
        return reservationQueryService.findReservationWithPaymentsByMemberId(memberId)
                .stream()
                .map(MemberReservationResponse::toResponse)
                .toList();
    }

    private List<MemberReservationResponse> findMemberWaitings(Long memberId) {
        return waitingQueryService.findWaitingWithRanksByMemberId(memberId)
                .stream()
                .map(MemberReservationResponse::toResponse)
                .toList();
    }
}
