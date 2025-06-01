package roomescape.service.query;

import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.domain.Member;
import roomescape.domain.Reservation;
import roomescape.domain.ReservationTime;
import roomescape.domain.Theme;
import roomescape.dto.business.WaitingWithRank;
import roomescape.dto.response.ReservationResponse;
import roomescape.dto.response.ReservationStatusResponse;
import roomescape.exception.NotFoundException;
import roomescape.repository.MemberRepository;
import roomescape.repository.ReservationRepository;
import roomescape.repository.WaitingRepository;

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

    public List<ReservationResponse> findAllReservations() {
        List<Reservation> reservations = reservationRepository.findAll();
        return reservations.stream()
                .map(ReservationResponse::new)
                .toList();
    }

    public List<ReservationResponse> findAllReservationsByMember(long memberId) {
        Member savedMember = getMemberById(memberId);
        List<Reservation> reservations = reservationRepository.findByMember(savedMember);
        return reservations.stream()
                .map(ReservationResponse::new)
                .toList();
    }

    public ReservationStatusResponse findAllReservationStatusByMember(long memberId) {
        Member savedMember = getMemberById(memberId);
        List<Reservation> reservations = reservationRepository.findByMember(savedMember);
        List<WaitingWithRank> waitingWithRanks = waitingRepository.findWithRankingByMember(savedMember.getId());
        return ReservationStatusResponse.createReservationStatusResponses(reservations, waitingWithRanks);
    }

    public List<ReservationResponse> findReservationsByFilter(
            long memberId, long themeId, LocalDate from, LocalDate to
    ) {
        List<Reservation> reservations = reservationRepository.findReservationsByFilter(memberId, themeId, from, to);
        return reservations.stream()
                .map(ReservationResponse::new)
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
