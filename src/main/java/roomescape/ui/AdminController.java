package roomescape.ui;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import roomescape.auth.aop.RequiredRoles;
import roomescape.user.domain.UserRole;

@Slf4j
@Controller
@RequiredRoles(UserRole.ADMIN)
@RequestMapping("/admin")
public class AdminController {

    @GetMapping
    public String getMainPage() {
        log.info("[ADMIN] 메인 페이지 진입");
        return "admin/index";
    }

    @GetMapping("/reservation")
    public String getReservationPage() {
        log.info("[ADMIN] 예약 관리 페이지 진입");
        return "admin/reservation-new";
    }

    @GetMapping("/reservation/waiting")
    public String getReservationWaitingPage() {
        log.info("[ADMIN] 대기 예약 관리 페이지 진입");
        return "admin/waiting";
    }

    @GetMapping("/time")
    public String getTimePage() {
        log.info("[ADMIN] 시간 관리 페이지 진입");
        return "admin/time";
    }

    @GetMapping("/theme")
    public String getThemePage() {
        log.info("[ADMIN] 테마 관리 페이지 진입");
        return "admin/theme";
    }
}
