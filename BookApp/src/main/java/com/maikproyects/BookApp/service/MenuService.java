package com.maikproyects.BookApp.service;


import com.maikproyects.BookApp.model.Autor;
import com.maikproyects.BookApp.model.Idioma;
import com.maikproyects.BookApp.model.Libro;
import com.maikproyects.BookApp.model.LibroRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class MenuService {
    private ApiService peticionAPI;
    private Scanner sc;
    private LibroService libroService;
    private AutorService autorService;
    private JsonParser jsonParser;

    @Autowired
    public MenuService(LibroService libroService, AutorService autorService, JsonParser jsonParser) {
        this.peticionAPI = new ApiService();
        this.sc = new Scanner(System.in);
        this.libroService = libroService;
        this.autorService = autorService;
        this.jsonParser = jsonParser;
    }

    public void guardarLibro() {
        List<LibroRecord> librosObtenidos = obtenerLibrosApi();

        if (librosObtenidos.isEmpty()) {
            System.out.println("No se encontró ningún libro");
            return;
        }

        System.out.println("Escoja un libro para guardar [0-Cancelar]");
        for (int i = 0; i < librosObtenidos.size(); i++) {
            String idioma = librosObtenidos.get(i).idiomas().isEmpty() ? "Idioma desconocido" : librosObtenidos.get(i).idiomas().get(0);
            String autor = librosObtenidos.get(i).autores().isEmpty() ? "Autor desconocido" : librosObtenidos.get(i).autores().get(0).nombre();

            System.out.println((i + 1) + " - " + librosObtenidos.get(i).titulo() + " - " + idioma + " - " + autor);
        }

        int opcion = sc.nextInt();
        sc.nextLine();
        if (opcion == 0) {
            return;
        }
        if (opcion < 1 || opcion > librosObtenidos.size()) {
            System.out.println("Error: número erróneo");
            return;
        }

        LibroRecord libroRecord = librosObtenidos.get(opcion - 1);
        Optional<Libro> libroTraidoDelRepo = libroService.obtenerLibroPorNombre(libroRecord.titulo());

        if (libroTraidoDelRepo.isPresent()) {
            System.out.println("Error: no se puede registrar dos veces el mismo libro");
            return;
        }

        // Crear el libro y manejar autor
        Libro libro = new Libro(libroRecord);
        if (!libroRecord.autores().isEmpty()) {
            String nombreAutor = libroRecord.autores().get(0).nombre();
            Optional<Autor> autorTraidodelRepo = autorService.obtenerAutorPorNombre(nombreAutor);

            if (autorTraidodelRepo.isPresent()) {
                libro.setAutor(autorTraidodelRepo.get());
            } else {
                Autor autorNuevo = libro.getAutor();
                autorService.guardarAutor(autorNuevo);
                libro.setAutor(autorNuevo);
            }
        } else {
            System.out.println("Advertencia: el libro seleccionado no tiene autor.");
        }

        libroService.guardarLibro(libro);
        System.out.println("El libro ha sido guardado exitosamente.");
    }


    public List<LibroRecord> obtenerLibrosApi() {
        System.out.print("Ingrese el título del libro [0-Cancelar]: ");
        String titulo = sc.nextLine();
        if (titulo.equals("0")) {
            return Collections.emptyList();
        }
        List<LibroRecord> librosObtenidos;
        librosObtenidos = jsonParser.parsearLibros(peticionAPI.obtenerDatos(titulo));
        return librosObtenidos;
    }


    public void listarLibrosRegistrados() {
        List<Libro> libros = libroService.obtenerTodosLosLibros();
        libros.forEach(libro -> libro.imprimirInformacion());
    }

    public void listarAutoresRegistrados() {
        List<Autor> autores = autorService.obtenerTodosLosAutores();
        autores.forEach(autor -> autor.imprimirInformacion());
    }

    public void listarAutoresVivosEnAnio() {
        try {
            System.out.print("Ingrese año: ");
            int anio = sc.nextInt();
            sc.nextLine();
            List<Autor> autores = autorService.obtenerAutoresVivosEnAnio(anio);
            autores.forEach(autor -> autor.imprimirInformacion());
        } catch (InputMismatchException e) {
            System.out.println("Error: debe ingresar un número");
        }

    }

    public void listarLibrosPorIdioma() {
        Idioma.listarIdiomas();
        System.out.print("Ingrese el codigo del idioma [0-Cancelar]: ");
        String idiomaBuscado = sc.nextLine();
        if (idiomaBuscado.equals("0")) {
            return;
        }
        List<Libro> libros = libroService.obtenerLibrosPorIdioma(Idioma.stringToEnum(idiomaBuscado));
        libros.forEach(libro -> libro.imprimirInformacion());
    }
}
