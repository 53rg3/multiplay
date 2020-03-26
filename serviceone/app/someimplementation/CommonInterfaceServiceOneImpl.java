package someimplementation;

import commoninterfaces.CommonInterface;

public class CommonInterfaceServiceOneImpl implements CommonInterface {

    private final String name;

    public CommonInterfaceServiceOneImpl() {
        this.name = "CommonInterfaceServiceOneImpl, autowired by Guice - Service One";
    }

    @Override
    public String getName() {
        return this.name;
    }
}
