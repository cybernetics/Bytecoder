/*
 * Copyright 2017 Mirko Sertic
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.mirkosertic.bytecoder.backend.wasm;

import de.mirkosertic.bytecoder.classlib.java.lang.TArray;
import de.mirkosertic.bytecoder.core.*;
import de.mirkosertic.bytecoder.ssa.TypeRef;

public class WASMWriterUtils {


    public static String typeRefToString(BytecodeTypeRef aTypeRef) {
        if (aTypeRef.isPrimitive()) {
            BytecodePrimitiveTypeRef thePrimitive = (BytecodePrimitiveTypeRef) aTypeRef;
            return thePrimitive.name();
        }
        if (aTypeRef.isArray()) {
            BytecodeArrayTypeRef theRef = (BytecodeArrayTypeRef) aTypeRef;
            return "A" + theRef.getDepth() + typeRefToString(theRef.getType());
        }
        BytecodeObjectTypeRef theObjectRef = (BytecodeObjectTypeRef) aTypeRef;
        return toClassName(theObjectRef);
    }

    public static String toMethodSignature(BytecodeMethodSignature aSignature, boolean aIsStatic) {
        String theName = typeRefToString(aSignature.getReturnType()) + "_";
        if (!aIsStatic) {
            theName += "THISREF";
        }

        for (BytecodeTypeRef theTypeRef : aSignature.getArguments()) {
            theName += typeRefToString(theTypeRef);
        }
        return theName.replace("(", "").replace(")", "");
    }


    public static String toMethodName(String aMethodName, BytecodeMethodSignature aSignature) {
        String theName = typeRefToString(aSignature.getReturnType());
        theName += aMethodName.replace("<", "").replace(">", "");

        for (BytecodeTypeRef theTypeRef : aSignature.getArguments()) {
            theName += typeRefToString(theTypeRef);
        }
        return theName;
    }

    public static String toMethodName(BytecodeObjectTypeRef aClassName, String aMethodName, BytecodeMethodSignature aSignature) {
        return toClassName(aClassName) + "_" + toMethodName(aMethodName, aSignature);
    }

    public static String toMethodName(BytecodeObjectTypeRef aClassName, BytecodeUtf8Constant aConstant, BytecodeMethodSignature aSignature) {
        return toClassName(aClassName) + "_" + toMethodName(aConstant.stringValue(), aSignature);
    }

    public static String toClassNameInternal(String aClassName) {
        int p = aClassName.lastIndexOf(".");
        return aClassName.substring(p + 1);
    }

    public static String toClassName(BytecodeObjectTypeRef aTypeRef) {
        if (aTypeRef.name().endsWith(";")) {
            // This seems to be an array
            return toClassName(BytecodeObjectTypeRef.fromRuntimeClass(TArray.class));
        }
        return toClassNameInternal(aTypeRef.name());
    }

    public static String toClassName(BytecodeClassinfoConstant aTypeRef) {
        return toClassNameInternal(aTypeRef.getConstant().stringValue().replace("/","."));
    }

    public static String toType(TypeRef aType) {
        switch (aType.resolve()) {
            case DOUBLE:
                return "f32";
            case FLOAT:
                return "f32";
            default:
                return "i32";
        }
    }

    public static String toWASMMethodSignature(BytecodeMethodSignature aSignatutre) {
        String theTypeDefinition = "(func";

        theTypeDefinition+= " (param ";
        theTypeDefinition+= WASMWriterUtils.toType(TypeRef.Native.REFERENCE);
        theTypeDefinition+=")";

        for (int i=0;i<aSignatutre.getArguments().length;i++) {
            BytecodeTypeRef theParamType = aSignatutre.getArguments()[i];
            theTypeDefinition+= " (param ";
            theTypeDefinition+=WASMWriterUtils.toType(TypeRef.toType(theParamType));
            theTypeDefinition+= ")";
        }

        if (!aSignatutre.getReturnType().isVoid()) {
            theTypeDefinition+= " (result "; // result
            theTypeDefinition+=WASMWriterUtils.toType(TypeRef.toType(aSignatutre.getReturnType()));
            theTypeDefinition+=")";
        }
        theTypeDefinition+= ")";
        return theTypeDefinition;
    }
}
