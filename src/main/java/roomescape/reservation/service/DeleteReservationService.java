package roomescape.reservation.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.auth.service.dto.LoginMember;
import roomescape.common.exception.ForbiddenException;
import roomescape.common.exception.NotFoundException;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.repository.ReservationRepository;
import roomescape.waiting.domain.ReservationInformation;
import roomescape.waiting.domain.Waiting;
import roomescape.waiting.repository.WaitingRepository;

@Service
public class DeleteReservationService {

    private final ReservationRepository reservationRepository;
    private final WaitingRepository waitingRepository;

    public DeleteReservationService(
            ReservationRepository reservationRepository,
            WaitingRepository waitingRepository
    ) {
        this.reservationRepository = reservationRepository;
        this.waitingRepository = waitingRepository;
    }

    @Transactional
    public void delete(final Long id, LoginMember loginMember) {
        Reservation reservation = getAuthorizedReservation(id, loginMember);
        reservationRepository.deleteById(id);
        reservationRepository.flush(); // TODO: identity 키 생성을 위한 insert 과정에서 unique key 충돌 문제 발생 방지 (다른 방법 모색)
        promoteFirstWaitingFor(reservation);
    }

    private Reservation getAuthorizedReservation(final Long id, LoginMember loginMember) {
        return switch (loginMember.role()) {
            case MEMBER -> reservationRepository.findByIdAndMemberId(id, loginMember.id())
                    .orElseThrow(() -> new ForbiddenException("삭제 권한이 없습니다."));
            case ADMIN -> reservationRepository.findById(id)
                    .orElseThrow(() -> new NotFoundException("존재하지 않는 예약입니다."));
        };
    }

    private void promoteFirstWaitingFor(Reservation reservation) {
        Waiting firstWaiting = waitingRepository.findFirstByReservationInfo(ReservationInformation.of(reservation));
        if (firstWaiting != null) {
            waitingRepository.delete(firstWaiting);
            reservationRepository.save(Reservation.of(firstWaiting));
        }
    }
}
