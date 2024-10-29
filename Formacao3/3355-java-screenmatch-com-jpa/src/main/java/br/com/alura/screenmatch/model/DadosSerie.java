package br.com.alura.screenmatch.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record DadosSerie(
                        @JsonAlias("Title") String titulo,
                         @JsonAlias("totalSeasons") Integer totalTemporadas,
                         @JsonAlias("Genre") String genero,
                         @JsonAlias("imdbRating") String avaliacao,
                         @JsonAlias("Actors") String atores,
                         @JsonAlias("Plot") String sinopse,
                         @JsonAlias("Awards") String premio,
                         @JsonAlias("Runtime") String duracao,
                         @JsonAlias("Released") String lancamento,
                         @JsonAlias("Country") String pais,
                         @JsonAlias("Ratings") List<Rating> votacoes,
                        @JsonAlias("Poster" ) String posterUrl
        ){
}
