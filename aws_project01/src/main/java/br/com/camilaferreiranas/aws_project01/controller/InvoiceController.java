package br.com.camilaferreiranas.aws_project01.controller;

import br.com.camilaferreiranas.aws_project01.model.Invoice;
import br.com.camilaferreiranas.aws_project01.model.UrlResponse;
import br.com.camilaferreiranas.aws_project01.repository.InvoiceRepository;
import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
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

    @Autowired
    private AmazonS3 amazonS3;

    @Autowired
    InvoiceRepository invoiceRepository;

    @PostMapping
    public ResponseEntity<UrlResponse> createInvoiceUrl() {
        UrlResponse response = new UrlResponse();
        Instant expirationTime = Instant.now().plus(Duration.ofMinutes(5));
        String processId = UUID.randomUUID().toString();

        GeneratePresignedUrlRequest generatePresignedUrlRequest
                = new GeneratePresignedUrlRequest(bucketName, processId)
                .withMethod(HttpMethod.PUT)
                .withExpiration(Date.from(expirationTime));

        response.setExpirationTime(expirationTime.getEpochSecond());
        response.setUrl(amazonS3.generatePresignedUrl(generatePresignedUrlRequest).toString());

        return new ResponseEntity<UrlResponse>(response, HttpStatus.OK);
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
