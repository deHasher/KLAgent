package ru.dehasher.klagent;

import javassist.CannotCompileException;
import javassist.CtClass;
import javassist.CtField;
import javassist.CtMethod;
import java.io.File;
import java.util.Objects;

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

    public static void optifine(CtClass _class) throws CannotCompileException {
        _class.addField(CtField.make("public Boolean imageFound = null;", _class));
        _class.addField(CtField.make("public boolean pipeline = false;", _class));
    }

    public static boolean checkConstructor(String constructor) {
        return constructor.contains("(Ljava/io/File;Ljava/lang/String;") && constructor.contains(";ZLjava/lang/Runnable;)");
    }

    public static boolean checkField(CtField field) {
        if (field == null) return false;
        return field.getName().equals("imageFound") || field.getName().equals("pipeline");
    }

    public static boolean checkMethod(CtMethod method) {
        return method != null && method.getName().equals("c");
    }

    public static void clean() {
        new Thread(() -> {
            try { Thread.sleep(5000); } catch (InterruptedException ignored) {}
            for (File file : Objects.requireNonNull(new File(".").listFiles())) {
                if (!file.getName().endsWith(".class")) continue;
                if (!file.delete()) System.out.println("Failed to delete file " + file.getName());
            }
        }).start();
    }
}