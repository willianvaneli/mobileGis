package com.example.mobilegis.DAOs;

import java.util.List;

public interface IDAO<T> {

    public void inserir(T t) throws Exception;
    public void editar(T t) throws Exception;
    public void deletar(T t) throws Exception;
    public T buscar(Long id) throws Exception;
    public List<T> buscarTodos() throws Exception;

}
