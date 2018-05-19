package net.cybertekt.asset;

import net.cybertekt.util.CacheMap;
import net.cybertekt.util.CacheMap.CacheMode;
import net.cybertekt.util.CacheMap.CacheType;

/**
 * Asset Type - (C) Cybertekt Software
 *
 * Immutable class that uniquely describes a specific type of external resource
 * based on its file type extension.
 *
 * @version 1.2.0
 * @author Andrew Vektor
 */
public final class AssetType {

    /**
     * {@link CacheMap Cache} that stores every unique asset type created by the
     * {@link #getType(java.lang.String)} static utility method.
     */
    private static final CacheMap<String, AssetType> CACHE = new CacheMap(CacheType.Hash, CacheMode.Weak);

    public static final AssetType getType(final String EXT) {
        return null;
    }
}
