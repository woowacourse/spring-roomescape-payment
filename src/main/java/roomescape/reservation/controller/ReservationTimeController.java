package roomescape.reservation.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import roomescape.reservation.dto.request.ReservationTimeRequest;
import roomescape.reservation.dto.response.ReservationTimeInfosResponse;
import roomescape.reservation.dto.response.ReservationTimeResponse;
import roomescape.reservation.dto.response.ReservationTimesResponse;
import roomescape.reservation.service.ReservationTimeService;
import roomescape.system.auth.annotation.Admin;
import roomescape.system.auth.annotation.LoginRequired;
import roomescape.system.dto.response.ErrorResponse;
import roomescape.system.dto.response.RoomEscapeApiResponse;

@RestController
@Tag(name = "4. 예약 시간 API", description = "예약 시간을 조회 / 추가 / 삭제할 때 사용합니다.")
public class ReservationTimeController {

    private final ReservationTimeService reservationTimeService;

    public ReservationTimeController(ReservationTimeService reservationTimeService) {
        this.reservationTimeService = reservationTimeService;
    }

    @Admin
    @GetMapping("/times")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "모든 시간 조회", tags = "관리자 로그인이 필요한 API")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "성공", useReturnTypeSchema = true)
    })
    public RoomEscapeApiResponse<ReservationTimesResponse> getAllTimes() {
        return RoomEscapeApiResponse.success(reservationTimeService.findAllTimes());
    }

    @Admin
    @PostMapping("/times")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "시간 추가", tags = "관리자 로그인이 필요한 API")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "성공", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "409", description = "같은 시간을 추가할 수 없습니다.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public RoomEscapeApiResponse<ReservationTimeResponse> saveTime(
            @Valid @RequestBody ReservationTimeRequest reservationTimeRequest,
            HttpServletResponse response
    ) {
        ReservationTimeResponse reservationTimeResponse = reservationTimeService.addTime(reservationTimeRequest);
        response.setHeader(HttpHeaders.LOCATION, "/times/" + reservationTimeResponse.id());

        return RoomEscapeApiResponse.success(reservationTimeResponse);
    }

    @Admin
    @DeleteMapping("/times/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "시간 삭제", tags = "관리자 로그인이 필요한 API")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "성공", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "409", description = "예약된 시간은 삭제할 수 없습니다.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public RoomEscapeApiResponse<Void> removeTime(
            @NotNull(message = "timeId는 null 또는 공백일 수 없습니다.") @PathVariable @Parameter(description = "삭제하고자 하는 시간의 ID값") Long id
    ) {
        reservationTimeService.removeTimeById(id);

        return RoomEscapeApiResponse.success();
    }

    @LoginRequired
    @GetMapping("/times/filter")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "예약 가능 여부를 포함한 모든 시간 조회", tags = "로그인이 필요한 API")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "성공", useReturnTypeSchema = true)
    })
    public RoomEscapeApiResponse<ReservationTimeInfosResponse> findAllAvailableReservationTimes(
            @NotNull(message = "날짜는 null일 수 없습니다.")
            @RequestParam
            @Parameter(description = "yyyy-MM-dd 형식으로 입력해주세요.", example = "2024-06-10")
            LocalDate date,
            @NotNull(message = "themeId는 null일 수 없습니다.")
            @RequestParam
            @Parameter(description = "조회할 테마의 ID를 입력해주세요.", example = "1")
            Long themeId
    ) {
        return RoomEscapeApiResponse.success(reservationTimeService.findAllAvailableTimesByDateAndTheme(date, themeId));
    }
}
