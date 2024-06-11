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
import roomescape.exception.RoomEscapeException;
import roomescape.repository.MemberRepository;
import roomescape.repository.ReservationRepository;
import roomescape.repository.ReservationTimeRepository;
import roomescape.repository.ThemeRepository;
import roomescape.util.DateUtil;

@Service
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

    @Transactional
    public Reservation registerReservation(ReservationRequest request) {
        Reservation reservation = convertReservation(request);
        validateReservationAvailability(reservation);
        return reservationRepository.save(reservation);
    }

    private Reservation convertReservation(ReservationRequest request) {
        ReservationTime reservationTime = findReservationTime(request.timeId());
        Theme theme = findTheme(request.themeId());
        Member member = findMember(request.memberId());
        return request.toEntity(reservationTime, theme, member, Status.RESERVED);
    }

    private ReservationTime findReservationTime(Long timeId) {
        return timeRepository.findById(timeId)
                .orElseThrow(() -> new RoomEscapeException(
                        "잘못된 예약시간 정보 입니다.",
                        "time_id : " + timeId
                ));
    }

    private Theme findTheme(Long themeId) {
        return themeRepository.findById(themeId)
                .orElseThrow(() -> new RoomEscapeException(
                        "잘못된 테마 정보 입니다.",
                        "theme_id : " + themeId
                ));
    }

    private Member findMember(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new RoomEscapeException(
                        "잘못된 사용자 정보 입니다.",
                        "member_id : " + memberId
                ));
    }

    private void validateReservationAvailability(Reservation reservation) {
        validateUnPassedDate(reservation.getDate(), reservation.getTime().getStartAt());
        validateReservationNotDuplicate(reservation);
    }

    private void validateUnPassedDate(LocalDate date, LocalTime time) {
        if (DateUtil.isPastDateTime(date, time)) {
            throw new RoomEscapeException(
                    "지나간 날짜와 시간은 예약이 불가능합니다.",
                    "생성 예약 시간 : " + date + " " + time
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
                    "해당 시간에 동일한 테마가 예약되어있어 예약이 불가능합니다.",
                    "생성 예약 정보 : " + reservation
            );
        }
    }
}
