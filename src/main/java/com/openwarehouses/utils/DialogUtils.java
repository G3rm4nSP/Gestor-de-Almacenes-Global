package com.openwarehouses.utils;

import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Utilidad estática para la creación y gestión de diálogos JavaFX
 * reutilizables.
 *
 * <p>
 * Centraliza la lógica común de diálogos de edición, creación, validación
 * y confirmación de borrado, evitando duplicación de código en las vistas.
 *
 * <p>
 * Todos los métodos son estáticos y la clase no debe ser instanciada.
 *
 * @author German
 * @version 1.0
 * @since 2025-12-16
 */
public final class DialogUtils {

  /** Valor mínimo permitido en rangos numéricos. */
  private static final int RANGE_MIN = 1;

  /** Valor máximo permitido en rangos numéricos. */
  private static final int RANGE_MAX = 999;

  /** Valor inicial por defecto de los spinners de rango. */
  private static final int RANGE_INITIAL = 1;

  /** Espaciado estándar entre elementos de layout. */
  private static final int LAYOUT_SPACING = 10;

  /** Padding estándar de los diálogos. */
  private static final int DIALOG_PADDING = 20;

  /**
 * Constructor privado para evitar instanciación.
 */
  private DialogUtils() {
  }


  /**
   * Muestra un diálogo de edición de texto con validación y guardado.
   *
   * <p>
   * El flujo es:
   * <ol>
   * <li>Se muestra un {@link TextInputDialog} con un valor inicial</li>
   * <li>Se parsea el texto introducido</li>
   * <li>Se ejecuta la acción de guardado</li>
   * <li>Si todo es correcto, se ejecuta la acción de éxito</li>
   * </ol>
   *
   * @param title        título del diálogo
   * @param prompt       texto descriptivo del campo
   * @param initialValue valor inicial del campo
   * @param parser       función que convierte el texto a tipo {@code T}
   * @param saveAction   acción que guarda el valor y devuelve {@code true} si
   *                     tiene éxito
   * @param onSuccess    acción a ejecutar tras un guardado correcto
   * @param errorMessage mensaje mostrado si el guardado falla
   * @param <T>          tipo del valor editado
   * @return {@code true} si la edición fue correcta, {@code false} en caso
   *         contrario
   */
  public static <T> boolean edit(
      String title,
      String prompt,
      String initialValue,
      Function<String, T> parser,
      Function<T, Boolean> saveAction,
      Runnable onSuccess,
      String errorMessage) {

    TextInputDialog dialog = new TextInputDialog(initialValue);
    dialog.setTitle(title);
    dialog.setHeaderText(null);
    dialog.setContentText(prompt);

    Optional<String> result = dialog.showAndWait();
    if (result.isEmpty()) {
      return false;
    }

    T value;
    try {
      value = parser.apply(result.get().trim());
    } catch (Exception e) {
      showWarning("Entrada inválida", "Valor no válido.");
      return false;
    }

    boolean ok = saveAction.apply(value);
    if (!ok) {
      showWarning("Error", errorMessage);
      return false;
    }

    onSuccess.run();
    return true;
  }

  /**
   * Muestra un diálogo de confirmación de borrado con validación textual.
   *
   * <p>
   * El usuario debe escribir explícitamente {@code "eliminar"} para habilitar
   * el botón de confirmación.
   *
   * @param titulo       título del diálogo
   * @param mensaje      mensaje descriptivo del borrado
   * @param items        elementos a eliminar
   * @param deleteAction acción de borrado por cada elemento
   * @param onSuccess    acción a ejecutar tras eliminar correctamente
   * @param <T>          tipo de los elementos a borrar
   * @return {@code true} si se confirmó y ejecutó el borrado, {@code false} en
   *         caso contrario
   */
  public static <T> boolean confirmDelete(
      String titulo, String mensaje, List<T> items, Consumer<T> deleteAction, Runnable onSuccess) {

    Dialog<String> dialog = new Dialog<>();
    dialog.setTitle(titulo);
    dialog.setHeaderText(mensaje + "\n\nEsta acción no se puede deshacer.");

    ButtonType eliminarBtnType = new ButtonType("Eliminar", ButtonBar.ButtonData.OK_DONE);
    dialog.getDialogPane().getButtonTypes().addAll(eliminarBtnType, ButtonType.CANCEL);

    TextField confirmField = new TextField();
    confirmField.setText("Escribe 'eliminar' para confirmar");

    VBox content = new VBox(LAYOUT_SPACING, confirmField);
    content.setPadding(new Insets(DIALOG_PADDING));
    dialog.getDialogPane().setContent(content);

    Node eliminarButton = dialog.getDialogPane().lookupButton(eliminarBtnType);
    eliminarButton.setDisable(true);

    confirmField
        .textProperty()
        .addListener(
            (obs, oldVal, newVal) -> eliminarButton.setDisable(!"eliminar".equalsIgnoreCase(newVal.trim())));

    dialog.setResultConverter(btn -> btn == eliminarBtnType ? confirmField.getText() : null);

    return dialog
        .showAndWait()
        .map(
            result -> {
              if (!"eliminar".equalsIgnoreCase(result)) {
                return false;
              }

              items.forEach(deleteAction);
              onSuccess.run();
              return true;
            })
        .orElse(false);
  }

  /**
   * Muestra un diálogo de entrada de texto que se repite hasta que el valor sea
   * válido
   * o el usuario cancele.
   *
   * @param title        título del diálogo
   * @param header       encabezado del diálogo
   * @param prompt       texto descriptivo del campo
   * @param validator    predicado de validación
   * @param errorMessage mensaje mostrado si la validación falla
   * @return {@link Optional} con el valor válido o vacío si se cancela
   */
  public static Optional<String> createTextValidated(
      String title,
      String header,
      String prompt,
      Predicate<String> validator,
      String errorMessage) {

    TextInputDialog dialog = new TextInputDialog();
    dialog.setTitle(title);
    dialog.setHeaderText(header);
    dialog.setContentText(prompt);

    while (true) {
      Optional<String> result = dialog.showAndWait();
      if (result.isEmpty()) {
        return Optional.empty();
      }

      String value = result.get().trim();

      if (validator.test(value)) {
        return Optional.of(value);
      }

      showWarning("Error", errorMessage);
    }
  }

  /**
   * Muestra un diálogo para crear múltiples elementos a partir de un rango
   * numérico.
   *
   * <p>
   * Permite validar cada número individualmente y muestra un resumen final
   * con los elementos creados y no creados.
   *
   * @param title        título del diálogo
   * @param header       encabezado del diálogo
   * @param itemName     nombre del elemento (para mensajes)
   * @param parent       elemento padre asociado
   * @param validator    validación por número y padre
   * @param createAction acción de creación
   * @param <P>          tipo del elemento padre
   * @return el valor inicial del rango si se creó algún elemento, o {@code null}
   */
  public static <P> Integer createRange(
      String title,
      String header,
      String itemName,
      P parent,
      BiPredicate<Integer, P> validator,
      BiFunction<Integer, P, Boolean> createAction) {

    Dialog<Integer> dialog = new Dialog<>();
    dialog.setTitle(title);
    dialog.setHeaderText(header);

    Spinner<Integer> spinnerInicio = new Spinner<>(RANGE_MIN, RANGE_MAX, RANGE_INITIAL);
    Spinner<Integer> spinnerFin = new Spinner<>(RANGE_MIN, RANGE_MAX, RANGE_INITIAL);

    spinnerInicio.setEditable(true);
    spinnerFin.setEditable(true);

    VBox content = new VBox(
        LAYOUT_SPACING,
        new Label("Rango (inicio - fin):"),
        new HBox(
            LAYOUT_SPACING,
            new Label("Inicio:"),
            spinnerInicio,
            new Label("Fin:"),
            spinnerFin));

    content.setPadding(new Insets(DIALOG_PADDING));
    dialog.getDialogPane().setContent(content);

    ButtonType crearBtn = new ButtonType("Crear", ButtonBar.ButtonData.OK_DONE);
    dialog.getDialogPane().getButtonTypes().addAll(crearBtn, ButtonType.CANCEL);

    dialog.setResultConverter(
        btn -> {
          if (btn != crearBtn) {
            return null;
          }

          int inicio = spinnerInicio.getValue();
          int fin = spinnerFin.getValue();

          if (inicio > fin) {
            showWarning("Rango inválido", "El inicio no puede ser mayor que el fin.");
            return null;
          }

          StringBuilder creados = new StringBuilder();
          StringBuilder noCreados = new StringBuilder();

          for (int n = inicio; n <= fin; n++) {
            if (validator.test(n, parent)) {
              boolean ok = createAction.apply(n, parent);
              if (ok) {
                append(creados, String.valueOf(n));
              } else {
                append(noCreados, n + " (error)");
              }
            } else {
              append(noCreados, n + " (ya existe)");
            }
          }

          if (creados.length() == 0 && noCreados.length() == 0) {
            showWarning("Error", "No se creó ningún " + itemName);
            return null;
          }

          StringBuilder resumen = new StringBuilder();
          if (creados.length() > 0) {
            resumen.append("Creados: ").append(creados).append(".\n");
          }
          if (noCreados.length() > 0) {
            resumen.append("No creados: ").append(noCreados).append(".");
          }

          new Alert(Alert.AlertType.INFORMATION, resumen.toString()).showAndWait();

          return creados.length() > 0 ? inicio : null;
        });

    Platform.runLater(
        () -> {
          Stage st = (Stage) dialog.getDialogPane().getScene().getWindow();
          st.setOnCloseRequest(ev -> dialog.setResult(null));
        });

    return dialog.showAndWait().orElse(null);
  }

  /**
   * Muestra un diálogo de advertencia estándar.
   *
   * @param title título del aviso
   * @param msg   mensaje a mostrar
   */
  private static void showWarning(String title, String msg) {
    Alert alert = new Alert(Alert.AlertType.WARNING);
    alert.setTitle(title);
    alert.setHeaderText(null);
    alert.setContentText(msg);
    alert.showAndWait();
  }

  /**
   * Añade un texto a un {@link StringBuilder} separándolo por comas.
   *
   * @param sb   acumulador de texto
   * @param text texto a añadir
   */
  @SuppressWarnings("unused")
  private static void append(StringBuilder sb, String text) {
    if (sb.length() > 0) {
      sb.append(", ");
    }
    sb.append(text);
  }
}
