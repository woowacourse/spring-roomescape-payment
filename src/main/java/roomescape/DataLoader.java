package roomescape;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import roomescape.member.Member;
import roomescape.member.MemberRepository;
import roomescape.theme.Theme;
import roomescape.theme.ThemeRepository;
import roomescape.time.Time;
import roomescape.time.TimeRepository;

@Component
public class DataLoader implements CommandLineRunner {
    private MemberRepository memberRepository;
    private ThemeRepository themeRepository;
    private TimeRepository timeRepository;

    public DataLoader(MemberRepository memberRepository,
                      ThemeRepository themeRepository,
                      TimeRepository timeRepository) {
        this.memberRepository = memberRepository;
        this.themeRepository = themeRepository;
        this.timeRepository = timeRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        memberRepository.save(new Member("어드민", "admin@email.com", "password", "ADMIN"));
        memberRepository.save(new Member("브라운", "brown@email.com", "password", "USER"));

        themeRepository.save(new Theme("테마1", "테마1 설명"));
        themeRepository.save(new Theme("테마2", "테마2 설명"));
        themeRepository.save(new Theme("테마3", "테마3 설명"));
        timeRepository.save(new Time("10:00"));
        timeRepository.save(new Time("12:00"));
        timeRepository.save(new Time("14:00"));
        timeRepository.save(new Time("16:00"));
    }
}
