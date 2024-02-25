package model;

import java.util.UUID;

public record AuthTokenResponse(String username, UUID authToken) {
}
