package roomescape.application.facade;

import jakarta.transaction.Transactional;
import java.util.UUID;
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

    @Transactional
    public ReservationTicketResponseDto saveReservationWithTossPaymentGateWay(
            ReservationTicketPaymentWithTossRequestDto reservationTicketPaymentWithTossRequestDto,
            LoginMember loginMember) {
        ReservationTicket reservationTicket = reservationTicketService.saveReservation(
                reservationTicketPaymentWithTossRequestDto.reservationTicketRegisterDto(),
                loginMember);

        String requestKey = UUID.randomUUID().toString();
        tossPaymentService.processPayment(reservationTicketPaymentWithTossRequestDto.tossPaymentRequestDto(),
                reservationTicket, requestKey);

        return new ReservationTicketResponseDto(reservationTicket);
    }

}
