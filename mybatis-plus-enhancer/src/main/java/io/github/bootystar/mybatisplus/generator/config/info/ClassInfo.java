package io.github.bootystar.mybatisplus.generator.config.info;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Collections;


/**
 * @author bootystar
 */
@Getter
@NoArgsConstructor
public class ClassInfo {
    protected String classPackage;
    protected String classSimpleName;
    protected String classFullName;
    protected int classGenericTypeCount;

    public ClassInfo(Class<?> clazz) {
        this.classPackage = clazz.getPackage().getName();
        this.classSimpleName = clazz.getSimpleName();
        this.classFullName = clazz.getName();
        this.classGenericTypeCount = clazz.getTypeParameters().length;
    }

    public boolean enable() {
        if (classPackage == null || classSimpleName == null || classFullName == null) {
            return false;
        }
        return true;
    }

    public String classDeclaration(String... genericTypeStr) {
        if (classSimpleName == null) {
            return genericTypeStr!=null && genericTypeStr.length == 1 ? genericTypeStr[0] : "Object";
        }
        if (classGenericTypeCount == 0) {
            return classSimpleName;
        }

        if (genericTypeStr != null || classGenericTypeCount == genericTypeStr.length) {
            return String.format("%s<%s>", classSimpleName, String.join(", ", genericTypeStr));
        }
        return String.format("%s<%s>", classSimpleName, String.join(", ", Collections.nCopies(classGenericTypeCount, "?")));
    }




}
