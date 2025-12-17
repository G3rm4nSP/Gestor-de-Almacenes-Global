package com.openwarehouses;

import com.openwarehouses.views.InicioView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Aplicación principal que arranca la interfaz JavaFX. Inicializa la ventana principal y muestra la
 * vista de inicio.
 *
 * @author German
 * @version 1.0
 * @since 2025-12-16
 */
public class App extends Application {

  /** Ancho mínimo de la aplicación. */
  private static final int MIN_WIDTH = 1024;

  /** Altura mínima de la aplicación. */
  private static final int MIN_HEIGHT = 768;

  /**
   * Punto de entrada de la aplicación. Inicializa la ventana principal y lanza la vista
   * {@link com.openwarehouses.views.InicioView} en el hilo de JavaFX.
   *
   * @param primaryStage Escenario principal de JavaFX
   */
  @Override
  public void start(Stage primaryStage) {

    InicioView inicioView = new InicioView(primaryStage);
    Scene scene = new Scene(inicioView);

    primaryStage.setScene(scene);
    primaryStage.setMinWidth(MIN_WIDTH);
    primaryStage.setMinHeight(MIN_HEIGHT);
    primaryStage.setMaximized(true); // pantalla grande
    primaryStage.show();
  }

  /**
   * Main principal de la aplicación que lanza la interfaz JavaFX.
   *
   * @param args Argumentos de línea de comandos
   */
  public static void main(String[] args) {
    launch(args);
  }
}
