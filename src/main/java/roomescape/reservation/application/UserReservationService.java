package roomescape.reservation.application;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.payment.application.dto.response.TossPaymentsResponse;
import roomescape.payment.model.PaymentClient;
import roomescape.payment.model.UserReservationWithPaymentInfoResponse;
import roomescape.payment.model.entity.Payment;
import roomescape.payment.model.repository.PaymentRepository;
import roomescape.payment.model.service.PaymentOperation;
import roomescape.reservation.application.dto.request.CreateReservationServiceRequest;
import roomescape.reservation.application.dto.response.ReservationServiceResponse;
import roomescape.reservation.application.dto.response.UserReservationServiceResponse;
import roomescape.reservation.model.entity.Reservation;
import roomescape.reservation.model.entity.ReservationWaiting;
import roomescape.reservation.model.repository.ReservationRepository;
import roomescape.reservation.model.repository.ReservationWaitingRepository;
import roomescape.reservation.model.repository.dto.ReservationWaitingWithRank;
import roomescape.reservation.model.service.ReservationOperation;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserReservationService {

    private final ReservationRepository reservationRepository;
    private final ReservationWaitingRepository reservationWaitingRepository;
    private final PaymentRepository paymentRepository;
    private final ReservationOperation reservationOperation;
    private final PaymentOperation paymentOperation;
    private final PaymentClient paymentClient;

    @Transactional
    public ReservationServiceResponse create(CreateReservationServiceRequest request) {
        Reservation savedReservation = reservationOperation.reserve(request.toSchedule(), request.memberId());
        log.info("결제 승인 요청 - memberId: {}, schedule: {}, amount: {}",
                request.memberId(), request.toSchedule(), request.toPaymentInfo().amount());

        TossPaymentsResponse tossPaymentsResponse = paymentClient.requestConfirm(request.toPaymentInfo());
        paymentOperation.savePayment(tossPaymentsResponse.toEntity(savedReservation.getId()));

        log.info("결제 승인 완료 - memberId: {}, orderId: {}, amount: {}",
                request.memberId(), tossPaymentsResponse.orderId(), tossPaymentsResponse.totalAmount());
        return ReservationServiceResponse.from(savedReservation);
    }

    public List<UserReservationServiceResponse> getAllByMemberId(Long memberId) {
        List<Reservation> reservations = reservationRepository.findAllByMemberId(memberId);
        List<UserReservationWithPaymentInfoResponse> reservationWithPaymentInfo = getReservationWithPaymentInfoResponse(
                reservations);
        List<ReservationWaitingWithRank> waitingWithRanks = reservationWaitingRepository.findAllWithRankByMemberId(
                memberId);

        List<UserReservationServiceResponse> responses = createUserReservationServiceResponse(
                reservationWithPaymentInfo,
                waitingWithRanks
        );

        return sortByDateTime(responses);
    }

    private List<UserReservationWithPaymentInfoResponse> getReservationWithPaymentInfoResponse(
            final List<Reservation> reservations) {

        final List<Long> reservationIds = getReservationIds(reservations);
        final List<Payment> payments = paymentRepository.findAllByReservationIdIn(reservationIds);

        return getResponse(reservations, payments);
    }

    private List<Long> getReservationIds(final List<Reservation> reservations) {
        return reservations.stream()
                .map(Reservation::getId)
                .toList();
    }

    private List<UserReservationWithPaymentInfoResponse> getResponse(
            final List<Reservation> reservations, final List<Payment> payments) {

        final Map<Long, Payment> reservationIdToPayment = getReservationIdToPayment(payments);

        final List<UserReservationWithPaymentInfoResponse> userReservationWithPaymentInfo = new ArrayList<>();
        for (Reservation reservation : reservations) {
            final Payment payment = reservationIdToPayment.get(reservation.getId());
            final UserReservationWithPaymentInfoResponse response = toResponse(reservation, payment);
            userReservationWithPaymentInfo.add(response);
        }
        return userReservationWithPaymentInfo;
    }

    private Map<Long, Payment> getReservationIdToPayment(final List<Payment> payments) {
        return payments.stream()
                .collect(Collectors.toMap(Payment::getReservationId, Function.identity()));
    }

    private UserReservationWithPaymentInfoResponse toResponse(
            final Reservation reservation, final Payment payment) {

        return new UserReservationWithPaymentInfoResponse(
                reservation.getId(),
                reservation.getDate(),
                reservation.getTime().getStartAt(),
                reservation.getTheme().getName(),
                reservation.getStatus().name(),
                payment.getPaymentKey(),
                payment.getAmount()
        );
    }

    @Transactional
    public void cancel(Long id, Long memberId) {
        Reservation reservation = reservationRepository.getById(id);
        reservation.checkOwner(memberId);
        reservationOperation.cancel(reservation.getId());
    }

    private List<UserReservationServiceResponse> createUserReservationServiceResponse(
            List<UserReservationWithPaymentInfoResponse> payments,
            List<ReservationWaitingWithRank> reservationWaitingWithRanks
    ) {
        List<UserReservationServiceResponse> responses = new ArrayList<>();
        for (UserReservationWithPaymentInfoResponse reservation : payments) {
            responses.add(UserReservationServiceResponse.of(reservation));
        }
        for (ReservationWaitingWithRank waitingWithRank : reservationWaitingWithRanks) {
            ReservationWaiting reservationWaiting = waitingWithRank.getReservationWaiting();
            int rank = waitingWithRank.getRankToInt();
            responses.add(UserReservationServiceResponse.of(reservationWaiting, rank));
        }

        return responses;
    }

    private List<UserReservationServiceResponse> sortByDateTime(List<UserReservationServiceResponse> responses) {
        return responses.stream()
                .sorted(Comparator.comparing(UserReservationServiceResponse::date)
                        .thenComparing(UserReservationServiceResponse::time))
                .toList();
    }
}
