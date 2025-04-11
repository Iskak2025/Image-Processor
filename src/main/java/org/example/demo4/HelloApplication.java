package org.example.demo4;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class HelloApplication extends Application {
    private Image originalImage;
    private Image resultImage;

    @Override
    public void start(Stage stage) {
        VBox root = new VBox(10);
        root.setPadding(new Insets(10));

        ComboBox<String> select = new ComboBox<>();
        select.getItems().addAll("grayscale", "inverse", "blur");
        select.setPromptText("Выберите эффект");

        Button loadButton = new Button("Загрузить изображение");
        Button saveButton = new Button("Сохранить результат");

        HBox controls = new HBox(10, loadButton, select, saveButton);
        controls.setAlignment(Pos.CENTER_LEFT);

        ImageView originalView = new ImageView();
        ImageView resultView = new ImageView();

        originalView.setFitWidth(300);
        originalView.setFitHeight(300);
        originalView.setPreserveRatio(true);

        resultView.setFitWidth(300);
        resultView.setFitHeight(300);
        resultView.setPreserveRatio(true);

        loadButton.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Выберите изображение");
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("Изображения", "*.png", "*.jpg", "*.jpeg", "*.bmp")
            );
            File file = fileChooser.showOpenDialog(stage);
            if (file != null) {
                try {
                    originalImage = new Image(new FileInputStream(file));
                    originalView.setImage(originalImage);
                    resultView.setImage(null);
                    select.setValue(null); // сброс выбранного фильтра
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        select.setOnAction(e -> {
            if (originalImage != null && select.getValue() != null) {
                resultImage = ImageProcessor.processingImage(originalImage, select.getValue());
                resultView.setImage(resultImage);
            }
        });

        saveButton.setOnAction(e -> {
            if (resultImage != null) {
                saveImage(resultImage, stage);
            }
        });

        HBox imageBox = new HBox(10, originalView, resultView);
        root.getChildren().addAll(controls, imageBox);

        Scene scene = new Scene(root, 700, 450);
        stage.setTitle("Image Processor");
        stage.setScene(scene);
        stage.show();
    }

    private void saveImage(Image image, Stage stage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Сохранить изображение");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("PNG Image", "*.png")
        );
        fileChooser.setInitialFileName("processed.png");
        File file = fileChooser.showSaveDialog(stage);
        if (file != null) {
            try {
                WritableImage writableImage = new WritableImage((int) image.getWidth(), (int) image.getHeight());
                PixelReader reader = image.getPixelReader();

                BufferedImage bufferedImage = new BufferedImage(
                        (int) image.getWidth(),
                        (int) image.getHeight(),
                        BufferedImage.TYPE_INT_ARGB
                );

                // Получаем пиксели из изображения
                for (int y = 0; y < image.getHeight(); y++) {
                    for (int x = 0; x < image.getWidth(); x++) {
                        javafx.scene.paint.Color color = reader.getColor(x, y);
                        int argb = (int) (color.getOpacity() * 255) << 24 |
                                (int) (color.getRed() * 255) << 16 |
                                (int) (color.getGreen() * 255) << 8 |
                                (int) (color.getBlue() * 255);
                        bufferedImage.setRGB(x, y, argb);
                    }
                }

                // Сохраняем изображение в файл
                ImageIO.write(bufferedImage, "png", file);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        launch();
    }
}
