package us.kosdt.harmonics;

public class HarmonicsFunctions {

    /**
     * Takes the x-value of the harmonic and returns its amplitude.
     * @param x A value in the range [0,1] representing the location of the harmonic.
     * @param n A value in the range [-1,1] representing the shape of the amplitude dropoff curve.
     * @return The amplitude of the harmonic.
     */
    public static double harmonicAmplitude(double x, double n) {
        double p = 1.0/(1-n);             // Maps [ 0,1] to [1,infinity]
        double q = 1.0+n;                 // Maps [-1,0] to [0,1]
        double reducedX = x % 1;
        /*
        double amplitude = 1.0;           // Harmonics close to 0 are 100% amplitude
        if (n<=0) {
            amplitude -= Math.pow(x,p); // Curve down
        } else {
            amplitude -= Math.pow(x,q); // Curve up
        }
        */
        double amplitude = 1-Math.pow(reducedX, p)*((1+Math.signum(n))/2)-Math.pow(reducedX,q)*((1-Math.signum(n))/2);
        return amplitude;
    }

    /**
     * Returns the y value of the given waveform at a given x and with a given period.
     * @param variables Variables representing the shape of the waveform
     * @param x The x value
     * @param period The period of the waveform
     * @return The y value at the given x value
     */
    public static double harmonicSum(HarmonicsVariables variables, double x, double period) {
        double periodFactor = Math.PI*2/period;
        double y = Math.sin(periodFactor*x);
        for (int i = 1; i <= variables.numberOfHarmonics; i++) {    // [1...N]
            double harmonicFrequencyFactor = i+1;                   // [2...N+1]
            y += variables.harmonicAmplitude*harmonicAmplitude(i / (double)variables.numberOfHarmonics, variables.harmonicDropoff)
                    * Math.sin(periodFactor*harmonicFrequencyFactor*x);
        }
        return y/(variables.numberOfHarmonics*variables.harmonicAmplitude+1);
    }

}