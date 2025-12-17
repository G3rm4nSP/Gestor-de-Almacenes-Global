package com.openwarehouses.controllers;

import com.openwarehouses.models.Almacen;
import com.openwarehouses.models.Estanteria;
import com.openwarehouses.models.Pasillo;
import com.openwarehouses.services.StorageService;
import com.openwarehouses.services.ValidationService;
import java.util.List;

/**
 * Controlador para gestionar operaciones de Estanterías. Maneja creación,
 * edición y eliminación de
 * estanterías dentro de un pasillo.
 *
 * @author German
 * @version 1.0
 * @since 2025-12-16
 */
public class EstanteriaController {
  /** Servicio de almacenamiento para persistencia de datos. */
  private final StorageService storageService;

  /** Pasillo actual. */
  private Pasillo pasilloActual;

  /** Lista de almacenes. */
  private List<Almacen> almacenes;

  /**
   * Constructor que inicializa el controlador con servicio de almacenamiento y
   * lista de almacenes.
   *
   * @param pasilloActual  Pasillo en la que se encuentra esta Estanteria
   * @param almacenes      Lista de almacenes a gestionar
   * @param storageService Servicio de almacenamiento para persistencia de datos
   */
  public EstanteriaController(
      Pasillo pasilloActual, List<Almacen> almacenes, StorageService storageService) {
    this.pasilloActual = pasilloActual;
    this.almacenes = almacenes;
    this.storageService = storageService;
  }

  /**
   * Establece el pasillo actual.
   *
   * @param pasillo Pasillo a trabajar
   */
  public void setPasilloActual(Pasillo pasillo) {
    this.pasilloActual = pasillo;
  }

  /**
   * Obtiene el pasillo actual.
   *
   * @return Pasillo actual
   */
  public Pasillo getPasilloActual() {
    return pasilloActual;
  }

  /**
   * Crea una nueva estantería en el pasillo actual.
   *
   * @param numero Número de la estantería
   * @return true si se creó exitosamente, false si el número ya existe
   */
  public boolean crearEstanteria(int numero) {
    if (!ValidationService.isNumeroValido(numero)) {
      return false;
    }

    if (!ValidationService.isEstanteriaNumeroValid(numero, pasilloActual)) {
      return false;
    }

    Estanteria nuevaEstanteria = new Estanteria(numero);
    pasilloActual.addEstanteria(nuevaEstanteria);
    guardar();
    return true;
  }

  /**
   * Edita el número de una estantería existente.
   *
   * @param estanteriaActual Estantería actual
   * @param nuevoNumero      Nuevo número
   * @return true si se editó exitosamente, false en caso contrario
   */
  public boolean editarEstanteria(Estanteria estanteriaActual, int nuevoNumero) {
    if (!ValidationService.isNumeroValido(nuevoNumero)) {
      return false;
    }

    if (nuevoNumero == estanteriaActual.getNumero()) {
      return true; // Sin cambios
    }

    if (!ValidationService.isEstanteriaNumeroValid(nuevoNumero, pasilloActual)) {
      return false; // El número ya existe
    }

    estanteriaActual.setNumero(nuevoNumero);
    guardar();
    return true;
  }

  /**
   * Elimina una estantería del pasillo actual.
   *
   * @param estanteria Estantería a eliminar
   */
  public void eliminarEstanteria(Estanteria estanteria) {
    pasilloActual.removeEstanteria(estanteria);
    guardar();
  }

  /**
   * Obtiene una estantería por su número.
   *
   * @param numero Número de la estantería
   * @return Estantería encontrada o null
   */
  public Estanteria getEstanteriaByNumero(int numero) {
    if (pasilloActual == null) {
      return null;
    }
    return pasilloActual.getEstanteriaByNumero(numero);
  }

  /** Guarda los datos en el almacenamiento. */
  private void guardar() {
    storageService.saveAlmacenes(almacenes);
  }
}
