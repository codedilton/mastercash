package com.mastercash.mastercash.data;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {Movimentacao.class, Categoria.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract MovimentacaoDao movimentacaoDao();
    public abstract CategoriaDao categoriaDao();
}
