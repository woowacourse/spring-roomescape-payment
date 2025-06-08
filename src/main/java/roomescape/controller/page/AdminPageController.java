package roomescape.controller.page;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminPageController {

    @GetMapping
    public String home() {
        return "admin/index";
    }

    @GetMapping("/reservation")
    public String reservationPage() {
        return "admin/reservation-new";
    }

    @GetMapping("/time")
    public String timePage() {
        return "admin/time";
    }

    @GetMapping("/theme")
    public String themePage() {
        return "admin/theme";
    }

    @GetMapping("/waiting")
    public String waitingPage() {
        return "admin/waiting";
    }
}
