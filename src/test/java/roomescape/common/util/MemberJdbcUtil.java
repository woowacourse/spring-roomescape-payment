package roomescape.common.util;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import roomescape.member.domain.Member;

@Component
public class MemberJdbcUtil {

    private final JdbcTemplate jdbcTemplate;

    public MemberJdbcUtil(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void saveMemberAsKaki() {
        String sql = "insert into member (name, email, password, role) values ('카키', 'kaki@email.com', '1234', 'MEMBER')";

        jdbcTemplate.update(sql);
    }

    public void saveAdminMember() {
        String sql = "insert into member (name, email, password, role) values ('어드민', 'admin@email.com', '1234', 'ADMIN')";

        jdbcTemplate.update(sql);
    }

    public void saveMember(Member member) {
        String name = member.getName();
        String email = member.getEmail();
        String password = member.getPassword();
        String role = member.getRole().name();

        String sql = "insert into member (name, email, password, role) values (?, ?, ?, ?)";

        jdbcTemplate.update(sql, name, email, password, role);
    }
}
