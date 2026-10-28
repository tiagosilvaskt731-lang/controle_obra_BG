package com.senac.controle_obra;

public class FaseObra {
    private String nome;
    private int atividadesTotais;
    private int atividadesExecutadas;
    private int atividadesRefazer; // Nova variável para rastrear o retrabalho

    public FaseObra(String nome, int atividadesTotais, int atividadesExecutadas) {
        this.nome = nome;
        this.atividadesTotais = atividadesTotais;
        this.atividadesExecutadas = atividadesExecutadas;
        this.atividadesRefazer = 0; // Começa sem retrabalho
    }

    public int getRestante() {
        int restante = atividadesTotais - atividadesExecutadas;
        return restante < 0 ? 0 : restante;
    }

    public double getPorcentagem() {
        if (atividadesTotais == 0) return 0;
        return ((double) atividadesExecutadas / atividadesTotais) * 100;
    }

    // Getters e Setters
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public int getAtividadesTotais() { return atividadesTotais; }
    public void setAtividadesTotais(int atividadesTotais) { this.atividadesTotais = atividadesTotais; }
    public int getAtividadesExecutadas() { return atividadesExecutadas; }
    public void setAtividadesExecutadas(int atividadesExecutadas) { this.atividadesExecutadas = atividadesExecutadas; }
    public int getAtividadesRefazer() { return atividadesRefazer; }
    public void setAtividadesRefazer(int atividadesRefazer) { this.atividadesRefazer = atividadesRefazer; }
}