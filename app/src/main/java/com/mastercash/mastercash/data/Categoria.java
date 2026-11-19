package com.mastercash.mastercash.data;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "categorias")
public class Categoria {

    @PrimaryKey(autoGenerate = true)
    public int id;

    public String nome;

    public Categoria(String nome) {
        this.nome = nome;
    }
}
