package roomescape.reservation.service.manager;

import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import roomescape.global.exception.InvalidArgumentException;
import roomescape.global.function.TriFunction;
import roomescape.member.domain.Member;
import roomescape.member.service.MemberQueryService;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.domain.ReservationDate;
import roomescape.reservation.domain.ReservationDateTime;
import roomescape.reservation.domain.ReservationStatus;
import roomescape.reservation.repository.ReservationRepository;
import roomescape.reservation.service.command.ReserveCommand;
import roomescape.theme.domain.Theme;
import roomescape.theme.service.ThemeQueryService;
import roomescape.time.service.ReservationTimeQueryService;

@Component
@RequiredArgsConstructor
public class ReservationManager {

    private final MemberQueryService memberQueryService;
    private final ThemeQueryService themeQueryService;
    private final ReservationTimeQueryService reservationTimeQueryService;
    private final ReservationRepository reservationRepository;

    @Transactional
    public Reservation reserved(ReserveCommand reserveCommand) {
        isAlreadyReservedTime(reserveCommand.date(), reserveCommand.timeId());
        Reservation reserved = reservationFrom(reserveCommand, Reservation::reserve);

        return reservationRepository.save(reserved);
    }

    private void isAlreadyReservedTime(LocalDate date, Long timeId) {
        if (reservationRepository.existsByDateAndTimeIdAndStatus(date, timeId, ReservationStatus.RESERVED)) {
            throw new InvalidArgumentException("이미 예약이 존재하는 시간입니다.");
        }
    }

    @Transactional
    public Reservation waiting(ReserveCommand reserveCommand) {
        Reservation waiting = reservationFrom(reserveCommand, Reservation::waiting);

        return reservationRepository.save(waiting);
    }

    private Reservation reservationFrom(ReserveCommand reserveCommand,
                                        TriFunction<Member, ReservationDateTime, Theme, Reservation> reservationFunction) {
        Member member = memberQueryService.getMember(reserveCommand.memberId());
        ReservationDateTime reservationDateTime = ReservationDateTime.create(new ReservationDate(reserveCommand.date()),
                reservationTimeQueryService.getReservationTime(reserveCommand.timeId()));
        Theme theme = themeQueryService.getTheme(reserveCommand.themeId());

        return reservationFunction.apply(member, reservationDateTime, theme);
    }

    public void delete(Reservation reservation) {
        reservationRepository.delete(reservation);
    }
}
