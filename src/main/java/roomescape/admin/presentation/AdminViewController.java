package roomescape.admin.presentation;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import roomescape.auth.AdminOnly;

@Controller
public class AdminViewController {

    @GetMapping("/admin")
    @AdminOnly
    public String adminMainPage() {
        return "admin/index";
    }

    @GetMapping("/admin/reservation")
    @AdminOnly
    public String reservationPage() {
        return "admin/reservation-new";
    }

    @GetMapping("/admin/time")
    @AdminOnly
    public String reservationTimePage() {
        return "admin/time";
    }

    @GetMapping("/admin/theme")
    @AdminOnly
    public String themePage() {
        return "admin/theme";
    }

    @GetMapping("/admin/waiting")
    @AdminOnly
    public String waitingPage() {
        return "admin/waiting";
    }
}
