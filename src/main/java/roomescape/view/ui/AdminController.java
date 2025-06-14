package roomescape.view.ui;

import static roomescape.auth.domain.AuthRole.ADMIN;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import roomescape.auth.domain.RequiresRole;

@Controller
@RequestMapping("/admin")
@RequiresRole(authRoles = ADMIN)
public class AdminController {

    @GetMapping
    @Operation(summary = "관리자 홈페이지")
    public String home() {
        return "admin/index";
    }

    @GetMapping("/reservation")
    @Operation(summary = "관리자 예약 관리 페이지")
    public String reservation() {
        return "admin/reservation-new";
    }

    @GetMapping("/waiting")
    @Operation(summary = "관리자 예약 대기 관리 페이지")
    public String waiting() {
        return "admin/waiting";
    }

    @GetMapping("/time")
    @Operation(summary = "관리자 예약 시간 관리 페이지")
    public String time() {
        return "admin/time";
    }

    @GetMapping("/theme")
    @Operation(summary = "관리자 테마 관리 페이지")
    public String theme() {
        return "admin/theme";
    }
}
