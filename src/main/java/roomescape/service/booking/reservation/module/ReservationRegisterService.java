package roomescape.service.booking.reservation.module;

import java.time.LocalDate;
import java.time.LocalTime;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.domain.member.Member;
import roomescape.domain.reservation.Reservation;
import roomescape.domain.reservation.Status;
import roomescape.domain.theme.Theme;
import roomescape.domain.time.ReservationTime;
import roomescape.dto.reservation.ReservationRequest;
import roomescape.dto.reservation.ReservationResponse;
import roomescape.exception.ErrorCode;
import roomescape.exception.RoomEscapeException;
import roomescape.repository.MemberRepository;
import roomescape.repository.ReservationRepository;
import roomescape.repository.ReservationTimeRepository;
import roomescape.repository.ThemeRepository;
import roomescape.util.DateUtil;

@Service
@Transactional
public class ReservationRegisterService {

    private final ReservationRepository reservationRepository;
    private final ReservationTimeRepository timeRepository;
    private final ThemeRepository themeRepository;
    private final MemberRepository memberRepository;

    public ReservationRegisterService(ReservationRepository reservationRepository,
                                      ReservationTimeRepository timeRepository,
                                      ThemeRepository themeRepository,
                                      MemberRepository memberRepository
    ) {
        this.reservationRepository = reservationRepository;
        this.timeRepository = timeRepository;
        this.themeRepository = themeRepository;
        this.memberRepository = memberRepository;
    }

    public ReservationResponse registerReservation(ReservationRequest request) {
        Reservation reservation = convertReservation(request);
        validateReservationAvailability(reservation);
        return ReservationResponse.from(reservationRepository.save(reservation));
    }

    private Reservation convertReservation(ReservationRequest request) {
        ReservationTime reservationTime = timeRepository.findByIdOrThrow(request.timeId());
        Theme theme = themeRepository.findByIdOrThrow(request.themeId());
        Member member = memberRepository.findByIdOrThrow(request.memberId());
        return request.toEntity(reservationTime, theme, member, Status.RESERVED);
    }

    private void validateReservationAvailability(Reservation reservation) {
        validateUnPassedDate(reservation.getDate(), reservation.getTime().getStartAt());
        validateReservationNotDuplicate(reservation);
    }

    private void validateUnPassedDate(LocalDate date, LocalTime time) {
        if (DateUtil.isPastDateTime(date, time)) {
            throw new RoomEscapeException(
                    ErrorCode.RESERVATION_NOT_REGISTER_BY_PAST_DATE,
                    "생성 예약 시간 = " + date + " " + time
            );
        }
    }

    private void validateReservationNotDuplicate(Reservation reservation) {
        if (reservationRepository.existsByDateAndTimeIdAndThemeId(
                reservation.getDate(),
                reservation.getTime().getId(),
                reservation.getTheme().getId())
        ) {
            throw new RoomEscapeException(
                    ErrorCode.RESERVATION_NOT_REGISTER_BY_DUPLICATE,
                    "생성 예약 정보 = " + reservation
            );
        }
    }
}
