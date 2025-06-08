package roomescape.auth.presentation;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import roomescape.admin.domain.Admin;
import roomescape.auth.annotation.LoginAdmin;
import roomescape.auth.application.LoginService;
import roomescape.auth.dto.info.LoginAdminInfo;
import roomescape.auth.dto.request.LoginRequest;
import roomescape.auth.dto.response.LoginCheckResponse;

@RestController
@RequestMapping("/admin/login")
public class AdminLoginController {

    private final LoginService loginService;

    public AdminLoginController(final LoginService loginService) {
        this.loginService = loginService;
    }

    @PostMapping
    public ResponseEntity<Void> login(@RequestBody final LoginRequest request) {
        String token = loginService.createAdminToken(request);

        ResponseCookie cookie = ResponseCookie.from("token", token)
                .httpOnly(true)
                .path("/")
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .build();
    }

    @GetMapping("/check")
    public ResponseEntity<LoginCheckResponse> checkLogin(@LoginAdmin final LoginAdminInfo info) {
        Admin admin = loginService.findByAdminId(info.id());
        return ResponseEntity.ok().body(new LoginCheckResponse(admin.getName()));
    }
}
