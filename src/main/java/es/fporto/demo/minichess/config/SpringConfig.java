package es.fporto.demo.minichess.config;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.aspectj.EnableSpringConfigured;

import es.fporto.demo.minichess.player.UCIEngine.Difficulty;
import es.fporto.demo.minichess.uciclient.UCIClient;
import es.fporto.demo.minichess.uciclient.UCIConfigOption;
import es.fporto.demo.minichess.uciclient.exception.UCIClientException;
import es.fporto.demo.minichess.uciclient.stockfish.StockFishClient;
import es.fporto.demo.minichess.uciclient.stockfish.StockFishVariant;

@Configuration
@EnableSpringConfigured

public class SpringConfig {

	@Value("${es.fporto.demo.minichess.analysisengine.pool.size}")
	private int analysisEnginesPoolSize;

	@Value("${es.fporto.demo.minichess.stockfish.path}")
	private String pathToStockFish;

	@Value("${es.fporto.demo.minichess.stockfish.extension}")
	private String stockfishExtension;

	@Value("${es.fporto.demo.minichess.stockfish.skilllevel.hard}")
	private int skillLevelHard;

	@Value("${es.fporto.demo.minichess.stockfish.skilllevel.medium}")
	private int skillLevelMedium;

	@Value("${es.fporto.demo.minichess.stockfish.skilllevel.easy}")
	private int skillLevelEasy;

	@Value("${es.fporto.demo.minichess.stockfish.variant}")
	private String variant;

	@Bean
	@Scope(BeanDefinition.SCOPE_PROTOTYPE)
	@Lazy
	public UCIClient uciClient(Difficulty level) {
		switch (level) {
		case HARD:
			return new StockFishClient(StockFishVariant.valueOf(variant), Optional.empty(), Optional.empty(),
					pathToStockFish, stockfishExtension,
					new UCIConfigOption[] { UCIConfigOption.Skill_Level.setValue(skillLevelHard) });
		case MEDIUM:
			return new StockFishClient(StockFishVariant.valueOf(variant), Optional.empty(), Optional.empty(),
					pathToStockFish, stockfishExtension,
					new UCIConfigOption[] { UCIConfigOption.Skill_Level.setValue(skillLevelMedium) });
		case EASY:
		default:
			return new StockFishClient(StockFishVariant.valueOf(variant), Optional.of(0l), Optional.empty(),
					pathToStockFish, stockfishExtension,
					new UCIConfigOption[] { UCIConfigOption.Skill_Level.setValue(skillLevelEasy) });
		}

	}

	@Bean
	public BlockingQueue<UCIClient> analysisClients() {
		BlockingQueue<UCIClient> analysisClientsQueue = new ArrayBlockingQueue<UCIClient>(analysisEnginesPoolSize);

		for (int i = 0; i < analysisEnginesPoolSize; i++) {
			UCIClient newClient = new StockFishClient(StockFishVariant.valueOf(variant), Optional.empty(),
					Optional.empty(), pathToStockFish, stockfishExtension,
					new UCIConfigOption[] { UCIConfigOption.Skill_Level.setValue(20l) });
			try {
				newClient.connect();
				newClient.newGame();
				analysisClientsQueue.add(newClient);
			} catch (UCIClientException e) {
				e.printStackTrace();
			}

		}

		return analysisClientsQueue;

	}

	@Bean
	public UCIClient uciClient() {
		return null; // to be fixed
	}

	@Bean
	@Lazy
	public Map<String, UCIClient> uciClients() {
		return new HashMap<String, UCIClient>();
	}

}
