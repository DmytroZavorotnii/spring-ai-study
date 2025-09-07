package zavorotnii.dmytro.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import zavorotnii.dmytro.model.document.LoadedDocument;

public interface LoadedDocumentRepository extends JpaRepository<LoadedDocument, String> {
    boolean existsByFilenameAndContentHash(String filename, String contentHash);
}
