package net.dehasher.klagent;

import javassist.*;
import java.io.File;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

public class Methods {
    public static String getBody(String name) {
        return "{" +
                "if (32 == $1.b()) {" +
                "    " + name + " nativeimage = new " + name + "(64, 64, true);" +
                "    nativeimage.a($1);" +
                "    $1.close();" +
                "    $1 = nativeimage;" +
                "    nativeimage.a(0, 32, 64, 32, 0);" +
                "    nativeimage.a(4, 16, 16, 32, 4, 4, true, false);" +
                "    nativeimage.a(8, 16, 16, 32, 4, 4, true, false);" +
                "    nativeimage.a(0, 20, 24, 32, 4, 12, true, false);" +
                "    nativeimage.a(4, 20, 16, 32, 4, 12, true, false);" +
                "    nativeimage.a(8, 20, 8, 32, 4, 12, true, false);" +
                "    nativeimage.a(12, 20, 16, 32, 4, 12, true, false);" +
                "    nativeimage.a(44, 16, -8, 32, 4, 4, true, false);" +
                "    nativeimage.a(48, 16, -8, 32, 4, 4, true, false);" +
                "    nativeimage.a(40, 20, 0, 32, 4, 12, true, false);" +
                "    nativeimage.a(44, 20, -8, 32, 4, 12, true, false);" +
                "    nativeimage.a(48, 20, -16, 32, 4, 12, true, false);" +
                "    nativeimage.a(52, 20, -8, 32, 4, 12, true, false);" +
                "}" +

                "b($1, 0, 0, 32, 16);" +
                "if (32 == $1.b()) {" +
                "    a($1, 32, 0, 64, 32);" +
                "}" +

                "b($1, 0, 16, 64, 32);" +
                "b($1, 16, 48, 48, 64);" +
                "return ($r)$1;" +
                "}";
    }

    public static void injectOFFields(CtClass _class) throws CannotCompileException {
        _class.addField(CtField.make("public Boolean imageFound = null;", _class));
        _class.addField(CtField.make("public boolean pipeline = false;", _class));
    }

    public static boolean isSkinClass(CtClass ctClass) {
        CtField[] ctFields;
        try {
            ctFields = ctClass.getDeclaredFields();
        } catch (Throwable ignored) {
            return false;
        }

        if (Stream.of(ctFields).filter(Objects::nonNull).filter(ctField -> {
            try {
                return ctField.getType().getName().equals(int.class.getName()) && Modifier.isStatic(ctField.getModifiers()) && Modifier.isFinal(ctField.getModifiers());
            } catch (Throwable ignored) {}
            return false;
        }).count() < 3) return false;

        AtomicInteger size64 = new AtomicInteger(0);
        AtomicInteger size32 = new AtomicInteger(0);
        Stream.of(ctFields).filter(Objects::nonNull).forEach(ctField -> {
            try {
                if (ctField.getType().getName().equals(int.class.getName()) && Modifier.isStatic(ctField.getModifiers()) && Modifier.isFinal(ctField.getModifiers())) {
                    Object object;
                    try {
                        object = ctField.getConstantValue();
                    } catch (Throwable t) {
                        return;
                    }
                    if (object == null) return;
                    int count = (int) object;
                    if (count == 64) size64.incrementAndGet();
                    if (count == 32) size32.incrementAndGet();
                }
            } catch (Throwable ignored) {}
        });
        return size64.get() == 2 && size32.get() == 1;
    }

    public static boolean isOFField(CtField field) {
        if (field == null) return false;
        return field.getName().equals("imageFound") || field.getName().equals("pipeline");
    }

    public static boolean checkMethod(CtMethod ctMethod) throws Throwable {
        return ctMethod.getReturnType() == ctMethod.getParameterTypes()[0];
    }

    public static void clearTrash() {
        new Thread(() -> {
            try { Thread.sleep(5000); } catch (InterruptedException ignored) {}
            for (File file : Objects.requireNonNull(new File(".").listFiles())) {
                if (!file.getName().endsWith(".class")) continue;
                if (!file.delete()) info("Failed to delete file " + file.getName());
            }
        }).start();
    }

    public static void info(String info) {
        System.out.println(info);
    }
}