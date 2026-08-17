package karafi.aicomplaint.complaint;

import karafi.aicomplaint.ai.AIAnalyzer;
import karafi.aicomplaint.dto.ComplaintAnalysis;
import karafi.aicomplaint.dto.CustomerContext;
import karafi.aicomplaint.customer.Customer;
import karafi.aicomplaint.customer.CustomerService;
import karafi.aicomplaint.speech.AudioFileValidator;
import karafi.aicomplaint.speech.SpeechToTextService;
import karafi.aicomplaint.storage.AudioStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class ComplaintService {

    private final ComplaintRepository complaintRepository;
    private final CustomerService customerService;
    private final AIAnalyzer aiAnalyzer;
    private final AudioFileValidator audioFileValidator;
    private final AudioStorageService audioStorageService;
    private final SpeechToTextService speechToTextService;

    public Complaint createFromText(String phoneNumber, String rawText) {
        Customer customer = customerService.getOrCreate(phoneNumber);
        Complaint complaint = new Complaint();
        complaint.setCustomer(customer);
        complaint.setRawText(rawText);
        complaint.setStatus(ComplaintStatus.RECEIVED);
        return complaintRepository.save(complaint);
    }

    public Complaint createFromAudio(String phoneNumber, MultipartFile audioFile) {
        audioFileValidator.validate(audioFile);

        byte[] audioBytes;
        try {
            audioBytes = audioFile.getBytes();
        } catch (IOException e) {
            throw new IllegalStateException("Impossible de lire le fichier audio reçu", e);
        }

        // 1. Stockage dans MinIO
        String storageKey = audioStorageService.store(
                audioBytes, audioFile.getOriginalFilename(), audioFile.getContentType());

        // 2. Transcription
        String transcription = speechToTextService.transcribe(audioBytes, audioFile.getOriginalFilename());

        // 3. Création de la plainte avec transcription
        Customer customer = customerService.getOrCreate(phoneNumber);
        Complaint complaint = new Complaint();
        complaint.setCustomer(customer);
        complaint.setRawText(null);
        complaint.setTranscription(transcription);
        complaint.setAudioStorageKey(storageKey);
        complaint.setStatus(ComplaintStatus.RECEIVED);
        complaint = complaintRepository.save(complaint);

        // 4. Analyse IA (réutilise la logique du Jour 2)
        return analyzeAndSave(complaint.getId());
    }

    public Complaint analyzeAndSave(Long complaintId) {
        Complaint complaint = complaintRepository.findById(complaintId)
                .orElseThrow(() -> new NoSuchElementException("Complaint non trouvée : " + complaintId));

        CustomerContext context = new CustomerContext(
                complaint.getCustomer().getId(),
                "STANDARD",
                "UNKNOWN"
        );

        String textToAnalyze = complaint.getTranscription() != null
                ? complaint.getTranscription()
                : complaint.getRawText();

        ComplaintAnalysis analysis = aiAnalyzer.analyze(textToAnalyze, context);

        complaint.setCategory(analysis.category());
        complaint.setIntent(analysis.intent());
        complaint.setSentiment(analysis.sentiment());
        complaint.setPriority(analysis.priority());
        complaint.setProbableCause(analysis.probableCause());
        complaint.setConfidence(analysis.confidence());
        complaint.setStatus(ComplaintStatus.ANALYZED);

        return complaintRepository.save(complaint);
    }

    public Complaint createAndAnalyzeFromText(String phoneNumber, String rawText) {
        Complaint complaint = createFromText(phoneNumber, rawText);
        return analyzeAndSave(complaint.getId());
    }
}}