package com.app.aws_project01.controller;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.app.aws_project01.model.Invoice;
import com.app.aws_project01.model.UrlResponse;
import com.app.aws_project01.repository.InvoiceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Date;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@RestController
@RequestMapping("/api/invoices")
public class InvoiceController {

    @Value("${aws.s3.bucket.invoice.name}")
    private String bucketName;

    private AmazonS3 amazonS3;
    private InvoiceRepository invoiceRepository;

    @Autowired
    public InvoiceController(AmazonS3 amazonS3, InvoiceRepository invoiceRepository) {
        this.amazonS3 = amazonS3;
        this.invoiceRepository = invoiceRepository;
    }

    @PostMapping
    public ResponseEntity<UrlResponse> createInvoiceUrl() {
        UrlResponse urlResponse = new UrlResponse();
        Instant expirationTime = Instant.now().plus(Duration.ofMinutes(5)); // URL Valida apenas por 5min
        String processId = UUID.randomUUID().toString();

        GeneratePresignedUrlRequest generatePresignedUrlRequest =
                new GeneratePresignedUrlRequest(bucketName, processId)
                        .withMethod(HttpMethod.PUT)
                        .withExpiration(Date.from(expirationTime)); // Limitando acesso apenas pelo PUT e limitando uma data par expirar

        urlResponse.setExpirationTime(expirationTime.getEpochSecond()); // data de expiração
        urlResponse.setUrl(amazonS3.generatePresignedUrl(generatePresignedUrlRequest).toString()); // Gerar URL pre assinada


        return new ResponseEntity<UrlResponse>(urlResponse, HttpStatus.OK);
    }

    @GetMapping
    public Iterable<Invoice> findAll() {
        return invoiceRepository.findAll();
    }

    @GetMapping(path = "/bycustomername")
    public Iterable<Invoice> findByCustomerName(@RequestParam String customerName) {
        return invoiceRepository.findAllByCustomerName(customerName);
    }
}
