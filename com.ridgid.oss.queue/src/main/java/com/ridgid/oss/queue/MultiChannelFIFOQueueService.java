package com.ridgid.oss.queue;

import com.ridgid.oss.queue.spi.MultiChannelFIFOQueue;
import com.ridgid.oss.queue.spi.MultiChannelFIFOQueue.MultiChannelFIFOQueueException;
import com.ridgid.oss.spi.SPIServiceBase;

import java.io.Serializable;

@SuppressWarnings("JavaDoc")
public class MultiChannelFIFOQueueService<BaseMessageType extends Serializable>
    extends SPIServiceBase<MultiChannelFIFOQueue<BaseMessageType>, MultiChannelFIFOQueueException>
{
    protected MultiChannelFIFOQueueService(Class<MultiChannelFIFOQueue<BaseMessageType>> serviceClass,
                                           Class<MultiChannelFIFOQueueException> serviceException)
    {
        super(serviceClass, serviceException);
    }

    protected MultiChannelFIFOQueueService(Class<MultiChannelFIFOQueue<BaseMessageType>> serviceClass,
                                           Class<MultiChannelFIFOQueueException> serviceException,
                                           ClassLoader classLoader)
    {
        super(serviceClass, serviceException, classLoader);
    }

    protected MultiChannelFIFOQueueService(Class<MultiChannelFIFOQueue<BaseMessageType>> serviceClass,
                                           Class<MultiChannelFIFOQueueException> serviceException,
                                           boolean onlyInstalled)
    {
        super(serviceClass, serviceException, onlyInstalled);
    }
}
