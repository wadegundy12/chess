package server.handlers;

import model.GameData;

import java.util.Collection;

public record GamesListRecord(Collection<GameData> games) {
}
