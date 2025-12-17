package com.openwarehouses.controllers;

import java.util.List;

import com.openwarehouses.models.Almacen;
import com.openwarehouses.models.Altura;
import com.openwarehouses.models.Estanteria;
import com.openwarehouses.services.StorageService;
import com.openwarehouses.services.ValidationService;

/**
 * Controlador para gestionar operaciones de Alturas. Maneja creación, edición y
 * eliminación de
 * alturas dentro de una estantería.
 *
 * @author German
 * @version 1.0
 * @since 2025-12-16
 */
public class AlturaController {

  /** Servicio de almacenamiento para persistencia de datos. */
  private final StorageService storageService;

  /** Estantería actual. */
  private Estanteria estanteriaActual;

  /** Lista de almacenes. */
  private List<Almacen> almacenes;

  /**
   * Constructor que inicializa el controlador con servicio de almacenamiento y
   * lista de almacenes.
   *
   * @param estanteriaActual Estanteria en la que se encuentra esta Altura
   * @param almacenes        Lista de almacenes a gestionar
   * @param storageService   Servicio de almacenamiento para persistencia de datos
   */
  public AlturaController(
      Estanteria estanteriaActual, List<Almacen> almacenes, StorageService storageService) {
    this.estanteriaActual = estanteriaActual;
    this.almacenes = almacenes;
    this.storageService = storageService;
  }

  /**
   * Establece la estantería actual.
   *
   * @param estanteria Estantería a trabajar
   */
  public void setEstanteriaActual(Estanteria estanteria) {
    this.estanteriaActual = estanteria;
  }

  /**
   * Obtiene la estantería actual.
   *
   * @return Estantería actual
   */
  public Estanteria getEstanteriaActual() {
    return estanteriaActual;
  }

  /**
   * Crea una nueva altura en la estantería actual.
   *
   * @param numero Número de la altura
   * @return true si se creó exitosamente, false si el número ya existe
   */
  public boolean crearAltura(int numero) {
    if (!ValidationService.isNumeroValido(numero)) {
      return false;
    }

    if (!ValidationService.isAlturaNumeroValid(numero, estanteriaActual)) {
      return false;
    }

    Altura nuevaAltura = new Altura(numero);
    estanteriaActual.addAltura(nuevaAltura);
    guardar();
    return true;
  }

  /**
   * Edita el número de una altura existente.
   *
   * @param alturaActual Altura actual
   * @param nuevoNumero  Nuevo número
   * @return true si se editó exitosamente, false en caso contrario
   */
  public boolean editarAltura(Altura alturaActual, int nuevoNumero) {
    if (!ValidationService.isNumeroValido(nuevoNumero)) {
      return false;
    }

    if (nuevoNumero == alturaActual.getNumero()) {
      return true; // Sin cambios
    }

    if (!ValidationService.isAlturaNumeroValid(nuevoNumero, estanteriaActual)) {
      return false; // El número ya existe
    }

    alturaActual.setNumero(nuevoNumero);
    guardar();
    return true;
  }

  /**
   * Elimina una altura de la estantería actual.
   *
   * @param altura Altura a eliminar
   */
  public void eliminarAltura(Altura altura) {
    estanteriaActual.removeAltura(altura);
    guardar();
  }

  /**
   * Obtiene una altura por su número.
   *
   * @param numero Número de la altura
   * @return Altura encontrada o null
   */
  public Altura getAlturaByNumero(int numero) {
    if (estanteriaActual == null) {
      return null;
    }
    return estanteriaActual.getAlturaByNumero(numero);
  }

  /** Guarda los datos en el almacenamiento. */
  private void guardar() {
    storageService.saveAlmacenes(almacenes);
  }
}
