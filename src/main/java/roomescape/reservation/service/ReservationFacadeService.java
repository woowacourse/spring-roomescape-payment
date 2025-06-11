package roomescape.reservation.service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.global.auth.dto.UserInfo;
import roomescape.payment.domain.Payment;
import roomescape.payment.service.PaymentService;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.domain.ReservationInfo;
import roomescape.reservation.domain.Waiting;
import roomescape.reservation.dto.request.ReservationCreateRequest;
import roomescape.reservation.dto.request.ReservationRequest;
import roomescape.reservation.dto.response.MyReservationResponse;
import roomescape.reservation.dto.response.ReservationResponse;
import roomescape.reservation.dto.response.ReservationResponseWithPayment;
import roomescape.reservation.dto.response.WaitingWithRank;
import roomescape.reservation.exception.ReservationAlreadyExistsException;
import roomescape.reservation.repository.dto.ReservationWithPayment;
import roomescape.reservation.service.transaction.ReservationTransactionService;

@Slf4j
@Service
public class ReservationFacadeService {

    private final ReservationService reservationService;
    private final WaitingService waitingService;
    private final PaymentService paymentService;
    private final ReservationTransactionService reservationTransactionService;

    public ReservationFacadeService(final ReservationService reservationService,
                                    final WaitingService waitingService,
                                    final PaymentService paymentService,
                                    final ReservationTransactionService reservationTransactionService) {
        this.reservationService = reservationService;
        this.waitingService = waitingService;
        this.paymentService = paymentService;
        this.reservationTransactionService = reservationTransactionService;
    }

    public List<MyReservationResponse> findMyReservations(final UserInfo userInfo) {
        List<ReservationWithPayment> myReservations = reservationService.findMyReservations(userInfo);
        List<WaitingWithRank> waitingWithRanks = waitingService.findMyWaitingsWithRank(userInfo);
        return Stream.concat(myReservations.stream()
                        .map(MyReservationResponse::from),
                waitingWithRanks.stream()
                        .map(MyReservationResponse::from)
        ).collect(Collectors.toList());
    }

    public List<ReservationResponse> findReservations(final Long themeId, final Long memberId,
                                                      final LocalDate startDate,
                                                      final LocalDate endDate) {
        return reservationService.getReservations(themeId, memberId, startDate, endDate)
                .stream()
                .map(ReservationResponse::of)
                .toList();
    }

    public ReservationResponse createForAdmin(final ReservationRequest request,
                                              final Long memberId) {
        if (reservationService.isReservationExists(request)) {
            return ReservationResponse.of(waitingService.createWaiting(request, memberId));
        }
        return ReservationResponse.of(reservationService.createReservation(request, memberId));
    }

    public ReservationResponseWithPayment create(final ReservationCreateRequest request, final Long memberId) {
        if (reservationService.isReservationExists(request.reservation())) {
            log.info("기존 예약 존재로 인한 에약 생성 실패 memberId = {}", memberId);
            throw new ReservationAlreadyExistsException("이미 예약이 존재합니다.");
        }
        Payment payment = reservationTransactionService.createPaymentAndReservation(request, memberId);

        try {
            paymentService.sendPaymentRequest(request.payment());
            log.info("예약 결제 성공 memberId = {}, amount = {}", memberId, request.getAmount());
        } catch (Exception e) {
            log.info("예약 결제 실패 memberId = {}, errorMessage = {}", memberId, e.getMessage());
            paymentService.updatePaymentToFail(payment.getId());
            throw e;
        }

        paymentService.updatePaymentToSuccess(payment.getId());
        return ReservationResponseWithPayment.of(payment);
    }

    @Transactional
    public void deleteReservation(final Long reservationId) {
        Reservation reservation = reservationService.findById(reservationId);
        reservationService.delete(reservationId);
        promoteWaiting(reservation.getInfo());

    }

    private void promoteWaiting(final ReservationInfo info) {
        if (waitingService.isWaitingExists(info)) {
            Waiting waiting = waitingService.findFirstWaitingOfInfo(info);
            log.info("대기 승급 시도 waitingId = {}", waiting.getId());
            Reservation reservation = reservationService.createReservation(ReservationRequest.from(info),
                    waiting.getMemberId());
            waitingService.delete(waiting.getId());
            log.info("대기 승급 성공 reservationId = {}", reservation.getId());
        }
    }

}
