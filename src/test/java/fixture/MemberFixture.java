package fixture;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import roomescape.member.entity.Member;
import roomescape.member.entity.RoleType;

public class MemberFixture {

    private static final String DEFAULT_EMAIL = "@test.com";

    public static Member create(RoleType roleType) {
        return new Member(
                createRandomString(5),
                createRandomString(5) + DEFAULT_EMAIL,
                createRandomString(5),
                roleType
        );
    }

    public static Member createDefault() {
        return create(RoleType.USER);
    }

    public static List<Member> createDefaultList(int size) {
        return IntStream.range(0, size)
                .mapToObj(i -> createDefault())
                .collect(Collectors.toList());
    }

    private static String createRandomString(int length) {
        return java.util.UUID.randomUUID().toString().substring(0, length);
    }
}
