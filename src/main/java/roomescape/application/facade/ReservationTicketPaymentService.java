package roomescape.application.facade;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import roomescape.application.service.ReservationTicketService;
import roomescape.application.service.TossPaymentService;
import roomescape.dto.LoginMember;
import roomescape.dto.request.ReservationTicketPaymentWithTossRequestDto;
import roomescape.dto.response.ReservationTicketResponseDto;
import roomescape.model.ReservationTicket;

@Service
@RequiredArgsConstructor
public class ReservationTicketPaymentService {

    private final ReservationTicketService reservationTicketService;
    private final TossPaymentService tossPaymentService;

    public ReservationTicketResponseDto saveReservationWithTossPaymentGateWay(
            ReservationTicketPaymentWithTossRequestDto reservationTicketPaymentWithTossRequestDto,
            LoginMember loginMember) {
        ReservationTicket reservationTicket = reservationTicketService.saveReservation(
                reservationTicketPaymentWithTossRequestDto.reservationTicketRegisterDto(),
                loginMember);
        tossPaymentService.processPayment(reservationTicketPaymentWithTossRequestDto.tossPaymentRequestDto(),
                reservationTicket);

        return new ReservationTicketResponseDto(reservationTicket);
    }

}
