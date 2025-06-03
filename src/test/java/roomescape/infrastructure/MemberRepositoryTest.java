package roomescape.infrastructure;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import roomescape.business.model.entity.Member;
import roomescape.business.model.vo.Id;
import roomescape.test_util.JpaTestUtil;

@DataJpaTest
@Import(JpaTestUtil.class)
class MemberRepositoryTest {

    private final MemberRepository sut;
    private final JpaTestUtil testUtil;

    @Autowired
    MemberRepositoryTest(MemberRepository sut, JpaTestUtil testUtil) {
        this.sut = sut;
        this.testUtil = testUtil;
    }

    @AfterEach
    void tearDown() {
        testUtil.deleteAll();
    }

    @Test
    void 사용자를_저장하고_조회할_수_있다() {
        // given
        final Member member = Member.create("테스트유저", "test@example.com", "password123");

        // when, then
        assertThatCode(() -> sut.save(member))
                .doesNotThrowAnyException();
    }

    @Test
    void 모든_사용자를_조회할_수_있다() {
        // given
        testUtil.insertUser("1", "유저일");
        testUtil.insertUser("2", "유저이");

        // when
        final List<Member> result = sut.findAll();

        // then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getId().value()).isEqualTo("1");
        assertThat(result.get(1).getId().value()).isEqualTo("2");
    }

    @Test
    void ID로_사용자를_조회할_수_있다() {
        // given
        testUtil.insertUser("1", "유저일");

        // when
        final Optional<Member> result = sut.findById(Id.create("1"));

        // then
        assertThat(result).isPresent();
        final Member member = result.get();
        assertThat(member.getId().value()).isEqualTo("1");
        assertThat(member.getName().value()).isEqualTo("유저일");
        assertThat(member.getEmail().value()).isEqualTo("유저일@email.com");
    }

    @Test
    void 존재하지_않는_ID로_사용자를_조회하면_빈_Optional을_반환한다() {
        // when
        final Optional<Member> result = sut.findById(Id.create("999"));

        // then
        assertThat(result).isEmpty();
    }

    @Test
    void 이메일로_사용자를_조회할_수_있다() {
        // given
        final String name = "유저일";
        final String email = name + "@email.com";
        testUtil.insertUser("1", name);

        // when
        final Optional<Member> result = sut.findByEmail_Value(email);

        // then
        assertThat(result).isPresent();
        final Member member = result.get();
        assertThat(member.getName().value()).isEqualTo(name);
        assertThat(member.getEmail().value()).isEqualTo(email);
    }

    @Test
    void 존재하지_않는_이메일로_사용자를_조회하면_빈_Optional을_반환한다() {
        // when
        final Optional<Member> result = sut.findByEmail_Value("nonexistent@email.com");

        // then
        assertThat(result).isEmpty();
    }

    @Test
    void 이메일이_존재하는지_확인할_수_있다() {
        // given
        final String name = "유저";
        final String email = name + "@email.com";
        testUtil.insertUser("1", name);

        // when
        final boolean result = sut.existsByEmail_Value(email);

        // then
        assertThat(result).isTrue();
    }

    @Test
    void 존재하지_않는_이메일로_확인하면_false를_반환한다() {
        // when
        final boolean result = sut.existsByEmail_Value("nonexistent@email.com");

        // then
        assertThat(result).isFalse();
    }
}
