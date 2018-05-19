package net.cybertekt.app;

import net.cybertekt.app.AppSettings.Version;
import net.cybertekt.vulkan.Vulkan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Vulkan Test - (C) Cybertekt Software
 *
 * @version 1.2.0
 * @author Andrew Vektor
 */
public class VulkanTest extends Application {

    /**
     * Internal SLF4J Class Logger For Debugging.
     */
    private static final Logger LOG = LoggerFactory.getLogger(VulkanTest.class);

    public static void main(final String[] args) {
        AppSettings settings = new AppSettings("Vulkan Test", new Version(1, 0, 0));
        
        // Set Vulkan Extension Settings \\
        settings.VK_EXTENSIONS.add(Vulkan.Extension.DEBUG_REPORT);
        
        // Set Vulkan Layer Settings \\
        settings.VK_LAYERS.add(Vulkan.Layer.STANDARD_VALIDATION);
        
        VulkanTest app = new VulkanTest();
        app.initialize(settings);
    }
    
    @Override
    public final void init() {
        
    }
    
    @Override
    public final void exit() {
        
    }
}
