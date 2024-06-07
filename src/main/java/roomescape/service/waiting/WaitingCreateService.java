package roomescape.service.waiting;

import org.springframework.stereotype.Service;
import roomescape.domain.member.Member;
import roomescape.domain.member.MemberRepository;
import roomescape.domain.reservation.Reservation;
import roomescape.domain.reservation.ReservationRepository;
import roomescape.domain.reservation.ReservationStatus;
import roomescape.domain.reservationdetail.ReservationDetail;
import roomescape.domain.reservationdetail.ReservationDetailRepository;
import roomescape.domain.schedule.ReservationDate;
import roomescape.domain.schedule.ReservationTime;
import roomescape.domain.schedule.ReservationTimeRepository;
import roomescape.domain.schedule.Schedule;
import roomescape.domain.theme.Theme;
import roomescape.domain.theme.ThemeRepository;
import roomescape.exception.InvalidMemberException;
import roomescape.exception.InvalidReservationException;
import roomescape.service.reservation.dto.ReservationResponse;
import roomescape.service.waiting.dto.WaitingRequest;

@Service
public class WaitingCreateService {

    private final ReservationRepository reservationRepository;
    private final ReservationTimeRepository reservationTimeRepository;
    private final ThemeRepository themeRepository;
    private final MemberRepository memberRepository;
    private final ReservationDetailRepository reservationDetailRepository;

    public WaitingCreateService(
        ReservationRepository reservationRepository,
        ReservationTimeRepository reservationTimeRepository,
        ThemeRepository themeRepository,
        MemberRepository memberRepository,
        ReservationDetailRepository reservationDetailRepository
    ) {
        this.reservationRepository = reservationRepository;
        this.reservationTimeRepository = reservationTimeRepository;
        this.themeRepository = themeRepository;
        this.memberRepository = memberRepository;
        this.reservationDetailRepository = reservationDetailRepository;
    }

    public ReservationResponse createWaiting(WaitingRequest waitingRequest, long memberId) {
        ReservationDate reservationDate = ReservationDate.of(waitingRequest.date());
        ReservationTime reservationTime = findTimeById(waitingRequest.timeId());
        Theme theme = findThemeById(waitingRequest.themeId());
        Member member = findMemberById(memberId);
        ReservationDetail reservationDetail = getReservationDetail(reservationDate, reservationTime, theme);
        validateDuplication(reservationDetail, member);

        Reservation reservation = reservationRepository.save(new Reservation(member, reservationDetail, ReservationStatus.WAITING));
        return new ReservationResponse(reservation);
    }

    private ReservationTime findTimeById(long timeId) {
        return reservationTimeRepository.findById(timeId)
            .orElseThrow(() -> new InvalidReservationException("더이상 존재하지 않는 시간입니다."));
    }

    private Theme findThemeById(long themeId) {
        return themeRepository.findById(themeId)
            .orElseThrow(() -> new InvalidReservationException("더이상 존재하지 않는 테마입니다."));
    }

    private Member findMemberById(long memberId) {
        return memberRepository.findById(memberId)
            .orElseThrow(() -> new InvalidMemberException("존재하지 않는 회원입니다."));
    }

    private ReservationDetail getReservationDetail(ReservationDate reservationDate, ReservationTime reservationTime, Theme theme) {
        Schedule schedule = new Schedule(reservationDate, reservationTime);
        return reservationDetailRepository.findByScheduleAndTheme(schedule, theme)
            .orElseGet(() -> reservationDetailRepository.save(new ReservationDetail(schedule, theme)));
    }

    private void validateDuplication(ReservationDetail reservationDetail, Member member) {
        if (reservationRepository.existsByDetailIdAndMemberId(reservationDetail.getId(), member.getId())) {
            throw new InvalidReservationException("이미 예약(대기) 상태입니다.");
        }
        if (!reservationRepository.existsByDetailIdAndStatus(reservationDetail.getId(), ReservationStatus.RESERVED)) {
            throw new InvalidReservationException("존재하는 예약이 없습니다. 예약으로 다시 시도해주세요.");
        }
    }
}
