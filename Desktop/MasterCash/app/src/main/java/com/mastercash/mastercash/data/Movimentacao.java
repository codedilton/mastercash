package com.mastercash.mastercash.data;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.ColumnInfo;

@Entity(tableName = "movimentacoes")
public class Movimentacao {

    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "descricao")
    public String descricao;

    @ColumnInfo(name = "valor")
    public double valor;

    @ColumnInfo(name = "tipo")
    public String tipo; // "entrada" ou "saida"

    @ColumnInfo(name = "categoria")
    public String categoria;

    @ColumnInfo(name = "data")
    public String data; // formato yyyy-MM-dd

    public Movimentacao(String descricao, double valor, String tipo, String categoria, String data) {
        this.descricao = descricao;
        this.valor = valor;
        this.tipo = tipo;
        this.categoria = categoria;
        this.data = data;
    }
}
