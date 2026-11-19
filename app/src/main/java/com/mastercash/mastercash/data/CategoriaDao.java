package com.mastercash.mastercash.data;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import java.util.List;

@Dao
public interface CategoriaDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(Categoria categoria);

    @Update
    int update(Categoria categoria);

    @Delete
    int delete(Categoria categoria);

    @Query("SELECT * FROM categorias ORDER BY nome ASC")
    List<Categoria> getAll();

    @Query("SELECT * FROM categorias WHERE nome = :nome")
    Categoria findByNome(String nome);

    @Query("DELETE FROM categorias WHERE id = :id")
    void deleteById(int id);
}
