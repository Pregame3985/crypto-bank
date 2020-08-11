package com.tokimi.config;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.web3j.abi.TypeDecoder;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.generated.Uint256;

import javax.xml.bind.DatatypeConverter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author william
 */
@Slf4j
public class PubSubConfigTest {

    public static String ASCIItoHEX(String ascii) {
        // Initialize final String
        String hex = "";

        // Make a loop to iterate through
        // every character of ascii string
        for (int i = 0; i < ascii.length(); i++) {

            // take a char from
            // position i of string
            char ch = ascii.charAt(i);

            // cast char to integer and
            // find its ascii value
            int in = (int) ch;

            // change this ascii value
            // integer to hexadecimal value
            String part = Integer.toHexString(in);

            // add this hexadecimal value
            // to final string.
            hex += part;
        }
        // return the final string hex
        return hex;
    }

    @Test
    public void test() {
        log.info(ASCIItoHEX("56bc75e2d63100000"));

    }

    private static String asciiToHex(String asciiStr) {
        char[] chars = asciiStr.toCharArray();
        StringBuilder hex = new StringBuilder();
        for (char ch : chars) {
            hex.append(Integer.toHexString((int) ch));
        }

        return hex.toString();
    }

    private static String hexToAscii(String hexStr) {
        StringBuilder output = new StringBuilder("");

        for (int i = 0; i < hexStr.length(); i += 2) {
            String str = hexStr.substring(i, i + 2);
            output.append((char) Integer.parseInt(str, 16));
        }

        return output.toString();
    }

    public static void main(String[] args) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        String hex = "56bc75e2d631000000";
        byte[] s = DatatypeConverter.parseHexBinary(hex);
        System.out.println(new String(s));

//        TypeReference<Uint256> typeReference = };

//        TypeReference.StaticArrayTypeReference.

        String inputdata = "0xa9059cbb0000000000000000000000005c9bd53a924e62e3ba6793640ab63cedc59ad6410000000000000000000000000000000000000000000000056bc75e2d63100000";
        String to = inputdata.substring(10, 74);
        String value = inputdata.substring(74, 138);

        Method refMethod = TypeDecoder.class.getDeclaredMethod("decode", String.class, int.class, Class.class);
        refMethod.setAccessible(true);

        Address cadena = (Address) refMethod.invoke(null, to, 0, Address.class);
        System.out.println(cadena);
        Uint256 amount = (Uint256) refMethod.invoke(null, value, 0, Uint256.class);
        System.out.println(amount.getValue());

//        TypeReference<StaticArray> typeReference = TypeReference.create(StaticArray.class);
//
//        TypeDecoder.decode(value, 0, Uint256.class);
//                typeReference
//        );
//
//        log.info(new String(s));
        log.info("ok");
    }

//    public class UintTypeReference extends TypeReference<>
}