package com.mastercash.mastercash.ui.viewmodel;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import dagger.hilt.android.lifecycle.HiltViewModel;
import com.mastercash.mastercash.data.repository.MovimentacaoRepository;
import com.mastercash.mastercash.domain.MovimentacaoDomain;
import com.mastercash.mastercash.domain.CategoriaDomain;
import java.util.List;
import javax.inject.Inject;

@HiltViewModel
public class MovimentacaoViewModel extends ViewModel {

    private final MovimentacaoRepository repository;

    private final MutableLiveData<List<MovimentacaoDomain>> _movimentacoes = new MutableLiveData<>();
    public LiveData<List<MovimentacaoDomain>> movimentacoes = _movimentacoes;

    private final MutableLiveData<List<CategoriaDomain>> _categorias = new MutableLiveData<>();
    public LiveData<List<CategoriaDomain>> categorias = _categorias;

    private final MutableLiveData<Double> _totalEntradas = new MutableLiveData<>(0.0);
    public LiveData<Double> totalEntradas = _totalEntradas;

    private final MutableLiveData<Double> _totalSaidas = new MutableLiveData<>(0.0);
    public LiveData<Double> totalSaidas = _totalSaidas;

    private final MutableLiveData<Double> _saldo = new MutableLiveData<>(0.0);
    public LiveData<Double> saldo = _saldo;

    private final MutableLiveData<String> _categoriaMaiorGasto = new MutableLiveData<>("");
    public LiveData<String> categoriaMaiorGasto = _categoriaMaiorGasto;

    private final MutableLiveData<String> _toastMessage = new MutableLiveData<>("");
    public LiveData<String> toastMessage = _toastMessage;

    @Inject
    public MovimentacaoViewModel(MovimentacaoRepository repository) {
        this.repository = repository;
        loadData();
    }

    public void loadData() {
        List<MovimentacaoDomain> movs = repository.getAllMovimentacoes();
        _movimentacoes.setValue(movs);

        List<CategoriaDomain> cats = repository.getAllCategorias();
        _categorias.setValue(cats);

        _totalEntradas.setValue(repository.getTotalEntradas());
        _totalSaidas.setValue(repository.getTotalSaidas());
        _saldo.setValue(repository.getSaldo());
        _categoriaMaiorGasto.setValue(repository.getCategoriaMaiorGasto());
    }

    public List<MovimentacaoDomain> getFiltered(String descricao, String categoria) {
        return repository.getFilteredMovimentacoes(descricao, categoria);
    }

    public void addMovimentacao(String descricao, double valor, String tipo, String categoria, String data) {
        MovimentacaoDomain m = new MovimentacaoDomain(descricao, valor, tipo, categoria, data);
        long id = repository.insertMovimentacao(m);
        if (id > 0) {
            _toastMessage.setValue("Adicionado!");
            loadData();
        } else {
            _toastMessage.setValue("Erro ao adicionar.");
        }
    }

    public void updateMovimentacao(MovimentacaoDomain m) {
        int result = repository.updateMovimentacao(m);
        if (result > 0) {
            _toastMessage.setValue("Atualizado!");
            loadData();
        } else {
            _toastMessage.setValue("Erro ao atualizar.");
        }
    }

    public void deleteMovimentacao(int id) {
        repository.deleteMovimentacao(id);
        _toastMessage.setValue("Removido!");
        loadData();
    }

    public void addCategoria(String nome) {
        if (repository.categoriaExists(nome)) {
            _toastMessage.setValue("Categoria já existe.");
            return;
        }
        CategoriaDomain c = new CategoriaDomain(nome);
        long id = repository.insertCategoria(c);
        if (id > 0) {
            _toastMessage.setValue("Categoria adicionada!");
            loadData();
        } else {
            _toastMessage.setValue("Erro ao adicionar categoria.");
        }
    }

    public void showToast(String message) {
        _toastMessage.setValue(message);
    }
}
