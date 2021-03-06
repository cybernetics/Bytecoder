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
package de.mirkosertic.bytecoder.backend;

import java.lang.reflect.Method;

import de.mirkosertic.bytecoder.backend.js.JSSSACompilerBackend;
import de.mirkosertic.bytecoder.backend.js.JSWriterUtils;
import de.mirkosertic.bytecoder.backend.wasm.WASMSSACompilerBackend;
import de.mirkosertic.bytecoder.classlib.java.lang.TClass;
import de.mirkosertic.bytecoder.classlib.java.lang.TObject;
import de.mirkosertic.bytecoder.classlib.java.lang.invoke.TCallSite;
import de.mirkosertic.bytecoder.classlib.java.lang.invoke.TMethodHandle;
import de.mirkosertic.bytecoder.core.BytecodeArrayTypeRef;
import de.mirkosertic.bytecoder.core.BytecodeLinkedClass;
import de.mirkosertic.bytecoder.core.BytecodeLinkerContext;
import de.mirkosertic.bytecoder.core.BytecodeLoader;
import de.mirkosertic.bytecoder.core.BytecodeMethodSignature;
import de.mirkosertic.bytecoder.core.BytecodeObjectTypeRef;
import de.mirkosertic.bytecoder.core.BytecodePackageReplacer;
import de.mirkosertic.bytecoder.core.BytecodePrimitiveTypeRef;
import de.mirkosertic.bytecoder.core.BytecodeTypeRef;
import de.mirkosertic.bytecoder.core.Logger;
import de.mirkosertic.bytecoder.ssa.NaiveProgramGenerator;

public class CompileTarget {

    public static enum BackendType {
        js {
            @Override
            public CompileBackend createBackend() {
                return new JSSSACompilerBackend(NaiveProgramGenerator.FACTORY);
            }
        },
        wasm {
            @Override
            public CompileBackend createBackend() {
                return new WASMSSACompilerBackend(NaiveProgramGenerator.FACTORY);
            }
        };

        public abstract CompileBackend createBackend();
    }

    private final CompileBackend backend;
    private final BytecodeLoader bytecodeLoader;

    public CompileTarget(ClassLoader aClassLoader, BackendType aType) {
        backend = aType.createBackend();
        bytecodeLoader = new BytecodeLoader(aClassLoader, new BytecodePackageReplacer());
    }

    public String generatedFileName() {
        return backend.generatedFileName();
    }

    public CompileResult compileToJS(CompileOptions aOptions, Class aClass, String aMethodName, BytecodeMethodSignature aSignature) {
        BytecodeLinkerContext theLinkerContext = new BytecodeLinkerContext(bytecodeLoader, aOptions.getLogger());

        BytecodeLinkedClass theClassLinkedCass = theLinkerContext.linkClass(BytecodeObjectTypeRef.fromRuntimeClass(TClass.class));
        theClassLinkedCass.linkConstructorInvocation(new BytecodeMethodSignature(
                BytecodePrimitiveTypeRef.VOID, new BytecodeTypeRef[] {}));

        // Lambda handling
        BytecodeLinkedClass theCallsite = theLinkerContext.linkClass(BytecodeObjectTypeRef.fromRuntimeClass(TCallSite.class));
        theCallsite.linkVirtualMethod("getTarget", new BytecodeMethodSignature(BytecodeObjectTypeRef.fromRuntimeClass(
                TMethodHandle.class), new BytecodeTypeRef[0]));

        BytecodeLinkedClass theMethodHandle = theLinkerContext.linkClass(BytecodeObjectTypeRef.fromRuntimeClass(TMethodHandle.class));
        theMethodHandle.linkVirtualMethod("invokeExact", new BytecodeMethodSignature(BytecodeObjectTypeRef.fromRuntimeClass(TObject.class),
                new BytecodeTypeRef[] {new BytecodeArrayTypeRef(BytecodeObjectTypeRef.fromRuntimeClass(TObject.class), 1)}));

        BytecodeObjectTypeRef theTypeRef = BytecodeObjectTypeRef.fromRuntimeClass(aClass);

        theLinkerContext.linkClass(theTypeRef).linkStaticMethod(aMethodName, aSignature);

        return backend.generateCodeFor(aOptions, theLinkerContext, aClass, aMethodName, aSignature);
    }

    public String toClassName(BytecodeObjectTypeRef aTypeRef) {
        return JSWriterUtils.toClassName(aTypeRef);
    }

    public String toMethodName(String aName, BytecodeMethodSignature aSignature) {
        return JSWriterUtils.toMethodName(aName, aSignature);
    }

    public BytecodeMethodSignature toMethodSignature(Method aMethod) {
        return bytecodeLoader.getSignatureParser().toMethodSignature(aMethod);
    }
}