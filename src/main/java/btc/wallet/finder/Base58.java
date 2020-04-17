package btc.wallet.finder;

import java.util.Arrays;

public class Base58 {

  public static final char[] ALPHABET =
      "123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz".toCharArray();
  private static final char ENCODED_ZERO = ALPHABET[0];
  private static final int[] INDEXES = new int[128];

  static {
    Arrays.fill(INDEXES, -1);
    for (int i = 0; i < ALPHABET.length; i++) {
      INDEXES[ALPHABET[i]] = i;
    }
  }

  public static String encode(byte[] input) {
    if (input.length == 0) {
      return "";
    }
    int zeros = 0;
    while (zeros < input.length && input[zeros] == 0) {
      ++zeros;
    }
    input = Arrays.copyOf(input, input.length);
    char[] encoded = new char[input.length * 2];
    int outputStart = encoded.length;
    for (int inputStart = zeros; inputStart < input.length;) {
      encoded[--outputStart] = ALPHABET[divmod(input, inputStart, 256, 58)];
      if (input[inputStart] == 0) {
        ++inputStart;
      }
    }

    while (outputStart < encoded.length && encoded[outputStart] == ENCODED_ZERO) {
      ++outputStart;
    }
    while (--zeros >= 0) {
      encoded[--outputStart] = ENCODED_ZERO;
    }

    return new String(encoded, outputStart, encoded.length - outputStart);
  }

  public static byte[] decode(String input) throws Exception {
    if (input.length() == 0) {
      return new byte[0];
    }

    byte[] input58 = new byte[input.length()];
    for (int i = 0; i < input.length(); ++i) {
      char c = input.charAt(i);
      int digit = c < 128 ? INDEXES[c] : -1;
      if (digit < 0) {
        throw new Exception("Invalid Character: " + c + ", " + i);
      }
      input58[i] = (byte) digit;
    }

    int zeros = 0;
    while (zeros < input58.length && input58[zeros] == 0) {
      ++zeros;
    }

    byte[] decoded = new byte[input.length()];
    int outputStart = decoded.length;
    for (int inputStart = zeros; inputStart < input58.length;) {
      decoded[--outputStart] = divmod(input58, inputStart, 58, 256);
      if (input58[inputStart] == 0) {
        ++inputStart;
      }
    }

    while (outputStart < decoded.length && decoded[outputStart] == 0) {
      ++outputStart;
    }

    return Arrays.copyOfRange(decoded, outputStart - zeros, decoded.length);
  }

  private static byte divmod(byte[] number, int firstDigit, int base, int divisor) {

    int remainder = 0;
    for (int i = firstDigit; i < number.length; i++) {
      int digit = (int) number[i] & 0xFF;
      int temp = remainder * base + digit;
      number[i] = (byte) (temp / divisor);
      remainder = temp % divisor;
    }
    return (byte) remainder;
  }
}
