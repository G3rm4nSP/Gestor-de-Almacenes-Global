package com.openwarehouses.services;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.openwarehouses.models.Label;

import java.awt.Desktop;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Hashtable;
import java.util.List;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

/**
 * Servicio para imprimir etiquetas en formato PDF. Permite configurar el tamaño de las etiquetas,
 * la cantidad de copias y la inclusión de códigos QR y de barras.
 *
 * @author German
 * @version 1.0
 * @since 2025-12-16
 */
public final class PrintLabelService {

  /** Ancho de etiqueta pequeña en puntos PDF. */
  private static final int SMALL_WIDTH = 150;

  /** Alto de etiqueta pequeña en puntos PDF. */
  private static final int SMALL_HEIGHT = 75;

  /** Ancho de etiqueta mediana en puntos PDF. */
  private static final int MEDIUM_WIDTH = 250;

  /** Alto de etiqueta mediana en puntos PDF. */
  private static final int MEDIUM_HEIGHT = 120;

  /** Ancho de etiqueta grande en puntos PDF. */
  private static final int BIG_WIDTH = 400;

  /** Alto de etiqueta grande en puntos PDF. */
  private static final int BIG_HEIGHT = 200;

  /** Margen general de la página en puntos PDF. */
  private static final float PAGE_MARGIN = 5f;

  /** Espacio reservado en la parte inferior para códigos gráficos. */
  private static final float RESERVED_BOTTOM = 30f;

  /** Padding horizontal máximo permitido para el texto principal. */
  private static final float TEXT_HORIZONTAL_PADDING = 20f;

  /** Espacio vertical adicional de seguridad para el cálculo del texto. */
  private static final float EXTRA_VERTICAL_PADDING = 10f;

  /** Altura estándar de los códigos de barras y QR. */
  private static final int BARCODE_HEIGHT = 25;

  /** Proporción del ancho del código de barras respecto a la página. */
  private static final float BARCODE_WIDTH_RATIO = 0.5f;

  /** Color negro en formato RGB. */
  private static final int COLOR_BLACK = 0x000000;

  /** Color blanco en formato RGB. */
  private static final int COLOR_WHITE = 0xFFFFFF;

  /** Factor interno de PDFBox para el cálculo del ancho de texto. */
  private static final float PDFBOX_TEXT_WIDTH_FACTOR = 1000f;

  private PrintLabelService() {}

  /** Tamaños de etiqueta disponibles. */
  public enum LabelSize {
    /**Pequeño 150x75*/
    SMALL,
    /**Mediano 250x120*/
    MEDIUM,
    /**Grande 400x200*/
    LARGE
  }

  /**
   * Imprime una lista de etiquetas en un archivo PDF.
   *
   * @param labels Lista de etiquetas a imprimir
   * @param copies Número de copias por etiqueta
   * @param size Tamaño de la etiqueta
   */
  public static void print(List<Label> labels, int copies, LabelSize size) {
    if (labels == null || labels.isEmpty()) {
      return;
    }

    boolean drawQrFlag = false;
    boolean drawBarcodeFlag = false;

    try {
      PDRectangle pageSize = getPageSize(size);
      PDDocument document = new PDDocument();

      for (Label label : labels) {
        for (int i = 0; i < copies; i++) {
          PDPage page = new PDPage(pageSize);
          document.addPage(page);

          try (PDPageContentStream content = new PDPageContentStream(document, page)) {

            drawMainCode(content, pageSize, label.getCodigo(), size);

            if (drawQrFlag) {
              drawQr(content, document, pageSize, label.getCodigo(), size);
            }

            if (drawBarcodeFlag) {
              drawBarcode(content, document, pageSize, label.getCodigo(), size);
            }
          }
        }
      }

      File file = new File("etiquetas_" + size.name().toLowerCase() + ".pdf");
      document.save(file);
      document.close();

      openPdf(file);
      printLabels(labels, copies, size);

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /** Dibuja el código principal centrado en la etiqueta. */
  private static void drawMainCode(
      PDPageContentStream content, PDRectangle pageSize, String text, LabelSize size)
      throws Exception {

    PDType1Font font = PDType1Font.HELVETICA_BOLD;

    float usableHeight = pageSize.getHeight() - RESERVED_BOTTOM - EXTRA_VERTICAL_PADDING;

    float fontSize =
        calculateMaxFontSize(
            font, text, pageSize.getWidth() - TEXT_HORIZONTAL_PADDING, usableHeight);

    float textWidth = font.getStringWidth(text) / PDFBOX_TEXT_WIDTH_FACTOR * fontSize;
    float x = (pageSize.getWidth() - textWidth) / 2;
    float y = RESERVED_BOTTOM + (usableHeight / 2) - (fontSize / 2);

    content.setFont(font, fontSize);
    content.beginText();
    content.newLineAtOffset(x, y);
    content.showText(text);
    content.endText();
  }

  /** Dibuja un código QR en la parte inferior izquierda de la etiqueta. */
  private static void drawQr(
      PDPageContentStream content,
      PDDocument document,
      PDRectangle pageSize,
      String text,
      LabelSize size)
      throws Exception {

    int qrHeight = BARCODE_HEIGHT;
    int qrWidth = qrHeight;

    Hashtable<EncodeHintType, Object> hints = new Hashtable<>();
    hints.put(EncodeHintType.MARGIN, 0);

    QRCodeWriter qrWriter = new QRCodeWriter();
    BitMatrix matrix = qrWriter.encode(text, BarcodeFormat.QR_CODE, qrWidth, qrHeight, hints);

    BufferedImage qrImage = new BufferedImage(qrWidth, qrHeight, BufferedImage.TYPE_INT_RGB);

    for (int x = 0; x < qrWidth; x++) {
      for (int y = 0; y < qrHeight; y++) {
        int color = matrix.get(x, y) ? COLOR_BLACK : COLOR_WHITE;
        qrImage.setRGB(x, y, color);
      }
    }

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    javax.imageio.ImageIO.write(qrImage, "png", baos);
    byte[] qrBytes = baos.toByteArray();
    baos.close();

    PDImageXObject pdImage = PDImageXObject.createFromByteArray(document, qrBytes, "QR");

    content.drawImage(pdImage, PAGE_MARGIN, PAGE_MARGIN, qrWidth, qrHeight);
  }

  /** Dibuja un código de barras en la parte inferior derecha de la etiqueta. */
  private static void drawBarcode(
      PDPageContentStream content,
      PDDocument document,
      PDRectangle pageSize,
      String text,
      LabelSize size)
      throws Exception {

    int barcodeWidth = (int) (pageSize.getWidth() * BARCODE_WIDTH_RATIO);

    MultiFormatWriter writer = new MultiFormatWriter();
    BitMatrix matrix = writer.encode(text, BarcodeFormat.CODE_128, barcodeWidth, BARCODE_HEIGHT);

    BufferedImage image =
        new BufferedImage(barcodeWidth, BARCODE_HEIGHT, BufferedImage.TYPE_INT_RGB);

    for (int x = 0; x < barcodeWidth; x++) {
      for (int y = 0; y < BARCODE_HEIGHT; y++) {
        int color = matrix.get(x, y) ? COLOR_BLACK : COLOR_WHITE;
        image.setRGB(x, y, color);
      }
    }

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    javax.imageio.ImageIO.write(image, "png", baos);
    byte[] bytes = baos.toByteArray();
    baos.close();

    PDImageXObject pdImage = PDImageXObject.createFromByteArray(document, bytes, "BARCODE");

    float xPos = pageSize.getWidth() - barcodeWidth - PAGE_MARGIN;
    content.drawImage(pdImage, xPos, PAGE_MARGIN, barcodeWidth, BARCODE_HEIGHT);
  }

  /**
   * Calcula el tamaño máximo de fuente que permite que el texto encaje dentro de las dimensiones
   * dadas.
   */
  private static float calculateMaxFontSize(
      PDType1Font font, String text, float maxWidth, float maxHeight) throws Exception {

    float size = 1f;
    while (true) {
      float textWidth = font.getStringWidth(text) / PDFBOX_TEXT_WIDTH_FACTOR * size;
      if (textWidth > maxWidth || size > maxHeight) {
        return size - 1;
      }
      size += 1f;
    }
  }

  /** Devuelve el tamaño de página correspondiente al tamaño de etiqueta. */
  private static PDRectangle getPageSize(LabelSize size) {
    return switch (size) {
      case SMALL -> new PDRectangle(SMALL_WIDTH, SMALL_HEIGHT);
      case MEDIUM -> new PDRectangle(MEDIUM_WIDTH, MEDIUM_HEIGHT);
      case LARGE -> new PDRectangle(BIG_WIDTH, BIG_HEIGHT);
    };
  }

  /** Abre un archivo PDF con la aplicación predeterminada del sistema. */
  private static void openPdf(File file) {
    try {
      if (Desktop.isDesktopSupported()) {
        Desktop.getDesktop().open(file);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /** Simula la impresión de etiquetas mostrando un resumen. */
  private static void printLabels(List<Label> labels, int copies, LabelSize size) {
    StringBuilder sb = new StringBuilder();
    sb.append("=== IMPRIMIENDO ETIQUETAS ===\n");
    sb.append("Número de copias: ").append(copies).append("\n");
    sb.append("Tamaño de etiqueta: ").append(size.name()).append("\n");
    sb.append("Total de etiquetas: ").append(labels.size()).append("\n");
    sb.append("Etiquetas a imprimir:\n");
    for (Label label : labels) {
      sb.append("  - ").append(label.getCodigo()).append("\n");
    }
    sb.append("=============================\n");

    System.out.println(sb.toString());

    javafx.scene.control.Alert success =
        new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
    success.setTitle("Impresión completada");
    success.setHeaderText(null);
    success.setContentText(
        "Se han enviado " + (labels.size() * copies) + " etiqueta(s) a la impresora.");
    success.showAndWait();

    PrintLabelService.print(labels, copies, size);
  }
}
