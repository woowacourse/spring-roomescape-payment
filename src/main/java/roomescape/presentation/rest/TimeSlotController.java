package roomescape.presentation.rest;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;

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
import roomescape.domain.auth.AuthenticationInfo;
import roomescape.exception.AuthorizationException;
import roomescape.presentation.request.CreateTimeSlotRequest;
import roomescape.presentation.response.AvailableTimeSlotResponse;
import roomescape.presentation.response.TimeSlotResponse;

@RestController
@RequestMapping("/times")
@AllArgsConstructor
public class TimeSlotController {

    private final TimeSlotService service;

    @PostMapping
    @ResponseStatus(CREATED)
    public TimeSlotResponse register(
        final AuthenticationInfo authenticationInfo,
        @RequestBody @Valid final CreateTimeSlotRequest request
    ) {
        if (authenticationInfo.isNotAdmin()) {
            throw new AuthorizationException("관리자에게만 허용된 작업입니다.");
        }
        var timeSlot = service.register(request.startAt());
        return TimeSlotResponse.from(timeSlot);
    }

    @GetMapping
    public List<TimeSlotResponse> getAllTimeSlots() {
        var timeSlots = service.findAllTimeSlots();
        return TimeSlotResponse.from(timeSlots);
    }

    @GetMapping(value = "/available", params = {"date", "themeId"})
    public List<AvailableTimeSlotResponse> getAvailableTimes(
        @RequestParam("date") final LocalDate date,
        @RequestParam("themeId") final Long themeId
    ) {
        var availableTimeSlots = service.findAvailableTimeSlots(date, themeId);
        return AvailableTimeSlotResponse.from(availableTimeSlots);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(NO_CONTENT)
    public void delete(
        final AuthenticationInfo authenticationInfo,
        @PathVariable("id") final long id
    ) {
        if (authenticationInfo.isNotAdmin()) {
            throw new AuthorizationException("관리자에게만 허용된 작업입니다.");
        }
        service.removeById(id);
    }
}
