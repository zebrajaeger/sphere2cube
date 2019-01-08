package de.zebrajaeger.sphere2cube.jocl;

/**
 * @author Lars Brandt, Silpion IT Solutions GmbH
 */
public class JoclCalculatorManager {
    private static JoclCalculatorManager INSTANCE = new JoclCalculatorManager();
    private int platformIndex;
    private int deviceIndex;

    private JoclCalculator calculator;

    public static JoclCalculatorManager getInstance() {
        return INSTANCE;
    }

    public void init(int platformIndex, int deviceIndex, int n) {
        this.platformIndex = platformIndex;
        this.deviceIndex = deviceIndex;
        this.calculator = new JoclCalculator(platformIndex, deviceIndex, n);
    }

    public JoclCalculator getCalculator() {
        return calculator;
    }

    public int getPlatformIndex() {
        return platformIndex;
    }

    public int getDeviceIndex() {
        return deviceIndex;
    }
}
