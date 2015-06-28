package cc.vileda.rdrctr;

import javax.annotation.Resource;
import javax.ejb.SessionContext;
import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.ArrayList;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LogInterceptor {
    @Resource
    private SessionContext sessionCtx;

    @Inject
    private Logger log;

    @AroundInvoke
    protected Object protocolInvocation(final InvocationContext ic)
            throws Exception {
        StringBuilder sb = new StringBuilder("[");
        ArrayList<Object> objects = new ArrayList<>();
        Collections.addAll(objects, ic.getParameters());
        if(objects.size() > 0) {
            if(objects.size() > 1) {
                for (Object obj : objects.subList(0, objects.size()-2)) {
                    sb.append(obj.toString());
                    sb.append(", ");
                }
            }
            sb.append(objects.get(objects.size() - 1).toString());
        }
        sb.append("]");

        log.log(Level.INFO,
                ">>> user {0} invoced {1} with method {2} and parameters: {3}",
                new Object[]{sessionCtx.getCallerPrincipal().getName(),
                        ic.getTarget().toString(), ic.getMethod().getName(),
                        sb.toString()});

        long cpuTimeStart = getCpuTime();
        long systemTimeStart = getSystemTime();
        long userTimeStart = getUserTime();

        Object result = ic.proceed();

        long cpuTimeStop = getCpuTime();
        long systemTimeStop = getSystemTime();
        long userTimeStop = getUserTime();

        log.log(Level.INFO,
                "<<< user {0} left class {1} and method {2} with parameters: {3}. c={4}, s={5}, u={6}",
                new Object[] { sessionCtx.getCallerPrincipal().getName(),
                        ic.getTarget().toString(), ic.getMethod().getName(),
                        sb.toString(),
                        (cpuTimeStop - cpuTimeStart)/1000,
                        (systemTimeStop - systemTimeStart)/1000,
                        (userTimeStop - userTimeStart)/1000
                });
        return result;
    }

    public long getCpuTime() {
        ThreadMXBean bean = ManagementFactory.getThreadMXBean();
        return bean.isCurrentThreadCpuTimeSupported() ?
                bean.getCurrentThreadCpuTime() : 0L;
    }

    /** Get user time in nanoseconds. */
    public long getUserTime() {
        ThreadMXBean bean = ManagementFactory.getThreadMXBean();
        return bean.isCurrentThreadCpuTimeSupported() ?
                bean.getCurrentThreadUserTime() : 0L;
    }

    /** Get system time in nanoseconds. */
    public long getSystemTime() {
        ThreadMXBean bean = ManagementFactory.getThreadMXBean();
        return bean.isCurrentThreadCpuTimeSupported() ?
                (bean.getCurrentThreadCpuTime() - bean.getCurrentThreadUserTime()) : 0L;
    }
}
