package es.fporto.demo.minichess.uciclient.stockfish;

import java.util.Optional;

import es.fporto.demo.minichess.uciclient.UCIClient;
import es.fporto.demo.minichess.uciclient.UCIConfigOption;

public class StockFishClient extends UCIClient{

	private String pathToStockFish;
	
	
	public StockFishClient(StockFishVariant variant,Optional<Long> depth, Optional<Long> moveTime ,String path,String extension,UCIConfigOption ... options) {
		super(depth,moveTime,options);
		pathToStockFish=path + variant.getSuffix()+"."+extension;
	}

	@Override
	public String getEnginePath() {
		return pathToStockFish;
	}
	
}