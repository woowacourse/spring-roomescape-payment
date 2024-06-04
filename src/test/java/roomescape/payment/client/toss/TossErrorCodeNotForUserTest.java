package roomescape.payment.client.toss;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class TossErrorCodeNotForUserTest {

    @Test
    @DisplayName("문자열이 enum에 존재하는 값: 참")
    void hasContains() {
        assertTrue(TossErrorCodeNotForUser.hasContains("INVALID_UNREGISTERED_SUBMALL"));
    }

    @Test
    @DisplayName("문자열이 enum에 존재하는 값: 거짓")
    void hasNotContains() {
        assertFalse(TossErrorCodeNotForUser.hasContains("EXCEED_MAX_ONE_DAY_AMOUNT"));
    }
}
