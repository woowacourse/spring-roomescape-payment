package roomescape.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import roomescape.domain.Invoice;
import roomescape.domain.Member;
import roomescape.domain.Payment;
import roomescape.domain.Reservation;
import roomescape.domain.ReservationTime;
import roomescape.domain.Role;
import roomescape.domain.Theme;
import roomescape.infrastructure.repository.InvoiceRepository;
import roomescape.presentation.dto.request.LoginMember;
import roomescape.presentation.dto.request.PaymentProcessRequest;
import roomescape.presentation.dto.request.ReservationWithPaymentRequest;
import roomescape.presentation.dto.response.InvoiceResponse;
import roomescape.presentation.dto.response.PaymentResponse;
import roomescape.presentation.dto.response.ReservationResponse;

@ExtendWith(MockitoExtension.class)
public class ReservationPayServiceTest {

    @Mock
    MemberService memberService;

    @Mock
    InvoiceRepository invoiceRepository;

    @Mock
    ReservationService reservationService;

    @Mock
    PaymentService paymentService;

    @InjectMocks
    ReservationPayService reservationPayService;

    @Test
    void 나의_예약_기록을_조회한다() {
        Member member = Member.create("한스", Role.USER, "test1@email.com", "pass1");
        LocalDate date = LocalDate.of(2025, 4, 21);
        ReservationTime time = ReservationTime.create(LocalTime.of(10, 0));
        Theme theme = Theme.create("공포", "공포테마", "공포.jpg");
        Reservation reservation = Reservation.create(member, date, time, theme);
        LoginMember loginMember = new LoginMember(member.getId(), member.getName(), Role.USER, member.getEmail());
        Payment payment = Payment.create("paymentKey", "orderId", 1000);
        Invoice invoice = Invoice.create(reservation, payment);

        when(memberService.findMemberById(loginMember.id())).thenReturn(member);
        when(invoiceRepository.findAllByReservation_Member(member)).thenReturn(List.of(invoice));

        List<InvoiceResponse> responses = reservationPayService.getMyInvoices(loginMember);
        InvoiceResponse invoiceResponse = responses.getFirst();

        assertAll(
                () -> assertThat(responses).hasSize(1),
                () -> assertThat(invoiceResponse.id()).isEqualTo(reservation.getId()),
                () -> assertThat(invoiceResponse.myReservationResponse().date()).isEqualTo(reservation.getDate()),
                () -> assertThat(invoiceResponse.myReservationResponse().time()).isEqualTo(
                        reservation.getTime().getStartAt()),
                () -> assertThat(invoiceResponse.myReservationResponse().theme()).isEqualTo(
                        reservation.getTheme().getName()),
                () -> assertThat(invoiceResponse.myReservationResponse().status()).isEqualTo(
                        reservation.getStatus().getName())
        );
    }

    @Test
    void 사용자가_예약을_생성한다() {
        Member member = Member.create("한스", Role.USER, "test@email.com", "pass1");
        LocalDate date = LocalDate.of(2025, 4, 21);
        ReservationTime time = ReservationTime.create(LocalTime.of(10, 0));
        Theme theme = Theme.create("공포", "공포테마", "공포.jpg");
        Reservation reservation = Reservation.create(member, date, time, theme);
        Payment payment = Payment.create("paymentKey", "orderId", 10000);

        ReservationWithPaymentRequest request = new ReservationWithPaymentRequest(
                date,
                1L,
                1L,
                "paymentKey",
                "orderId",
                "10000");
        LoginMember loginMember = new LoginMember(member.getId(), member.getName(), Role.USER, member.getEmail());

        ReservationResponse reservationResponse = ReservationResponse.from(reservation);
        PaymentResponse paymentResponse = PaymentResponse.from(payment);
        Invoice invoice = Invoice.create(reservation, payment);

        when(reservationService.createMemberReservation(any(ReservationWithPaymentRequest.class),
                any(LoginMember.class)))
                .thenReturn(reservationResponse);
        when(reservationService.findReservationById(any())).thenReturn(reservation);
        when(paymentService.process(any(PaymentProcessRequest.class))).thenReturn(paymentResponse);
        when(invoiceRepository.save(any(Invoice.class))).thenReturn(invoice);

        InvoiceResponse resultResponse = reservationPayService.createReservationWithPayment(request, loginMember);

        assertAll(
                () -> assertThat(resultResponse.myReservationResponse().date()).isEqualTo(date),
                () -> assertThat(resultResponse.paymentResponse().paymentKey()).isEqualTo("paymentKey"),
                () -> assertThat(resultResponse.paymentResponse().orderId()).isEqualTo("orderId"),
                () -> assertThat(resultResponse.paymentResponse().totalAmount()).isEqualTo(10000),
                () -> verify(reservationService).createMemberReservation(any(ReservationWithPaymentRequest.class),
                        eq(loginMember)),
                () -> verify(paymentService).process(any(PaymentProcessRequest.class)),
                () -> verify(invoiceRepository).save(any(Invoice.class))
        );
    }

}
