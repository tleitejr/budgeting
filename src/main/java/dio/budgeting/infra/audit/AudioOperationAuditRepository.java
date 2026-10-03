package dio.budgeting.infra.audit;

import java.util.UUID;

import org.springframework.data.repository.CrudRepository;

public interface AudioOperationAuditRepository extends CrudRepository<AudioOperationAudit, UUID> {}