package com.javali.controllers.web;

import com.javali.dtos.UsuarioRequest;
import com.javali.exceptions.EmailYaRegistradoException;
import com.javali.services.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class RegistroWebController {

    private final UsuarioService usuarioService;

    public RegistroWebController(
            UsuarioService usuarioService) {

        this.usuarioService = usuarioService;
    }

    //Muestra el formulario de registro.
    @GetMapping("/registro")
    public String mostrarRegistro(Model model) {
        model.addAttribute(
                "usuario",
                new UsuarioRequest("", "", "")
        );
        return "registro";
    }
    
    //Registra un estudiante desde la interfaz web.
    @PostMapping("/registro")
    public String registrar(
            @Valid UsuarioRequest request,
            BindingResult resultado,
            Model model) {
        if (resultado.hasErrors()) {
            model.addAttribute("usuario", request);
            return "registro";
        }
        try {
            usuarioService.registrar(request);
        } catch (EmailYaRegistradoException exception) {
            model.addAttribute("usuario", request);
            model.addAttribute(
                    "errorEmail",
                    exception.getMessage()
            );
            return "registro";
        }
        return "redirect:/registro-exitoso";
    }

    @GetMapping("/registro-exitoso")
    public String mostrarRegistroExitoso() {
        return "registro-exitoso";
    }
}