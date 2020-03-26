package guice;

import com.google.inject.AbstractModule;
import commoninterfaces.CommonInterface;
import someimplementation.CommonInterfaceServiceOneImpl;

public class Guice extends AbstractModule {
    protected void configure() {

        bind(CommonInterface.class).to(CommonInterfaceServiceOneImpl.class);

    }
}
