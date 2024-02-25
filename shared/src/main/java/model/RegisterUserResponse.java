package model;

import java.util.UUID;

public record RegisterUserResponse(String username, UUID authToken) {
}
