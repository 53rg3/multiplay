package someimplementation;

import commoninterfaces.CommonInterface;

public class CommonInterfaceServiceTwoImpl implements CommonInterface {

    @Override
    public String getName() {
        throw new IllegalStateException("Not implemented. See usage in ServiceOneController.");
    }
}
