package roomescape.reservation;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;
import org.springframework.stereotype.Service;
import roomescape.member.LoginMember;
import roomescape.member.Member;
import roomescape.member.MemberService;
import roomescape.payment.Payment;
import roomescape.payment.PaymentConfirmRequest;
import roomescape.payment.PaymentServiceV2;
import roomescape.theme.Theme;
import roomescape.theme.ThemeRepository;
import roomescape.time.Time;
import roomescape.time.TimeRepository;
import roomescape.waiting.WaitingRepository;

@Service
public class ReservationServiceV2 {
    private ReservationRepository reservationRepository;
    private WaitingRepository waitingRepository;
    private MemberService memberService;
    private TimeRepository timeRepository;
    private ThemeRepository themeRepository;
    private PaymentServiceV2 paymentServiceV2;

    public ReservationServiceV2(ReservationRepository reservationRepository,
                                WaitingRepository waitingRepository,
                                MemberService memberService,
                                TimeRepository timeRepository,
                                ThemeRepository themeRepository,
                                PaymentServiceV2 paymentServiceV2
                              ) {
        this.reservationRepository = reservationRepository;
        this.waitingRepository = waitingRepository;
        this.memberService = memberService;
        this.timeRepository = timeRepository;
        this.themeRepository = themeRepository;
        this.paymentServiceV2 = paymentServiceV2;
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
        Reservation reservation = reservationRepository.save(new Reservation(member, member.getName(), reservationRequest.getDate(), time, theme));
        Payment payment = paymentServiceV2.pay(new PaymentConfirmRequest(reservationRequest), reservation);
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
        List<MyReservationResponse> reservations = reservationRepository.findPaymentByMemberId(loginMember.getMemberId()).stream()
                .map(it -> new MyReservationResponse(it.getReservation().getId(),
                        it.getReservation().getTheme().getName(),
                        it.getReservation().getDate(),
                        it.getReservation().getTime().getValue(),
                        "예약"
                        ,it.getPaymentKey(),
                        it.getTotalAmount()
                ))
                .toList();

        List<MyReservationResponse> waitings = waitingRepository.findWaitingsWithRankByMemberId(loginMember.getMemberId()).stream()
                .map(it -> new MyReservationResponse(it.getWaiting().getId(),
                        it.getWaiting().getTheme().getName(),
                        it.getWaiting().getDate(),
                        it.getWaiting().getTime(),
                        (it.getRank() + 1) + "번째 예약대기",null,null))
                .toList();

        List<MyReservationResponse> results = Stream.concat(reservations.stream(), waitings.stream())
                .sorted(Comparator.comparing(MyReservationResponse::getDate))
                .toList();

        return results;
    }
}
