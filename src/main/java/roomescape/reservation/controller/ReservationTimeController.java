package roomescape.reservation.controller;

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
import roomescape.system.dto.response.ApiResponse;

@RestController
public class ReservationTimeController {
    private final ReservationTimeService reservationTimeService;

    public ReservationTimeController(final ReservationTimeService reservationTimeService) {
        this.reservationTimeService = reservationTimeService;
    }

    @Admin
    @GetMapping("/times")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<ReservationTimesResponse> getAllTimes() {
        return ApiResponse.success(reservationTimeService.findAllTimes());
    }

    @Admin
    @PostMapping("/times")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<ReservationTimeResponse> saveTime(
            @Valid @RequestBody final ReservationTimeRequest reservationTimeRequest,
            final HttpServletResponse response
    ) {
        final ReservationTimeResponse reservationTimeResponse = reservationTimeService.addTime(reservationTimeRequest);
        response.setHeader(HttpHeaders.LOCATION, "/times/" + reservationTimeResponse.id());

        return ApiResponse.success(reservationTimeResponse);
    }

    @Admin
    @DeleteMapping("/times/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ApiResponse<Void> removeTime(
            @NotNull(message = "timeId는 null 또는 공백일 수 없습니다.") @PathVariable final Long id
    ) {
        reservationTimeService.removeTimeById(id);

        return ApiResponse.success();
    }

    @GetMapping("/times/filter")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<ReservationTimeInfosResponse> findAllAvailableReservationTimes(
            @NotNull(message = "날짜는 null일 수 없습니다.") @RequestParam final LocalDate date,
            @NotNull(message = "themeId는 null일 수 없습니다.") @RequestParam final Long themeId) {
        return ApiResponse.success(reservationTimeService.findAllAvailableTimesByDateAndTheme(date, themeId));
    }
}
