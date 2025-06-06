package roomescape.reservation.service;

import jakarta.persistence.OptimisticLockException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import roomescape.common.exception.PaymentException;
import roomescape.member.auth.vo.MemberInfo;
import roomescape.payment.PaymentService;
import roomescape.payment.domain.PaymentHistory;
import roomescape.payment.domain.PaymentStatus;
import roomescape.payment.repository.PaymentHistoryRepository;
import roomescape.reservation.controller.dto.CreateReservationWithPaymentWebRequest;
import roomescape.reservation.controller.dto.ReservationWebResponse;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.service.converter.ReservationConverter;
import roomescape.reservation.service.dto.CreateReservationServiceRequest;
import roomescape.reservation.service.usecase.ReservationCommandUseCase;

@Service
@RequiredArgsConstructor
public class ReservationPayService {

    private final ReservationCommandUseCase reservationCommandUseCase;
    private final PaymentService paymentService;
    private final PaymentHistoryRepository paymentHistoryRepository;

    public ReservationWebResponse createReservationWithPayment(
            final CreateReservationWithPaymentWebRequest webRequest,
            final MemberInfo memberInfo
    ) {
        Reservation savedReservation = saveReservation(webRequest, memberInfo);
        confirmPayment(webRequest, savedReservation);
        savePaymentHistory(webRequest, savedReservation);
        return ReservationConverter.toDto(savedReservation);
    }

    private Reservation saveReservation(CreateReservationWithPaymentWebRequest webRequest, MemberInfo memberInfo) {
        CreateReservationServiceRequest createRequest = webRequest.toCreateServiceRequest(webRequest, memberInfo);
        Reservation savedReservation = reservationCommandUseCase.create(createRequest);
        return savedReservation;
    }

    private void confirmPayment(CreateReservationWithPaymentWebRequest webRequest, Reservation savedReservation) {
        try {
            paymentService.confirm(webRequest.toPaymentConfirmRequest());
        } catch (PaymentException e) {
            reservationCommandUseCase.delete(savedReservation.getId());
            throw e;
        }
    }

    private void savePaymentHistory(CreateReservationWithPaymentWebRequest webRequest, Reservation savedReservation) {
        try {
            PaymentHistory paymentHistory = webRequest.toPaymentHistory(savedReservation, PaymentStatus.DONE);
            paymentHistoryRepository.save(paymentHistory);
        } catch (IllegalArgumentException | OptimisticLockException e) {
            // TODO 로깅
        }
    }
}
