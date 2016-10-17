/*
 * Copyright (c) 2008, 2009, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

/* @test
 *
 * @bug 6638195 6844297
 * @author Igor Kushnirskiy
 * @summary tests if EventQueueDelegate.Delegate is invoked.
 */

import sun.awt.EventQueueDelegate;

import java.util.*;
import java.util.concurrent.*;
import java.awt.*;
import sun.awt.EventQueueDelegate.Delegate;

public final class bug6638195 {
    public static void main(String[] args) throws Exception {
        MyEventQueueDelegate delegate = new MyEventQueueDelegate();
        EventQueueDelegate.setDelegate(delegate);
        runTest(delegate);

        delegate = new MyEventQueueDelegate();
        SwingUtilities3.setEventQueueDelegate(getObjectMap(delegate));
        runTest(delegate);
    }

    private static void runTest(MyEventQueueDelegate delegate) throws Exception {
        // We need an empty runnable here, so the next event is
        // processed with a new EventQueueDelegate. See 6844297
        // for details
        EventQueue.invokeLater(
            new Runnable() {
                @Override
                public void run() {
                }
            });
        // The following event is expected to be processed by
        // the EventQueueDelegate instance
        EventQueue.invokeLater(
            new Runnable() {
                @Override
                public void run() {
                }
            });
        // Finally, proceed on the main thread
        CountDownLatch latch = new CountDownLatch(1);
        EventQueue.invokeLater(
            new Runnable() {
                @Override
                public void run() {
                    latch.countDown();
                }
            });
        latch.await();
        if (!delegate.allInvoked()) {
            throw new RuntimeException("failed");
        }
    }

    static Map<String, Map<String, Object>> getObjectMap(
          Delegate delegate) {
        Map<String, Map<String, Object>> objectMap = new HashMap<>();
        Map<String, Object> methodMap;

        AWTEvent[] afterDispatchEventArgument = new AWTEvent[1];
        Object[] afterDispatchHandleArgument = new Object[1];
        Callable<Void> afterDispatchCallable =
            new Callable<Void>() {
                @Override
                public Void call() {
                    try {
                        delegate.afterDispatch(afterDispatchEventArgument[0],
                                afterDispatchHandleArgument[0]);
                    }
                    catch (InterruptedException e) {
                        throw new RuntimeException("afterDispatch interrupted", e);
                    }
                    return null;
                }
            };
        methodMap = new HashMap<>();
        methodMap.put("event", afterDispatchEventArgument);
        methodMap.put("handle", afterDispatchHandleArgument);
        methodMap.put("method", afterDispatchCallable);
        objectMap.put("afterDispatch", methodMap);

        AWTEvent[] beforeDispatchEventArgument = new AWTEvent[1];
        Callable<Object> beforeDispatchCallable =
            new Callable<Object>() {
                @Override
                public Object call() {
                    try {
                        return delegate.beforeDispatch(
                                beforeDispatchEventArgument[0]);
                    }
                    catch (InterruptedException e) {
                        throw new RuntimeException("beforeDispatch interrupted", e);
                    }
                }
            };
        methodMap = new HashMap<>();
        methodMap.put("event", beforeDispatchEventArgument);
        methodMap.put("method", beforeDispatchCallable);
        objectMap.put("beforeDispatch", methodMap);

        EventQueue[] getNextEventEventQueueArgument = new EventQueue[1];
        Callable<AWTEvent> getNextEventCallable =
            new Callable<AWTEvent>() {
                @Override
                public AWTEvent call() throws Exception {
                    return delegate.getNextEvent(
                        getNextEventEventQueueArgument[0]);
                }
            };
        methodMap = new HashMap<>();
        methodMap.put("eventQueue", getNextEventEventQueueArgument);
        methodMap.put("method", getNextEventCallable);
        objectMap.put("getNextEvent", methodMap);

        return objectMap;
    }

    static class MyEventQueueDelegate implements Delegate {
        private volatile boolean getNextEventInvoked;
        private volatile boolean beforeDispatchInvoked;
        private volatile boolean afterDispatchInvoked;
        @Override
        public AWTEvent getNextEvent(EventQueue eventQueue)
              throws InterruptedException {
            getNextEventInvoked = true;
            return eventQueue.getNextEvent();
        }
        @Override
        public Object beforeDispatch(AWTEvent event) {
            beforeDispatchInvoked = true;
            return null;
        }
        @Override
        public void afterDispatch(AWTEvent event, Object handle) {
            afterDispatchInvoked = true;
        }
        boolean allInvoked() {
            return getNextEventInvoked && beforeDispatchInvoked && afterDispatchInvoked;
        }
    }
}
