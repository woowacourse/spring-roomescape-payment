package roomescape.application.reservation.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.application.payment.CreatePaymentService;
import roomescape.application.reservation.command.dto.CreateReservationCommand;
import roomescape.application.reservation.command.dto.ReservationWithPaymentResult;
import roomescape.domain.payment.AdminPayment;
import roomescape.domain.payment.PaymentType;
import roomescape.domain.payment.repository.AdminPaymentRepository;
import roomescape.domain.reservation.ReservationPayment;
import roomescape.domain.reservation.repository.ReservationPaymentRepository;

@Slf4j
@RequiredArgsConstructor
@Service
public class RegisterReservationByAdminUseCase {

    private final CreateReservationService createReservationService;
    private final CreatePaymentService createPaymentService;
    private final AdminPaymentRepository adminPaymentRepository;
    private final ReservationPaymentRepository reservationPaymentRepository;

    @Transactional
    public ReservationWithPaymentResult execute(final CreateReservationCommand command, final Long adminId) {
        final Long reservationId = createReservationService.reserve(command);
        final Long paymentId = createPaymentService.register(PaymentType.ADMIN);
        reservationPaymentRepository.save(new ReservationPayment(reservationId, paymentId));

        // TODO 서비스로 분리
        final Long adminPaymentId = adminPaymentRepository.save(new AdminPayment(paymentId, adminId)).getId();

        return new ReservationWithPaymentResult(reservationId, paymentId, adminPaymentId);
    }
}
