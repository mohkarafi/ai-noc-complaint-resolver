package karafi.aicomplaint.speech;

import karafi.aicomplaint.common.exception.InvalidAudioFileException;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;
@Component
public class AudioFileValidator {

    private static final long MAX_SIZE_BYTES = 20 * 1024 * 1024; // 20 Mo
    private static final Set<String> ALLOWED_EXTENSIONS = Set.of("wav", "mp3", "m4a", "ogg");

    public void validate(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new InvalidAudioFileException("Le fichier audio est vide ou absent");
        }
        if (file.getSize() > MAX_SIZE_BYTES) {
            throw new InvalidAudioFileException("Le fichier audio dépasse la taille maximale autorisée (20 Mo)");
        }
        String filename = file.getOriginalFilename();
        if (filename == null || filename.lastIndexOf('.') < 0) {
            throw new InvalidAudioFileException("Nom de fichier invalide, extension manquante");
        }
        String extension = filename.substring(filename.lastIndexOf('.') + 1).toLowerCase();
        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new InvalidAudioFileException("Format audio non supporté : " + extension);
        }
    }
}