package br.com.alura.screenmatch;

import br.com.alura.screenmatch.model.DadosEpisodio;
import br.com.alura.screenmatch.model.DadosSerie;
import br.com.alura.screenmatch.model.DadosTemporada;
import br.com.alura.screenmatch.service.ConsumoApi;
import br.com.alura.screenmatch.service.ConverteDados;
import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
public class ScreenmatchApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(ScreenmatchApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		Dotenv dotenv = Dotenv.load();
		String apiKey = dotenv.get("API_KEY");

		var consumoApi = new ConsumoApi();
		var json = consumoApi.obterDados("https://www.omdbapi.com/?t=the+witcher&apikey="+apiKey);
		System.out.println(json);

		ConverteDados conversor = new ConverteDados();

		DadosSerie dadosSerie = conversor.obterDados(json, DadosSerie.class);
		//System.out.println(dadosSerie);

		json = consumoApi.obterDados("https://www.omdbapi.com/?t=the+witcher&season=2&episode=3&apikey="+apiKey);
		DadosEpisodio dadosEpisodio = conversor.obterDados(json, DadosEpisodio.class);
		//System.out.println(dadosEpisodio);


		List<DadosTemporada> dadosTemporadas = new ArrayList<>();

		for(int i = 1; i<=dadosSerie.totalTemporadas(); i++) {
			json = consumoApi.obterDados("https://www.omdbapi.com/?t=the+witcher&season="+i+"&apikey="+apiKey);
			DadosTemporada dadosTemporada = conversor.obterDados(json,DadosTemporada.class);
			dadosTemporadas.add(dadosTemporada);
		}

		for(DadosTemporada d : dadosTemporadas) {
			System.out.println(d);
		}








	}
}
