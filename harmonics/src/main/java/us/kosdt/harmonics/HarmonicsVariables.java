package us.kosdt.harmonics;

public class HarmonicsVariables {
 
    private static final double ONE_HUNDRED_PERCENT = 1.0;
    private static final double LINEAR_DROPOFF = 0.0;
    private static final int FIVE = 5;
    private static final double CONCERT_A = 440.0;

    public double totalAmplitude;
    public double harmonicAmplitude;
    public double harmonicDropoff;
    public double frequency;
    public int numberOfHarmonics;

    public HarmonicsVariables() {
        totalAmplitude = ONE_HUNDRED_PERCENT;
        harmonicAmplitude = ONE_HUNDRED_PERCENT;
        harmonicDropoff = LINEAR_DROPOFF;
        frequency = CONCERT_A;
        numberOfHarmonics = FIVE;
    }

}