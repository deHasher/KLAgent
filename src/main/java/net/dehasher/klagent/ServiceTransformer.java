package net.dehasher.klagent;

import javassist.*;
import java.lang.instrument.ClassFileTransformer;
import java.security.ProtectionDomain;
import java.util.stream.Stream;

public class ServiceTransformer implements ClassFileTransformer {
    private static boolean transformed = false;

    @Override
    @SuppressWarnings("CallToPrintStackTrace")
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) {
        if (transformed || className.contains("/") || className.contains("$")) return classfileBuffer;

        try {
            CtClass ctClass = ClassPool.getDefault().get(className);
            if (!Methods.isSkinClass(ctClass)) return classfileBuffer;

            try {
                for (CtMethod ctMethod : ctClass.getDeclaredMethods()) {
                    if (!Methods.checkMethod(ctMethod)) continue;
                    ctMethod.setBody(Methods.getBody(ctMethod.getReturnType().getName()));
                    break;
                }
                if (Stream.of(ctClass.getDeclaredFields()).noneMatch(Methods::isOFField)) Methods.injectOFFields(ctClass);
                ctClass.writeFile();
                transformed = true;
                Methods.clearTrash();
                Methods.info("HD skins have been successfully fixed!");
                return ctClass.toBytecode();
            } catch (Throwable t) {
                Methods.info("An error occurred during the transformation!");
                t.printStackTrace();
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }

        return classfileBuffer;
    }
}