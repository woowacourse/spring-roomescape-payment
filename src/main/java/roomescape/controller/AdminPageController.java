package roomescape.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Tag(name = "관리자 페이지 API", description = "관리자 페이지 API 입니다.")
@Controller
public class AdminPageController {
    @Operation(summary = "관리자 메인 페이지", description = "관리자 메인 페이지를 조회합니다.")
    @GetMapping("/admin")
    public String mainPage() {
        return "admin/index";
    }

    @Operation(summary = "관리자 예약 페이지", description = "관리자 예약 페이지를 조회합니다.")
    @GetMapping("/admin/reservation")
    public String reservationPage() {
        return "admin/reservation-new";
    }

    @Operation(summary = "시간 관리 페이지", description = "시간 관리 페이지를 조회합니다.")
    @GetMapping("/admin/time")
    public String reservationTimePage() {
        return "admin/time";
    }

    @Operation(summary = "테마 관리 페이지", description = "테마 관리 페이지를 조회합니다.")
    @GetMapping("/admin/theme")
    public String themePage() {
        return "admin/theme";
    }

    @Operation(summary = "예약 대기 관리 페이지", description = "예약 대기 관리 페이지를 조회합니다.")
    @GetMapping("/admin/reservation-waiting")
    public String reservationWaitingPage() {
        return "admin/waiting";
    }
}
