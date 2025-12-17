package com.openwarehouses.utils;

import java.util.List;
import java.util.function.Consumer;
import javafx.scene.control.Button;
import javafx.scene.input.MouseButton;

/**
 * Fábrica para crear botones reutilizables en la jerarquía de almacenes.
 *
 * <p>
 * Esta clase permite generar botones para cualquier nivel de la jerarquía
 * (Almacén, Pasillo, Estantería, Altura…) que soporten:
 * <ul>
 * <li>Selección simple (un clic) con cambio de estilo visual.</li>
 * <li>Doble clic para ejecutar una acción específica (abrir nivel
 * inferior).</li>
 * </ul>
 *
 * @author German
 * @version 1.0
 * @since 2025-12-16
 */
public final class HierarchyButtonFactory {

  /** Constructor privado para evitar instanciación. */
  private HierarchyButtonFactory() {
  }

  /** Estilo CSS para botones sin seleccionar. */
  private static final String ESTILO_NORMAL = ""
      + "-fx-background-color: linear-gradient(to bottom right, #3498db, #2980b9);"
      + "-fx-text-fill: white;"
      + "-fx-font-size: 18;"
      + "-fx-font-weight: bold;"
      + "-fx-padding: 30;"
      + "-fx-border-radius: 15;"
      + "-fx-background-radius: 15;"
      + "-fx-cursor: hand;";

  /** Estilo CSS para botones seleccionados. */
  private static final String ESTILO_SELECCIONADO = ""
      + "-fx-background-color: linear-gradient(to bottom right, #2ecc71, #27ae60);"
      + "-fx-text-fill: white;"
      + "-fx-font-size: 18;"
      + "-fx-font-weight: bold;"
      + "-fx-padding: 30;"
      + "-fx-border-radius: 15;"
      + "-fx-background-radius: 15;"
      + "-fx-cursor: hand;";

  /** Ancho y alto mínimo de los botones. */
  private static int min = 250;

  /**
   * Crea un botón genérico para la jerarquía de almacenes.
   *
   * <p>
   * El botón soporta:
   * <ul>
   * <li>Selección/desselección mediante clic único.</li>
   * <li>Ejecución de una acción personalizada mediante doble clic.</li>
   * </ul>
   *
   * @param <T>              Tipo del elemento representado (Almacen, Pasillo,
   *                         Estantería, etc.)
   * @param texto            Texto a mostrar en el botón
   * @param elemento         Elemento representado por el botón
   * @param listaSeleccion   Lista de selección correspondiente, que se actualiza
   *                         al hacer clic
   * @param accionDobleClick Acción a ejecutar en doble clic (puede ser null si no
   *                         aplica)
   * @return Botón configurado con estilo y comportamiento de selección/doble clic
   */
  public static <T> Button crearBotonJerarquia(
      String texto, T elemento, List<T> listaSeleccion, Consumer<T> accionDobleClick) {

    Button btn = new Button(texto);

    btn.setStyle(ESTILO_NORMAL);
    btn.setMinWidth(min);
    btn.setMinHeight(min);

    btn.setOnMouseClicked(
        mouse -> {
          if (mouse.getButton() != MouseButton.PRIMARY) {
            return;
          }

          if (mouse.getClickCount() == 2 && accionDobleClick != null) {
            accionDobleClick.accept(elemento);
            return;
          }

          if (mouse.getClickCount() == 1) {
            if (listaSeleccion.contains(elemento)) {
              listaSeleccion.remove(elemento);
              btn.setStyle(ESTILO_NORMAL);
            } else {
              listaSeleccion.add(elemento);
              btn.setStyle(ESTILO_SELECCIONADO);
            }
          }
        });

    return btn;
  }
}
