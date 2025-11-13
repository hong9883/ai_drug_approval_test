package com.drugapproval.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * PDF 파일 처리 유틸리티
 */
@Slf4j
@Component
public class PdfProcessor {

    /**
     * PDF 파일의 총 페이지 수를 반환
     */
    public int getPageCount(File pdfFile) throws IOException {
        try (PDDocument document = PDDocument.load(pdfFile)) {
            return document.getNumberOfPages();
        }
    }

    /**
     * PDF 파일의 전체 텍스트를 추출
     */
    public String extractAllText(File pdfFile) throws IOException {
        try (PDDocument document = PDDocument.load(pdfFile)) {
            PDFTextStripper stripper = new PDFTextStripper();
            return stripper.getText(document);
        }
    }

    /**
     * PDF 파일의 특정 페이지 텍스트를 추출
     */
    public String extractPageText(File pdfFile, int pageNumber) throws IOException {
        try (PDDocument document = PDDocument.load(pdfFile)) {
            PDFTextStripper stripper = new PDFTextStripper();
            stripper.setStartPage(pageNumber);
            stripper.setEndPage(pageNumber);
            return stripper.getText(document);
        }
    }

    /**
     * PDF 파일을 페이지별로 분할하여 텍스트 추출
     */
    public List<PageContent> extractPageByPage(File pdfFile) throws IOException {
        List<PageContent> pages = new ArrayList<>();

        try (PDDocument document = PDDocument.load(pdfFile)) {
            PDFTextStripper stripper = new PDFTextStripper();
            int totalPages = document.getNumberOfPages();

            for (int i = 1; i <= totalPages; i++) {
                stripper.setStartPage(i);
                stripper.setEndPage(i);
                String text = stripper.getText(document);

                pages.add(new PageContent(i, text));
            }
        }

        return pages;
    }

    /**
     * PDF 파일을 청크 단위로 분할하여 텍스트 추출
     * @param pdfFile PDF 파일
     * @param chunkSize 청크 크기 (문자 수)
     * @param overlap 청크 간 중복 크기
     */
    public List<TextChunk> extractChunks(File pdfFile, int chunkSize, int overlap) throws IOException {
        List<TextChunk> chunks = new ArrayList<>();
        List<PageContent> pages = extractPageByPage(pdfFile);

        for (PageContent page : pages) {
            String text = page.getText();
            int start = 0;
            int chunkIndex = 0;

            while (start < text.length()) {
                int end = Math.min(start + chunkSize, text.length());
                String chunkText = text.substring(start, end);

                chunks.add(new TextChunk(
                    page.getPageNumber(),
                    chunkIndex++,
                    chunkText,
                    start,
                    end
                ));

                start += (chunkSize - overlap);
            }
        }

        return chunks;
    }

    /**
     * 페이지 내용 클래스
     */
    public record PageContent(int pageNumber, String text) {}

    /**
     * 텍스트 청크 클래스
     */
    public record TextChunk(
        int pageNumber,
        int chunkIndex,
        String text,
        int startOffset,
        int endOffset
    ) {}
}
