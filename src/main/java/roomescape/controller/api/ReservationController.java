package roomescape.controller.api;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import roomescape.controller.annotation.AdminMember;
import roomescape.controller.annotation.CurrentMember;
import roomescape.dto.auth.LoginInfo;
import roomescape.dto.payment.PaymentResponseDto;
import roomescape.dto.reservation.CreatedReservationResponseDto;
import roomescape.dto.reservation.MemberReservationCreateRequestDto;
import roomescape.dto.reservation.MyReservationResponseDto;
import roomescape.dto.reservation.ReservationResponseDto;
import roomescape.service.command.PaymentCommandService;
import roomescape.service.command.ReservationCommandService;
import roomescape.service.dto.ReservationCreateDto;
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
                                 PaymentCommandService paymentCommandService) {
        this.reservationQueryService = reservationQueryService;
        this.reservationCommandService = reservationCommandService;
        this.paymentCommandService = paymentCommandService;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ReservationResponseDto> getAllReservationWaitings(
            @AdminMember LoginInfo loginInfo
    ) {
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
    public CreatedReservationResponseDto addReservation(
            @CurrentMember LoginInfo loginInfo,
            @RequestBody MemberReservationCreateRequestDto requestDto
    ) {
        ReservationCreateDto reservationCreateDto = new ReservationCreateDto(
                requestDto.date(), requestDto.timeId(), requestDto.themeId(), loginInfo.id());
        ReservationResponseDto reservationDto = reservationCommandService.bookReservation(reservationCreateDto);
        PaymentResponseDto paymentDto = paymentCommandService.confirmPayment(
                reservationDto.id(),
                requestDto.extractTossPaymentDto());

        return CreatedReservationResponseDto.from(reservationDto, paymentDto);
    }
}
