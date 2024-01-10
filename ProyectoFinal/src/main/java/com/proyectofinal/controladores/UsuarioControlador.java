package com.proyectofinal.controladores;

import com.proyectofinal.entidades.Usuario;
import com.proyectofinal.excepciones.MiExcepcion;
import com.proyectofinal.servicios.UsuarioServicio;
import java.util.Arrays;
import java.util.List;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/usuario")
public class UsuarioControlador {

    @Autowired
    private UsuarioServicio usuarioServicio;

    @GetMapping("/") // funciona bien
    public String index() {
        return "index.html";
    }

    @GetMapping("/registrar")
    public String registrar(ModelMap modelo) {
        List<String> rutasImagenes = Arrays.asList(
                "/images/perfil/fotoperfil1.jpeg",
                "/images/perfil/fotoperfil2.jpeg",
                "/images/perfil/fotoperfil3.jpeg"
        );
        modelo.addAttribute("imagenesPreCargadas", rutasImagenes);
        return "usuario_form.html";
    }

    //registroControlador
    @PostMapping("/registrar")
    public String registro(@RequestParam("idCodigoTributario") String idCodigoTributario,
            @RequestParam("archivo") MultipartFile archivo,
            @RequestParam("nombre") String nombre,
            @RequestParam("apellido") String apellido,
            @RequestParam("direccion") String direccion,
            @RequestParam("ciudad") String ciudad,
            @RequestParam("provincia") String provincia,
            @RequestParam("DNI") String DNI,
            @RequestParam("sexo") String sexo,
            @RequestParam("email") String email,
            @RequestParam("celular") String celular,
            @RequestParam("tipoPersona") String tipoPersona,
            @RequestParam("contrasenia") String contrasenia,
            @RequestParam("contrasenia2") String contrasenia2,
            @RequestParam("selectedImagePath") String selectedImagePath,
            ModelMap modelo) throws Exception {
        try {
            usuarioServicio.registrarUsuario(idCodigoTributario, nombre, apellido, archivo, direccion, ciudad,
                    provincia, DNI, sexo, email, celular, tipoPersona, contrasenia, contrasenia2, selectedImagePath);
            modelo.put("exito", "Usuario registrado correctamente");
            return "redirect:/login";
        } catch (MiExcepcion ex) {
            modelo.put("error", ex.getMessage());
            modelo.put("idCodigoTributario", idCodigoTributario);
            modelo.put("apellido", apellido);
            modelo.put("direccion", direccion);
            modelo.put("ciudad", ciudad);
            modelo.put("provincia", provincia);
            modelo.put("DNI", DNI);
            modelo.put("sexo", sexo);
            modelo.put("email", email);
            modelo.put("celular", celular);
            modelo.put("tipoPersona", tipoPersona);
            modelo.put("contrasenia", contrasenia);
            modelo.put("contrasenia2", contrasenia2);

            return "usuario_form.html";
        }
    }

    @PreAuthorize("hasAnyRole('ROLE_CLIENTE','ROLE_ADMIN','ROLE_ENTE')")
    @GetMapping("/perfil/{idCodigoTributario}")
    public String perfil(ModelMap modelo, HttpSession session) {

        Usuario usuario = (Usuario) session.getAttribute("usuariosession");

        modelo.put("usuario", usuario);

        return "usuario_perfil.html";
    }

    @PreAuthorize("hasAnyRole('ROLE_CLIENTE', 'ROLE_ADMIN', 'ROLE_ENTE')")
    @PostMapping("/perfil/{idCodigoTributario}")
    public String subirFoto(
            @RequestParam("archivo") MultipartFile archivo,
            @RequestParam("idCodigoTributario") String idCodigoTributario,
            ModelMap modelo
    ) {
        try {
            usuarioServicio.setImagenUsuario(archivo, idCodigoTributario);
            modelo.put("exito", "Su imagen se subió correctamente!");
        } catch (Exception e) {
            modelo.put("error", "Error al subir la imagen: " + e.getMessage());
        }
        return "usuario_perfil.html";
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_CLIENTE','ROLE_ENTE')")
    @GetMapping("/modificar/{idCodigoTributario}")
    public String modificar(ModelMap modelo, HttpSession session) {

        Usuario usuario = (Usuario) session.getAttribute("usuariosession");
        List<String> rutasImagenes = Arrays.asList(
                "/images/perfil/fotoperfil1.jpeg",
                "/images/perfil/fotoperfil2.jpeg",
                "/images/perfil/fotoperfil3.jpeg"
        );
        modelo.addAttribute("imagenesPreCargadas", rutasImagenes);
        modelo.put("usuario", usuario);

        return "usuario_modificar.html";
    }

    @PreAuthorize("hasAnyRole('ROLE_CLIENTE','ROLE_ADMIN','ROLE_ENTE')")
    @PostMapping("/modificar/{idCodigoTributario}")
    public String modificar(
            @PathVariable("idCodigoTributario") String idCodigoTributario,
            @RequestParam(required = false) MultipartFile archivo,
            @RequestParam("direccion") String direccion,
            @RequestParam("ciudad") String ciudad,
            @RequestParam("provincia") String provincia,
            @RequestParam("sexo") String sexo,
            @RequestParam("email") String email,
            @RequestParam("celular") String celular,
            @RequestParam("tipoPersona") String tipoPersona,
            @RequestParam("Rol") String rol,
            ModelMap modelo) throws Exception {
        try {
            usuarioServicio.modificarUsuario(archivo, idCodigoTributario, direccion, ciudad, provincia,
                    sexo, email, celular, tipoPersona, rol);
            modelo.put("exito", "Usuario actualizado correctamente!");
            return "usuario_form_exito.html";
        } catch (DataIntegrityViolationException e) {
            //} catch (DataIntegrityViolationException e) Manejar la excepción de violación de unicidad (ID duplicado)
            modelo.addAttribute("error", "Ya existe una entidad con ese ID");
            return "usuario_form.html";
        } catch (MiExcepcion ex) {
            modelo.put("error", ex.getMessage());
            modelo.put("email", email);
            return "usuario_modificar.html";
        }
    }

    @GetMapping("/lista")
    public String listarUsuario(ModelMap modelo) throws Exception {
        List<Usuario> usuarios = usuarioServicio.listarUsuarios();
        modelo.addAttribute("usuarios", usuarios);

        return "usuario_lista";
    }

    @GetMapping("/eliminar/{idCodigoTributario}")
    public String eliminarUsuario(@PathVariable("idCodigoTributario") String idCodigoTributario,
            ModelMap modelo) throws Exception {
        usuarioServicio.eliminarUsuario(idCodigoTributario);

        return "index.html";
    }

    @GetMapping("/dar-baja/{idCodigoTributario}")
    public String darBajaUsuario(@PathVariable("idCodigoTributario") String idCodigoTributario,
            ModelMap modelo) throws Exception {
        usuarioServicio.darBajaUsuario(idCodigoTributario);

        return "index.html";
    }

    @GetMapping("/verificarDNI")
    public ResponseEntity<Object> verificarDNI(@RequestParam String DNI) {
        boolean dniEncontrado = usuarioServicio.existeUsuarioConDNI(DNI);
        System.out.println(DNI);

        if (dniEncontrado) {
            return ResponseEntity.status(HttpStatus.OK).body("DNI encontrado");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("DNI no encontrado");
        }
    }

    @GetMapping("/dar-alta/{idCodigoTributario}")
    public String darAltaUsuario(@PathVariable("idCodigoTributario") String idCodigoTributario,
            ModelMap modelo) throws Exception {
        usuarioServicio.darAltaUsuario(idCodigoTributario);

        return "index.html";
    }
    /*
    Evaluando la incorporacion de este metodo para optimizar el codigo de las validaciones. ->  Emi
    
    private ResponseEntity<?> validation(BindingResult result) {
                Map<String, String> errors = new HashMap<>();

        result.getFieldErrors().forEach(err -> {
            errors.put(err.getField(), "El campo " + err.getField() + " " + err.getDefaultMessage());
        });
        return ResponseEntity.badRequest().body(errors);
    }
update(@Valid @RequestBody UserRequest user, BindingResult result, @PathVariable Long id)
if(result.hasErrors()){
            return validation(result);
        }*/
}
