package com.ennaslennas.storage;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MinioConfig {

    @Value("${ennas.minio.url:http://localhost:9008}")
    private String minioUrl;

    @Value("${ennas.minio.access-key:n62JqnPRXJRV2JAHDCg8}")
    private String accessKey;

    @Value("${ennas.minio.secret-key:XgySJpBcz2wU6NSlF3cvpXK87CUDFj4Q6ZOcrysy}")
    private String secretKey;

    @Value("${ennas.minio.bucket-name:ennas-lennas-documents}")
    private String bucketName;

    @Bean
    public MinioClient minioClient() {
        MinioClient client = MinioClient.builder()
                .endpoint(minioUrl)
                .credentials(accessKey, secretKey)
                .build();

        // Ensure bucket exists
        try {
            boolean exists = client.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
            if (!exists) {
                client.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
            }
        } catch (Exception e) {
            System.err.println("Warning: MinIO bucket initialization notice: " + e.getMessage());
        }

        return client;
    }
}
