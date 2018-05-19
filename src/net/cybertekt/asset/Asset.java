package net.cybertekt.asset;

/**
 * Asset - (C) Cybertekt Software
 *
 * Encapsulates the data of an external resource located at the file path
 * specified by an {@link AssetKey asset key}. As a general rule, asset
 * subclasses should be designed to be effectively immutable.
 *
 * @version 1.2.0
 * @author Andrew Vektor
 */
public abstract class Asset {

    /**
     * Specifies the file location and file type of the external resource used
     * to define the asset.
     */
    private final AssetKey KEY;

    protected Asset(final AssetKey KEY) {
        this.KEY = KEY;
    }

    public final AssetKey getKey() {
        return KEY;
    }

}
