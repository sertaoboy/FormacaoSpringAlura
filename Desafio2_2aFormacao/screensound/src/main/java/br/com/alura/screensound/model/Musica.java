package br.com.alura.screensound.model;

import jakarta.persistence.*;

@Entity
@Table(name = "musicas")
public class Musica {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String titulo;
    @ManyToOne
    private Artista artista;

    public Musica(String nomemusicaInserida) {
        this.titulo = nomemusicaInserida;
    }
    public Musica(){

    }

    @Override
    public String toString() {
        return "Musica:" +
                ", titulo='" + titulo + '\'' +
                ", artista=" + artista.getNome(); //correcao de recursao!
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public Artista getArtista() {
        return artista;
    }

    public void setArtista(Artista artista) {
        this.artista = artista;
    }
}
