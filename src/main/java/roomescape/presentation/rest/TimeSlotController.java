package roomescape.presentation.rest;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import roomescape.application.TimeSlotService;
import roomescape.presentation.request.CreateTimeSlotRequest;
import roomescape.presentation.response.AvailableTimeSlotResponse;
import roomescape.presentation.response.TimeSlotResponse;

@Tag(name = "Reservation Time", description = "예약 시간 관련 API")
@RestController
@RequestMapping("/times")
@AllArgsConstructor
public class TimeSlotController {

    private final TimeSlotService service;

    @Operation(summary = "예약 시간 생성", description = "관리자가 예약 시간을 생성합니다.")
    @PostMapping
    @ResponseStatus(CREATED)
    public TimeSlotResponse register(@RequestBody @Valid final CreateTimeSlotRequest request) {
        var timeSlot = service.register(request.startAt());
        return TimeSlotResponse.from(timeSlot);
    }

    @Operation(summary = "예약 시간 조회", description = "모든 예약 시간을 조회합니다.")
    @GetMapping
    public List<TimeSlotResponse> getAllTimeSlots() {
        var timeSlots = service.findAllTimeSlots();
        return TimeSlotResponse.from(timeSlots);
    }

    @Operation(summary = "예약 시간의 예약 가능 여부 조회", description = "모든 예약 시간의 예약 가능 여부를 함께 조회합니다.")
    @GetMapping(value = "/available", params = {"date", "themeId"})
    public List<AvailableTimeSlotResponse> getAvailableTimes(
            @RequestParam("date") final LocalDate date,
            @RequestParam("themeId") final Long themeId
    ) {
        var availableTimeSlots = service.findAvailableTimeSlots(date, themeId);
        return AvailableTimeSlotResponse.from(availableTimeSlots);
    }

    @Operation(summary = "예약 시간 삭제", description = "관리자가 예약 시간을 삭제합니다.")
    @DeleteMapping("/{id}")
    @ResponseStatus(NO_CONTENT)
    public void delete(@PathVariable("id") final long id) {
        service.removeById(id);
    }
}
