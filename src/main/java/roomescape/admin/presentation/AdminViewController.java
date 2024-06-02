package roomescape.admin.presentation;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import roomescape.auth.AdminOnly;

@Controller
@RequestMapping("/admin")
public class AdminViewController {

    @GetMapping
    @AdminOnly
    public String adminMainPage() {
        return "admin/index";
    }

    @GetMapping("/reservation")
    @AdminOnly
    public String reservationPage() {
        return "admin/reservation-new";
    }

    @GetMapping("/time")
    @AdminOnly
    public String reservationTimePage() {
        return "admin/time";
    }

    @GetMapping("/theme")
    @AdminOnly
    public String themePage() {
        return "admin/theme";
    }

    @GetMapping("/waiting")
    @AdminOnly
    public String waitingPage() {
        return "admin/waiting";
    }
}
