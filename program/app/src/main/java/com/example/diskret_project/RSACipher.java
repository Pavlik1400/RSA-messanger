package com.example.diskret_project;


import java.math.BigInteger;

public class RSACipher {
    private BigInteger e;
    private BigInteger d;
    private BigInteger n;


    public RSACipher(BigInteger P, BigInteger Q, BigInteger E){
        // Save encoding parameters (n, e, d)
        e = E;
        this.n = P.multiply(Q);
        this.d = E.modInverse(P.subtract(BigInteger.ONE).multiply(Q.subtract(BigInteger.ONE)));
        System.out.println(e.toString() + ", " + n.toString() + ", " + d.toString());
    }

    /**
     * Encodes given message with RSA and given p, q, e
     * @param message - message for encoding
     * @return encoded message
     */
    public String encode(String message){
        // encoding encoded pairs of symbol, so initialize
        // two chars
        char first_symbol;
        char second_symbol;

        // add space (" ") if length of message is odd
        if (message.length() % 2 == 1)
            message += " ";

        StringBuilder result = new StringBuilder();
        int length = message.length();

        // iterate through all pairs of symbols
        for (int index = 0; index < length; index+=2){
            // get first and second symbol
            first_symbol = message.charAt(index);
            second_symbol = message.charAt(index + 1);

            // If symbol is not in ascii table than return message with this
            if (!((int) first_symbol <= 255 && (int) second_symbol <= 255))
                return "not_ascii_input";

            // add encoded pair of symbol to result String
            result.append(encodeSymbol(first_symbol, second_symbol) + ",");
        }
        return result.toString();
    }

    /**
     * Encodes pair of symbol with RSA cipher
     * @param first_symbol first symbol from pair
     * @param second_symbol second symbol from pair
     * @return encoded String
     */
    public String encodeSymbol(char first_symbol, char second_symbol) {
        // Convert symbols to their number in ascii table
        String first_string = String.valueOf((int) first_symbol);
        String second_string = String.valueOf((int) second_symbol);

        // add zero before second number, so that its length is three
        while (second_string.length() < 3)
            second_string = "0" + second_string;

        // add Strings, convert to long number, get it to the power of e by n mod and return
        return modExp(new BigInteger(first_string + second_string),
                this.e, this.n).toString();
    }

    /**
     * Decodes given message with RSA
     * @param encodedMessage - message for decoding
     * @return - decoded String
     */
    public String decode(String encodedMessage){
        String decoded;
        BigInteger long_encoded;
        StringBuilder result = new StringBuilder();
        String[] encoded_array = encodedMessage.split(",");
        for (String encoded : encoded_array) {
            try {
                long_encoded = new BigInteger(encoded);
                decoded = modExp(long_encoded, this.d, this.n).toString();
            } catch (NumberFormatException e){
                return "Wrong encoded message";
            }

            char first_char = (char) Integer.parseInt(decoded.substring(0, decoded.length() - 3));
            char second_char = (char) Integer.parseInt(decoded.substring(decoded.length() - 3));
            result.append(first_char);
            result.append(second_char);
        }
        return result.toString();
    }

    /**
     * raises number to the power by mod
     * @param base - base that will be raised
     * @param exp - exponent
     * @param module - module
     * @return
     */
    private BigInteger modExp(BigInteger base, BigInteger exp, BigInteger module){
        //
        String binary_exp = exp.toString(2);
        BigInteger x = BigInteger.ONE;
        BigInteger power = base.mod(module);

        for (int k = binary_exp.length() - 1; k >= 0; --k){
            if (binary_exp.charAt(k) == '1')
                x = (x.multiply(power)).mod(module);
            power = (power.multiply(power)).mod(module);
        }
        return x;
    }
}

//public class RSACipher {
//    private long e;
//    private long d;
//    private long n;
//
//
//    public RSACipher(long P, long Q, long E){
//        // Save encoding parameters (n, e, d)
//        e = E;
//        this.n = P * Q;
//        this.d = modularInverse(E, (P-1)*(Q-1));
//    }
//
//    /**
//     * Encodes given message with RSA and given p, q, e
//     * @param message - message for encoding
//     * @return encoded message
//     */
//    public String encode(String message){
//        // encoding encoded pairs of symbol, so initialize
//        // two chars
//        char first_symbol;
//        char second_symbol;
//
//        // add space (" ") if length of message is odd
//        if (message.length() % 2 == 1)
//            message += " ";
//
//        StringBuilder result = new StringBuilder();
//        int length = message.length();
//
//        // iterate through all pairs of symbols
//        for (int index = 0; index < length; index+=2){
//            // get first and second symbol
//            first_symbol = message.charAt(index);
//            second_symbol = message.charAt(index + 1);
//
//            // If symbol is not in ascii table than return message with this
//            if (!((int) first_symbol <= 255 && (int) second_symbol <= 255))
//                return "not_ascii_input";
//
//            // add encoded pair of symbol to result String
//            result.append(encodeSymbol(first_symbol, second_symbol) + ",");
//        }
//        return result.toString();
//    }
//
//    /**
//     * Encodes pair of symbol with RSA cipher
//     * @param first_symbol first symbol from pair
//     * @param second_symbol second symbol from pair
//     * @return encoded String
//     */
//    public String encodeSymbol(char first_symbol, char second_symbol) {
//        // Convert symbols to their number in ascii table
//        String first_string = String.valueOf((int) first_symbol);
//        String second_string = String.valueOf((int) second_symbol);
//
//        // add zero before second number, so that its length is three
//        while (second_string.length() < 3)
//            second_string = "0" + second_string;
//
//        // add Strings, convert to long number, get it to the power of e by n mod and return
//        return Long.toString(modExp(Long.parseLong(first_string + second_string),
//                this.e, this.n));
//    }
//
//    /**
//     * Decodes given message with RSA
//     * @param encodedMessage - message for decoding
//     * @return - decoded String
//     */
//    public String decode(String encodedMessage){
//        String decoded;
//        long long_encoded;
//        StringBuilder result = new StringBuilder();
//        String[] encoded_array = encodedMessage.split(",");
//        for (String encoded : encoded_array) {
//            try {
//                long_encoded = Long.parseLong(encoded);
//                decoded = Long.toString(modExp(long_encoded, this.d, this.n));
//            } catch (NumberFormatException e){
//                return "Wrong encoded message";
//            }
//
//            char first_char = (char) Integer.parseInt(decoded.substring(0, decoded.length() - 3));
//            char second_char = (char) Integer.parseInt(decoded.substring(decoded.length() - 3));
//            result.append(first_char);
//            result.append(second_char);
//        }
//        return result.toString();
//    }
//
//    /**
//     * Returns inverse number by given module
//     * @param num - num you for what you want to find inverse
//     * @param m - module
//     * @return - long inverse number
//     */
//    static public long modularInverse(long num, long m)
//    {
//        num = num % m;
//        for (int x = 1; x < m; x++)
//            if ((num * x) % m == 1)
//                return x;
//        return 1;
//    }
//
//    /**
//     * raises number to the power by mod
//     * @param base - base that will be raised
//     * @param exp - exponent
//     * @param module - module
//     * @return
//     */
//    private long modExp(long base, long exp, long module){
//        //
//        String binary_exp = Long.toBinaryString(exp);
//        long x = 1;
//        long power = base % module;
//
//        for (int k = binary_exp.length() - 1; k >= 0; --k){
//            if (binary_exp.charAt(k) == '1')
//                x = (x * power) % module;
//            power = (power * power) % module;
//        }
//        return x;
//    }
//}
