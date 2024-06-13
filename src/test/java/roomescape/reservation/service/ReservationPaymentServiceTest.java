package roomescape.reservation.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import roomescape.auth.dto.LoggedInMember;
import roomescape.fixture.ReservationFixture;
import roomescape.member.domain.Member;
import roomescape.paymenthistory.PaymentType;
import roomescape.paymenthistory.dto.PaymentCreateRequest;
import roomescape.paymenthistory.exception.PaymentException;
import roomescape.paymenthistory.service.TossPaymentHistoryService;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.dto.ReservationCreateRequest;
import roomescape.reservation.dto.ReservationSaveResponse;

@ExtendWith(MockitoExtension.class)
class ReservationPaymentServiceTest {
    @Mock
    private ReservationService reservationService;
    @Mock
    private TossPaymentHistoryService tossPaymentHistoryService;
    @InjectMocks
    private ReservationPaymentService reservationPaymentService;

    @DisplayName("결제에 실패할 경우 에러를 발생한다.")
    @Test
    void saveReservationWithPayment_whenPaymentFails() {
        PaymentType paymentType = PaymentType.NORMAL;
        Reservation reservation = ReservationFixture.RESERVATION_WITH_ID;
        Member member = reservation.getMember();

        ReservationCreateRequest request = new ReservationCreateRequest(reservation.getDate(), reservation.getTimeId(),
                reservation.getThemeId(), "paymentKey", "orderId", 214000, paymentType);

        given(reservationService.createReservation(request, reservation.getMemberId()))
                .willReturn(new ReservationSaveResponse(reservation));

        doNothing().when(reservationService).deleteReservation(reservation.getId());

        doThrow(PaymentException.class)
                .when(tossPaymentHistoryService)
                .approvePayment(new PaymentCreateRequest("paymentKey", "orderId", 214000, reservation));

        assertThrows(PaymentException.class, () -> reservationPaymentService.saveReservationWithPayment(request,
                new LoggedInMember(member.getId(), member.getName(), member.getEmail(), member.isAdmin())));
    }
}
