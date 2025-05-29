package roomescape.reservation.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.stereotype.Service;
import roomescape.global.auth.dto.UserInfo;
import roomescape.member.domain.Member;
import roomescape.member.service.MemberService;
import roomescape.payment.service.PaymentService;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.domain.ReservationInfo;
import roomescape.reservation.domain.Waiting;
import roomescape.reservation.dto.request.ReservationCreateRequest;
import roomescape.reservation.dto.request.ReservationRequest;
import roomescape.reservation.dto.response.MyReservationResponse;
import roomescape.reservation.dto.response.ReservationResponse;
import roomescape.reservation.dto.response.WaitingWithRank;
import roomescape.reservation.exception.ReservationAlreadyExistsException;
import roomescape.reservationtime.domain.ReservationTime;
import roomescape.reservationtime.service.ReservationTimeService;
import roomescape.theme.domain.Theme;
import roomescape.theme.service.ThemeService;

@Service
public class ReservationFacadeService {

    private final ReservationService reservationService;
    private final WaitingService waitingService;
    private final MemberService memberService;
    private final ThemeService themeService;
    private final ReservationTimeService reservationTimeService;
    private final PaymentService paymentService;

    public ReservationFacadeService(final ReservationService reservationService,
                                    final WaitingService waitingService,
                                    final MemberService memberService,
                                    final ThemeService themeService,
                                    final ReservationTimeService reservationTimeService,
                                    final PaymentService paymentService) {
        this.reservationService = reservationService;
        this.waitingService = waitingService;
        this.memberService = memberService;
        this.themeService = themeService;
        this.reservationTimeService = reservationTimeService;
        this.paymentService = paymentService;
    }

    public List<MyReservationResponse> findMyReservations(final UserInfo userInfo) {
        List<Reservation> myReservations = reservationService.findMyReservations(userInfo);
        List<WaitingWithRank> waitingWithRanks = waitingService.findMyWaitingsWithRank(userInfo);
        List<MyReservationResponse> myReservationResponses = new ArrayList<>();
        return Stream.concat(myReservations.stream().map(MyReservationResponse::from),
                waitingWithRanks.stream().map(MyReservationResponse::from)
        ).collect(Collectors.toList());
    }

    public ReservationResponse createForAdmin(final ReservationRequest request,
                                              final Long memberId) {
        if (!reservationService.isReservationExists(request)) {
            return createReservation(request, memberId);
        }
        return createWaiting(request, memberId);
    }

    public ReservationResponse create(final ReservationCreateRequest request, final Long memberId) {

        if (!reservationService.isReservationExists(request.reservation())) {
            paymentService.payment(request.getPaymentKey(), request.getOrderId(), request.getAmount(),
                    request.getPaymentType());
            return createReservation(request.reservation(), memberId);
        }
        throw new ReservationAlreadyExistsException("이미 예약이 존재합니다.");
    }

    private ReservationResponse createReservation(final ReservationRequest request, final Long memberId) {
        reservationService.checkIfReservationExists(request);
        ReservationTime time = reservationTimeService.findReservationTime(request.timeId());
        Theme theme = themeService.findTheme(request.themeId());
        Member member = memberService.findUserByMemberId(memberId);
        ReservationInfo reservationInfo = new ReservationInfo(request.date(), time, theme);
        Reservation newReservation = reservationService.save(
                Reservation.createUpcomingReservationWithUnassignedId(member, reservationInfo)
        );
        return ReservationResponse.of(newReservation);
    }

    public ReservationResponse createWaiting(final ReservationRequest request, final Long memberId) {
        ReservationTime time = reservationTimeService.findReservationTime(request.timeId());
        Theme theme = themeService.findTheme(request.themeId());
        Member member = memberService.findUserByMemberId(memberId);
        int turn = waitingService.findMaxOrderByDateAndTimeAndTheme(request.date(), request.timeId(),
                request.themeId());
        ReservationInfo reservationInfo = new ReservationInfo(request.date(), time, theme);
        Waiting newWaiting = waitingService.save(
                Waiting.createUpcomingReservationWithUnassignedId(member, turn + 1, reservationInfo));
        return ReservationResponse.of(newWaiting);
    }

    public void deleteReservation(final Long reservationId) {
        Reservation reservation = reservationService.findById(reservationId);
        reservationService.delete(reservationId);
        promoteWaiting(reservation.getInfo());
    }

    private void promoteWaiting(final ReservationInfo info) {
        if (!waitingService.isWaitingExists(info)) {
            return;
        }
        Waiting waiting = waitingService.findFirstWaitingOfInfo(info);
        createReservation(ReservationRequest.from(info), waiting.getMemberId());
        waitingService.delete(waiting.getId());
    }

}
