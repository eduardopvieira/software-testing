package edu.ufersa.unidade1_tds.controller;

import edu.ufersa.unidade1_tds.service.DuendeService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class DuendeController {
    private final DuendeService duendeService;

    public DuendeController(DuendeService duendeService) {
        this.duendeService = duendeService;
    }

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("duendes", duendeService.getDuendes());
        model.addAttribute("numIteracoes", duendeService.getNumIteracoes());
        return "index";
    }

    @PostMapping("/iniciar")
    public String iniciarSimulacao(@RequestParam int numDuendes, @RequestParam int numIteracoes, Model model) {
        duendeService.iniciarSimulacao(numDuendes);
        duendeService.setNumIteracoes(numIteracoes);
        return "redirect:/";
    }

    @PostMapping("/iterar")
    public String executarIteracao(Model model) {
        duendeService.executarIteracao();
        return "redirect:/";
    }
}
