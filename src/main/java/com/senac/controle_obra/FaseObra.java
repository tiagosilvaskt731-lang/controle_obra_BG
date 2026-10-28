package com.senac.controle_obra;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class FaseObra {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;
    private int activitiesTotais;
    private int activitiesExecutadas;
    private int activitiesRefazer;
    private String usuario; // NOVO: Guarda o nome do proprietário desta linha

    public FaseObra() {}

    // Construtor atualizado recebendo o dono da tarefa
    public FaseObra(String nome, int activitiesTotais, int activitiesExecutadas, String usuario) {
        this.nome = nome;
        this.activitiesTotais = activitiesTotais;
        this.activitiesExecutadas = activitiesExecutadas;
        this.activitiesRefazer = 0;
        this.usuario = usuario;
    }

    public int getRestante() {
        int restante = activitiesTotais - activitiesExecutadas;
        return restante < 0 ? 0 : restante;
    }

    public double getPorcentagem() {
        if (activitiesTotais == 0) return 0;
        return ((double) activitiesExecutadas / activitiesTotais) * 100;
    }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public int getActivitiesTotais() { return activitiesTotais; }
    public void setActivitiesTotais(int activitiesTotais) { this.activitiesTotais = activitiesTotais; }
    public int getActivitiesExecutadas() { return activitiesExecutadas; }
    public void setActivitiesExecutadas(int activitiesExecutadas) { this.activitiesExecutadas = activitiesExecutadas; }
    public int getActivitiesRefazer() { return activitiesRefazer; }
    public void setActivitiesRefazer(int activitiesRefazer) { this.activitiesRefazer = activitiesRefazer; }
    public String getUsuario() { return usuario; }
    public void setUsuario(String usuario) { this.usuario = usuario; }
}