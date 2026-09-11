package com.ennaslennas.storage;

import com.ennaslennas.common.exception.BusinessException;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.http.Method;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class StorageService {

    private final MinioClient minioClient;

    @Value("${ennas.minio.bucket-name:ennas-lennas-documents}")
    private String bucketName;

    private static final List<String> ALLOWED_MIME_TYPES = Arrays.asList(
            "image/jpeg",
            "image/png",
            "image/webp",
            "application/pdf"
    );

    public record StoredFileInfo(
            String originalFilename,
            String storageKey,
            String mimeType,
            long fileSize
    ) {}

    public StoredFileInfo storeFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException("Le fichier est vide.");
        }

        String mimeType = file.getContentType();
        if (mimeType == null || !ALLOWED_MIME_TYPES.contains(mimeType.toLowerCase())) {
            throw new BusinessException("Format de fichier non autorisé. Formats acceptés : JPEG, PNG, WEBP, PDF.");
        }

        String originalFilename = file.getOriginalFilename() != null ? file.getOriginalFilename() : "document";
        String extension = "";
        int dotIndex = originalFilename.lastIndexOf('.');
        if (dotIndex > 0) {
            extension = originalFilename.substring(dotIndex).toLowerCase();
        }

        // Generate server-side random storage key for absolute confidentiality
        String storageKey = UUID.randomUUID().toString() + extension;

        try (InputStream inputStream = file.getInputStream()) {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(storageKey)
                            .stream(inputStream, file.getSize(), -1)
                            .contentType(mimeType)
                            .build()
            );

            log.info("Fichier stocké avec succès dans MinIO: key={}, size={}", storageKey, file.getSize());

            return new StoredFileInfo(
                    originalFilename,
                    storageKey,
                    mimeType,
                    file.getSize()
            );
        } catch (Exception e) {
            log.error("Erreur lors de l'enregistrement du fichier dans MinIO", e);
            throw new BusinessException("Échec du téléversement du justificatif : " + e.getMessage());
        }
    }

    /**
     * Generate an ephemeral presigned URL (15 minutes expiration) for admin private review.
     */
    public String getPresignedUrl(String storageKey) {
        try {
            return minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Method.GET)
                            .bucket(bucketName)
                            .object(storageKey)
                            .expiry(15, TimeUnit.MINUTES)
                            .build()
            );
        } catch (Exception e) {
            log.error("Impossible de générer l'URL éphémère pour {}", storageKey, e);
            throw new BusinessException("Impossible d'accéder au document demandé.");
        }
    }
}
