package roomescape.reservation;

import org.springframework.stereotype.Service;
import roomescape.member.LoginMember;
import roomescape.member.Member;
import roomescape.member.MemberService;
import roomescape.theme.Theme;
import roomescape.theme.ThemeRepository;
import roomescape.time.Time;
import roomescape.time.TimeRepository;
import roomescape.waiting.WaitingRepository;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

@Service
public class ReservationService {
    private ReservationRepository reservationRepository;
    private WaitingRepository waitingRepository;
    private MemberService memberService;
    private TimeRepository timeRepository;
    private ThemeRepository themeRepository;

    public ReservationService(ReservationRepository reservationRepository, WaitingRepository waitingRepository, MemberService memberService, TimeRepository timeRepository, ThemeRepository themeRepository) {
        this.reservationRepository = reservationRepository;
        this.waitingRepository = waitingRepository;
        this.memberService = memberService;
        this.timeRepository = timeRepository;
        this.themeRepository = themeRepository;
    }

    public ReservationResponse save(LoginMember loginMember, ReservationRequest reservationRequest) {
        Time time = timeRepository.findById(reservationRequest.getTime()).orElseThrow(RuntimeException::new);
        Theme theme = themeRepository.findById(reservationRequest.getTheme()).orElseThrow(RuntimeException::new);
        Member member = memberService.findMemberById(loginMember.memberId);

        reservationRepository.findByThemeIdAndDateAndTimeId(theme.getId(), reservationRequest.getDate(), time.getId()).stream()
                .filter(it -> it.getMember().getId().equals(member.getId()))
                .findAny()
                .ifPresent(it -> {
                    throw new IllegalArgumentException("이미 예약된 시간입니다.");
                });

        Reservation reservation = reservationRepository.save(new Reservation(member, reservationRequest.getName(), reservationRequest.getDate(), time, theme));

        return new ReservationResponse(reservation.getId(), reservation.getDisplayName(), reservation.getTheme().getName(), reservation.getDate(), reservation.getTime().getValue());
    }

    public void deleteById(Long id) {
        reservationRepository.deleteById(id);
    }

    public List<ReservationResponse> findAll() {
        return reservationRepository.findAll().stream()
                .map(it -> new ReservationResponse(it.getId(), it.getDisplayName(), it.getTheme().getName(), it.getDate(), it.getTime().getValue()))
                .toList();
    }

    public List<MyReservationResponse> findMine(LoginMember loginMember) {
        List<MyReservationResponse> reservations = reservationRepository.findByMemberId(loginMember.getMemberId()).stream()
                .map(it -> new MyReservationResponse(it.getId(), it.getTheme().getName(), it.getDate(), it.getTime().getValue(), "예약"))
                .toList();

        List<MyReservationResponse> waitings = waitingRepository.findWaitingsWithRankByMemberId(loginMember.getMemberId()).stream()
                .map(it -> new MyReservationResponse(it.getWaiting().getId(), it.getWaiting().getTheme().getName(), it.getWaiting().getDate(), it.getWaiting().getTime(), (it.getRank() + 1) + "번째 예약대기"))
                .toList();

        List<MyReservationResponse> results = Stream.concat(reservations.stream(), waitings.stream())
                .sorted(Comparator.comparing(MyReservationResponse::getDate))
                .toList();

        return results;
    }
}
