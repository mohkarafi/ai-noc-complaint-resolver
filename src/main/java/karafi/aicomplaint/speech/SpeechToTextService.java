package karafi.aicomplaint.speech;

public interface SpeechToTextService {
    String transcribe(byte[] audioBytes, String originalFilename);
}