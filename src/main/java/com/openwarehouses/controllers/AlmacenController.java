package com.openwarehouses.controllers;

import com.openwarehouses.models.Almacen;
import com.openwarehouses.services.StorageService;
import com.openwarehouses.services.ValidationService;
import java.util.List;

/**
 * Controlador para gestionar operaciones de Almacenes. Maneja creación, edición
 * y eliminación de
 * almacenes.
 *
 * @author German
 * @version 1.0
 * @since 2025-12-16
 */
public class AlmacenController {
  /** Servicio de almacenamiento para persistencia de datos. */
  private final StorageService storageService;

  /** Lista de almacenes gestionados. */
  private List<Almacen> almacenes;

  /**
   * Constructor que inicializa el controlador con servicio de almacenamiento y
   * lista de almacenes.
   *
   * @param storageService Servicio de almacenamiento para persistencia de datos
   * @param almacenes      Lista de almacenes a gestionar
   */
  public AlmacenController(StorageService storageService, List<Almacen> almacenes) {
    this.storageService = storageService;
    this.almacenes = almacenes;
  }

  /** Constructor vacío para solo visualizar datos. */
  public AlmacenController() {
    this.storageService = new StorageService();
    this.almacenes = storageService.loadAlmacenes();
  }

  /**
   * Obtiene todos los almacenes.
   *
   * @return Lista de almacenes
   */
  public List<Almacen> getAllAlmacenes() {
    return almacenes;
  }

  /**
   * Crea un nuevo almacén.
   *
   * @param nombre Nombre del almacén
   * @return true si se creó exitosamente, false si el nombre ya existe
   */
  public boolean crearAlmacen(String nombre) {
    if (!ValidationService.isTextoValido(nombre)) {
      return false;
    }

    if (!ValidationService.isAlmacenNameValid(nombre, almacenes)) {
      return false;
    }

    Almacen nuevoAlmacen = new Almacen(nombre);
    almacenes.add(nuevoAlmacen);
    guardar();
    return true;
  }

  /**
   * Edita el nombre de un almacén existente.
   *
   * @param almacenActual Almacén actual
   * @param nuevoNombre   Nuevo nombre
   * @return true si se editó exitosamente, false en caso contrario
   */
  public boolean editarAlmacen(Almacen almacenActual, String nuevoNombre) {
    if (!ValidationService.isTextoValido(nuevoNombre)) {
      return false;
    }

    if (nuevoNombre.equalsIgnoreCase(almacenActual.getNombre())) {
      return true; // Sin cambios
    }

    if (!ValidationService.isAlmacenNameValid(nuevoNombre, almacenes)) {
      return false; // El nombre ya existe
    }

    almacenActual.setNombre(nuevoNombre);
    guardar();
    return true;
  }

  /**
   * Elimina un almacén.
   *
   * @param almacen Almacén a eliminar
   */
  public void eliminarAlmacen(Almacen almacen) {
    almacenes.remove(almacen);
    guardar();
  }

  /**
   * Obtiene un almacén por su nombre.
   *
   * @param nombre Nombre del almacén
   * @return Almacén encontrado o null
   */
  public Almacen getAlmacenByNombre(String nombre) {
    return almacenes.stream()
        .filter(a -> a.getNombre().equalsIgnoreCase(nombre))
        .findFirst()
        .orElse(null);
  }

  /** Recarga los datos desde el almacenamiento. */
  public void recargar() {
    this.almacenes = storageService.loadAlmacenes();
  }

  /** Guarda los datos en el almacenamiento. */
  private void guardar() {
    storageService.saveAlmacenes(almacenes);
  }

  /**
   * Verifica si hay almacenes disponibles.
   *
   * @return true si hay almacenes, false en caso contrario
   */
  public boolean hasAlmacenes() {
    return !almacenes.isEmpty();
  }
}
