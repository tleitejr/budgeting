package dio.budgeting.infra.persistence.entity;

import java.util.UUID;

import dio.budgeting.domain.Category;
import dio.budgeting.domain.Transaction;
import dio.budgeting.domain.TransactionId;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor 
@AllArgsConstructor 
public class TransactionEntity {

  @Id
  private UUID id;
  private String description;
  private long amount;

  @Enumerated(EnumType.STRING)
  private Category category;

  public static TransactionEntity from(Transaction transaction) {
    return new TransactionEntity(
      transaction.getId().id(),
      transaction.getDescription(),
      transaction.getAmount(),
      transaction.getCategory()
    );
  }

  public Transaction toDomain() {
    return new Transaction(
      new TransactionId(this.id),
      this.description,
      this.amount,
      this.category
    );
  }
}
