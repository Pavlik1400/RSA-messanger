package com.example.diskret_project;

public class RSACipher {
    private long e;
    private long d;
    private long n;


    public RSACipher(long P, long Q, long E){
        // Save encoding parameters (n, e, d)
        e = E;
        this.n = P * Q;
        this.d = modularInverse(E, (P-1)*(Q-1));
    }

    public String encode(String message){
        char first_symbol;
        char second_symbol;
        if (message.length() % 2 == 1)
            message += " ";

        StringBuilder result = new StringBuilder();
        int length = message.length();
        for (int index = 0; index < length; index+=2){
            first_symbol = message.charAt(index);
            second_symbol = message.charAt(index + 1);

            if (!((int) first_symbol <= 255 && (int) second_symbol <= 255))
                return "not_ascii_input";

            result.append(encodeSymbol(first_symbol, second_symbol) + ",");
        }
        return result.toString();
    }

    public String encodeSymbol(char first_symbol, char second_symbol) {
        String first_string = String.valueOf((int) first_symbol);
        String second_string = String.valueOf((int) second_symbol);
        while (first_string.length() < 3)
            first_string = "0" + first_string;
        while (second_string.length() < 3)
            second_string = "0" + second_string;

        String encoded =  Long.toString(modExp(Long.parseLong(first_string + second_string), this.e, this.n));
        return encoded;
    }


    public String decode(String encodedMessage){
        StringBuilder result = new StringBuilder();
        String[] encoded_array = encodedMessage.split(",");
        for (String encoded : encoded_array) {
            long long_encoded = Long.parseLong(encoded);
            String decoded =Long.toString (modExp(long_encoded, this.d, this.n));

            char first_char = (char) Integer.parseInt(decoded.substring(0, decoded.length() - 3));
            char second_char = (char) Integer.parseInt(decoded.substring(decoded.length() - 3));
            result.append(first_char);
            result.append(second_char);
        }
        return result.toString();
    }


    static public long modularInverse(long num, long m)
    {
        num = num % m;
        for (int x = 1; x < m; x++)
            if ((num * x) % m == 1)
                return x;
        return 1;
    }


    private long modExp(long base, long exp, long module){
        String binary_exp = Long.toBinaryString(exp);
        long x = 1;
        long power = base % module;

        for (int k = binary_exp.length() - 1; k >= 0; --k){
            if (binary_exp.charAt(k) == '1')
                x = (x * power) % module;
            power = (power * power) % module;
        }
        return x;
    }
}
