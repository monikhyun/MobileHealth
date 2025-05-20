package mobile.health.healine.Repository;


import mobile.health.healine.Entity.MemberRecord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRecordRepository extends JpaRepository<MemberRecord, Long> {
}
