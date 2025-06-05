package roomescape.reservation.ui.dto;

import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.experimental.FieldNameConstants;
import roomescape.common.domain.DomainTerm;
import roomescape.common.validate.Validator;
import roomescape.payment.domain.vo.PaymentInfo;
import roomescape.reservation.domain.Reservation;
import roomescape.theme.ui.dto.ThemeResponse;
import roomescape.time.ui.dto.ReservationTimeResponse;
import roomescape.user.domain.User;
import roomescape.user.ui.dto.UserResponse;

@FieldNameConstants(level = AccessLevel.PRIVATE)
public record ReservationWithPaymentInfoResponse(Long reservationId,
                                                 UserResponse user,
                                                 LocalDate date,
                                                 ReservationTimeResponse time,
                                                 ThemeResponse theme,
                                                 String paymentKey,
                                                 int totalAmount) {

    public ReservationWithPaymentInfoResponse {
        validate(reservationId, user, date, time, theme, paymentKey, totalAmount);
    }

    public static ReservationWithPaymentInfoResponse from(final Reservation domain, final User user, final PaymentInfo paymentInfo) {
        return new ReservationWithPaymentInfoResponse(
                domain.getId(),
                UserResponse.from(user),
                domain.getDate().getValue(),
                ReservationTimeResponse.from(domain.getTime()),
                ThemeResponse.from(domain.getTheme()),
                paymentInfo.getPaymentKey(),
                paymentInfo.getTotalAmount());
    }

    private void validate(final Long reservationId,
                          final UserResponse user,
                          final LocalDate date,
                          final ReservationTimeResponse time,
                          final ThemeResponse theme,
                          final String paymentKey,
                          final int totalAmount
    ) {
        Validator.of(ReservationWithPaymentInfoResponse.class)
                .validateNotNull(Fields.reservationId, reservationId, DomainTerm.RESERVATION_ID.label())
                .validateNotNull(Fields.user, user, DomainTerm.USER.label())
                .validateNotNull(Fields.date, date, DomainTerm.RESERVATION_DATE.label())
                .validateNotNull(Fields.time, time, DomainTerm.RESERVATION_TIME.label())
                .validateNotNull(Fields.theme, theme, DomainTerm.THEME_ID.label())
                .validateNotNull(Fields.paymentKey, paymentKey, DomainTerm.PAYMENT_KEY.label())
                .validateNotNull(Fields.totalAmount, totalAmount, DomainTerm.PAYMENT_AMOUNT.label());
    }
}
