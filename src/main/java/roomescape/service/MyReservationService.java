package roomescape.service;

import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import roomescape.domain.ReservationWaiting;
import roomescape.dto.LoginMemberReservationResponse;
import roomescape.repository.ReservationRepository;
import roomescape.repository.ReservationWaitingRepository;
import roomescape.service.mapper.LoginMemberReservationResponseMapper;
import roomescape.service.mapper.ReservationWaitingResponseMapper;

@Service
public class MyReservationService {
    private final ReservationRepository reservationRepository;
    private final ReservationWaitingRepository waitingRepository;

    public MyReservationService(ReservationRepository reservationRepository,
                                ReservationWaitingRepository waitingRepository) {
        this.reservationRepository = reservationRepository;
        this.waitingRepository = waitingRepository;
    }

    public List<LoginMemberReservationResponse> findLoginMemberReservations(long memberId) {
        List<LoginMemberReservationResponse> reservations = findByMemberIdFromReservations(memberId);
        List<LoginMemberReservationResponse> waitings = findByMemberIdFromWaiting(memberId);

        List<LoginMemberReservationResponse> response = new ArrayList<>(reservations);
        response.addAll(waitings);
        return response;
    }

    private List<LoginMemberReservationResponse> findByMemberIdFromReservations(long memberId) {
        return reservationRepository.findByMemberId(memberId)
                .stream()
                .map(LoginMemberReservationResponseMapper::toResponse)
                .toList();
    }

    private List<LoginMemberReservationResponse> findByMemberIdFromWaiting(long memberId) {
        List<ReservationWaiting> allByMemberId = waitingRepository.findAllByMemberId(memberId);
        return allByMemberId.stream()
                .map(waiting -> {
                    int priority = waiting.calculatePriority(allByMemberId);
                    return ReservationWaitingResponseMapper.toResponse(waiting, priority);
                })
                .map(LoginMemberReservationResponseMapper::from)
                .toList();
    }
}
