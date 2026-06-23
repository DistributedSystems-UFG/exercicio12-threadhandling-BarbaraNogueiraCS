public class SimpleThreads {

    private static final long DEFAULT_PATIENCE_MILLIS = 10_000L;
    private static final long ONE_SECOND_MILLIS = 1_000L;
    private static final long CPU_PROGRESS_INTERVAL = 200_000L;

    /**
     * Display a message preceded by the name of the current thread.
     */
    static void threadMessage(String message) {
        String threadName = Thread.currentThread().getName();
        System.out.format("%s: %s%n", threadName, message);
    }

    /**
     * Original task from the template: prints messages with pauses between them.
     */
    private static class MessageLoop implements Runnable {
        @Override
        public void run() {
            String[] importantInfo = {
                "Mares eat oats",
                "Does eat oats",
                "Little lambs eat ivy",
                "A kid will eat ivy too"
            };

            try {
                for (String message : importantInfo) {
                    Thread.sleep(4_000L);
                    threadMessage(message);
                }
            } catch (InterruptedException e) {
                threadMessage("MessageLoop was interrupted before finishing.");
                Thread.currentThread().interrupt();
            }
        }
    }

    /**
     * New CPU-intensive task required by the exercise.
     *
     * This task searches for prime numbers starting from a large value. It uses
     * CPU because it performs many arithmetic operations. Since it does not call
     * Thread.sleep(), it must explicitly check whether it was interrupted.
     */
    private static class CpuIntensiveTask implements Runnable {
        private final long firstCandidate;

        CpuIntensiveTask(long firstCandidate) {
            this.firstCandidate = firstCandidate;
        }

        @Override
        public void run() {
            long candidate = firstCandidate;
            long primesFound = 0L;

            try {
                while (true) {
                    throwIfInterrupted();

                    if (isPrime(candidate)) {
                        primesFound++;
                    }

                    if (candidate % CPU_PROGRESS_INTERVAL == 0) {
                        threadMessage("CPU task still running. Primes found so far: " + primesFound
                                + ". Last tested number: " + candidate);
                    }

                    candidate++;
                }
            } catch (InterruptedException e) {
                threadMessage("CPU-intensive task interrupted safely. Primes found: " + primesFound
                        + ". Last tested number: " + candidate);
                Thread.currentThread().interrupt();
            }
        }

        private static boolean isPrime(long number) throws InterruptedException {
            if (number < 2) {
                return false;
            }
            if (number == 2) {
                return true;
            }
            if (number % 2 == 0) {
                return false;
            }

            for (long divisor = 3; divisor <= number / divisor; divisor += 2) {
                if (number % divisor == 0) {
                    return false;
                }

                // CPU-intensive tasks do not necessarily block, sleep or join.
                // Therefore, they must voluntarily check the interruption flag.
                if (divisor % 10_001 == 0) {
                    throwIfInterrupted();
                }
            }

            return true;
        }

        private static void throwIfInterrupted() throws InterruptedException {
            if (Thread.currentThread().isInterrupted()) {
                throw new InterruptedException();
            }
        }
    }

    private static long readPatienceFromArgs(String[] args) {
        if (args.length == 0) {
            return DEFAULT_PATIENCE_MILLIS;
        }

        try {
            long patienceInSeconds = Long.parseLong(args[0]);
            if (patienceInSeconds <= 0) {
                throw new NumberFormatException("The value must be positive.");
            }
            return patienceInSeconds * 1_000L;
        } catch (NumberFormatException e) {
            System.err.println("Argument must be a positive integer representing seconds.");
            System.exit(1);
            return DEFAULT_PATIENCE_MILLIS; // Unreachable, but required by the compiler.
        }
    }

    private static void waitForThreads(long patienceMillis, Thread... threads) throws InterruptedException {
        long startTime = System.currentTimeMillis();
        boolean interruptionRequested = false;

        while (isAnyThreadAlive(threads)) {
            threadMessage("Still waiting...");

            for (Thread thread : threads) {
                thread.join(ONE_SECOND_MILLIS);
            }

            long elapsedTime = System.currentTimeMillis() - startTime;
            if (elapsedTime > patienceMillis && !interruptionRequested) {
                threadMessage("Time limit exceeded. Requesting interruption of unfinished threads.");
                interruptAliveThreads(threads);
                interruptionRequested = true;
            }
        }
    }

    private static boolean isAnyThreadAlive(Thread... threads) {
        for (Thread thread : threads) {
            if (thread.isAlive()) {
                return true;
            }
        }
        return false;
    }

    private static void interruptAliveThreads(Thread... threads) {
        for (Thread thread : threads) {
            if (thread.isAlive()) {
                threadMessage("Interrupting " + thread.getName());
                thread.interrupt();
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        long patienceMillis = readPatienceFromArgs(args);

        Thread messageLoopThread = new Thread(new MessageLoop(), "MessageLoopThread");
        Thread cpuThread = new Thread(new CpuIntensiveTask(10_000_000_000L), "CpuIntensiveThread");

        threadMessage("Starting MessageLoop thread.");
        messageLoopThread.start();

        threadMessage("Starting CPU-intensive thread.");
        cpuThread.start();

        threadMessage("Waiting for threads to finish. Time limit: " + patienceMillis / 1_000L + " second(s).");
        waitForThreads(patienceMillis, messageLoopThread, cpuThread);

        threadMessage("Finally!");
    }
}
