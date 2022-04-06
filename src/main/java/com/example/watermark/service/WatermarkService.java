package com.example.watermark.service;

import com.example.watermark.model.Document;
import com.example.watermark.model.DocumentType;
import com.example.watermark.model.Topic;
import com.example.watermark.model.Watermark;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
public class WatermarkService {

    @Async
    public CompletableFuture<Document> watermark(Document document, List<Watermark> watermarks) {
        log.info("Start watermarking: {}...", document.toString());

        Watermark watermark;

        if(document. getId() <= 10) {
            watermark = watermarks.stream().filter(w -> Topic.BUSINESS.equals(w.getTopic())).findFirst()
                    .orElse(Watermark.builder().build());
        } else if (document.getId() > 10 && document.getId() <= 20) {
            watermark = watermarks.stream().filter(w -> Topic.MEDIA.equals(w.getTopic())).findFirst()
                    .orElse(Watermark.builder().build());
        } else if (document.getId() > 20 && document.getId() <=30) {
            watermark = watermarks.stream().filter(w -> Topic.SCIENCE.equals(w.getTopic())).findFirst()
                    .orElse(Watermark.builder().build());
        } else {
            watermark = watermarks.stream().filter(w -> DocumentType.JOURNALS.equals(w.getContent())).findFirst()
                    .orElse(Watermark.builder().build());
        }

        Watermark watermarkDoc = Watermark.builder()
                .content(watermark.getContent())
                .topic(watermark.getTopic())
                .author(document.getAuthor())
                .title(document.getTitle())
                .build();

        document.setWatermark(watermarkDoc);

        return CompletableFuture.completedFuture(document);
    }
}
