package roomescape.application;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.domain.Invoice;
import roomescape.domain.Member;
import roomescape.domain.Payment;
import roomescape.domain.Reservation;
import roomescape.infrastructure.repository.InvoiceRepository;
import roomescape.presentation.dto.request.LoginMember;
import roomescape.presentation.dto.request.PaymentProcessRequest;
import roomescape.presentation.dto.request.ReservationWithPaymentRequest;
import roomescape.presentation.dto.response.InvoiceResponse;
import roomescape.presentation.dto.response.PaymentResponse;
import roomescape.presentation.dto.response.ReservationResponse;

@Service
@Transactional
public class ReservationPayService {

    private final ReservationService reservationService;
    private final PaymentService paymentService;
    private final MemberService memberService;
    private final InvoiceRepository invoiceRepository;

    public ReservationPayService(
            ReservationService reservationService,
            PaymentService paymentService,
            MemberService memberService,
            InvoiceRepository invoiceRepository
    ) {
        this.reservationService = reservationService;
        this.paymentService = paymentService;
        this.memberService = memberService;
        this.invoiceRepository = invoiceRepository;
    }

    public InvoiceResponse createReservationWithPayment(
            ReservationWithPaymentRequest reservationWithPaymentRequest,
            LoginMember loginMember
    ) {
        ReservationResponse reservationResponse = reservationService.createMemberReservation(
                reservationWithPaymentRequest,
                loginMember);

        Reservation reservation = reservationService.findReservationById(reservationResponse.id());
        PaymentProcessRequest paymentRequest = reservationWithPaymentRequest.toPaymentProcessRequest();
        PaymentResponse paymentResponse = paymentService.process(paymentRequest);
        Payment payment = paymentService.findPaymentById(paymentResponse.id());

        Invoice invoice = Invoice.create(reservation, payment);
        Invoice savedInvoice = invoiceRepository.save(invoice);

        return InvoiceResponse.from(savedInvoice);
    }

    public List<InvoiceResponse> getMyInvoices(LoginMember loginMember) {
        Member member = memberService.findMemberById(loginMember.id());
        List<Invoice> invoices = invoiceRepository.findAllByReservation_Member(member);
        return invoices.stream()
                .map(InvoiceResponse::from)
                .toList();
    }
}
