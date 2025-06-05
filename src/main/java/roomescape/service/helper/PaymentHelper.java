package roomescape.service.helper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.domain.payment.Payment;
import roomescape.domain.payment.PaymentRepository;

import java.util.NoSuchElementException;

@RequiredArgsConstructor
@Service
public class PaymentHelper {

    private final PaymentRepository paymentRepository;

    @Transactional(readOnly = true)
    public Payment getByReservationId(Long reservationId) {
        return paymentRepository.findByReservationId(reservationId)
                .orElseThrow(() -> new NoSuchElementException("[ERROR] 존재하지 않는 결제 정보입니다."));
    }

    @Transactional
    public void deleteByReservationIdIfExist(Long reservationId) {
        paymentRepository.deleteByReservationId(reservationId);
    }
}
