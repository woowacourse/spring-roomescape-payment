package roomescape;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import roomescape.member.Member;
import roomescape.member.MemberRepository;

@Component
public class DataLoader implements CommandLineRunner {
    private MemberRepository memberRepository;

    public DataLoader(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        memberRepository.save(new Member("어드민", "admin@email.com", "password", "ADMIN"));
    }
}
