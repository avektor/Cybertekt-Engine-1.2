package net.cybertekt.asset;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Asset Manager - (C) Cybertekt Software
 *
 * @version 1.2.0
 * @author Andrew Vektor
 */
public final class AssetManager {

    /**
     * Internal SLF4J Class Logger For Debugging.
     */
    private static final Logger LOG = LoggerFactory.getLogger(AssetManager.class);

    /**
     * Stores the absolute file path of the root assets directory on the current
     * user system. This is the directory in which the asset manager will search
     * for {@link Asset assets} to be loaded.
     */
    public static final String DIR = System.getProperty("user.dir").replace('\\', '/') + "/assets/";

}
