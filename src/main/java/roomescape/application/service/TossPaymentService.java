package roomescape.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import roomescape.application.support.TossPaymentWithHttpClient;
import roomescape.common.exception.PaymentClientException;
import roomescape.dto.request.TossPaymentConfirmDto;
import roomescape.dto.request.TossPaymentRequestDto;
import roomescape.dto.response.TossPaymentConfirmResponseDto;
import roomescape.model.ReservationTicket;
import roomescape.model.TossPayment;
import roomescape.persistence.repository.TossPaymentRepository;

@Service
@RequiredArgsConstructor
public class TossPaymentService {

    private final TossPaymentRepository tossPaymentRepository;
    private final TossPaymentWithHttpClient tossPaymentWithHttpClient;

    @Transactional(propagation = Propagation.REQUIRED)
    public void processPayment(
            TossPaymentRequestDto tossPaymentRequestDto,
            ReservationTicket reservationTicket) {
        TossPayment tossPayment = new TossPayment(
                tossPaymentRequestDto.paymentKey(),
                tossPaymentRequestDto.orderId(),
                tossPaymentRequestDto.amount(),
                reservationTicket
        );

        TossPaymentConfirmDto tossPaymentConfirmDto = new TossPaymentConfirmDto(
                tossPaymentRequestDto.paymentKey(),
                tossPaymentRequestDto.orderId(),
                tossPaymentRequestDto.amount()
        );

        TossPaymentConfirmResponseDto tossPaymentConfirmResponseDto = tossPaymentWithHttpClient.requestConfirmation(
                tossPaymentConfirmDto);

        if (!tossPaymentConfirmResponseDto.status().equals("DONE")) {
            throw new PaymentClientException("승인되지 않은 결제 내역입니다.");
        }

        tossPaymentRepository.save(tossPayment);
    }

}
