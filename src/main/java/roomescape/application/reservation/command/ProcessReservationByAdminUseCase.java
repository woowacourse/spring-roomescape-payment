package roomescape.application.reservation.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import roomescape.application.reservation.command.dto.CreateReservationCommand;
import roomescape.application.reservation.command.dto.ReservationWithPaymentResult;

@Slf4j
@RequiredArgsConstructor
@Service
public class ProcessReservationByAdminUseCase {

    private final RegisterReservationByAdminUseCase registerReservationByAdminUseCase;

    public Long execute(final CreateReservationCommand command, final Long adminId) {
        final ReservationWithPaymentResult result = registerReservationByAdminUseCase.execute(command, adminId);
        final Long reservationId = result.reservationId();
        log.info("관리자 예약 생성 완료 - reservationId: {}, adminId: {}", reservationId, adminId);

        return reservationId;
    }
}
