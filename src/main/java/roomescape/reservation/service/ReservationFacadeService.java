package roomescape.reservation.service;

import java.time.LocalDate;
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

    public ReservationResponse createForAdmin(final LocalDate date, final Long timeId, final Long themeId,
                                              final Long memberId) {
        if (reservationService.isReservationExists(date, timeId, themeId)) {
            return createReservation(date, timeId, themeId, memberId);
        }
        return createWaiting(date, timeId, themeId, memberId);
    }

    public ReservationResponse create(final LocalDate date, final Long timeId, final Long themeId,
                                      final Long memberId, final String paymentKey, final String orderId,
                                      final Integer amount, final String paymentType) {

        if (reservationService.isReservationExists(date, timeId, themeId)) {
            paymentService.payment(paymentKey, orderId, amount, paymentType);
            return createReservation(date, timeId, themeId, memberId);
        }
        throw new ReservationAlreadyExistsException("이미 예약이 존재합니다.");
    }

    private ReservationResponse createReservation(final LocalDate date, final Long timeId, final Long themeId,
                                                  final Long memberId) {
        reservationService.checkIfReservationExists(date, timeId, themeId);
        ReservationTime time = reservationTimeService.findReservationTime(timeId);
        Theme theme = themeService.findTheme(themeId);
        Member member = memberService.findUserByMemberId(memberId);
        ReservationInfo reservationInfo = new ReservationInfo(date, time, theme);
        Reservation newReservation = reservationService.save(
                Reservation.createUpcomingReservationWithUnassignedId(member, reservationInfo)
        );
        return ReservationResponse.of(newReservation);
    }

    public ReservationResponse createWaiting(final LocalDate date, final Long timeId, final Long themeId,
                                             final Long memberId) {
        validateAlreadyExistReservation(date, timeId, themeId);
        ReservationTime time = reservationTimeService.findReservationTime(timeId);
        Theme theme = themeService.findTheme(themeId);
        Member member = memberService.findUserByMemberId(memberId);
        int turn = waitingService.findMaxOrderByDateAndTimeAndTheme(date, timeId, themeId);
        ReservationInfo reservationInfo = new ReservationInfo(date, time, theme);
        Waiting newWaiting = waitingService.save(
                Waiting.createUpcomingReservationWithUnassignedId(member, turn + 1, reservationInfo));
        return ReservationResponse.of(newWaiting);
    }

    private void validateAlreadyExistReservation(final LocalDate date, final Long timeId, final Long themeId) {
        if (reservationService.isReservationExists(date, timeId, themeId)) {
            throw new ReservationAlreadyExistsException("이미 예약이 존재합니다.");
        }
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
        createReservation(
                waiting.getDate(),
                waiting.getTimeId(),
                waiting.getThemeId(),
                waiting.getMemberId()
        );
        waitingService.delete(waiting.getId());
    }

}
