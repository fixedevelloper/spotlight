package com.ballack.softlight.util;


import lombok.SneakyThrows;
import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
public class OCRUtil {

    public static String extractFromRealMeter(MultipartFile file) throws Exception {

        // 1) Charger l'image avec OpenCV
        Mat src = Imgcodecs.imdecode(new MatOfByte(file.getBytes()), Imgcodecs.IMREAD_COLOR);

        if (src.empty()) {
            throw new RuntimeException("Image non lisible par OpenCV");
        }

        // 2) Grayscale
        Mat gray = new Mat();
        Imgproc.cvtColor(src, gray, Imgproc.COLOR_BGR2GRAY);

        // 3) Réduction de bruit
        Imgproc.GaussianBlur(gray, gray, new Size(3,3), 0);

        // 4) Augmenter contraste
        Core.normalize(gray, gray, 0, 255, Core.NORM_MINMAX);

        // 5) Binarisation Otsu + inversion
        Mat binary = new Mat();
        Imgproc.threshold(gray, binary, 0, 255, Imgproc.THRESH_BINARY_INV + Imgproc.THRESH_OTSU);

        // 6) Épaissir chiffres (dilatation)
        Mat kernel = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(2,2));
        Imgproc.dilate(binary, binary, kernel);

        // 7) Agrandir ×2 pour Tesseract
        Mat upscaled = new Mat();
        Imgproc.resize(binary, upscaled, new Size(binary.cols()*2, binary.rows()*2));

        // 8) Convertir en BufferedImage pour Tesseract
        BufferedImage buffered = MatToBufferedImage(upscaled);

        Tesseract tess = new Tesseract();
        tess.setDatapath("/usr/share/tesseract-ocr/4.00/tessdata");
        tess.setLanguage("eng");
        tess.setTessVariable("tessedit_char_whitelist", "0123456789");
        tess.setPageSegMode(7);

        String raw = tess.doOCR(buffered);

        // 9) Nettoyage pour ne garder que les chiffres
        return raw.replaceAll("[^0-9]", "");
    }

    private static BufferedImage MatToBufferedImage(Mat mat) {
        MatOfByte mob = new MatOfByte();
        Imgcodecs.imencode(".png", mat, mob);
        try {
            return ImageIO.read(new ByteArrayInputStream(mob.toArray()));
        } catch (Exception e) {
            return null;
        }
    }
}

/*
public class OCRUtil {

    static {
        // Charger OpenCV
        nu.pattern.OpenCV.loadLocally();
    }
    @SneakyThrows
    public static String preprocessAndExtract(MultipartFile file) {

        System.out.println("Preprocessing file: " + file.getOriginalFilename());
        // Sauvegarde temporaire image originale
        File original = File.createTempFile("meter_raw_", ".jpg");
        file.transferTo(original);

        // Lecture OpenCV
        Mat img = Imgcodecs.imread(original.getAbsolutePath());

        if (img.empty()) {
            throw new RuntimeException("Impossible de lire l'image envoyée.");
        }

        // ---- PRETRAITEMENT HAUTE QUALITÉ ----

        // 1. Passage au niveau de gris
        Imgproc.cvtColor(img, img, Imgproc.COLOR_BGR2GRAY);

        // 2. Réduction du bruit (GaussianBlur)
        Imgproc.GaussianBlur(img, img, new Size(5, 5), 0);

        // 3. Augmentation du contraste (Histogram Equalization)
        Imgproc.equalizeHist(img, img);

        // 4. Threshold adaptatif (idéal pour digits sous verre ou faible luminosité)
        Imgproc.adaptiveThreshold(
                img,
                img,
                255,
                Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C,
                Imgproc.THRESH_BINARY,
                11,
                2
        );

        // Sauvegarde image nettoyée pour debug
        File cleaned = File.createTempFile("meter_clean_", ".jpg");
        Imgcodecs.imwrite(cleaned.getAbsolutePath(), img);


        // ---- CONFIG TESSERACT ----
        ITesseract tesseract = new Tesseract();
        tesseract.setDatapath("/usr/share/tesseract-ocr/4.00/tessdata");
        tesseract.setLanguage("eng");

        // Lire uniquement les CHIFFRES → élimine immédiatement les mauvaises lectures
        tesseract.setVariable("tessedit_char_whitelist", "0123456789");

        // PSM 7 → ligne de texte (idéal compteur électrique)
        tesseract.setPageSegMode(7);

        // ---- EXECUTION OCR ----
        String result;

        try {
            result = tesseract.doOCR(cleaned);
        } catch (Exception e) {
            throw new RuntimeException("Erreur OCR : " + e.getMessage());
        }

        return result;
    }
}*/
