package dio.budgeting.infra.audit;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "audio_operation_audit")
public class AudioOperationAudit {
  @Id
  private UUID id;
  private Instant occurredAt;
  private String userId;
  private String sourceFilename;
  private String storageKey;
  private String sha256;
  private String channel;
  private String contentType;
  private long fileSize;

  @Enumerated(EnumType.STRING)
  private Status status;
  private String failureCode;

  protected AudioOperationAudit() {}

  public AudioOperationAudit(
    String userId,
    String sourceFilename,
    AudioStorage.StoredAudio storedAudio,
    String channel,
    String contentType
  ) {
    this.id = UUID.randomUUID();
    this.occurredAt = Instant.now();
    this.userId = userId;
    this.sourceFilename = sourceFilename;
    this.storageKey = storedAudio.key();
    this.sha256 = storedAudio.sha256();
    this.channel = channel;
    this.contentType = contentType;
    this.fileSize = storedAudio.size();
    this.status = Status.RECEIVED;
  }

  public void complete() {
    this.status = Status.COMPLETED;
  }

  public void fail(String code) {
    this.status = Status.FAILED;
    this.failureCode = code;
  }

  public UUID getId() {
    return id;
  }

  public enum Status { RECEIVED, COMPLETED, FAILED }
}