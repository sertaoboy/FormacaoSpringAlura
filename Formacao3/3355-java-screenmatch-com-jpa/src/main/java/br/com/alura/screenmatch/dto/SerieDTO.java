package br.com.alura.screenmatch.dto;

import br.com.alura.screenmatch.model.Categoria;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

import java.time.LocalDate;
import java.util.List;

public record SerieDTO( Long id,
                        String titulo,
                        Integer totalTemporadas,
                        Double avaliacao,
                        Categoria genero,
                        String atores,
                        String posterUrl,
                        String sinpose,
                        String premio,
                        LocalDate lancamento,
                        LocalDate duracao,
                        List<String> votacoes) {
}
