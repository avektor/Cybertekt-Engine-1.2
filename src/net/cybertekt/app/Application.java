package net.cybertekt.app;

import net.cybertekt.vulkan.Vulkan;
import org.lwjgl.system.Configuration;
import static org.lwjgl.vulkan.EXTDebugReport.VK_DEBUG_REPORT_ERROR_BIT_EXT;
import static org.lwjgl.vulkan.EXTDebugReport.VK_DEBUG_REPORT_WARNING_BIT_EXT;
import org.lwjgl.vulkan.VkInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Application - (C) Cybertekt Software
 *
 * @version 1.2.0
 * @author Andrew Vektor <Vektor@secMail.pro>
 */
public abstract class Application {

    /**
     * SLF4J Application Class Logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger(Application.class);
    
    /**
     * Static Application Initialization Block.
     */
    static {
        // LWJGL Configuration \\
        //Configuration.DEBUG.set(true);                    // LWJGL General Debugging.
        Configuration.DEBUG_MEMORY_ALLOCATOR.set(true);     // LWJGL Native Memory Allocator Debugging.
        Configuration.DEBUG_STACK.set(true);                // LWJGL Native Memory Stack Debugging.
        Configuration.STACK_SIZE.set(64);                   // LWJGL Native Memory Stack Size (in KB).

        // GLFW Initialization \\
        if (!org.lwjgl.glfw.GLFW.glfwInit()) {
            LOG.error("GLFW Initialization Failed");
            throw new ApplicationInitializationException("GLFW Initialization Failed");
        }
    }

    public final void initialize(final AppSettings SETTINGS) {
        LOG.info("Initializing {} {}", SETTINGS.APP_NAME, SETTINGS.APP_VERSION);

        try {
            // Create Vulkan Instance \\
            VkInstance vkInstance = Vulkan.createInstance(SETTINGS);

            // Enable Vulkan Instance Debugging \\
            final long vkDebugCallback = Vulkan.enableDebug(vkInstance, VK_DEBUG_REPORT_ERROR_BIT_EXT | VK_DEBUG_REPORT_WARNING_BIT_EXT);
            
            // final VkPhysicalDevice physicalDevice = getFirstPhysicalDevice(instance);
            // final DeviceAndGraphicsQueueFamily deviceAndGraphicsQueueFamily = createDeviceAndGetGraphicsQueueFamily(physicalDevice);
            // final VkDevice device = deviceAndGraphicsQueueFamily.device;
            // int queueFamilyIndex = deviceAndGraphicsQueueFamily.queueFamilyIndex;
            init();

            LOG.info("{} {} Initialized", SETTINGS.APP_NAME, SETTINGS.APP_VERSION);

            loop();
        } finally {
            destroy();
        }
    }

    /**
     * Application Subclass Initialization.
     */
    public abstract void init();

    public final void loop() {

    }

    public final void destroy() {
        Vulkan.terminate();
        org.lwjgl.glfw.GLFW.glfwTerminate();
    }

    public abstract void exit();

    /**
     * {@link RuntimeException Runtime Exception} thrown when an error occurs
     * during the initialization of a Cybertekt Application.
     */
    protected static class ApplicationInitializationException extends RuntimeException {

        public ApplicationInitializationException(final String REASON) {
            super(REASON);
        }
    }
}
