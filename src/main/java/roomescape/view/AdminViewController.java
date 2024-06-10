package roomescape.view;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@Tag(name = "AdminView API", description = "관리자 화면 관련 API")
public class AdminViewController {

    @Operation(summary = "관리자 메인 페이지 연결 API")
    @GetMapping("/admin")
    public String adminMainPage() {
        return "admin/index";
    }

    @Operation(summary = "관리자 예약 관리 페이지 연결 API")
    @GetMapping("/admin/reservation")
    public String reservationPage() {
        return "admin/reservation-new";
    }

    @Operation(summary = "관리자 예약 대기 관리 페이지 연결 API")
    @GetMapping("admin/reservation/waiting")
    public String reservationWaitingPage() {
        return "admin/waiting";
    }

    @Operation(summary = "관리자 예약 시간 관리 페이지 연결 API")
    @GetMapping("/admin/time")
    public String reservationTimePage() {
        return "admin/time";
    }

    @Operation(summary = "관리자 테마 관리 페이지 연결 API")
    @GetMapping("/admin/theme")
    public String themePage() {
        return "admin/theme";
    }
}
