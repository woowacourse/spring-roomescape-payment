package roomescape.application.reservation.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import roomescape.application.reservation.command.dto.CreateReservationCommand;
import roomescape.domain.reservation.PaymentType;
import roomescape.domain.reservation.ReservationPayment;
import roomescape.domain.reservation.repository.ReservationPaymentRepository;

@Slf4j
@RequiredArgsConstructor
@Service
public class ProcessReservationByAdminUseCase {

    private final CreateReservationService createReservationService;
    private final ReservationPaymentRepository reservationPaymentRepository;

    public Long execute(final CreateReservationCommand command, final Long adminId) {
        final Long reservationId = createReservationService.reserve(command);
        reservationPaymentRepository.save(new ReservationPayment(reservationId, PaymentType.ADMIN, adminId));
        log.info("관리자 예약 생성 완료 - reservationId: {}, adminId: {}", reservationId, adminId);

        return reservationId;
    }
}
