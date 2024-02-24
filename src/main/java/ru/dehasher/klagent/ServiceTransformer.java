package ru.dehasher.klagent;

import javassist.*;
import java.lang.instrument.ClassFileTransformer;
import java.security.ProtectionDomain;
import java.util.Arrays;

public class ServiceTransformer implements ClassFileTransformer {
    private static boolean transformed = false;

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) {
        if (transformed) return classfileBuffer;

        try {
            CtClass _class = ClassPool.getDefault().get(className);
            String constructor = Arrays.toString(_class.getDeclaredConstructors());

            if (!Methods.checkConstructor(constructor)) {
                return classfileBuffer;
            } else {
                System.out.println("HD skins have been successfully fixed!");
            }

            try {
                for (CtMethod method : _class.getDeclaredMethods()) {
                    if (!Methods.checkMethod(method)) continue;
                    String name = method.getReturnType().getName();
                    method.setBody(Methods.getBody(name));
                    break;
                }
                boolean hasFileds = false;
                for (CtField field : _class.getDeclaredFields()) {
                    if (hasFileds) break;
                    hasFileds = Methods.checkField(field);
                }
                if (!hasFileds) Methods.optifine(_class);
                _class.writeFile();
                transformed = true;
                Methods.clean();
                return _class.toBytecode();
            } catch (Throwable t) {
                System.out.println("An error occurred during the transformation!");
                t.printStackTrace();
            }
        } catch (Throwable ignored) {}

        return classfileBuffer;
    }
}