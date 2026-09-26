package net.dragonultimate.stats;

public class Spi {
    public static final int KI_PER_SPI = 12;
    public static final int KI_REGEN_PER_SPI = 15;
    private int spi;

    public Spi() {
        this.spi = 10;
    }
    public void setSpi(int spi) {
        this.spi = spi;
    }

    public int getSpi() {
        return this.spi;
    }

    public int getMaxSpi(int spiPoints ) {
        return Math.max(0, spiPoints ) * KI_PER_SPI;
    }

    public int convertSpi ( int spiPoints) {
        return getMaxSpi (spiPoints);
    }

    public int getSpiRegen(int spiPoints) {
        return Math.max(0, spiPoints) * KI_REGEN_PER_SPI;
    }

    public int clamp(int spi, int spiPoints) {
        return Math.clamp(spi, 0, getMaxSpi(spiPoints));
    }

    public int regenerate(int spi, int spiPoints) {
        return clamp(spi + getSpiRegen(spiPoints), spiPoints);
    }


}
