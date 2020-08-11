package com.tokimi.chain.service.tx;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;

public class ScriptChunk {

    public final byte[] data;
    private final int opcode;
    private int startLocationInProgram;

    ScriptChunk(int opcode, byte[] data) {
        this(opcode, data, -1);
    }

    private ScriptChunk(int opcode, byte[] data, int startLocationInProgram) {
        this.opcode = opcode;
        this.data = data;
        this.startLocationInProgram = startLocationInProgram;
    }

    static void writeUint32ToStream(long val, OutputStream stream) throws IOException {
        stream.write((int) (0xFF & val));
        stream.write((int) (0xFF & (val >> 8)));
        stream.write((int) (0xFF & (val >> 16)));
        stream.write((int) (0xFF & (val >> 24)));
    }

    static void checkState(boolean expression) {
        if (!expression) {
            throw new IllegalStateException();
        }
    }

    boolean equalsOpCode(int opcode) {
        return opcode == this.opcode;
    }

    private boolean isOpCode() {
        return opcode > ScriptOpCode.OP_PUSHDATA4;
    }

    void write(OutputStream stream) throws IOException {
        if (isOpCode()) {
            checkState(data == null);
            stream.write(opcode);
        } else if (data != null) {
            if (opcode < ScriptOpCode.OP_PUSHDATA1) {
                checkState(data.length == opcode);
                stream.write(opcode);
            } else if (opcode == ScriptOpCode.OP_PUSHDATA1) {
                checkState(data.length <= 0xFF);
                stream.write(ScriptOpCode.OP_PUSHDATA1);
                stream.write(data.length);
            } else if (opcode == ScriptOpCode.OP_PUSHDATA2) {
                checkState(data.length <= 0xFFFF);
                stream.write(ScriptOpCode.OP_PUSHDATA2);
                stream.write(0xFF & data.length);
                stream.write(0xFF & (data.length >> 8));
            } else if (opcode == ScriptOpCode.OP_PUSHDATA4) {
                checkState(data.length <= Script.MAX_SCRIPT_ELEMENT_SIZE);
                stream.write(ScriptOpCode.OP_PUSHDATA4);
                writeUint32ToStream(data.length, stream);
            } else {
                throw new RuntimeException("Unimplemented");
            }
            stream.write(data);
        } else {
            stream.write(opcode); // smallNum
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ScriptChunk other = (ScriptChunk) o;
        return opcode == other.opcode && startLocationInProgram == other.startLocationInProgram
                && Arrays.equals(data, other.data);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(new Object[]{opcode, startLocationInProgram, Arrays.hashCode(data)});
    }
}
