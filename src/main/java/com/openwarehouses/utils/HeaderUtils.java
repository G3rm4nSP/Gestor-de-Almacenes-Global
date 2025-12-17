package com.openwarehouses.utils;

import com.openwarehouses.views.InicioView;
import com.openwarehouses.views.edicion.PinDialog;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

/**
 * Utilidad para crear encabezados (headers) estándar en las vistas de la
 * aplicación.
 *
 * <p>Permite generar un header con botones comunes y, opcionalmente,
 * mostrar funcionalidades adicionales solo en vistas de edición.</p>
 *
 * @author German
 * @version 1.1
 * @since 2025-12-16
 */
public final class HeaderUtils {

  /** Padding horizontal y vertical estándar para los HBox del header. */
  private static final int HBOX_PADDING = 15;

  /** Constructor privado para evitar instanciación. */
  private HeaderUtils() {
    throw new UnsupportedOperationException("Utility class");
  }

  /**
   * Modos de header disponibles.
   */
  public enum HeaderMode {
    /** Header normal (sin acciones de edición). */
    NORMAL,

    /** Header para vistas de edición (incluye botón editar PIN). */
    EDITION
  }

  /**
   * Crea un header estándar en un {@link BorderPane}, con:
   * <ul>
   *   <li>Botón "← ATRÁS"</li>
   *   <li>Botón "INICIO"</li>
   *   <li>Opcionalmente, botón "EDITAR PIN" solo en modo edición</li>
   * </ul>
   *
   * @param parent {@link BorderPane} donde se insertará el header
   * @param primaryStage {@link Stage} principal de la aplicación
   * @param onBackAction acción a ejecutar al pulsar "ATRÁS"
   * @param mode modo del header (NORMAL o EDITION)
   */
  public static void createHeader(
      BorderPane parent,
      Stage primaryStage,
      Runnable onBackAction,
      HeaderMode mode) {

    Button btnBack = new Button("← ATRÁS");
    Button btnExit = new Button("INICIO");

    btnBack.setStyle(
        "-fx-padding: 15; -fx-font-size: 14; "
            + "-fx-background-color: #95a5a6; -fx-text-fill: white;");

    btnExit.setStyle(
        "-fx-padding: 15; -fx-font-size: 14; "
            + "-fx-background-color: #e74c3c; -fx-text-fill: white;");

    btnBack.setOnAction(e -> onBackAction.run());

    btnExit.setOnAction(
        e -> {
          InicioView inicio = new InicioView(primaryStage);
          inicio.show();
        });

    HBox topBar = new HBox(HBOX_PADDING);
    topBar.setPadding(new Insets(HBOX_PADDING));
    topBar.setStyle("-fx-background-color: #ecf0f1;");

    topBar.getChildren().add(btnBack);

    // 🔐 Botón SOLO visible en vistas de edición
    if (mode == HeaderMode.EDITION) {
      Button btnEditPin = new Button("EDITAR PIN");
      btnEditPin.setStyle(
          "-fx-padding: 15; -fx-font-size: 14; "
              + "-fx-background-color: #27ae60; -fx-text-fill: white;");

      btnEditPin.setOnAction(e -> PinDialog.editPin(primaryStage));

      topBar.getChildren().add(btnEditPin);
    }

    topBar.getChildren().add(btnExit);

    parent.setTop(topBar);
  }

  /**
   * Crea un header estándar en modo NORMAL.
   * <p>
   * Mantiene compatibilidad con todas las vistas existentes.
   * </p>
   *
   * @param parent {@link BorderPane} donde se insertará el header
   * @param primaryStage {@link Stage} principal de la aplicación
   * @param onBackAction acción a ejecutar al pulsar "ATRÁS"
   */
  public static void createHeader(
      BorderPane parent,
      Stage primaryStage,
      Runnable onBackAction) {

    createHeader(parent, primaryStage, onBackAction, HeaderMode.NORMAL);
  }
}
