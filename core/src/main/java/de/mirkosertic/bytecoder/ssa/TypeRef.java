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
package de.mirkosertic.bytecoder.ssa;

import de.mirkosertic.bytecoder.classlib.Address;
import de.mirkosertic.bytecoder.core.BytecodeArrayTypeRef;
import de.mirkosertic.bytecoder.core.BytecodeObjectTypeRef;
import de.mirkosertic.bytecoder.core.BytecodePrimitiveTypeRef;
import de.mirkosertic.bytecoder.core.BytecodeTypeRef;

public interface TypeRef {

    interface ArrayTypeRef extends TypeRef {

        BytecodeArrayTypeRef arrayType();
    }

    interface ObjectTypeRef extends TypeRef {

        BytecodeObjectTypeRef objectType();
    }

    Native resolve();

    boolean isArray();

    boolean isObject();

    enum Native implements TypeRef {
        UNKNOWN {
            @Override
            public Native eventuallyPromoteTo(Native aOtherType) {
                throw new IllegalStateException();
            }
        }
        ,BYTE {
            @Override
            public Native eventuallyPromoteTo(Native aOtherType) {
                switch (aOtherType) {
                    case BYTE:
                        return BYTE;
                    default:
                        throw new IllegalStateException("Don't know how to promote " + this + " to " + aOtherType);

                }
            }
        }
        ,SHORT {
            @Override
            public Native eventuallyPromoteTo(Native aOtherType) {
                switch (aOtherType) {
                    case SHORT:
                        return SHORT;
                    default:
                        throw new IllegalStateException("Don't know how to promote " + this + " to " + aOtherType);

                }
            }
        },CHAR {
            @Override
            public Native eventuallyPromoteTo(Native aOtherType) {
                switch (aOtherType) {
                    case CHAR:
                        return CHAR;
                    default:
                        throw new IllegalStateException("Don't know how to promote " + this + " to " + aOtherType);

                }
            }
        },BOOLEAN {
            @Override
            public Native eventuallyPromoteTo(Native aOtherType) {
                switch (aOtherType) {
                    case INT:
                        return INT;
                    case BOOLEAN:
                        return BOOLEAN;
                    default:
                        throw new IllegalStateException("Don't know how to promote " + this + " to " + aOtherType);

                }
            }
        },INT {
            @Override
            public Native eventuallyPromoteTo(Native aOtherType) {
                switch (aOtherType) {
                    case INT:
                        return INT;
                    case BOOLEAN:
                        return INT;
                    default:
                        throw new IllegalStateException("Don't know how to promote " + this + " to " + aOtherType);

                }
            }
        },LONG {
            @Override
            public Native eventuallyPromoteTo(Native aOtherType) {
                switch (aOtherType) {
                    case LONG:
                        return LONG;
                    default:
                        throw new IllegalStateException("Don't know how to promote " + this + " to " + aOtherType);

                }
            }
        },FLOAT {
            @Override
            public Native eventuallyPromoteTo(Native aOtherType) {
                switch (aOtherType) {
                    case FLOAT:
                        return FLOAT;
                    default:
                        throw new IllegalStateException("Don't know how to promote " + this + " to " + aOtherType);

                }
            }
        },DOUBLE {
            @Override
            public Native eventuallyPromoteTo(Native aOtherType) {
                switch (aOtherType) {
                    case DOUBLE:
                        return DOUBLE;
                    default:
                        throw new IllegalStateException("Don't know how to promote " + this + " to " + aOtherType);

                }
            }
        },REFERENCE {
            @Override
            public Native eventuallyPromoteTo(Native aOtherType) {
                switch (aOtherType) {
                    case REFERENCE:
                        return REFERENCE;
                    default:
                        throw new IllegalStateException("Don't know how to promote " + this + " to " + aOtherType);

                }
            }

            @Override
            public boolean isObject() {
                return true;
            }
        },VOID {
            @Override
            public Native eventuallyPromoteTo(Native aOtherType) {
                switch (aOtherType) {
                    case VOID:
                        return VOID;
                    default:
                        throw new IllegalStateException("Don't know how to promote " + this + " to " + aOtherType);

                }
            }
        };

        @Override
        public Native resolve() {
            return this;
        }

        @Override
        public boolean isArray() {
            return false;
        }

        @Override
        public boolean isObject() {
            return false;
        }

        public abstract Native eventuallyPromoteTo(Native aOtherType);
    }

    static TypeRef toType(final BytecodeTypeRef aTypeRef) {
        if (aTypeRef.isVoid()) {
            return Native.VOID;
        }
        if (aTypeRef.isArray()) {
            return new ArrayTypeRef() {
                @Override
                public BytecodeArrayTypeRef arrayType() {
                    return (BytecodeArrayTypeRef) aTypeRef;
                }

                @Override
                public boolean isArray() {
                    return true;
                }

                @Override
                public Native resolve() {
                    return Native.REFERENCE;
                }

                @Override
                public boolean isObject() {
                    return true;
                }
            };
        }
        if (aTypeRef.isPrimitive()) {
            BytecodePrimitiveTypeRef thePrimitive = (BytecodePrimitiveTypeRef) aTypeRef;
            switch (thePrimitive) {
            case INT:
                return Native.INT;
            case LONG:
                return Native.LONG;
            case FLOAT:
                return Native.FLOAT;
            case DOUBLE:
                return Native.DOUBLE;
            case BYTE:
                return Native.BYTE;
            case VOID:
                return Native.VOID;
            case CHAR:
                return Native.CHAR;
            case SHORT:
                return Native.SHORT;
            case BOOLEAN:
                return Native.BOOLEAN;
            default:
                throw new IllegalStateException("Not supported : " + aTypeRef);
            }
        }
        if (BytecodeObjectTypeRef.fromRuntimeClass(Address.class).equals(aTypeRef)) {
            return TypeRef.Native.INT;
        }
        return new ObjectTypeRef() {
            @Override
            public BytecodeObjectTypeRef objectType() {
                return (BytecodeObjectTypeRef) aTypeRef;
            }

            @Override
            public Native resolve() {
                return Native.REFERENCE;
            }

            @Override
            public boolean isArray() {
                return false;
            }

            @Override
            public boolean isObject() {
                return true;
            }
        };
    }
}
