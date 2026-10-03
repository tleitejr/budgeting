package dio.budgeting.infra.audit;

import java.io.IOException;
import java.util.UUID;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class AudioAuditService {
  private final AudioStorage storage;
  private final AudioOperationAuditRepository repository;
  private final AudioTransactionLinkRepository linkRepository;
  private static final ThreadLocal<UUID> ACTIVE_AUDIT_ID = new ThreadLocal<>();

  public AudioAuditService(
    AudioStorage storage,
    AudioOperationAuditRepository repository,
    AudioTransactionLinkRepository linkRepository
  ) {
    this.storage = storage;
    this.repository = repository;
    this.linkRepository = linkRepository;
  }

  public AudioOperationAudit recordReceived(MultipartFile file, String channel) throws IOException {
    AudioStorage.StoredAudio stored;
    try (var input = file.getInputStream()) {
      stored = storage.store(input, file.getOriginalFilename());
    }
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String userId = authentication == null ? "unknown" : authentication.getName();
    return repository.save(new AudioOperationAudit(
      userId,
      safeFilename(file.getOriginalFilename()),
      stored,
      normalizeChannel(channel),
      file.getContentType()
    ));
  }

  public void complete(AudioOperationAudit audit) {
    audit.complete();
    repository.save(audit);
  }

  public void fail(AudioOperationAudit audit, Exception exception) {
    audit.fail(exception.getClass().getSimpleName());
    repository.save(audit);
  }

  public void activate(AudioOperationAudit audit) {
    ACTIVE_AUDIT_ID.set(audit.getId());
  }

  public void clearContext() {
    ACTIVE_AUDIT_ID.remove();
  }

  public void linkTransaction(UUID transactionId) {
    UUID auditId = ACTIVE_AUDIT_ID.get();
    if (auditId != null) {
      linkRepository.save(new AudioTransactionLink(auditId, transactionId));
    }
  }

  private String normalizeChannel(String channel) {
    if (channel == null || channel.isBlank()) {
      return "unknown";
    }
    return channel.length() > 64 ? channel.substring(0, 64) : channel;
  }

  private String safeFilename(String filename) {
    if (filename == null || filename.isBlank()) {
      return "unknown";
    }
    String normalized = filename.replace('\\', '/');
    String basename = normalized.substring(normalized.lastIndexOf('/') + 1);
    return basename.length() > 255 ? basename.substring(0, 255) : basename;
  }
}