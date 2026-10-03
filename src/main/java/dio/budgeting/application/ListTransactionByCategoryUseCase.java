package dio.budgeting.application;

import java.util.List;

import org.springframework.stereotype.Service;

import dio.budgeting.application.output.TransactionOutput;
import dio.budgeting.domain.Category;
import dio.budgeting.domain.TransactionRepository;

@Service
public class ListTransactionByCategoryUseCase {
  private final TransactionRepository repository;

  public ListTransactionByCategoryUseCase(TransactionRepository repository) {
    this.repository = repository;
  }

  public List<TransactionOutput> execute(Category category) {
    return repository
      .fildAllByCategory(category)
      .stream()
      .map(TransactionOutput::from)
      .toList();
  }
}
