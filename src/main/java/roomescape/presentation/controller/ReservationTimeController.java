package roomescape.presentation.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import roomescape.application.service.ReservationTimeService;
import roomescape.dto.request.ReservationTimeRegisterDto;
import roomescape.dto.response.AvailableReservationTimeResponseDto;
import roomescape.dto.response.ReservationTimeResponseDto;

@Tag(name = "예약 시각 관련 API")
@RestController
@RequestMapping("/times")
@RequiredArgsConstructor
public class ReservationTimeController {

    private final ReservationTimeService reservationTimeService;

    @Operation(summary = "예약 시각 전체 조회")
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ReservationTimeResponseDto> getTimes() {
        return reservationTimeService.getAllTimes();
    }

    @Operation(summary = "예약 시각 저장")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ReservationTimeResponseDto addTime(
            @RequestBody @Valid ReservationTimeRegisterDto reservationTimeRegisterDto) {
        return reservationTimeService.saveTime(reservationTimeRegisterDto);
    }

    @Operation(summary = "예약 시각 삭제")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTime(@PathVariable("id") Long id) {
        reservationTimeService.deleteTime(id);
    }

    @Operation(summary = "날짜와 테마에 따른 예약 가능 시각 조회")
    @GetMapping("/available")
    @ResponseStatus(HttpStatus.OK)
    public List<AvailableReservationTimeResponseDto> findAvailableTimes(@RequestParam String date,
                                                                        @RequestParam Long themeId) {
        return reservationTimeService.getAvailableTimes(date, themeId);
    }
}
