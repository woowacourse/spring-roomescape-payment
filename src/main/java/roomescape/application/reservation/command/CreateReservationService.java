package roomescape.application.reservation.command;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.application.payment.TossPaymentService;
import roomescape.application.reservation.command.dto.CreateReservationCommand;
import roomescape.application.reservation.command.dto.CreateReservationWithPaymentCommand;
import roomescape.domain.member.Member;
import roomescape.domain.member.repository.MemberRepository;
import roomescape.domain.reservation.Reservation;
import roomescape.domain.reservation.ReservationTime;
import roomescape.domain.reservation.Theme;
import roomescape.domain.reservation.repository.ReservationRepository;
import roomescape.domain.reservation.repository.ReservationTimeRepository;
import roomescape.domain.reservation.repository.ThemeRepository;
import roomescape.infrastructure.error.exception.MemberException;
import roomescape.infrastructure.error.exception.ReservationException;
import roomescape.infrastructure.error.exception.ReservationTimeException;
import roomescape.infrastructure.error.exception.ThemeException;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class CreateReservationService {

    private final ReservationRepository reservationRepository;
    private final ReservationTimeRepository reservationTimeRepository;
    private final ThemeRepository themeRepository;
    private final MemberRepository memberRepository;
    private final TossPaymentService tossPaymentService;
    private final Clock clock;

    public Long reserve(final CreateReservationCommand command) {
        final Member member = getMember(command.memberId());
        final ReservationTime time = getTime(command.timeId());
        final Theme theme = getTheme(command.themeId());
        validateDuplicateReservation(command.date(), time, theme);
        final Reservation reservation = new Reservation(member, command.date(), time, theme);
        reservation.validateReservable(LocalDateTime.now(clock));
        final Reservation savedReservation = reservationRepository.save(reservation);
        return savedReservation.getId();
    }

    public Long reserve(final CreateReservationWithPaymentCommand command) {
        tossPaymentService.approve(command.toPaymentCommand());
        return reserve(command.toCreateWithoutPaymentCommand());
    }

    private Member getMember(final Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException("존재하지 않는 회원입니다."));
    }

    private ReservationTime getTime(final Long timeId) {
        return reservationTimeRepository.findById(timeId)
                .orElseThrow(() -> new ReservationTimeException("존재하지 않는 예약 시간입니다."));
    }

    private Theme getTheme(final Long themeId) {
        return themeRepository.findById(themeId)
                .orElseThrow(() -> new ThemeException("존재하지 않는 테마입니다."));
    }

    private void validateDuplicateReservation(final LocalDate date, final ReservationTime time, final Theme theme) {
        final boolean duplicated = reservationRepository.existsByDateAndTimeIdAndThemeId(date, time.getId(), theme.getId());
        if (duplicated) {
            throw new ReservationException("날짜와 시간이 중복된 예약이 존재합니다.");
        }
    }
}
