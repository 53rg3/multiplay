package controllers;

import com.google.inject.Inject;
import commoninterfaces.CommonInterface;
import models.SomePojo;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import utils.SomeUtils;

public class ServiceOneController extends Controller {

    private final CommonInterface commonInterface;

    @Inject
    public ServiceOneController(final CommonInterface commonInterface) {
        this.commonInterface = commonInterface;
    }

    public Result index() {
        String name = this.commonInterface.getName();
        final SomePojo somePojo = SomeUtils.createSomePojo(name);
        return ok(Json.toJson(somePojo));
    }

}
