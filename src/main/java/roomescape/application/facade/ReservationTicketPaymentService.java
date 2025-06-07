package roomescape.application.facade;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.application.service.ReservationTicketService;
import roomescape.application.service.TossPaymentService;
import roomescape.dto.LoginMember;
import roomescape.dto.request.ReservationTicketPaymentRequestDto;
import roomescape.dto.request.TossPaymentRequestDto;
import roomescape.dto.response.ReservationTicketResponseDto;
import roomescape.model.ReservationTicket;

@Service
@RequiredArgsConstructor
public class ReservationTicketPaymentService {

    private final ReservationTicketService reservationTicketService;
    private final TossPaymentService tossPaymentService;

    public void processReservationRegistration(
            ReservationTicketPaymentRequestDto reservationTicketPaymentRequestDto,
            LoginMember loginMember) {

        reservationTicketService.validateReservationTicket(
                reservationTicketPaymentRequestDto.reservationTicketRegisterDto(),
                loginMember
        );

        String requestKey = UUID.randomUUID().toString();
        TossPaymentRequestDto tossPaymentRequestDto = reservationTicketPaymentRequestDto.tossPaymentRequestDto();
        tossPaymentService.processPayment(tossPaymentRequestDto, requestKey);
    }

    @Transactional
    public ReservationTicketResponseDto saveReservationWithPayment(
            ReservationTicketPaymentRequestDto reservationTicketPaymentRequestDto,
            LoginMember loginMember) {
        ReservationTicket reservationTicket = reservationTicketService.saveReservationTicket(
                reservationTicketPaymentRequestDto.reservationTicketRegisterDto(),
                loginMember
        );

        tossPaymentService.saveTossPayment(reservationTicketPaymentRequestDto.tossPaymentRequestDto(),
                reservationTicket);

        return new ReservationTicketResponseDto(reservationTicket);
    }

}
