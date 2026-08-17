package karafi.aicomplaint.ai;


import karafi.aicomplaint.common.exception.AIAnalysisException;
import karafi.aicomplaint.dto.ComplaintAnalysis;
import karafi.aicomplaint.dto.CustomerContext;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class LLMComplaintAnalyzer implements AIAnalyzer {

    private static final Set<String> VALID_CATEGORIES = Set.of(
            "QUALITE_RESEAU", "FACTURATION", "ACCES_COMPTE", "PROBLEME_APPAREIL", "AUTRE");
    private static final Set<String> VALID_INTENTS = Set.of(
            "INTERNET_LENT", "PAS_DE_CONNEXION", "LITIGE_FACTURATION", "RESILIATION_SERVICE", "DEMANDE_GENERALE", "AUTRE");
    private static final Set<String> VALID_SENTIMENTS = Set.of(
            "CALME", "FRUSTRE", "EN_COLERE", "NEUTRE");
    private static final Set<String> VALID_PRIORITIES = Set.of(
            "FAIBLE", "MOYENNE", "ELEVEE", "CRITIQUE");

    private final ChatClient chatClient;
    private final PromptBuilder promptBuilder;

    public LLMComplaintAnalyzer(ChatClient.Builder chatClientBuilder, PromptBuilder promptBuilder) {
        this.chatClient = chatClientBuilder.build();
        this.promptBuilder = promptBuilder;
    }

    @Override
    public ComplaintAnalysis analyze(String text, CustomerContext context) {
        String prompt = promptBuilder.buildAnalysisPrompt(text, context);

        ComplaintAnalysis result;
        try {
            result = chatClient.prompt()
                    .user(prompt)
                    .call()
                    .entity(ComplaintAnalysis.class);
        } catch (Exception e) {
            throw new AIAnalysisException("Échec de l'appel au LLM Ollama : " + e.getMessage(), e);
        }

        if (result == null) {
            throw new AIAnalysisException("Le LLM n'a retourné aucune analyse exploitable");
        }

        validate(result);
        return result;
    }

    private void validate(ComplaintAnalysis analysis) {
        if (!VALID_CATEGORIES.contains(analysis.category())) {
            throw new AIAnalysisException("Catégorie invalide retournée par le LLM : " + analysis.category());
        }
        if (!VALID_INTENTS.contains(analysis.intent())) {
            throw new AIAnalysisException("Intention invalide retournée par le LLM : " + analysis.intent());
        }
        if (!VALID_SENTIMENTS.contains(analysis.sentiment())) {
            throw new AIAnalysisException("Sentiment invalide retourné par le LLM : " + analysis.sentiment());
        }
        if (!VALID_PRIORITIES.contains(analysis.priority())) {
            throw new AIAnalysisException("Priorité invalide retournée par le LLM : " + analysis.priority());
        }
        if (analysis.confidence() == null || analysis.confidence() < 0.0 || analysis.confidence() > 1.0) {
            throw new AIAnalysisException("Confidence invalide retournée par le LLM : " + analysis.confidence());
        }
    }
}