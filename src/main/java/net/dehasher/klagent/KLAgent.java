package net.dehasher.klagent;

import java.lang.instrument.Instrumentation;
import java.lang.management.ManagementFactory;
import java.util.jar.JarFile;

public class KLAgent {
    private static final String javaagent = "-javaagent";

    public static void premain(String args, Instrumentation inst) throws Exception {
        String agentPath = ManagementFactory.getRuntimeMXBean().getInputArguments().stream()
                .filter(arg -> arg.startsWith(javaagent))
                .findAny().map(arg -> arg.substring(javaagent.length() + 1))
                .orElseThrow(() -> new RuntimeException("Current javaagent jar path not found"))
                .replace("\"", "");
        inst.appendToBootstrapClassLoaderSearch(new JarFile(agentPath));
        inst.addTransformer(new ServiceTransformer(), true);
    }
}