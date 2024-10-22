package br.com.alura.screenmatch.repository;

import br.com.alura.screenmatch.model.Categoria;
import br.com.alura.screenmatch.model.Serie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface SerieRepository extends JpaRepository<Serie, Long> {
    Optional <Serie> findByTituloContainingIgnoreCase(String nomeSerie);

    List<Serie> findByAtoresContainingIgnoreCaseAndAvaliacaoGreaterThanEqual(String nomeAtor, Double avaliacao);

    List<Serie> findTop5ByOrderByAvaliacaoDesc();

    List<Serie> findByGenero(Categoria categoria);

    List<Serie> findByTotalTemporadasLessThanEqualAndAvaliacaoGreaterThanEqual(int totalTemporadas, double totalAvaliacao);

//    @Query("select * from series WHERE series.total_temporadas <= 5 AND series.avaliacao >= 7.5")  --> Query nativa
//    List<Serie> seriesPorTemporadaEAvaliacao();

    @Query("select s from Serie s WHERE s.totalTemporadas <= :totalTemporadas AND s.avaliacao >= :avaliacao") // Query atraves da JPQL
    List<Serie> seriesPorTemporadaEAvaliacao(int totalTemporadas, double avaliacao);
}
