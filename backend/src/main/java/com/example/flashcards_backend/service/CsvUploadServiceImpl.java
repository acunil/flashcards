package com.example.flashcards_backend.service;

import com.example.flashcards_backend.dto.UploadResponse;
import com.example.flashcards_backend.model.Card;
import com.example.flashcards_backend.repository.CardRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class CsvUploadServiceImpl implements CsvUploadService {

    public static final String FRONT = "front";
    public static final String BACK = "back";
    private final CardRepository cardRepository;

    @Override
    public UploadResponse uploadCsv(InputStream csvStream) throws IOException {
        try (Reader reader = new BufferedReader(new InputStreamReader(csvStream, StandardCharsets.UTF_8))) {
            List<CSVRecord> all = parseAllRecords(reader);

            List<CSVRecord> valid  = filterValid(all);
            logInvalid(all, valid);

            Map<Boolean, List<CSVRecord>> byDup = partitionByDuplicate(valid);

            List<Card> duplicates = buildCards(byDup.get(true));
            List<Card> toSave = buildCards(byDup.get(false));

            duplicates.forEach(d ->
                log.info("Duplicate, skipping: front='{}', back='{}'", d.getFront(), d.getBack())
            );

            List<Card> saved = cardRepository.saveAll(toSave);

            return UploadResponse.builder()
                .saved(saved)
                .duplicates(duplicates)
                .build();
        } catch (IOException e) {
            log.error("CSV processing error", e);
            throw e;
        }
    }

    private List<CSVRecord> parseAllRecords(Reader reader) throws IOException {
        CSVFormat format = CSVFormat.DEFAULT.builder()
            .setHeader()
            .setSkipHeaderRecord(true)
            .setIgnoreHeaderCase(true)
            .setTrim(true)
            .get();

        try (CSVParser parser = CSVParser.parse(reader, format)) {
            return parser.getRecords();
        }
    }

    private List<CSVRecord> filterValid(List<CSVRecord> rows) {
        return rows.stream()
            .filter(r -> {
                String f = r.get(FRONT);
                String b = r.get(BACK);
                return f != null && !f.isBlank()
                    && b != null && !b.isBlank();
            })
            .toList();
    }

    private void logInvalid(List<CSVRecord> all, List<CSVRecord> valid) {
        var validSet = new HashSet<>(valid);
        all.stream()
            .filter(r -> !validSet.contains(r))
            .forEach(r -> log.warn("Skipping invalid row: {}", r));
    }

    private Map<Boolean, List<CSVRecord>> partitionByDuplicate(List<CSVRecord> rows) {
        return rows.stream()
            .collect(Collectors.partitioningBy(r ->
                cardRepository.existsByFrontAndBack(r.get(FRONT), r.get(BACK))
            ));
    }

    private List<Card> buildCards(List<CSVRecord> csvRecords) {
        return csvRecords.stream()
            .map(r -> Card.builder()
                .front(r.get(FRONT))
                .back(r.get(BACK))
                .build())
            .toList();
    }
}

