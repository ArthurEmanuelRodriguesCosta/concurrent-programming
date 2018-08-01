import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Questao11 {
    enum Status {
        NONEXISTENT, ACQUIRING, READY, RELEASING, FREE
    }

    static class Orchestrator {
        private final int CORE_POLL_SIZE = 10;
        private final int MAX_POOL_SIZE = 10;
        private final long KEEP_ALIVE = 60 * 10; // 10 Minutes
        private final TimeUnit UNIT = TimeUnit.SECONDS;

        private ThreadPoolExecutor executor;
        private ConcurrentHashMap<Long, Status> vms;
        private Long nextVM = 1L;
        private final Object lockAcquire = new Object();

        private static Orchestrator instance = new Orchestrator();

        private Orchestrator() {
            final BlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<>();

            this.executor = new ThreadPoolExecutor(CORE_POLL_SIZE, MAX_POOL_SIZE, KEEP_ALIVE, UNIT, workQueue);
            this.vms = new ConcurrentHashMap<>();
        }

        public static Orchestrator getInstance() {
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

    // Testing out the Orchestrator

    private static final Orchestrator o = Orchestrator.getInstance();

    public static void main(String[] args) {
        long vm1 = o.requireVm();
        long vm2 = o.requireVm();
        long vm3 = o.requireVm();
        long vm4 = o.requireVm();

        while (o.getStatusVm(vm1) != Status.FREE ||
                o.getStatusVm(vm2) != Status.FREE ||
                o.getStatusVm(vm3) != Status.FREE ||
                o.getStatusVm(vm4) != Status.FREE) {

            if (o.getStatusVm(vm1) == Status.READY) {
                System.out.println(">>> (" + vm1 + ") acquired. Trying to release now...");
                o.releaseVm(vm1);
            }
            if (o.getStatusVm(vm2) == Status.READY) {
                System.out.println(">>> (" + vm2 + ") acquired. Trying to release now...");
                o.releaseVm(vm2);
            }
            if (o.getStatusVm(vm3) == Status.READY) {
                System.out.println(">>> (" + vm3 + ") acquired. Trying to release now...");
                o.releaseVm(vm3);
            }
            if (o.getStatusVm(vm4) == Status.READY) {
                System.out.println(">>> (" + vm4 + ") acquired. Trying to release now...");
                o.releaseVm(vm4);
            }
        }

        System.out.println(">>> All VM's released.");
        o.shutDown();
    }
}
