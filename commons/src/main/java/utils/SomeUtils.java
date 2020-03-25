package utils;

import models.SomePojo;

public class SomeUtils {

    public static SomePojo createSomePojo(final String name) {
        return new SomePojo(name);
    }

}
