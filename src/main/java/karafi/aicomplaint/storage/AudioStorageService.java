package karafi.aicomplaint.storage;

public interface AudioStorageService {
    String store(byte[] audioBytes, String originalFilename, String contentType);
    byte[] retrieve(String storageKey);
}
