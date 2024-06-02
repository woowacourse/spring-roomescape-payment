package roomescape.member.service;

import java.util.List;

import org.springframework.stereotype.Service;

import roomescape.member.domain.Members;
import roomescape.member.dto.MemberResponse;
import roomescape.member.repository.MemberRepository;

@Service
public class MemberService {

    private final MemberRepository memberRepository;

    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }


    public List<MemberResponse> findAll() {
        return new Members(memberRepository.findAll()).getMembers().stream()
                .map(MemberResponse::from)
                .toList();
    }
}
