package roomescape.theme.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import fixture.MemberFixture;
import fixture.PaymentFixture;
import fixture.ReservationFixture;
import fixture.ReservationTimeFixture;
import fixture.ThemeFixture;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.annotation.DirtiesContext;
import roomescape.global.error.exception.ConflictException;
import roomescape.member.entity.Member;
import roomescape.member.repository.MemberRepository;
import roomescape.payment.entity.Payment;
import roomescape.payment.repository.PaymentRepository;
import roomescape.reservation.entity.Reservation;
import roomescape.reservation.entity.ReservationTime;
import roomescape.reservation.repository.ReservationRepository;
import roomescape.reservation.repository.ReservationTimeRepository;
import roomescape.theme.dto.request.ThemeCreateRequest;
import roomescape.theme.dto.response.ThemeResponse;
import roomescape.theme.entity.Theme;
import roomescape.theme.repository.ThemeRepository;
import roomescape.theme.service.ThemeService;

@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class ThemeIntegrationTest {

    @Autowired
    private ThemeService themeService;
    @Autowired
    private ThemeRepository themeRepository;
    @Autowired
    private ReservationTimeRepository reservationTimeRepository;
    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private ReservationRepository reservationRepository;

    @Test
    @DisplayName("테마 생성 - 성공")
    void createTheme() {
        // given
        Theme theme = ThemeFixture.createDefault();
        var request = new ThemeCreateRequest(
                theme.getName(),
                theme.getDescription(),
                theme.getThumbnail()
        );

        // when
        var response = themeService.createTheme(request);

        // then
        assertAll(
                () -> assertThat(response.name()).isEqualTo(theme.getName()),
                () -> assertThat(response.description()).isEqualTo(theme.getDescription()),
                () -> assertThat(response.thumbnail()).isEqualTo(theme.getThumbnail())
        );
    }

    @Test
    @DisplayName("테마 생성 - 중복 이름으로 실패")
    void createThemeWithDuplicateName() {
        // given
        Theme theme = ThemeFixture.createDefault();
        var request = new ThemeCreateRequest(
                theme.getName(),
                theme.getDescription(),
                theme.getThumbnail()
        );
        themeService.createTheme(request);

        var duplicatedThemeNameRequest = new ThemeCreateRequest(
                theme.getName(),
                theme.getDescription() + "diff",
                theme.getThumbnail() + "diff"
        );

        // when & then
        assertThatThrownBy(() -> themeService.createTheme(duplicatedThemeNameRequest))
                .isInstanceOf(ConflictException.class)
                .hasMessage("이미 존재하는 테마 이름입니다.");
    }

    @Test
    @DisplayName("모든 테마 조회")
    void getAllThemes() {
        // given
        List<Theme> themes = ThemeFixture.createDefaultList(2);
        themeRepository.saveAll(themes);

        // when
        var responses = themeService.getAllThemes();

        // then
        assertAll(
                () -> assertThat(responses).hasSize(2),
                () -> assertThat(responses.get(0).name()).isEqualTo(themes.get(0).getName()),
                () -> assertThat(responses.get(1).name()).isEqualTo(themes.get(1).getName())
        );
    }

    @Test
    @DisplayName("인기 테마 조회 요청 - 인기순 정렬 확인")
    void getPopularThemes() {
        // given
        LocalDate yesterday = LocalDate.now().minusDays(1);

        ReservationTime reservationTime = ReservationTimeFixture.createDefault();
        reservationTimeRepository.save(reservationTime);

        List<Theme> themes = ThemeFixture.createDefaultList(2);
        themeRepository.saveAll(themes);

        Member member = MemberFixture.createDefault();
        memberRepository.save(member);

        Payment payment = PaymentFixture.createDefault();
        paymentRepository.save(payment);

        Reservation reservation = ReservationFixture.create(yesterday, reservationTime, themes.get(1), member, payment);
        reservationRepository.save(reservation);

        // when
        List<ThemeResponse> popularThemes = themeService.getPopularThemes(2);

        // then
        assertAll(
                () -> assertThat(popularThemes).hasSize(2),
                () -> assertThat(popularThemes.get(0).name()).isEqualTo(themes.get(1).getName()),
                () -> assertThat(popularThemes.get(1).name()).isEqualTo(themes.get(0).getName())
        );
    }

    @Test
    @DisplayName("인기 테마 조회 - limit 개수 확인")
    void getPopularThemesWhenExistsLimit() {
        // given
        List<Theme> themes = ThemeFixture.createDefaultList(10);
        themeRepository.saveAll(themes);

        // when
        var responses = themeService.getPopularThemes(2);

        // then
        assertThat(responses).hasSize(2);
    }

    @Test
    @DisplayName("테마 삭제")
    void deleteTheme() {
        // given
        Theme theme = ThemeFixture.createDefault();
        var request = new ThemeCreateRequest(
                theme.getName(),
                theme.getDescription(),
                theme.getThumbnail()
        );
        var createdTheme = themeService.createTheme(request);

        // when
        themeService.deleteTheme(createdTheme.id());

        // then
        assertThat(themeService.getAllThemes()).isEmpty();
    }
}
