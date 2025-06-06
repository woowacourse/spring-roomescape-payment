package roomescape.reservation.service;

import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.stereotype.Service;
import roomescape.global.auth.dto.UserInfo;
import roomescape.member.domain.Member;
import roomescape.member.service.MemberService;
import roomescape.payment.dto.response.PaymentResponse;
import roomescape.payment.service.PaymentApiClient;
import roomescape.payment.service.PaymentService;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.domain.ReservationInfo;
import roomescape.reservation.domain.Waiting;
import roomescape.reservation.dto.request.ReservationCreateRequest;
import roomescape.reservation.dto.request.ReservationRequest;
import roomescape.reservation.dto.response.MyReservationResponse;
import roomescape.reservation.dto.response.ReservationResponse;
import roomescape.reservation.dto.response.WaitingWithRank;
import roomescape.reservationtime.domain.ReservationTime;
import roomescape.reservationtime.service.ReservationTimeService;
import roomescape.theme.domain.Theme;
import roomescape.theme.service.ThemeService;

@Service
public class ReservationUseCase {

    private final ReservationService reservationService;
    private final ReservationCreatorService reservationCreatorService;
    private final WaitingService waitingService;
    private final MemberService memberService;
    private final ThemeService themeService;
    private final ReservationTimeService reservationTimeService;
    private final PaymentService paymentService;
    private final PaymentApiClient paymentApiClient;

    public ReservationUseCase(final ReservationService reservationService,
                              final ReservationCreatorService reservationCreatorService,
                              final WaitingService waitingService,
                              final MemberService memberService,
                              final ThemeService themeService,
                              final ReservationTimeService reservationTimeService,
                              final PaymentService paymentService, final PaymentApiClient paymentApiClient) {
        this.reservationService = reservationService;
        this.reservationCreatorService = reservationCreatorService;
        this.waitingService = waitingService;
        this.memberService = memberService;
        this.themeService = themeService;
        this.reservationTimeService = reservationTimeService;
        this.paymentService = paymentService;
        this.paymentApiClient = paymentApiClient;
    }

    @Transactional
    public List<MyReservationResponse> findMyReservations(final UserInfo userInfo) {
        List<Reservation> myReservations = reservationService.findMyReservations(userInfo);
        List<WaitingWithRank> waitingWithRanks = waitingService.findMyWaitingsWithRank(userInfo);
        List<MyReservationResponse> myReservationResponses = new ArrayList<>();
        return Stream.concat(myReservations.stream().map(MyReservationResponse::from),
                waitingWithRanks.stream().map(MyReservationResponse::from)
        ).collect(Collectors.toList());
    }

    @Transactional
    public ReservationResponse createForAdmin(final ReservationRequest request,
                                              final Long memberId) {
        if (!reservationService.isReservationExists(request)) {
            return ReservationResponse.of(reservationCreatorService.createReservation(request, memberId));
        }
        return createWaiting(request, memberId);
    }

    public ReservationResponse executeReservation(final ReservationCreateRequest request, final Long memberId) {
        reservationService.checkIfReservationExists(request.reservation());
        Reservation reservation = reservationCreatorService.createReservation(request.reservation(), memberId);
        paymentService.createPaymentWithRequest(reservation, request.payment());
        PaymentResponse paymentResponse = paymentApiClient.authPayment(request.payment());
        paymentService.updatePaymentWithConfirm(paymentResponse, reservation);
        return ReservationResponse.of(reservation);
    }

    @Transactional
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

    @Transactional
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
        reservationCreatorService.createReservation(ReservationRequest.from(info), waiting.getMemberId());
        waitingService.delete(waiting.getId());
    }

    @Transactional
    public List<ReservationResponse> findReservations(final Long themeId, final Long memberId, final LocalDate dateFrom,
                                                      final LocalDate dateTo) {
        return reservationService.findReservations(themeId, memberId, dateFrom, dateTo);
    }
}
