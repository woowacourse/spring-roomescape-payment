package roomescape.controller.api;

import java.net.URI;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import roomescape.dto.timeslot.request.TimeSlotConditionRequest;
import roomescape.dto.timeslot.request.TimeSlotRequest;
import roomescape.dto.timeslot.response.TimeSlotConditionResponse;
import roomescape.dto.timeslot.response.TimeSlotResponse;
import roomescape.service.TimeSlotService;

@Slf4j
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
        log.info("예약 시간 전체 조회 요청 수신");
        List<TimeSlotResponse> response = reservationTimeService.getTimeSlots();

        log.info("예약 시간 전체 조회 완료: {}건", response.size());
        return ResponseEntity.ok(response);
    }

    @GetMapping(consumes = {"application/json"})
    public ResponseEntity<List<TimeSlotConditionResponse>> getTimeSlots(final TimeSlotConditionRequest request) {
        log.info("조건 기반 예약 시간 조회 요청 수신: {}", request);
        List<TimeSlotConditionResponse> responses = reservationTimeService.getTimesWithCondition(request);

        log.info("조건 기반 예약 시간 조회 완료: {}건", responses.size());
        return ResponseEntity.ok().body(responses);
    }

    @PostMapping
    public ResponseEntity<TimeSlotResponse> createTimeSlot(@RequestBody final TimeSlotRequest request) {
        log.info("예약 시간 생성 요청 수신: startAt={}", request.startAt());
        TimeSlotResponse response = reservationTimeService.createTimeSlot(request);

        log.info("예약 시간 생성 완료: id={}, startAt={}", response.id(), response.startAt());
        return ResponseEntity.created(URI.create(GET_ADMIN_TIME)).body(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTimeSlotById(@PathVariable("id") final Long id) {
        log.info("예약 시간 삭제 요청 수신: id={}", id);
        reservationTimeService.deleteTimeSlotById(id);

        log.info("예약 시간 삭제 완료: id={}", id);
        return ResponseEntity.noContent().build();
    }
}
