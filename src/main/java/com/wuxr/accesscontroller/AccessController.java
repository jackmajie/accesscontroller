package com.wuxr.accesscontroller;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class AccessController {

    private static final Logger logger = LogManager.getLogger(AccessController.class);


    private CacheAdapter cacheAdapter;

    private long bucketLimit;

    private String controllerType;

    public void setCacheAdapter(CacheAdapter cacheAdapter) {
        this.cacheAdapter = cacheAdapter;
    }

    public void setBucketLimit(long bucketLimit) {
        this.bucketLimit = bucketLimit;
    }

    public void setControllerType(String controllerType) {
        this.controllerType = controllerType;
    }

    public long getPermitPerInterval() {
        return permitPerInterval;
    }

    public void setPermitPerInterval(long permitPerSec) {
        this.permitPerInterval = permitPerSec;
    }

    private long permitPerInterval;

    public void setInteralInMills(long interalInMills) {
        this.interalInMills = interalInMills;
    }

    private long interalInMills;


    public boolean accessControl(String controlKey) {
        String controlTypeKey=this.controllerType+controlKey;
        TokenBucket tb = cacheAdapter.getBucket(controlTypeKey);

        if (tb == null) {
            synchronized (this) {
                logger.trace("new to cache");
                tb=cacheAdapter.getBucket(controlTypeKey);
                if (tb == null) {
                    tb = new TokenBucket(
                            this.bucketLimit,  this.permitPerInterval,this.interalInMills);
                    tb.getToken();
                    cacheAdapter.putBucket(this.controllerType+controlKey,tb);
                    return true;
                } else {
                    boolean result=tb.getToken();
                    cacheAdapter.putBucket(this.controllerType+controlKey,tb);
                    return result;
                }
            }

        } else {
            boolean result=tb.getToken();
            cacheAdapter.putBucket(this.controllerType+controlKey,tb);
            return result;
        }

    }
}
