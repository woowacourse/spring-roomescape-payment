package roomescape.common.session;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;

@Component
public class SessionManager {
    public void saveToSession(HttpSession session, String key, Object value) {
        session.setAttribute(key, value);
    }

    public Object getFromSession(HttpSession session, String key) {
        return session.getAttribute(key);
    }

    public void removeFromSession(HttpSession session, String key) {
        session.removeAttribute(key);
    }
}
