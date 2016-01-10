import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.stream.IntStream;

/**
 * Rafal on 11/28/2015.
 */
public class Sandbox {

    public int solution(int X, int Y, int D) {
        if (X > Y || D < 0) {
            throw new RuntimeException("Assumptions not met.");
        }
        int odd = Y - X;
        if (odd % D == 0) {
            return odd / D;
        }
        if (odd % D != 0) {
            return odd / D + 1;
        }
        throw new RuntimeException("Sth went wrong.");
    }

    public int findMissingElementInPermutation(int[] A) {
        int currentSum = Arrays.stream(A).sum();
        int shouldBe = IntStream.range(1, A.length + 2).sum();
        return shouldBe - currentSum;
    }

    public int isPermutation(int[] A) {
        Map<Integer, Boolean> integerBoolMap = new HashMap<>();
        for (int i : A) {
            integerBoolMap.put(i, true);
        }
        for (int i = 1; i < A.length + 1; i++) {
            if (!integerBoolMap.getOrDefault(i, false))
                return 0;
        }
        return 1;
    }

    public int frogAndRiver(int X, int[] A) {
        int[] posToTime = new int[X];
        Arrays.fill(posToTime, Integer.MAX_VALUE);
        for (int i = 0; i < A.length; i++) {
            if (posToTime[A[i] - 1] > i) {
                posToTime[A[i] - 1] = i;
            }
        }
        int i = 0;
        for (i = 0; i < X; i++) {
            if (posToTime[i] == Integer.MAX_VALUE) {
                return -1;
            }
        }
        return posToTime[i - 1] != Integer.MAX_VALUE ? posToTime[i - 1] : -1;
    }

    public int[] counters(int N, int[] A) {
        int possibleMax = 0;
        int currentMaxCounter = 0;
        int[] counters = new int[N];
        for (int i = 0; i < A.length; i++) {
            int operation = A[i];
            if (operation >= 1 && operation <= N) {
                counters[operation - 1] = Math.max(counters[operation - 1], currentMaxCounter);
                counters[operation - 1]++;
                possibleMax = Math.max(possibleMax, counters[operation - 1]);
            } else if (operation > N) {
                currentMaxCounter = possibleMax;
            } else {
                throw new RuntimeException("Unexpected operation.");
            }
        }
        for (int i = 0; i < N; i++) {
            counters[i] = Math.max(counters[i], currentMaxCounter);
        }
        return counters;
    }


    public int missingInteger(int A[]) {
        boolean[] check = new boolean[A.length + 1];
        for (int i : A) {
            if (i > 0 && i <= A.length) {
                check[i] = true;
            }
        }
        for (int i = 1; i < check.length; i++) {
            if (!check[i]) {
                return i;
            }
        }
        return check.length;
    }

    public int findPassingCars(int A[]) {
        final int MAX_CARS_PAIR = 1000000000;
        int goingWestCars = 0;
        int passingPairs = 0;
        for (int i = A.length - 1; i >= 0; i--) {
            if (A[i] == 1) {
                goingWestCars++;
            } else if (A[i] == 0 && passingPairs < MAX_CARS_PAIR) {
                passingPairs += goingWestCars;
                if (passingPairs > MAX_CARS_PAIR)
                    return -1;
            }
        }

        return passingPairs > MAX_CARS_PAIR ? -1 : passingPairs;
    }

    public int findSmallestStartOfSlice(int[] A) {
        int minIndex = 0;
        if (A.length < 1) {
            throw new RuntimeException("Incorrect input.");
        }
        int minValue = Integer.MAX_VALUE;
        for (int i = 0; i < A.length - 1; i++) {
            if ((A[i] + A[i + 1]) / 2 < minValue) {
                minIndex = i;
                minValue = (A[i] + A[i + 1]) / 2;
            } else if (i < A.length - 2 && (A[i] + A[i + 1] + A[i + 2]) / 3 < minValue) {
                minIndex = i;
                minValue = (A[i] + A[i + 1] + A[i + 2]) / 3;
            }
        }

        return minIndex;
    }

    public int countDiv(int A, int B, int K) {
        if (K < 1)
            throw new RuntimeException("K not in correct range.");
        if (B - A < 0)
            throw new RuntimeException("Wrong range of A and B.");
        return B / K - A / K + (A % K == 0 ? 1 : 0);
    }

    public int isThereTriangle(int[] A) {
        Arrays.sort(A);
        if (A.length < 3)
            throw new RuntimeException("Not enough points.");

        for (int i = 0; i < A.length - 2; i++) {
            if (A[i] + A[i + 1] > A[i + 2])
                return 1;
        }
        return 0;
    }

    public int distinctValues(int[] A) {
        return Arrays.stream(A).distinct().toArray().length;
    }

    public int isProperlyNested(String S) {
        if (S.length() == 0) {
            return 1;
        }
        Stack<String> stack = new Stack<>();
        for (int i = 0; i < S.length(); i++) {
            char current = S.charAt(i);
            if (isStarting(String.valueOf(current))) {
                stack.add(String.valueOf(current));
            } else {
                if (stack.isEmpty())
                    return 0;
                else {
                    if (!isValidPair(stack.pop(), String.valueOf(current)))
                        return 0;
                }
            }
        }
        if (!stack.isEmpty())
            return 0;

        return 1;
    }

    private boolean isStarting(String current) {
        return "(".equals(current) || "[".equals(current) || "{".equals(current);
    }

    private boolean isValidPair(String pop, String s) {
        switch (pop) {
            case "{":
                return "}".equals(s);
            case "(":
                return ")".equals(s);
            case "[":
                return "]".equals(s);
            default:
                return false;
        }
    }

    private int aliveFishProblem(int A[], int B[]) {
        if (A.length == 0 && B.length == 0 && A.length != B.length)
            throw new RuntimeException("Wrong input.");

        Stack<Integer> downstreamStack = new Stack<>();
        int survived = 0;
        for (int i = 0; i < A.length; i++) {
            if (B[i] == 0) {
                while (!downstreamStack.isEmpty()) {
                    if (A[downstreamStack.peek()] > A[i]) {
                        break;
                    } else {
                        downstreamStack.pop();
                    }
                }
                if (downstreamStack.isEmpty()) {
                    survived++;
                }
            } else {
                downstreamStack.push(i);
            }
        }
        return downstreamStack.size() + survived;
    }

    public int dominator(int A[]) {
        if (A == null && A.length < 1) {
            return -1;
        }
        Stack<Integer> integerStack = new Stack<>();
        for (int i = 0; i < A.length; i++) {
            if (!integerStack.isEmpty() && integerStack.peek() != A[i]) {
                integerStack.pop();
            } else {
                integerStack.push(A[i]);
            }
        }
        int candidate = -1;
        if (!integerStack.isEmpty()) {
            candidate = integerStack.peek();
        }
        int count = 0;
        int lastPos = -1;
        for (int i = 0; i < A.length; i++) {
            if (count <= A.length / 2 && A[i] == candidate) {
                count++;
                lastPos = i;
            }
        }
        return count >= A.length / 2 ? lastPos : -1;
    }

    public int equiLeader(int A[]) {
        int dominator = dominator(A);
        if (dominator == -1) {
            return 0;
        }
        int beforeNow = 0;
        int equiDominators = 0;
        int count = 0;
        int candidate = A[dominator];
        for (int i = 0; i < A.length; i++) {
            if (A[i] == candidate) {
                count++;
            }
        }

        for (int i = 0; i < A.length; i++) {
            int remaining = count - beforeNow;
            if (beforeNow > i / 2 && remaining > (A.length - i) / 2) {
                System.out.println(i - 1);
                equiDominators++;
            }
            if (A[i] == A[dominator]) {
                beforeNow++;
            }

        }
        return equiDominators;
    }

    public int maxDoubleSliceSum(int A[]) {
        int endingHere[] = new int[A.length + 1];
        int startingHere[] = new int[A.length + 1];
        int maxSum = 0;
        for (int i = 1; i < A.length; i++) {
            maxSum = Math.max(0, maxSum + A[i]);
            endingHere[i] = maxSum;
        }
        maxSum = 0;
        for (int i = A.length - 2; i > 0; i--) {
            maxSum = Math.max(0, maxSum + A[i]);
            startingHere[i] = maxSum;
        }
        int maxDoubleSlice = 0;
        for (int i = 0; i < A.length - 2; i++) {
            maxDoubleSlice = Math.max(maxDoubleSlice, startingHere[i + 2] + endingHere[i]);
        }
        return maxDoubleSlice;
    }

    public static void main(String[] args) {
        Sandbox solution = new Sandbox();

        System.out.println(solution.maxDoubleSliceSum(new int[]{3, 2, 6, -1, 4, 5, -1, 2}));
        System.out.println(solution.maxDoubleSliceSum(new int[]{5, 5, 5}));
        System.out.println(solution.maxDoubleSliceSum(new int[]{5, 5, 5, 5}));
        System.out.println(solution.maxDoubleSliceSum(new int[]{5, 5, 5, -1, 5}));
    }
}
