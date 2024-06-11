package roomescape.domain.reservation;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.springframework.http.HttpStatus;
import roomescape.domain.DomainService;
import roomescape.domain.member.Member;
import roomescape.domain.member.MemberRepository;
import roomescape.domain.reservationwaiting.ReservationWaiting;
import roomescape.domain.reservationwaiting.ReservationWaitingRepository;
import roomescape.domain.theme.Theme;
import roomescape.domain.theme.ThemeRepository;
import roomescape.exception.RoomescapeException;

@DomainService
public class ReservationFactory {
    private final ReservationRepository reservationRepository;
    private final ReservationWaitingRepository reservationWaitingRepository;
    private final ReservationTimeRepository reservationTimeRepository;
    private final ThemeRepository themeRepository;
    private final MemberRepository memberRepository;
    private final Clock clock;

    public ReservationFactory(
            ReservationRepository reservationRepository,
            ReservationWaitingRepository reservationWaitingRepository,
            ReservationTimeRepository reservationTimeRepository,
            ThemeRepository themeRepository,
            MemberRepository memberRepository,
            Clock clock
    ) {
        this.reservationRepository = reservationRepository;
        this.reservationWaitingRepository = reservationWaitingRepository;
        this.reservationTimeRepository = reservationTimeRepository;
        this.themeRepository = themeRepository;
        this.memberRepository = memberRepository;
        this.clock = clock;
    }

    public Reservation createReservation(Long memberId, LocalDate date, Long timeId, Long themeId) {
        ReservationTime reservationTime = getReservationTime(timeId);
        LocalDateTime dateTime = LocalDateTime.of(date, reservationTime.getStartAt());
        validateRequestDateAfterCurrentTime(dateTime);
        validateUniqueReservation(date, timeId, themeId);
        validateAlreadyWaiting(date, timeId, themeId, memberId);
        return new Reservation(getMember(memberId), date, reservationTime, getTheme(themeId), ReservationStatus.RESERVATION);
    }

    public ReservationWaiting createWaiting(Long memberId, LocalDate date, Long timeId, Long themeId) {
        ReservationTime reservationTime = getReservationTime(timeId);
        LocalDateTime dateTime = LocalDateTime.of(date, reservationTime.getStartAt());
        validateRequestDateAfterCurrentTime(dateTime);
        validateIsExistMyReservation(date, timeId, themeId, memberId);
        validateReservationNotExist(date, timeId, themeId);
        validateAlreadyWaiting(date, timeId, themeId, memberId);
        return new ReservationWaiting(getMember(memberId), date, reservationTime, getTheme(themeId));
    }

    private ReservationTime getReservationTime(Long timeId) {
        return reservationTimeRepository.findById(timeId)
                .orElseThrow(() -> new RoomescapeException(HttpStatus.NOT_FOUND, "존재하지 않는 예약 시간입니다."));
    }

    private Theme getTheme(Long themeId) {
        return themeRepository.findById(themeId)
                .orElseThrow(() -> new RoomescapeException(HttpStatus.NOT_FOUND, "존재하지 않는 테마입니다."));
    }

    private Member getMember(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new RoomescapeException(HttpStatus.NOT_FOUND, "존재하지 않는 회원입니다."));
    }

    private void validateRequestDateAfterCurrentTime(LocalDateTime dateTime) {
        LocalDateTime currentTime = LocalDateTime.now(clock);
        if (dateTime.isBefore(currentTime)) {
            throw new RoomescapeException(HttpStatus.BAD_REQUEST, "현재 시간보다 과거로 예약할 수 없습니다.");
        }
    }

    private void validateUniqueReservation(LocalDate date, Long timeId, Long themeId) {
        if (reservationRepository.existsByDateAndTimeIdAndThemeId(date, timeId, themeId)) {
            throw new RoomescapeException(HttpStatus.BAD_REQUEST, "예약이 존재합니다.");
        }
    }

    private void validateReservationNotExist(LocalDate date, Long timeId, Long themeId) {
        if (!reservationRepository.existsByDateAndTimeIdAndThemeId(date, timeId, themeId)) {
            throw new RoomescapeException(HttpStatus.BAD_REQUEST, "예약이 존재하지 않아서 예약 대기를 할 수 없습니다.");
        }
    }

    private void validateIsExistMyReservation(LocalDate date, Long timeId, Long themeId, Long memberId) {
        if (reservationRepository.existsByDateAndTimeIdAndThemeIdAndMemberId(date, timeId, themeId, memberId)) {
            throw new RoomescapeException(HttpStatus.CONFLICT, "이미 예약을 했습니다.");
        }
    }

    private void validateAlreadyWaiting(LocalDate date, long timeId, long themeId, long memberId) {
        if (reservationWaitingRepository.existsByDateAndTimeIdAndThemeIdAndMemberId(date, timeId, themeId, memberId)) {
            throw new RoomescapeException(HttpStatus.BAD_REQUEST, "이미 예약 대기 중입니다.");
        }
    }
}
