import java.util.*;

public class Main {
    private static final String ROUTE_TEMPLATE = "RLRFR";
    private static final int ROUTE_LENGTH = 100;
    private static final int ROUTE_COUNT = 1000;
    public static final Map<Integer, Integer> sizeToFreq = new HashMap<>();

    public static String generateRoute(String letters, int length) {
        Random random = new Random();
        StringBuilder route = new StringBuilder();
        for (int i = 0; i < length; i++) {
            route.append(letters.charAt(random.nextInt(letters.length())));
        }
        return route.toString();
    }

    public static int getMaxFreqRepeat(Map<Integer, Integer> map) {
        if (!map.isEmpty()) {
            return sizeToFreq.entrySet().stream().max(Map.Entry.comparingByValue()).get().getValue();
        }
        return 0;
    }

    public static void outMaxFreqRepeat(Map<Integer, Integer> map, int max) {
        if (!map.isEmpty()) {
            List<Integer> keys = sizeToFreq
                    .entrySet()
                    .stream()
                    .filter(x -> x.getValue() == max)
                    .sorted(Map.Entry.comparingByKey())
                    .map(Map.Entry::getKey)
                    .toList();
            System.out.print("Самое частое кол-во повторений команды \"R\"");
            for (int i = 0; i < keys.size(); i++) {
                System.out.printf("%s%d", (i == 0 ? " " : ", "), keys.get(i));
            }
            System.out.printf(" (встретилось %d раз).\n", max);
        }
    }

    public static void outOtherFreqRepeat(Map<Integer, Integer> map, int max) {
        if (!map.isEmpty()) {
            sizeToFreq
                    .entrySet()
                    .stream()
                    .filter(x -> x.getValue() < max)
                    .sorted(Map.Entry.comparingByKey())
                    .forEach(x -> System.out.printf("Количество повторений команды \"R\" %d (встретилось %d раз).\n", x.getKey(), x.getValue()));
        }
    }

    public static void main(String[] args) throws Exception {
        long startTs = System.currentTimeMillis(); // start time
        List<Thread> threadsMain = getThreadsMain();
        for (Thread threadMain : threadsMain) {
            threadMain.join();
        }
        int max = getMaxFreqRepeat(sizeToFreq);
        outMaxFreqRepeat(sizeToFreq, max);
        outOtherFreqRepeat(sizeToFreq, max);
        long endTs = System.currentTimeMillis(); // end time
        System.out.printf("Time: %d ms.\n", (endTs - startTs));
    }

    private static List<Thread> getThreadsMain() {
        List<Thread> threadsMain = new ArrayList<>();
        for (int i = 0; i < ROUTE_COUNT; i++) {
            Runnable taskMain = () -> {
                int countR = (int) generateRoute(ROUTE_TEMPLATE, ROUTE_LENGTH).chars().filter(x -> x == 'R').count();
                synchronized (sizeToFreq) {
                    sizeToFreq.put(countR, (sizeToFreq.containsKey(countR) ? sizeToFreq.get(countR) + 1 : 1));
                }
            };
            Thread threadMain = new Thread(taskMain);
            threadMain.start();
            threadsMain.add(threadMain);
        }
        return threadsMain;
    }
}