package handlers;

import exceptions.BadRequestException;
import model.ErrorResponse;
import spark.*;
import com.google.gson.Gson;

public class ExceptionHandler {
    public String handleServerError(Exception e, Response res) {
        res.status(500);
        return new Gson().toJson(new ErrorResponse(e.getMessage()));
    }

    public String handleRequestError(BadRequestException e, Response res) {
        res.status(e.StatusCode());
        return new Gson().toJson(new ErrorResponse(e.Message()));
    }
}
