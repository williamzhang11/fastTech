# 线程池介绍

## 线程池的状态
变量ctl定义为AtomicInteger，共32位，其中高3位表示“线程池状态”，低29位表示：线程池
```
public class ThreadPoolExecutor extends AbstractExecutorService {

	...
    private final AtomicInteger ctl = new AtomicInteger(ctlOf(RUNNING, 0));
    ...
    
}
```

	RUNNING:
	