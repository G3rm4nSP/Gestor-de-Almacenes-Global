package com.openwarehouses.views.edicion;

import com.openwarehouses.services.StorageService;
import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * Diálogo modal para la introducción y validación de un PIN de seguridad.
 * <p>
 * Permite al usuario introducir un PIN numérico de 4 dígitos, mostrando
 * temporalmente el dígito introducido y ocultándolo posteriormente con ●.
 * </p>
 * <p>
 * El diálogo devuelve {@code true} si el PIN es correcto, o {@code false}
 * si es incorrecto o el usuario cancela la operación.
 * </p>
 *
 * @author German
 * @version 1.0
 * @since 2025-12-16
 */
public final class PinDialog {

  /**
   * Longitud fija del PIN.
   */
  private static final int PIN_LENGTH = 4;

  /**
   * Tamaño de fuente de los botones.
   */
  private static final int BUTTON_FONT_SIZE = 40;

  /**
   * Padding principal del layout.
   */
  private static final int ROOT_PADDING = 20;

  /**
   * Tamaño de fuente del campo de texto.
   */
  private static final int TEXTFIELD_FONT = 24;

  /**
   * Anchura máxima del campo de texto.
   */
  private static final int TEXTFIELD_WIDTH = 150;

  /**
   * Espaciado vertical entre elementos.
   */
  private static final int LAYOUT_SPACING = 20;

  /**
   * Anchura de la escena.
   */
  private static final int SCENE_WIDTH = 350;

  /**
   * Altura de la escena.
   */
  private static final int SCENE_HEIGHT = 300;

  /**
   * Duración de las animaciones de fade.
   */
  private static final double FADE_DURATION = 0.3;

  /**
   * Tiempo que se muestra el mensaje de error.
   */
  private static final double ERROR_DELAY = 2.0;

  /**
   * Tiempo durante el cual se muestra el último dígito antes de ocultarlo.
   */
  private static final double HIDE_DIGIT_DELAY = 0.5;

  /**
   * Constructor privado para evitar la instanciación de la clase.
   */
  private PinDialog() {
    throw new UnsupportedOperationException("Utility class");
  }

  /**
   * Muestra el diálogo modal para introducir el PIN.
   *
   * @return {@code true} si el PIN es correcto, {@code false} si es incorrecto o
   *         se cancela
   */
  public static boolean show() {
    StorageService storage = new StorageService();
    String correctPin = storage.loadPin();

    Stage window = new Stage();
    window.initModality(Modality.APPLICATION_MODAL);
    window.setTitle("Introduce el PIN");
    window.setResizable(false);

    BorderPane root = new BorderPane();
    root.setPadding(new Insets(ROOT_PADDING));
    root.setStyle("-fx-background-color: #ecf0f1;");

    TextField pinField = new TextField();
    pinField.setFont(Font.font(TEXTFIELD_FONT));
    pinField.setMaxWidth(TEXTFIELD_WIDTH);
    pinField.setAlignment(Pos.CENTER);

    StringBuilder actualPin = new StringBuilder();
    StringBuilder maskedPin = new StringBuilder();

    pinField.addEventFilter(
        KeyEvent.KEY_TYPED,
        e -> {
          if (!e.getCharacter().matches("[0-9]") || actualPin.length() >= PIN_LENGTH) {
            e.consume();
            return;
          }

          actualPin.append(e.getCharacter());
          maskedPin.append(e.getCharacter());
          pinField.setText(maskedPin.toString());
          pinField.positionCaret(maskedPin.length());

          int lastIndex = maskedPin.length() - 1;
          PauseTransition hideDigit = new PauseTransition(Duration.seconds(HIDE_DIGIT_DELAY));
          hideDigit.setOnFinished(
              ev -> {
                maskedPin.setCharAt(lastIndex, '●');
                pinField.setText(maskedPin.toString());
                pinField.positionCaret(maskedPin.length());
              });
          hideDigit.play();

          e.consume();
        });

    Label errorLabel = new Label();
    errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 16px;");
    errorLabel.setOpacity(0);

    Button okBtn = new Button("CONFIRMAR");
    okBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-weight: bold;");
    okBtn.setPrefWidth(Double.MAX_VALUE);
    okBtn.setMinHeight(BUTTON_FONT_SIZE);
    okBtn.setMaxHeight(BUTTON_FONT_SIZE);
    okBtn.setOnAction(
        e -> checkPin(window, actualPin, maskedPin, pinField, errorLabel, correctPin));

    Button backBtn = new Button("ATRÁS");
    backBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-weight: bold;");
    backBtn.setPrefWidth(Double.MAX_VALUE);
    backBtn.setMinHeight(BUTTON_FONT_SIZE);
    backBtn.setMaxHeight(BUTTON_FONT_SIZE);
    backBtn.setOnAction(
        e -> {
          window.setUserData(false);
          window.close();
        });

    VBox center = new VBox(LAYOUT_SPACING, pinField, okBtn, backBtn, errorLabel);
    center.setAlignment(Pos.CENTER);
    root.setCenter(center);

    Scene scene = new Scene(root, SCENE_WIDTH, SCENE_HEIGHT);
    scene.setOnKeyPressed(
        event -> {
          switch (event.getCode()) {
            case ENTER -> checkPin(window, actualPin, maskedPin, pinField, errorLabel, correctPin);
            case ESCAPE -> {
              window.setUserData(false);
              window.close();
            }
            default -> {
            }
          }
        });

    Platform.runLater(pinField::requestFocus);

    window.setScene(scene);
    window.showAndWait();

    Boolean result = (Boolean) window.getUserData();
    return result != null && result;
  }

  /**
   * Muestra un diálogo modal para editar y guardar un nuevo PIN.
   *
   * @param owner escenario propietario
   * @return {@code true} si el PIN se guardó correctamente
   */
  public static boolean editPin(Stage owner) {
    StorageService storage = new StorageService();

    Stage window = new Stage();
    window.initOwner(owner);
    window.initModality(Modality.APPLICATION_MODAL);
    window.setTitle("Editar PIN");
    window.setResizable(false);

    BorderPane root = new BorderPane();
    root.setPadding(new Insets(ROOT_PADDING));
    root.setStyle("-fx-background-color: #ecf0f1;");

    TextField pinField = new TextField();
    pinField.setPromptText("Nuevo PIN (4 dígitos)");
    pinField.setFont(Font.font(TEXTFIELD_FONT));
    pinField.setMaxWidth(TEXTFIELD_WIDTH);
    pinField.setAlignment(Pos.CENTER);

    Label errorLabel = new Label();
    errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 14px;");
    errorLabel.setOpacity(0);

    Button saveBtn = new Button("GUARDAR");
    saveBtn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-weight: bold;");
    saveBtn.setPrefWidth(Double.MAX_VALUE);
    saveBtn.setMinHeight(BUTTON_FONT_SIZE);
    saveBtn.setOnAction(
        e -> {
          String newPin = pinField.getText();
          if (newPin == null || !newPin.matches("\\d{4}")) {
            errorLabel.setText("El PIN debe tener 4 dígitos numéricos");
            errorLabel.setOpacity(1);
            return;
          }
          storage.savePin(newPin);
          Alert ok = new Alert(Alert.AlertType.INFORMATION);
          ok.setTitle("PIN guardado");
          ok.setHeaderText(null);
          ok.setContentText("El PIN se ha actualizado correctamente.");
          ok.showAndWait();
          window.setUserData(true);
          window.close();
        });

    Button cancelBtn = new Button("CANCELAR");
    cancelBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-weight: bold;");
    cancelBtn.setPrefWidth(Double.MAX_VALUE);
    cancelBtn.setMinHeight(BUTTON_FONT_SIZE);
    cancelBtn.setOnAction(
        e -> {
          window.setUserData(false);
          window.close();
        });

    VBox center = new VBox(LAYOUT_SPACING, pinField, saveBtn, cancelBtn, errorLabel);
    center.setAlignment(Pos.CENTER);
    root.setCenter(center);

    Scene scene = new Scene(root, SCENE_WIDTH, SCENE_HEIGHT);
    scene.setOnKeyPressed(
        event -> {
          switch (event.getCode()) {
            case ENTER -> saveBtn.fire();
            case ESCAPE -> {
              window.setUserData(false);
              window.close();
            }
            default -> {
            }
          }
        });

    window.setScene(scene);
    window.showAndWait();

    Boolean res = (Boolean) window.getUserData();
    return res != null && res;
  }

  /**
   * Comprueba el PIN introducido por el usuario.
   */
  private static void checkPin(
      Stage window,
      StringBuilder actualPin,
      StringBuilder maskedPin,
      TextField pinField,
      Label errorLabel,
      String correctPin) {

    if (actualPin.toString().equals(correctPin)) {
      window.setUserData(true);
      window.close();
    } else {
      showError(errorLabel);
      actualPin.setLength(0);
      maskedPin.setLength(0);
      pinField.clear();
      pinField.requestFocus();
    }
  }

  /**
   * Muestra un mensaje de error con animación de entrada y salida.
   *
   * @param label etiqueta donde se muestra el error
   */
  private static void showError(Label label) {
    label.setOpacity(0);
    label.setText("PIN incorrecto");

    FadeTransition fadeIn = new FadeTransition(Duration.seconds(FADE_DURATION), label);
    fadeIn.setFromValue(0);
    fadeIn.setToValue(1);

    FadeTransition fadeOut = new FadeTransition(Duration.seconds(FADE_DURATION), label);
    fadeOut.setFromValue(1);
    fadeOut.setToValue(0);
    fadeOut.setDelay(Duration.seconds(ERROR_DELAY));

    fadeIn.setOnFinished(e -> fadeOut.play());
    fadeIn.play();
  }
}
