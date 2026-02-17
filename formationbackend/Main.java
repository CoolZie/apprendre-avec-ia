import java.util.Arrays;

public class Main {
    public static void main(String[] args) {
        fixProblem();
    }

    public static void fixProblem() {
        int diviseur = 0;
        int trNbre = 1;
        for (int i = 1;; i++) {
            diviseur = 0;
            trNbre += i + 1;
            for (int j = 1; j * j <= trNbre; j++) {
                if (trNbre % j == 0) {
                    if (trNbre == j * j) {
                        diviseur++;
                    } else {
                        diviseur += 2;
                    }
                }
            }
            if (diviseur > 500) {
                break;
            }
        }
        System.out.println(trNbre);
    }

}

/*
 * 1
 * 1+2=3
 * 3+3=6
 * 6+4=10
 * 10+5=15
 */