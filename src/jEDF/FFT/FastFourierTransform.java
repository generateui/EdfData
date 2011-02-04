package jEDF.FFT;

/**
 *
 * <p>Titre : jEDF</p>
 * <p>Description : Java European Data Format Viewer and Analyser</p>
 * <p>Author : Nizar Kerkeni</p>
 * <p>Copyright : Copyright (c) 2003-2006</p>
 * <p>Version : 2.0</p>
 */

public class FastFourierTransform {

    private static int n;
    private static int nu;
    /**
     * Remove the baseline and compute the real FFT of the given short array.
     * Methode : Butterfly Operation
     * Adapted from : {@link http://www.dsptutor.freeuk.com/analyser/SpectrumAnalyser.html}
     * @param data the array to be computed
     * @return its FFT, in real numbers
     */
    public static double[] processDataRealFFT(short data[]) {
        if (data.length == 0) {
            return new double[0];
        }

        // elimination of baseline
        int sum = 0;
        for (int i = 0; i < data.length; i++) {
            sum += data[i];
        }
        double average = (double) sum / (double) data.length;
        // conversion to double and filling with zeroes up to the next power of 2
        double[] datad = new double[getNearestPowerOfTwo(data.length)];
        for (int i = 0; i < datad.length; i++) {
            if (i < data.length) {
                datad[i] = (double) data[i] - average;
            }
            else {
                datad[i] = 0;
            }
        }
        datad = realFFT(datad);
        return datad;
    }

    private static double[] realFFT(double data[]) {
        n = data.length;
        nu = (int) (Math.log(n) / Math.log(2));
        int n2 = n / 2;
        int nu1 = nu - 1;
        double[] xre = new double[n];
        double[] xim = new double[n];
        double[] mag = new double[n2];
        double tr, ti, p, arg, c, s;
        for (int i = 0; i < n; i++) {
            xre[i] = data[i];
            xim[i] = 0;
        }
        int k = 0;

        for (int l = 1; l <= nu; l++) {
            while (k < n) {
                for (int i = 1; i <= n2; i++) {
                    p = bitReverse(k >> nu1);
                    arg = 2 * (double) Math.PI * p / n;
                    c = (double) Math.cos(arg);
                    s = (double) Math.sin(arg);
                    tr = xre[k + n2] * c + xim[k + n2] * s;
                    ti = xim[k + n2] * c - xre[k + n2] * s;
                    xre[k + n2] = xre[k] - tr;
                    xim[k + n2] = xim[k] - ti;
                    xre[k] += tr;
                    xim[k] += ti;
                    k++;
                }
                k += n2;
            }
            k = 0;
            nu1--;
            n2 = n2 / 2;
        }
        k = 0;
        int r;
        while (k < n) {
            r = bitReverse(k);
            if (r > k) {
                tr = xre[k];
                ti = xim[k];
                xre[k] = xre[r];
                xim[k] = xim[r];
                xre[r] = tr;
                xim[r] = ti;
            }
            k++;
        }
//      mag[0] = (double) (Math.sqrt(xre[0] * xre[0] + xim[0] * xim[0])); // Magnitude
//      mag[0] = (double) (Math.sqrt(xre[0] * xre[0] + xim[0] * xim[0]))/ n; // Amplitude
        mag[0] = (double) 2*(xre[0] * xre[0] + xim[0] * xim[0])/ n; // Power as Sum Squared Amplitude
        for (int i = 1; i < n / 2; i++) {
//          mag[i] = (double) (Math.sqrt(xre[i] * xre[i] + xim[i] * xim[i])); // Magnitude
//          mag[i] = (double) 2*(Math.sqrt(xre[i] * xre[i] + xim[i] * xim[i]))/ n; // Amplitude
            mag[i] = (double) 2 *(xre[i] * xre[i] + xim[i] * xim[i])/ n; // Power as Sum Squared Amplitude
        }
        return mag;
    }

    /**
     * Calculate the binary reverse of a given value
     * exp : 011000 return 000110
     * @param j int integer value
     * @return int the binary reverse of the given value
     */
    private static int bitReverse(int j) {
        int j2;
        int j1 = j;
        int k = 0;
        for (int i = 1; i <= nu; i++) {
            j2 = j1 / 2;
            k = 2 * k + j1 - 2 * j2;
            j1 = j2;
        }
        return k;
    }

    /**
     * return the nearest power of two for a given number
     * @param value the number
     * @return the nearest power of two
     */
    private static int getNearestPowerOfTwo(int value) {
        return (int) Math.pow(2, Math.ceil(Math.log(value) / Math.log(2)));
    }
}
