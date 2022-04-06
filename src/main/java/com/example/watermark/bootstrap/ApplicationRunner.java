package com.example.watermark.bootstrap;

import com.example.watermark.model.Document;
import com.example.watermark.model.DocumentType;
import com.example.watermark.model.Topic;
import com.example.watermark.model.Watermark;
import com.example.watermark.service.WatermarkService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Slf4j
@Component
public class ApplicationRunner implements CommandLineRunner {
    private static final String IS_COMPLETE = "Is watermarking for object: {} complete? - {}";

    private final WatermarkService watermarkService;

    public ApplicationRunner(WatermarkService watermarkService) {
        this.watermarkService = watermarkService;
    }

    @Override
    public void run(String... args) throws Exception {
        loadDocuments();
    }

    private void loadDocuments() throws ExecutionException, InterruptedException {
        Document book1 = Document.builder()
                .id(2L)
                .author("T. Shevcnenko")
                .title("Kobzar")
                .build();

        Document journal1 = Document.builder()
                .id(50L)
                .author("I. Franko")
                .title("Kamenyari")
                .build();


        List<Watermark> watermarks = new ArrayList<>();
        watermarks.add(Watermark.builder()
                .topic(Topic.MEDIA)
                .content(DocumentType.BOOK)
                .build());
        watermarks.add(Watermark.builder()
                .topic(Topic.BUSINESS)
                .content(DocumentType.BOOK)
                .build());
        watermarks.add(Watermark.builder()
                .topic(Topic.SCIENCE)
                .content(DocumentType.BOOK)
                .build());
        watermarks.add(Watermark.builder()
                .content(DocumentType.JOURNALS)
                .build());

        CompletableFuture<Document> watermarkResult1 = watermarkService.watermark(book1, watermarks);
        CompletableFuture<Document> watermarkResult2 = watermarkService.watermark(journal1, watermarks);

        log.info(IS_COMPLETE, book1.getTitle(), watermarkResult1.isDone());
        log.info(IS_COMPLETE, journal1.getTitle(), watermarkResult2.isDone());
        log.info("watermarking finished: {}", watermarkResult1.get());
        log.info("watermarking finished: {}", watermarkResult2.get());
        log.info(IS_COMPLETE, book1.getTitle(), watermarkResult1.isDone());
        log.info(IS_COMPLETE, journal1.getTitle(), watermarkResult2.isDone());
    }
}
