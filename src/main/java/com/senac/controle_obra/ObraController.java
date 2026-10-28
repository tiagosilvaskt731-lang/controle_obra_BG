package com.senac.controle_obra;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Controller
public class ObraController {

    private static final List<FaseObra> fases = new ArrayList<>();
    private static final String ARQUIVO_DADOS = "dados_obra.csv";

    static {
        carregarDadosDoArquivo();
    }

    @GetMapping("/")
    public String index(Model model) {
        int totalAtividades = fases.stream().mapToInt(FaseObra::getAtividadesTotais).sum();
        int totalExecutadas = fases.stream().mapToInt(FaseObra::getAtividadesExecutadas).sum();
        int totalRetrabalho = fases.stream().mapToInt(FaseObra::getAtividadesRefazer).sum();
        double progressoGeral = totalAtividades == 0 ? 0 : ((double) totalExecutadas / totalAtividades) * 100;

        model.addAttribute("fases", fases);
        model.addAttribute("progressoGeral", String.format("%.1f", progressoGeral));
        model.addAttribute("totalAtividades", totalAtividades);
        model.addAttribute("totalExecutadas", totalExecutadas);
        model.addAttribute("totalRetrabalho", totalRetrabalho);

        return "index";
    }

    @PostMapping("/atualizar")
    public String atualizarProgressos(@RequestParam("index") int index, @RequestParam("executadas") int executadas) {
        if (index >= 0 && index < fases.size()) {
            FaseObra fase = fases.get(index);
            if (executadas <= fase.getAtividadesTotais() && executadas >= 0) {

                if (executadas < fase.getAtividadesExecutadas()) {
                    int diferencaPerda = fase.getAtividadesExecutadas() - executadas;
                    fase.setAtividadesRefazer(fase.getAtividadesRefazer() + diferencaPerda);
                }

                fase.setAtividadesExecutadas(executadas);
                salvarDadosNoArquivo();
            }
        }
        return "redirect:/";
    }

    @PostMapping("/editar-valores")
    public String editarValoresFase(@RequestParam("index") int index, @RequestParam("totais") int totais) {
        if (index >= 0 && index < fases.size() && totais > 0) {
            FaseObra fase = fases.get(index);
            fase.setAtividadesTotais(totais);
            if (fase.getAtividadesExecutadas() > totais) {
                fase.setAtividadesExecutadas(totais);
            }
            salvarDadosNoArquivo();
        }
        return "redirect:/";
    }

    @PostMapping("/editar-nome")
    public String editarNomeFase(@RequestParam("index") int index, @RequestParam("novoNome") String novoNome) {
        if (index >= 0 && index < fases.size() && novoNome != null && !novoNome.trim().isEmpty()) {
            fases.get(index).setNome(novoNome.trim());
            salvarDadosNoArquivo();
        }
        return "redirect:/";
    }

    @PostMapping("/adicionar")
    public String adicionarNovaFase(@RequestParam("nome") String nome, @RequestParam("totais") int totais) {
        if (nome != null && !nome.trim().isEmpty() && totais > 0) {
            fases.add(new FaseObra(nome.trim(), totais, 0));
            salvarDadosNoArquivo();
        }
        return "redirect:/";
    }

    @PostMapping("/deletar")
    public String deletarFase(@RequestParam("index") int index) {
        if (index >= 0 && index < fases.size()) {
            fases.remove(index);
            salvarDadosNoArquivo();
        }
        return "redirect:/";
    }

    @PostMapping("/zerar-retrabalho")
    public String zerarRetrabalhoFase(@RequestParam("index") int index) {
        if (index >= 0 && index < fases.size()) {
            fases.get(index).setAtividadesRefazer(0);
            salvarDadosNoArquivo();
        }
        return "redirect:/";
    }

    private static void salvarDadosNoArquivo() {
        try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(ARQUIVO_DADOS), StandardCharsets.UTF_8))) {
            for (FaseObra fase : fases) {
                bw.write(fase.getNome() + ";" + fase.getAtividadesTotais() + ";" +
                        fase.getAtividadesExecutadas() + ";" + fase.getAtividadesRefazer());
                bw.newLine();
            }
        } catch (IOException e) {
            System.err.println("Erro ao salvar dados: " + e.getMessage());
        }
    }

    private static void carregarDadosDoArquivo() {
        File arquivo = new File(ARQUIVO_DADOS);
        fases.clear();
        if (arquivo.exists()) {
            try (BufferedReader br = new BufferedReader(new InputStreamReader(
                    new FileInputStream(arquivo), StandardCharsets.UTF_8))) {
                String linha;
                while ((linha = br.readLine()) != null) {
                    String[] partes = linha.split(";");
                    if (partes.length >= 3) {
                        FaseObra f = new FaseObra(partes[0], Integer.parseInt(partes[1]), Integer.parseInt(partes[2]));
                        if (partes.length == 4) {
                            f.setAtividadesRefazer(Integer.parseInt(partes[3]));
                        }
                        fases.add(f);
                    }
                }
            } catch (IOException | NumberFormatException e) {
                carregarDadosPadrao();
                salvarDadosNoArquivo();
            }
        } else {
            carregarDadosPadrao();
            salvarDadosNoArquivo();
        }
    }

    private static void carregarDadosPadrao() {
        fases.add(new FaseObra("1. Fundação e Infraestrutura", 20, 0));
        fases.add(new FaseObra("2. Estrutura e Alvenaria", 35, 0));
        fases.add(new FaseObra("3. Cobertura (Telhado)", 15, 0));
        fases.add(new FaseObra("4. Instalações (Elétrica/Hidráulica)", 40, 0));
        fases.add(new FaseObra("5. Acabamentos e Pintura", 50, 0));
    }
}