package dio.budgeting.infra.audit;

import java.util.UUID;

import org.springframework.data.repository.CrudRepository;

public interface AudioTransactionLinkRepository extends CrudRepository<AudioTransactionLink, UUID> {}