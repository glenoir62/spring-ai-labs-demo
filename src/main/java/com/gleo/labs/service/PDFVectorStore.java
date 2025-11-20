package com.gleo.labs.service;

import com.gleo.labs.config.ResourceProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.reader.pdf.config.PdfDocumentReaderConfig;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Service
public class PDFVectorStore {

  private static final Logger log = LoggerFactory.getLogger(PDFVectorStore.class);
  private static final int BATCH_SIZE = 10;

  private final VectorStore vectorStore;

  public PDFVectorStore(VectorStore vectorStore, ResourceProperties resourceProperties) {
    this.vectorStore = vectorStore;
    initializeVectorStore(resourceProperties.getResources());
  }

  private void initializeVectorStore(Resource[] resources) {
    log.info("Initialisation du VectorStore avec {} ressource(s)", resources.length);

    Arrays.stream(resources).forEach(this::processResource);

    log.info("Initialisation du VectorStore terminée");
  }

  private void processResource(Resource resource) {
    if (!isValidResource(resource)) {
      return;
    }

    try {
      log.info("Traitement du PDF: {}", resource.getFilename());

      List<Document> documents = extractAndSplitDocuments(resource);
      storeDocumentsInBatches(documents, resource.getFilename());

      log.info("✓ {} traité avec succès ({} documents)",
          resource.getFilename(), documents.size());

    } catch (Exception e) {
      log.error("Erreur lors du traitement du PDF {}: {}",
          resource.getFilename(), e.getMessage(), e);
    }
  }

  private boolean isValidResource(Resource resource) {
    try {
      if (!resource.exists()) {
        log.warn("Le fichier n'existe pas: {}", resource.getFilename());
        return false;
      }
      if (!resource.isReadable()) {
        log.warn("Le fichier n'est pas lisible: {}", resource.getFilename());
        return false;
      }
      if (resource.contentLength() == 0) {
        log.warn("Le fichier est vide: {}", resource.getFilename());
        return false;
      }
      return true;
    } catch (IOException e) {
      log.error("Erreur lors de la validation du fichier {}: {}",
          resource.getFilename(), e.getMessage());
      return false;
    }
  }

  private List<Document> extractAndSplitDocuments(Resource resource) {
    PdfDocumentReaderConfig config = PdfDocumentReaderConfig.builder()
        .withPagesPerDocument(1)
        .build();

    PagePdfDocumentReader reader = new PagePdfDocumentReader(resource, config);

    // Constructeur par défaut (compatible toutes versions)
    TokenTextSplitter splitter = new TokenTextSplitter();

    return splitter.apply(reader.get());
  }

  private void storeDocumentsInBatches(List<Document> documents, String filename) {
    int totalBatches = (int) Math.ceil((double) documents.size() / BATCH_SIZE);

    for (int i = 0; i < documents.size(); i += BATCH_SIZE) {
      int end = Math.min(i + BATCH_SIZE, documents.size());
      List<Document> batch = documents.subList(i, end);

      int batchNumber = (i / BATCH_SIZE) + 1;
      log.debug("Embedding batch {}/{} pour {}", batchNumber, totalBatches, filename);

      vectorStore.accept(batch);

      if (i + BATCH_SIZE < documents.size()) {
        pauseBetweenBatches();
      }
    }
  }

  private void pauseBetweenBatches() {
    try {
      Thread.sleep(100);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      log.warn("Interruption lors de la pause entre batches", e);
    }
  }

  public VectorStore getVectorStore() {
    return vectorStore;
  }
}