package roomescape.timeslot.presentation;

import jakarta.servlet.http.HttpServletRequest;
import java.net.URI;
import java.time.format.DateTimeParseException;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import roomescape.common.dto.response.ExceptionResponse;
import roomescape.timeslot.application.TimeSlotService;
import roomescape.timeslot.dto.request.TimeSlotConditionRequest;
import roomescape.timeslot.dto.request.TimeSlotRequest;
import roomescape.timeslot.dto.response.TimeSlotConditionResponse;
import roomescape.timeslot.dto.response.TimeSlotResponse;

@RestController
@RequestMapping("/times")
public class TimeSlotController {

    public static final String GET_ADMIN_TIME = "/admin/time";

    private final TimeSlotService reservationTimeService;

    public TimeSlotController(final TimeSlotService reservationTimeService) {
        this.reservationTimeService = reservationTimeService;
    }

    @GetMapping
    public ResponseEntity<List<TimeSlotResponse>> getTimeSlots() {
        List<TimeSlotResponse> response = reservationTimeService.getTimeSlots();
        return ResponseEntity.ok(response);
    }

    @GetMapping(consumes = {"application/json"})
    public ResponseEntity<List<TimeSlotConditionResponse>> getTimeSlots(final TimeSlotConditionRequest request) {
        List<TimeSlotConditionResponse> responses = reservationTimeService.getTimesWithCondition(request);
        return ResponseEntity.ok().body(responses);
    }

    @PostMapping
    public ResponseEntity<TimeSlotResponse> createTimeSlot(
            @RequestBody final TimeSlotRequest request) {
        TimeSlotResponse response = reservationTimeService.createTimeSlot(request);
        return ResponseEntity.created(URI.create(GET_ADMIN_TIME)).body(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTimeSlotById(@PathVariable("id") final Long id) {
        reservationTimeService.deleteTimeSlotById(id);
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler(value = DateTimeParseException.class)
    public ResponseEntity<ExceptionResponse> noMatchTimeType(final HttpServletRequest request) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(
                400, "[ERROR] 요청 시간 형식이 맞지 않습니다.", request.getRequestURI()
        );
        return ResponseEntity.badRequest().body(exceptionResponse);
    }
}
