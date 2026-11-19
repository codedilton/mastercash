package com.mastercash.mastercash.data.repository;

import android.app.Application;
import android.os.AsyncTask;
import com.google.dagger.hilt.android.qualifiers.ApplicationContext;
import com.google.dagger.hilt.android.installIn;
import com.google.dagger.hilt.android.components.ApplicationComponent;
import com.mastercash.mastercash.data.MovimentacaoDao;
import com.mastercash.mastercash.data.CategoriaDao;
import com.mastercash.mastercash.data.Movimentacao;
import com.mastercash.mastercash.data.Categoria;
import com.mastercash.mastercash.domain.MovimentacaoDomain;
import com.mastercash.mastercash.domain.CategoriaDomain;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class MovimentacaoRepository {

    private final MovimentacaoDao movimentacaoDao;
    private final CategoriaDao categoriaDao;

    @Inject
    public MovimentacaoRepository(@ApplicationContext Application application,
                                  MovimentacaoDao movimentacaoDao,
                                  CategoriaDao categoriaDao) {
        this.movimentacaoDao = movimentacaoDao;
        this.categoriaDao = categoriaDao;
    }

    // Movimentacoes
    public List<MovimentacaoDomain> getAllMovimentacoes() {
        List<Movimentacao> entities = movimentacaoDao.getAll();
        List<MovimentacaoDomain> domains = new ArrayList<>();
        for (Movimentacao m : entities) {
            domains.add(new MovimentacaoDomain(m.id, m.descricao, m.valor, m.tipo, m.categoria, m.data));
        }
        return domains;
    }

    public List<MovimentacaoDomain> getFilteredMovimentacoes(String descricao, String categoria) {
        String descLike = "%" + descricao + "%";
        List<Movimentacao> entities = movimentacaoDao.getFiltered(descLike, categoria);
        List<MovimentacaoDomain> domains = new ArrayList<>();
        for (Movimentacao m : entities) {
            domains.add(new MovimentacaoDomain(m.id, m.descricao, m.valor, m.tipo, m.categoria, m.data));
        }
        return domains;
    }

    public long insertMovimentacao(MovimentacaoDomain domain) {
        Movimentacao m = new Movimentacao(domain.descricao, domain.valor, domain.tipo, domain.categoria, domain.data);
        return movimentacaoDao.insert(m);
    }

    public int updateMovimentacao(MovimentacaoDomain domain) {
        Movimentacao m = new Movimentacao(domain.descricao, domain.valor, domain.tipo, domain.categoria, domain.data);
        m.id = domain.id;
        return movimentacaoDao.update(m);
    }

    public void deleteMovimentacao(int id) {
        movimentacaoDao.deleteById(id);
    }

    public double getTotalEntradas() {
        Double sum = movimentacaoDao.sumEntradas();
        return sum != null ? sum : 0.0;
    }

    public double getTotalSaidas() {
        Double sum = movimentacaoDao.sumSaidas();
        return sum != null ? sum : 0.0;
    }

    public double getSaldo() {
        return getTotalEntradas() - getTotalSaidas();
    }

    public String getCategoriaMaiorGasto() {
        return movimentacaoDao.getCategoriaMaiorGasto();
    }

    // Categorias
    public List<CategoriaDomain> getAllCategorias() {
        List<Categoria> entities = categoriaDao.getAll();
        List<CategoriaDomain> domains = new ArrayList<>();
        for (Categoria c : entities) {
            domains.add(new CategoriaDomain(c.id, c.nome));
        }
        return domains;
    }

    public long insertCategoria(CategoriaDomain domain) {
        Categoria c = new Categoria(domain.nome);
        return categoriaDao.insert(c);
    }

    public boolean categoriaExists(String nome) {
        return categoriaDao.findByNome(nome) != null;
    }
}
