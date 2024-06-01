package roomescape.domain.reservation.service;

import org.springframework.stereotype.Service;
import roomescape.domain.member.model.Member;
import roomescape.domain.member.repository.MemberRepository;
import roomescape.domain.reservation.dto.ReservationWaitingDto;
import roomescape.domain.reservation.dto.ReservationWaitingWithOrderDto;
import roomescape.domain.reservation.dto.SaveReservationWaitingRequest;
import roomescape.domain.reservation.model.ReservationDate;
import roomescape.domain.reservation.model.ReservationTime;
import roomescape.domain.reservation.model.ReservationWaiting;
import roomescape.domain.reservation.model.Theme;
import roomescape.domain.reservation.repository.CustomReservationWaitingRepository;
import roomescape.domain.reservation.repository.ReservationRepository;
import roomescape.domain.reservation.repository.ReservationTimeRepository;
import roomescape.domain.reservation.repository.ReservationWaitingRepository;
import roomescape.domain.reservation.repository.ThemeRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class ReservationWaitingService {

    private final ReservationWaitingRepository reservationWaitingRepository;
    private final CustomReservationWaitingRepository customReservationWaitingRepository;
    private final ReservationRepository reservationRepository;
    private final MemberRepository memberRepository;
    private final ThemeRepository themeRepository;
    private final ReservationTimeRepository reservationTimeRepository;

    public ReservationWaitingService(
            final CustomReservationWaitingRepository customReservationWaitingRepository,
            final ReservationWaitingRepository reservationWaitingRepository,
            final ReservationRepository reservationRepository,
            final MemberRepository memberRepository,
            final ThemeRepository themeRepository,
            final ReservationTimeRepository reservationTimeRepository
    ) {
        this.customReservationWaitingRepository = customReservationWaitingRepository;
        this.reservationWaitingRepository = reservationWaitingRepository;
        this.reservationRepository = reservationRepository;
        this.memberRepository = memberRepository;
        this.themeRepository = themeRepository;
        this.reservationTimeRepository = reservationTimeRepository;
    }

    public List<ReservationWaitingDto> getAllReservationWaiting() {
        return reservationWaitingRepository.findAll()
                .stream()
                .map(ReservationWaitingDto::from)
                .toList();
    }

    public List<ReservationWaitingWithOrderDto> getMyReservationWaiting(final Long memberId) {
        return customReservationWaitingRepository.findAllReservationWaitingWithOrdersByMemberId(memberId)
                .stream()
                .map(ReservationWaitingWithOrderDto::from)
                .toList();
    }

    public Long saveReservationWaiting(final SaveReservationWaitingRequest request) {
        final ReservationTime reservationTime = reservationTimeRepository.findById(request.time())
                .orElseThrow(() -> new NoSuchElementException("해당 id의 예약 시간이 존재하지 않습니다."));
        final Theme theme = themeRepository.findById(request.theme())
                .orElseThrow(() -> new NoSuchElementException("해당 id의 테마가 존재하지 않습니다."));
        final Member member = memberRepository.findById(request.memberId())
                .orElseThrow(() -> new NoSuchElementException("해당 id의 회원이 존재하지 않습니다."));

        final ReservationDate reservationDate = new ReservationDate(request.date());

        checkReservationExist(theme, reservationDate, reservationTime);
        checkReservationWaitingAlreadyExist(member, reservationDate, reservationTime, theme);

        final ReservationWaiting reservationWaiting = new ReservationWaiting(
                reservationTime,
                theme,
                member,
                reservationDate,
                LocalDateTime.now()
        );
        return reservationWaitingRepository.save(reservationWaiting).getId();
    }

    private void checkReservationExist(
            final Theme theme,
            final ReservationDate date,
            final ReservationTime reservationTime
    ) {
        if (!reservationRepository.existsByDateAndTime_IdAndTheme_Id(date, reservationTime.getId(), theme.getId())) {
            throw new IllegalStateException("존재하지 않는 예약에 대한 대기 신청을 할 수 없습니다.");
        }
    }

    private void checkReservationWaitingAlreadyExist(
            final Member member,
            final ReservationDate reservationDate,
            final ReservationTime reservationTime,
            final Theme theme
    ) {
        if (reservationWaitingRepository.existsByMemberAndDateAndTimeAndTheme(member, reservationDate, reservationTime, theme)) {
            throw new IllegalStateException("이미 해당 예약 대기가 존재합니다.");
        }
    }

    public void deleteReservationWaiting(final Long reservationWaitingId) {
        reservationWaitingRepository.deleteById(reservationWaitingId);
    }
}
