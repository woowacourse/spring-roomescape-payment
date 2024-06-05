package roomescape.service;

import org.springframework.stereotype.Service;
import roomescape.domain.reservation.PaymentInfo;
import roomescape.domain.reservation.Reservation;
import roomescape.repository.PaymentInfoRepository;
import roomescape.service.dto.TossPaymentResponseDto;

@Service
public class PaymentInfoService {

    private final PaymentInfoRepository paymentInfoRepository;

    public PaymentInfoService(PaymentInfoRepository paymentInfoRepository) {
        this.paymentInfoRepository = paymentInfoRepository;
    }

    public void save(TossPaymentResponseDto response, Reservation reservation) {
        paymentInfoRepository.save(
            new PaymentInfo(response.paymentKey(), response.totalAmount(), response.method(), reservation));
    }
}
