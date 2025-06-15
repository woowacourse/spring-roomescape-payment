package roomescape.reservation.service.usecase;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.member.auth.vo.MemberInfo;
import roomescape.payment.domain.Payment;
import roomescape.payment.service.PaymentService;
import roomescape.payment.service.dto.CreatePaymentServiceRequest;
import roomescape.reservation.controller.dto.CreateReservationWithPaymentWebRequest;
import roomescape.reservation.controller.dto.ReservationWebResponse;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.domain.ReservationPayment;
import roomescape.reservation.repository.ReservationPaymentRepository;
import roomescape.reservation.service.converter.ReservationConverter;
import roomescape.reservation.service.dto.CreateReservationServiceRequest;

@RequiredArgsConstructor
@Service
public class ReservationPaymentCommandUseCase {

    private final ReservationPaymentRepository reservationPaymentRepository;

    private final PaymentService paymentService;
    private final ReservationCommandUseCase reservationCommandUseCase;

    public void save(
            final Reservation reservation,
            final Payment payment
    ) {
        final ReservationPayment reservationPayment = new ReservationPayment(payment, reservation);

        reservationPaymentRepository.save(reservationPayment);
    }

    @Transactional
    public ReservationWebResponse createReservationWithPayment(
            final CreateReservationWithPaymentWebRequest request,
            final MemberInfo memberInfo
    ) {
        final Payment payment = paymentService.create(
                new CreatePaymentServiceRequest(
                        request.paymentConfirmWebRequest().paymentKey(),
                        request.paymentConfirmWebRequest().orderId(),
                        request.paymentConfirmWebRequest().amount()
                )
        );
        final Reservation reservation = reservationCommandUseCase.create(
                new CreateReservationServiceRequest(
                        memberInfo.id(),
                        request.createReservationWebRequest().date(),
                        request.createReservationWebRequest().timeId(),
                        request.createReservationWebRequest().themeId()
                )
        );

        save(reservation, payment);

        return ReservationConverter.toDto(reservation);
    }
}
