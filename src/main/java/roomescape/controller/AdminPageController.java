package roomescape.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(name = "Admin-Page", description = "Loads pages related to admins")
@Controller
@RequestMapping("/admin")
public class AdminPageController {

    @Operation(
            summary = "Load index page for admin",
            description = "This endpoint loads index page for admin only.",
            tags = {"Page API"})
    @GetMapping
    public String welcomePage() {
        return "/admin/index";
    }

    @Operation(
            summary = "Load reservation page for admin",
            description = "This endpoint loads reservation page for admin only.",
            tags = {"Page API"})
    @GetMapping("/reservation")
    public String reservationPage() {
        return "/admin/reservation-new";
    }

    @Operation(
            summary = "Load time page for admin",
            description = "This endpoint loads time page for admin only.",
            tags = {"Page API"})
    @GetMapping("/time")
    public String timePage() {
        return "/admin/time";
    }

    @Operation(
            summary = "Load theme page for admin",
            description = "This endpoint loads theme page for admin only.",
            tags = {"Page API"})
    @GetMapping("/theme")
    public String themePage() {
        return "/admin/theme";
    }

    @Operation(
            summary = "Load waiting page for admin",
            description = "This endpoint loads waiting page for admin only.",
            tags = {"Page API"})
    @GetMapping("/waiting")
    public String waitingPage() {
        return "/admin/waiting";
    }
}
