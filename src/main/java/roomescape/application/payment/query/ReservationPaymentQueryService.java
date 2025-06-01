package roomescape.application.payment.query;

import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.application.payment.query.dto.ReservationPaymentResponse;
import roomescape.domain.payment.repository.ReservationPaymentRepository;

@Service
@Transactional(readOnly = true)
public class ReservationPaymentQueryService {

    private final ReservationPaymentRepository reservationPaymentRepository;

    public ReservationPaymentQueryService(ReservationPaymentRepository reservationPaymentRepository) {
        this.reservationPaymentRepository = reservationPaymentRepository;
    }

    public Map<Long, ReservationPaymentResponse> findPaymentsByMemberId(Long memberId) {
        System.out.println(reservationPaymentRepository.findAllByReservation_Member_Id(memberId)
                .stream()
                .collect(
                        Collectors.toMap(
                                reservationPayment -> reservationPayment.getReservation().getId(),
                                ReservationPaymentResponse::from
                        )
                ));
        return reservationPaymentRepository.findAllByReservation_Member_Id(memberId)
                .stream()
                .collect(
                        Collectors.toMap(
                                reservationPayment -> reservationPayment.getReservation().getId(),
                                ReservationPaymentResponse::from
                        )
                );
    }
}
