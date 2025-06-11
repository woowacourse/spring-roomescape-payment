package roomescape.controller.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import roomescape.controller.annotation.AdminMember;
import roomescape.dto.auth.LoginInfo;
import roomescape.dto.time.AvailableReservationTimeResponseDto;
import roomescape.dto.time.ReservationTimeCreateRequestDto;
import roomescape.dto.time.ReservationTimeResponseDto;
import roomescape.global.Loggable;
import roomescape.service.command.ReservationTimeCommandService;
import roomescape.service.query.ReservationTimeQueryService;

import java.time.LocalDate;
import java.util.List;

@Tag(name = "예약 시간 관리 API")
@RestController
@RequestMapping("/times")
public class ReservationTimeController {

    private final ReservationTimeQueryService reservationTimeQueryService;
    private final ReservationTimeCommandService reservationTimeCommandService;

    public ReservationTimeController(ReservationTimeQueryService reservationTimeQueryService,
                                     ReservationTimeCommandService reservationTimeCommandService) {
        this.reservationTimeQueryService = reservationTimeQueryService;
        this.reservationTimeCommandService = reservationTimeCommandService;
    }

    @Operation(summary = "모든 예약 시간 조회")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ReservationTimeResponseDto> getAllReservationTimes(
            @AdminMember LoginInfo loginInfo
    ) {
        return reservationTimeQueryService.findAllReservationTimes();
    }

    @Operation(summary = "예약 가능한 시간 조회")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @GetMapping("/available")
    @ResponseStatus(HttpStatus.OK)
    public List<AvailableReservationTimeResponseDto> getAllReservationTimesWithAvailability(
            @RequestParam("date") LocalDate date,
            @RequestParam("themeId") Long themeId
    ) {
        return reservationTimeQueryService.findAllReservationTimesWithAvailabilityBy(date, themeId);
    }

    @Loggable
    @Operation(summary = "예약 시간 추가")
    @ApiResponse(responseCode = "201", description = "생성 성공")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ReservationTimeResponseDto addReservationTime(
            @RequestBody ReservationTimeCreateRequestDto requestDto,
            @AdminMember LoginInfo loginInfo
    ) {
        return reservationTimeCommandService.createReservationTime(requestDto);
    }

    @Loggable
    @Operation(summary = "예약 시간 삭제")
    @ApiResponse(responseCode = "204", description = "삭제 성공")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteReservationTime(
            @PathVariable("id") Long id,
            @AdminMember LoginInfo loginInfo
    ) {
        reservationTimeCommandService.deleteReservationTimeById(id);
    }
}
