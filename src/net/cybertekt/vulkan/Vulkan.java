package net.cybertekt.vulkan;

import java.nio.ByteBuffer;
import java.nio.LongBuffer;
import java.util.Iterator;
import net.cybertekt.app.AppSettings;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.vulkan.EXTDebugReport;
import org.lwjgl.vulkan.KHRSurface;
import org.lwjgl.vulkan.VK10;
import org.lwjgl.vulkan.VkApplicationInfo;
import org.lwjgl.vulkan.VkDebugReportCallbackCreateInfoEXT;
import org.lwjgl.vulkan.VkDebugReportCallbackEXT;
import org.lwjgl.vulkan.VkInstance;
import org.lwjgl.vulkan.VkInstanceCreateInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Vulkan - (C) Cybertekt Software
 *
 * @version 1.2.0
 * @author Andrew Vektor <Vektor@secMail.pro>
 */
public class Vulkan {

    /**
     * Internal SLF4J Logger For Debugging.
     */
    private static final Logger LOG = LoggerFactory.getLogger(Vulkan.class);

    public static enum Extension {
        /**
         * Vulkan KHR Surface Extension.
         */
        KHR_SURFACE(KHRSurface.VK_KHR_SURFACE_EXTENSION_NAME),
        /**
         * Vulkan Debug Report Extension.
         */
        DEBUG_REPORT(EXTDebugReport.VK_EXT_DEBUG_REPORT_EXTENSION_NAME);

        private final String EXT_NAME;

        Extension(final String NAME) {
            EXT_NAME = NAME;
        }
    }

    public static enum Layer {
        /**
         * Vulkan Standard Validation Layer.
         */
        STANDARD_VALIDATION("VK_LAYER_LUNARG_standard_validation");

        private final String LYR_NAME;

        Layer(final String NAME) {
            LYR_NAME = NAME;
        }
    }

    public static VkInstance createInstance(final AppSettings SETTINGS) {

        // GLFW Initialization \\
        if (!org.lwjgl.glfw.GLFW.glfwInit()) {
            LOG.error("GLFW Initialization Failed");
            throw new VulkanInitializationException("GLFW Initialization Failed");
        }

        // Check For Vulkan Loader \\
        if (!org.lwjgl.glfw.GLFWVulkan.glfwVulkanSupported()) {
            LOG.error("Vulkan loader not found");
            throw new VulkanInitializationException("Vulkan loader not found");
        }

        // Initialize Vulkan Application Information \\
        VkApplicationInfo vkAppInfo = VkApplicationInfo.calloc()
                .sType(VK10.VK_STRUCTURE_TYPE_APPLICATION_INFO) // Struct Type
                .pEngineName(MemoryUtil.memUTF8(SETTINGS.ENGINE_NAME)) // Engine Name
                .engineVersion(SETTINGS.ENGINE_VERSION.hashCode()) // Engine Version
                .pApplicationName(MemoryUtil.memUTF8(SETTINGS.APP_NAME)) // Application Name
                .applicationVersion(SETTINGS.APP_VERSION.hashCode()) // Application Version
                .apiVersion(SETTINGS.VK_API_VERSION.hashCode());            // Vulkan API Target Version

        // Initialize Vulkan Extensions \\
        PointerBuffer vkExtensions = MemoryUtil.memAllocPointer(SETTINGS.VK_EXTENSIONS.size());
        ByteBuffer[] vkExt = new ByteBuffer[SETTINGS.VK_EXTENSIONS.size()];
        Iterator<Extension> vkExtIt = SETTINGS.VK_EXTENSIONS.iterator();
        for (int i = 0; vkExtIt.hasNext(); i++) {
            vkExtensions.put(vkExt[i] = MemoryUtil.memUTF8(vkExtIt.next().EXT_NAME));
        }
        vkExtensions.flip();

        // Initialize Vulkan Layers \\
        PointerBuffer vkLayers = MemoryUtil.memAllocPointer(SETTINGS.VK_LAYERS.size());
        ByteBuffer[] vkLyr = new ByteBuffer[SETTINGS.VK_LAYERS.size()];
        Iterator<Layer> vkLyrIt = SETTINGS.VK_LAYERS.iterator();
        for (int i = 0; vkLyrIt.hasNext(); i++) {
            vkLayers.put(vkLyr[i] = MemoryUtil.memUTF8(vkLyrIt.next().LYR_NAME));
        }
        vkLayers.flip();

        // Initialize Vulkan Instance Information \\
        VkInstanceCreateInfo vkInfo = VkInstanceCreateInfo.calloc()
                .sType(VK10.VK_STRUCTURE_TYPE_INSTANCE_CREATE_INFO) // Struct Type
                .pNext(MemoryUtil.NULL) // Always Null
                .pApplicationInfo(vkAppInfo) // Vulkan Application Info
                .ppEnabledExtensionNames(vkExtensions) // Vulkan Enabled Extensions
                .ppEnabledLayerNames(vkLayers);                             // Vulkan Enabled Layers

        // Create Vulkan Instance \\
        PointerBuffer vkInstancePointer = MemoryUtil.memAllocPointer(1);
        if (VK10.vkCreateInstance(vkInfo, null, vkInstancePointer) != VK10.VK_SUCCESS) {
            throw new VulkanInitializationException("Vulkan instance creation failed");
        }
        VkInstance vkInstance = new VkInstance(vkInstancePointer.get(0), vkInfo);

        // Free Vulkan Application Info Resources \\
        MemoryUtil.memFree(vkAppInfo.pApplicationName());                   // Free Vulkan Application Name Pointer
        MemoryUtil.memFree(vkAppInfo.pEngineName());                        // Free Vulkan Engine Name Pointer
        vkAppInfo.free();                                                   // Free Vulkan Application Info

        // Free Vulkan Extension Resources \\
        for (ByteBuffer ext : vkExt) {
            MemoryUtil.memFree(ext);
        }
        MemoryUtil.memFree(vkExtensions);                                   // Free Vulkan Extension Info

        // Release Vulkan Layer Resources \\
        for (ByteBuffer lyr : vkLyr) {
            MemoryUtil.memFree(lyr);
        }
        MemoryUtil.memFree(vkLayers);                                       // Free Vulkan Layer Info

        vkInfo.free();                                                      // Free Vulkan Instance Info
        vkInstancePointer.free();                                           // Free Vulkan Instance Pointer       

        // Return New Vulkan Instance \\
        return vkInstance;
    }

    /**
     *
     *
     * @param vkInstance
     * @param FLAGS
     * @return the Vulkan debug callback pointer.
     * @throw VulkanExtensionException if the {@link Extension#DEBUG_REPORT}
     * extension is not enabled.
     */
    public static long enableDebug(final VkInstance vkInstance, final int FLAGS) {

        // Check For Vulkan Debug Report Extension \\
        if (!vkInstance.getCapabilities().VK_EXT_debug_report) {
            throw new VulkanExtensionException("Extension is disabled: ", Extension.DEBUG_REPORT.EXT_NAME);
        }

        // Create Vulkan Debug Report Callback Information \\
        VkDebugReportCallbackCreateInfoEXT vkDebugCallbackInfo = VkDebugReportCallbackCreateInfoEXT.calloc()
                .sType(EXTDebugReport.VK_STRUCTURE_TYPE_DEBUG_REPORT_CALLBACK_CREATE_INFO_EXT)
                .pNext(MemoryUtil.NULL)
                .pfnCallback(VK_DEBUG_CALLBACK)
                .pUserData(MemoryUtil.NULL)
                .flags(FLAGS);

        // Create Vulkan Debug Report Callback \\
        LongBuffer vkCallback = MemoryUtil.memAllocLong(1);
        if (EXTDebugReport.vkCreateDebugReportCallbackEXT(vkInstance, vkDebugCallbackInfo, null, vkCallback) != VK10.VK_SUCCESS) {
            throw new VulkanInitializationException("Debug report callback initialization failed");
        }
        long vkDebugCallback = vkCallback.get(0);

        // Release Debug Callback Resources \\
        MemoryUtil.memFree(vkCallback);
        vkDebugCallbackInfo.free();

        return vkDebugCallback;
    }

    public static final void terminate() {
        VK_DEBUG_CALLBACK.free();
    }

    /**
     * Vulkan Debug Report Callback.
     */
    private static final VkDebugReportCallbackEXT VK_DEBUG_CALLBACK = new VkDebugReportCallbackEXT() {
        @Override
        public int invoke(int flags, int objectType, long object, long location, int messageCode, long layerPrefix, long message, long userData) {
            LOG.error(VkDebugReportCallbackEXT.getString(message));
            return 0;
        }
    };

    /**
     * {@link RuntimeException Runtime Exception} thrown when an error occurs
     * during Vulkan initialization.
     */
    private static class VulkanInitializationException extends RuntimeException {

        public VulkanInitializationException(final String REASON) {
            super(REASON);
        }
    }

    private static class VulkanExtensionException extends RuntimeException {

        public VulkanExtensionException(final String REASON, final String EXT) {
            super(REASON + EXT);
        }
    }
}
