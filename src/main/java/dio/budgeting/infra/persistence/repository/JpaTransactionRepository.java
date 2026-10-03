package dio.budgeting.infra.persistence.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import dio.budgeting.domain.Category;
import dio.budgeting.domain.Transaction;
import dio.budgeting.domain.TransactionRepository;
import dio.budgeting.infra.persistence.entity.TransactionEntity;

@Repository
public class JpaTransactionRepository implements TransactionRepository {
  private final TransactionEntityRepository repository;

  public JpaTransactionRepository(TransactionEntityRepository repository) {
    this.repository = repository;
  }

  @Override
  public Transaction save(Transaction transaction) {
    var entity = TransactionEntity.from(transaction);
    return repository.save(entity).toDomain();
  }

  @Override
  public List<Transaction> fildAllByCategory(Category category) {
    return repository
      .findAllByCategory(category)
      .stream()
      .map(TransactionEntity::toDomain)
      .toList();
  }
}
