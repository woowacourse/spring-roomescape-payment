package roomescape.controller.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import roomescape.controller.annotation.AdminMember;
import roomescape.controller.annotation.CurrentMember;
import roomescape.dto.auth.LoginInfo;
import roomescape.dto.reservation.MemberReservationCreateRequestDto;
import roomescape.dto.reservation.ReservationResponseDto;
import roomescape.global.Loggable;
import roomescape.service.command.ReservationWaitingCommandService;
import roomescape.service.dto.ReservationCreateDto;
import roomescape.service.query.ReservationQueryService;

import java.util.List;

@Tag(name = "예약 대기 관리 API")
@RestController
@RequestMapping("/reservations/waiting")
public class ReservationWaitingController {

    private final ReservationWaitingCommandService reservationWaitingCommandService;
    private final ReservationQueryService reservationQueryService;

    public ReservationWaitingController(ReservationWaitingCommandService reservationWaitingCommandService,
                                        ReservationQueryService reservationQueryService) {
        this.reservationWaitingCommandService = reservationWaitingCommandService;
        this.reservationQueryService = reservationQueryService;
    }

    @Operation(summary = "모든 예약 대기 조회")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ReservationResponseDto> getReservationWaitings(
            @AdminMember LoginInfo loginInfo
    ) {
        return reservationQueryService.findAllReservationWaitings();
    }

    @Loggable
    @Operation(summary = "예약 대기 추가")
    @ApiResponse(responseCode = "201", description = "생성 성공")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ReservationResponseDto addReservationWaiting(
            @CurrentMember LoginInfo loginInfo,
            @RequestBody MemberReservationCreateRequestDto request
    ) {
        ReservationCreateDto reservationCreateDto = new ReservationCreateDto(
                request.date(), request.timeId(), request.themeId(), loginInfo.id());
        return reservationWaitingCommandService.createReservationWaiting(reservationCreateDto);
    }

    @Loggable
    @Operation(summary = "예약 대기 삭제")
    @ApiResponse(responseCode = "204", description = "삭제 성공")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteWaitingReservation(
            @CurrentMember LoginInfo loginInfo,
            @PathVariable("id") final Long id
    ) {
        reservationWaitingCommandService.deleteReservationWaiting(id, loginInfo);
    }
}
