package dio.budgeting.infra.audit;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.UUID;

import org.junit.jupiter.api.Test;

class AudioAuditServiceTest {
  @Test
  void linksTransactionsOnlyWhileAnAudioOperationIsActive() {
    var linkRepository = mock(AudioTransactionLinkRepository.class);
    var service = new AudioAuditService(
      mock(AudioStorage.class),
      mock(AudioOperationAuditRepository.class),
      linkRepository
    );
    var audit = new AudioOperationAudit(
      "user-1",
      "audio.wav",
      new AudioStorage.StoredAudio("key.wav", "digest", 10),
      "mobile",
      "audio/wav"
    );
    var transactionId = UUID.randomUUID();

    service.activate(audit);
    service.linkTransaction(transactionId);
    service.clearContext();
    service.linkTransaction(UUID.randomUUID());

    verify(linkRepository, times(1)).save(any(AudioTransactionLink.class));
  }
}