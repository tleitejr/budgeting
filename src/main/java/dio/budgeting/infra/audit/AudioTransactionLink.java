package dio.budgeting.infra.audit;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "audio_transaction_link")
public class AudioTransactionLink {
  @Id
  private UUID id;
  private UUID audioAuditId;
  private UUID transactionId;
  private Instant linkedAt;

  protected AudioTransactionLink() {}

  public AudioTransactionLink(UUID audioAuditId, UUID transactionId) {
    this.id = UUID.randomUUID();
    this.audioAuditId = audioAuditId;
    this.transactionId = transactionId;
    this.linkedAt = Instant.now();
  }
}