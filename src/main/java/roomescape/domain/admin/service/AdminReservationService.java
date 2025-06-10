package roomescape.domain.admin.service;

import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.domain.member.entity.Member;
import roomescape.domain.member.exception.MemberNotFoundException;
import roomescape.domain.member.repository.MemberRepositoryInterface;
import roomescape.domain.reservation.entity.Reservation;
import roomescape.domain.reservation.exception.DuplicateReservationExistenceException;
import roomescape.domain.reservation.repository.ReservationRepositoryInterface;
import roomescape.domain.theme.entity.Theme;
import roomescape.domain.theme.repository.ThemeRepositoryInterface;
import roomescape.domain.time.entity.ReservationTime;
import roomescape.domain.time.repository.ReservationTimeRepositoryInterface;

@RequiredArgsConstructor
@Service
public class AdminReservationService {

    private final ReservationRepositoryInterface reservationRepository;
    private final ReservationTimeRepositoryInterface reservationTimeRepository;
    private final ThemeRepositoryInterface themeRepository;
    private final MemberRepositoryInterface memberRepository;

    @Transactional
    public Reservation save(final LocalDate date, final Long themeId, final Long timeId, final Long memberId) {
        final ReservationTime reservationTime = reservationTimeRepository.findById(timeId);
        final Theme theme = themeRepository.findById(themeId);
        final Member member = findMemberById(memberId);

        validateExistReservation(date, reservationTime, theme);

        final Reservation reservation = new Reservation(member, date, reservationTime, theme);
        final Reservation savedReservation = reservationRepository.save(reservation);

        return savedReservation;
    }

    @Transactional
    public void deleteById(final Long reservationId) {
        reservationRepository.deleteById(reservationId);
    }

    @Transactional(readOnly = true)
    public Reservation findById(final Long reservationId) {
        return reservationRepository.findById(reservationId);
    }

    @Transactional(readOnly = true)
    public List<Reservation> findByInFromTo(final Long themeId, final Long memberId, final LocalDate dateFrom,
                                            final LocalDate dateTo) {
        final Theme theme = themeRepository.findById(themeId);
        final Member member = findMemberById(memberId);

        return reservationRepository.findByThemeAndMemberAndDateBetween(theme, member, dateFrom, dateTo);
    }

    private Member findMemberById(final Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberNotFoundException("멤버를 찾을 수 없습니다. memberId: " + memberId));
    }

    private void validateExistReservation(
            final LocalDate date,
            final ReservationTime reservationTime,
            final Theme theme) {
        if (reservationRepository.existsByDateAndTimeAndTheme(date, reservationTime, theme)) {
            throw new DuplicateReservationExistenceException("해당 날짜, 시간 그리고 테마에 대한 예약 정보가 존재합니다.");
        }
    }
}
