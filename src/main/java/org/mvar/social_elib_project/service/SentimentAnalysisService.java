package org.mvar.social_elib_project.service;

import ai.djl.Application;
import ai.djl.inference.Predictor;
import ai.djl.modality.Classifications.Classification;
import ai.djl.modality.Classifications;
import ai.djl.repository.zoo.Criteria;
import ai.djl.repository.zoo.ZooModel;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SentimentAnalysisService {

    private static final Logger log = LoggerFactory.getLogger(SentimentAnalysisService.class);

    private ZooModel<String, Classifications> model;
    private Predictor<String, Classifications> predictor;

    @PostConstruct
    public void init() {
        try {
            Criteria<String, Classifications> criteria = Criteria.builder()
                    .setTypes(String.class, Classifications.class)
                    .optApplication(Application.NLP.TEXT_CLASSIFICATION)
                    .optModelUrls("djl://ai.djl.huggingface.pytorch/cardiffnlp/twitter-xlm-roberta-base-sentiment")
                    .optEngine("PyTorch")
                    .build();

            model = criteria.loadModel();
            predictor = model.newPredictor();
            log.info("DJL Sentiment Analysis model successfully loaded.");
        } catch (Exception e) {
            log.error("Failed to load DJL sentiment analysis model", e);
        }
    }

    public double analyzeSentiment(String text) {
        if (text == null || text.isBlank()) {
            return 0.5;
        }
        try {
            Classifications classifications = predictor.predict(text);
            for (Classification c : classifications.items()) {
                String className = c.getClassName().toUpperCase();
                if (className.equals("POSITIVE") || className.equals("LABEL_1")) {
                    return c.getProbability();
                }
            }
            Classification best = classifications.best();
            String bestName = best.getClassName().toUpperCase();
            if (bestName.equals("NEGATIVE") || bestName.equals("LABEL_0")) {
                return 1.0 - best.getProbability();
            }

            return best.getProbability();

        } catch (Exception e) {
            log.error("Error during sentiment analysis prediction", e);
            return 0.5;
        }
    }

    @PreDestroy
    public void cleanup() {
        if (predictor != null) {
            predictor.close();
        }
        if (model != null) {
            model.close();
        }
    }
}