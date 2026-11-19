package com.mastercash.mastercash.data;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

@Database(entities = {Movimentacao.class, Categoria.class}, version = 1)
@TypeConverters({Converters.class})
public abstract class AppDatabase extends RoomDatabase {
    public abstract MovimentacaoDao movimentacaoDao();
    public abstract CategoriaDao categoriaDao();
}
