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
package de.mirkosertic.bytecoder.core;

public class BytecodeInstructionTABLESWITCH implements BytecodeInstruction {

    private final long defaultValue;
    private final long lowValue;
    private final long highValue;
    private final long offsets[];

    public BytecodeInstructionTABLESWITCH(long defaultValue, long lowValue, long highValue, long[] offsets) {
        this.defaultValue = defaultValue;
        this.lowValue = lowValue;
        this.highValue = highValue;
        this.offsets = offsets;
    }
}