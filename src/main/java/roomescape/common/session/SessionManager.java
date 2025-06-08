package roomescape.common.session;

import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class SessionManager {
    public void saveToSession(HttpSession session, String key, Object value) {
        session.setAttribute(key, value);
        log.info("세션 저장 성공 key={}, value={}", key, value);
    }

    public Object getFromSession(HttpSession session, String key) {
        return session.getAttribute(key);
    }

    public void removeFromSession(HttpSession session, String key) {
        session.removeAttribute(key);
    }
}
