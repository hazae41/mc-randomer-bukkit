package hazae41.randomer;

public class Random {
  static int getInt(int min, int max) {
    return min + new java.util.Random().nextInt(max - min + 1);
  }

  static boolean getBoolean() {
    return new java.util.Random().nextBoolean();
  }
}
