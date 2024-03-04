package roomescape.member;

import auth.JwtUtils;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class MemberService {
    private MemberRepository memberRepository;
    private JwtUtils jwtUtils;

    public MemberService(MemberRepository memberRepository, JwtUtils jwtUtils) {
        this.memberRepository = memberRepository;
        this.jwtUtils = jwtUtils;
    }

    public MemberResponse createMember(MemberRequest memberRequest) {
        Member member = memberRepository.save(new Member(memberRequest.getName(), memberRequest.getEmail(), memberRequest.getPassword(), "USER"));
        return new MemberResponse(member.getId(), member.getName(), member.getEmail(), member.getRole());
    }

    public String login(LoginRequest loginRequest) {
        Member member = memberRepository.findByEmailAndPassword(loginRequest.getEmail(), loginRequest.getPassword());

        if (member == null) {
            throw new RuntimeException();
        }

        return jwtUtils.createToken(member.getId().toString(), Map.of("name", member.getName(), "role", member.getRole()));
    }

    public Member findMemberById(Long id) {
        return memberRepository.findById(id).orElseThrow(RuntimeException::new);
    }

    public MemberResponse findResponseById(Long id) {
        Member member = memberRepository.findById(id).orElse(new Member());
        return new MemberResponse(member.getId(), member.getName(), member.getEmail(), member.getRole());
    }
}
