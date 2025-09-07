package zavorotnii.dmytro.model.document;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoadedDocument {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(length = 255, nullable = false)
    private String filename;

    @Column(length = 64, nullable = false)
    private String contentHash;

    @Column(length = 10, nullable = false)
    private String documentType;

    private Integer chunkCount;

    @CreationTimestamp
    private Instant loadedAt;

}
