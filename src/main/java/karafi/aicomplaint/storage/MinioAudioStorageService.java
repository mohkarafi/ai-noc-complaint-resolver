package karafi.aicomplaint.storage;


import io.minio.*;
import karafi.aicomplaint.common.exception.AudioStorageException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.UUID;

@Component
public class MinioAudioStorageService implements AudioStorageService {

    private final MinioClient minioClient;
    private final String bucketName;

    public MinioAudioStorageService(
            @Value("${minio.endpoint}") String endpoint,
            @Value("${minio.access-key}") String accessKey,
            @Value("${minio.secret-key}") String secretKey,
            @Value("${minio.bucket-name}") String bucketName) {

        this.minioClient = MinioClient.builder()
                .endpoint(endpoint)
                .credentials(accessKey, secretKey)
                .build();
        this.bucketName = bucketName;
        ensureBucketExists();
    }

    private void ensureBucketExists() {
        try {
            boolean exists = minioClient.bucketExists(
                    BucketExistsArgs.builder().bucket(bucketName).build());
            if (!exists) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
            }
        } catch (Exception e) {
            throw new AudioStorageException("Impossible d'initialiser le bucket MinIO : " + e.getMessage() , e);
        }
    }

    @Override
    public String store(byte[] audioBytes, String originalFilename, String contentType) {
        String extension = extractExtension(originalFilename);
        String storageKey = UUID.randomUUID() + extension;

        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(audioBytes)) {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(storageKey)
                            .stream(inputStream, audioBytes.length, -1)
                            .contentType(contentType)
                            .build()
            );
            return storageKey;
        } catch (Exception e) {
            throw new AudioStorageException("Échec du stockage du fichier audio sur MinIO : " + e.getMessage(), e);
        }
    }

    @Override
    public byte[] retrieve(String storageKey) {
        try (var response = minioClient.getObject(
                GetObjectArgs.builder().bucket(bucketName).object(storageKey).build());
             ByteArrayOutputStream buffer = new ByteArrayOutputStream()) {
            response.transferTo(buffer);
            return buffer.toByteArray();
        } catch (Exception e) {
            throw new AudioStorageException("Échec de la récupération du fichier audio depuis MinIO : " + e.getMessage(), e);
        }
    }

    private String extractExtension(String filename) {
        int lastDot = filename.lastIndexOf('.');
        return lastDot >= 0 ? filename.substring(lastDot) : ".wav";
    }
}