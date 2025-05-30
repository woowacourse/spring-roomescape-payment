package roomescape.application.facade;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import roomescape.application.service.ReservationTicketService;
import roomescape.application.service.TossPaymentService;
import roomescape.dto.LoginMember;
import roomescape.dto.request.ReservationTicketPaymentRequestDto;
import roomescape.dto.response.ReservationTicketResponseDto;
import roomescape.model.ReservationTicket;

@Service
@RequiredArgsConstructor
public class ReservationTicketPaymentService {

    private final ReservationTicketService reservationTicketService;
    private final TossPaymentService tossPaymentService;

    public ReservationTicketResponseDto saveReservation(
            ReservationTicketPaymentRequestDto reservationTicketPaymentRequestDto,
            LoginMember loginMember) {
        ReservationTicket reservationTicket = reservationTicketService.saveReservation(
                reservationTicketPaymentRequestDto.reservationTicketRegisterDto(),
                loginMember);
        tossPaymentService.processPayment(reservationTicketPaymentRequestDto.tossPaymentRequestDto(),
                reservationTicket);

        return new ReservationTicketResponseDto(reservationTicket);
    }

}
