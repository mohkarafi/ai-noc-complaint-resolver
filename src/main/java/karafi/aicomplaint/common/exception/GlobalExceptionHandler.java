package karafi.aicomplaint.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.io.IOException;
import java.util.Map;

public class GlobalExceptionHandler extends IOException {
    public GlobalExceptionHandler(String message) {
        super(message);
    }
    @ExceptionHandler(InvalidAudioFileException.class)
    public ResponseEntity<Map<String, String>> handleInvalidAudioFile(InvalidAudioFileException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", "Fichier audio invalide", "message", ex.getMessage()));
    }

    @ExceptionHandler(SpeechToTextException.class)
    public ResponseEntity<Map<String, String>> handleSpeechToTextException(SpeechToTextException ex) {
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                .body(Map.of("error", "Échec de la transcription", "message", ex.getMessage()));
    }

    @ExceptionHandler(AudioStorageException.class)
    public ResponseEntity<Map<String, String>> handleAudioStorageException(AudioStorageException ex) {
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                .body(Map.of("error", "Échec du stockage audio", "message", ex.getMessage()));
    }
}
