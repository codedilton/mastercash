package com.mastercash.mastercash.domain;

public class CategoriaDomain {
    public int id;
    public String nome;

    public CategoriaDomain(int id, String nome) {
        this.id = id;
        this.nome = nome;
    }

    public CategoriaDomain(String nome) {
        this(-1, nome);
    }
}
