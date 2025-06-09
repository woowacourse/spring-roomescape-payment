package roomescape.controller.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import roomescape.dto.reservation.AdminReservationCreateRequestDto;
import roomescape.dto.reservation.ReservationResponseDto;
import roomescape.global.Loggable;
import roomescape.service.command.ReservationCommandService;
import roomescape.service.dto.ReservationCreateDto;
import roomescape.service.query.ReservationQueryService;

import java.time.LocalDate;
import java.util.List;

@Tag(name = "관리자 예약 관리 API")
@RestController
@RequestMapping("/admin/reservations")
public class AdminReservationController {

    private final ReservationQueryService reservationQueryService;
    private final ReservationCommandService reservationCommandService;

    public AdminReservationController(ReservationQueryService reservationQueryService,
                                      ReservationCommandService reservationCommandService) {
        this.reservationQueryService = reservationQueryService;
        this.reservationCommandService = reservationCommandService;
    }

    @Operation(summary = "모든 예약 조회")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ReservationResponseDto> readReservedReservations() {
        return reservationQueryService.findReservedReservations();
    }

    @Loggable
    @Operation(summary = "관리자에 의해 예약 추가")
    @ApiResponse(responseCode = "201", description = "생성 성공")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ReservationResponseDto addReservationByAdmin(
            @RequestBody AdminReservationCreateRequestDto requestDto
    ) {
        ReservationCreateDto createDto = new ReservationCreateDto(requestDto.date(), requestDto.timeId(),
                requestDto.themeId(), requestDto.memberId());
        return reservationCommandService.bookReservation(createDto);
    }

    @Operation(summary = "기간으로 예약 조회")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @GetMapping("/search")
    @ResponseStatus(HttpStatus.OK)
    public List<ReservationResponseDto> searchReservationsByPeriod(
            @RequestParam("themeId") long themeId,
            @RequestParam("memberId") long memberId,
            @RequestParam("dateFrom") LocalDate dateFrom,
            @RequestParam("dateTo") LocalDate dateTo
    ) {
        return reservationQueryService.searchReservationsBy(themeId, memberId, dateFrom, dateTo);
    }

    @Loggable
    @Operation(summary = "예약 삭제")
    @ApiResponse(responseCode = "204", description = "삭제 성공")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteReservation(
            @PathVariable("id") final Long id
    ) {
        reservationCommandService.cancelReservationBy(id);
    }
}
