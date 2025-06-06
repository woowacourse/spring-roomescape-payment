package roomescape.reservation.application;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.common.domain.DomainTerm;
import roomescape.common.exception.DuplicateException;
import roomescape.payment.domain.vo.PaymentInfo;
import roomescape.payment.dto.PaymentRequest;
import roomescape.payment.resolver.PaymentClient;
import roomescape.reservation.application.dto.AvailableReservationTimeServiceRequest;
import roomescape.reservation.application.dto.CreateReservationServiceRequest;
import roomescape.reservation.application.dto.MyReservationsResponse;
import roomescape.reservation.application.dto.SimpleWaitingReservationResponse;
import roomescape.reservation.application.service.ReservationCommandService;
import roomescape.reservation.application.service.ReservationQueryService;
import roomescape.reservation.application.service.ReservationViewQueryService;
import roomescape.reservation.application.service.WaitingReservationCommandService;
import roomescape.reservation.application.service.WaitingReservationQueryService;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.domain.ReservationDate;
import roomescape.reservation.domain.WaitingReservation;
import roomescape.reservation.ui.dto.AvailableReservationTimeWebResponse;
import roomescape.reservation.ui.dto.CreateReservationWithUserIdWebRequest;
import roomescape.reservation.ui.dto.ReservationResponse;
import roomescape.reservation.ui.dto.ReservationSearchWebRequest;
import roomescape.reservation.ui.dto.ReservationWithPaymentInfoResponse;
import roomescape.reservation.ui.dto.WaitingReservationResponse;
import roomescape.user.application.service.UserQueryService;
import roomescape.user.domain.User;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReservationFacadeImpl implements ReservationFacade {

    private final ReservationQueryService reservationQueryService;
    private final ReservationCommandService reservationCommandService;
    private final WaitingReservationCommandService waitingReservationCommandService;
    private final WaitingReservationQueryService waitingReservationQueryService;
    private final ReservationViewQueryService reservationViewQueryService;
    private final UserQueryService userQueryService;

    private final PaymentClient paymentClient;

    @Override
    public List<ReservationResponse> getAll() {
        log.info("[RESERVATION] 전체 예약 목록 조회 요청");
        final List<Reservation> reservations = reservationQueryService.getAll();
        final List<Long> userIds = reservations.stream()
                .map(Reservation::getUserId)
                .toList();
        final List<User> users = userQueryService.getAllByIds(userIds);
        return ReservationResponse.from(reservations, users);
    }

    @Override
    public List<AvailableReservationTimeWebResponse> getAvailable(final LocalDate date, final Long themeId) {
        log.info("[RESERVATION] 예약 가능 시간 조회 요청: date={}, themeId={}", date, themeId);
        final AvailableReservationTimeServiceRequest request = new AvailableReservationTimeServiceRequest(
                ReservationDate.from(date),
                themeId);
        return reservationQueryService.getTimesWithAvailability(request).stream()
                .map(AvailableReservationTimeWebResponse::from)
                .toList();
    }

    @Override
    public List<ReservationResponse> getByParams(final ReservationSearchWebRequest request) {
        log.info("[RESERVATION] 파라미터로 예약 목록 조회 요청: {}", request);
        final List<Reservation> reservations = reservationQueryService.getByParams(request.toServiceRequest());
        final List<Long> userIds = reservations.stream()
                .map(Reservation::getUserId)
                .toList();
        final List<User> users = userQueryService.getAllByIds(userIds);
        return ReservationResponse.from(reservations, users);
    }

    @Override
    public List<MyReservationsResponse> getAllByUserId(final Long userId) {
        log.info("[RESERVATION] 사용자별 예약 목록 조회 요청: userId={}", userId);
        userQueryService.getById(userId);
        return reservationViewQueryService.getAllByUserId(userId)
                .stream()
                .map(MyReservationsResponse::from)
                .sorted(Comparator.comparing(MyReservationsResponse::sequence))
                .toList();
    }

    @Override
    @Transactional
    public ReservationWithPaymentInfoResponse create(final CreateReservationWithUserIdWebRequest request) {
        log.info("[RESERVATION] 예약 생성 요청: {}", request);
        final User user = userQueryService.getById(request.userId());
        final Reservation reservation = reservationCommandService.createWithPayment(request.toPaymentServiceRequest());
        log.info("[RESERVATION] 결제 확인 시도: paymentKey={}, amount={}", request.paymentKey(), request.amount());
        PaymentInfo paymentInfo = paymentClient.confirmPayment(
                new PaymentRequest(request.paymentKey(),
                        request.amount(),
                        request.orderId(),
                        request.paymentType())
        );
        paymentInfo.checkPaymentInfoMatch(request.paymentKey(), request.amount());
        log.info("[RESERVATION] 예약 및 결제 성공: reservationId={}, userId={}", reservation.getId(), user.getId());
        return ReservationWithPaymentInfoResponse.from(reservation, user, paymentInfo);
    }

    @Override
    @Transactional
    public void delete(final Long id) {
        log.info("[RESERVATION] 예약 삭제 요청: id={}", id);
        Optional<Long> waitingId = reservationViewQueryService.findFirstWaitingByReservationId(id);
        waitingId.ifPresentOrElse(
                waiting -> promotionWaiting(id, waiting),
                () -> reservationCommandService.delete(id)
        );
    }

    @Override
    public List<WaitingReservationResponse> getAllWaiting() {
        log.info("[RESERVATION] 대기 예약 목록 조회 요청");
        final List<WaitingReservation> waiting = waitingReservationQueryService.getAll();
        final List<Long> userIds = waiting.stream()
                .map(WaitingReservation::getUserId)
                .toList();
        final List<User> users = userQueryService.getAllByIds(userIds);
        return WaitingReservationResponse.from(waiting, users);
    }

    @Override
    public SimpleWaitingReservationResponse addWaiting(final CreateReservationWithUserIdWebRequest request) {
        log.info("[RESERVATION] 대기 예약 추가 요청: {}", request);
        final User user = userQueryService.getById(request.userId());
        final CreateReservationServiceRequest serviceRequest = request.toServiceRequest();
        if (reservationViewQueryService.existsByParams(serviceRequest, user.getId())) {
            log.warn("[RESERVATION] 중복 대기 예약 시도: userId={}, date={}, themeId={}, timeId={}", user.getId(), request.date(), request.themeId(), request.timeId());
            throw new DuplicateException(DomainTerm.RESERVATION,
                    request.date(),
                    DomainTerm.THEME_ID,
                    DomainTerm.RESERVATION_TIME_ID,
                    DomainTerm.USER_ID
            );
        }
        final WaitingReservation waitingReservation
                = waitingReservationCommandService.create(serviceRequest);
        return SimpleWaitingReservationResponse.from(waitingReservation, user);
    }

    @Override
    public void deleteWaiting(final Long id) {
        log.info("[RESERVATION] 대기 예약 삭제 요청: id={}", id);
        waitingReservationCommandService.delete(id);
    }

    @Override
    @Transactional
    public ReservationResponse promotionWaiting(final Long id, final CreateReservationWithUserIdWebRequest request) {
        log.info("[RESERVATION] 대기 예약 승급 요청: waitingId={}, request={}", id, request);
        final User user = userQueryService.getById(request.userId());
        final Reservation reservation = reservationCommandService.create(request.toServiceRequest());
        waitingReservationCommandService.delete(id);
        return ReservationResponse.from(reservation, user);
    }

    private void promotionWaiting(final Long id, final Long waiting) {
        log.info("[RESERVATION] 대기 예약 승급(내부) 요청: id={}, waitingId={}", id, waiting);
        final Long userId = waitingReservationQueryService.findUserIdById(waiting);
        reservationCommandService.updateUserId(id, userId);
        waitingReservationCommandService.delete(waiting);
    }
}
