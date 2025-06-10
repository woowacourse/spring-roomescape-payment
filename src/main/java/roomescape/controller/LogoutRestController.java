package roomescape.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import roomescape.controller.api.LogoutRestControllerInterface;

@RestController
public class LogoutRestController implements LogoutRestControllerInterface {

    @Override
    public ResponseEntity<Void> logout(
            final HttpServletResponse response
    ) {
        final Cookie cookie = new Cookie("token", null);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);

        return ResponseEntity.ok().build();
    }
}
