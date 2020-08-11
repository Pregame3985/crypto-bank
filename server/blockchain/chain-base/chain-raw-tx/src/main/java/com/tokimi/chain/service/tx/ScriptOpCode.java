package com.tokimi.chain.service.tx;

/**
 * @author william
 */
public interface ScriptOpCode {

    int OP_0 = 0x00; // push empty vector
    int OP_FALSE = OP_0;
    int OP_PUSHDATA1 = 0x4c;
    int OP_PUSHDATA2 = 0x4d;
    int OP_PUSHDATA4 = 0x4e;
    int OP_1NEGATE = 0x4f;
    int OP_RESERVED = 0x50;
    int OP_1 = 0x51;
    int OP_TRUE = OP_1;
    int OP_2 = 0x52;
    int OP_3 = 0x53;
    int OP_4 = 0x54;
    int OP_5 = 0x55;
    int OP_6 = 0x56;
    int OP_7 = 0x57;
    int OP_8 = 0x58;
    int OP_9 = 0x59;
    int OP_10 = 0x5a;
    int OP_11 = 0x5b;
    int OP_12 = 0x5c;
    int OP_13 = 0x5d;
    int OP_14 = 0x5e;
    int OP_15 = 0x5f;
    int OP_16 = 0x60;

    int OP_NOP = 0x61;
    int OP_VER = 0x62;
    int OP_IF = 0x63;
    int OP_NOTIF = 0x64;
    int OP_VERIF = 0x65;
    int OP_VERNOTIF = 0x66;
    int OP_ELSE = 0x67;
    int OP_ENDIF = 0x68;
    int OP_VERIFY = 0x69;
    int OP_RETURN = 0x6a;

    int OP_TOALTSTACK = 0x6b;
    int OP_FROMALTSTACK = 0x6c;
    int OP_2DROP = 0x6d;
    int OP_2DUP = 0x6e;
    int OP_3DUP = 0x6f;
    int OP_2OVER = 0x70;
    int OP_2ROT = 0x71;
    int OP_2SWAP = 0x72;
    int OP_IFDUP = 0x73;
    int OP_DEPTH = 0x74;
    int OP_DROP = 0x75;
    int OP_DUP = 0x76;
    int OP_NIP = 0x77;
    int OP_OVER = 0x78;
    int OP_PICK = 0x79;
    int OP_ROLL = 0x7a;
    int OP_ROT = 0x7b;
    int OP_SWAP = 0x7c;
    int OP_TUCK = 0x7d;

    int OP_CAT = 0x7e;
    int OP_SUBSTR = 0x7f;
    int OP_LEFT = 0x80;
    int OP_RIGHT = 0x81;
    int OP_SIZE = 0x82;

    int OP_INVERT = 0x83;
    int OP_AND = 0x84;
    int OP_OR = 0x85;
    int OP_XOR = 0x86;
    int OP_EQUAL = 0x87;
    int OP_EQUALVERIFY = 0x88;
    int OP_RESERVED1 = 0x89;
    int OP_RESERVED2 = 0x8a;

    int OP_1ADD = 0x8b;
    int OP_1SUB = 0x8c;
    int OP_2MUL = 0x8d;
    int OP_2DIV = 0x8e;
    int OP_NEGATE = 0x8f;
    int OP_ABS = 0x90;
    int OP_NOT = 0x91;
    int OP_0NOTEQUAL = 0x92;
    int OP_ADD = 0x93;
    int OP_SUB = 0x94;
    int OP_MUL = 0x95;
    int OP_DIV = 0x96;
    int OP_MOD = 0x97;
    int OP_LSHIFT = 0x98;
    int OP_RSHIFT = 0x99;
    int OP_BOOLAND = 0x9a;
    int OP_BOOLOR = 0x9b;
    int OP_NUMEQUAL = 0x9c;
    int OP_NUMEQUALVERIFY = 0x9d;
    int OP_NUMNOTEQUAL = 0x9e;
    int OP_LESSTHAN = 0x9f;
    int OP_GREATERTHAN = 0xa0;
    int OP_LESSTHANOREQUAL = 0xa1;
    int OP_GREATERTHANOREQUAL = 0xa2;
    int OP_MIN = 0xa3;
    int OP_MAX = 0xa4;
    int OP_WITHIN = 0xa5;

    int OP_RIPEMD160 = 0xa6;
    int OP_SHA1 = 0xa7;
    int OP_SHA256 = 0xa8;
    int OP_HASH160 = 0xa9;
    int OP_HASH256 = 0xaa;
    int OP_CODESEPARATOR = 0xab;
    int OP_CHECKSIG = 0xac;
    int OP_CHECKSIGVERIFY = 0xad;
    int OP_CHECKMULTISIG = 0xae;
    int OP_CHECKMULTISIGVERIFY = 0xaf;

    int OP_CHECKLOCKTIMEVERIFY = 0xb1;

    int OP_NOP1 = 0xb0;

    @Deprecated
    int OP_NOP2 = OP_CHECKLOCKTIMEVERIFY;
    int OP_NOP3 = 0xb2;
    int OP_NOP4 = 0xb3;
    int OP_NOP5 = 0xb4;
    int OP_NOP6 = 0xb5;
    int OP_NOP7 = 0xb6;
    int OP_NOP8 = 0xb7;
    int OP_NOP9 = 0xb8;
    int OP_NOP10 = 0xb9;
    int OP_INVALIDOPCODE = 0xff;
}
