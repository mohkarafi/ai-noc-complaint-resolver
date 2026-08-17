package karafi.aicomplaint.ai;


import karafi.aicomplaint.dto.CustomerContext;
import org.springframework.stereotype.Component;

@Component
public class PromptBuilder {

    public String buildAnalysisPrompt(String transcription, CustomerContext context) {
        return """
                Tu es un assistant expert en analyse de plaintes clients pour un opérateur de télécommunications SFR.

                Analyse la plainte suivante et retourne UNIQUEMENT un objet JSON valide, sans aucun texte avant ou après, sans balises markdown, sans explication.

                Plainte du client (transcription de l'appel) :
                "%s"

                Contexte client :
                - Plan : %s
                - Région : %s

                Retourne un JSON avec EXACTEMENT cette structure (toutes les valeurs textuelles doivent être en français) :
                {
                  "category": "<une valeur parmi: QUALITE_RESEAU, FACTURATION, ACCES_COMPTE, PROBLEME_APPAREIL, AUTRE>",
                  "intent": "<une valeur parmi: INTERNET_LENT, PAS_DE_CONNEXION, LITIGE_FACTURATION, RESILIATION_SERVICE, DEMANDE_GENERALE, AUTRE>",
                  "sentiment": "<une valeur parmi: CALME, FRUSTRE, EN_COLERE, NEUTRE>",
                  "priority": "<une valeur parmi: FAIBLE, MOYENNE, ELEVEE, CRITIQUE>",
                  "probableCause": "<une courte description technique probable en français, ou INCONNUE si tu ne peux pas déterminer>",
                  "confidence": <un nombre décimal entre 0.0 et 1.0>
                }

                Règles importantes :
                - Utilise UNIQUEMENT les valeurs listées ci-dessus pour category, intent, sentiment et priority (en majuscules, sans accents, exactement comme écrit).
                - Le champ "probableCause" doit être rédigé en français, de façon concise (5-10 mots maximum).
                - Ne mets AUCUN texte en dehors du JSON.
                - confidence doit refléter ta certitude réelle sur l'analyse.
                """.formatted(transcription, context.plan(), context.region());
    }
}