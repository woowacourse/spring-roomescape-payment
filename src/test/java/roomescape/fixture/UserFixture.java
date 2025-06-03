package roomescape.fixture;

import org.springframework.test.util.ReflectionTestUtils;
import roomescape.domain.user.User;
import roomescape.domain.user.UserRole;

public class UserFixture {

    public static User CREATE_USER_1() {
        User user = User.register("사용자1", "user1@test.com", "password1");
        ReflectionTestUtils.setField(user, "id", 1L);
        return user;
    }

    public static User CREATE_USER_2() {
        User user = User.register("사용자2", "user2@test.com", "password2");
        ReflectionTestUtils.setField(user, "id", 2L);
        return user;
    }

    public static User CREATE_USER_3() {
        User user = User.register("사용자3", "user3@test.com", "password3");
        ReflectionTestUtils.setField(user, "id", 3L);
        return user;
    }

    public static User CREATE_USER_4() {
        User user = User.register("사용자4", "user4@test.com", "password4");
        ReflectionTestUtils.setField(user, "id", 4L);
        return user;
    }

    public static User CREATE_ADMIN_1() {
        User admin = User.register("관리자1", "admin1@test.com", "adminpw1");
        ReflectionTestUtils.setField(admin, "id", 5L);
        ReflectionTestUtils.setField(admin, "role", UserRole.ADMIN);
        return admin;
    }

    public static User CREATE_ADMIN_2() {
        User admin = User.register("관리자2", "admin2@test.com", "adminpw2");
        ReflectionTestUtils.setField(admin, "id", 6L);
        ReflectionTestUtils.setField(admin, "role", UserRole.ADMIN);
        return admin;
    }
}
