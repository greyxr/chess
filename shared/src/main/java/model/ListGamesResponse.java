package model;

import java.util.ArrayList;
import java.util.Collection;

public record ListGamesResponse(Collection<GameData> games) {
}
