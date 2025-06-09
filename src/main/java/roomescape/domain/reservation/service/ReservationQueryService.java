package roomescape.domain.reservation.service;

import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.domain.member.domain.Member;
import roomescape.domain.member.repository.MemberRepository;
import roomescape.domain.reservation.domain.Reservation;
import roomescape.domain.reservation.repository.ReservationRepository;
import roomescape.domain.reservation.response.FindAllReservationResponse;
import roomescape.domain.reservation.response.FindReservationsByFilter;
import roomescape.domain.reservation.response.ReservationStatusResponse;
import roomescape.domain.theme.domain.Theme;
import roomescape.domain.time.domain.ReservationTime;
import roomescape.domain.waiting.dto.WaitingWithRank;
import roomescape.domain.waiting.repository.WaitingRepository;
import roomescape.exception.NotFoundException;

@Service
@Transactional(readOnly = true)
public class ReservationQueryService {

    private final ReservationRepository reservationRepository;
    private final MemberRepository memberRepository;
    private final WaitingRepository waitingRepository;

    public ReservationQueryService(ReservationRepository reservationRepository, MemberRepository memberRepository,
            WaitingRepository waitingRepository) {
        this.reservationRepository = reservationRepository;
        this.memberRepository = memberRepository;
        this.waitingRepository = waitingRepository;
    }

    public List<FindAllReservationResponse> findAllReservations() {
        List<Reservation> reservations = reservationRepository.findAll();
        return reservations.stream()
                .map(FindAllReservationResponse::new)
                .toList();
    }

    public ReservationStatusResponse findAllReservationStatusByMember(long memberId) {
        Member savedMember = getMemberById(memberId);
        List<Reservation> reservations = reservationRepository.findByMember(savedMember);
        List<WaitingWithRank> waitingWithRanks = waitingRepository.findWithRankingByMember(savedMember.getId());
        return ReservationStatusResponse.createReservationStatusResponses(reservations, waitingWithRanks);
    }

    public List<FindReservationsByFilter> findReservationsByFilter(
            long memberId, long themeId, LocalDate from, LocalDate to
    ) {
        List<Reservation> reservations = reservationRepository.findReservationsByFilter(memberId, themeId, from, to);
        return reservations.stream()
                .map(FindReservationsByFilter::new)
                .toList();
    }

    public Reservation getReservationById(long reservationId) {
        return reservationRepository.findById(reservationId)
                .orElseThrow(() -> new NotFoundException("ID에 해당하는 예약을 찾을 수 없습니다."));
    }

    public Member getMemberById(long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new NotFoundException("ID에 해당하는 회원을 찾을 수 없습니다."));
    }

    public boolean existsAlreadyReservation(Theme theme, LocalDate date, ReservationTime time) {
        return reservationRepository.existsByThemeAndDateAndReservationTime(theme, date, time);
    }

    public boolean existsReservationInTime(ReservationTime time) {
        return reservationRepository.existsByReservationTime(time);
    }

    public boolean existsReservationInTheme(Theme theme) {
        return reservationRepository.existsByTheme(theme);
    }
}
