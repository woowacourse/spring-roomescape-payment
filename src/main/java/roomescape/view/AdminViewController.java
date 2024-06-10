package roomescape.view;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(name = "관리자 페이지 API", description = "관리자 페이지 관련 API")
@Controller
@RequestMapping("/admin")
public class AdminViewController {

    @Operation(summary = "관리자 메인 페이지 API")
    @GetMapping
    public String getMainPage() {
        return "admin/index";
    }

    @Operation(summary = "관리자 예약 페이지 API")
    @GetMapping("/reservation")
    public String getReservationPage() {
        return "admin/reservation-new";
    }

    @Operation(summary = "관리자 예약 시간 페이지 API")
    @GetMapping("/time")
    public String getTimePage() {
        return "admin/time";
    }

    @Operation(summary = "관리자 테마 페이지 API")
    @GetMapping("/theme")
    public String getAdminMainPage() {
        return "admin/theme";
    }

    @Operation(summary = "관리자 예약 대기 페이지 API")
    @GetMapping("/waiting")
    public String getWaitingMainPage() {
        return "admin/waiting";
    }
}
