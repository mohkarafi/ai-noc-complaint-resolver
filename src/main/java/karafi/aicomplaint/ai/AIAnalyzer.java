package karafi.aicomplaint.ai;

import karafi.aicomplaint.dto.ComplaintAnalysis;
import karafi.aicomplaint.dto.CustomerContext;

public interface AIAnalyzer {
    ComplaintAnalysis analyze(String text, CustomerContext context);
}