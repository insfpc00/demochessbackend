package es.fporto.demo.minichess.loader;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import es.fporto.demo.minichess.model.OpeningTree;
import es.fporto.demo.minichess.repository.OpeningTreeRepository;

@Component
public class OpeningsLoader {

	@Value("${es.fporto.demo.minichess.openings.file}")
	private String openingsFile;

	@Autowired
	private OpeningTreeRepository openingTreeRepository;

	public void loadOpenings() {

		ObjectMapper mapper = new ObjectMapper();
		TypeReference<List<Opening>> typeReference = new TypeReference<List<Opening>>() {
		};
		InputStream inputStream = TypeReference.class.getResourceAsStream(openingsFile);
		try {
			List<Opening> openings = mapper.readValue(inputStream, typeReference);
			final Map<String[], Opening> openingsMap = openings.stream()
					.collect(Collectors.toMap(o -> o.getMoves().split(" "), o -> o));

			final OpeningTree openingRoot = new OpeningTree("root", null, "", "");
			openingsMap.keySet().stream().sorted((a, b) -> a.length - b.length).forEach(o -> {
				int i = 0;
				OpeningTree openingTree = openingRoot;
				while (openingTree.getChildren().containsKey(o[i])) {
					openingTree = openingTree.getChildren().get(o[i]);
					i++;
				}
				OpeningTree newTree = new OpeningTree(openingsMap.get(o).getMoves(), openingTree, o[i],
						openingsMap.get(o).getName());
				openingTree.getChildren().put(o[i], newTree);
			});
			openingRoot.getChildren().values().forEach(o -> o.setParent(null));
			openingTreeRepository.saveAll(openingRoot.getChildren().values());

		} catch (IOException e) {
			System.out.println("Unable to save openings: " + e.getMessage());
		}
	}
}
