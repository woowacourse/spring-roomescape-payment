package roomescape.service.booking.waiting.module;

import java.time.LocalDate;
import java.time.LocalTime;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.domain.member.Member;
import roomescape.domain.reservation.Reservation;
import roomescape.domain.reservation.Status;
import roomescape.domain.theme.Theme;
import roomescape.domain.time.ReservationTime;
import roomescape.domain.waiting.Waiting;
import roomescape.dto.reservation.ReservationRequest;
import roomescape.exception.ErrorCode;
import roomescape.exception.RoomEscapeException;
import roomescape.repository.MemberRepository;
import roomescape.repository.ReservationRepository;
import roomescape.repository.ReservationTimeRepository;
import roomescape.repository.ThemeRepository;
import roomescape.repository.WaitingRepository;
import roomescape.util.DateUtil;

@Service
@Transactional
public class WaitingRegisterService {

    private final WaitingRepository waitingRepository;
    private final ReservationRepository reservationRepository;
    private final ReservationTimeRepository timeRepository;
    private final ThemeRepository themeRepository;
    private final MemberRepository memberRepository;

    public WaitingRegisterService(WaitingRepository waitingRepository,
                                  ReservationRepository reservationRepository,
                                  ReservationTimeRepository timeRepository,
                                  ThemeRepository themeRepository,
                                  MemberRepository memberRepository
    ) {
        this.waitingRepository = waitingRepository;
        this.reservationRepository = reservationRepository;
        this.timeRepository = timeRepository;
        this.themeRepository = themeRepository;
        this.memberRepository = memberRepository;
    }

    public Long registerWaiting(ReservationRequest request) {
        Reservation reservation = convertReservation(request);
        validateAddableWaiting(reservation);
        Reservation savedReservation = reservationRepository.save(reservation);
        addWaiting(savedReservation);
        return savedReservation.getId();
    }

    private Reservation convertReservation(ReservationRequest request) {
        ReservationTime reservationTime = timeRepository.findByIdOrThrow(request.timeId());
        Theme theme = themeRepository.findByIdOrThrow(request.themeId());
        Member member = memberRepository.findByIdOrThrow(request.memberId());
        return request.toEntity(reservationTime, theme, member, Status.WAITING);
    }

    private void addWaiting(Reservation savedReservation) {
        int waitingOrder = reservationRepository.countByDateAndTimeIdAndThemeIdAndStatus(
                savedReservation.getDate(),
                savedReservation.getTime().getId(),
                savedReservation.getTheme().getId(),
                savedReservation.getStatus()
        );
        waitingRepository.save(new Waiting(savedReservation, waitingOrder));
    }

    private void validateAddableWaiting(Reservation reservation) {
        validateUnPassedDate(reservation.getDate(), reservation.getTime().getStartAt());
        validateWaitingDuplicate(reservation);
    }

    private void validateUnPassedDate(LocalDate date, LocalTime time) {
        if (DateUtil.isPastDateTime(date, time)) {
            throw new RoomEscapeException(
                    ErrorCode.RESERVATION_NOT_REGISTER_BY_PAST_DATE,
                    "생성 예약 시간 = " + date + " " + time
            );
        }
    }

    private void validateWaitingDuplicate(Reservation reservation) {
        if (reservationRepository.existsByDateAndTimeIdAndThemeIdAndMemberId(
                reservation.getDate(),
                reservation.getTime().getId(),
                reservation.getTheme().getId(),
                reservation.getMember().getId())
        ) {
            throw new RoomEscapeException(
                    ErrorCode.WAITING_NOT_REGISTER_BY_DUPLICATE,
                    "생성 예약 대기 정보 = " + reservation
            );
        }
    }
}
