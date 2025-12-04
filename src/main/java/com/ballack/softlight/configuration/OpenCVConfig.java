package com.ballack.softlight.configuration;

import jakarta.annotation.PostConstruct;
import org.opencv.core.Core;
import nu.pattern.OpenCV;
import org.springframework.context.annotation.Configuration;


@Configuration
public class OpenCVConfig {

    @PostConstruct
    public void init() {
        try {
            // Charge automatiquement la librairie native OpenCV
            OpenCV.loadShared();

            // Vérifie que OpenCV est chargé
            System.out.println("OpenCV loaded successfully. Version: " + Core.VERSION);

        } catch (UnsatisfiedLinkError e) {
            System.err.println("Failed to load OpenCV native library.");
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Unexpected error while loading OpenCV.");
            e.printStackTrace();
        }
    }
}
