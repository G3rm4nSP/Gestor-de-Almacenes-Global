package com.openwarehouses.utils;

import com.openwarehouses.views.InicioView;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

/**
 * Utilidad para crear encabezados (headers) estándar en las vistas de la
 * aplicación.
 *
 * <p>
 * Proporciona un método para generar un header con un botón "ATRÁS"
 * configurable y un botón
 * "INICIO" fijo que siempre lleva a la pantalla principal.
 *
 * @author German
 * @version 1.0
 * @since 2025-12-16
 */
public final class HeaderUtils {

  /** Padding horizontal y vertical estándar para los HBox del header. */
  private static int hBoxNum = 15;

  /** Constructor privado para evitar instanciación. */
  private HeaderUtils() {
  }

  /**
   * Crea un header estándar en un {@link BorderPane}, con:
   * <ul>
   * <li>Botón "← ATRÁS" que ejecuta la acción proporcionada.</li>
   * <li>Botón "INICIO" que siempre redirige a {@link InicioView}.</li>
   * </ul>
   *
   * @param parent       {@link BorderPane} donde se insertará el header
   * @param primaryStage {@link Stage} principal de la aplicación (necesario para
   *                     iniciar InicioView)
   * @param onBackAction {@link Runnable} que se ejecutará al pulsar el botón
   *                     "ATRÁS"
   */
  public static void createHeader(BorderPane parent, Stage primaryStage, Runnable onBackAction) {

    Button btnBack = new Button("← ATRÁS");
    Button btnExit = new Button("INICIO");

    btnBack.setStyle(
        "-fx-padding: 15; -fx-font-size: 14; -fx-background-color: #95a5a6; -fx-text-fill: white;");
    btnExit.setStyle(
        "-fx-padding: 15; -fx-font-size: 14; -fx-background-color: #e74c3c; -fx-text-fill: white;");

    // Acción personalizada para "ATRÁS"
    btnBack.setOnAction(e -> onBackAction.run());

    // Acción fija para "INICIO"
    btnExit.setOnAction(
        e -> {
          InicioView inicio = new InicioView(primaryStage);
          inicio.show();
        });

    HBox topBar = new HBox(hBoxNum, btnBack, btnExit);
    topBar.setPadding(new Insets(hBoxNum));
    topBar.setStyle("-fx-background-color: #ecf0f1;");

    parent.setTop(topBar);
  }
}
