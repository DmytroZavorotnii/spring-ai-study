package zavorotnii.dmytro.service;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.SneakyThrows;
import org.apache.tomcat.util.buf.HexUtils;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.TextReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import zavorotnii.dmytro.model.document.LoadedDocument;
import zavorotnii.dmytro.repository.LoadedDocumentRepository;
import zavorotnii.dmytro.utils.HashedResource;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Service
public class DocumentLoaderService implements CommandLineRunner {
    private static final Integer CHUNK_SIZE = 512;

    @Autowired
    private LoadedDocumentRepository loadedDocumentRepository;

    @Autowired
    private ResourcePatternResolver resolver;

    @Autowired
    private VectorStore vectorStore;

    @SneakyThrows
    public void loadDocuments(){
        List<Resource> resources = Arrays.stream(resolver.getResources("classpath:/knowledgebase/**/*.txt")).toList();
        resources
                .stream()
                .map(HashedResource::new)
                .filter(this::isNotLoaded)
                .forEach(this::loadDocument);
    }

    private boolean isNotLoaded(HashedResource hashedResource) {
        return !loadedDocumentRepository.existsByFilenameAndContentHash(
                hashedResource.getResource().getFilename(), hashedResource.getHash()
        );
    }

    @Transactional
    public void loadDocument(HashedResource hashedResource) {
        List<Document> documents = new TextReader(hashedResource.getResource()).get();
        TokenTextSplitter splitter = TokenTextSplitter.builder().withChunkSize(CHUNK_SIZE).build();
        List<Document> chunks = splitter.apply(documents);
        vectorStore.accept(chunks);

        LoadedDocument loadedDocument = LoadedDocument
                .builder()
                .documentType("txt")
                .filename(hashedResource.getResource().getFilename())
                .chunkCount(chunks.size())
                .contentHash(hashedResource.getHash())
                .build();

        loadedDocumentRepository.save(loadedDocument);
    }

    @SneakyThrows
    public String getFileExtension(String filename) {
        if (filename == null){
            return "undefined";
        }
        int dotIndex = filename.lastIndexOf(".");
        if (dotIndex >= 0) {
            return filename.substring(dotIndex + 1);
        }
        return "undefined";
    }


    @Override
    public void run(String... args) throws Exception {
        loadDocuments();
    }
}
