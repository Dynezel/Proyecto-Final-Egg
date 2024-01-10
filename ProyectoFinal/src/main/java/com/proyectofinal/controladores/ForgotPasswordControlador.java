package com.proyectofinal.controladores;

import com.proyectofinal.entidades.Usuario;
import com.proyectofinal.entidades.Utility;
import com.proyectofinal.excepciones.MiExcepcion;
import com.proyectofinal.excepciones.UsuarioNoEncontradoExcepcion;
import com.proyectofinal.servicios.UsuarioServicio;
import java.io.UnsupportedEncodingException;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
import net.bytebuddy.utility.RandomString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class ForgotPasswordControlador {

    @Autowired
    private UsuarioServicio usuarioServicio;

    @Autowired
    private JavaMailSender mailSender;

    @GetMapping("/pwreset")
    public String mostrarPwResetForm(Model model) {
        model.addAttribute("pageTitle", "Olvido sus credenciales??");
        return "pwreset";
    }

    @PostMapping("/pwreset")
    public String FormProcesopwReset(HttpServletRequest request, Model model) {
        String email = request.getParameter("email");
        String token = RandomString.make(30);

        try {
            usuarioServicio.updateResetPwToken(token, email);
            String resetPasswordLink = Utility.getSiteURL(request) + "/reset_password?token=" + token;
            sendEmail(email, resetPasswordLink);
            model.addAttribute("mensaje", "Enviamos el link, por favor revise su correo electronico");
        } catch (UsuarioNoEncontradoExcepcion ex) {
            model.addAttribute("error", ex.getMessage());
        } catch (UnsupportedEncodingException | MessagingException e) {
            model.addAttribute("error", "Error al enviar el email.");
            return "/pwreset";
        }
//         model.addAttribute("pageTitle", "Olvido sus credenciales??");

        return "/pwreset";
    }

    private void sendEmail(String email, String resetPasswordLink) throws MessagingException, UnsupportedEncodingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        helper.setFrom("facio.gabrielemiliano@gmail.com", "Buscasitas.com.ar - Soporte ");
        helper.setTo(email);

        String asunto = "Aqui le compartimos el link para resetear su password";

        String cuerpoMensaje = "<p>Hola,</p>"
                + "<p>Usted ha hecho una solicitud para resetear sus credenciales. </p>"
                + "<p>Haga click en el link que le proporcionamos debajo de este mensaje.</p> "
                + "<p><b><a href=\"" + resetPasswordLink + "\">Cambiar mis credenciales</b></a></p>"
                + "<p>Ignore este mail si recordo su password, o si usted no hizo esta solicitud.</p> ";

        helper.setSubject(asunto);
        helper.setText(cuerpoMensaje, true);

        mailSender.send(message);
    }

    @GetMapping("/reset_password")
    public String mostrarResetPwForm(@Param(value = "token") String token, Model model) {
        Usuario usuario = usuarioServicio.getResetPwToken(token);
        model.addAttribute("token", token);

        if (usuario == null) {
            model.addAttribute("title", "Elija su nuevo password");//descomentado
            model.addAttribute("error", "Invalid Token");

            return "error";
        }
//         model.addAttribute("pageTitle","Ingresa un nuevo password. ");
        return "reset_password.html";
    }

    @PostMapping("/reset_password")
    public String processResetPassword(HttpServletRequest request, ModelMap modelo) throws MiExcepcion {
        String token = request.getParameter("token");
        String password = request.getParameter("password");

        Usuario usuario = usuarioServicio.getResetPwToken(token);
        System.out.println(usuario);
        modelo.addAttribute("title", "Resetea tu password");

        if (usuario == null) {
            modelo.addAttribute("error", "Invalid Token");
            return "error";

        } else if (password == null || password.isEmpty()) {
            modelo.addAttribute("mensaje", "La contraseÃ±a no puede estar vacÃ­a.");
            return "reset_password.html";
        } else {
            try {
                usuarioServicio.updatePassword(usuario, password);
                modelo.put("exito", "ContraseÃ±a cambiada correctamente");
            } catch (MiExcepcion ex) {
                modelo.put("error", ex.getMessage());
                return "reset_password.html";

            }
            modelo.addAttribute("mensaje", "Su password ha sido modificado satisfactoriamente.");
        }

        return "index.html";
    }

}

//     @GetMapping("/pwreset")
//    public String pwreset() {
//        
//        return "pwreset.html";
//    }
//    
//    @PostMapping("/pwreset")
//    public String pwResetProceso(@RequestParam String email, ModelMap modelo){
//        String output = "";
//        Usuario usuario = usuarioRepositorio.buscarPorEmail(email);
//                if (usuario!= null) { 
//           output = usuarioServicio.sendEmail();
//           
//              // mensaje de Ã©xito o error al modelo para mostrarlo en la vista
//        if (output.equals("enviado_exitosamente")) {
//            modelo.addAttribute("mensaje", "El correo se ha enviado exitosamente.");
//        } else {
//            modelo.addAttribute("mensaje", "Hubo un problema al enviar el correo.");
//        }
//    } else {
//        modelo.addAttribute("mensaje", "Usuario no encontrado");
//    }
//
//     
//        
//    return "";
//    
//}
