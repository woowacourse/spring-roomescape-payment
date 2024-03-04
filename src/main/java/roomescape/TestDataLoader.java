package roomescape;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import roomescape.member.Member;
import roomescape.member.MemberRepository;
import roomescape.reservation.Reservation;
import roomescape.reservation.ReservationRepository;
import roomescape.theme.Theme;
import roomescape.theme.ThemeRepository;
import roomescape.time.Time;
import roomescape.time.TimeRepository;
import roomescape.waiting.Waiting;
import roomescape.waiting.WaitingRepository;

@Profile("test")
@Component
public class TestDataLoader implements CommandLineRunner {
    private MemberRepository memberRepository;
    private ThemeRepository themeRepository;
    private TimeRepository timeRepository;
    private ReservationRepository reservationRepository;
    private WaitingRepository waitingRepository;

    public TestDataLoader(MemberRepository memberRepository, ThemeRepository themeRepository, TimeRepository timeRepository, ReservationRepository reservationRepository, WaitingRepository waitingRepository) {
        this.memberRepository = memberRepository;
        this.themeRepository = themeRepository;
        this.timeRepository = timeRepository;
        this.reservationRepository = reservationRepository;
        this.waitingRepository = waitingRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        Member admin = memberRepository.save(new Member("어드민", "admin@email.com", "password", "ADMIN"));
        Member brown = memberRepository.save(new Member("브라운", "brown@email.com", "password", "USER"));
        Member brie = memberRepository.save(new Member("브리", "brie@email.com", "password", "USER"));
        Member neo = memberRepository.save(new Member("네오", "neo@email.com", "password", "USER"));

        Theme theme1 = themeRepository.save(new Theme("테마1", "테마1 설명"));
        Theme theme2 = themeRepository.save(new Theme("테마2", "테마2 설명"));
        Theme theme3 = themeRepository.save(new Theme("테마3", "테마3 설명"));

        Time time1 = timeRepository.save(new Time("10:00"));
        Time time2 = timeRepository.save(new Time("12:00"));
        Time time3 = timeRepository.save(new Time("14:00"));
        Time time4 = timeRepository.save(new Time("16:00"));
        Time time5 = timeRepository.save(new Time("18:00"));
        Time time6 = timeRepository.save(new Time("20:00"));

        reservationRepository.save(new Reservation(admin, "", "2024-03-01", time1, theme1));
        reservationRepository.save(new Reservation(admin, "", "2024-03-01", time2, theme2));
        reservationRepository.save(new Reservation(admin, "", "2024-03-01", time3, theme3));
        reservationRepository.save(new Reservation(brown, "", "2024-03-02", time2, theme2));

        waitingRepository.save(new Waiting(theme2, admin.getId(), "2024-03-02", "12:00"));
        waitingRepository.save(new Waiting(theme2, brown.getId(), "2024-03-02", "12:00"));
    }
}
