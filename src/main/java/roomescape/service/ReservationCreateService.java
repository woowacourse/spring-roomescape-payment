package roomescape.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.domain.member.Member;
import roomescape.domain.member.MemberRepository;
import roomescape.domain.reservation.Reservation;
import roomescape.domain.reservation.ReservationRepository;
import roomescape.domain.reservation.ReservationTime;
import roomescape.domain.reservation.ReservationTimeRepository;
import roomescape.domain.reservation.Status;
import roomescape.domain.theme.Theme;
import roomescape.domain.theme.ThemeRepository;
import roomescape.dto.LoginMember;
import roomescape.dto.request.reservation.AdminReservationRequest;
import roomescape.dto.request.reservation.ReservationRequest;
import roomescape.dto.request.reservation.WaitingRequest;
import roomescape.dto.response.reservation.ReservationResponse;
import roomescape.exception.RoomescapeException;

@Service
public class ReservationCreateService {
    private final ReservationRepository reservationRepository;
    private final ReservationTimeRepository reservationTimeRepository;
    private final ThemeRepository themeRepository;
    private final MemberRepository memberRepository;

    public ReservationCreateService(
            ReservationRepository reservationRepository,
            ReservationTimeRepository reservationTimeRepository,
            ThemeRepository themeRepository,
            MemberRepository memberRepository
    ) {
        this.reservationRepository = reservationRepository;
        this.reservationTimeRepository = reservationTimeRepository;
        this.themeRepository = themeRepository;
        this.memberRepository = memberRepository;
    }

    @Transactional
    public ReservationResponse saveReservationByClient(LoginMember loginMember, ReservationRequest reservationRequest) {
        Reservation reservation = createReservation(
                loginMember.id(),
                reservationRequest.date(),
                reservationRequest.timeId(),
                reservationRequest.themeId()
        );
        return ReservationResponse.from(reservationRepository.save(reservation));
    }

    @Transactional
    public ReservationResponse saveWaitingByClient(LoginMember loginMember, WaitingRequest waitingRequest) {
        Reservation reservation = createWaiting(
                loginMember.id(),
                waitingRequest.date(),
                waitingRequest.timeId(),
                waitingRequest.themeId()
        );
        return ReservationResponse.from(reservationRepository.save(reservation));
    }

    @Transactional
    public ReservationResponse saveReservationByAdmin(AdminReservationRequest adminReservationRequest) {
        Reservation reservation = createReservation(
                adminReservationRequest.memberId(),
                adminReservationRequest.date(),
                adminReservationRequest.timeId(),
                adminReservationRequest.themeId()
        );
        return ReservationResponse.from(reservationRepository.save(reservation));
    }

    private Reservation createReservation(Long memberId, LocalDate date, Long timeId, Long themeId) {
        ReservationTime reservationTime = getReservationTime(timeId);
        LocalDateTime dateTime = LocalDateTime.of(date, reservationTime.getStartAt());
        validateRequestDateAfterCurrentTime(dateTime);
        validateUniqueReservation(date, timeId, themeId);
        return new Reservation(getMember(memberId), date, reservationTime, getTheme(themeId), Status.RESERVATION);
    }

    private Reservation createWaiting(Long memberId, LocalDate date, Long timeId, Long themeId) {
        ReservationTime reservationTime = getReservationTime(timeId);
        LocalDateTime dateTime = LocalDateTime.of(date, reservationTime.getStartAt());
        validateRequestDateAfterCurrentTime(dateTime);
        validateIsExistMyReservation(date, timeId, themeId, memberId);
        validateReservationNotExist(date, timeId, themeId);
        return new Reservation(getMember(memberId), date, reservationTime, getTheme(themeId), Status.WAITING);
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
        LocalDateTime currentTime = LocalDateTime.now();
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
}
