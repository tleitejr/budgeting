package dio.budgeting.application;

import dio.budgeting.application.input.PersistTransactionInput;
import dio.budgeting.application.output.TransactionOutput;
import dio.budgeting.domain.Transaction;
import dio.budgeting.domain.TransactionRepository;

public class PersistTransactionUseCase {
  private final TransactionRepository repository;

  public PersistTransactionUseCase(TransactionRepository repository) {
    this.repository = repository;
  }

  public TransactionOutput execute(PersistTransactionInput input) {
    var transaction = repository.save(
      new Transaction(
        input.description(),
        input.amount(),
        input.category()
      )
    );

    return TransactionOutput.from(transaction);
  }
}
