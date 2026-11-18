package com.javali.controllers.web;

import com.javali.dtos.PreferenciasRequest;
import com.javali.exceptions.UsuarioNoEncontradoException;
import com.javali.models.NivelContraste;
import com.javali.models.TamanoLetra;
import com.javali.models.Tipografia;
import com.javali.models.Usuario;
import com.javali.repositories.UsuarioRepository;
import com.javali.services.PreferenciasService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class PreferenciasWebController {

    private final PreferenciasService preferenciasService;
    private final UsuarioRepository usuarioRepository;

    public PreferenciasWebController(
            PreferenciasService preferenciasService,
            UsuarioRepository usuarioRepository) {
        this.preferenciasService = preferenciasService;
        this.usuarioRepository = usuarioRepository;
    }

    //Muestra el formulario de ajustes de accesibilidad y racha.
    @GetMapping("/web/preferencias")
    public String mostrar(
            Authentication authentication,
            Model model) {
        Usuario usuario = buscarUsuario(authentication);
        model.addAttribute(
                "formulario",
                preferenciasService.obtener(usuario.getId())
        );
        model.addAttribute("tamanos", TamanoLetra.values());
        model.addAttribute("contrastes", NivelContraste.values());
        model.addAttribute("tipografias", Tipografia.values());
        return "preferencias";
    }

    //Guarda los ajustes y vuelve al formulario con un aviso.
    @PostMapping("/web/preferencias")
    public String guardar(
            @RequestParam("tamanoLetra") TamanoLetra tamanoLetra,
            @RequestParam("contraste") NivelContraste contraste,
            @RequestParam("tipografia") Tipografia tipografia,
            @RequestParam(
                    name = "mostrarRacha",
                    defaultValue = "false") boolean mostrarRacha,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {
        Usuario usuario = buscarUsuario(authentication);
        preferenciasService.guardar(
                usuario.getId(),
                new PreferenciasRequest(
                        tamanoLetra,
                        contraste,
                        tipografia,
                        mostrarRacha
                )
        );
        redirectAttributes.addFlashAttribute("guardado", true);
        return "redirect:/web/preferencias";
    }

    private Usuario buscarUsuario(Authentication authentication) {
        String email = authentication
                .getName()
                .trim()
                .toLowerCase();
        return usuarioRepository
                .findByEmail(email)
                .orElseThrow(
                        () -> new UsuarioNoEncontradoException(0L)
                );
    }
}
