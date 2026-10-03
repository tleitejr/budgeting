package dio.budgeting.application;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import dio.budgeting.application.input.PersistTransactionInput;
import dio.budgeting.application.output.TransactionOutput;
import dio.budgeting.domain.Transaction;
import dio.budgeting.domain.TransactionRepository;
import dio.budgeting.infra.audit.AudioAuditService;

@Service
public class PersistTransactionUseCase {
  private final TransactionRepository repository;
  private final AudioAuditService audioAuditService;

  public PersistTransactionUseCase(
    TransactionRepository repository,
    AudioAuditService audioAuditService
  ) {
    this.repository = repository;
    this.audioAuditService = audioAuditService;
  }

  @Tool(
    name = "persist-transaction",
    description = "Persiste uma nova transação financeira."
  )
  @Transactional
  public TransactionOutput execute(PersistTransactionInput input) {
    var transaction = repository.save(
      new Transaction(
        input.description(),
        input.amount(),
        input.category()
      )
    );
    audioAuditService.linkTransaction(transaction.getId().id());

    return TransactionOutput.from(transaction);
  }
}
