package dio.budgeting.infra.http;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import dio.budgeting.application.ListTransactionByCategoryUseCase;
import dio.budgeting.application.PersistTransactionUseCase;
import dio.budgeting.domain.Category;
import dio.budgeting.infra.http.request.TransactionRequest;
import dio.budgeting.infra.http.response.TransactionResponse;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;



@RestController
@RequestMapping("/transactions")
public class TransactionController {
  private final PersistTransactionUseCase persistTransactionUseCase;
  private final ListTransactionByCategoryUseCase listTransactionByCategoryUseCase;

  public TransactionController(
    PersistTransactionUseCase persistTransactionUseCase,
    ListTransactionByCategoryUseCase listTransactionByCategoryUseCase
  ) {
    this.persistTransactionUseCase = persistTransactionUseCase;
    this.listTransactionByCategoryUseCase = listTransactionByCategoryUseCase;
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public TransactionResponse createTransaction(@RequestBody TransactionRequest request) {
    var transaction = persistTransactionUseCase.execute(request.toInput());
    return TransactionResponse.from(transaction);
  }

  @GetMapping("/{category}")
  @ResponseStatus(HttpStatus.OK)
  public List<TransactionResponse> readTransactions(@PathVariable Category category) {
    return listTransactionByCategoryUseCase
      .execute(category)
      .stream()
      .map(TransactionResponse::from)
      .toList();
  }
  
}
