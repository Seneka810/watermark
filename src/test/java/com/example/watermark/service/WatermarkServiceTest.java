package com.example.watermark.service;

import com.example.watermark.model.Document;
import com.example.watermark.model.DocumentType;
import com.example.watermark.model.Topic;
import com.example.watermark.model.Watermark;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
class WatermarkServiceTest {

    @Spy
    private WatermarkService watermarkService;
    @Spy
    private ObjectMapper objectMapper;

    private List<Document> documents;
    private List<Watermark> watermarks;
    private List<CompletableFuture<Document>> watermarkResults;
    private List<Document> watermarkingDocuments;

    @BeforeEach
    void setUp() {
        documents = new ArrayList<>() {{
            add(Document.builder().id(2L).author("T. Shevcnenko").title("Kobzar").build());
            add(Document.builder().id(3L).author("I. Franko").title("Kamenyari").build());
            add(Document.builder().id(5L).author("L. Ukrainka").title("Intermezzo").build());
            add(Document.builder().id(10L).author("L. Tolstoy").title("Voina I Mir").build());
            add(Document.builder().id(12L).author("F. Dostoevskiy").title("Besi").build());
            add(Document.builder().id(15L).author("A. Tolstoy").title("Poem").build());
            add(Document.builder().id(20L).author("V. Gugo").title("Scar face").build());
            add(Document.builder().id(22L).author("F. Nietzsche").title("Zarathustra").build());
            add(Document.builder().id(23L).author("W. Shakespeare").title("Romeo and Juliet").build());
            add(Document.builder().id(34L).author("UA").title("Football").build());
            add(Document.builder().id(37L).author("USA").title("Times").build());
            add(Document.builder().id(50L).author("GER").title("Bild").build());
            add(Document.builder().id(58L).author("SPA").title("Marka").build());
        }};

        watermarks = new ArrayList<>() {{
            add(Watermark.builder().topic(Topic.MEDIA).content(DocumentType.BOOK).build());
            add(Watermark.builder().topic(Topic.BUSINESS).content(DocumentType.BOOK).build());
            add(Watermark.builder().topic(Topic.SCIENCE).content(DocumentType.BOOK).build());
            add(Watermark.builder().content(DocumentType.JOURNALS).build());
        }};

        watermarkResults = documents.stream()
                .map(document -> watermarkService.watermark(document, watermarks)).collect(Collectors.toList());

        watermarkingDocuments = watermarkResults.stream()
                .map(watermarkResult -> watermarkResult.getNow(Document.builder().build()))
                .collect(Collectors.toList());
    }

    @AfterEach
    void tearDown() {
        documents = null;
        watermarks = null;
        watermarkResults = null;
        watermarkingDocuments = null;
    }

    @Test
    void watermarkBookDocument() {

        assertNotNull(watermarkingDocuments.stream()
                .filter(w -> DocumentType.BOOK.equals(w.getWatermark().getContent()))
                .map(w -> w.getWatermark().getTopic()).findAny());
    }

    @Test
    void watermarkJournalDocument() {

        List<Topic> emptyList = new ArrayList<>();

        List<Topic> journals = watermarkingDocuments.stream()
                .filter(w -> DocumentType.JOURNALS.equals(w.getWatermark().getContent()))
                .map(w -> {
                    emptyList.add(null);
                    return w.getWatermark().getTopic();
                })
                .collect(Collectors.toList());

        assertEquals(journals, emptyList);
    }

    @Test
    void checkStatusOfCompletion() {
        List<Boolean> statusOfCompletion = watermarkResults.stream()
                .map(CompletableFuture::isDone)
                .collect(Collectors.toList());

        watermarkingDocuments.stream().map(w -> {
            try {
                return objectMapper.writeValueAsString(w);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
            return null;
        }).forEach(System.out::println);

        assertEquals(watermarkingDocuments.size(), documents.size());
        assertEquals(true, statusOfCompletion.stream().findAny().get());
    }
}