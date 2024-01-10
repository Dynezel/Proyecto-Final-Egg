package com.proyectofinal.servicios;

import com.proyectofinal.entidades.Imagen;
import com.proyectofinal.entidades.Usuario;
import com.proyectofinal.enumeraciones.Rol;
import com.proyectofinal.excepciones.MiExcepcion;
import com.proyectofinal.excepciones.UsuarioNoEncontradoExcepcion;
import com.proyectofinal.repositorios.UsuarioRepositorio;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

@Service
@Transactional
public class UsuarioServicio implements UserDetailsService {

    @Autowired
    private UsuarioRepositorio usuarioRepositorio;

    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Autowired
    JavaMailSender javaMailSender;

    @Autowired
    private ImagenServicio imagenServicio;

    @Transactional
    public Usuario registrarUsuario(String idCodigoTributario, String nombre, String apellido, MultipartFile archivo,
            String direccion, String ciudad, String provincia, String DNI, String sexo, String email,
            String celular, String tipoPersona, String contrasenia, String contrasenia2, String selectedImagePath) throws MiExcepcion, Exception {

        validarDatosRegistro(idCodigoTributario, nombre, direccion, ciudad, provincia, DNI, email, celular, tipoPersona, contrasenia, contrasenia2);

        Usuario usuario = new Usuario();

        usuario.setApellido(apellido);
        usuario.setCelular(celular);
        usuario.setCiudad(ciudad);
        usuario.setDNI(DNI);
        usuario.setDireccion(direccion);
        usuario.setEmail(email);
        usuario.setIdCodigoTributario(idCodigoTributario);
        usuario.setNombre(nombre);
        usuario.setProvincia(provincia);
        usuario.setSexo(sexo);
        usuario.setTipoPersona(tipoPersona);
        usuario.setAlta(true);
        usuario.setContrasenia(new BCryptPasswordEncoder().encode(contrasenia));

        usuario.setRol(Rol.CLIENTE);

        if (!archivo.isEmpty()) {
            Imagen fotoPerfil = imagenServicio.guardarImagen(archivo);
            usuario.setFotoPerfil(fotoPerfil);
        } else if (!selectedImagePath.isEmpty()) {
            Imagen fotoPerfil = imagenServicio.guardarImagenRuta(selectedImagePath);
            usuario.setFotoPerfil(fotoPerfil);
        }

        usuarioRepositorio.save(usuario);

        return usuario;
    }

    @Transactional
    public void modificarUsuario(MultipartFile archivo,
            String idCodigoTributario,
            String direccion,
            String ciudad,
            String provincia,
            String sexo,
            String email,
            String celular,
            String tipoPersona,
            String rol) throws MiExcepcion, Exception {

        validarDatos(idCodigoTributario, direccion, ciudad, provincia,
                email, celular, tipoPersona);
        Usuario usuario = getOne(idCodigoTributario);
        usuario.setDireccion(direccion);
        usuario.setCiudad(ciudad);
        usuario.setProvincia(provincia);
        usuario.setEmail(email);
        usuario.setCelular(celular);
        usuario.setTipoPersona(tipoPersona);
        usuario.setRol(Rol.valueOf(rol));
        String idImagen = null;
        if (usuario.getFotoPerfil() != null) {
            idImagen = usuario.getFotoPerfil().getId();
        }
        Imagen imagen = imagenServicio.actualizar(archivo, idImagen);
        setImagenUsuario(archivo, idCodigoTributario);
        usuarioRepositorio.save(usuario);

    }

    @Transactional
    public void setImagenUsuario(MultipartFile archivo, String idCodigoTributario) throws Exception {
        Usuario usuario = usuarioRepositorio.buscarPorIdCodigoTributario(idCodigoTributario);

        if (usuario == null) {
            System.out.println("error"); // Manejar el caso en el que no se encuentre el usuario con el código tributario dado.
            return;
        }

        Imagen imagen = imagenServicio.guardarImagen(archivo);// Manejar errores de lectura de bytes de la imagen

        usuario.setFotoPerfil(imagen);
        // Guardar el usuario actualizado
        usuarioRepositorio.save(usuario);
    }

    @Override

    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        Usuario usuario = usuarioRepositorio.buscarPorEmail(email);

        if (usuario == null) {
            throw new UsernameNotFoundException("Usuario no encontrado con el email: " + email);
        }

        List<GrantedAuthority> permisos = new ArrayList<>();
        GrantedAuthority p = new SimpleGrantedAuthority("ROLE_" + usuario.getRol().toString());
        permisos.add(p);

        ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        HttpSession session = attr.getRequest().getSession(true);
        session.setAttribute("usuariosession", usuario);

        return new UsuarioDetalles(
                usuario.getEmail(),
                usuario.getContrasenia(),
                permisos,
                usuario.getIdCodigoTributario()
        );
    }

    @Transactional(readOnly = true)
    public Usuario getOne(String idCodigoTributario) {
        return usuarioRepositorio.getOne(idCodigoTributario);
    }

    @Transactional(readOnly = true)
    public List<Usuario> listarUsuarios() {

        return usuarioRepositorio.findAll();
    }

    @Transactional
    public void darBajaUsuario(String idCodigoTributario) {
        Optional<Usuario> respuesta = usuarioRepositorio.findById(idCodigoTributario);
        if (respuesta.isPresent()) {
            Usuario usuario = respuesta.get();
            usuario.setAlta(false);

            usuarioRepositorio.save(usuario);
        }
    }

    @Transactional
    public void darAltaUsuario(String idCodigoTributario) {
        Optional<Usuario> respuesta = usuarioRepositorio.findById(idCodigoTributario);
        if (respuesta.isPresent()) {
            Usuario usuario = respuesta.get();
            usuario.setAlta(true);

            usuarioRepositorio.save(usuario);
        }
    }

    @Transactional
    public void eliminarUsuario(String idCodigoTributario) {
        Optional<Usuario> respuesta = usuarioRepositorio.findById(idCodigoTributario);
        if (respuesta.isPresent()) {
            Usuario usuario = respuesta.get();

            usuarioRepositorio.delete(usuario);
        }
    }

    public boolean existeUsuarioConDNI(String DNI) {
        // Implementa la lógica para buscar un usuario por su DNI en la base de datos
        // Retorna true si el usuario con ese DNI existe, de lo contrario, retorna false
        return usuarioRepositorio.existsByDNI(DNI); // Ajusta según tu modelo y repositorio
    }

    @Transactional(readOnly = true)
    public Usuario obtenerUsuarioPorUsername(String username) throws MiExcepcion {
        Usuario usuario = usuarioRepositorio.buscarPorEmail(username);
        if (usuario == null) {
            throw new MiExcepcion("No se encontró un usuario con el username: " + username);
        }
        return usuario;
    }

    public void updateResetPwToken(String token, String email) throws UsuarioNoEncontradoExcepcion {

        Usuario usuario = usuarioRepositorio.buscarPorEmail(email);

        if (usuario != null) {
            usuario.setResetPwToken(token);
            usuarioRepositorio.save(usuario);
        } else {
            throw new UsuarioNoEncontradoExcepcion("No pudimos encontrar ningún usuario con el email" + email);
        }
    }

    public Usuario getResetPwToken(String token) {

        return usuarioRepositorio.buscarPorResetPwToken(token);
    }

    public void updatePassword(Usuario usuario, String newPassword) throws MiExcepcion {
        validarContrasenia(newPassword);
        System.out.println(usuario);
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encodePassword = passwordEncoder.encode(newPassword);
        usuario.setContrasenia(encodePassword);

        usuario.setResetPwToken(null);
        usuarioRepositorio.save(usuario);
    }

    public void validarDatosRegistro(String idCodigoTributario, String nombre, String direccion, String ciudad, String provincia, String DNI,
            String email, String celular, String tipoPersona, String contrasenia, String contrasenia2) throws MiExcepcion {

        if (idCodigoTributario == null || idCodigoTributario.isEmpty()) {
            throw new MiExcepcion("El código tributario no puede estar vacío o ser nulo.");
        }
        if (nombre == null || nombre.isEmpty()) {
            throw new MiExcepcion("El nombre no puede estar vacío o ser nulo.");
        }
        if (direccion == null || direccion.isEmpty()) {
            throw new MiExcepcion("La dirección no puede estar vacía o ser nula.");
        }
        if (ciudad == null || ciudad.isEmpty()) {
            throw new MiExcepcion("La ciudad no puede estar vacía o ser nula.");
        }
        if (provincia == null || provincia.isEmpty()) {
            throw new MiExcepcion("La provincia no puede estar vacía o ser nula.");
        }
        if (DNI == null || DNI.isEmpty()) {
            throw new MiExcepcion("El DNI no puede estar vacío o ser nulo.");
        }
        if (email == null || email.isEmpty()) {
            throw new MiExcepcion("El email no puede estar vacío, ni haber sido registrado anteriormente");
        }
        if (celular == null || celular.isEmpty()) {
            throw new MiExcepcion("El celular no puede estar vacío, ni haber sido registrado anteriormente");
        }
        if (email == null || email.isEmpty()) {
            throw new MiExcepcion("El email no puede estar vacío o ser nulo.");
        }
        if (celular == null || celular.isEmpty()) {
            throw new MiExcepcion("El celular no puede estar vacío o ser nulo.");
        }
        if (tipoPersona == null || tipoPersona.isEmpty()) {
            throw new MiExcepcion("El tipo de persona no puede estar vacío o ser nulo.");
        }
        validarContrasenia(contrasenia);
        if (!contrasenia.equals(contrasenia2)) {
            throw new MiExcepcion("Las contraseñas ingresadas deben ser iguales");
        }
        if (tipoPersona.equals("1")) {
            if (DNI == null) {
                throw new MiExcepcion("El DNI no puede estar vacío y debe contener solo números (sin puntos), <br> y debe ser de al menos 7 dígitos.");
            } else if (!DNI.matches("\\d{7,9}")) {
                throw new MiExcepcion("El DNI debe ser de al menos 7 dígitos y debe contener solo números (sin puntos).");
            }
        }
        if (idCodigoTributario == null) {
            throw new MiExcepcion("El Codigo tributario no puede estar vacío y debe contener solo números (sin puntos), <br> y debe ser de 11 dígitos.");
        } else if (!idCodigoTributario.matches("\\d{10,12}")) {
            throw new MiExcepcion("El Codigo tributario debe ser de 11 dígitos y debe contener solo números (sin puntos).");
        }
        if (contrasenia == null || contrasenia.isEmpty() || contrasenia.length() <= 5) {
            throw new MiExcepcion("La contraseña no puede estar vacía, y debe tener más de 5 dígitos");
        }
        if (contrasenia == null || contrasenia.isEmpty()) {
            throw new MiExcepcion("La contraseña no puede estar vacía o ser nula.");
        }
        if (contrasenia2 == null || contrasenia2.isEmpty()) {
            throw new MiExcepcion("La confirmación de la contraseña no puede estar vacía o ser nula.");
        }
        Usuario usuarioExistentePorCodigo = usuarioRepositorio.buscarPorIdCodigoTributario(idCodigoTributario);
        if (usuarioExistentePorCodigo != null) {
            throw new MiExcepcion("Ya existe un usuario con el código tributario " + idCodigoTributario + ".");
        }
        if (usuarioRepositorio.existsByDNI(DNI)) {
            throw new MiExcepcion("Ya existe un usuario con el DNI " + DNI + ".");
        }
        Usuario usuarioExistentePorMail = usuarioRepositorio.buscarPorEmail(email);
        if (usuarioExistentePorMail != null) {
            throw new MiExcepcion("Ya existe un usuario registrado con el email " + email + ".");
        }
    }

    public void validarDatos(String idCodigoTributario, String direccion, String ciudad, String provincia, String email,
            String celular, String tipoPersona) throws MiExcepcion {

        if (idCodigoTributario == null || idCodigoTributario.isEmpty()) {
            throw new MiExcepcion("El código tributario no puede estar vacío o ser nulo");
        }
        if (direccion == null || direccion.isEmpty()) {
            throw new MiExcepcion("El direccion no puede estar vacío o ser nulo");
        }
        if (ciudad == null || ciudad.isEmpty()) {
            throw new MiExcepcion("El ciudad no puede estar vacío o ser nulo");
        }
        if (provincia == null || provincia.isEmpty()) {
            throw new MiExcepcion("El código no puede estar vacío o ser nulo");
        }
        if (email == null || email.isEmpty()) {
            throw new MiExcepcion("El email no puede estar vacío, ni haber sido registrado anteriormente");
        }
        if (celular == null || celular.isEmpty()) {
            throw new MiExcepcion("El celular no puede estar vacío o ser nulo");
        }
        if (tipoPersona == null || tipoPersona.isEmpty()) {
            throw new MiExcepcion("El tipoPersona no puede estar vacío o ser nulo");
        }
    }

    public void validarContrasenia(String contrasenia) throws MiExcepcion {
        if (contrasenia == null || contrasenia.isEmpty() || contrasenia.length() <= 5) {
            throw new MiExcepcion("La contraseña no puede estar vacía, y debe tener más de 5 dígitos");
        }

    }

}
