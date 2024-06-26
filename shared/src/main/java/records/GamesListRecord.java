package records;

import model.GameData;

import java.util.Collection;

public record GamesListRecord(Collection<GameData> games, String errorMessage) {
}
