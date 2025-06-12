package roomescape.controller.api;

import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import roomescape.client.PaymentClient;
import roomescape.controller.annotation.AdminOnly;
import roomescape.controller.annotation.CurrentMember;
import roomescape.dto.auth.LoginInfo;
import roomescape.dto.reservation.MemberReservationCreateRequestDto;
import roomescape.dto.reservation.MyReservationResponseDto;
import roomescape.dto.reservation.ReservationResponseDto;
import roomescape.service.command.PaymentCommandService;
import roomescape.service.command.ReservationCommandService;
import roomescape.service.query.ReservationQueryService;

import java.util.List;

@RestController
@RequestMapping("/reservations")
public class ReservationController {

    private final ReservationQueryService reservationQueryService;
    private final ReservationCommandService reservationCommandService;
    private final PaymentCommandService paymentCommandService;

    public ReservationController(ReservationQueryService reservationQueryService,
                                 ReservationCommandService reservationCommandService,
                                 PaymentCommandService paymentCommandService
    ) {
        this.reservationQueryService = reservationQueryService;
        this.reservationCommandService = reservationCommandService;
        this.paymentCommandService = paymentCommandService;
    }

    @AdminOnly
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ReservationResponseDto> getAllReservationWaitings() {
        return reservationQueryService.findAllReservations();
    }

    @GetMapping("/me")
    @ResponseStatus(HttpStatus.OK)
    public List<MyReservationResponseDto> getMyReservations(
            @CurrentMember LoginInfo loginInfo
    ) {
        return reservationQueryService.findMyReservations(loginInfo);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Transactional
    public ReservationResponseDto addReservation(
            @CurrentMember LoginInfo loginInfo,
            @RequestBody MemberReservationCreateRequestDto requestDto
    ) {
        ReservationResponseDto reservationResponse = reservationCommandService.bookReservation(
                requestDto, loginInfo);
        paymentCommandService.createPayment(
                reservationResponse.id(),
                requestDto.toTossPaymentDto()
        );
        return reservationResponse;
    }
}
