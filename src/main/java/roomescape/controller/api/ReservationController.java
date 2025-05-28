package roomescape.controller.api;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClient;
import roomescape.client.PaymentClient;
import roomescape.controller.annotation.AdminMember;
import roomescape.controller.annotation.CurrentMember;
import roomescape.dto.auth.LoginInfo;
import roomescape.dto.reservation.MemberReservationCreateRequestDto;
import roomescape.dto.reservation.MyReservationResponseDto;
import roomescape.dto.reservation.ReservationResponseDto;
import roomescape.dto.reservation.TossPaymentConfirmRequestDto;
import roomescape.service.command.ReservationCommandService;
import roomescape.service.dto.ReservationCreateDto;
import roomescape.service.query.ReservationQueryService;

import java.util.Base64;
import java.util.List;

@RestController
@RequestMapping("/reservations")
public class ReservationController {

    private final ReservationQueryService reservationQueryService;
    private final ReservationCommandService reservationCommandService;
    private final PaymentClient paymentClient;

    public ReservationController(ReservationQueryService reservationQueryService,
                                 ReservationCommandService reservationCommandService,
                                 PaymentClient paymentClient) {
        this.reservationQueryService = reservationQueryService;
        this.reservationCommandService = reservationCommandService;
        this.paymentClient = paymentClient;
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
    public ReservationResponseDto addReservation(
            @CurrentMember LoginInfo loginInfo,
            @RequestBody final MemberReservationCreateRequestDto requestDto
    ) {
        paymentClient.confirmPayment(requestDto.extractTossPaymentDto());

        ReservationCreateDto reservationCreateDto = new ReservationCreateDto(
                requestDto.date(), requestDto.timeId(), requestDto.themeId(), loginInfo.id());
        return reservationCommandService.bookReservation(reservationCreateDto);
    }
}
