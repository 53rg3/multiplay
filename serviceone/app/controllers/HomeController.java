package controllers;

import models.SomePojo;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import utils.SomeUtils;

public class HomeController extends Controller {

    public Result index() {
        final SomePojo somePojo = SomeUtils.createSomePojo("Bob");
        return ok(Json.toJson(somePojo));
    }

}
