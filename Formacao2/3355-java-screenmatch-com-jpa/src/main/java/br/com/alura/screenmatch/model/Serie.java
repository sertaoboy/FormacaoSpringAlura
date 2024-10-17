package br.com.alura.screenmatch.model;

import java.time.LocalDate;
import java.util.List;
import java.util.OptionalDouble;

public class Serie {
    private String titulo;
    private Integer totalTemporadas;
    private Double avaliacao;
    private Categoria genero;
    private String atores;
    private String posterUrl;
    private String sinpose;
    private String premio;
    private LocalDate lancamento;
    private LocalDate duracao;
    private List<String> votacoes;

    public Serie(DadosSerie dadosSerie){
        this.titulo = dadosSerie.titulo();
        this.totalTemporadas = dadosSerie.totalTemporadas();
        this.avaliacao= OptionalDouble.of(Double.valueOf(dadosSerie.avaliacao())).orElse(0);
        this.genero=Categoria.fromString(dadosSerie.genero().split(",")[0].trim());
        this.atores = dadosSerie.atores();
        this.posterUrl = dadosSerie.posterUrl();
        this.sinpose = dadosSerie.sinopse();

    }

    @Override
    public String toString() {
        return
                "Genero: "+ genero +
                "titulo='" + titulo + '\'' +
                ", totalTemporadas=" + totalTemporadas +
                ", avaliacao=" + avaliacao +
                ", genero=" + genero +
                ", atores='" + atores + '\'' +
                ", posterUrl='" + posterUrl + '\'' +
                ", sinpose='" + sinpose + '\'' +
                ", premio='" + premio + '\'' +
                ", lancamento=" + lancamento +
                ", duracao=" + duracao +
                ", votacoes=" + votacoes;
    }

    public Serie(){

    }
    public Serie(String titulo, Integer totalTemporadas, Double avaliacao, Categoria genero, String atores, String posterUrl, String sinpose, String premio, LocalDate lancamento, LocalDate duracao, List<String> votacoes) {
        this.titulo = titulo;
        this.totalTemporadas = totalTemporadas;
        this.avaliacao = avaliacao;
        this.genero = genero;
        this.atores = atores;
        this.posterUrl = posterUrl;
        this.sinpose = sinpose;
        this.premio = premio;
        this.lancamento = lancamento;
        this.duracao = duracao;
        this.votacoes = votacoes;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public Integer getTotalTemporadas() {
        return totalTemporadas;
    }

    public void setTotalTemporadas(Integer totalTemporadas) {
        this.totalTemporadas = totalTemporadas;
    }

    public Double getAvaliacao() {
        return avaliacao;
    }

    public void setAvaliacao(Double avaliacao) {
        this.avaliacao = avaliacao;
    }

    public Categoria getGenero() {
        return genero;
    }

    public void setGenero(Categoria genero) {
        this.genero = genero;
    }

    public String getAtores() {
        return atores;
    }

    public void setAtores(String atores) {
        this.atores = atores;
    }

    public String getPosterUrl() {
        return posterUrl;
    }

    public void setPosterUrl(String posterUrl) {
        this.posterUrl = posterUrl;
    }

    public String getSinpose() {
        return sinpose;
    }

    public void setSinpose(String sinpose) {
        this.sinpose = sinpose;
    }

    public String getPremio() {
        return premio;
    }

    public void setPremio(String premio) {
        this.premio = premio;
    }

    public LocalDate getLancamento() {
        return lancamento;
    }

    public void setLancamento(LocalDate lancamento) {
        this.lancamento = lancamento;
    }

    public LocalDate getDuracao() {
        return duracao;
    }

    public void setDuracao(LocalDate duracao) {
        this.duracao = duracao;
    }

    public List<String> getVotacoes() {
        return votacoes;
    }

    public void setVotacoes(List<String> votacoes) {
        this.votacoes = votacoes;
    }
}
