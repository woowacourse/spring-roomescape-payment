package roomescape.global.ui;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminPageController {

    @GetMapping
    public String adminHomePage() {
        return "admin/index";
    }

    @GetMapping("/reservation")
    public String adminReservationPage() {
        return "admin/reservation-new";
    }

    @GetMapping("/waiting")
    public String adminWaitingPage() {
        return "admin/waiting";
    }

    @GetMapping("/theme")
    public String adminThemePage() {
        return "admin/theme";
    }

    @GetMapping("/time")
    public String adminTimePage() {
        return "admin/time";
    }
}
