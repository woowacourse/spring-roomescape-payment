package roomescape.controller;

import static roomescape.exception.ExceptionType.DUPLICATE_RESERVATION;
import static roomescape.exception.ExceptionType.NOT_FOUND_MEMBER;
import static roomescape.exception.ExceptionType.NOT_FOUND_RESERVATION_TIME;
import static roomescape.exception.ExceptionType.NOT_FOUND_THEME;
import static roomescape.exception.ExceptionType.PAST_TIME_RESERVATION;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import roomescape.annotation.ApiSuccessResponse;
import roomescape.annotation.ErrorApiResponse;
import roomescape.dto.ReservationRequest;
import roomescape.dto.ReservationResponse;
import roomescape.service.ReservationService;

@Controller
@RequestMapping("/admin")
@Tag(name = "관리자 예약", description = "관리자용 예약 API")
public class AdminController {
    private final ReservationService reservationService;

    public AdminController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @GetMapping
    public String mainPage() {
        return "admin/index";
    }

    @GetMapping("/reservation")
    public String reservationPage() {
        return "admin/reservation-new";
    }

    @PostMapping("/reservations")
    @Operation(summary = "관리자 예약 추가", description = "관리자가 예약을 추가 할 때 사용하는 API")
    @ErrorApiResponse({NOT_FOUND_RESERVATION_TIME, NOT_FOUND_THEME, NOT_FOUND_MEMBER, DUPLICATE_RESERVATION,
            PAST_TIME_RESERVATION})
    @ApiSuccessResponse(status = HttpStatus.CREATED, bodyType = ReservationResponse.class, body = """
            {
              "id": 1,
              "name": "예약자 이름",
              "date": "2024-06-08",
              "time": {
                "id": 1,
                "startAt": "19:30"
              },
              "theme": {
                "id": 1,
                "name": "테마 이름",
                "description": "테마 설명",
                "thumbnail": "https://thumbnail"
              }
            }
            """)
    public ResponseEntity<ReservationResponse> saveReservation(@RequestBody ReservationRequest reservationRequest) {
        ReservationResponse saved = reservationService.save(reservationRequest);
        return ResponseEntity.created(URI.create("/reservations/" + saved.id()))
                .body(saved);
    }

    @GetMapping("/reservations")
    @ResponseBody
    @Operation(summary = "관리자 예약 조회", description = "관리자가 회원, 테마, 기간을 기준으로 예약을 조회할 때 사용하는 API")
    @ApiSuccessResponse(bodyType = ReservationResponse.class, body = """
            [
                {
                  "id": 1,
                  "name": "예약자 이름",
                  "date": "2024-06-08",
                  "time": {
                    "id": 1,
                    "startAt": "19:30"
                  },
                  "theme": {
                    "id": 1,
                    "name": "테마 이름",
                    "description": "테마 설명",
                    "thumbnail": "https://thumbnail"
                  }
                },
                {
                  "id": 2,
                  "name": "예약자 이름2",
                  "date": "2024-06-09",
                  "time": {
                    "id": 1,
                    "startAt": "19:30"
                  },
                  "theme": {
                    "id": 1,
                    "name": "테마 이름",
                    "description": "테마 설명",
                    "thumbnail": "https://thumbnail"
                  }
                }
            ]
            """)
    public List<ReservationResponse> search(@RequestParam long memberId,
                                            @RequestParam long themeId,
                                            @RequestParam LocalDate start,
                                            @RequestParam LocalDate end) {
        return reservationService.findByMemberAndThemeBetweenDates(memberId, themeId, start, end);
    }

    @GetMapping("/time")
    public String reservationTimePage() {
        return "admin/time";
    }

    @GetMapping("/theme")
    public String themePage() {
        return "admin/theme";
    }

    @GetMapping("/waiting")
    public String waitingManagePage() {
        return "admin/waiting";
    }
}
