/*
 * Copyright 2018 Mirko Sertic
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
package de.mirkosertic.bytecoder.api.opencl;

import java.nio.FloatBuffer;

@OpenCLType(name = "float2", elementCount = 2)
public class Vec2f implements FloatSerializable {

    public float s1;
    public float s2;

    public Vec2f(float aS1, float aS2) {
        s1 = aS1;
        s2 = aS2;
    }

    @Override
    public void writeTo(FloatBuffer aBuffer) {
        aBuffer.put(s1).put(s2);
    }

    @Override
    public void readFrom(FloatBuffer aBuffer) {
        s1 = aBuffer.get();
        s2 = aBuffer.get();
    }

    @Override
    public String toString() {
        return "Vec2f{" +
                "s1=" + s1 +
                ", s2=" + s2 +
                '}';
    }

    Vec2f normalize() {
        return this;
    }

    float length() {
        float theSquareSum = 0.0f;
        theSquareSum += s1 * s1;
        theSquareSum += s2 * s2;
        return (float) Math.sqrt(theSquareSum);

    }

    Vec2f cross(Vec2f aOtherVector) {
        return new Vec2f(
            s1 * aOtherVector.s1,
            s2 * aOtherVector.s2
        );
    }

    float dot(Vec2f aOtherVector) {
        float theDotProduct = 0.0f;
        theDotProduct += s1 * aOtherVector.s1;
        theDotProduct += s2 * aOtherVector.s2;
        return theDotProduct;
    }
}