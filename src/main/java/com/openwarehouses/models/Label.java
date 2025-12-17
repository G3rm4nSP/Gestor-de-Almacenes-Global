package com.openwarehouses.models;

/**
 * Modelo que representa una etiqueta de posición para impresión. Contiene el código único de la
 * posición (pasillo.estantería.altura.posición).
 *
 * @author German
 * @version 1.0
 * @since 2024-12-16
 */
public class Label {
  /** Código de la etiqueta (formato: pasillo.estanteria.altura.posicion). */
  private String codigo;

  /** Número del pasillo. */
  private int numeroPasillo;

  /** Número de la estantería. */
  private int numeroEstanteria;

  /** Número de la altura. */
  private int numeroAltura;

  /** Número de la posición. */
  private int numeroPosicion;

  /**
   * Constructor que crea una etiqueta con el código de posición.
   *
   * @param codigo Código de posición (ej: "2.3.1.5")
   * @param numeroPasillo Número del pasillo
   * @param numeroEstanteria Número de la estantería
   * @param numeroAltura Número de la altura
   * @param numeroPosicion Número de la posición
   */
  public Label(
      String codigo,
      int numeroPasillo,
      int numeroEstanteria,
      int numeroAltura,
      int numeroPosicion) {
    this.codigo = codigo;
    this.numeroPasillo = numeroPasillo;
    this.numeroEstanteria = numeroEstanteria;
    this.numeroAltura = numeroAltura;
    this.numeroPosicion = numeroPosicion;
  }

  /**
   * Obtiene el código de la etiqueta.
   *
   * @return Código de la etiqueta
   */
  public String getCodigo() {
    return codigo;
  }

  /**
   * Establece el código de la etiqueta.
   *
   * @param codigo Código de la etiqueta
   */
  public void setCodigo(String codigo) {
    this.codigo = codigo;
  }

  /**
   * Obtiene el número del pasillo.
   *
   * @return Número del pasillo
   */
  public int getNumeroPasillo() {
    return numeroPasillo;
  }

  /**
   * Obtiene el número de la estantería.
   *
   * @return Número de la estantería
   */
  public int getNumeroEstanteria() {
    return numeroEstanteria;
  }

  /**
   * Obtiene el número de la altura.
   *
   * @return Número de la altura
   */
  public int getNumeroAltura() {
    return numeroAltura;
  }

  /**
   * Obtiene el número de la posición.
   *
   * @return Número de la posición
   */
  public int getNumeroPosicion() {
    return numeroPosicion;
  }

  @Override
  /**
   * Representación en cadena de la etiqueta.
   *
   * @return cadena representando la etiqueta
   */
  public String toString() {
    return "Label{" + "codigo='" + codigo + '\'' + '}';
  }
}
