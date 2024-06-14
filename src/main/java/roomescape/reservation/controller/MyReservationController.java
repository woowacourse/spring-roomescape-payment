package roomescape.reservation.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
import roomescape.reservation.service.ReservationService;
import roomescape.waiting.service.WaitingService;

@Tag(name = "나의 예약 API")
@RestController
@RequestMapping("/my/reservaitons")
public class MyReservationController {
    private final ReservationService reservationService;
    private final WaitingService waitingService;

    public MyReservationController(ReservationService reservationService, WaitingService waitingService) {
        this.reservationService = reservationService;
        this.waitingService = waitingService;
    }

    @Operation(summary = "나의 예약 조회", description = "나의 예약 목록을 조회한다.")
    @GetMapping
    public List<MyReservationWaitingResponse> findMyReservations(@Parameter(hidden = true) LoggedInMember member) {
        Long memberId = member.id();

        return Stream.concat(
                        reservationService.findMyReservations(memberId).stream(),
                        waitingService.findMyWaitings(memberId).stream())
                .sorted(Comparator.comparing(myReservationResponse ->
                        LocalDateTime.of(myReservationResponse.date(), myReservationResponse.startAt())))
                .toList();
    }
}
