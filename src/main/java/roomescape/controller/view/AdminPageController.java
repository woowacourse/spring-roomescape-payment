package roomescape.controller.view;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import roomescape.config.auth.RoleAllowed;
import roomescape.domain.member.MemberRole;

@Tag(name = "[View] Admin")
@RequestMapping("/admin")
@Controller
public class AdminPageController {
    @RoleAllowed(MemberRole.ADMIN)
    @GetMapping
    @Operation(summary = "[관리자] 어드민 페이지 호출", description = "어드민 페이지를 호출한다.")
    public String getAdminPage() {
        return "admin/index";
    }

    @RoleAllowed(MemberRole.ADMIN)
    @GetMapping("/reservation")
    @Operation(summary = "[관리자] 회원 예약 관리 페이지 호출", description = "회원 예약 관리 페이지를 호출한다.")
    public String getReservationPage() {
        return "admin/reservation-new";
    }

    @RoleAllowed(MemberRole.ADMIN)
    @GetMapping("/time")
    @Operation(summary = "[관리자] 예약 가능 시간 관리 페이지 호출", description = "예약 가능 시간 관리 페이지를 호출한다.")
    public String getTimePage() {
        return "admin/time";
    }

    @RoleAllowed(MemberRole.ADMIN)
    @GetMapping("/theme")
    @Operation(summary = "[관리자] 테마 관리 페이지 호출", description = "테마 관리 페이지를 호출한다.")
    public String getThemePage() {
        return "admin/theme";
    }

    @RoleAllowed(MemberRole.ADMIN)
    @GetMapping("/waiting")
    @Operation(summary = "[관리자] 예약 대기 관리 페이지 호출", description = "예약 대기 관리 페이지를 호출한다.")
    public String getWaitingPage() {
        return "admin/waiting";
    }
}
