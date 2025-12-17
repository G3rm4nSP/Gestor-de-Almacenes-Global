package com.openwarehouses.views;

import com.openwarehouses.views.edicion.EdicionAlmacenView;
import com.openwarehouses.views.edicion.PinDialog;
import com.openwarehouses.views.visualization.SeleccionAlmacenEtiquetasView;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

/**
 * Vista inicial de la aplicación OpenWarehouses.
 *
 * <p>Esta clase representa la pantalla principal que se muestra al iniciar la aplicación.
 * Proporciona acceso a las funcionalidades principales mediante:
 *
 * <ul>
 *   <li>Visualización de almacenes
 *   <li>Acceso al área de administración protegido por PIN
 *   <li>Cierre de la aplicación
 * </ul>
 *
 * <p>La vista está construida sobre un {@link BorderPane} y utiliza JavaFX para aplicar estilos
 * visuales modernos, efectos de sombra y escalado dinámico del contenido según el tamaño de la
 * ventana.
 */
public class InicioView extends BorderPane {

  /** Padding superior de la barra de botones. */
  private static final int TOP_PADDING = 15;

  /** Espaciado horizontal entre botones. */
  private static final int BUTTON_SPACING = 10;

  /** Padding general del contenido central. */
  private static final int CENTER_PADDING = 20;

  /** Tamaño base de la fuente del botón principal. */
  private static final int BUTTON_FONT_SIZE = 28;

  /** Opacidad de la sombra aplicada al botón principal. */
  private static final double SHADOW_OPACITY = 0.25;

  /** Radio de difuminado de la sombra. */
  private static final double SHADOW_RADIUS = 10;

  /** Factor de escalado dinámico del texto según el ancho del botón. */
  private static final double FONT_SCALE = 14.0;

  /** Stage principal de la aplicación. */
  private final Stage primaryStage;

  /**
   * Construye la vista de inicio de la aplicación.
   *
   * <p>Inicializa la estructura visual, crea los botones principales, configura los estilos,
   * efectos visuales y define las acciones asociadas a cada botón.
   *
   * @param primaryStage stage principal desde el cual se gestionan los cambios de escena y apertura
   *     de nuevas vistas
   */
  public InicioView(Stage primaryStage) {

    this.primaryStage = primaryStage;

    Button btnSalir = new Button("SALIR");
    Button btnAdmin = new Button("ADMIN");

    btnSalir.setStyle(
        "-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-weight: bold;");
    btnAdmin.setStyle(
        "-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-weight: bold;");

    btnSalir.setOnAction(e -> primaryStage.close());
    btnAdmin.setOnAction(e -> abrirVistaAdmin());

    HBox topBar = new HBox(BUTTON_SPACING, btnAdmin, btnSalir);
    topBar.setPadding(new Insets(TOP_PADDING));
    topBar.setAlignment(Pos.CENTER_RIGHT);

    /* =========================
     * Botón central
     * ========================= */
    Button btnVisualizar = new Button("Visualizar Almacenes");
    btnVisualizar.setFont(Font.font(BUTTON_FONT_SIZE));
    btnVisualizar.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
    btnVisualizar.setStyle(
        "-fx-background-color: linear-gradient(to bottom right, #1abc9c, #16a085);"
            + "-fx-text-fill: white;"
            + "-fx-font-weight: bold;"
            + "-fx-border-radius: 15;"
            + "-fx-background-radius: 15;"
            + "-fx-font-size: "
            + BUTTON_FONT_SIZE
            + "px;"
            + "-fx-cursor: hand;");

    /* =========================
     * Sombra para efecto 3D
     * ========================= */
    DropShadow shadow = new DropShadow();
    shadow.setColor(Color.rgb(0, 0, 0, SHADOW_OPACITY));
    shadow.setRadius(SHADOW_RADIUS);
    btnVisualizar.setEffect(shadow);

    /* Mantener proporción cuadrada */
    btnVisualizar.prefHeightProperty().bind(btnVisualizar.widthProperty());

    /* Escalado dinámico del texto */
    btnVisualizar
        .widthProperty()
        .addListener(
            (obs, oldVal, newVal) ->
                btnVisualizar.setStyle(
                    "-fx-background-color: linear-gradient(to bottom right, #1abc9c, #16a085);"
                        + "-fx-text-fill: white;"
                        + "-fx-font-weight: bold;"
                        + "-fx-border-radius: 15;"
                        + "-fx-background-radius: 15;"
                        + "-fx-font-size: "
                        + newVal.doubleValue() / FONT_SCALE
                        + "px;"
                        + "-fx-cursor: hand;"));

    /* Hover effect */
    btnVisualizar.setOnMouseEntered(
        e ->
            btnVisualizar.setStyle(
                "-fx-background-color: linear-gradient(to bottom right, #16a085, #1abc9c);"
                    + "-fx-text-fill: white;"
                    + "-fx-font-weight: bold;"
                    + "-fx-border-radius: 15;"
                    + "-fx-background-radius: 15;"
                    + "-fx-font-size: "
                    + btnVisualizar.getWidth() / FONT_SCALE
                    + "px;"
                    + "-fx-cursor: hand;"));

    btnVisualizar.setOnMouseExited(
        e ->
            btnVisualizar.setStyle(
                "-fx-background-color: linear-gradient(to bottom right, #1abc9c, #16a085);"
                    + "-fx-text-fill: white;"
                    + "-fx-font-weight: bold;"
                    + "-fx-border-radius: 15;"
                    + "-fx-background-radius: 15;"
                    + "-fx-font-size: "
                    + btnVisualizar.getWidth() / FONT_SCALE
                    + "px;"
                    + "-fx-cursor: hand;"));

    btnVisualizar.setOnAction(e -> vistaAlmacen());

    /* =========================
     * Panel central
     * ========================= */
    StackPane centerPane = new StackPane(btnVisualizar);
    centerPane.setPadding(new Insets(CENTER_PADDING));
    StackPane.setAlignment(btnVisualizar, Pos.CENTER);

    /*
     * Layout principal
     */
    this.setTop(topBar);
    this.setCenter(centerPane);
    this.setPadding(new Insets(CENTER_PADDING));

    /* Fondo general */
    this.setStyle("-fx-background-color: #ecf0f1;");
  }

  /**
   * Abre la vista de administración del sistema.
   *
   * <p>Antes de permitir el acceso, se solicita un PIN mediante un diálogo modal. Si la validación
   * falla, el acceso se cancela.
   *
   * <p>En caso de error durante la carga de la vista, se muestra un {@link
   * javafx.scene.control.Alert} informando del problema.
   */
  private void abrirVistaAdmin() {
    try {
      boolean accesoPermitido = PinDialog.show();

      if (!accesoPermitido) {
        return;
      }

      EdicionAlmacenView adminView = new EdicionAlmacenView(primaryStage);
      adminView.show();

    } catch (Exception ex) {
      javafx.scene.control.Alert err =
          new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
      err.setTitle("Error");
      err.setHeaderText("No se pudo abrir Admin");
      err.setContentText(ex.getClass().getSimpleName() + ": " + ex.getMessage());
      err.showAndWait();
    }
  }

  /**
   * Abre la vista de visualización de almacenes.
   *
   * <p>Sustituye la escena actual del {@link Stage} principal por la vista de selección y
   * visualización de almacenes.
   */
  private void vistaAlmacen() {
    SeleccionAlmacenEtiquetasView view = new SeleccionAlmacenEtiquetasView(primaryStage);
    view.show();
  }

  /**
   * Muestra esta vista en el {@link Stage} principal.
   *
   * <p>Mantiene el tamaño actual de la ventana y respeta el estado de maximización si el stage ya
   * estaba maximizado.
   */
  public void show() {
    Scene scene =
        new Scene(this, primaryStage.getScene().getWidth(), primaryStage.getScene().getHeight());

    primaryStage.setScene(scene);
    primaryStage.setTitle("Gestión de almacenes");
    primaryStage.show();

    if (primaryStage.isMaximized()) {
      primaryStage.setMaximized(true);
    }
  }
}
