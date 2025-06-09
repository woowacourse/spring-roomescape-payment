package roomescape.controller.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
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
import roomescape.global.Loggable;
import roomescape.service.command.PaymentCommandService;
import roomescape.service.command.ReservationCommandService;
import roomescape.service.dto.ReservationCreateDto;
import roomescape.service.query.ReservationQueryService;

import java.util.List;

@Tag(name = "예약 관리 API")
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

    @Operation(summary = "모든 예약 조회")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ReservationResponseDto> getAllReservationWaitings(
            @AdminMember LoginInfo loginInfo
    ) {
        return reservationQueryService.findAllReservations();
    }

    @Operation(summary = "내 예약 조회")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @GetMapping("/me")
    @ResponseStatus(HttpStatus.OK)
    public List<MyReservationResponseDto> getMyReservations(
            @CurrentMember LoginInfo loginInfo
    ) {
        return reservationQueryService.findMyReservations(loginInfo);
    }

    @Loggable
    @Operation(summary = "예약 추가")
    @ApiResponse(responseCode = "201", description = "생성 성공")
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
