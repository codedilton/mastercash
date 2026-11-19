package com.mastercash.mastercash.domain;

public class MovimentacaoDomain {
    public int id;
    public String descricao;
    public double valor;
    public String tipo; // "entrada" or "saida"
    public String categoria;
    public String data; // yyyy-MM-dd

    public MovimentacaoDomain(int id, String descricao, double valor, String tipo, String categoria, String data) {
        this.id = id;
        this.descricao = descricao;
        this.valor = valor;
        this.tipo = tipo;
        this.categoria = categoria;
        this.data = data;
    }

    public MovimentacaoDomain(String descricao, double valor, String tipo, String categoria, String data) {
        this(-1, descricao, valor, tipo, categoria, data);
    }
}
