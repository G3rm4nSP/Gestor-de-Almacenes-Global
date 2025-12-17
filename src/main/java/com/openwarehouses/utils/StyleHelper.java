package com.openwarehouses.utils;

import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;

/**
 * Utilidad para definir estilos consistentes en toda la aplicación.
 *
 * <p>
 * Incluye:
 * <ul>
 * <li>Colores reutilizables de la paleta de la aplicación.
 * <li>Constantes de diseño como sombras, radio de botones y escalas de fuente.
 * <li>Métodos para generar estilos CSS dinámicos para botones, labels,
 * TextField, etc.
 * </ul>
 *
 * @author German
 * @version 1.0
 * @since 2025-12-16
 */
public final class StyleHelper {

  /** Constructor privado para evitar instanciación. */
  private StyleHelper() {
  }

  // ----------------------
  // Colores de la paleta
  // ----------------------
  /** Color principal de la interfaz. */
  public static final String COLOR_PRIMARY = "#1abc9c";
  /** Variante oscura del color principal. */
  public static final String COLOR_PRIMARY_DARK = "#16a085";
  /** Color usado para indicar éxito o confirmación. */
  public static final String COLOR_SUCCESS = "#27ae60";
  /** Variante oscura del color de éxito. */
  public static final String COLOR_SUCCESS_DARK = "#229954";
  /** Color usado para indicar peligro o error. */
  public static final String COLOR_DANGER = "#e74c3c";
  /** Variante oscura del color de peligro. */
  public static final String COLOR_DANGER_DARK = "#c0392b";
  /** Color usado para advertencias o alertas. */
  public static final String COLOR_WARNING = "#f39c12";
  /** Variante oscura del color de advertencia. */
  public static final String COLOR_WARNING_DARK = "#d68910";
  /** Color usado para información o mensajes neutrales. */
  public static final String COLOR_INFO = "#3498db";
  /** Variante oscura del color de información. */
  public static final String COLOR_INFO_DARK = "#2980b9";
  /** Color de fondo de la interfaz. */
  public static final String COLOR_BACKGROUND = "#ecf0f1";
  /** Color principal del texto en la interfaz. */
  public static final String COLOR_TEXT = "#2c3e50";

  // ----------------------
  // Constantes de diseño
  // ----------------------
  /** Opacidad de las sombras usadas en elementos UI. */
  public static final double SHADOW_OPACITY = 0.25;
  /** Radio de difuminado de las sombras. */
  public static final double SHADOW_RADIUS = 10;
  /** Escala base del tamaño de fuente. */
  public static final double FONT_SCALE = 14.0;
  /** Radio de los bordes de los botones. */
  public static final double BUTTON_BORDER_RADIUS = 15;
  /** Opacidad máxima para colores RGBA (0-255). */
  public static final int OPACITY = 255;

  // ----------------------
  // Efectos y estilos CSS
  // ----------------------

  /**
   * Obtiene un efecto de sombra estándar para botones.
   *
   * @return DropShadow configurado con opacidad y radio definidos
   */
  public static DropShadow getShadowEffect() {
    DropShadow shadow = new DropShadow();
    shadow.setColor(Color.rgb(0, 0, 0, (int) (SHADOW_OPACITY * OPACITY)));
    shadow.setRadius(SHADOW_RADIUS);
    return shadow;
  }

  /**
   * Genera estilo CSS para un botón grande con gradiente.
   *
   * @param colorFrom Color inicial del gradiente (ej: #1abc9c)
   * @param colorTo   Color final del gradiente (ej: #16a085)
   * @param fontSize  Tamaño de la fuente en píxeles
   * @return String con el estilo CSS
   */
  public static String getGradientButtonStyle(String colorFrom, String colorTo, double fontSize) {
    return "-fx-background-color: linear-gradient(to bottom right, "
        + colorFrom
        + ", "
        + colorTo
        + ");"
        + "-fx-text-fill: white;"
        + "-fx-font-weight: bold;"
        + "-fx-border-radius: "
        + BUTTON_BORDER_RADIUS
        + ";"
        + "-fx-background-radius: "
        + BUTTON_BORDER_RADIUS
        + ";"
        + "-fx-font-size: "
        + fontSize
        + "px;"
        + "-fx-cursor: hand;";
  }

  /**
   * Genera estilo CSS para botones de acción estándar (crear, editar, eliminar).
   *
   * @param action   Tipo de acción: "create", "edit", "delete" (otros valores
   *                 usan color primario)
   * @param fontSize Tamaño de fuente en píxeles
   * @return String con el estilo CSS correspondiente
   */
  public static String getActionButtonStyle(String action, double fontSize) {
    String colorFrom;
    String colorTo;
    switch (action.toLowerCase()) {
      case "create":
        colorFrom = COLOR_SUCCESS;
        colorTo = COLOR_SUCCESS_DARK;
        break;
      case "edit":
        colorFrom = COLOR_INFO;
        colorTo = COLOR_INFO_DARK;
        break;
      case "delete":
        colorFrom = COLOR_DANGER;
        colorTo = COLOR_DANGER_DARK;
        break;
      default:
        colorFrom = COLOR_PRIMARY;
        colorTo = COLOR_PRIMARY_DARK;
    }
    return getGradientButtonStyle(colorFrom, colorTo, fontSize);
  }

  /**
   * Genera estilo CSS para un botón de retroceso.
   *
   * @param fontSize Tamaño de fuente en píxeles
   * @return String con el estilo CSS
   */
  public static String getBackButtonStyle(double fontSize) {
    return "-fx-background-color: linear-gradient(to bottom right, #95a5a6, #7f8c8d);"
        + "-fx-text-fill: white;"
        + "-fx-font-weight: bold;"
        + "-fx-border-radius: "
        + BUTTON_BORDER_RADIUS
        + ";"
        + "-fx-background-radius: "
        + BUTTON_BORDER_RADIUS
        + ";"
        + "-fx-font-size: "
        + fontSize
        + "px;"
        + "-fx-cursor: hand;";
  }

  /**
   * Genera estilo CSS para un botón de salida.
   *
   * @param fontSize Tamaño de fuente en píxeles
   * @return String con el estilo CSS
   */
  public static String getExitButtonStyle(double fontSize) {
    return "-fx-background-color: linear-gradient(to bottom right, #e74c3c, #c0392b);"
        + "-fx-text-fill: white;"
        + "-fx-font-weight: bold;"
        + "-fx-border-radius: "
        + BUTTON_BORDER_RADIUS
        + ";"
        + "-fx-background-radius: "
        + BUTTON_BORDER_RADIUS
        + ";"
        + "-fx-font-size: "
        + fontSize
        + "px;"
        + "-fx-cursor: hand;";
  }

  /**
   * Obtiene el estilo CSS de fondo estándar de la aplicación.
   *
   * @return String con el estilo CSS para fondo
   */
  public static String getBackgroundStyle() {
    return "-fx-background-color: " + COLOR_BACKGROUND + ";";
  }

  /**
   * Genera estilo CSS para mensajes de "no hay elementos disponibles".
   *
   * @param fontSize Tamaño de fuente en píxeles
   * @return String con el estilo CSS
   */
  public static String getEmptyMessageStyle(double fontSize) {
    return "-fx-font-size: " + fontSize + "px;" + "-fx-text-fill: " + COLOR_TEXT + ";";
  }

  /**
   * Genera estilo CSS para TextField estándar.
   *
   * @return String con el estilo CSS
   */
  public static String getTextFieldStyle() {
    return "-fx-padding: 10;"
        + "-fx-font-size: 14;"
        + "-fx-border-radius: 5;"
        + "-fx-background-radius: 5;";
  }
}
