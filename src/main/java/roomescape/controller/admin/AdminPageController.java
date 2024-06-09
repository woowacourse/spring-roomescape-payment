package roomescape.controller.admin;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(name = "어드민 페이지 API", description = "어드민 페이지 관련 API 입니다.")
@Controller
@RequestMapping("/admin")
public class AdminPageController {
    @Operation(summary = "어드민 메인 페이지")
    @GetMapping
    public String welcomePage() {
        return "admin/index";
    }

    @Operation(summary = "어드민 예약 관리 페이지")
    @GetMapping("/reservation")
    public String reservationPage() {
        return "admin/reservation-new";
    }

    @Operation(summary = "어드민 예약 시간 관리 페이지")
    @GetMapping("/time")
    public String timePage() {
        return "admin/time";
    }

    @Operation(summary = "어드민 테마 관리 페이지")
    @GetMapping("/theme")
    public String themePage() {
        return "admin/theme";
    }

    @Operation(summary = "어드민 예약 대기 관리 페이지")
    @GetMapping("/waiting")
    public String waitingPage() {
        return "admin/waiting";
    }

    @Operation(summary = "어드민 예약 취소 관리 페이지")
    @GetMapping("/reservation/canceled")
    public String canceledReservation() {
        return "admin/canceled-reservation";
    }
}
