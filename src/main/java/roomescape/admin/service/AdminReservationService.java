package roomescape.admin.service;

import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.common.exception.DataNotFoundException;
import roomescape.member.domain.Member;
import roomescape.member.repository.MemberRepositoryInterface;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.exception.ReservationNotAllowedException;
import roomescape.reservation.repository.ReservationRepositoryInterface;
import roomescape.theme.domain.Theme;
import roomescape.theme.repository.ThemeRepositoryInterface;
import roomescape.time.domain.ReservationTime;
import roomescape.time.repository.ReservationTimeRepositoryInterface;

@RequiredArgsConstructor
@Service
public class AdminReservationService {

    private final ReservationRepositoryInterface reservationRepository;
    private final ReservationTimeRepositoryInterface reservationTimeRepository;
    private final ThemeRepositoryInterface themeRepository;
    private final MemberRepositoryInterface memberRepository;

    @Transactional
    public Reservation saveByAdmin(final LocalDate date, final Long themeId, final Long timeId, final Long memberId) {
        final ReservationTime reservationTime = reservationTimeRepository.findById(timeId);
        final Theme theme = themeRepository.findById(themeId);
        final Member member = findMemberById(memberId);

        validateExistReservation(date, reservationTime, theme);

        final Reservation reservation = new Reservation(member, date, reservationTime, theme);
        final Reservation savedReservation = reservationRepository.save(reservation);

        return savedReservation;
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
                .orElseThrow(() -> new DataNotFoundException("해당 회원 데이터가 존재하지 않습니다. id = " + memberId));
    }

    private void validateExistReservation(final LocalDate date, final ReservationTime reservationTime,
                                          final Theme theme) {
        if (reservationRepository.existsByDateAndTimeAndTheme(date, reservationTime, theme)) {
            throw new ReservationNotAllowedException("이미 예약이 존재해 예약을 할 수 없습니다.");
        }
    }
}
