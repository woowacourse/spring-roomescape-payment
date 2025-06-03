package roomescape.infrastructure;

import org.springframework.stereotype.Repository;
import roomescape.domain.Member;
import roomescape.domain.repository.MemberRepository;

import java.util.List;
import java.util.Optional;

@Repository
public class MemberRepositoryAdapter implements MemberRepository {
    private final JpaMemberRepository jpaMemberRepository;

    public MemberRepositoryAdapter(JpaMemberRepository jpaMemberRepository) {
        this.jpaMemberRepository = jpaMemberRepository;
    }

    @Override
    public Member save(Member member) {
        return jpaMemberRepository.save(member);
    }

    @Override
    public List<Member> findAll() {
        return jpaMemberRepository.findAll();
    }

    @Override
    public Optional<Member> findById(Long id) {
        return jpaMemberRepository.findById(id);
    }

    @Override
    public Optional<Member> findByEmail(String email) {
        return jpaMemberRepository.findByEmail(email);
    }

    @Override
    public boolean existsByEmail(String email) {
        return jpaMemberRepository.existsByEmail(email);
    }


}
