import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Questao11 {
    enum Status {
        NONEXISTENT, ACQUIRING, READY, RELEASING, FREE
    }

    static class Middleware {
        private final int CORE_POLL_SIZE = 10;
        private final int MAX_POOL_SIZE = 10;
        private final long KEEP_ALIVE = 60 * 10; // 10 Minutes
        private final TimeUnit UNIT = TimeUnit.SECONDS;

        private ThreadPoolExecutor executor;
        private ConcurrentHashMap<Long, Status> vms;
        private Long nextVM = 1L;
        private final Object lockAcquire = new Object();

        private static Middleware instance = new Middleware();

        private Middleware() {
            final BlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<>();

            this.executor = new ThreadPoolExecutor(CORE_POLL_SIZE, MAX_POOL_SIZE, KEEP_ALIVE, UNIT, workQueue);
            this.vms = new ConcurrentHashMap<>();
        }

        public static Middleware getInstance() {
            return instance;
        }

        private void dispatchStateChange(Long vm, Status preState, Status postState) {
            vms.put(vm, preState);

            Runnable workUnit = () -> {
                // Simulating the operation to the REST API with a sleep
                final long sleepTime = Math.round(Math.random() * 10 * 1000);
                try {
                    Thread.sleep(sleepTime);
                } catch (InterruptedException e) {}

                // Updating the status ending the operation
                vms.put(vm, postState);
            };

            executor.execute(workUnit);
        }

        public void shutDown() {
            executor.shutdown();
        }

        public Long requireVm() {
            Long thisVm;
            synchronized (lockAcquire) {
                thisVm = nextVM;
                nextVM += 1L;

                dispatchStateChange(thisVm, Status.ACQUIRING, Status.READY);
            }
            return thisVm;
        }

        public void releaseVm(Long vm){
            synchronized (lockAcquire) {
                if (Status.READY.equals(vms.get(vm))) {
                    dispatchStateChange(vm, Status.RELEASING, Status.FREE);
                }
            }
        }

        public Status getStatusVm(Long vm) {
            vms.putIfAbsent(vm, Status.NONEXISTENT);
            return vms.get(vm);
        }
    }

    // Testing out the Middleware
    private static final Middleware middleware = Middleware.getInstance();

    public static void main(String[] args) {
        long vm1 = middleware.requireVm();
        long vm2 = middleware.requireVm();
        long vm3 = middleware.requireVm();
        long vm4 = middleware.requireVm();

        while (middleware.getStatusVm(vm1) != Status.FREE ||
                middleware.getStatusVm(vm2) != Status.FREE ||
                middleware.getStatusVm(vm3) != Status.FREE ||
                middleware.getStatusVm(vm4) != Status.FREE) {

            if (middleware.getStatusVm(vm1) == Status.READY) {
                System.out.println(">>> (" + vm1 + ") acquired. Trying to release now...");
                middleware.releaseVm(vm1);
            }
            if (middleware.getStatusVm(vm2) == Status.READY) {
                System.out.println(">>> (" + vm2 + ") acquired. Trying to release now...");
                middleware.releaseVm(vm2);
            }
            if (middleware.getStatusVm(vm3) == Status.READY) {
                System.out.println(">>> (" + vm3 + ") acquired. Trying to release now...");
                middleware.releaseVm(vm3);
            }
            if (middleware.getStatusVm(vm4) == Status.READY) {
                System.out.println(">>> (" + vm4 + ") acquired. Trying to release now...");
                middleware.releaseVm(vm4);
            }
        }

        System.out.println(">>> All VM's released.");
        middleware.shutDown();
    }
}
