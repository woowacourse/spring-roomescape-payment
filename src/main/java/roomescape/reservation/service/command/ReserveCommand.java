package roomescape.reservation.service.command;

import java.time.LocalDate;
import roomescape.reservation.controller.request.ReserveByAdminRequest;
import roomescape.reservation.controller.request.ReserveByUserRequest;
import roomescape.reservation.controller.request.ReservePaymentRequest;

public record ReserveCommand(
        LocalDate date,
        Long themeId,
        Long timeId,
        Long memberId
) {
    public static ReserveCommand byAdmin(ReserveByAdminRequest request) {
        return new ReserveCommand(
                request.date(),
                request.themeId(),
                request.timeId(),
                request.memberId()
        );
    }

    public static ReserveCommand byUser(ReserveByUserRequest request, Long memberId) {
        return new ReserveCommand(
                request.date(),
                request.themeId(),
                request.timeId(),
                memberId
        );
    }

    public static ReserveCommand byPayment(ReservePaymentRequest request, Long memberId) {
        return new ReserveCommand(
                request.date(),
                request.themeId(),
                request.timeId(),
                memberId
        );
    }

}
