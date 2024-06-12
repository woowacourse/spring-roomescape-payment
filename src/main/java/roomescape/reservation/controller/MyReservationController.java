package roomescape.reservation.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import roomescape.auth.dto.LoggedInMember;
import roomescape.reservation.dto.MyReservationWaitingResponse;
import roomescape.reservation.dto.ReservationResponse;
import roomescape.reservation.service.ReservationPaymentService;
import roomescape.waiting.service.WaitingService;

@Tag(name = "유저의 예약 API", description = "유저의 예약 API 입니다.")
@RestController
@RequestMapping("/my/reservations")
public class MyReservationController {
    private final WaitingService waitingService;
    private final ReservationPaymentService reservationPaymentService;

    public MyReservationController(WaitingService waitingService,
                                   ReservationPaymentService reservationPaymentService) {
        this.waitingService = waitingService;
        this.reservationPaymentService = reservationPaymentService;
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = MyReservationWaitingResponse.class))),
            @ApiResponse(responseCode = "500", content = @Content(schema = @Schema(implementation = ReservationResponse.class)))})
    @Operation(summary = "예약 삭제", description = "단일 예약을 삭제 합니다.")
    @GetMapping
    public List<MyReservationWaitingResponse> findMyReservations(@Parameter(hidden = true) LoggedInMember member) {
        Long memberId = member.id();

        return Stream.concat(
                        reservationPaymentService.findMyReservationsWithPayment(memberId).stream(),
                        waitingService.findMyWaitings(memberId).stream())
                .sorted(Comparator.comparing(myReservationResponse ->
                        LocalDateTime.of(myReservationResponse.date(), myReservationResponse.startAt())))
                .toList();
    }
}
