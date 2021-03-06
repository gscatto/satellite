package com.github.giulioscattolin.satellite;

import static com.github.giulioscattolin.satellite.Constant.OMEGA_E_DOT;
import static java.lang.Double.NaN;
import static java.lang.Math.*;

public class QuasiKeplerianSatellitePositionModel {
    private double itsCrs = NaN;
    private double itsDeltaN = NaN;
    private double itsM0 = NaN;
    private double itsCuc = NaN;
    private double itsE = NaN;
    private double itsCus = NaN;
    private double itsSqrtA = NaN;
    private double itsCic = NaN;
    private double itsOmega0 = NaN;
    private double itsCis = NaN;
    private double itsI0 = NaN;
    private double itsCrc = NaN;
    private double itsOmega = NaN;
    private double itsOmegaDot = NaN;
    private double itsIDot = NaN;
    private double itsMu = NaN;
    private double itsToeInSecondsSinceTheBeginningOfTheWeek = NaN;
    private double itsSecondsSinceTheBeginningOfTheWeek = NaN;

    public double getCrs() {
        return itsCrs;
    }

    public void setCrs(double newCrs) {
        itsCrs = newCrs;
    }

    public double getDeltaN() {
        return itsDeltaN;
    }

    public void setDeltaN(double newDeltaN) {
        itsDeltaN = newDeltaN;
    }

    public double getM0() {
        return itsM0;
    }

    public void setM0(double newM0) {
        itsM0 = newM0;
    }

    public double getCuc() {
        return itsCuc;
    }

    public void setCuc(double newCuc) {
        itsCuc = newCuc;
    }

    public double getE() {
        return itsE;
    }

    public void setE(double newE) {
        itsE = newE;
    }

    public double getCus() {
        return itsCus;
    }

    public void setCus(double newCus) {
        itsCus = newCus;
    }

    public double getSqrtA() {
        return itsSqrtA;
    }

    public void setSqrtA(double newSqrtA) {
        itsSqrtA = newSqrtA;
    }

    public double getCic() {
        return itsCic;
    }

    public void setCic(double newCic) {
        itsCic = newCic;
    }

    public double getOmega0() {
        return itsOmega0;
    }

    public void setOmega0(double newOmega0) {
        itsOmega0 = newOmega0;
    }

    public double getCis() {
        return itsCis;
    }

    public void setCis(double newCis) {
        itsCis = newCis;
    }

    public double getI0() {
        return itsI0;
    }

    public void setI0(double newI0) {
        itsI0 = newI0;
    }

    public double getCrc() {
        return itsCrc;
    }

    public void setCrc(double newCrc) {
        itsCrc = newCrc;
    }

    public double getOmega() {
        return itsOmega;
    }

    public void setOmega(double newOmega) {
        itsOmega = newOmega;
    }

    public double getOmegaDot() {
        return itsOmegaDot;
    }

    public void setOmegaDot(double newOmegaDot) {
        itsOmegaDot = newOmegaDot;
    }

    public double getIDot() {
        return itsIDot;
    }

    public void setIDot(double newIDot) {
        itsIDot = newIDot;
    }

    public double getMu() {
        return itsMu;
    }

    public void setMu(double newMu) {
        itsMu = newMu;
    }

    public double getToeInSecondsSinceTheBeginningOfTheWeek() {
        return itsToeInSecondsSinceTheBeginningOfTheWeek;
    }

    public void setToeInSecondsSinceTheBeginningOfTheWeek(double newToeInSecondsSinceTheBeginningOfTheWeek) {
        itsToeInSecondsSinceTheBeginningOfTheWeek = newToeInSecondsSinceTheBeginningOfTheWeek;
    }

    public double getSecondsSinceTheBeginningOfTheWeek() {
        return itsSecondsSinceTheBeginningOfTheWeek;
    }

    public void setSecondsSinceTheBeginningOfTheWeek(double newSecondsSinceTheBeginningOfTheWeek) {
        itsSecondsSinceTheBeginningOfTheWeek = newSecondsSinceTheBeginningOfTheWeek;
    }

    public double[] getPosition() {
        return new Algorithm().position;
    }

    class Algorithm {
        final double tk;
        final double A;
        final double N0;
        final double n;
        final double Mk;
        final double Ek;
        final double vk;
        final double phik;
        final double omegadotk;
        final double xkfirst;
        final double ykfirst;
        final double uk;
        final double rk;
        final double ik;
        final double omegak;
        final double xk;
        final double yk;
        final double zk;
        final double[] position;

        Algorithm() {
            tk = getTk();
            A = getA();
            N0 = getN0();
            n = getN();
            Mk = getMeanAnomaly();
            Ek = getEccentricAnomaly();
            vk = getTrueAnomaly();
            phik = getPhik();
            omegadotk = getLongitudeOfAscendingNodeRate();
            uk = getArgumentOfLatitude();
            rk = getRadialDistance();
            ik = getInclinationOfOrbitalPlane();
            xkfirst = getInPlaneXPosition();
            ykfirst = getInPlaneYPosition();
            omegak = getLongitudeOfAscendingNode();
            xk = getX();
            yk = getY();
            zk = getZ();
            position = new double[]{xk, yk, zk};
        }

        /**
         * Returns the seconds since the ephemeris reference epoch.
         */
        double getTk() {
            double tk = itsSecondsSinceTheBeginningOfTheWeek - itsToeInSecondsSinceTheBeginningOfTheWeek;
            if (tk > 302400)
                return tk - 604800;
            if (tk < -302400)
                return tk + 604800;
            return tk;
        }

        double getA() {
            return itsSqrtA * itsSqrtA;
        }

        double getN0() {
            return sqrt(itsMu / (A * A * A));
        }

        double getN() {
            return N0 + itsDeltaN;
        }

        double getMeanAnomaly() {
            return itsM0 + n * tk;
        }

        double getEccentricAnomaly() {
            double Ej = Mk;
            for (int i = 0; i < 4; i++)
                Ej += ((Mk - Ej) + itsE * sin(Ej)) / (1 - itsE * cos(Ej));
            return Ej;
        }

        double getTrueAnomaly() {
            return 2 * atan(sqrt((1 + itsE) / (1 - itsE)) * tan(Ek / 2));
        }

        double getPhik() {
            return vk + itsOmega;
        }

        double getLongitudeOfAscendingNodeRate() {
            return itsOmegaDot - OMEGA_E_DOT;
        }

        double getArgumentOfLatitude() {
            return phik + itsCus * sin(2 * phik) + itsCuc * cos(2 * phik);
        }

        double getRadialDistance() {
            return A * (1 - itsE * cos(Ek)) + itsCrs * sin(2 * phik) + itsCrc * cos(2 * phik);
        }

        double getInclinationOfOrbitalPlane() {
            return itsI0 + itsCis * sin(2 * phik) + itsCic * cos(2 * phik) + itsIDot * tk;
        }

        double getLongitudeOfAscendingNode() {
            return itsOmega0 + omegadotk * tk - OMEGA_E_DOT * itsToeInSecondsSinceTheBeginningOfTheWeek;
        }

        double getInPlaneXPosition() {
            return rk * cos(uk);
        }

        double getInPlaneYPosition() {
            return rk * sin(uk);
        }

        double getX() {
            return (xkfirst * cos(omegak)) - (ykfirst * cos(ik) * sin(omegak));
        }

        double getY() {
            return (xkfirst * sin(omegak)) + (ykfirst * cos(ik) * cos(omegak));
        }

        double getZ() {
            return rk * sin(uk) * sin(ik);
        }
    }
}
