package roomescape.infra.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import roomescape.domain.member.Member;
import roomescape.domain.reservation.Reservation;
import roomescape.domain.reservation.ReservationRepository;
import roomescape.domain.reservation.ReservationWithRank;
import roomescape.domain.reservation.Status;
import roomescape.domain.reservationdetail.ReservationDetail;
import roomescape.exception.reservation.NotFoundReservationException;

@Repository
@RequiredArgsConstructor
public class ReservationRepositoryImpl implements ReservationRepository {
    private final ReservationJpaRepository reservationJpaRepository;

    @Override
    public Reservation save(Reservation reservation) {
        return reservationJpaRepository.save(reservation);
    }

    @Override
    public Reservation getReservation(Long id) {
        return findReservation(id)
                .orElseThrow(NotFoundReservationException::new);
    }

    @Override
    public Optional<Reservation> findReservation(Long id) {
        return reservationJpaRepository.findById(id);
    }

    @Override
    public List<Reservation> findAll() {
        return reservationJpaRepository.findAll();
    }

    @Override
    public List<Reservation> findAll(Status status) {
        return reservationJpaRepository.findAllByStatus(status);
    }

    @Override
    public List<Reservation> findReservation(LocalDate start, LocalDate end, Long memberId, Long themeId) {
        return reservationJpaRepository.findByPeriodAndThemeAndMember(start, end, memberId, themeId);
    }

    @Override
    public Optional<Reservation> findNextWaiting(ReservationDetail detail) {
        return reservationJpaRepository.findNextWaitingReservation(detail);
    }

    @Override
    public List<ReservationWithRank> findWithRank(Long memberId) {
        return reservationJpaRepository.findWithRank(memberId);
    }

    @Override
    public boolean existsReservation(ReservationDetail detail, Member member, List<Status> status) {
        return reservationJpaRepository.existsByDetailAndMemberAndStatusIn(detail, member, status);
    }

    @Override
    public boolean existsReservation(ReservationDetail detail, List<Status> status) {
        return reservationJpaRepository.existsByDetailAndStatusIn(detail, status);
    }
}
