package com.tokimi.chain.service.tx;

import lombok.Getter;
import lombok.Setter;
import org.bitcoinj.core.Address;
import org.bitcoinj.core.Utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Script {

    static final long MAX_SCRIPT_ELEMENT_SIZE = 520;  // bytes

    private List<ScriptChunk> chunks;
    private byte[] program;

    @Getter
    @Setter
    private long creationTimeSeconds;

    Script(List<ScriptChunk> chunks) {
        this.chunks = Collections.unmodifiableList(new ArrayList<>(chunks));
        creationTimeSeconds = Utils.currentTimeSeconds();
    }

    static int encodeToOpN(int value) {
        checkArgument(value >= -1 && value <= 16, "encodeToOpN called for " + value + " which we cannot encode in an opcode.");
        if (value == 0) {
            return ScriptOpCode.OP_0;
        } else if (value == -1) {
            return ScriptOpCode.OP_1NEGATE;
        } else {
            return value - 1 + ScriptOpCode.OP_1;
        }
    }

    static void checkArgument(boolean expression, Object errorMessage) {
        if (!expression) {
            throw new IllegalArgumentException(String.valueOf(errorMessage));
        }
    }

    @Override
    public String toString() {
        return Utils.join(chunks);
    }

    byte[] getProgram() {
        try {
            if (program != null) {
                return Arrays.copyOf(program, program.length);
            }
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            for (ScriptChunk chunk : chunks) {
                chunk.write(bos);
            }
            program = bos.toByteArray();
            return program;
        } catch (IOException e) {
            throw new RuntimeException(e);  // Cannot happen.
        }
    }

    public List<ScriptChunk> getChunks() {
        return Collections.unmodifiableList(chunks);
    }

    public boolean isSentToAddress() {
        return chunks.size() == 5
                && chunks.get(0).equalsOpCode(ScriptOpCode.OP_DUP)
                && chunks.get(1).equalsOpCode(ScriptOpCode.OP_HASH160)
                && chunks.get(2).data.length == Address.LENGTH
                && chunks.get(3).equalsOpCode(ScriptOpCode.OP_EQUALVERIFY)
                && chunks.get(4).equalsOpCode(ScriptOpCode.OP_CHECKSIG);
    }

    public byte[] getPubKeyHash() {
        if (isSentToAddress()) {
            return chunks.get(2).data;
        } else if (isPayToScriptHash()) {
            return chunks.get(1).data;
        } else {
            throw new IllegalStateException("Script not in the standard scriptPubKey form");
        }
    }

    private boolean isPayToScriptHash() {
        byte[] program = getProgram();
        return program.length == 23 &&
                (program[0] & 0xff) == ScriptOpCode.OP_HASH160 &&
                (program[1] & 0xff) == 0x14 &&
                (program[22] & 0xff) == ScriptOpCode.OP_EQUAL;
    }

    public boolean isOpReturn() {
        return chunks.size() > 0 && chunks.get(0).equalsOpCode(ScriptOpCode.OP_RETURN);
    }

    private byte[] getQuickProgram() {
        if (program != null) {
            return program;
        }
        return getProgram();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        return Arrays.equals(getQuickProgram(), ((Script) o).getQuickProgram());
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(getQuickProgram());
    }
}
