package karafi.aicomplaint.speech;

import karafi.aicomplaint.common.exception.SpeechToTextException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

@Component
public class WhisperSpeechToTextService implements SpeechToTextService {

    private final String whisperBinaryPath;
    private final String whisperModelPath;

    public WhisperSpeechToTextService(
            @Value("${whisper.binary-path}") String whisperBinaryPath,
            @Value("${whisper.model-path}") String whisperModelPath) {
        this.whisperBinaryPath = whisperBinaryPath;
        this.whisperModelPath = whisperModelPath;
    }

    @Override
    public String transcribe(byte[] audioBytes, String originalFilename) {
        Path tempAudioFile = null;
        try {
            tempAudioFile = Files.createTempFile("complaint-audio-" + UUID.randomUUID(), ".wav");
            Files.write(tempAudioFile, audioBytes);

            ProcessBuilder pb = new ProcessBuilder(
                    whisperBinaryPath,
                    "-m", whisperModelPath,
                    "-f", tempAudioFile.toAbsolutePath().toString(),
                    "-l", "fr",
                    "-nt",       // pas de timestamps dans la sortie
                    "-otxt"
            );
            pb.redirectErrorStream(true);
            Process process = pb.start();

            String processOutput = new String(process.getInputStream().readAllBytes());
            boolean finished = process.waitFor(120, java.util.concurrent.TimeUnit.SECONDS);

            if (!finished) {
                process.destroyForcibly();
                throw new SpeechToTextException("Le processus whisper.cpp a dépassé le délai imparti");
            }
            if (process.exitValue() != 0) {
                throw new SpeechToTextException("whisper.cpp a échoué : " + processOutput);
            }

            Path transcriptionFile = Path.of(tempAudioFile.toAbsolutePath() + ".txt");
            if (!Files.exists(transcriptionFile)) {
                throw new SpeechToTextException("Fichier de transcription introuvable après exécution de whisper.cpp");
            }

            String transcription = Files.readString(transcriptionFile).trim();
            Files.deleteIfExists(transcriptionFile);

            if (transcription.isBlank()) {
                throw new SpeechToTextException("La transcription générée est vide");
            }
            return transcription;

        } catch (SpeechToTextException e) {
            throw e;
        } catch (Exception e) {
            throw new SpeechToTextException("Erreur lors de la transcription audio : " + e.getMessage(), e);
        } finally {
            if (tempAudioFile != null) {
                try {
                    Files.deleteIfExists(tempAudioFile);
                } catch (Exception ignored) {
                }
            }
        }
    }
}