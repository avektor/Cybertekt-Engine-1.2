package net.cybertekt.app;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;
import net.cybertekt.vulkan.Vulkan.Extension;
import net.cybertekt.vulkan.Vulkan.Layer;

/**
 * Application Settings - (C) Cybertekt Software
 *
 * @version 1.2.0
 * @author Andrew Vektor <Vektor@secMail.pro>
 */
public final class AppSettings {
    
    /**
     * Hard-coded Engine Name Constant.
     */
    public final String ENGINE_NAME = "Cybertekt Engine";
    
    /**
     * Hard-coded Engine Version Constant.
     */
    public final Version ENGINE_VERSION = new Version(1, 2, 0);
    
    /**
     * Application Name.
     */
    public final String APP_NAME;
    
    /**
     * Application Version.
     */
    public final Version APP_VERSION;
    
    /**
     * Hard-coded Vulkan Target API Version Constant.
     */
    public final Version VK_API_VERSION = new Version(1, 0, 2);
    
    public final Set<Extension> VK_EXTENSIONS = EnumSet.of(
        Extension.KHR_SURFACE
    );
    
    public final Set<Layer> VK_LAYERS = EnumSet.noneOf(Layer.class);
    

    public AppSettings(final String NAME, final Version VERSION) {
        this.APP_NAME = NAME;
        this.APP_VERSION = VERSION;
    }
    
    public static class Version {
        
        private final int MAJOR, MINOR, REVISION;
        
        public Version(final int MAJOR, final int MINOR, final int REVISION) {
            this.MAJOR = MAJOR;
            this.MINOR = MINOR;
            this.REVISION = REVISION;
        }
        
        @Override
        public final String toString() {
            return "v" + MAJOR + "." + MINOR + "." + REVISION;
        }
        
        @Override
        public final boolean equals(final Object obj) {
            if (obj == this) 
                return true;
            else if (obj instanceof Version)
                return obj.hashCode() == hashCode();
            else 
                return false;
        }
        
        @Override
        public final int hashCode() {
            return (((MAJOR) << 22) | ((MINOR) << 12) | (REVISION));
        }
    }

}
