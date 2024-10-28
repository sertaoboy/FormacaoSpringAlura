package br.com.alura.screensound.app;

import br.com.alura.screensound.model.Artista;
import br.com.alura.screensound.model.Musica;
import br.com.alura.screensound.model.TipoArtista;
import br.com.alura.screensound.repository.ArtistaRepository;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class App {
    private final ArtistaRepository repositorio;
    private Scanner leitura = new Scanner(System.in);

    public App(ArtistaRepository repositorio) {
        this.repositorio = repositorio;
    }

    public void exibirMenu() {
        var opcao = -1;
        while (opcao != 9) {
            var menu = """
                    *** Screen Sound Músicas ***                    
                    
                    1- Cadastrar artistas
                    2- Cadastrar músicas
                    3- Listar músicas
                    4- Buscar músicas por artistas
                    5- Pesquisar dados sobre um artista (n/a)
                    
                    9 - Sair
                    """;
            System.out.println(menu);
            opcao = leitura.nextInt();
            leitura.nextLine();
            switch (opcao) {
                case 1:
                    cadastrarArtistas();
                    break;
                case 2:
                    cadastrarMusicas();
                    break;
                case 3:
                    listarMusicas();
                    break;
                case 4:
                    buscarMusicasPorArtista();
                    break;
                case 5:
                    pesquisarDadosDoArtista();
                    break;
                case 9:
                    System.out.println("Encerrando a aplicação!");
                    break;
                default:
                    System.out.println("Opção inválida!");
            }
        }
    }

    private void pesquisarDadosDoArtista() {
    }

    private void buscarMusicasPorArtista() {
        System.out.println("De qual Artista deseja buscar as musicas?");
        String nomeArtista = leitura.nextLine();
        List<Musica>musicas = repositorio.buscaMusicasPorArtista(nomeArtista);
        musicas.forEach(System.out::println);
    }

    private void listarMusicas() {
        List<Artista> artistas = repositorio.findAll();
        artistas.forEach(a -> a.getMusicas().forEach(System.out::println));
    }

    private void cadastrarMusicas() {
        System.out.println("Cadastar musicas de que Artista?");
        var nomeInserido = leitura.nextLine();
        Optional<Artista>artista =repositorio.findByNomeContainingIgnoreCase(nomeInserido);
        if(artista.isPresent()) {
            System.out.println("Informe o titulo da musica");
            var nomemusicaInserida = leitura.nextLine();
            Musica musica = new Musica(nomemusicaInserida);
            System.out.println("Salvando artista...");
            musica.setArtista(artista.get());
            System.out.println("Salvando musica...");
            artista.get().getMusicas().add(musica);
            repositorio.save(artista.get());
            System.out.println("Musica salva!");
        }else {
            System.out.println("Artista nao encontrado.");
        }
    }

    private void cadastrarArtistas() {
        String cadastrarNovo = "S";
        while (cadastrarNovo.equalsIgnoreCase("s")) {
            System.out.println("Informe o nome do artista:");
            String nomeInserido = leitura.nextLine();
            System.out.println("Informe o tipo desse artista (solo,dupla ou banda)");
            var tipo = leitura.nextLine();
            TipoArtista tipoArtista = TipoArtista.valueOf(tipo.toUpperCase()); //conversao de string para enum(TipoArtista)
            Artista artista = new Artista(nomeInserido,tipoArtista);
            repositorio.save(artista);
            System.out.println("Deseja cadastar outro artista? (S/N)");
            cadastrarNovo=leitura.nextLine();
        }
    }
}

