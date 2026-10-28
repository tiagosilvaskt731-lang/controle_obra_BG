package com.senac.controle_obra;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class ObraController {

    @Autowired
    private FaseObraRepository repository;

    // Método utilitário para descobrir quem é o usuário logado no momento
    private String getUsuarioLogado() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            return ((UserDetails) principal).getUsername();
        }
        return principal.toString();
    }

    @GetMapping("/")
    public String index(Model model) {
        String usuario = getUsuarioLogado(); // Descobre quem logou (ex: admin ou tiago)
        List<FaseObra> fases = repository.findByUsuario(usuario); // Busca APENAS as fases desse usuário

        // Se esse usuário específico nunca entrou antes, cria a planilha padrão para ele
        if (fases.isEmpty()) {
            repository.save(new FaseObra("1. Fundação e Infraestrutura", 20, 0, usuario));
            repository.save(new FaseObra("2. Estrutura e Alvenaria", 35, 0, usuario));
            repository.save(new FaseObra("3. Cobertura (Telhado)", 15, 0, usuario));
            repository.save(new FaseObra("4. Instalações (Elétrica/Hidráulica)", 40, 0, usuario));
            repository.save(new FaseObra("5. Acabamentos e Pintura", 50, 0, usuario));
            fases = repository.findByUsuario(usuario);
        }

        int totalAtividades = fases.stream().mapToInt(FaseObra::getActivitiesTotais).sum();
        int totalExecutadas = fases.stream().mapToInt(FaseObra::getActivitiesExecutadas).sum();
        int totalRetrabalho = fases.stream().mapToInt(FaseObra::getActivitiesRefazer).sum();
        double progressoGeral = totalAtividades == 0 ? 0 : ((double) totalExecutadas / totalAtividades) * 100;

        model.addAttribute("fases", fases);
        model.addAttribute("progressoGeral", String.format("%.1f", progressoGeral));
        model.addAttribute("totalAtividades", totalAtividades);
        model.addAttribute("totalExecutadas", totalExecutadas);
        model.addAttribute("totalRetrabalho", totalRetrabalho);
        model.addAttribute("username", usuario); // Envia o nome do usuário para a tela

        return "index";
    }

    @PostMapping("/atualizar")
    public String atualizarProgressos(@RequestParam("index") int idDoMenu, @RequestParam("executadas") int executadas) {
        List<FaseObra> fases = repository.findByUsuario(getUsuarioLogado());
        if (idDoMenu >= 0 && idDoMenu < fases.size()) {
            FaseObra fase = fases.get(idDoMenu);
            if (executadas <= fase.getActivitiesTotais() && executadas >= 0) {
                if (executadas < fase.getActivitiesExecutadas()) {
                    int diferencaPerda = fase.getActivitiesExecutadas() - executadas;
                    fase.setActivitiesRefazer(fase.getActivitiesRefazer() + diferencaPerda);
                }
                fase.setActivitiesExecutadas(executadas);
                repository.save(fase);
            }
        }
        return "redirect:/";
    }

    @PostMapping("/editar-valores")
    public String editarValoresFase(@RequestParam("index") int idDoMenu, @RequestParam("totais") int totais) {
        List<FaseObra> fases = repository.findByUsuario(getUsuarioLogado());
        if (idDoMenu >= 0 && idDoMenu < fases.size() && totais > 0) {
            FaseObra fase = fases.get(idDoMenu);
            fase.setActivitiesTotais(totais);
            if (fase.getActivitiesExecutadas() > totais) {
                fase.setActivitiesExecutadas(totais);
            }
            repository.save(fase);
        }
        return "redirect:/";
    }

    @PostMapping("/editar-nome")
    public String editarNomeFase(@RequestParam("index") int idDoMenu, @RequestParam("novoNome") String novoNome) {
        List<FaseObra> fases = repository.findByUsuario(getUsuarioLogado());
        if (idDoMenu >= 0 && idDoMenu < fases.size() && novoNome != null && !novoNome.trim().isEmpty()) {
            FaseObra fase = fases.get(idDoMenu);
            fase.setNome(novoNome.trim());
            repository.save(fase);
        }
        return "redirect:/";
    }

    @PostMapping("/adicionar")
    public String adicionarNovaFase(@RequestParam("nome") String nome, @RequestParam("totais") int totais) {
        if (nome != null && !nome.trim().isEmpty() && totais > 0) {
            repository.save(new FaseObra(nome.trim(), totais, 0, getUsuarioLogado()));
        }
        return "redirect:/";
    }

    @PostMapping("/deletar")
    public String deletarFase(@RequestParam("index") int idDoMenu) {
        List<FaseObra> fases = repository.findByUsuario(getUsuarioLogado());
        if (idDoMenu >= 0 && idDoMenu < fases.size()) {
            repository.delete(fases.get(idDoMenu));
        }
        return "redirect:/";
    }

    @PostMapping("/zerar-retrabalho")
    public String zerarRetrabalhoFase(@RequestParam("index") int idDoMenu) {
        List<FaseObra> fases = repository.findByUsuario(getUsuarioLogado());
        if (idDoMenu >= 0 && idDoMenu < fases.size()) {
            FaseObra fase = fases.get(idDoMenu);
            fase.setActivitiesRefazer(0);
            repository.save(fase);
        }
        return "redirect:/";
    }
}