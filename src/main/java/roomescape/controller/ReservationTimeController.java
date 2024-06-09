package roomescape.controller;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static roomescape.exception.ExceptionType.DELETE_USED_TIME;
import static roomescape.exception.ExceptionType.DUPLICATE_RESERVATION_TIME;
import static roomescape.exception.ExceptionType.EMPTY_TIME;
import static roomescape.exception.ExceptionType.NOT_FOUND_THEME;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import roomescape.annotation.ApiSuccessResponse;
import roomescape.annotation.ErrorApiResponse;
import roomescape.dto.AvailableTimeResponse;
import roomescape.dto.ReservationTimeRequest;
import roomescape.dto.ReservationTimeResponse;
import roomescape.service.AvailableTimeService;
import roomescape.service.ReservationTimeService;

@RestController
@Tag(name = "예약 시간", description = "예약 시간 API")
public class ReservationTimeController {
    private final ReservationTimeService reservationTimeService;
    private final AvailableTimeService availableTimeService;

    public ReservationTimeController(ReservationTimeService reservationTimeService,
                                     AvailableTimeService availableTimeService) {
        this.reservationTimeService = reservationTimeService;
        this.availableTimeService = availableTimeService;
    }

    @PostMapping("/admin/times")
    @Operation(summary = "예약 시간 생성", description = "관리자가 예약 시간을 생성할 때 사용하는 API")
    @ErrorApiResponse({DUPLICATE_RESERVATION_TIME, EMPTY_TIME})
    @ApiSuccessResponse(status = CREATED, bodyType = ReservationTimeResponse.class, body = """
            {
              "id": 1,
              "startAt": "19:30"
            }
            """)
    public ResponseEntity<ReservationTimeResponse> save(@RequestBody ReservationTimeRequest reservationTimeRequest) {
        ReservationTimeResponse saved = reservationTimeService.save(reservationTimeRequest);
        return ResponseEntity.created(URI.create("/times/" + saved.id()))
                .body(saved);
    }

    @GetMapping("/times/all")
    @Operation(summary = "예약 시간 목록 조회", description = "관리자가 예약 시간목록을 조회할 때 사용하는 API")
    @ApiSuccessResponse(bodyType = ReservationTimeResponse.class, body = """
            [
                {
                  "id": 1,
                  "startAt": "19:30"
                },
                {
                  "id": 2,
                  "startAt": "20:30"
                }
            ]
            """)
    public List<ReservationTimeResponse> findAll() {
        return reservationTimeService.findAll();
    }

    @GetMapping(value = "/times", params = {"date", "themeId"})
    @Operation(summary = "예약 시간 및 예약 가능 여부 조회", description = "회원이 특정 날짜와 테마의 예약 시간과 예약 가능 여부를 조회하는 API")
    @ErrorApiResponse(NOT_FOUND_THEME)
    @ApiSuccessResponse(bodyType = AvailableTimeResponse.class, body = """
            [
                {
                  "id": 1,
                  "startAt": "19:30",
                  "isBooked": false
                },
                {
                  "id": 2,
                  "startAt": "20:30",
                  "isBooked": false
                }
            ]
            """)
    public List<AvailableTimeResponse> findByThemeAndDate(@RequestParam LocalDate date, @RequestParam long themeId) {
        return availableTimeService.findByThemeAndDate(date, themeId);
    }

    @DeleteMapping("/admin/times/{id}")
    @Operation(summary = "예약 시간 삭제", description = "관리자가 예약 시간을 삭제할 때 사용하는 API")
    @ErrorApiResponse(DELETE_USED_TIME)
    @ApiSuccessResponse(status = NO_CONTENT)
    public ResponseEntity<Void> delete(@PathVariable long id) {
        reservationTimeService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
