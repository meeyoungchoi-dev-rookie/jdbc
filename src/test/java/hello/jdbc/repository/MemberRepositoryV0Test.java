package hello.jdbc.repository;

import hello.jdbc.domain.Member;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.*;

@Slf4j
class MemberRepositoryV0Test {

    MemberRepositoryV0 repository = new MemberRepositoryV0();

    @Test
    void crud() throws SQLException {
        // save
        Member member =  new Member("memberV300", 10000);
        repository.save(member);

        // findById
        Member findMember = repository.findById(member.getMemberId());
        log.info("findMember = {}", findMember);
        log.info("member != findMember {}", member == findMember );
        assertThat(findMember).isEqualTo(member); // 내부에서 equals 메서드를 호출하여 비교한다

        // update : money 10000 -> 20000
        repository.update(member.getMemberId(),  20000);
        Member updatedMember = repository.findById(member.getMemberId()); // 여기서 새로운 Member 객체가 만들어 진다 따라서 두개의 인스턴스가 같을 수 없다
//        assertThat(member).isEqualTo(updatedMember);
        //따라서 객체의 인스턴스를 비교하면 결과가 false가 나온다
        // 직접 udpate된 금액이 맞는지 비교해야 한다
        assertThat(updatedMember.getMoney()).isEqualTo(20000);

        //delete
        repository.delete(member.getMemberId());
        Assertions.assertThatThrownBy(() -> repository.findById(member.getMemberId())).isInstanceOf(NoSuchElementException.class);
    }
}