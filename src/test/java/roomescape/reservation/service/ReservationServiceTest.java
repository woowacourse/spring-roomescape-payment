package roomescape.reservation.service;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import roomescape.common.exception.PaymentException;
import roomescape.member.auth.vo.MemberInfo;
import roomescape.member.domain.Member;
import roomescape.member.domain.MemberEmail;
import roomescape.member.domain.MemberName;
import roomescape.member.domain.Role;
import roomescape.payment.PaymentConfirmResponse;
import roomescape.payment.PaymentConfirmWebRequest;
import roomescape.payment.PaymentService;
import roomescape.reservation.controller.dto.CreateReservationWebRequest;
import roomescape.reservation.controller.dto.CreateReservationWithPaymentWebRequest;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.domain.ReservationDate;
import roomescape.reservation.service.dto.CreateReservationServiceRequest;
import roomescape.reservation.service.usecase.ReservationCommandUseCase;
import roomescape.theme.domain.Theme;
import roomescape.theme.domain.ThemeDescription;
import roomescape.theme.domain.ThemeName;
import roomescape.theme.domain.ThemeThumbnail;
import roomescape.time.domain.ReservationTime;

@ExtendWith(MockitoExtension.class)
public class ReservationServiceTest {

    @Mock
    private ReservationCommandUseCase reservationCommandUseCase;

    @Mock
    private PaymentService paymentService;

    @InjectMocks
    private ReservationService reservationService;

    @Nested
    class create {

        private static final CreateReservationWithPaymentWebRequest REQUEST = new CreateReservationWithPaymentWebRequest(
                new CreateReservationWebRequest(
                        LocalDate.of(2023, 10, 15), 1L, 1L
                ),
                new PaymentConfirmWebRequest("paymentKey", "orderId", 1000)
        );

        private static final MemberInfo MEMBER_INFO = new MemberInfo(1L, "name", "email", Role.USER);

        private static final CreateReservationServiceRequest CREATE_RESERVATION_SERVICE_REQUEST = new CreateReservationServiceRequest(
                MEMBER_INFO.id(),
                REQUEST.createReservationWebRequest().date(),
                REQUEST.createReservationWebRequest().timeId(),
                REQUEST.createReservationWebRequest().themeId()
        );

        private static final PaymentConfirmResponse PAYMENT_CONFIRM_RESPONSE = new PaymentConfirmResponse(
                "paymentKey",
                "orderId",
                1000,
                "success"
        );

        @DisplayName("결제 승인 요청을 성공하면 예약을 생성한다.")
        @Test
        void paymentConfirmSuccess() {
            // given
            when(paymentService.confirm(REQUEST.paymentConfirmWebRequest().toPaymentConfirmRequest()))
                    .thenReturn(PAYMENT_CONFIRM_RESPONSE);

            when(reservationCommandUseCase.create(CREATE_RESERVATION_SERVICE_REQUEST))
                    .thenReturn(ReservationFixture.createWithId());

            // when
            reservationService.paymentConfirmAndCreate(REQUEST, MEMBER_INFO);

            // then
            verify(reservationCommandUseCase).create(CREATE_RESERVATION_SERVICE_REQUEST);
        }

        @DisplayName("결제 승인 요청에 실패하면 예약을 생성하지 않는다.")
        @Test
        void paymentConfirmFailed() {
            // given
            when(paymentService.confirm(REQUEST.paymentConfirmWebRequest().toPaymentConfirmRequest()))
                    .thenThrow(new PaymentException(HttpStatus.FORBIDDEN, "결제 승인 실패", "FAILED"));

            // when
        }
    }

    private static class ReservationFixture {
        private static final Member MEMBER = Member.withId(1L, MemberName.from("name"),
                MemberEmail.from("email@email.com"), Role.USER);
        private static final ReservationDate DATE = ReservationDate.from(LocalDate.of(2023, 10, 15));
        private static final ReservationTime TIME = ReservationTime.withId(1L, LocalTime.of(10, 0));
        private static final Theme THEME = Theme.withId(1L, ThemeName.from("theme"),
                ThemeDescription.from("description"), ThemeThumbnail.from("thumbnail"));

        public static Reservation createWithId() {
            return Reservation.withId(1L, MEMBER, DATE, TIME, THEME);
        }

        public static Reservation createWithoutId() {
            return Reservation.withoutId(MEMBER, DATE, TIME, THEME);
        }
    }
}
