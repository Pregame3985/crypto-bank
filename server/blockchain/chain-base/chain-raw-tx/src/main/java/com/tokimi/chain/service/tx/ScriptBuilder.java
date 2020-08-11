package com.tokimi.chain.service.tx;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * @author william
 */
public class ScriptBuilder {

    private List<ScriptChunk> chunks;

    public ScriptBuilder() {
        chunks = new LinkedList<>();
    }

    static Script createP2PKHOutputScript(byte[] hash) {
        checkArgument(hash.length == 20);
        return new ScriptBuilder().op(ScriptOpCode.OP_DUP).op(ScriptOpCode.OP_HASH160).data(hash).op(ScriptOpCode.OP_EQUALVERIFY).op(ScriptOpCode.OP_CHECKSIG).build();
    }

    static Script createP2SHOutputScript(byte[] hash) {
        checkArgument(hash.length == 20);
        return new ScriptBuilder().op(ScriptOpCode.OP_HASH160).data(hash).op(ScriptOpCode.OP_EQUAL).build();
    }

    static Script createRedeemScript(byte[] hash) {
        checkArgument(hash.length == 20);
        return new ScriptBuilder().op(ScriptOpCode.OP_0).data(hash).build();
    }

    static Script createScriptCode(byte[] hash) {
        return new ScriptBuilder().data(hash).build();
    }

    public static Script createSegwitRedeemScript(byte[] hash) {
        checkArgument(hash.length == 20);
        return new ScriptBuilder().data(hash).op(ScriptOpCode.OP_CHECKSIG).build();
    }

    static Script createOmniPayload(byte[] hash) {
        checkArgument(hash.length == 20);
        return new ScriptBuilder().op(ScriptOpCode.OP_RETURN).data(hash).build();
    }

    static void checkArgument(boolean expression) {
        if (!expression) {
            throw new IllegalArgumentException();
        }
    }

    public ScriptBuilder addChunk(ScriptChunk chunk) {
        return addChunk(chunks.size(), chunk);
    }

    private ScriptBuilder addChunk(int index, ScriptChunk chunk) {
        chunks.add(index, chunk);
        return this;
    }

    private ScriptBuilder op(int opcode) {
        return op(chunks.size(), opcode);
    }

    private ScriptBuilder op(int index, int opcode) {
        // TODO
//        checkArgument(opcode > OP_PUSHDATA4);
        return addChunk(index, new ScriptChunk(opcode, null));
    }

    public ScriptBuilder data(byte data) {

        byte[] newByte = new byte[1];
        newByte[0] = data;
        return data(1, newByte);
    }

    public ScriptBuilder data(byte[] data) {
        return data(chunks.size(), data);
    }

    public ScriptBuilder data(int index, byte[] data) {
        byte[] copy = Arrays.copyOf(data, data.length);
        int opcode;
        if (data.length == 0) {
            opcode = ScriptOpCode.OP_0;
        } else if (data.length == 1) {
            byte b = data[0];
            if (b >= 1 && b <= 16) {
                opcode = Script.encodeToOpN(b);
            } else {
                opcode = 1;
            }
        } else if (data.length < ScriptOpCode.OP_PUSHDATA1) {
            opcode = data.length;
        } else if (data.length < 256) {
            opcode = ScriptOpCode.OP_PUSHDATA1;
        } else if (data.length < 65536) {
            opcode = ScriptOpCode.OP_PUSHDATA2;
        } else {
            throw new RuntimeException("Unimplemented");
        }
        return addChunk(index, new ScriptChunk(opcode, copy));
    }

    public Script build() {
        return new Script(chunks);
    }
}
