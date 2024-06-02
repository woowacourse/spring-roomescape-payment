package roomescape.member.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminPageController {

    @GetMapping
    public String adminPage() {
        return "admin/index";
    }

    @GetMapping("/reservation")
    public String adminReservationPage() {
        return "admin/reservation-new";
    }

    @GetMapping("/time")
    public String timePage() {
        return "admin/time";
    }

    @GetMapping("/theme")
    public String adminThemePage() {
        return "admin/theme";
    }

    @GetMapping("/waiting")
    public String adminWaitingManagePage() {
        return "admin/waiting";
    }
}
