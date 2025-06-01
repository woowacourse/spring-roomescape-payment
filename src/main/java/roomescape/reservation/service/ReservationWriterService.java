package roomescape.reservation.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.common.util.time.DateTime;
import roomescape.member.domain.Member;
import roomescape.member.domain.MemberRepository;
import roomescape.member.exception.MemberNotFound;
import roomescape.payment.service.PaymentService;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.domain.ReservationRepository;
import roomescape.reservation.domain.Status;
import roomescape.reservation.exception.ReservationException;
import roomescape.reservation.presentation.dto.ReservationRequest;
import roomescape.reservationTime.domain.ReservationTime;
import roomescape.reservationTime.domain.ReservationTimeRepository;
import roomescape.reservationTime.exception.ReservationTimeException;
import roomescape.theme.domain.Theme;
import roomescape.theme.domain.ThemeRepository;
import roomescape.theme.exception.ThemeException;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReservationWriterService {

    private final DateTime dateTime;
    private final ReservationRepository reservationRepository;
    private final ReservationTimeRepository reservationTimeRepository;
    private final ThemeRepository themeRepository;
    private final MemberRepository memberRepository;

    public ReservationWriterService(
        final DateTime dateTime,
        final ReservationRepository reservationRepository,
        final ReservationTimeRepository reservationTimeRepository,
        final ThemeRepository themeRepository,
        final MemberRepository memberRepository
    ) {
        this.dateTime = dateTime;
        this.reservationRepository = reservationRepository;
        this.reservationTimeRepository = reservationTimeRepository;
        this.themeRepository = themeRepository;
        this.memberRepository = memberRepository;
    }

    @Transactional
    public Reservation saveReservation(final ReservationRequest request, final Long memberId) {
        ReservationTime time = reservationTimeRepository.findById(request.timeId())
            .orElseThrow(() -> new ReservationTimeException("예약 시간을 찾을 수 없습니다."));
        Theme theme = themeRepository.findById(request.themeId())
            .orElseThrow(() -> new ThemeException("테마를 찾을 수 없습니다."));
        Member member = memberRepository.findById(memberId)
            .orElseThrow(() -> new MemberNotFound("멤버를 찾을 수 없습니다."));

        List<Reservation> reservations = reservationRepository.findAllByDateAndThemeId(request.date(), request.themeId());
        validateExistDuplicateReservation(reservations, time);

        Reservation reservation = Reservation.createWithoutId(request.date(), time, theme, member, Status.RESERVED);
        validateCanReserveDateTime(reservation, dateTime.now());

        return reservationRepository.save(reservation);
    }

    private void validateExistDuplicateReservation(final List<Reservation> reservations, final ReservationTime time) {
        boolean isBooked = reservations.stream()
            .anyMatch(reservation -> reservation.isSameTime(time));

        if (isBooked) {
            throw new ReservationException("이미 예약이 존재합니다.");
        }
    }

    private void validateCanReserveDateTime(final Reservation reservation, final LocalDateTime now) {
        if (reservation.isCannotReserveDateTime(now)) {
            throw new ReservationException("예약할 수 없는 날짜와 시간입니다.");
        }
    }
}
