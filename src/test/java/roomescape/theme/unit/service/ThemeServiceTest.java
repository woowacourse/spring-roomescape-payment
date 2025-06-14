package roomescape.theme.unit.service;

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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
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

@DataJpaTest
class ThemeServiceTest {

    @Autowired
    private ThemeRepository themeRepository;
    @Autowired
    private ReservationTimeRepository reservationTimeRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private ReservationRepository reservationRepository;

    private ThemeService themeService;

    @BeforeEach
    void setUp() {
        themeService = new ThemeService(themeRepository);
    }

    @Test
    @DisplayName("테마 생성 - 성공")
    void createTheme() {
        // given
        Theme theme = ThemeFixture.createDefault();
        var request = new ThemeCreateRequest(theme.getName(), theme.getDescription(), theme.getThumbnail());

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
    void createThemeFailWhenNameDuplicate() {
        // given
        Theme theme = ThemeFixture.createDefault();
        var request = new ThemeCreateRequest(theme.getName(), theme.getDescription(), theme.getThumbnail());
        themeService.createTheme(request);

        var duplicatedThemeNameRequest = new ThemeCreateRequest(
                theme.getName(),
                theme.getDescription() + "diff",
                theme.getThumbnail() + "diff"
        );

        // when & then
        assertThatThrownBy(() -> themeService.createTheme(duplicatedThemeNameRequest))
                .isInstanceOf(ConflictException.class);
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
