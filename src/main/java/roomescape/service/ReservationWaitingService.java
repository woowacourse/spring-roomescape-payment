package roomescape.service;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.domain.reservation.ReservationFactory;
import roomescape.domain.reservationwaiting.ReservationWaiting;
import roomescape.domain.reservationwaiting.ReservationWaitingRepository;
import roomescape.dto.LoginMember;
import roomescape.dto.request.reservation.WaitingRequest;
import roomescape.dto.response.reservation.ReservationResponse;
import roomescape.exception.RoomescapeException;

@Service
@Transactional
public class ReservationWaitingService {
    private final ReservationWaitingRepository reservationWaitingRepository;
    private final ReservationFactory reservationFactory;

    public ReservationWaitingService(ReservationWaitingRepository reservationWaitingRepository,
                                     ReservationFactory reservationFactory) {
        this.reservationWaitingRepository = reservationWaitingRepository;
        this.reservationFactory = reservationFactory;
    }

    public ReservationResponse saveReservationWaiting(WaitingRequest request, LoginMember member) {
        ReservationWaiting reservationWaiting =
                reservationFactory.createWaiting(member.id(), request.date(), request.timeId(), request.themeId());
        return ReservationResponse.from(reservationWaitingRepository.save(reservationWaiting));
    }

    public void deleteById(long id) {
        reservationWaitingRepository.findById(id)
                .orElseThrow(() -> new RoomescapeException(HttpStatus.BAD_REQUEST, "존재하지 않는 예약 대기입니다."));
        reservationWaitingRepository.deleteById(id);
    }

    public List<ReservationResponse> findAll() {
        return reservationWaitingRepository.findAll().stream()
                .map(ReservationResponse::from)
                .toList();
    }
}
