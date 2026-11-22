package com.mastercash.mastercash.data;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import java.util.List;

@Dao
public interface MovimentacaoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(Movimentacao movimentacao);

    @Update
    int update(Movimentacao movimentacao);

    @Delete
    int delete(Movimentacao movimentacao);

    @Query("DELETE FROM movimentacoes WHERE id = :id")
    void deleteById(int id);

    @Query("SELECT * FROM movimentacoes ORDER BY data DESC, id DESC")
    List<Movimentacao> getAll();

    @Query("SELECT * FROM movimentacoes WHERE descricao LIKE :descricao AND (:categoria = '' OR categoria = :categoria) ORDER BY data DESC, id DESC")
    List<Movimentacao> getFiltered(String descricao, String categoria);

    @Query("SELECT COUNT(*) FROM movimentacoes WHERE tipo = 'entrada'")
    int countEntradas();

    @Query("SELECT SUM(valor) FROM movimentacoes WHERE tipo = 'entrada'")
    Double sumEntradas();

    @Query("SELECT COUNT(*) FROM movimentacoes WHERE tipo = 'saida'")
    int countSaidas();

    @Query("SELECT SUM(valor) FROM movimentacoes WHERE tipo = 'saida'")
    Double sumSaidas();

    @Query("SELECT categoria FROM movimentacoes WHERE tipo = 'saida' GROUP BY categoria ORDER BY SUM(valor) DESC LIMIT 1")
    String getCategoriaMaiorGasto();
}
