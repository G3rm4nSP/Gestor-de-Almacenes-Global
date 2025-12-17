package com.openwarehouses.utils;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Utilidades para manejar y mostrar vistas en un {@link Stage} de JavaFX.
 *
 * <p>
 * Proporciona métodos para mostrar escenas de manera uniforme, manteniendo
 * tamaño dinámico
 * y fullscreen si estaba activado previamente.
 *
 * @author German
 * @version 1.0
 * @since 2025-12-16
 */
public final class StageUtils {

  /** Constructor privado para evitar instanciación. */
  private StageUtils() {
  }

  /**
   * Muestra una vista en el {@link Stage} principal, ajustando la escena al
   * tamaño actual del
   * Stage y preservando el estado de maximizado si aplica.
   *
   * @param primaryStage Stage principal donde se mostrará la vista
   * @param view         Vista a mostrar (normalmente el {@code this} de la clase
   *                     de la vista)
   * @param title        Título que se establecerá en la ventana
   */
  public static void showView(Stage primaryStage, Parent view, String title) {
    Scene scene = new Scene(view, primaryStage.getScene().getWidth(), primaryStage.getScene().getHeight());

    primaryStage.setScene(scene);
    primaryStage.setTitle(title);
    primaryStage.show();

    if (primaryStage.isMaximized()) {
      primaryStage.setMaximized(true); // Mantiene fullscreen si estaba activado
    }
  }
}
