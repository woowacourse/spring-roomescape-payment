package roomescape.controller.view;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import roomescape.config.auth.RoleAllowed;
import roomescape.domain.member.MemberRole;

@RequestMapping("/admin")
@Controller
public class AdminPageController {
    @RoleAllowed(MemberRole.ADMIN)
    @GetMapping
    public String getAdminPage() {
        return "admin/index";
    }

    @RoleAllowed(MemberRole.ADMIN)
    @GetMapping("/reservation")
    public String getReservationPage() {
        return "admin/reservation-new";
    }

    @RoleAllowed(MemberRole.ADMIN)
    @GetMapping("/time")
    public String getTimePage() {
        return "admin/time";
    }

    @RoleAllowed(MemberRole.ADMIN)
    @GetMapping("/theme")
    public String getThemePage() {
        return "admin/theme";
    }

    @RoleAllowed(MemberRole.ADMIN)
    @GetMapping("/waiting")
    public String getWaitingPage() {
        return "admin/waiting";
    }
}
