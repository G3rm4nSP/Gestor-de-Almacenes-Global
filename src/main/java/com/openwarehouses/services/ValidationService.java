package com.openwarehouses.services;

import java.util.List;

import com.openwarehouses.models.Almacen;
import com.openwarehouses.models.Altura;
import com.openwarehouses.models.Estanteria;
import com.openwarehouses.models.Pasillo;

/**
 * Servicio encargado de validar la integridad de datos en la jerarquía.
 * Previene duplicados y
 * valida números únicos en cada nivel.
 *
 * @author German
 * @version 1.0
 * @since 2025-12-16
 */
public final class ValidationService {

  /** Constructor privado para evitar instanciación. */
  private ValidationService() {

  }

  /**
   * Valida que un nombre de almacén no exista.
   *
   * @param nombre    Nombre a validar
   * @param almacenes Lista de almacenes existentes
   * @return true si el nombre es válido (no existe), false en caso contrario
   */
  public static boolean isAlmacenNameValid(String nombre, List<Almacen> almacenes) {
    return almacenes.stream().noneMatch(a -> a.getNombre().equalsIgnoreCase(nombre));
  }

  /**
   * Valida que un número de pasillo no exista en el almacén.
   *
   * @param numero  Número a validar
   * @param almacen Almacén donde validar
   * @return true si el número es válido (no existe), false en caso contrario
   */
  public static boolean isPasilloNumeroValid(int numero, Almacen almacen) {
    return almacen.getPasillos().stream().noneMatch(p -> p.getNumero() == numero);
  }

  /**
   * Valida que un número de estantería no exista en el pasillo.
   *
   * @param numero  Número a validar
   * @param pasillo Pasillo donde validar
   * @return true si el número es válido (no existe), false en caso contrario
   */
  public static boolean isEstanteriaNumeroValid(int numero, Pasillo pasillo) {
    return pasillo.getEstanterias().stream().noneMatch(e -> e.getNumero() == numero);
  }

  /**
   * Valida que un número de altura no exista en la estantería.
   *
   * @param numero     Número a validar
   * @param estanteria Estantería donde validar
   * @return true si el número es válido (no existe), false en caso contrario
   */
  public static boolean isAlturaNumeroValid(int numero, Estanteria estanteria) {
    return estanteria.getAlturas().stream().noneMatch(a -> a.getNumero() == numero);
  }

  /**
   * Valida que un número de posición no exista en la altura.
   *
   * @param numero Número a validar
   * @param altura Altura donde validar
   * @return true si el número es válido (no existe), false en caso contrario
   */
  public static boolean isPosicionNumeroValid(int numero, Altura altura) {
    return altura.getPosiciones().stream().noneMatch(p -> p.getNumero() == numero);
  }

  /**
   * Genera el código único para una posición: pasillo.estanteria.altura.posicion
   *
   * @param numeroPasillo    Número del pasillo
   * @param numeroEstanteria Número de la estantería
   * @param numeroAltura     Número de la altura
   * @param numeroPosicion   Número de la posición
   * @return Código en formato X.Y.Z.P
   */
  public static String generateCodigo(
      int numeroPasillo, int numeroEstanteria, int numeroAltura, int numeroPosicion) {
    return numeroPasillo + "." + numeroEstanteria + "." + numeroAltura + "." + numeroPosicion;
  }

  /**
   * Valida que un número sea positivo.
   *
   * @param numero Número a validar
   * @return true si es válido, false en caso contrario
   */
  public static boolean isNumeroValido(int numero) {
    return numero > 0;
  }

  /**
   * Valida que una cadena no esté vacía o sea nula.
   *
   * @param texto Texto a validar
   * @return true si es válido, false en caso contrario
   */
  public static boolean isTextoValido(String texto) {
    return texto != null && !texto.trim().isEmpty();
  }
}
